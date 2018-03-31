
import java.util.Arrays;
import java.awt.Dimension;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * @author GOH KA HIAN NANYANG TECHNOLOGICAL UNIVERSITY
 *
 */
public class ValueIteration implements Constant {
	static DisplayGraph valueIterationGraph;
	private int iterationCount;
	private GridWorld gw;
	// KEEP TRACK OF OLD UTILITY
	private double oldUtility[][];

	// MAXIMUM CHANGE IN UTILITY
	private double maximumChange;
	// MAXIMUM ERROR ALLOWED FOR ALGORITHM, DEFAULT 0.1
	static double maximumErrorAllowed = 0.1;

	// CONSTRUCTOR
	public ValueIteration(GridWorld gw) {
		this.gw = gw;
	}

	// START VALUE ITERATION
	public void startIteration() {
		iterationCount = 0;

		// INITIALIZE AUXILLARY ARRAY
		this.oldUtility = new double[gw.getRows()][gw.getCols()];
		// UPDATE STATES HAVING NEIGHBOR WALLS
		gw.updateIsWall();

		// FOR GRAPH PLOTTING
		final XYSeriesCollection collection = new XYSeriesCollection();
		;
		final XYSeries series[][] = new XYSeries[gw.getCols()][gw.getRows()];
		// INSTANTIATE SERIES AND INITIAL UTILITY PLOTTING (0,0)
		for (int i = 0; i < gw.getCols(); i++)
			for (int j = 0; j < gw.getRows(); j++) {
				series[i][j] = new XYSeries("(" + i + "," + j + ")");
				series[i][j].add(0, 0);
			}

		// INITIAL STEP: SET U(S) = 0
		setUtlityZero();

		// ITERATE UNTIL CONVERGENCE BASED ON maximumErrorAllowed
		do {
			iterationCount++;
			// MAXIMUM CHANGES IN UTILITY
			maximumChange = 0;
			// KEEP TRACK OLD UTILITY
			storeOldUtility();
			// UPDATE NEW UTILITY VALUE
			updateUtility();

			// PLOT GRAPH AND CALCULATE MAXIMUM CHANGES IN UTILITY
			double differences = 0;
			for (int i = 0; i < gw.getCols(); i++)
				for (int j = 0; j < gw.getRows(); j++) {
					series[i][j].add(iterationCount, gw.states[i][j].getUtility());
					differences = Math.abs(gw.states[i][j].getUtility() - oldUtility[i][j]);
					if (differences > maximumChange)
						maximumChange = differences;
				}
		} while ((maximumChange) >= (maximumErrorAllowed * (1.0 - DISCOUNT) / DISCOUNT) && !(maximumChange == 0));

		// DISPLAY OPTIMAL POLICY
		gw.displayOptimalPolicy();

		// COMBINE ALL XYSERIES
		for (int i = 0; i < gw.getCols(); i++)
			for (int j = 0; j < gw.getRows(); j++) {
				if (gw.states[i][j].isWall())
					continue;
				else
					collection.addSeries(series[i][j]);
			}
		/* DISPLAY GRAPH AND STATISTIC */
		Main.numIteration.setText("Total Iteration Count: " + iterationCount);
		Main.numStates.setText("Numbers of states: " + collection.getSeriesCount());
		valueIterationGraph = new DisplayGraph("Value Iteration (Max error: " + maximumErrorAllowed + ")", collection);
		valueIterationGraph.setSize(new Dimension(720, 720));
		valueIterationGraph.setLocationRelativeTo(null);
		valueIterationGraph.setVisible(true);
	}

	// SET UTILITY OF ALL STATES TO 0
	public void setUtlityZero() {
		for (int i = 0; i < gw.getCols(); i++)
			for (int j = 0; j < gw.getRows(); j++)
				gw.states[i][j].setUtility(0);
	}

	// STORE OLD UTILITY VALUES
	public void storeOldUtility() {
		for (int i = 0; i < gw.getCols(); i++)
			for (int j = 0; j < gw.getRows(); j++) {
				// IGNORE WALL
				if (gw.states[i][j].isWall())
					continue;
				oldUtility[i][j] = gw.states[i][j].getUtility();
			}
	}

