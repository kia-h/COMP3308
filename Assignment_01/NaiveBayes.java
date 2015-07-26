// author: Internet's own boy

import java.util.ArrayList;
import java.util.HashMap;

public class NaiveBayes
{
	
	private HashMap<String, Double[]> attributeMean;
	private HashMap<String, Double[]> attributeStdDev;
	private HashMap<String, Double> classPriorProb;
	private HashMap<String, Integer> numClasses;	
	
	/**
	 * Constructs the Naive Bayes classifier
	 */
	public NaiveBayes() 
	{
		attributeMean = new HashMap<>();
		attributeStdDev = new HashMap<>();
		numClasses = new HashMap<>();
		classPriorProb = new HashMap<>();
	}
	
	/**
	 * Normal Probability Density Function
	 * @param x the value to calculate for
	 * @param u mean
	 * @param o standard deviation
	 * @return returns the Normal Probability Density Function for x
	 */
	public double pdf(double x, double u, double o) 
	{
		double fraction = (1.0)/(o * Math.sqrt(2*Math.PI));
		double exponent = -((Math.pow((x - u), 2)/(2*Math.pow(o, 2))));
		double e = Math.pow(Math.E, exponent);
		double pdf = fraction * e;
		return pdf;
	}
	
	/**
	 * Method to train the classifier
	 * @param trainingSet set of arrays(instances) the classifier trains on
	 */
	public void train(ArrayList<Instance> trainingSet) 
	{
		
		/* calculate mean for each attribute */
		for(Instance instance : trainingSet) 
		{ 
			// for each instance of the trainingSet
			
			String instanceClass = instance.getInstanceClass();
			
			/* If there is no attribute mean for a class in the map, initialise it with 
			 * the values array of that instance and then skip to the next instance.
			 * While traversing the instances keep count of the number of each class.
			 */
			if(!attributeMean.containsKey(instanceClass)) 
			{
				attributeMean.put(instanceClass, instance.getAttributes());
				numClasses.put(instanceClass, 1);
				continue;
			}
			
			int update = numClasses.get(instanceClass) + 1;
			numClasses.put(instanceClass, update);
			
			for(int i = 0; i < instance.getNumAttributes(); i++) 
			{
				attributeMean.get(instanceClass)[i] += instance.getAttributeValue(i);
			}
		}
		
		// for each class
		for(String key : attributeMean.keySet()) 
		{ 
			 // for each attribute
			for(int i = 0; i < attributeMean.get(key).length; i++) 
			{
				attributeMean.get(key)[i] /= numClasses.get(key);
			}
		}
		
		/* calculate standard deviation for each attribute */
		for(Instance instance : trainingSet) 
		{
			 // for each instance of the trainingSet			
			String instanceClass = instance.getInstanceClass();
			
			/* If there is no attribute standard deviation for a class in the map, 
			 * initialise it with that instances squared difference from the mean
			 * for each attribute and then skip to the next instance.
			 */
			if(!attributeStdDev.containsKey(instanceClass)) 
			{
				Double[] squaredDifferences = new Double[instance.getNumAttributes()];
				for(int i = 0; i < squaredDifferences.length; i++) 
				{
					double x = instance.getAttributeValue(i);
					double mean = attributeMean.get(instanceClass)[i];
					squaredDifferences[i] = Math.pow((x - mean), 2);
				}
				attributeStdDev.put(instanceClass, squaredDifferences);
				continue;
			}

			// for each attribute
			for(int i = 0; i < instance.getNumAttributes(); i++) 
			{ 
				double x = instance.getAttributeValue(i);
				double mean = attributeMean.get(instanceClass)[i];
				attributeStdDev.get(instanceClass)[i] += Math.pow((x - mean), 2);
			}
		}

		// for each class
		for(String key : attributeStdDev.keySet()) 
		{ 
			// for each attribute
			for(int i = 0; i < attributeStdDev.get(key).length; i++) 
			{ 
				attributeStdDev.get(key)[i] = Math.sqrt(attributeStdDev.get(key)[i]*(1.0/((numClasses.get(key) - 1))));
			}
		}
		
		/* calculate prior probability for each class */
		int totalInstances = trainingSet.size();

		// for each class
		for(String key : numClasses.keySet()) 
		{ 
			double priorProb = (double) (numClasses.get(key))/totalInstances;
			classPriorProb.put(key, priorProb);
		}
	}
	
	/**
	 * Classifies the given instance using Bayes Theorem: P(H|E) = P(E|H)P(H)
	 * @param testInstance an array containing the attribute values
	 * @return returns the classification for the given instance
	 */
	public String classify(Double[] testInstance) 
	{
		HashMap<String, Double> probabilities = new HashMap<>();
		String classification = "";
		Double highestProb = -Double.MAX_VALUE;
		
		/* calculate the probability for each class */
		for(String key : attributeMean.keySet()) 
		{ 
			// for each class
			Double probability = classPriorProb.get(key);

			// for each attribute
			for(int i = 0; i < testInstance.length; i++) 
			{ 
				double x = testInstance[i];
				double u = attributeMean.get(key)[i];
				double o = attributeStdDev.get(key)[i];
				double pdf = pdf(x, u, o);
				probability *= pdf;
			}
			probabilities.put(key, probability);
		}
		
		/* check which class is most likely */
		for(String key : probabilities.keySet()) 
		{ 
		// for each class
			if(probabilities.get(key) > highestProb) {
				highestProb = probabilities.get(key);
				classification = key;
			}
		}
		return classification;
	}

}//end of class