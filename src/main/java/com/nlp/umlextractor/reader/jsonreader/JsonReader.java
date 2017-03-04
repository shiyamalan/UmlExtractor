package com.nlp.umlextractor.reader.jsonreader;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.nlp.umlextractor.model.Attribute;
import com.nlp.umlextractor.model.ClassData;
import com.nlp.umlextractor.model.Dependencies;
import com.nlp.umlextractor.model.InterfaceData;
import com.nlp.umlextractor.model.Method;
import com.nlp.umlextractor.model.ModelData;
import com.nlp.umlextractor.model.Parameter;
import com.nlp.umlextractor.staticdata.StaticData;
import com.util.Reader;

/**
 * Read the content from .mdj file, which comes from star UML and put the
 * contents into list
 * 
 * @author SRatnavel
 * @since 1.0
 * @version 1.0
 *
 */
public class JsonReader implements Reader
{
    private static HashMap<String, String> names_id_Map = new HashMap<String, String>();

    public static boolean isViewExists(String type, ViewReader aReader, String Id)
    {

	boolean isExist = false;
	List<String> tempReferences;
	if (type.equals("UMLClass") || type.equals("UMLInterface"))
	{

	    tempReferences = aReader.getClassView();
	    for (int i = 0; i < tempReferences.size(); i++)
	    {
		if (Id.equals(tempReferences.get(i)))
		{
		    return true;
		}
	    }
	} else if (type.equals("UMLGeneralization"))
	{
	    tempReferences = aReader.getGeneralizationView();
	    for (int i = 0; i < tempReferences.size(); i++)
	    {
		if (Id.equals(tempReferences.get(i)))
		{
		    return true;
		}
	    }

	} else if (type.equals("UMLAssociation"))
	{
	    tempReferences = aReader.getAssociationView();
	    for (int i = 0; i < tempReferences.size(); i++)
	    {
		if (Id.equals(tempReferences.get(i)))
		{
		    return true;
		}
	    }
	} else if (type.equals("UMLInterfaceRealization"))
	{
	    tempReferences = aReader.getRelizationView();
	    for (int i = 0; i < tempReferences.size(); i++)
	    {
		if (Id.equals(tempReferences.get(i)))
		{
		    return true;
		}
	    }
	}
	return isExist;
    }

    public void read1(String filePath) throws Exception
    {

	StaticData.classLst = new ArrayList<>();
	StaticData.depencyList = new ArrayList<>();
	ModelData tempData = null;

	FileReader reader = new FileReader(filePath);

	JSONParser jsonParser = new JSONParser();
	JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);

	JSONArray ownedElementArray = (JSONArray) jsonObject.get("ownedElements");
	JSONObject jsonObjOwnedElement = (JSONObject) ownedElementArray.get(0);

	JSONArray jsonArrOwnedElement = (JSONArray) jsonObjOwnedElement.get("ownedElements");

