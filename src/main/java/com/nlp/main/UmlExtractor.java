package com.nlp.main;

import java.io.File;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import com.nlp.umlextractor.UMLToXML.xmlwriter.XMLWriter;
import com.nlp.umlextractor.reader.jsonreader.JsonReader;
import com.util.PropertyFileReader;
import com.util.Reader;

public class UmlExtractor
{

    static String input_file = "inputfile";
    static String output_dir = "outputdir";

    public static void main(String[] args)
    {
	if (args != null && args.length > 2)
	{
	    throw new IllegalArgumentException(
		    "Wrong argument, there should be input file location and output directory");
	} else
	{
	    input_file = args[0];
	    output_dir = args[1];

	    if (input_file.contains(".xmi") || input_file.contains(".xml") || input_file.contains(".uml"))
	    {

	    } else if (input_file.contains(".mdj"))
	    {
		Reader reader = new JsonReader();
		reader.read(input_file);

		XMLWriter writer = new XMLWriter();
		try
		{
		    writer.createXML(output_dir + File.separator
			    + PropertyFileReader.getInstance().getProjectPropertie().getProperty("file.uml"));
		} catch (TransformerException | ParserConfigurationException e)
		{
		    e.printStackTrace();
		}
	    } else
	    {
		throw new RuntimeException("The file should be .mdj, .xml, or ,uml format");
	    }
	}
    }
}
