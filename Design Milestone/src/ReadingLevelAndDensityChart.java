
/**
 * This class
 * - creates a chart that displays the z-scores for the average reading level and lexical density by source
 * - creates a chart displaying reading levels by source
 * - creates a chart displaying lexical density by source
 */
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.VectorGraphicsEncoder;
import org.knowm.xchart.VectorGraphicsEncoder.VectorGraphicsFormat;
import org.knowm.xchart.style.Styler.ChartTheme;
import org.knowm.xchart.style.Styler.LegendPosition;

public class ReadingLevelAndDensityChart implements Charts {

	// Instance Variables
	HashMap<String, Double[]> LevelsBySource;
	HashMap<String, Double[]> LevelsBySourceCOPY;
	HashMap<String, Double[]> Zdata;

	// Constructor
	public ReadingLevelAndDensityChart() {
		LevelsBySource = extractSourceLevelData();
		LevelsBySourceCOPY = extractSourceLevelData();
		Zdata = makeZs(LevelsBySourceCOPY);

	}

	/**
	 * Creates a HashMap with - the average reading level for each source - the
	 * average lexical densities for each source
	 * @return readingLevelBySource
	 */
	public HashMap<String, Double[]> extractSourceLevelData() {

		HashMap<String, Double[]> readingAndZBySource = new HashMap<>();

		// populating the hashmap
		for (Article article : Charts.articles) {
			String source = article.getSource().trim();
			double readingLevel = article.getReadingLevel();
			double density = article.getLexicalDensity();

			if (readingAndZBySource.containsKey(source)) {
				Double[] levels = readingAndZBySource.get(source);
				levels[0] += readingLevel;
				levels[1] += density;
				readingAndZBySource.replace(source, levels);

			}

			else {
				Double[] levels = { readingLevel, density };
				readingAndZBySource.put(source, levels);

			}

		}

		// converting sums to averages
		for (String source : readingAndZBySource.keySet()) {
			Double[] levels = readingAndZBySource.get(source);
			levels[0] = levels[0] / 100;
			levels[1] = levels[1] / 100;
			readingAndZBySource.replace(source, levels);

		}

		return readingAndZBySource;
	}

	/**
	 * converts averages (on a source level) into z-scores (on a corpus level) for
	 * reading level and lexical density for each source.
	 * 
	 * @param Zdata
	 * @return Zdata
	 */
	public HashMap<String, Double[]> makeZs(HashMap<String, Double[]> Zdata) {

		double readingAvg = 0;
		double readingStDev = 0;
		double densityAvg = 0;
		double densityStDev = 0;
		ArrayList<Double> reading = new ArrayList<>();
		ArrayList<Double> density = new ArrayList<>();

		// populating
		for (String source : Zdata.keySet()) {
			Double[] levels = Zdata.get(source);
			readingAvg += levels[0];
			densityAvg += levels[1];
			reading.add(levels[0]);
			density.add(levels[1]);
		}

		// compute the average for reading Level and density
		readingAvg = readingAvg / 14;
		densityAvg = densityAvg / 14;

		// compute the standard deviation for reading Level and density
		for (String source : Zdata.keySet()) {
			Double[] levels = Zdata.get(source);
			Double readingN = levels[0];
			Double densityN = levels[1];
			readingN = Math.pow((double) readingN - readingAvg, 2);
			readingStDev += readingN;

			densityN = Math.pow((double) densityN - densityAvg, 2);
			densityStDev += densityN;

		}

		// compute the z-score for each source
		for (String source : Zdata.keySet()) {
			Double[] levels = Zdata.get(source);
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

	/**
	 * creates the Z-scores graphxz
	 * 
	 * @return
	 */
	public CategoryChart makeZChart(String zType) {

		// Create Chart
		CategoryChart chart = new CategoryChartBuilder().width(2000).height(600)
				.title( zType + " Z-Scores by Source").xAxisTitle("Source")
				.yAxisTitle("Z-Score").theme(ChartTheme.GGPlot2).build();

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

		if(zType.contains("Density")) {
			chart.addSeries("lexical density", new ArrayList<String>(Arrays.asList(xseries)), y2);	
		}
		
		else {
			chart.addSeries("reading level", new ArrayList<String>(Arrays.asList(xseries)), y1);	
		}
		
		

		return chart;
	}

	public CategoryChart makeAvgsChart(String avgType) {
		// Create Chart
		CategoryChart chart = new CategoryChartBuilder().width(2000).height(600).title(avgType + " by Source")
				.xAxisTitle("Source").yAxisTitle("Level").theme(ChartTheme.GGPlot2).build();

		// Series
		String[] xseries = LevelsBySource.keySet().toArray(new String[0]);
		ArrayList<Double> y1 = new ArrayList<>();
		ArrayList<Double> y2 = new ArrayList<>();

		for (String source : LevelsBySource.keySet()) {
			Double[] levels = LevelsBySource.get(source);
			Double readingLevel = levels[0];
			Double density = levels[1];
			y1.add(readingLevel);
			y2.add(density);

		}

		if (avgType.contains("Density")) {
			chart.addSeries("lexical density", new ArrayList<String>(Arrays.asList(xseries)), y2);
		}

		else {
			chart.addSeries("reading level", new ArrayList<String>(Arrays.asList(xseries)), y1);

		}

		return chart;

	}

	public static void main(String[] args) {


	}

}
