package Group4.CS419RecommenderSystem;

import java.io.BufferedReader;
import java.io.FileReader;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.stage.Stage;

public class MainApp extends Application {

	@SuppressWarnings("exports")
	@Override
    public void start(Stage s) {
    	displayAlgo1and2(s);
    	displayAlgo3();
    	displayAlgo4();
    	displayLossFunc();
    }

	private void displayAlgo1and2(Stage s) {
		s.setTitle("CS419: Informatics Project");
        
    	//defining the axes
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis(0.7, 1.4, 0.05);
        xAxis.setLabel("Neighbours");
        yAxis.setLabel("MAE");
        //creating the chart
        final LineChart<Number,Number> lineChart = new LineChart<Number,Number>(xAxis,yAxis);
        lineChart.setTitle("Algorithm 1 & 2 MAE Output");
        
        //defining a series
        Series<Number, Number> series = new XYChart.Series<Number, Number>();
        series.setName("Algorithm 1: User Based Neighborhood | MovieLens");
        //populating the series with data
        populateAlgo1and2or4(series, "u1.base", 1);										// 1 - algorithm 1
        //Add series to chart
        lineChart.getData().add(series);
        //Color Change
        Node line = series.getNode();
        StringBuilder style = new StringBuilder();
    	style.append("-fx-stroke: grey;");
    	line.setStyle(style.toString());
    	
        //defining a series
        Series<Number, Number> series2 = new XYChart.Series<Number, Number>();
        series2.setName("Algorithm 2: Item Based Neighborhood | MovieLens");
        //populating the series with data
        populateAlgo1and2or4(series2, "u1.base", 2);									// 2 - algorithm 2
        lineChart.getData().add(series2);
    	
        //defining a series
        Series<Number, Number> series3 = new XYChart.Series<Number, Number>();
        series3.setName("Algorithm 1: User Based Neighborhood | Yelp");
        //populating the series with data
        populateAlgo1and2or4(series3, "yelp80csv.base", 1);								// 1 - algorithm 1
        lineChart.getData().add(series3);
        
        //defining a series
        Series<Number, Number> series4 = new XYChart.Series<Number, Number>();
        series4.setName("Algorithm 2: Item Based Neighborhood | Yelp");
        //populating the series with data
        populateAlgo1and2or4(series4, "yelp80csv.base", 2);								// 2 - algorithm 2
        lineChart.getData().add(series4);

    	lineChart.setStyle("CHART_COLOR_1:#808080; CHART_COLOR_2:#FFA500; CHART_COLOR_3:#FF0000; CHART_COLOR_4:#0000FF;");
    	
        Scene scene  = new Scene(lineChart,800,600);
        s.setScene(scene);
        s.show();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
    private void displayAlgo3() {
    	Stage subStage = new Stage();
        subStage.setTitle("CS419: Informatics Project");
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart bc = new BarChart(xAxis, yAxis);
        bc.setTitle("Algorithm 3 MAE Output");
        
        XYChart.Series ds = new XYChart.Series();
        ds.setName("Algorithm 3: Statistical Baseline | MovieLens");
        populateAlgo3(ds, "u1.base", 3);
        bc.getData().add(ds);
        
        XYChart.Series ds2 = new XYChart.Series();
        ds2.setName("Algorithm 3: Statistical Baseline | Yelp");
        populateAlgo3(ds2, "yelp80csv.base", 3);
        bc.getData().add(ds2);
        
        Scene scene = new Scene(bc, 800, 600);
        subStage.setScene(scene);
        subStage.show();
    }
	
	private void displayAlgo4() {
    	Stage subStage = new Stage();
    	subStage.setTitle("CS419: Informatics Project");
    	
    	//defining the axes
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis(0.7, 1.4, 0.05);
        xAxis.setLabel("Epochs");
        yAxis.setLabel("MAE");
        //creating the chart
        final LineChart<Number,Number> lineChart = new LineChart<Number,Number>(xAxis,yAxis);
        lineChart.setTitle("Algorithm 4 MAE Output");
        
        //defining a series
        Series<Number, Number> series = new XYChart.Series<Number, Number>();
        series.setName("Algorithm 4: Gradient Decent Baseline Model | MovieLens");
        //populating the series with data
        populateAlgo1and2or4(series, "u1.base", 4);								// 4 - algorithm 4
        lineChart.getData().add(series);

        //defining a series
        Series<Number, Number> series2 = new XYChart.Series<Number, Number>();
        series2.setName("Algorithm 4: Gradient Decent Baseline Model | Yelp");
        //populating the series with data
        populateAlgo1and2or4(series2, "yelp80csv.base", 4);						// 4 - algorithm 4
        lineChart.getData().add(series2);
        
        Scene scene = new Scene(lineChart, 800, 600);
        subStage.setScene(scene);
        subStage.show();
	}
	
	private void displayLossFunc() {
		Stage subStage = new Stage();
    	subStage.setTitle("CS419: Informatics Project");
    	
    	//defining the axes
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Epochs");
        yAxis.setLabel("Loss Function");
        //creating the chart
        final LineChart<Number,Number> lineChart = new LineChart<Number,Number>(xAxis,yAxis);
        lineChart.setTitle("Algorithm 4 Loss Function Output");
        
        //defining a series
        Series<Number, Number> series = new XYChart.Series<Number, Number>();
        series.setName("Algorithm 4: Gradient Decent Baseline Model | MovieLens");
        //populating the series with data
        populateLossFunc(series, "movieLens_epochWithEpochsVsLoss", 4);										// 4 - algorithm 4
        lineChart.getData().add(series);

        //defining a series
        Series<Number, Number> series2 = new XYChart.Series<Number, Number>();
        series2.setName("Algorithm 4: Gradient Decent Baseline Model | Yelp");
        //populating the series with data
        populateLossFunc(series2, "yelp_epochWithEpochsVsLoss", 4);											// 4 - algorithm 4
        lineChart.getData().add(series2);
        
        Scene scene = new Scene(lineChart, 800, 600);
        subStage.setScene(scene);
        subStage.show();
	}

	private void populateLossFunc(Series<Number, Number> series, String dataSetName, int algo) {
		FileReader fileReader;
		try {
			fileReader = new FileReader("result/"+dataSetName+"_result"+algo+".txt");
			BufferedReader bufferedReader = new BufferedReader(fileReader);
	        int i = 1;
	        String line = bufferedReader.readLine();
	        String[] values = line.split("\t");
	        for(String value : values)
	        {
	        	series.getData().add(new XYChart.Data<Number, Number>(i++, Double.parseDouble(value)));
	        }
	        bufferedReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void populateAlgo1and2or4(Series<Number, Number> series, String trainingFileName, int algo) {
        FileReader fileReader;
		try {
			fileReader = new FileReader("result/"+trainingFileName+"_result"+algo+".txt");
			BufferedReader bufferedReader = new BufferedReader(fileReader);
	        String line = null;
	        while((line = bufferedReader.readLine()) != null)
	        {
	        	String[] values = line.split(",");
	        	series.getData().add(new XYChart.Data<Number, Number>(Integer.parseInt(values[0]), Double.parseDouble(values[1])));
	        }
	        bufferedReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	
	private void populateAlgo3(Series<String, Double> ds, String trainingFileName, int algo) {
        FileReader fileReader;
		try {
			fileReader = new FileReader("result/"+trainingFileName+"_result"+algo+".txt");
			BufferedReader bufferedReader = new BufferedReader(fileReader);
	        ds.getData().add(new XYChart.Data<String, Double>("Statistical Baseline", Double.parseDouble(bufferedReader.readLine())));
	        bufferedReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void main(String[] args) {
        launch(args);
    }

}
