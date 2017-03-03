
package com.nlp.umlextractor.UMLToXML.xmlwriter;

import static com.nlp.umlextractor.staticdata.StaticData.ANNOTATION_ROOT;
import static com.nlp.umlextractor.staticdata.StaticData.ARTEFACTELEMENT_ROOT;
import static com.nlp.umlextractor.staticdata.StaticData.ARTEFACTS_ROOT;
import static com.nlp.umlextractor.staticdata.StaticData.ARTEFACTUBELEMENT_ROOT;
import static com.nlp.umlextractor.staticdata.StaticData.ARTEFACT_ROOT;
import static com.nlp.umlextractor.staticdata.StaticData.CONNECTION_ROOT;
import static com.nlp.umlextractor.staticdata.StaticData.ENDPOINT_ROOT;
import static com.nlp.umlextractor.staticdata.StaticData.ID_ROOT;
import static com.nlp.umlextractor.staticdata.StaticData.INTRACONNECTION_ROOT;
import static com.nlp.umlextractor.staticdata.StaticData.MULTIPLICITY_ROOT;
import static com.nlp.umlextractor.staticdata.StaticData.NAME_ROOT;
import static com.nlp.umlextractor.staticdata.StaticData.PARAMETER_ROOT;
import static com.nlp.umlextractor.staticdata.StaticData.RETURN_TYPE_ROOT;
import static com.nlp.umlextractor.staticdata.StaticData.STARTPOINT_ROOT;
import static com.nlp.umlextractor.staticdata.StaticData.STATUS;
import static com.nlp.umlextractor.staticdata.StaticData.TYPE_CONNECTION_ROOT;
import static com.nlp.umlextractor.staticdata.StaticData.TYPE_ROOT;
import static com.nlp.umlextractor.staticdata.StaticData.VARIABLE_TYPE_ROOT;
import static com.nlp.umlextractor.staticdata.StaticData.VISIBILITY_ROOT;
import static com.nlp.umlextractor.staticdata.StaticData.depencyList;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.nlp.umlextractor.model.Attribute;
import com.nlp.umlextractor.model.Dependencies;
import com.nlp.umlextractor.model.Method;
import com.nlp.umlextractor.model.ModelData;
import com.nlp.umlextractor.model.Parameter;
import com.nlp.umlextractor.staticdata.StaticData;
import com.util.FileOperator;

public class XMLWriter
{

  public static HashMap<String, String> keyIDMap = new HashMap<>();

  public static String fileName = "";

  public String type = "UMLDiagram";

  private static int attrId = 1;

  private static int methodId = 1;

  private int count = 0;
  
  public void createXML(String fileDir) throws TransformerException, ParserConfigurationException
  {
    List<ModelData> classLst = StaticData.classLst;

    Document doc = createDocument();
    Element rootElement = doc.createElement(ARTEFACTS_ROOT);
    Element artifact = doc.createElement(ARTEFACT_ROOT);
    doc.appendChild(rootElement);
    rootElement.appendChild(artifact);


    setAttribute(doc, type, artifact, TYPE_ROOT);
    for (int i = 0; i < classLst.size(); i++)
    {
      attrId = 1;
      methodId = 1;
      String id = getDesignElementID();
      ModelData tempData = classLst.get(i);

      Element artifactElement = doc.createElement(ARTEFACTELEMENT_ROOT);
      artifact.appendChild(artifactElement);

      setAttribute(doc, tempData.getName(), artifactElement, NAME_ROOT);
      setAttribute(doc, tempData.getType(), artifactElement, TYPE_ROOT);
      
      setAttribute(doc,id , artifactElement, ID_ROOT);
      keyIDMap.put(tempData.getId(), id);

      createAttributeElement(doc, artifactElement, tempData);
      createMethodElement(doc, artifactElement, tempData);
    }

    Element intraConnectionElement = doc.createElement(INTRACONNECTION_ROOT);
    artifact.appendChild(intraConnectionElement);
   
    
    System.out.println("------Writing  XML to directory : " + fileDir + " ------");

    FileOperator.writeToXML(new File(fileDir).getPath(), doc);
  }

  private Document createDocument() throws ParserConfigurationException
  {
    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
    
    Document doc = docBuilder.newDocument();
    return doc;
  }

  private String getFileDir()
  {
    String dir = System.getProperty("user.home") + File.separator + "RequirementArtefactFile.xml";//default
    
    System.out.println("------Writing Requirement XML -- XML folder path is returned--:  " + dir);
    return dir;
  }

  private String getDesignElementID()
  {
    String ID;
    if (type.equals("Requirement"))
    {
      count++;
      ID = "RQ".concat(Integer.toString(count));

    }
    else if (type.equals("Sourcecode"))
    {
      count++;
      ID = "SC".concat(Integer.toString(count));
    }
    else
    {
      count++;
      ID = "D".concat(Integer.toString(count));

    }
    return ID;
  }





  private void createAttributeElement(Document doc, Element artifactElement, ModelData tempData)
  {
    for (Attribute attribute : tempData.getAttributeList())
    {
      Element artifactSubElement = doc.createElement(ARTEFACTUBELEMENT_ROOT);
      artifactElement.appendChild(artifactSubElement);//append an artifact main root

      setAttribute(doc, attribute.getName(), artifactSubElement, NAME_ROOT);
      setAttribute(doc, attribute.getType(), artifactSubElement, TYPE_ROOT);
      setAttribute(doc, attribute.getVisibility(), artifactSubElement, VISIBILITY_ROOT);
      setAttribute(doc, getAttributeID(), artifactSubElement, ID_ROOT);
      setAttribute(doc, attribute.getDataType(), artifactSubElement, VARIABLE_TYPE_ROOT);

    }
  }