	// U(s) = R(s) + discount*MAX(expected utility of an action)
	public void updateUtility() {
		double actionUtility[] = new double[4];
		// FOREACH STATE UPDATE THE UTILITY
		for (int i = 0; i < gw.getCols(); i++)
			for (int j = 0; j < gw.getRows(); j++) {
				// IGNORE UPDATING WALL
				if (gw.states[i][j].isWall())
					continue;
				// RESET AUXILIARY ARRAY
				Arrays.fill(actionUtility, 0);

				// if north of state has wall
				if (gw.states[i][j].isNorthWall()) {
					actionUtility[UP] += FRONT_CHANCE * oldUtility[i][j];
					actionUtility[LEFT] += RIGHT_CHANCE * oldUtility[i][j];
					actionUtility[RIGHT] += LEFT_CHANCE * oldUtility[i][j];
				} else {// no wall on north
					actionUtility[UP] += FRONT_CHANCE * oldUtility[i][j - 1];
					actionUtility[LEFT] += RIGHT_CHANCE * oldUtility[i][j - 1];
					actionUtility[RIGHT] += LEFT_CHANCE * oldUtility[i][j - 1];
				}
				// if south has wall
				if (gw.states[i][j].isSouthWall()) {
					actionUtility[DOWN] += FRONT_CHANCE * oldUtility[i][j];
					actionUtility[LEFT] += LEFT_CHANCE * oldUtility[i][j];
					actionUtility[RIGHT] += RIGHT_CHANCE * oldUtility[i][j];
				} else {// no wall on south
					actionUtility[DOWN] += FRONT_CHANCE * oldUtility[i][j + 1];
					actionUtility[LEFT] += LEFT_CHANCE * oldUtility[i][j + 1];
					actionUtility[RIGHT] += RIGHT_CHANCE * oldUtility[i][j + 1];
				}
				// if west has wall
				if (gw.states[i][j].isWestWall()) {
					actionUtility[DOWN] += RIGHT_CHANCE * oldUtility[i][j];
					actionUtility[LEFT] += FRONT_CHANCE * oldUtility[i][j];
					actionUtility[UP] += LEFT_CHANCE * oldUtility[i][j];
				} else {// no wall on west
					actionUtility[DOWN] += RIGHT_CHANCE * oldUtility[i - 1][j];
					actionUtility[LEFT] += FRONT_CHANCE * oldUtility[i - 1][j];
					actionUtility[UP] += LEFT_CHANCE * oldUtility[i - 1][j];
				}
				// if east has wall
				if (gw.states[i][j].isEastWall()) {
					actionUtility[DOWN] += LEFT_CHANCE * oldUtility[i][j];
					actionUtility[RIGHT] += FRONT_CHANCE * oldUtility[i][j];
					actionUtility[UP] += RIGHT_CHANCE * oldUtility[i][j];
				} else {// no wall on east
					actionUtility[DOWN] += LEFT_CHANCE * oldUtility[i + 1][j];
					actionUtility[RIGHT] += FRONT_CHANCE * oldUtility[i + 1][j];
					actionUtility[UP] += RIGHT_CHANCE * oldUtility[i + 1][j];
				}

				// SET THE ACTION WITH HIGHEST EXPECTED UTILITY
				gw.states[i][j].setBestAction(DOWN);
				if (actionUtility[UP] > actionUtility[gw.states[i][j].getBestAction()])
					gw.states[i][j].setBestAction(UP);
				if (actionUtility[LEFT] > actionUtility[gw.states[i][j].getBestAction()])
					gw.states[i][j].setBestAction(LEFT);
				if (actionUtility[RIGHT] > actionUtility[gw.states[i][j].getBestAction()])
					gw.states[i][j].setBestAction(RIGHT);

				// UPDATE UTILITY BASED ON BELLMAN EQUATION
				gw.states[i][j].setUtility(
						gw.states[i][j].getReward() + DISCOUNT * actionUtility[gw.states[i][j].getBestAction()]);
				// System.out.println("s("+i+","+j+") :" +
				// gw.states[i][j].getUtility());
			}
	}

	// //NOT IN USE
	// public boolean convergence(){
	// for (int i = 0; i < gw.getRows(); i++)
	// for (int j = 0; j < gw.getCols(); j++)
	// if(oldUtility[i][j] != gw.states[i][j].getUtility())
	// return false;
	// System.out.println("num of iteration :"+ iterationCount);
	// return true;
	// }

}
