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

public class UserBasedSimilarityModel {
	
	public static HashMap<Integer, HashMap<Integer, Integer>> getRatingHashMap(ArrayList<ArrayList<Integer>> trainingDataFromFile, int totalUsers, int totalItems) {
		HashMap<Integer, HashMap<Integer, Integer>> ratingHashMap = new HashMap<Integer, HashMap<Integer,Integer>>();
		for (int i = 0; i < trainingDataFromFile.size(); i++) {
			HashMap<Integer, Integer> tempHashMap = ratingHashMap.get(trainingDataFromFile.get(i).get(0));
			if(tempHashMap == null) {
				tempHashMap = new HashMap<Integer, Integer>();
			}
			tempHashMap.put(trainingDataFromFile.get(i).get(1), trainingDataFromFile.get(i).get(2));
			ratingHashMap.put(trainingDataFromFile.get(i).get(0),tempHashMap);
		}
		return ratingHashMap;
	}

	public static ArrayList<ArrayList<Double>> getPCCArrayList(HashMap<Integer, HashMap<Integer, Integer>> ratingHashMap, int totalUsers, int totalItems) {
		ArrayList<ArrayList<Double>> pCCArrayList = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> tempArrayList = null;
		for (int u = 1; u <= totalUsers; u++) {
			System.gc();
			tempArrayList = new ArrayList<Double>();
			for (int v = 1; v <= totalUsers; v++) {
				if(u == v) {
					tempArrayList.add(1.0);
				} else if(u > v) {
					tempArrayList.add(pCCArrayList.get(v-1).get(u-1));
				} else {
					tempArrayList.add(calculatePCC(ratingHashMap, u , v, totalItems));
				}
			}
			pCCArrayList.add(tempArrayList);
		}
		return pCCArrayList;
	}

	private static Double calculatePCC(HashMap<Integer, HashMap<Integer, Integer>> ratingHashMap, int u, int v, int totalItems) {			// possible pcc: -5, -3, -2 & -1 to 1.
		double pCC = -5;											// So that we know if we found -5 in pcc array, then pcc never got calculated
		double numer = 0;
		double deno = 1;
		double deno1 = 0;
		double deno2 = 0;
		int sum_u = 0;
		int sum_v= 0;
		int common_count = 0;
		HashMap<Integer, Integer> user1 = ratingHashMap.get(u);
		HashMap<Integer, Integer> user2 = ratingHashMap.get(v);
		
		if(user1 != null || user2 != null) {							// Both user have at least rated on 1 item
			for (int i = 1; i <= totalItems; i++) {
				if(user1.get(i) != null && user2.get(i) != null) {
					sum_u += user1.get(i);
					sum_v += user2.get(i);
					common_count++;
				}
			}
			if(common_count == 0) {
				return -3.0;											// Both users have no common item rated
			} else {
				double uMean = sum_u/(common_count*1.0);
				double vMean = sum_v/(common_count*1.0);

				for (int i = 1; i <= totalItems; i++) {
					if(user1.get(i) != null && user2.get(i) != null) {
						int ui_rating = user1.get(i);
						int vi_rating = user2.get(i);
						numer += ( (ui_rating - uMean)*(vi_rating - vMean) );
						deno1 += ( (ui_rating - uMean)*(ui_rating - uMean) );
						deno2 += ( (vi_rating - vMean)*(vi_rating - vMean) );
					}
				}
				
				if(deno1 == 0 && deno2 == 0) {								// both user gave some specific rating for common items rated so far RESPECTIVELY (For u=1,1,1,1 ; v=5,5,5,5)
					int u_rated = (int) sum_u/common_count;
					int v_rated = (int) sum_v/common_count;
					int diff = Math.abs(u_rated - v_rated);
					if(diff == 0) {
						pCC = 1;
					} else if (diff == 1) {
						pCC = 0.5;				
					} else if (diff == 2) {
						pCC = 0;
					} else if (diff == 3) {
						pCC = -0.5;
					} else if (diff == 4) {
						pCC = -1;
					}
				} else if(deno1 == 0 || deno2 == 0) {
					pCC = -1;												// this will help in ignoring such neighbors as much as possible
				}
				else {
					deno = Math.sqrt(deno1) * Math.sqrt(deno2);
					pCC = Double.parseDouble(String.format("%.2f", numer/deno));
					if(pCC > 1) {											// so far never executed for u1.base
						pCC = 1;
					} else if(pCC < -1) {									// so far never executed for u1.base
						pCC = -1;
					}
				}
				return pCC;
			}
		} else {
			return -2.0;											// either of user has never rated
		}
	}

