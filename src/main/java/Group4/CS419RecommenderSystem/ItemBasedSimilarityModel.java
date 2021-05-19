package Group4.CS419RecommenderSystem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ItemBasedSimilarityModel {

	public static HashMap<Integer, HashMap<Integer, Integer>> getRatingHashMap(ArrayList<ArrayList<Integer>> trainingDataFromFile, int totalUsers, int totalItems) {
		HashMap<Integer, HashMap<Integer, Integer>> ratingHashMap = new HashMap<Integer, HashMap<Integer,Integer>>();
		for (int i = 0; i < trainingDataFromFile.size(); i++) {
			HashMap<Integer, Integer> tempHashMap = ratingHashMap.get(trainingDataFromFile.get(i).get(1));
			if(tempHashMap == null) {
				tempHashMap = new HashMap<Integer, Integer>();
			}
			tempHashMap.put(trainingDataFromFile.get(i).get(0), trainingDataFromFile.get(i).get(2));
			ratingHashMap.put(trainingDataFromFile.get(i).get(1), tempHashMap);
		}
		return ratingHashMap;
	}

	public static ArrayList<ArrayList<Double>> getCosineSimilarityArrayList(HashMap<Integer, HashMap<Integer, Integer>> ratingHashMap, int totalUsers, int totalItems) {
		ArrayList<ArrayList<Double>> cosineSimilarityArrayList = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> tempArrayList = null;
		for (int i1 = 1; i1 <= totalItems; i1++) {
			tempArrayList = new ArrayList<Double>();
			for (int i2 = 1; i2 <= totalItems; i2++) {
				if(i1 == i2) {
					tempArrayList.add(1.0);
				} else if(i1 > i2) {
					tempArrayList.add(cosineSimilarityArrayList.get(i2-1).get(i1-1));
				} else {
					tempArrayList.add(calculateCosineSimilarity(ratingHashMap, i1 , i2, totalUsers));							// -1 if either of item is never rated
				}
			}
			cosineSimilarityArrayList.add(tempArrayList);
		}
		return cosineSimilarityArrayList;
	}

	private static Double calculateCosineSimilarity(HashMap<Integer, HashMap<Integer, Integer>> ratingHashMap, int i1, int i2, int totalUsers) {			// possible output: -5, -3, -2, 0 to 1
		double cosineSimilarity = -5;
		double numer = 0;
		double deno = 1;
		double deno1 = 0;
		double deno2 = 0;
		int common_count = 0;
		HashMap<Integer, Integer> item1 = ratingHashMap.get(i1);
		HashMap<Integer, Integer> item2 = ratingHashMap.get(i2);
		if(item1 != null && item2 != null) {
			for (int u = 1; u <= totalUsers; u++) {
				if(item1.get(u) != null && item2.get(u) != null) {
					numer += ( item1.get(u) * item2.get(u) );
					deno1 += ( item1.get(u) * item1.get(u) );
					deno2 += ( item2.get(u) * item2.get(u) );
					common_count++;
				}
			}
			if(common_count == 0) {
				return -3.0;											// No user have rated both this items
			} else {
				deno = Math.sqrt(deno1) * Math.sqrt(deno2);
				cosineSimilarity = Double.parseDouble(String.format("%.2f", numer/deno));
				return cosineSimilarity;
			}
		}
		else {
			return -2.0;												// either of item is never rated
		}
	}
	public static HashMap<Integer, Double> getEvaluation(String testFileFullAddr, String splitter, ArrayList<ArrayList<Double>> cosineSimilarityArrayList, HashMap<Integer, HashMap<Integer, Integer>> ratingHashMap, int totalUsers, int totalItems) {
		ArrayList<ArrayList<Integer>> testDataFromFile = GetDataFromFile.readFile(testFileFullAddr, splitter);
		HashMap<Integer, ArrayList<Double>> RuiHashMap = null;
		HashMap<Integer, ArrayList<Double>> diff = new HashMap<Integer, ArrayList<Double>>();
		HashMap<Integer, Double> MAE = new HashMap<Integer, Double>();
		int[] n = {1, 5, 10, 50, 100};
		
		for (int row = 0; row < testDataFromFile.size(); row++) {
			int user = testDataFromFile.get(row).get(0);
			int item = testDataFromFile.get(row).get(1);
			HashMap<Integer, Double> allNeighbors = getNeighbour(user, item, cosineSimilarityArrayList, ratingHashMap, totalItems);
			for (int index = 0; index < n.length; index++) {
				RuiHashMap = updateRui(user, item, RuiHashMap, n[index] ,allNeighbors, ratingHashMap, totalItems);
				
				ArrayList<Double> diffTempArrayList = null;
				if(diff.get(n[index]) == null) {
					diffTempArrayList = new ArrayList<Double>();
				} else {
					diffTempArrayList = diff.get(n[index]);
				}
				diffTempArrayList.add(Math.abs(testDataFromFile.get(row).get(2) - RuiHashMap.get(n[index]).get(RuiHashMap.get(n[index]).size()-1)));
				diff.put(n[index], diffTempArrayList);
			}
		}
		
		for (int i = 0; i < n.length; i++) {
			ArrayList<Double> tempArrayList2 = diff.get(n[i]);
			double mae = 0;
			for (int j = 0; j < tempArrayList2.size() ; j++) {
				mae += tempArrayList2.get(j);
			}
			MAE.put(n[i], Double.parseDouble(String.format("%.2f", mae/testDataFromFile.size() )));
		}
		System.out.println(MAE);
		return MAE;
	}

	private static HashMap<Integer, Double> getNeighbour(int user, int item, ArrayList<ArrayList<Double>> cosineSimilarityArrayList, HashMap<Integer, HashMap<Integer, Integer>> ratingHashMap, int totalItems) {
		ArrayList<Integer> ItemsRatedByUser = getItemsRatedByUser(ratingHashMap, user, item, totalItems);
		HashMap<Integer, Double> sortedCosineSimilarityBetweenItemAndNeighbours = getSortedCosineSimilarityBetweenItemAndNeighboursInDesc(ItemsRatedByUser, cosineSimilarityArrayList, item);
		return sortedCosineSimilarityBetweenItemAndNeighbours;
	}

	private static ArrayList<Integer> getItemsRatedByUser(HashMap<Integer, HashMap<Integer, Integer>> ratingHashMap, int user, int item, int totalItems) {
		ArrayList<Integer> usersRatedOnItem = new ArrayList<Integer>();
		for (int i = 1; i <= totalItems; i++) {
			if(i != item && ratingHashMap.get(i) != null)
				if(ratingHashMap.get(i).get(user) != null)
					usersRatedOnItem.add(i);
		}
		return usersRatedOnItem;
	}

	private static HashMap<Integer, Double> getSortedCosineSimilarityBetweenItemAndNeighboursInDesc(ArrayList<Integer> ItemsRatedByUser, ArrayList<ArrayList<Double>> cosineSimilarityArrayList, int item) {
		HashMap<Integer, Double> CosineSimilarityBetweenItemAndNeighbours = new HashMap<Integer, Double>();
		for (int i = 0; i < ItemsRatedByUser.size(); i++) {
			if(cosineSimilarityArrayList.get(item - 1).get(ItemsRatedByUser.get(i) - 1) > 0.5)
				CosineSimilarityBetweenItemAndNeighbours.put(ItemsRatedByUser.get(i), cosineSimilarityArrayList.get(item - 1).get(ItemsRatedByUser.get(i) - 1));
		}
		HashMap<Integer, Double> sortedCosineSimilarityBetweenItemAndNeighbours = sortByValueInDesc(CosineSimilarityBetweenItemAndNeighbours);
		return sortedCosineSimilarityBetweenItemAndNeighbours;
	}
	
	private static HashMap<Integer, ArrayList<Double>> updateRui(int user, int item, HashMap<Integer, ArrayList<Double>> RuiHashMap, int neighborsCount, HashMap<Integer, Double> allNeighbors, HashMap<Integer, HashMap<Integer, Integer>> ratingHashMap, int totalItems) {
		ArrayList<Double> tempRuiArrayList = null;
		double rui = 0;
		if(RuiHashMap == null) {
			RuiHashMap = new HashMap<Integer, ArrayList<Double>>();
		}

		if(RuiHashMap.get(neighborsCount) == null) {
			tempRuiArrayList = new ArrayList<Double>();
		} else {
			tempRuiArrayList = RuiHashMap.get(neighborsCount);
		}
		if(allNeighbors.size() == 0) {
			rui = 3;																	// No Neighbors	
		} else {
			Iterator<Entry<Integer, Double>> it = allNeighbors.entrySet().iterator();
			double numer = 0;
			double deno = 0;
			for (int i = 1; i <= neighborsCount && it.hasNext(); i++) {
				Entry<Integer, Double> item_cosinrSimilarity = it.next();
				if(neighborsCount == 1) {
					rui = ratingHashMap.get(item_cosinrSimilarity.getKey()).get(user);
					break;
				} else {
					numer += ( item_cosinrSimilarity.getValue() * ratingHashMap.get(item_cosinrSimilarity.getKey()).get(user) );
					deno += Math.abs(item_cosinrSimilarity.getValue());
				}
			}
			if(neighborsCount != 1) {													// check 8 lines above code
				rui = numer/deno;
			}
		}
		if(rui > 5) {
			rui = 5;
		} else if(rui < 1) {
			rui = 1;
		}
		tempRuiArrayList.add(Double.parseDouble(String.format("%.2f", rui)));
		RuiHashMap.put(neighborsCount, tempRuiArrayList);

		return RuiHashMap;
	}

	public static HashMap<Integer, Double> sortByValueInDesc(HashMap<Integer, Double> hm) {
        // Create a list from elements of HashMap 
        List<Entry<Integer, Double>> list = new LinkedList<Map.Entry<Integer, Double> >(hm.entrySet()); 
  
        // Sort the list 
        Collections.sort(list, new Comparator<Map.Entry<Integer, Double> >() { 
            public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {
                return (o2.getValue()).compareTo(o1.getValue()); 
            }
        });
          
        // put data from sorted list to hashmap  
        HashMap<Integer, Double> temp = new LinkedHashMap<Integer, Double>();
        for (Map.Entry<Integer, Double> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }
}