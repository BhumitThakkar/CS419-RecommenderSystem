package Group4.CS419RecommenderSystem;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class GradientDecentBaselineModel {

	public static HashMap<Integer, Double> getEvaluatoin(String testFileFullAddr, String splitter, ArrayList<ArrayList<Integer>> trainingDataFromFile, int totalUsers, int totalItems) {
		ArrayList<ArrayList<Integer>> testDataFromFile = GetDataFromFile.readFile(testFileFullAddr, splitter);
		HashMap<Integer, Double> MAE = new HashMap<Integer, Double>();
		double[] bus = new double[totalUsers];
		double[] bis = new double[totalItems];
		double[] initializedBus = Arrays.copyOf(initializeArray(bus), totalUsers);
		double[] initializedBis = Arrays.copyOf(initializeArray(bis), totalItems);
		double miu = getMiu(trainingDataFromFile);
		HashMap<Integer, ArrayList<Double>> epochWithEpochsVsLoss = new HashMap<Integer, ArrayList<Double>>();
		int[] epochs = {1, 5, 10, 50, 100};
		for (int i = 0; i < epochs.length; i++) {
			bus = Arrays.copyOf(initializedBus, totalUsers);
			bis = Arrays.copyOf(initializedBis, totalItems);
			ArrayList<Double> lossArrayList = updateBusBisEpochTimes(trainingDataFromFile, bus, bis, epochs[i], miu, totalUsers, totalItems);
			epochWithEpochsVsLoss.put(epochs[i], lossArrayList);
			double mae = Double.MAX_VALUE;
			double mae_sum = 0;
			for (int j = 0; j < testDataFromFile.size(); j++) {
				double Rui = GradientDecentBaselineModel.getRui(miu, bus[testDataFromFile.get(j).get(0) - 1], bis[testDataFromFile.get(j).get(1) - 1]);
				mae_sum += (Math.abs(Rui - testDataFromFile.get(j).get(2)));
			}
			mae = mae_sum/testDataFromFile.size();
			MAE.put(epochs[i], Double.parseDouble(String.format("%.2f", mae)));
		}
		try {
			String path = null;
			if(totalUsers == 943)
				path = "result/movieLens_epochWithEpochsVsLoss_result4.txt";
			else
				path = "result/yelp_epochWithEpochsVsLoss_result4.txt";
			writeEpochWithEpochsVsLoss(path, epochWithEpochsVsLoss);
			System.out.println();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return MAE;
	}

	private static double getMiu(ArrayList<ArrayList<Integer>> trainingDataFromFile) {
		double miu = 0;
		int miu_sum = 0;
		for (int i = 0; i < trainingDataFromFile.size(); i++) {
			miu_sum += trainingDataFromFile.get(i).get(2);
		}
		miu = miu_sum/(trainingDataFromFile.size() * 1.0);
		return miu;
	}
		
	private static ArrayList<Double> updateBusBisEpochTimes(ArrayList<ArrayList<Integer>> trainingDataFromFile, double[] bus, double[] bis, int epochs, double miu, int totalUsers, int totalItems) {
		ArrayList<Double> lossArrayList = new ArrayList<Double>();
		for (int e = 1; e <= epochs; e++) {
			double eui[][] = new double[totalUsers][totalItems];
			double lossFun = 0;
			double l = 0.01;
			double lambda = 0.5;
			for (int j = 0; j < trainingDataFromFile.size(); j++) {
				int u = trainingDataFromFile.get(j).get(0);
				int i = trainingDataFromFile.get(j).get(1);
				eui[u-1][i-1] = trainingDataFromFile.get(j).get(2) - miu - bus[u -1] - bis[i - 1];
				lossFun += ( eui[u-1][i-1]*eui[u-1][i-1] );
				bus[u - 1] = bus[u-1] - (l* ((-2*eui[u-1][i-1]) + (2*lambda*bus[u-1])) );
				bis[i - 1] = bis[i-1] - (l* ((-2*eui[u-1][i-1]) + (2*lambda*bis[i-1])) );
			}
			double buSq = sumOfSquareOfArray(bus);
			double biSq = sumOfSquareOfArray(bis);
			lossFun += ( lambda*(buSq + biSq) );
			lossArrayList.add(Double.parseDouble(String.format("%.2f", lossFun)));
		}
		return lossArrayList;
	}

	private static double sumOfSquareOfArray(double[] array) {
		double sum = 0;
		for (int i = 0; i < array.length; i++) {
			sum += (array[i] * array[i]);
		}
		return sum;
	}

	private static double[] initializeArray(double[] array) {
		for (int i = 0; i < array.length; i++) {
			array[i] = Double.parseDouble(String.format("%.2f", Math.random()));				// 0>= <1
		}
		return array;
	}

	private static double getRui(double miu, double bu, double bi) {
		return miu + bu + bi;
	}
	
	private static void writeEpochWithEpochsVsLoss(String filePath, HashMap<Integer, ArrayList<Double>> epochWithEpochsVsLoss) throws IOException {
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
	    ArrayList<Double> lossesArrayList = epochWithEpochsVsLoss.get(100);
	    for (int i = 0; i < lossesArrayList.size(); i++) {
	    	buffer.write(""+lossesArrayList.get(i)+"\t");
		}
	    buffer.close();
	}

}
