package edu.uci.ics.cs221wiki;
/*
 * HTMLTextParser.java
 * Author: S.Prasanna
 *
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;

import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.InputSource;

import org.cyberneko.html.parsers.DOMFragmentParser;

import org.apache.xerces.dom.CoreDocumentImpl;
 
public class HTMLTextParser {
	
	FileInputStream fin = null;
	StringBuffer TextBuffer = null;
	InputSource inSource = null;
	
	// HTMLTextParser Constructor 
    public HTMLTextParser() {
    	File f = new File("./output/");
    	f.mkdir();
    }
    
    //Gets the text content from Nodes recursively
    void processNode(Node node) {
    	if (node == null) return;
    	
    	//Process a text node
    	if (node.getNodeType() == node.TEXT_NODE) {
    		TextBuffer.append(node.getNodeValue());
    	} else if (node.hasChildNodes()) {
			//Process the Node's children 
					
			NodeList childList = node.getChildNodes();
			int childLen = childList.getLength();
			
			for (int count = 0; count < childLen; count ++) 
				processNode(childList.item(count));					
		}
		else return;
    }
    
    // Extracts text from HTML Document
    String htmltoText(String fileName) {
    	
    	DOMFragmentParser parser = new DOMFragmentParser();
    	
    	//System.out.println("Parsing text from HTML file " + fileName + "....");
        File f = new File(fileName);
        
        if (!f.isFile()) {
            System.out.println("File " + fileName + " does not exist.");
            return null;
        }
        
        try {
            fin = new FileInputStream(f);
        } catch (Exception e) {
            System.out.println("Unable to open HTML file " + fileName + " for reading.");
            return null;
        } 
        	
        try {
            inSource = new InputSource(fin);
        } catch (Exception e) {
            System.out.println("Unable to open Input source from HTML file " + fileName);
            return null;
        } 
        
        CoreDocumentImpl codeDoc = new CoreDocumentImpl();
        DocumentFragment doc = codeDoc.createDocumentFragment();
        
        try {
        	parser.parse(inSource, doc);
        } catch (Exception e) {
        	System.out.println("Unable to parse HTML file " + fileName);
        	return null;       	
        }
        
        TextBuffer = new StringBuffer();
        
        //Node is a super interface of DocumentFragment, so no typecast needed
        processNode(doc);
        
        //System.out.println("Done.");
    		
    	return TextBuffer.toString();
    }     
    
    // Writes the parsed text from HTML to a file
    void writeTexttoFile(String htmlText, String fileName) {
    	
    	//System.out.println("\nWriting HTML text to output text file " + fileName + "....");
    	try {
    		PrintWriter pw = new PrintWriter(fileName);
    		pw.print(htmlText);
    		pw.close();    	
    	} catch (Exception e) {
    		System.out.println("An exception occurred in writing the html text to file.");
    		e.printStackTrace();
    	}
    	//System.out.println("Done.");
    }
    
    // Extracts text from an HTML Document and writes it to a text file
    String HTMLtoTextParser(String file) {
//    	
//    	if (args.length != 2) {
//        	System.out.println("Usage: java HTMLTextParser <InputHTMLFile> <OutputTextFile>");
//        	System.exit(1);
//        }
        
//        HTMLTextParser htmlTextParserObj = new HTMLTextParser();
//        String htmlToText = htmlTextParserObj.htmltoText(file);
    	String htmlToTextfile = this.htmltoText(file);
        
        if (htmlToTextfile == null) {
        	System.out.println("HTML to Text Conversion failed.");
        	return null;
        }
        else {
        	//System.out.println("\nThe text parsed from the HTML Document....\n" + htmlToTextfile);
        	String filename = file.substring(file.lastIndexOf("/")+1, file.indexOf(".")-1);
        	String parsedData = "./output/"+filename+".txt";
        	this.writeTexttoFile(htmlToTextfile, parsedData);
        	return parsedData;
        }
    }  
}
