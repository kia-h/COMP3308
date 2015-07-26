// author: Internet's own boy

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class CrossValidation 
{
	
	private final int S_FOLDS = 10;

	// change for different K in KNN
	private final int K = 5;
	private ArrayList<ArrayList<Instance>> folds;
	private String filename;
	
	public CrossValidation() 
	{
		folds = new ArrayList<>();
		for(int i = 0; i < S_FOLDS; i++) 
		{
			folds.add(new ArrayList<Instance>());
		}
	}
	
	/**
	 * Stratifies the data into S folds
	 * @param filename the file containing the data
	 */
	public void stratification(String filename) 
	{
		BufferedReader reader = null;
		BufferedWriter writer = null;
		HashMap<String, ArrayList<Instance>> dataSet = new HashMap<>();
		String[] tokenizedString = filename.split("\\.");
		String outFilename = tokenizedString[0] + "-folds." + tokenizedString[1];
		this.filename = outFilename;
		
		try 
		{
			reader = new BufferedReader(new FileReader(filename));
			String line;
			String[] tokenizedLine;
			Double[] attributeValues;
			
			String instanceClass;
			
			/* read in the data set */
			while((line = reader.readLine()) != null) 
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
					if(!dataSet.containsKey(instanceClass)) 
					{
						dataSet.put(instanceClass, new ArrayList<Instance>());
					}
					dataSet.get(instanceClass).add(new Instance(attributeValues, instanceClass));
				}
			}
			
			/* create the S folds */
			for(String key : dataSet.keySet()) 
			{ 
				// for each class
				/* shuffle the instances */
				Collections.shuffle(dataSet.get(key));
				int numClassInstances = dataSet.get(key).size();

				 // for each fold
				for(int i = 0; i < folds.size(); i++) 
				{
					// for each instance of that class
					for(int j = (numClassInstances/S_FOLDS) - 1; j >= 0; j--) 
					{ 
						folds.get(i).add(dataSet.get(key).get(j));
						dataSet.get(key).remove(j);
					}
				}
			}
			/* add remaining instances evenly to folds */
			for(String key : dataSet.keySet()) 
			{ 
				// for each class
				for(int i = 0; i < folds.size(); i++) 
				{ // for each fold
					if(dataSet.get(key).size() <= 0) 
					{
						break;
					}
					folds.get(i).add(dataSet.get(key).get(0));
					dataSet.get(key).remove(0);
				}
			}
			
			writer = new BufferedWriter(new FileWriter(outFilename));

			/* write the S folds to file */
			for(int i = 0; i < folds.size(); i++) 
			{
				writer.write("fold" + (i + 1));
				for(int j = 0; j < folds.get(i).size(); j++) 
				{
					writer.write("\n" + folds.get(i).get(j).getAttributesAsString());
				}
				if(i != folds.size() - 1) 
				{
					writer.write("\n\n");
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
		finally 
		{
			if(reader != null) 
			{
				try 
				{
					reader.close();
				} 
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			if(writer != null) 
			{
				try 
				{
					writer.close();
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Runs Cross-Validation on the S folds
	 */
	public void validation() 
	{
		double nbAccuracy = 0.0;
		double knnAccuracy = 0.0;
		
		System.out.println("\nRunning Cross-Validation on file: " + filename);
		
		/* Run 10 fold cross validation for Naive Bayes and KNN */
		for(int i = 0; i < S_FOLDS; i++) 
		{
			ArrayList<Instance> trainingSet = new ArrayList<>();
			for(int j = 0; j < folds.size(); j++) 
			{
				if(j != i)
				 {
					trainingSet.addAll(folds.get(j));
				}
			}
			NaiveBayes nb = new NaiveBayes();
			nb.train(trainingSet);
			KNN knn = new KNN(trainingSet, K);
			double nbRunAccuracy = 0.0;
			double knnRunAccuracy = 0.0;
			
			// for each instance in the test fold
			for(int k = 0; k < folds.get(i).size(); k++)
			{ 
				String result = nb.classify(folds.get(i).get(k).getAttributes());
				String actualClass = folds.get(i).get(k).getInstanceClass();
				if(result.equals(actualClass)) 
				{
					nbRunAccuracy += 1.0;
				}
				result = knn.classify(folds.get(i).get(k).getAttributes());
				if(result.equals(actualClass)) 
				{
					knnRunAccuracy += 1.0;
				}
			}
			nbRunAccuracy /= folds.get(i).size();
			knnRunAccuracy /= folds.get(i).size();
			nbAccuracy += nbRunAccuracy;
			knnAccuracy += knnRunAccuracy;
			System.out.println("Run " + (i + 1) + " leaving fold " + (i + 1) +" out - " + "Accuracy of Naive Bayes: " + nbRunAccuracy + "%");
			System.out.println("Run " + (i + 1) + " leaving fold " + (i + 1) +" out - " + "Accuracy of " + K + "NN: " + knnRunAccuracy + "%");
		}
		nbAccuracy /= S_FOLDS;
		knnAccuracy /= S_FOLDS;
		System.out.println("Accuracy of Naive Bayes: " + nbAccuracy + "%");
		System.out.println("Accuracy of " + K + "NN: " + knnAccuracy + "%");
	}
	
}//end of class