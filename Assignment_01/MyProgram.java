// author: Internet's own boy

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class MyProgram 
{

	public static void main(String[] args) 
	{
		BufferedReader trainingSetReader = null;
		BufferedReader testSetReader = null;
		String trainingSetPath = args[0];
		String testSetPath = args[1];
		String classifier = args[2];
		ArrayList<Instance> trainingSet = new ArrayList<>();
		ArrayList<Double[]> testSet = new ArrayList<>();
		
		try 
		{
			trainingSetReader = new BufferedReader(new FileReader(trainingSetPath));
			String line;
			String[] tokenizedLine;
			Double[] attributeValues;
			
			String instanceClass;
			
			/* read in training data set */
			while((line = trainingSetReader.readLine()) != null) 
			{
				if(line.trim().length() > 0) 
				{
					tokenizedLine = line.split(",");
					instanceClass = tokenizedLine[tokenizedLine.length - 1];
					attributeValues = new Double[tokenizedLine.length - 1];
					for(int i = 0; i < tokenizedLine.length - 1; i++) 
					{
						attributeValues[i] = Double.parseDouble(tokenizedLine[i]);
					}
					trainingSet.add(new Instance(attributeValues, instanceClass));
				}
			}

			testSetReader = new BufferedReader(new FileReader(testSetPath));
			
			/* read in test data set */
			while((line = testSetReader.readLine()) != null) 
			{
				if(line.trim().length() > 0) 
				{
					tokenizedLine = line.split(",");
					attributeValues = new Double[tokenizedLine.length];
					for(int i = 0; i < tokenizedLine.length; i++) 
					{
						attributeValues[i] = Double.parseDouble(tokenizedLine[i]);
					}
					testSet.add(attributeValues);
				}
			}
			
			/* run the classification algorithm given as an argument */
			if(classifier.equals("NB")) 
			{
				NaiveBayes nb = new NaiveBayes();
				nb.train(trainingSet);
				for(Double[] testInstance : testSet) 
				{
					System.out.println(nb.classify(testInstance));
				}
			}
			else 
			{
				int k = Character.getNumericValue(classifier.charAt(0));
				KNN knn = new KNN(trainingSet, k);
				for(Double[] testInstance : testSet) 
				{
					System.out.println(knn.classify(testInstance));
				}
			}
			
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		finally {
			if(trainingSetReader != null) 
			{
				try 
				{
					trainingSetReader.close();
				}
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
			if(testSetReader != null) 
			{
				try 
				{
					testSetReader.close();
				} catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		}
		
		/* if a fourth argument is passed in (filename), run 10-fold Stratified 
		 * Cross Validation
		 */
		if(args.length > 3) 
		{
			CrossValidation cv = new CrossValidation();
			cv.stratification(args[3]);
			cv.validation();
		}
	}//end of main method

}//end of class