import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DOMReader {
    public static void main(String[] args) {
        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File("filtered_by_ethnicity.xml"));
            Element root = document.getDocumentElement();
            NodeList names = root.getChildNodes();
            List<RatingName> namesObjects = new ArrayList<>();

            for (int i = 0; i < names.getLength(); i++)
            {
                Node ethnicNode = names.item(i);

                if (ethnicNode.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element ethnic = (Element) ethnicNode;
                    String ethnicName = ethnic.getNodeName();
                    NodeList rows = ethnic.getChildNodes();

                    for (int j = 0; j < rows.getLength(); j++)
                    {
                        Node rowNode = rows.item(j);
                        if (rowNode.getNodeType() == Node.ELEMENT_NODE)
                        {
                            Element ratName = (Element) rowNode;
                            String ratNameElemName = ratName.getNodeName();
                            RatingName ratingName = new RatingName();
                            NodeList ratNameFields = ratName.getChildNodes();

                            for (int k = 0; k < ratNameFields.getLength(); k++)
                            {
                                Node fieldNode = ratNameFields.item(k);
                                if (fieldNode.getNodeType() == Node.ELEMENT_NODE)
                                {
                                    Element field = (Element) fieldNode;
                                    String fieldElemName = field.getNodeName();
                                    String textContent = field.getTextContent();
                                    
                                    if (fieldElemName.equals("gender"))
                                    {
                                        ratingName.set_gender(textContent);
                                    }
                                    else if (fieldElemName.equals("ethnicity"))
                                    {
                                        ratingName.set_ethnicity(textContent);
                                    }
                                    else if (fieldElemName.equals("name"))
                                    {
                                        ratingName.set_name(textContent);
                                    }
                                    else if (fieldElemName.equals("count"))
                                    {
                                        ratingName.set_count(Integer.parseInt(textContent));
                                    }
                                    else if (fieldElemName.equals("rank"))
                                    {
                                        ratingName.set_rank(Integer.parseInt(textContent));
                                    }
                                }
                            }
                            namesObjects.add(ratingName);
                        }
                    }
                }
}

            table_print(namesObjects);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public static void table_print(List<RatingName> table)
    {
        for (RatingName f: table)
        {
            System.out.printf("%-6s\t%-26s\t%-12s\t%-3d\t%-3d\n", f.get_gender(), f.get_ethnicity(), f.get_name(), f.get_count(), f.get_rank());
        }
    }
}

public class RatingName
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