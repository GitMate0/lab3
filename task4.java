import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class XMLFilter
{
    public enum Field
    {
       RANK,
       COUNT
    }

    public enum Sequence
    {
        ASC,
        DESC
    }

    public enum Ethnicity 
    {
        WHITE("white"),
        BLACK("black"),
        HISPANIC("hisp"),
        OCEASIAN("oceas");

        private String name;

        Ethnicity(String name)
        {
            this.name = name;
        }

        public String get_name()
        {
            return name;
        }
    }

    public static void main(String[] args) throws ParserConfigurationException
    {
        try
        {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            DefaultHandler handler = new DefaultHandler()
            {
                String currentTag;
                RatingName item = new RatingName();
                int counter = 0;
                List<RatingName> popularNames = new ArrayList<>();

                public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
                {
                    currentTag = qName;
                }

                public void characters(char[] ch, int start, int length) throws SAXException
                {
                    if (counter % 6 == 0 && counter != 0)
                    {
                        popularNames.add(item);
                        item = new RatingName();
                    }
                    String content = new String(ch, start, length).trim();
                    if (!content.isEmpty())
                    {
                        if (currentTag.equals("gndr"))
                        {
                            item.set_gender(content);
                        }
                        else if (currentTag.equals("ethcty"))
                        {
                            item.set_ethnicity(content);
                        }
                        else if (currentTag.equals("nm"))
                        {
                            item.set_name(content);
                        }
                        else if (currentTag.equals("cnt"))
                        {
                            item.set_count(Integer.parseInt(content));
                        }
                        else if (currentTag.equals("rnk"))
                        {
                            item.set_rank(Integer.parseInt(content));
                        }
                        counter++;
                    }
                }

                public void endDocument() throws SAXException
                {
                    int counter = 0;
                    List<List<RatingName>> popularNamesByEthnicity = new ArrayList<>();
                    for (Ethnicity e: Ethnicity.values())
                    {
                        popularNamesByEthnicity.add(filter_by_ethnicity(popularNames, e));
                        quickSort(popularNamesByEthnicity.get(counter), Field.COUNT, Sequence.DESC);
                        popularNamesByEthnicity.set(counter, shorten(deduplicate(popularNamesByEthnicity.get(counter)), 30));
                        quickSort(popularNamesByEthnicity.get(counter), Field.RANK, Sequence.ASC);

                        counter++;
                    }
                    for (int i = 0; i < counter; i++)
                    {
                        System.out.println("\nEthnicity: " + Ethnicity.values()[i]);
                        table_print(popularNamesByEthnicity.get(i));
                    }
                    try {
                        createXML(popularNamesByEthnicity, "filtered_by_ethnicity.xml");
                    } catch (ParserConfigurationException e) {
                        e.printStackTrace();
                    }
                }
            };

            saxParser.parse(new File("Popular_Baby_Names_NY.xml"), handler);

        }
        catch (Exception e)
        {
            System.out.println("JIOX");
            e.printStackTrace();
        }
    }
    public static String capitalize(String input)
    {
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }
    public static String spaces(int count)
    {
        String space = "";
        for (int i = 0; i < count; i++)
            space += " ";
        return space;
    }

    public static void table_print(List<RatingName> table)
    {
        for (RatingName f: table)
        {
            System.out.printf("%-6s\t%-26s\t%-12s\t%-3d\t%-3d\n", f.get_gender(), f.get_ethnicity(), f.get_name(), f.get_count(), f.get_rank());
        }
    }

    public static List<RatingName> filter_by_ethnicity(List<RatingName> data, Ethnicity ethnic_group)
    {
        List<RatingName> filtered = new ArrayList<>();
        String ethnic;
        for (RatingName f: data)
        {
            ethnic = f.get_ethnicity();
            switch (ethnic_group)
            {
                case WHITE:
                    if (ethnic.equals("WHITE NON HISPANIC") || ethnic.equals("WHITE NON HISP"))
                    {
                        filtered.add(f);
                    }
                    break;

                case BLACK:
                    if (ethnic.equals("BLACK NON HISPANIC") || ethnic.equals("BLACK NON HISP"))
                    {
                        filtered.add(f);
                    }
                    break;

                case HISPANIC:
                    if (ethnic.equals("HISPANIC"))
                    {
                        filtered.add(f);
                    }
                    break;

                case OCEASIAN:
                    if (ethnic.equals("ASIAN AND PACIFIC ISLANDER") || ethnic.equals("ASIAN AND PACI"))
                    {
                        filtered.add(f);
                    }
                    break;
            }
        }
        return filtered;
    }

    public static void quickSort(List<RatingName> list, Field field, Sequence seq) {
        quickSort(list, 0, list.size() - 1, field, seq);
    }

    private static void quickSort(List<RatingName> list, int low, int high, Field field, Sequence seq)
    {
        if (low < high)
        {
            int pivotIndex = partition(list, low, high, field, seq);
            quickSort(list, low, pivotIndex - 1, field, seq);
            quickSort(list, pivotIndex + 1, high, field, seq);
        }
    }

    private static int partition(List<RatingName> list, int low, int high, Field field, Sequence seq) {
        RatingName pivot = list.get(high);
        int i = low - 1;

        for (int j = low; j < high; j++)
        {
            switch (field)
            {
                case RANK:
                    switch (seq)
                    {
                        case ASC:
                            if (list.get(j).get_rank() <= pivot.get_rank())
                            {
                                i++;
                                swap(list, i, j);
                            }
                            break;

                        case DESC:
                            if (list.get(j).get_rank() >= pivot.get_rank())
                            {
                                i++;
                                swap(list, i, j);
                            }
                            break;
                    }
                    break;

                case COUNT:
                    switch (seq)
                    {
                        case ASC:
                            if (list.get(j).get_count() <= pivot.get_count())
                            {
                                i++;
                                swap(list, i, j);
                            }
                            break;

                        case DESC:
                            if (list.get(j).get_count() >= pivot.get_count())
                            {
                                i++;
                                swap(list, i, j);
                            }
                            break;
                    }
                    break;
            }
        }

        swap(list, i + 1, high);
        return i + 1;
    }

    private static void swap(List<RatingName> list, int i, int j) {
        RatingName temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }

    public static List<RatingName> deduplicate(List<RatingName> list)
    {
        Set<String> seenNames = new HashSet<>();
        List<RatingName> filtered = new ArrayList<>();

        for (RatingName f : list)
        {
            String newName = f.get_name().toLowerCase();

            if (!seenNames.contains(newName))
            {
                seenNames.add(newName);
                filtered.add(f);
            }
        }
        return filtered;
    }

    public static List<RatingName> shorten(List<RatingName> list, int count)
    {
        List<RatingName> shortened = new ArrayList<>();
        for (int i = 0; i < count; i++)
            shortened.add(list.get(i));
        return shortened;
    }

    public static void createXML(List<List<RatingName>> list, String filePath) throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();

        Element root = document.createElement("names");
        document.appendChild(root);

        for (int i = 0; i < list.size(); i++)
        {
            Element ethnic = document.createElement(Ethnicity.values()[i].get_name());
            for (RatingName f: list.get(i))
            {
                Element row = document.createElement("row");

                Element gender = document.createElement("gender");
                gender.setTextContent(f.get_gender());
                row.appendChild(gender);

                Element ethnicity = document.createElement("ethnicity");
                ethnicity.setTextContent(f.get_ethnicity());
                row.appendChild(ethnicity);
                
                Element name = document.createElement("name");
                name.setTextContent(f.get_name());
                row.appendChild(name);
                
                Element count = document.createElement("count");
                count.setTextContent(Integer.toString(f.get_count()));
                row.appendChild(count);
                
                Element rank = document.createElement("rank");
                rank.setTextContent(Integer.toString(f.get_rank()));
                row.appendChild(rank);

                ethnic.appendChild(row);
            }
            root.appendChild(ethnic);
        }
        save_document(document, filePath);
    }

    private static void save_document(Document document, String filePath) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File(filePath));

            transformer.transform(source, result);
        }
        catch (TransformerException e) {
            e.printStackTrace();
        }
    }
}

