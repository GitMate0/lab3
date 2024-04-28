import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.xml.sax.SAXException;
import java.io.File;
import java.io.IOException;

public class XMLValidator {

    public static void main(String[] args)
    {
        try
        {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new File("baby_name.xsd"));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new File("Popular_Baby_Names_NY.xml")));
            System.out.println("Validation successful. The XML document is valid.");
        }
        catch (SAXException | IOException e) 
        {
            System.out.println("Validation failed: " + e.getMessage());
        }
    }
}
