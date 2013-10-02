package org.openrosa.client.model;

import java.util.List;

import org.purc.purcforms.client.xforms.XformConstants;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;

/**
 * Defines an instance that is used to store information for use in dynamic selection questions
 * @author etherton
 *
 */
public class DataInstanceDef extends DataDefBase{

	
	
	String instanceId = null;
	
	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}


	public void setRootNodeName(String rootNodeName) {
		this.rootNodeName = rootNodeName;
	}

	String rootNodeName = null;
	
	/**
	 * Constructor
	 * @param instanceId
	 * @param rootNodeName
	 * @param parent
	 */
	public DataInstanceDef(String instanceId, String rootNodeName, IFormElement parent)
	{
		super(parent);
		this.instanceId = instanceId;
		this.rootNodeName = rootNodeName;
	}
	
	/**
	 * A copy constructor
	 * @param did the data instance to copy from
	 * @param parent the parent of the newly copied data instance
	 */
	public DataInstanceDef(DataInstanceDef did, IFormElement parent)
	{
		super(parent);
		this.instanceId = did.instanceId;
		this.rootNodeName = did.rootNodeName;
		//now copy the actual data
		for(IFormElement kid : did.getChildren())
		{
			if(!(kid instanceof DataDef))
			{
				continue;
			}
			DataDef dd = ((DataDef)kid).copy(this);
			this.addChild(dd);
		}
	}
	
	/**
	 * Copy a data instance def, but point it to the new parent
	 */
	public DataInstanceDef copy(IFormElement parent)
	{
		return new DataInstanceDef(this, parent);
	}

	public String getInstanceId() {
		return instanceId;
	}

	public String getRootNodeName() {
		return rootNodeName;
	}


	@Override
	public String getPath() {
		return getAbsolutePath();
	}


	@Override
	public String getAbsolutePath() {
		return "instance('" + instanceId + "')/" + rootNodeName;
	}
	
	public String toString()
	{
		return "instance('"+ instanceId + " / " + rootNodeName +"')";
	}
	
	/**
	 * Getter for the name
	 * @return
	 */
	public String getName() {
		return instanceId;
		//return "instance('" + instanceId +"')/" + rootNodeName;
	}


	@Override
	public void writeToXML(Document doc, Element instanceNode, Element bindNode,
			List<Element> iTextNodes, Element UINode) 
	{
		//write the instance node first
		Element dataInstanceNode = doc.createElement(XformConstants.NODE_NAME_INSTANCE);
		//set the id
		dataInstanceNode.setAttribute(XformConstants.ATTRIBUTE_NAME_ID, getInstanceId());
		//add this to the doc
		bindNode.appendChild(dataInstanceNode);
		
		//create the root node for the data
		Element dataRootNode = doc.createElement(getRootNodeName());
		//add this to the doc
		dataInstanceNode.appendChild(dataRootNode);
		
		//loop over the kids
		for(IFormElement kid : getChildren())
		{
			//this shouldn't happen, but just in case
			if(!(kid instanceof DataDef))
			{
				continue;
			}
			
			((DataDef)kid).writeToXML(doc, instanceNode, dataRootNode, iTextNodes, UINode);
		}
	}
}
