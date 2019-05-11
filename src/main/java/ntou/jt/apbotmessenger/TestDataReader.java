package readXml;
import java.io.File;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class TestDataReader {
	private String failCount;
	private String skipCount;
	private String totalCount;
	private String name;
	private String build;
	
	public TestDataReader()
	{
		Element element = getElement();
		
		this.failCount = element.getElementsByTagName("failCount").item(0).getTextContent();
		this.skipCount = element.getElementsByTagName("skipCount").item(0).getTextContent();
		this.totalCount = element.getElementsByTagName("totalCount").item(0).getTextContent();
		this.name = element.getElementsByTagName("name").item(0).getTextContent();
		this.build = element.getElementsByTagName("build").item(0).getTextContent();
		
	}
	
	private Element getElement()
	{
		try 
		{	
			File inputFile = new File("build.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();
			System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
			System.out.println("----------------------------");
			return (Element)doc.getElementsByTagName("hudson.maven.reporters.SurefireAggregatedReport").item(0);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public String getFailCount() 
	{
		return failCount;
	}

	public String getSkipCount() 
	{
		return skipCount;
	}

	public String getTotalCount() 
	{
		return totalCount;
	}

	public String getName() 
	{
		return name;
	}

	public String getBuild() 
	{
		return build;
	}
}