  private void createMethodElement(Document doc, Element artifactElement, ModelData tempData)
  {

    for (Method method : tempData.getOperationList())
    {
      Element artifactSubElement = doc.createElement(ARTEFACTUBELEMENT_ROOT);
      String parameterString = "";
      artifactElement.appendChild(artifactSubElement);

      setAttribute(doc, method.getName(), artifactSubElement, NAME_ROOT);
      setAttribute(doc, method.getType(), artifactSubElement, TYPE_ROOT);
      setAttribute(doc, method.getVisibility(), artifactSubElement, VISIBILITY_ROOT);
      setAttribute(doc, getMethodID(), artifactSubElement, ID_ROOT);
      setAttribute(doc, method.getReturnType(), artifactSubElement, RETURN_TYPE_ROOT);
      setAttribute(doc, method.getStatus(), artifactSubElement, STATUS);

      List<Parameter> paramList = method.getParameterList();
      String tempString = method.getParamString();
      if (tempString.equals(""))
      {
        parameterString = getParamString(paramList, parameterString);
      }
      setAttribute(doc, parameterString, artifactSubElement, PARAMETER_ROOT);
    }

  }
  private String getAttributeID()
  {
    String ID;
    if (type.equals("Requirement"))
    {
      ID = "RQ".concat(Integer.toString(count)).concat("_F").concat(Integer.toString(attrId));

    }
    else if (type.equals("Sourcecode"))
    {
      ID = "SC".concat(Integer.toString(count)).concat("_F").concat(Integer.toString(attrId));
    }
    else
    {

      ID = "D".concat(Integer.toString(count)).concat("_F").concat(Integer.toString(attrId));

    }
    attrId++;
    return ID;
  }
  
  private String getMethodID()
  {
    String ID;
    if (type.equals("Requirement"))
    {
      ID = "RQ".concat(Integer.toString(count)).concat("_M").concat(Integer.toString(methodId));

    }
    else if (type.equals("Sourcecode"))
    {
      ID = "SC".concat(Integer.toString(count)).concat("_M").concat(Integer.toString(methodId));
    }
    else
    {
      ID = "D".concat(Integer.toString(count)).concat("_M").concat(Integer.toString(methodId));

    }
    methodId++;
    return ID;
  }
  private String getParamString(List<Parameter> paramList, String parameterString)
  {
    for (int j = 0; paramList != null && j < paramList.size(); j++)
    {
      parameterString += paramList.get(j);

      if (j != paramList.size() - 1)
      {
        parameterString += ", ";
      }
    }
    return parameterString;
  }

  private Attr setAttribute(Document doc, String value, Element artifactSubElement, String type)
  {
    Attr attrName = doc.createAttribute(type);
    attrName.setValue(value == null ? "" : value);
    artifactSubElement.setAttributeNode(attrName);
    return attrName;
  }

  private void createConnectionElement(Document doc, Element intraConnectionElement)
  {

    try
    {

      List<Dependencies> dependenciesesLst = depencyList;
      for (int i = 0; dependenciesesLst != null && i < dependenciesesLst.size(); i++)
      {
        Dependencies dependencies = dependenciesesLst.get(i);
        //putting AretefactSubElement
        Element connectionElement = doc.createElement(CONNECTION_ROOT);
        intraConnectionElement.appendChild(connectionElement);//append an artifact main root

        Element typeElement = doc.createElement(TYPE_CONNECTION_ROOT);
        typeElement.appendChild(doc.createTextNode(dependencies.getDependency_type()));
        connectionElement.appendChild(typeElement);

        Element startPonintElement = doc.createElement(STARTPOINT_ROOT);
        String id = getCurrentDesignId(dependencies.getSource_id());
        startPonintElement.appendChild(doc.createTextNode(id));
        connectionElement.appendChild(startPonintElement);

        Element multiplicitySrcElement = doc.createElement(MULTIPLICITY_ROOT);
        multiplicitySrcElement.appendChild(doc.createTextNode(getNomilizedString(dependencies.getMuliplicity_src())));
        connectionElement.appendChild(multiplicitySrcElement);

        Element endPonintElement = doc.createElement(ENDPOINT_ROOT);
        id = getCurrentDesignId(dependencies.getTaget_id());
        endPonintElement.appendChild(doc.createTextNode(id));
        connectionElement.appendChild(endPonintElement);

        Element multiplicityTargetElement = doc.createElement(MULTIPLICITY_ROOT);
        String textTarget = getNomilizedString(dependencies.getMultiplicity_target());

        multiplicityTargetElement.appendChild(doc.createTextNode(textTarget));
        connectionElement.appendChild(multiplicityTargetElement);

        Element annotationElement = doc.createElement(ANNOTATION_ROOT);
        String textAnnotation = getNomilizedString(dependencies.getAnnotation());
        annotationElement.appendChild(doc.createTextNode(textAnnotation));
        connectionElement.appendChild(annotationElement);

      }

    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }

  public String getCurrentDesignId(String reference)
  {

    return keyIDMap.get(reference);
  }

  public String getNomilizedString(String temp)
  {

    String result = "";

    if (temp == null || temp.isEmpty() || temp.equals("null") || temp.equals("data"))
    {
      return result;
    }
    else
    {
      return temp;
    }
  }
}
