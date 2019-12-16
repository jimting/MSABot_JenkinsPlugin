package ntou.jt.apbotmessenger;

import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

class TestCase
{
	public String name;
	public String trace;
	public String detail;
	public String out;
	public TestCase(String name, String trace, String detail, String out)
	{
		this.name = name;
		this.trace = trace;
		this.detail = detail;
		this.out = out;
	}
	@Override
	public String toString()
	{
		JSONObject obj = new JSONObject();
		obj.put("name", name);
		obj.put("trace", trace);
		obj.put("detail", detail);
		obj.put("out", out);
		return obj.toString();
	}
}
public class TestDataReader {
	private String failCount = "";
	private String skipCount = "";
	private String totalCount = "";
	private String name = "";
	private String build = "";
	private String testCaseFileName = "junitResult.xml"; //default = junit
	private ArrayList<TestCase> failCase = new ArrayList();
	public String status = "";
	public String testCaseUrl = "";
	public String cmd = "";
	public TestDataReader(String dir)
	{
		Element element = getElement(dir);
		
		if(element!=null)
		{
			this.failCount = element.getElementsByTagName("failCount").item(0).getTextContent();
			this.skipCount = element.getElementsByTagName("skipCount").item(0).getTextContent();
			this.totalCount = element.getElementsByTagName("totalCount").item(0).getTextContent();
			this.name = element.getElementsByTagName("name").item(0).getTextContent();
			this.build = element.getElementsByTagName("build").item(0).getTextContent();
			String projectName = "'"+this.name.replace(":", "$")+"'";
			testCaseUrl = dir+"/"+projectName+"/"+testCaseFileName;
			cpJunitFile(dir,projectName);
			//failCase = getFailCase(dir, element.getElementsByTagName("name").item(0).getTextContent());
		}
		
	}
	
	private Element getElement(String dir)
	{
		try 
		{	
			File inputFile = new File(dir+"/build.xml");
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
	private ArrayList<TestCase> getFailCase(String dir, String projectName)
	{
		ArrayList<TestCase> testCase = new ArrayList<TestCase>();
		try 
		{	
			//rename projectName here
			projectName = "'"+projectName.replace(":", "$")+"'";
			System.out.println(dir+"/"+projectName+"/"+testCaseFileName);
			testCaseUrl = dir+"/"+projectName+"/"+testCaseFileName;
			cpJunitFile(dir,projectName);

			File inputFile = new File(dir+"/"+projectName+"/"+testCaseFileName);
			

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();
			NodeList caseList = doc.getElementsByTagName("case");
			status = "FoundCaseCount : " + caseList.getLength();
			for (int temp = 0; temp < caseList.getLength(); temp++) 
			{
				Element checkCase = (Element) caseList.item(temp);
				if(Integer.parseInt(checkCase.getElementsByTagName("failedSince").item(0).getTextContent()) != 0)
				{
					String name = checkCase.getElementsByTagName("className").item(0).getTextContent();
					String trace = checkCase.getElementsByTagName("errorStackTrace").item(0).getTextContent();
					String detail = checkCase.getElementsByTagName("errorDetails").item(0).getTextContent();
					String out = checkCase.getElementsByTagName("stdout").item(0).getTextContent();
					testCase.add(new TestCase(name, trace, detail, out));
				}
			}
			return testCase;
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			return testCase;
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
	public ArrayList<TestCase> getFailCase()
	{
		return failCase;
	}
	private void cpJunitFile(String dir, String projectName)
	{

		String s;
		Process p;
		try {
			cmd = "cp " + dir + "/" + projectName + "/" + testCaseFileName +" " + dir + "/" + testCaseFileName;
			System.out.println(cmd);
		    	p = Runtime.getRuntime().exec(cmd);
		   	BufferedReader br = new BufferedReader(
		       		new InputStreamReader(p.getInputStream()));
		  	while ((s = br.readLine()) != null)
		        	System.out.println("line: " + s);
		    	p.waitFor();
		    	System.out.println ("exit: " + p.exitValue());
		    	p.destroy();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
}