	public static HashMap<Integer, Double> getEvaluation(String testFileFullAddr, String splitter, ArrayList<ArrayList<Double>> pCCArrayList, HashMap<Integer, HashMap<Integer, Integer>> ratingHashMap, int totalUsers, int totalItems) {
		ArrayList<ArrayList<Integer>> testDataFromFile = GetDataFromFile.readFile(testFileFullAddr, splitter);
		HashMap<Integer, ArrayList<Double>> RuiHashMap = null;
		HashMap<Integer, ArrayList<Double>> diff = new HashMap<Integer, ArrayList<Double>>();
		HashMap<Integer, Double> MAE = new HashMap<Integer, Double>();
		int[] n = {1, 5, 10, 50, 100};
		
		for (int row = 0; row < testDataFromFile.size(); row++) {
			int user = testDataFromFile.get(row).get(0);
			int item = testDataFromFile.get(row).get(1);
			HashMap<Integer, Double> allNeighbors = getNeighbour(user, item, pCCArrayList, ratingHashMap, totalUsers);
			for (int index = 0; index < n.length; index++) {
				RuiHashMap = updateRui(user, item, RuiHashMap, n[index] ,allNeighbors, ratingHashMap, totalUsers);
				
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

	private static HashMap<Integer, Double> getNeighbour(int user, int item, ArrayList<ArrayList<Double>> pCCArrayList, HashMap<Integer, HashMap<Integer, Integer>> ratingHashMap, int totalUsers) {
		ArrayList<Integer> usersRatedOnItem = getUsersRatedOnItem(ratingHashMap, user, item, totalUsers);
		HashMap<Integer, Double> sortedPCCBetweenUserAndNeighbours = getSortedPCCBetweenUserAndNeighboursInDesc(usersRatedOnItem, pCCArrayList, user);
		return sortedPCCBetweenUserAndNeighbours;
	}

	private static ArrayList<Integer> getUsersRatedOnItem(HashMap<Integer, HashMap<Integer, Integer>> ratingHashMap, int user, int item, int totalUsers) {
		ArrayList<Integer> usersRatedOnItem = new ArrayList<Integer>();
		for (int u = 1; u <= totalUsers; u++) {
			if(u != user && ratingHashMap.get(u) != null)
				if(ratingHashMap.get(u).get(item) != null)
					usersRatedOnItem.add(u);
		}
		return usersRatedOnItem;
	}

	private static HashMap<Integer, Double> getSortedPCCBetweenUserAndNeighboursInDesc(ArrayList<Integer> usersRatedOnItem, ArrayList<ArrayList<Double>> pCCArrayList, int user) {
		HashMap<Integer, Double> PCCBetweenUserAndNeighbours = new HashMap<Integer, Double>();
		for (int i = 0; i < usersRatedOnItem.size(); i++) {
			if(pCCArrayList.get(user - 1).get(usersRatedOnItem.get(i) - 1) > 0)
				PCCBetweenUserAndNeighbours.put(usersRatedOnItem.get(i), pCCArrayList.get(user - 1).get(usersRatedOnItem.get(i) - 1));
		}
		HashMap<Integer, Double> sortedPCCBetweenUserAndNeighbours = sortByValueInDesc(PCCBetweenUserAndNeighbours);
		return sortedPCCBetweenUserAndNeighbours;
	}
	
	private static HashMap<Integer, ArrayList<Double>> updateRui(int user, int item, HashMap<Integer, ArrayList<Double>> RuiHashMap, int neighborsCount, HashMap<Integer, Double> allNeighbors, HashMap<Integer, HashMap<Integer, Integer>> ratingHashMap, int totalUsers) {
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
		
		Iterator<Entry<Integer, Double>> it = allNeighbors.entrySet().iterator();
		if(allNeighbors.size() == 0) {
			rui = 3;																	// No Neighbor who have rated on that item
		} else {
			double numer = 0;
			double deno = 0;
			for (int i = 1; i <= neighborsCount && it.hasNext(); i++) {
				Entry<Integer, Double> user_PCC = it.next();
				if(neighborsCount == 1) {
					rui = ratingHashMap.get(user_PCC.getKey()).get(item);
					break;
				} else {
					numer += ( user_PCC.getValue() * ratingHashMap.get(user_PCC.getKey()).get(item) );
					deno += Math.abs(user_PCC.getValue());
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