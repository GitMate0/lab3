import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class XMLEthnicitySearcher
{

    public static void main(String[] args)
    {
        try
        {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            DefaultHandler handler = new DefaultHandler()
            {
                Set<String> ethnicitySet = new HashSet<>();
                String currentTag;

                public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
                {
                    currentTag = qName;
                }

                public void characters(char[] ch, int start, int length) throws SAXException
                {
                    String content = new String(ch, start, length).trim();
                    if (!content.isEmpty())
                    {
                        if (currentTag.equals("ethcty"))
                        {
                            ethnicitySet.add(content);
                        }
                    }
                }

                public void endDocument() throws SAXException
                {
                    System.out.println("List of ethnicities present in the document:");
                    for (String tag : ethnicitySet)
                    {
                        System.out.println(tag);
                    }
                }
            };

            saxParser.parse(new File("Popular_Baby_Names_NY.xml"), handler);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}

