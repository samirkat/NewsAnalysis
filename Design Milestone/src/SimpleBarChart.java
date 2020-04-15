import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.style.Styler.ChartTheme;
import org.knowm.xchart.style.Styler.LegendPosition;

public class SimpleBarChart implements Charts{
	
	HashMap<String, Double[]> readingLevelBySource;
	HashMap<String, Double[]> Zdata;
	double[] xData;
    double[] yData;
    
    
    public SimpleBarChart() {
    	readingLevelBySource = extractSourceReadingLevelData();	
    	Zdata = makeZs(readingLevelBySource);
    	
    }
    
    
    public HashMap<String, Double[]> extractSourceReadingLevelData(){
    	
    	HashMap<String, Double[]> readingLevelBySource = new HashMap<>();
    	
    	 for (Article article: Charts.articles) {
    		 	String source = article.getSource().trim();
    			double readingLevel = article.getReadingLevel();
    			double density = article.getLexicalDensity();

    			if (readingLevelBySource.containsKey(source)) {
    				Double[] levels = readingLevelBySource.get(source);
    				levels[0] += readingLevel;
    				levels[1] += density;
    				readingLevelBySource.replace(source, levels);
	
    				
    			}
    			
    			else {
    				Double[] levels = {readingLevel, density};
    				readingLevelBySource.put(source, levels);
    				
    			}		 
    		 
    	 }
    	 
    	 for (String source : readingLevelBySource.keySet()) {
    		Double[] levels =  readingLevelBySource.get(source);
    		levels[0] = levels[0] / 100;
			levels[1] = levels[1];
    		readingLevelBySource.replace(source, levels);
    		 
    	 }
 	
    	return readingLevelBySource;
    }
    
    
    public HashMap<String, Double[]> makeZs(HashMap<String, Double[]> Zdata){
    	
    	double readingAvg = 0;
    	double readingStDev = 0;
    	double densityAvg = 0;
    	double densityStDev = 0;
    	ArrayList<Double> reading = new ArrayList<>();
    	ArrayList<Double> density = new ArrayList<>();
    	
    	for (String source : Zdata.keySet()) {
    		Double[] levels =  Zdata.get(source);
    		readingAvg += levels[0];
			densityAvg += levels[1]; 
			reading.add(levels[0]);
			density.add(levels[1]);
    	 }
    
    	//compute the average for reading Level and density
    	readingAvg = readingAvg / 14;
    	densityAvg = densityAvg / 14;
    	
    	
    	//compute the standard deviation for reading Level and density
    	for (String source : Zdata.keySet()) {
    		Double[] levels =  Zdata.get(source);
    		Double readingN = levels[0];
 			Double densityN = levels[1];
    		readingN = Math.pow((double) readingN - readingAvg, 2);
            readingStDev += readingN;
            
            densityN = Math.pow((double) densityN - densityAvg, 2);
            densityStDev += densityN;
    	
    	 }
    	
    	for (String source : Zdata.keySet()) {
    		Double[] levels =  Zdata.get(source);
    		Double readingN = levels[0];
 			Double densityN = levels[1];
 			Double readingZ = (double) ((readingN - readingAvg) / readingStDev);
 			Double densityZ = (double) ((densityN - densityAvg) / densityStDev);
    		
 			levels[0] = readingZ;
 			levels[1] = densityZ;
 			
 			Zdata.replace(source, levels);
    		
    	}
    	
    	
    	
    	return Zdata;
    }
    
    
    
    
    
    
    public CategoryChart makeChart() {
    	 
    	 // Create Chart
        CategoryChart chart = new CategoryChartBuilder().width(2000).height(600).title("Reading Level and Lexical Density Z-Scores by Source").xAxisTitle("Source").yAxisTitle("Z-Score").theme(ChartTheme.GGPlot2).build();
     
     
        // Series
        
        String[] xseries = Zdata.keySet().toArray(new String[0]);
        ArrayList<Double> y1 = new ArrayList<>();
        ArrayList<Double> y2 = new ArrayList<>();
        
        for (String source : Zdata.keySet()) {
        	Double[] levels = Zdata.get(source);
            Double readingLevel = levels[0];
			Double density = levels[1];
			y1.add(readingLevel);
			y2.add(density);
        	
        }
        
        
        
        
        chart.addSeries("reading level",  new ArrayList<String>(Arrays.asList(xseries)), y1);
        chart.addSeries("lexical density",  new ArrayList<String>(Arrays.asList(xseries)), y2);
        
        
        new SwingWrapper<CategoryChart>(chart).displayChart();
        return chart;
      }
    
    
    public static void main(String[] args) {
		SimpleBarChart bm = new SimpleBarChart();
		bm.makeChart();

	}
    
    
}