	Iterator<?> iterator = jsonArrOwnedElement.iterator();
	ViewReader aReader = new ViewReader(jsonArrOwnedElement);
	aReader.read("");
	while (iterator.hasNext())
	{
	    List<Attribute> attributeList = new ArrayList<Attribute>();
	    List<Method> operationList = new ArrayList<>();
	    JSONObject jsonInnerObj = (JSONObject) iterator.next();
	    String type = jsonInnerObj.get("_type").toString();
	    if (type.equals("UMLClass") || type.equalsIgnoreCase("UMLInterface"))
	    {

		if (type.equals("UMLClass"))
		{
		    tempData = new ClassData();
		    tempData.setType("Class");
		} else
		{
		    tempData = new InterfaceData();
		    tempData.setType("Interface");
		}

		String Id = jsonInnerObj.get("_id").toString();
		JSONArray jsonArr_Depencies = (JSONArray) jsonInnerObj.get("ownedElements");
		if (isViewExists(type, aReader, Id))
		{
		    tempData.setName(jsonInnerObj.get("name").toString());
		    tempData.setId(Id);
		    tempData.setVisibility(jsonInnerObj.get("visibility").toString());

		    JSONArray jsonArr_Attributes = (JSONArray) jsonInnerObj.get("attributes");
		    JSONArray jsonArr_Operations = (JSONArray) jsonInnerObj.get("operations");

		    names_id_Map.put(Id, jsonInnerObj.get("name").toString());
		    if (jsonArr_Attributes != null)
		    {
			Iterator<?> attributeIterator = jsonArr_Attributes.iterator();

			while (attributeIterator.hasNext())
			{

			    JSONObject jsonObject_Attribute = (JSONObject) attributeIterator.next();
			    Attribute attribute = new Attribute();
			    attribute.setType("UMLAttribute");
			    attribute.setId(jsonObject_Attribute.get("_id").toString());
			    attribute.setName(jsonObject_Attribute.get("name").toString());
			    attribute.setVisibility(jsonObject_Attribute.get("visibility").toString());
			    attribute.setDataType((String) jsonObject_Attribute.get("type"));
			    attribute.setIsStatic(jsonObject_Attribute.get("isStatic").toString());

			    attributeList.add(attribute);

			}
		    }

		    if (jsonArr_Operations != null)
		    {
			Iterator<?> operationIterator = jsonArr_Operations.iterator();

			while (operationIterator.hasNext())
			{

			    JSONObject jsonObject_Operation = (JSONObject) operationIterator.next();
			    Method operation = new Method();
			    operation.setType("UMLOperation");
			    operation.setId(jsonObject_Operation.get("_id").toString());
			    operation.setName(jsonObject_Operation.get("name").toString());
			    operation.setVisibility(jsonObject_Operation.get("visibility").toString());
			    operation.setIsStatic(jsonObject_Operation.get("isStatic").toString());
			    operation.setIsAbstract(jsonObject_Operation.get("isAbstract").toString());

			    Parameter parameter;// = new Parameter();
			    JSONArray jsonArr_Parameter = (JSONArray) jsonObject_Operation.get("parameters");

			    if (jsonArr_Parameter != null)
			    {
				Iterator<?> ParameterIterator = jsonArr_Parameter.iterator();
				List<Parameter> paramList = new ArrayList<Parameter>();
				while (ParameterIterator.hasNext())
				{
				    JSONObject jsonObject_Parameter = (JSONObject) ParameterIterator.next();
				    parameter = new Parameter();
				    Object obj = jsonObject_Parameter.get("name");
				    String name = "";
				    if (obj != null)
					name = obj.toString();
				    parameter.setParameterName(name);
				    obj = jsonObject_Parameter.get("type");
				    String typeParam = "";
				    if (obj != null)
					typeParam = obj.toString();
				    parameter.setParameterType(typeParam);
				    paramList.add(parameter);
				}
				operation.setParameterList(paramList);
			    }
			    operationList.add(operation);
			}
		    }

		    tempData.setAttributeList(attributeList);
		    tempData.setOperationList(operationList);
		    StaticData.classLst.add(tempData);

		}

		if (jsonArr_Depencies != null)
		{
		    Iterator<?> depencyIterator = jsonArr_Depencies.iterator();
		    while (depencyIterator.hasNext())
		    {

			JSONObject jsonObject_OwnedElement = (JSONObject) depencyIterator.next();
			Dependencies dependencies = new Dependencies();

			type = jsonObject_OwnedElement.get("_type").toString();
			if (type.equals("UMLGeneralization") || type.equals("UMLInterfaceRealization"))
			{
			    Id = jsonObject_OwnedElement.get("_id").toString();
			    if (isViewExists(type, aReader, Id))
			    {
				dependencies.setDependency_type(jsonObject_OwnedElement.get("_type").toString());
				String annotation = "";

				if (jsonObject_OwnedElement.get("name") != null)
				{
				    annotation = jsonObject_OwnedElement.get("name").toString();
				}
				JSONObject jsonObjSrc = (JSONObject) jsonObject_OwnedElement.get("source");
				dependencies.setSource_id(jsonObjSrc.get("$ref").toString());
				JSONObject jsonObjTarget = (JSONObject) jsonObject_OwnedElement.get("target");
				dependencies.setTaget_id(jsonObjTarget.get("$ref").toString());
				dependencies.setAnnotation(annotation);
				dependencies.setMuliplicity_src("data");
				dependencies.setMultiplicity_target("data");

				StaticData.depencyList.add(dependencies);
			    } // if finish of inner
			} else
			{
			    Id = jsonObject_OwnedElement.get("_id").toString();
			    if (isViewExists(type, aReader, Id))
			    {
				String annotation = "";
				if (jsonObject_OwnedElement.get("name") == null)
				{
				    annotation = "";
				} else
				{
				    annotation = jsonObject_OwnedElement.get("name").toString();
				}

				JSONObject jsonObjectEnd1 = (JSONObject) jsonObject_OwnedElement.get("end1");
				JSONObject tempRef = (JSONObject) jsonObjectEnd1.get("reference");
				String refenceSrc = tempRef.get("$ref").toString();
				String muliplicitySrc = "";

				if (jsonObjectEnd1.get("name") == null)
				{
				    muliplicitySrc = "";
				} else
				{
				    muliplicitySrc = jsonObjectEnd1.get("name").toString();
				}
				JSONObject jsonObjectEnd2 = (JSONObject) jsonObject_OwnedElement.get("end2");
				tempRef = (JSONObject) jsonObjectEnd2.get("reference");
				String refenceTarget = tempRef.get("$ref").toString();
				String muliplicityTarget = "";
				if (jsonObjectEnd2.get("name") == null)
				{
				    muliplicityTarget = "";
				} else
				{
				    muliplicityTarget = jsonObjectEnd2.get("name").toString();
				}
				dependencies = new Dependencies();
				dependencies.setDependency_type("Association");
				dependencies.setAnnotation(annotation);
				dependencies.setSource_id(refenceSrc);
				dependencies.setTaget_id(refenceTarget);
				dependencies.setMuliplicity_src(muliplicitySrc);
				dependencies.setMultiplicity_target(muliplicityTarget);

				StaticData.depencyList.add(dependencies);
			    } // if finish inner
			} // else finish of depency
		    } // while end of depency
		} // if checking finish of jsonArr_Depencies
	    } // if finish of check weather the umlclass or uml interface
	} // main while loop finish

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
		    System.out.println(
			    "\n\t Name : " + tempParam.getParameterName() + "   Type:" + tempParam.getParameterType());
		}
		System.out.println();
	    }
	}

	System.out.println("Depency Information\n Source\tSrc Multi\tTarget\tTarget Multi\t Annotation");
	for (int i = 0; i < StaticData.depencyList.size(); i++)
	{

	    Dependencies dependencies = StaticData.depencyList.get(i);

	    System.out.print(getName(dependencies.getSource_id()) + "\t");
	    System.out.print(dependencies.getMuliplicity_src() + "\t");
	    System.out.print(getName(dependencies.getTaget_id()) + "\t");
	    System.out.print(dependencies.getMultiplicity_target() + "\t");
	    System.out.print(dependencies.getAnnotation() + "\t");

	    System.out.println("\tType:" + dependencies.getDependency_type());
	}

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
	    read1(path);
	} catch (Exception e)
	{

	}
    }

}
