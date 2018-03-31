import java.awt.Font;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
/**
 * @author GOH KA HIAN NANYANG TECHNOLOGICAL UNIVERSITY
 *
 */
public class DisplayGraph extends ApplicationFrame {


	private static final long serialVersionUID = 1L;

	// CONSTRUCTOR
	public DisplayGraph(String title, XYDataset data) {
		super(title);
		final JFreeChart chart = createCombinedChart(data, title);
		final ChartPanel panel = new ChartPanel(chart, true, true, true, false, true);
		panel.setPreferredSize(new java.awt.Dimension(500, 270));
		setContentPane(panel);

	}

	// CREATE COMBINED CHART WITH DATA
	JFreeChart createCombinedChart(XYDataset data, String title) {

		// CREATE SUBPLOT 1
		final XYDataset data1 = data;
		final XYItemRenderer renderer1 = new StandardXYItemRenderer();
		final NumberAxis rangeAxis1 = new NumberAxis("Utility Estimates");
		final XYPlot subplot1 = new XYPlot(data1, null, rangeAxis1, renderer1);
		subplot1.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);

		final XYTextAnnotation annotation = new XYTextAnnotation("Hello!", 50.0, 10000.0);
		annotation.setFont(new Font("SansSerif", Font.PLAIN, 9));
		subplot1.addAnnotation(annotation);

		// PARENT PLOT
		final CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new NumberAxis("Number of iterations"));
		plot.setGap(10.0);

		// ADD THE SUBPLOTS
		plot.add(subplot1, 1);
		plot.setOrientation(PlotOrientation.VERTICAL);

		// RETURN A NEW CHART CONTAINING THE OVERLAID PLOT
		return new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, true);

	}
}
