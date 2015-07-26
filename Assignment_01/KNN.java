// author: Internet's own boy

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;


public class KNN 
{
	
	private ArrayList<Instance> trainingSet;
	private final int K;
	
	/**
	 * Constructs the kNN classifier
	 * @param trainingSet set of arrays(instances) the classifier trains on
	 * @param k number of neighbours
	 */
	public KNN(ArrayList<Instance> trainingSet, int k) 
	{
		this.trainingSet = trainingSet;
		this.K = k;
	}
	
	/**
	 * Euclidean distance measure
	 * @param a vector of attribute values
	 * @param b vector of attribute values
	 * @return returns the Eucledian distance between vector a and vector b
	 */
	private double eucledianDistance(Double[] a, Double[] b) 
	{
		double distance = 0.0;
		for(int i = 0; i < a.length; i++) {
			distance += Math.pow((a[i] - b[i]), 2);
		}
		return Math.sqrt(distance);
	}
	
	/**
	 * Classifies the given instance using KNN classifier
	 * @param testInstance an array containing the attribute values
	 * @return returns the classification for the given instance
	 */
	public String classify(Double[] testInstance) 
	{
		TreeMap<Double, String> eucledianDistances= new TreeMap<>();
		HashMap<String, Integer> classCount = new HashMap<>();
		String classification = "";
		
		/* calculate the testInstances distance from each value of the trainingSet */
		for(Instance instance : trainingSet) 
		{
			String instanceClass = instance.getInstanceClass();
			double distance = eucledianDistance(instance.getAttributes(), testInstance);
			eucledianDistances.put(distance, instanceClass);
		}
		
		/* get the K closest instances */
		int i = 1;
		for(Double distance : eucledianDistances.keySet()) 
		{
			if(i > K) {
				break;
			}
			String distanceClass = eucledianDistances.get(distance);
			if(!classCount.containsKey(distanceClass)) 
			{
				classCount.put(distanceClass, 1);
				i++;
				continue;
			}
			classCount.put(distanceClass, (classCount.get(distanceClass) + 1));
			i++;
		}

		
		/* work out the majority class - this is the classification */
		int trackMajority = 0;

		// for each class
		for(String instanceClass : classCount.keySet()) 
		{ 
			if(classCount.get(instanceClass) == trackMajority) 
			{
				classification = "yes";
			}
			else if(classCount.get(instanceClass) > trackMajority) 
			{
				classification = instanceClass;
				trackMajority = classCount.get(instanceClass);
			}
		}
		return classification;
	}
	
}//end of class
