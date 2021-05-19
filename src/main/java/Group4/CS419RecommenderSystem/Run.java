package Group4.CS419RecommenderSystem;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Run {

	public static void main(String[] args) {
		try {
			Scanner scanner = null;
			boolean outerLoopAgain = true;
			boolean innerLoopAgain;
			int totalUsers = 0;
			int totalItems = 0;
			String dataset = null;
			String splitter = null;
			String trainingFullFileName = "";
			String testFullFileName = "";

			while (outerLoopAgain) {
		    	System.out.println();
		    	System.out.println("Select Dataset(1/2):");
				System.out.println("1: MovieLens");
				System.out.println("2: Yelp");
				System.out.println("3: Exit");
			    System.out.print("Waiting for your input key:");
			    scanner = new Scanner(System.in);
			    scanner.reset();
			    int datasetKey = scanner.nextInt();
			    
			    innerLoopAgain = true;
				switch (datasetKey) {
					case 1: {
					}
					case 2: {
						if(datasetKey == 1) {
							totalUsers = 943;
							totalItems = 1682;
							dataset = "MovieLens";
							splitter = "	";
							System.out.println();
						    System.out.print("Please provide training data set file name with extension(Preffered: u1.base):");
						    scanner = new Scanner(System.in);
						    scanner.reset();
						    trainingFullFileName = scanner.nextLine();
						    testFullFileName = trainingFullFileName.replace("base","test");
						} else {
							totalUsers = 11911;
							totalItems = 1579;
							dataset = "Yelp";
							splitter = ",";
						    trainingFullFileName = "yelp80csv.base";
						    testFullFileName = "yelp20csv.test";
						}

					    while(innerLoopAgain) {
							System.out.println();
						    System.out.println("Welcome to "+dataset+" dataset & "+trainingFullFileName+" file");
						    
					    	System.out.println();
					    	System.out.println("Select Algorithm(1/2/3/4):");
							System.out.println("1: User-based similarity model");
							System.out.println("2: Item-based similarity model");
							System.out.println("3: Baseline model | Statistical computation");
							System.out.println("4: Baseline model | Gradient descent");
							System.out.println("5: Change File Name");
							System.out.println("6: Change Dataset");
							System.out.println("7: Exit");
						    System.out.print("Waiting for your input key:");
						    scanner = new Scanner(System.in);
						    scanner.reset();
						    int algorithmKey = scanner.nextInt();
						    
						    switch (algorithmKey) {
								case 1: {
								    ArrayList<ArrayList<Integer>> trainingDataFromFile = GetDataFromFile.readFile(dataset+"/"+trainingFullFileName, splitter);
//								    display2DArrayList1(trainingDataFromFile);
								    HashMap<Integer, HashMap<Integer, Integer>> ratingHashMap = UserBasedSimilarityModel.getRatingHashMap(trainingDataFromFile, totalUsers, totalItems);
//								    display2DHashMap1(ratingHashMap, totalUsers);
								    ArrayList<ArrayList<Double>> pCCArrayList = UserBasedSimilarityModel.getPCCArrayList(ratingHashMap, totalUsers, totalItems);
//								    display2DArrayList2(pCCArrayList);
								    HashMap<Integer, Double> MAE = UserBasedSimilarityModel.getEvaluation(dataset+"/"+testFullFileName, splitter ,pCCArrayList, ratingHashMap, totalUsers, totalItems);
//								    writeMAEintoFile
								    writeMAE1or2or4("result/"+trainingFullFileName+"_result1.txt", MAE);
								    break;
								}
								case 2: {
								    ArrayList<ArrayList<Integer>> trainingDataFromFile = GetDataFromFile.readFile(dataset+"/"+trainingFullFileName, splitter);
//								    display2DArrayList1(trainingDataFromFile);
								    HashMap<Integer, HashMap<Integer, Integer>> ratingHashMap = ItemBasedSimilarityModel.getRatingHashMap(trainingDataFromFile, totalUsers, totalItems);
//								    display2DHashMap1(ratingHashMap, totalItems);
								    ArrayList<ArrayList<Double>> cosineSimilarityArrayList = ItemBasedSimilarityModel.getCosineSimilarityArrayList(ratingHashMap, totalUsers, totalItems);
//								    display2DArrayList2(cosineSimilarityArrayList);
								    HashMap<Integer, Double> MAE = ItemBasedSimilarityModel.getEvaluation(dataset+"/"+testFullFileName, splitter ,cosineSimilarityArrayList, ratingHashMap, totalUsers, totalItems);
//								    writeMAEintoFile
								    writeMAE1or2or4("result/"+trainingFullFileName+"_result2.txt", MAE);
								    break;
								}
								case 3: {
									ArrayList<ArrayList<Integer>> trainingDataFromFile = GetDataFromFile.readFile(dataset+"/"+trainingFullFileName, splitter);
								    double MAE = StatisticalBaselineModel.getEvaluatoin(dataset+"/"+testFullFileName, splitter ,trainingDataFromFile);
									System.out.println("MAE: "+MAE);
//								    writeMAEintoFile
								    writeMAE3("result/"+trainingFullFileName+"_result3.txt", MAE);
									break;
								}
								case 4: {
									ArrayList<ArrayList<Integer>> trainingDataFromFile = GetDataFromFile.readFile(dataset+"/"+trainingFullFileName, splitter);
									HashMap<Integer, Double> MAE = GradientDecentBaselineModel.getEvaluatoin(dataset+"/"+testFullFileName, splitter ,trainingDataFromFile, totalUsers, totalItems);
									System.out.println("MAE: "+MAE);
//								    writeMAEintoFile
									writeMAE1or2or4("result/"+trainingFullFileName+"_result4.txt", MAE);
									break;
								}
								case 5: {
									if(datasetKey == 1) {
									    System.out.print("Please provide training data set file name with extension:");
									    scanner = new Scanner(System.in);
									    scanner.reset();
									    trainingFullFileName= scanner.nextLine();
									}
									else {
										System.out.println("Sorry only 1 file to train and test in Yelp Dataset");
									}
									break;
								}
								case 6: {
									innerLoopAgain = false;
									break;
								}
								case 7: {
									System.out.println();
								    System.out.println("Good Bye...");
									outerLoopAgain = false;
									innerLoopAgain = false;
									break;
								}
								default:{
									System.out.println();
									throw new IllegalArgumentException("Unexpected value: " + algorithmKey);
								}
							}
					    }
					    break;
					}
					case 3: {
						System.out.println();
					    System.out.println("Good Bye...");
					    outerLoopAgain = false;
					    break;
					}
					default: {
						System.out.println();
						throw new IllegalArgumentException("Unexpected value: " + datasetKey);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void writeMAE3(String filePath, double mAE) throws IOException {
		// TODO Auto-generated method stub
		File newDirectory = new File("result");
        //Create directory for non existed path.
        boolean isCreated = newDirectory.mkdirs();
        if (isCreated) {
            System.out.printf("1. Successfully created directories, path:%s",
                    newDirectory.getCanonicalPath());
        } else if (newDirectory.exists()) {
            System.out.printf("1. Directory path already exist, path:%s",
                    newDirectory.getCanonicalPath());
        } else {
            System.out.println("1. Unable to create directory");
            return;
        }
        
        File file = new File(filePath);
		file.createNewFile();
		FileWriter writer = new FileWriter(file);
	    BufferedWriter buffer = new BufferedWriter(writer);
	    buffer.write(""+mAE);
	    buffer.close();
	}

	private static void writeMAE1or2or4(String filePath, HashMap<Integer, Double> mAE) throws IOException {
		// TODO Auto-generated method stub
		File newDirectory = new File("result");
        //Create directory for non existed path.
        boolean isCreated = newDirectory.mkdirs();
        if (isCreated) {
            System.out.printf("1. Successfully created directories, path:%s",
                    newDirectory.getCanonicalPath());
        } else if (newDirectory.exists()) {
            System.out.printf("1. Directory path already exist, path:%s",
                    newDirectory.getCanonicalPath());
        } else {
            System.out.println("1. Unable to create directory");
            return;
        }
        
        File file = new File(filePath);
		file.createNewFile();
		FileWriter writer = new FileWriter(file);
	    BufferedWriter buffer = new BufferedWriter(writer);
	    buffer.write("1,"+mAE.get(1));
	    buffer.newLine();
	    buffer.write("5,"+mAE.get(5));
	    buffer.newLine();
	    buffer.write("10,"+mAE.get(10));
	    buffer.newLine();
	    buffer.write("50,"+mAE.get(50));
	    buffer.newLine();
	    buffer.write("100,"+mAE.get(100));
	    buffer.close();
	}

	public static void display2DArrayList1(ArrayList<ArrayList<Integer>> arrayList) {
		for (int i = 0; i < arrayList.size(); i++) {
		      System.out.println(arrayList.get(i));
		}
	}
	
	public static void display2DArrayList2(ArrayList<ArrayList<Double>> arrayList) {
		for (int i = 0; i < arrayList.size(); i++) {
		      System.out.println(arrayList.get(i));
		}
	}

	public static void display2DHashMap1(HashMap<Integer, HashMap<Integer, Integer>> hashMap, int users) {
		for (int i = 1; i <= users; i++) {
			System.out.println(hashMap.get(i));
		}
	}

}
