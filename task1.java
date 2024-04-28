import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class SAXReader
{

    public static void main(String[] args)
    {
        try
        {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            DefaultHandler handler = new DefaultHandler()
            {
                Set<String> tagSet = new HashSet<>();

                public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
                {
                    tagSet.add(qName);
                    System.out.print("<" + qName + ">");
                }

                public void characters(char[] ch, int start, int length) throws SAXException
                {
                    String content = new String(ch, start, length).trim();
                    if (!content.isEmpty())
                    {
                        System.out.print(content);
                    }
                }

                public void endElement(String uri, String localName, String qName) throws SAXException
                {
                    System.out.print("</" + qName + ">" + "\t\t");
                    if (qName.equals("row"))
                    {
                        System.out.println();
                    }
                }
                
                public void startDocument() throws SAXException
                {
                    System.out.println("Document processing started.");
                }

                public void endDocument() throws SAXException
                {
                    System.out.println("\nDocument processing finished.");
                    System.out.println("List of tags present in the document:");
                    for (String tag : tagSet)
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

