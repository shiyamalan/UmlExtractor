package com.nlp.umlextractor.reader.xmireader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.nlp.umlextractor.model.Attribute;
import com.nlp.umlextractor.model.ClassData;
import com.nlp.umlextractor.model.Dependencies;
import com.nlp.umlextractor.model.Method;
import com.nlp.umlextractor.model.ModelData;
import com.nlp.umlextractor.model.Parameter;
import com.util.Reader;
import com.util.StaticData;

/**
 * Read the content from MODELIO UML Diagram File, the file format is looks like XML format.
 * It can be read using Java DOMParser.
 * 
 * 
 * @author SRatnavel
 * @version 1.0
 * @since 1.0
 */
public class XMLReader implements Reader
{

  private static HashMap<String, String> names_id_Map = new HashMap<String, String>();

  public void readUMLXMI(String path) throws ParserConfigurationException, SAXException, IOException
  {
    StaticData.classLst = new ArrayList<ModelData>();
    StaticData.depencyList = new ArrayList<Dependencies>();

    File fXmlFile = new File(path);
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    Document doc = dBuilder.parse(fXmlFile);

    ClassData tempData = new ClassData();
    doc.getDocumentElement().normalize();
    NodeList nList = doc.getElementsByTagName("packagedElement");
    for (int temp = 0; temp < nList.getLength(); temp++)
    {
      Node nNode = nList.item(temp);
      tempData = new ClassData();
      if (nNode.getNodeType() == Node.ELEMENT_NODE)
      {

        Element eElement = (Element) nNode;

        String className = eElement.getAttribute("name");
        String id = eElement.getAttribute("xmi:id");
        String type = eElement.getAttribute("xmi:type").split(":")[1];

        if (type.equals("Class") || type.equals("Interface"))
        {
          tempData.setName(className);
          tempData.setId(id);
          tempData.setType(type);
          names_id_Map.put(id, className);
          NodeList tempGenList = eElement.getElementsByTagName("generalization");
          NodeList tempRealList = eElement.getElementsByTagName("interfaceRealization");
          if (tempGenList.getLength() > 0)
          {
            NodeList generalizationList = eElement.getElementsByTagName("generalization");
            Element genralElement = (Element) generalizationList.item(0);
            Dependencies depencies = new Dependencies();
            depencies.setAnnotation("Extends");
            depencies.setSource_id(genralElement.getAttribute("general"));
            depencies.setTaget_id(id);
            depencies.setDependency_type("Generalzation");
            StaticData.depencyList.add(depencies);
          }
          if (tempRealList.getLength() > 0)
          {

            NodeList realizationList = eElement.getElementsByTagName("interfaceRealization");
            for (int a = 0; a < realizationList.getLength(); a++)
            {
              Element genralElement = (Element) realizationList.item(a);
              Dependencies depencies = new Dependencies();
              depencies.setAnnotation("Implements");
              depencies.setSource_id(genralElement.getAttribute("supplier"));
              depencies.setTaget_id(genralElement.getAttribute("client"));
              depencies.setDependency_type("InterfaceRealization");
              StaticData.depencyList.add(depencies);
            }
          }

          NodeList attributeNodes = eElement.getElementsByTagName("ownedAttribute");
          List<Attribute> attributeList = new ArrayList<Attribute>();
          List<Method> operationList = new ArrayList<Method>();
          for (int i = 0; i < attributeNodes.getLength(); i++)
          {
            nNode = attributeNodes.item(i);
            if (nNode.getNodeType() == Node.ELEMENT_NODE)
            {
              eElement = (Element) nNode;
              Attribute attribute = new Attribute();
              if (!eElement.hasAttribute("type"))
              {

                attribute.setName(eElement.getAttribute("name"));
                attribute.setId(eElement.getAttribute("xmi:id"));
                attribute.setVisibility(eElement.getAttribute("visibility"));
                nNode = eElement.getElementsByTagName("type").item(0);
                
                eElement = (Element) nNode;

                attribute.setDataType(eElement.getAttribute("href").split("#")[1]);
                attribute.setType("Attribute");
                attributeList.add(attribute);
              }
              else if (eElement.hasAttribute("association"))
              {
                getDependencyList(eElement, id);
              }

            }
          }
          nNode = nList.item(temp);
          eElement = (Element) nNode;
          NodeList operationNodes = eElement.getElementsByTagName("ownedOperation");
          for (int i = 0; i < operationNodes.getLength(); i++)
          {
            nNode = operationNodes.item(i);
            if (nNode.getNodeType() == Node.ELEMENT_NODE)
            {
              eElement = (Element) nNode;
              Method operation = new Method();
              String operation_name = eElement.getAttribute("name");
              String operation_id = eElement.getAttribute("xmi:id");
              String operation_visibility = eElement.getAttribute("visibility");

              NodeList parameterList = eElement.getElementsByTagName("ownedParameter");
              List<Parameter> paramList = new ArrayList<Parameter>();
              for (int j = 0; j < parameterList.getLength(); j++)
              {
                Node node = parameterList.item(j);
                Parameter parameter = new Parameter();
                if (node.getNodeType() == Node.ELEMENT_NODE)
                {
                  Element element = (Element) node;

                  if (element.hasAttribute("direction"))
                  {
                    nNode = element.getElementsByTagName("type").item(0);
                    eElement = (Element) nNode;

                    String return_type = eElement.getAttribute("href").split("#")[1];
                    operation.setReturnType(return_type);
                  }
                  else
                  {
                    String parameterName = element.getAttribute("name");
                    nNode = element.getElementsByTagName("type").item(0);
                    eElement = (Element) nNode;

                    String parameter_type = eElement.getAttribute("href").split("#")[1];
                    parameter.setParameterName(parameterName);
                    parameter.setParameterType(parameter_type);
                    paramList.add(parameter);
                    operation.setReturnType("null");
                  }

                }

              } //end of inner for loop of parameter
              operation.setName(operation_name);
              operation.setType("operation");
              operation.setId(operation_id);
              operation.setVisibility(operation_visibility);
              operation.setParameterList(paramList);
              operationList.add(operation);
            }
          }
          tempData.setAttributeList(attributeList);
          tempData.setOperationList(operationList);
          StaticData.classLst.add(tempData);
        }
        else if (type.equals("Association"))
        {
          //because of there is associaion is in package element tag
          //getting association depency list

        }
      }
    }

    for (int i = 0; i < StaticData.classLst.size(); i++)
    {
      ModelData temp = StaticData.classLst.get(i);
      System.out.println(temp.getType() + "    " + temp.getName());

      List<Attribute> lstAtr = temp.getAttributeList();
      List<Method> lstOpr = temp.getOperationList();

      for (int j = 0; j < lstAtr.size(); j++)
      {
        System.out.println(lstAtr.get(j).getType() + ":->" + lstAtr.get(j).getName());
      }
      for (int j = 0; j < lstOpr.size(); j++)
      {
        System.out.print(lstOpr.get(j).getType() + ":->" + lstOpr.get(j).getName());

        List<Parameter> paramLst = lstOpr.get(j).getParameterList();
        for (int a = 0; paramLst != null && a < paramLst.size(); a++)
        {
          Parameter tempParam = paramLst.get(a);
          System.out.println("\n\t Name : " + tempParam.getParameterName() + "   Type:" + tempParam.getParameterType());
        }
        System.out.println("Return Type->" + lstOpr.get(j).getReturnType());
        System.out.println();
      }
    }
  }

  private void getDependencyList(Element eElement, String id)
  {
    Dependencies dependencies = new Dependencies();
    if (eElement.hasAttribute("aggregation"))
    {

      if (eElement.getAttribute("aggregation").equals("composite"))
      {
        dependencies.setDependency_type(eElement.getAttribute("aggregation"));
      }
      else if (eElement.getAttribute("aggregation").equals("shared"))
      {
        dependencies.setDependency_type("aggregation");
      }

    }
    else
    {
      dependencies.setDependency_type("association");
    }
    dependencies.setAnnotation(eElement.getAttribute("name"));
    dependencies.setSource_id(id);
    dependencies.setTaget_id(eElement.getAttribute("type"));

    StaticData.depencyList.add(dependencies);
  }

  public static String getName(String id)
  {
    return names_id_Map.get(id);
  }

  @Override
  public void read(String path) 
  {
    try
    {
      readUMLXMI(path);
    }
    catch (ParserConfigurationException | SAXException | IOException e)
    {
      System.err.println(e);
    }

  }
}