public class RatingName implements Serializable
{
    private int birth_year;
    private String gender;
    private String ethnicity;
    private String name;
    private int count;
    private int rank;

    public RatingName(int birth_year, String gender, String ethnicity, String name, int count, int rank)
    {
        this.birth_year = birth_year;
        this.gender = gender;
        this.ethnicity = ethnicity;
        this.name = name;
        this.count = count;
        this.rank = rank;
    }

    public RatingName()
    {
        this.birth_year = 0;
        this.gender = "";
        this.ethnicity = "";
        this.name = "";
        this.count = 0;
        this.rank = 0;
    }

    public int get_birth_year()
    {
        return birth_year;
    }

    public String get_gender()
    {
        return gender;
    }

    public String get_ethnicity()
    {
        return ethnicity;
    }

    public String get_name()
    {
        return name;
    }

    public int get_count()
    {
        return count;
    }

    public int get_rank()
    {
        return rank;
    }

    public void set_birth_year(int birth_year)
    {
        this.birth_year = birth_year;
    }

    public void set_gender(String gender)
    {
        this.gender = gender;
    }

    public void set_ethnicity(String ethnicity)
    {
        this.ethnicity = ethnicity;
    }

    public void set_name(String name)
    {
        this.name = name;
    }

    public void set_count(int count)
    {
        this.count = count;
    }

    public void set_rank(int rank)
    {
        this.rank = rank;
    }
}

