package Group4.CS419RecommenderSystem;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class PartitionYelpDataset {

	public static void main(String[] args) {
		ArrayList<ArrayList<Integer>> yelpFullFile = GetDataFromFile.readFile("Yelp/ratings.csv", ",");
		yelpFullFile.sort(new Comparator<ArrayList<Integer>>() {
			@Override
			public int compare(ArrayList<Integer> o1, ArrayList<Integer> o2) {
				return o1.get(0).compareTo(o2.get(0));
			}
		});
		Run.display2DArrayList1(yelpFullFile);									// User Id, Business Id, Rating
		
		HashMap<Integer, ArrayList<ArrayList<Integer>>> yelp80HashMap = new HashMap<Integer, ArrayList<ArrayList<Integer>>>();
		HashMap<Integer, ArrayList<ArrayList<Integer>>> yelp20HashMap = new HashMap<Integer, ArrayList<ArrayList<Integer>>>();
		
		int count = 0;
		int oldUser = 0;
		int currentUser;
		for (int i = 0; i < yelpFullFile.size(); i++) {
			currentUser = yelpFullFile.get(i).get(0);
			if(oldUser == currentUser) {
				ArrayList<ArrayList<Integer>> addArrayList20 = null;
				int sizeIn20;
				if(yelp20HashMap.get(currentUser) == null) {
					sizeIn20 = 0;
					addArrayList20 = new ArrayList<ArrayList<Integer>>();
					addArrayList20.add(yelpFullFile.get(i));
					yelp20HashMap.put(currentUser, addArrayList20);			// 2nd always goes in 20
					count++;
					continue;
				} else {
					sizeIn20 = yelp20HashMap.get(currentUser).size();
				}
				int iteration = (count++/5);								// int iteration = (count++/5) + 1;
				
				if(sizeIn20 < iteration) {
					yelp20HashMap.get(currentUser).add(yelpFullFile.get(i));
				} else {
					yelp80HashMap.get(currentUser).add(yelpFullFile.get(i));
				}
			}
			else {
				count = 1;
				oldUser = currentUser;
				ArrayList<ArrayList<Integer>> addArrayList80 = new ArrayList<ArrayList<Integer>>();
				addArrayList80.add(yelpFullFile.get(i));
				yelp80HashMap.put(currentUser, addArrayList80);				// 1st always goes in 80
			}
		}
		
		int total80 = 0;
		int total20 = 0;
		for (int i = 1; i <= 11911; i++) {
			if(yelp80HashMap.get(i) == null && yelp80HashMap.get(i) == null) {
				System.out.println(i +"\t: user not found");
				System.exit(0);
			} else if( yelp20HashMap.get(i) == null ){
				System.out.println(i +"\t: 100.0\t\t0.0\t1");
				total80++;
				continue;
			}
			int total = yelp80HashMap.get(i).size() + yelp20HashMap.get(i).size();
			double ratio80 = Double.parseDouble(String.format("%.2f", (yelp80HashMap.get(i).size() * 100) / (total*1.0)));
			double ratio20 = Double.parseDouble(String.format("%.2f", (yelp20HashMap.get(i).size() * 100) / (total*1.0)));
			System.out.println(i +"\t: "+ratio80 + "\t\t" + ratio20 + "\t"+total);
			total80 += yelp80HashMap.get(i).size();
			total20 += yelp20HashMap.get(i).size();
		}
		double ratio80 = Double.parseDouble(String.format("%.2f", (total80 * 100) / ((total80+total20)*1.0)));
		double ratio20 = Double.parseDouble(String.format("%.2f", (total20 * 100) / ((total80+total20)*1.0)));		
		System.out.println(0 +"\t: "+ratio80 + "\t\t" + ratio20 + "\t"+(total80+total20));
		System.out.println(total80 + "+" + total20 +"="+ (total80+total20));
		
		writeFile(yelp80HashMap, yelp20HashMap);
	}

	private static void writeFile(HashMap<Integer, ArrayList<ArrayList<Integer>>> yelp80HashMap, HashMap<Integer, ArrayList<ArrayList<Integer>>> yelp20HashMap) {
		try{
			FileWriter writer = new FileWriter("Yelp/yelp80csv.base");
		    BufferedWriter buffer = new BufferedWriter(writer);
		    for (int i = 1; i <= yelp80HashMap.size(); i++) {
		    	for (int j = 0; j < yelp80HashMap.get(i).size(); j++) {
				    buffer.write(yelp80HashMap.get(i).get(j).get(0)+","+yelp80HashMap.get(i).get(j).get(1)+","+yelp80HashMap.get(i).get(j).get(2)+",DummyToMatch4Column");
				    if(!(i == yelp80HashMap.size() && j == (yelp80HashMap.get(i).size()-1)))
				    	buffer.newLine();
			    }
			}
		    buffer.close();
		    
		    FileWriter writer2 = new FileWriter("Yelp/yelp20csv.test");
		    BufferedWriter buffer2 = new BufferedWriter(writer2);
		    for (int i = 1; i <= yelp80HashMap.size(); i++) {
		    	if(yelp20HashMap.get(i) != null) {
			    	for (int j = 0; j < yelp20HashMap.get(i).size(); j++) {
					    buffer2.write(yelp20HashMap.get(i).get(j).get(0)+","+yelp20HashMap.get(i).get(j).get(1)+","+yelp20HashMap.get(i).get(j).get(2)+",DummyToMatch4Column");
					    if(!(i == yelp20HashMap.size() && j == (yelp20HashMap.get(i).size()-1)))
					    	buffer2.newLine();
				    }
		    	}
			}
		    buffer2.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

}
