package Group4.CS419RecommenderSystem;
import java.util.ArrayList;

public class StatisticalBaselineModel {

	public static double getRui(ArrayList<ArrayList<Integer>> trainingDataFromFile, int user, int item) {
		int miu_sum = 0;
		int u_sum = 0;
		int i_sum = 0;
		double miu_avg = 0;
		double u_avg = 0;
		double i_avg = 0;
		int u_count = 0;
		int i_count = 0;
		double Rui = 0;
		for (int i = 0; i < trainingDataFromFile.size(); i++) {
			miu_sum += trainingDataFromFile.get(i).get(2);
			if(trainingDataFromFile.get(i).get(0) == user) {
				u_sum += trainingDataFromFile.get(i).get(2);
				u_count++;
			}
			if(trainingDataFromFile.get(i).get(1) == item) {
				i_sum += trainingDataFromFile.get(i).get(2);
				i_count++;
			}
		}

		miu_avg = miu_sum/(trainingDataFromFile.size() * 1.0);

		if(u_count == 0 && i_count == 0) {											// user & item both not found in training set, make 2nd and 3rd term on line 47 = 0 and only consider rui = miu_avg
			u_avg = miu_avg;
			i_avg = miu_avg;
		}
		else if(i_count == 0) {														// item not found in training set, make 3rd term on line 47 0 by making i_avg = miu_avg
			u_avg = u_sum/(u_count*1.0);
			i_avg = miu_avg;
		}
		else if(u_count == 0) {														// user not found in training set, make 2nd term on line 47 0 by making u_avg = miu_avg
			i_avg = i_sum/(i_count*1.0);
			u_avg = miu_avg;
		}
		else {
			u_avg = u_sum/(u_count*1.0);
			i_avg = i_sum/(i_count*1.0);
		}

		Rui = miu_avg + (u_avg - miu_avg) + (i_avg - miu_avg);

		if(Rui > 5) {
			return 5.0;
		}
		else if(Rui < 1) {
			return 1.0;
		}
		return Double.parseDouble(String.format("%.2f", Rui));
	}

	public static double getEvaluatoin(String testFileFullAddr, String splitter, ArrayList<ArrayList<Integer>> trainingDataFromFile) {
		ArrayList<ArrayList<Integer>> testDataFromFile = GetDataFromFile.readFile(testFileFullAddr, splitter);
		double mae = 999999999;
		double mae_sum = 0;
		for (int i = 0; i < testDataFromFile.size(); i++) {
			double Rui = StatisticalBaselineModel.getRui(trainingDataFromFile, testDataFromFile.get(i).get(0), testDataFromFile.get(i).get(1));
			mae_sum += (Math.abs(Rui - testDataFromFile.get(i).get(2)));
		}
		mae = mae_sum/testDataFromFile.size();
		return Double.parseDouble(String.format("%.2f", mae));
	}

}
