// author: Internet's own boy

/**
 * Class to store a single instance of a data set
 */
public class Instance 
{
	
	private final Double[] attributeValues;
	private final String instanceClass;
	
	public Instance(Double[] attributeValues, String instanceClass) 
	{
		this.attributeValues = attributeValues;
		this.instanceClass = instanceClass;
	}
	
	public Double[] getAttributes() 
	{
		return attributeValues.clone();
	}
	
	public double getAttributeValue(int index) 
	{
		return attributeValues[index];
	}
	
	public int getNumAttributes() 
	{
		return attributeValues.length;
	}
	
	public String getInstanceClass() 
	{
		return instanceClass;
	}
	
	public String getAttributesAsString() 
	{
		String line = "";
		for(int i = 0; i < attributeValues.length; i++) 
		{
			line += attributeValues[i];
			line += ",";
		}
		line += instanceClass;
		return line;
	}
	
}//end of class
