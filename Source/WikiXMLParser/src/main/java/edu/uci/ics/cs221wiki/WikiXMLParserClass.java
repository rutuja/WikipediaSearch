package edu.uci.ics.cs221wiki;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.regex.*;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import edu.jhu.nlp.wikipedia.*;

public class WikiXMLParserClass {

	static class TestCallbackHandler implements PageCallbackHandler {

		int counter;
		private final String htmlDirectory;
		//private final String imageDirectory;

		public TestCallbackHandler(String htmlDirectory, String imageDirectory) {
			this.counter = 0;
			if (htmlDirectory.charAt(htmlDirectory.length() - 1) != '/') {
				htmlDirectory = htmlDirectory + "/";
			}
			this.htmlDirectory = htmlDirectory;
			try{
				File file = new File(this.htmlDirectory);
				if(!file.exists())
				{
					file.mkdir();
				}
				/*this.imageDirectory = imageDirectory;
			file = new File(this.imageDirectory);
			if(!file.exists())
			{
				file.mkdir();
			}*/
			}catch(Exception e){

			}
		}

		public String processWikiText(String wikiText){
			wikiText = wikiText.replaceAll("\\p{Cntrl}", "").trim();
			String pattern = "[{][{][^}]*[}][}][\\.\\*]?";
			Pattern p2 = Pattern.compile(pattern);
			Matcher m2 = p2.matcher(wikiText);
			wikiText = m2.replaceAll("");
			pattern = "([^=])(==[^=])*(==[\\.\\*]?)";
			p2 = Pattern.compile(pattern);
			m2 = p2.matcher(wikiText);
			//for(int i = 0; i<=m2.groupCount(); i++)
			//System.out.println("Groups :" + m2.groupCount());
			wikiText = m2.replaceAll("");
			return wikiText;
		}

		public void process(WikiPage page){
			try{
				File logger = new File("logger.txt");
				if(!logger.exists())
				{
					logger.createNewFile();
				}
				if(page.isStub()||page.isRedirect()||page.isSpecialPage()||page.isDisambiguationPage())
					return;
				String htmlFileName = this.htmlDirectory + page.getTitle().replaceAll("\\p{Cntrl}", "").trim().concat(".html");
				File file = new File(htmlFileName);
				if(!file.exists())
					file.createNewFile();
				OutputStream destination = new FileOutputStream(file);
				XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
				XMLStreamWriter xml = outputFactory.createXMLStreamWriter(destination);

				xml.writeStartDocument();
				xml.writeStartElement("html");
				xml.writeDefaultNamespace("http://www.w3.org/1999/xhtml");

				xml.writeStartElement("head");
				xml.writeStartElement("title");
				xml.writeCharacters(page.getTitle().replaceAll("\\p{Cntrl}", "").trim());
				xml.writeEndElement();
				xml.writeEndElement();

				xml.writeStartElement("body");
				String processedText = processWikiText(page.getText());
				xml.writeCharacters(processedText);
				xml.writeEndElement();

				xml.writeEndElement();
				xml.writeEndDocument();
			}catch(Exception e){
			}
		}
	}


	public static void main(String args[]){
		if (args.length != 2) {
			System.err.println("Usage: Parser <XML-FILE> <OUTPUT-DIRECTORY>");
			System.exit(-1);
		}
		String xmlFilename = args[0];
		String htmlDirectory = args[1];
		String imageDirectory = "/home/malinik/test/dump/WikiDumpImages";
		try {
			PageCallbackHandler handler = new TestCallbackHandler(htmlDirectory, imageDirectory);
			WikiXMLSAXParser wxp = new WikiXMLSAXParser(xmlFilename);
			wxp.setPageCallback(handler);
			wxp.parse();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
