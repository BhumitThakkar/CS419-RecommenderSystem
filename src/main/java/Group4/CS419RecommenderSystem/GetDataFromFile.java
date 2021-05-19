package Group4.CS419RecommenderSystem;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class GetDataFromFile {
	
	static ArrayList<ArrayList<Integer>> readFile(String fileName, String splitter){
		ArrayList<ArrayList<Integer>> arrayListFromFile = new ArrayList<ArrayList<Integer>>();
		try
        {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = null;
            ArrayList<Integer> tempArrayList = null;
            if("Yelp/ratings.csv".equals(fileName)) {
            	bufferedReader.readLine();										// to skip first line from ratings file
            }
    		while((line = bufferedReader.readLine()) != null)
            {
                String[] values = line.split(splitter);
            	tempArrayList = new ArrayList<Integer>();            	
                for (int i = 0; i < values.length - 1; i++) {					// will not add the last column: i < values.length - 1
                	tempArrayList.add(Integer.parseInt(values[i]));
				}
                arrayListFromFile.add(tempArrayList);
            }
            bufferedReader.close();
        }
        catch(Exception e) {
        	e.printStackTrace();
            System.out.println("Error: " + e.getMessage());
        }
		return arrayListFromFile;
	}
	
}
