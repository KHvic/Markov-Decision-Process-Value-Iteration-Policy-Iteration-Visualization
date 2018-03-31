
import java.util.Arrays;
import java.awt.Dimension;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * @author GOH KA HIAN NANYANG TECHNOLOGICAL UNIVERSITY
 *
 */
public class PolicyIteration implements Constant {
	static DisplayGraph policyIterationGraph;
	private int iterationCount;
	private GridWorld gw;
	// AUXILLARY ARRAY
	private double oldUtility[][];
	private int previousPolicy[][];
	// k NUMBER OF TIMES BELLMAN UPDATE FOR MODIFIED POLICY ITERATION
	private static int k = 20;
	// mode=0 SOLVE LINEAR EQUATION FOR UTILITY , mode=1 MODIFIED POLICY
	// ITERATION
	private int mode = 0;
	// TERMINATING CONDITION, WHEN NO CHANGE IN POLICY's UTILITY
	private boolean unchanged = true;

	// CONSTRUTOR
	public PolicyIteration(GridWorld gw) {
		this.gw = gw;
	}

	// START POLICY ITERATION
	public void startIteration() {
		iterationCount = 0;
		gw.displayReward();
		// OBJECTS FOR GRAPH PLOTTING
		final XYSeriesCollection collection = new XYSeriesCollection();
		;
		final XYSeries series[][] = new XYSeries[gw.getCols()][gw.getRows()];

		// INITIAL UTILITY PLOTTING (0,0)
		for (int i = 0; i < gw.getCols(); i++)
			for (int j = 0; j < gw.getRows(); j++) {
				series[i][j] = new XYSeries("(" + i + "," + j + ")");
				series[i][j].add(0, 0);
			}

		// INITIALIZE ARRAY
		this.oldUtility = new double[gw.getCols()][gw.getRows()];
		this.previousPolicy = new int[gw.getCols()][gw.getRows()];
		// UPDATE HAS NEIGHBOURING WALL
		gw.updateIsWall();

		// START WITH INITIAL POLICY AND UTILITY
		setInitialPolicy();
		setInitialUtlity();

		// START OF LOOP UNTIL unchanged = true FOREACH ITERATION INDICATING NO
		// CHANGES/DIFFERENCES IN BEST ACTION's UTILITY
		do {
			// INCREMENT COUNT
			iterationCount++;

			// STORE PREVIOUS POLICY
			storeOldPolicy();
			// SET UNCHANGED
			unchanged = true;
			// mode == 0 THEN SOLVE FOR UTILITY USING GAUSSIAN ELIMINATION
			// ELSE APPLY MODIFILED POLICY ALGORITHM, PERFORM SIMPLIFIED BELLMAN
			// UPDATE FOR k TIMES
			if (getMode() == 0)
				policyEvaluation();
			else {
				storeOldUtility();
				modifiedPolicyEvaluation();
			}
			// POLICY IMPROVEMENT, SET BEST ACTION FOR EACH STATE s
			policyImprovement();

			// PLOT UTILITY GRAPH FOR EACH STATE
			for (int i = 0; i < gw.getCols(); i++)
				for (int j = 0; j < gw.getRows(); j++) {
					series[i][j].add(iterationCount, gw.states[i][j].getUtility());
					/* FOR DEBUG */
					// System.out.println("s("+i+","+j+") :" +
					// gw.states[i][j].getUtility());
					// System.out.println("s("+i+","+j+") :" +
					// gw.states[i][j].getBestAction()+";"+previousPolicy[i][j]);
				}
		} while (!unchanged);

		// DISPLAY THE OPTIMAL POLICY
		gw.displayOptimalPolicy();

		// COMBINE ALL GRAPH SERIES FOREACH STATES
		for (int i = 0; i < gw.getCols(); i++)
			for (int j = 0; j < gw.getRows(); j++) {
				if (gw.states[i][j].isWall())
					continue;
				else
					collection.addSeries(series[i][j]);
			}
		// UPDATE EAST PANEL ON 'STATISTIC'
		Main.numIteration.setText("Total Iteration Count: " + iterationCount);
		Main.numStates.setText("Numbers of states: " + collection.getSeriesCount());

		// FOR DISPLAYING GRAPH
		String title = "Policy Iteration -";
		if (mode == 0)
			title += " From solving linear system";
		else
			title += " Modified Policy Iteration " + "(k=" + getK() + ")";
		policyIterationGraph = new DisplayGraph(title, collection);
		policyIterationGraph.setSize(new Dimension(720, 720));
		policyIterationGraph.setLocationRelativeTo(null);
		policyIterationGraph.setVisible(true);
	}

	// INITIAL POLICY IS SET TO 'UP' ACTION
	public void setInitialPolicy() {
		for (int i = 0; i < gw.getCols(); i++)
			for (int j = 0; j < gw.getRows(); j++) {
				if (gw.states[i][j].isWall())
					continue;
				gw.states[i][j].setBestAction(UP);
			}
	}

	// SET INITIAL UTILITY TO 0 FOREACH STATE s
	public void setInitialUtlity() {
		for (int i = 0; i < gw.getCols(); i++)
			for (int j = 0; j < gw.getRows(); j++)
				// gw.states[i][j].setUtility(gw.states[i][j].getReward());
				gw.states[i][j].setUtility(0);
	}

	// UPDATE UTILITY BY SOLVING LINEAR EQUATIONS USING GAUSSIAN ELIMINATION
	public void policyEvaluation() {
		// TOTAL NUMBER OF GRID (INCLUDES WALL)
		int N = gw.getRows() * gw.getCols();
		// LEFT HAND VECTOR
		double[][] A = new double[N][N];
		// RIGHT HAND VECTOR
		double[] B = new double[N];

		// INITIALIZE ALL COEFFICIENT TO 0
		for (int i = 0; i < N; i++)
			for (int j = 0; j < N; j++)
				A[i][j] = 0;

		// SET COEFFICIENT FOR EACH STATE [state itself;row by col][neighbor]
		for (int i = 0; i < gw.getCols(); i++)
			for (int j = 0; j < gw.getRows(); j++) {
				// CAN SKIP IF WALL
				if (gw.states[i][j].isWall())
					continue;
				// FOREACH ACTION, ADD THE CORRECT CORRESPONDING COEFFICIENT
				// IF BEST ACTION IS UP
				if (gw.states[i][j].getBestAction() == UP) {
					if (gw.states[i][j].isNorthWall())
						A[i * gw.getCols() + j][i * gw.getCols() + j] += FRONT_CHANCE * DISCOUNT;
					else
						A[i * gw.getCols() + j][(i) * gw.getCols() + (j - 1)] += FRONT_CHANCE * DISCOUNT;

					if (gw.states[i][j].isWestWall())
						A[i * gw.getCols() + j][i * gw.getCols() + j] += LEFT_CHANCE * DISCOUNT;
					else
						A[i * gw.getCols() + j][(i - 1) * gw.getCols() + (j)] += LEFT_CHANCE * DISCOUNT;

					if (gw.states[i][j].isEastWall())
						A[i * gw.getCols() + j][i * gw.getCols() + j] += RIGHT_CHANCE * DISCOUNT;
					else
						A[i * gw.getCols() + j][(i + 1) * gw.getCols() + (j)] += RIGHT_CHANCE * DISCOUNT;
				}
				// IF BEST ACTION IS LEFT
				else if (gw.states[i][j].getBestAction() == LEFT) {
					if (gw.states[i][j].isNorthWall())
						A[i * gw.getCols() + j][i * gw.getCols() + j] += RIGHT_CHANCE * DISCOUNT;
					else
						A[i * gw.getCols() + j][i * gw.getCols() + (j - 1)] += RIGHT_CHANCE * DISCOUNT;

					if (gw.states[i][j].isWestWall())
						A[i * gw.getCols() + j][i * gw.getCols() + j] += FRONT_CHANCE * DISCOUNT;
					else
						A[i * gw.getCols() + j][(i - 1) * gw.getCols() + j] += FRONT_CHANCE * DISCOUNT;

					if (gw.states[i][j].isSouthWall())
						A[i * gw.getCols() + j][i * gw.getCols() + j] += LEFT_CHANCE * DISCOUNT;
					else
						A[i * gw.getCols() + j][i * gw.getCols() + (j + 1)] += LEFT_CHANCE * DISCOUNT;
				}
				// IF BEST ACTION IS RIGHT
				else if (gw.states[i][j].getBestAction() == RIGHT) {
					if (gw.states[i][j].isNorthWall())
						A[i * gw.getCols() + j][i * gw.getCols() + j] += LEFT_CHANCE * DISCOUNT;
					else
						A[i * gw.getCols() + j][i * gw.getCols() + (j - 1)] += LEFT_CHANCE * DISCOUNT;

					if (gw.states[i][j].isEastWall())
						A[i * gw.getCols() + j][i * gw.getCols() + j] += FRONT_CHANCE * DISCOUNT;
					else
						A[i * gw.getCols() + j][(i + 1) * gw.getCols() + j] += FRONT_CHANCE * DISCOUNT;

					if (gw.states[i][j].isSouthWall())
						A[i * gw.getCols() + j][i * gw.getCols() + j] += RIGHT_CHANCE * DISCOUNT;
					else
						A[i * gw.getCols() + j][i * gw.getCols() + (j + 1)] += RIGHT_CHANCE * DISCOUNT;
				}
				// IF BEST ACTION IS DOWN
				else if (gw.states[i][j].getBestAction() == DOWN) {
					if (gw.states[i][j].isSouthWall())
						A[i * gw.getCols() + j][i * gw.getCols() + j] += FRONT_CHANCE * DISCOUNT;
					else
						A[i * gw.getCols() + j][i * gw.getCols() + (j + 1)] += FRONT_CHANCE * DISCOUNT;

					if (gw.states[i][j].isWestWall())
						A[i * gw.getCols() + j][i * gw.getCols() + j] += RIGHT_CHANCE * DISCOUNT;
					else
						A[i * gw.getCols() + j][(i - 1) * gw.getCols() + j] += RIGHT_CHANCE * DISCOUNT;

					if (gw.states[i][j].isEastWall())
						A[i * gw.getCols() + j][i * gw.getCols() + j] += LEFT_CHANCE * DISCOUNT;
					else
						A[i * gw.getCols() + j][(i + 1) * gw.getCols() + j] += LEFT_CHANCE * DISCOUNT;
				}
				// ADD THE STATE ITSELF TO LHS VECTOR
				A[i * gw.getCols() + j][i * gw.getCols() + j] -= 1;
				// ADD REWARD OF STATE TO RHS VECTOR
				B[i * gw.getCols() + j] = -gw.states[i][j].getReward();
			}

		// FOR HANDLING TRUNCATION OF WALL STATES IN GAUSSIAN ELIMINATION, COPY
		// ARRAY WITHOUT WALL STATES
		// AS GAUSSIAN ELIMINATION REQUIRES A SINGULAR MATRIX, WALL STATES HAVE
		// TO BE TRUCATED
		int nonWallStates = gw.calcNumStates();
		double[][] A_duplicate = new double[nonWallStates][nonWallStates];
		double[] B_duplicate = new double[nonWallStates];

		// DUPLICATE RHS VECTOR 'B' WITHOUT WALL
		int a = 0;
		for (int i = 0; i < gw.getCols(); i++)
			for (int j = 0; j < gw.getRows(); j++) {
				if (gw.states[i][j].isWall())
					continue;
				B_duplicate[a] = B[i * gw.getCols() + j];
				a++;
			}
		// DUPLICATE LHS VECTOR 'A' WITHOUT WALL
		int a2 = 0;
		for (int l = 0; l < gw.getCols(); l++)
			for (int k = 0; k < gw.getRows(); k++) {
				if (gw.states[l][k].isWall())
					continue;
				a = 0;
				for (int i = 0; i < gw.getCols(); i++)
					for (int j = 0; j < gw.getRows(); j++) {
						if (gw.states[i][j].isWall())
							continue;
						A_duplicate[a2][a] = A[l * gw.getCols() + k][i * gw.getCols() + j];
						a++;
					}
				a2++;
			}

		// PERFORM GAUSSIAN ELIMINATION
		double[] x = GaussianElimination.lsolve(A_duplicate, B_duplicate);
		a = 0;
		// STORE UTILITY BACK WITH THE RESULTS
		for (int i = 0; i < gw.getCols(); i++)
			for (int j = 0; j < gw.getRows(); j++) {
				if (gw.states[i][j].isWall())
					continue;
				gw.states[i][j].setUtility(x[a]);
				a++;
			}
	}

	// MODIFIED POLICY ITERATION, PERFORM BELLMAN UPDATE U_i+1(s) = R(S) +
	// DISCOUNT*EU
	// FOR k TIMES
	public void modifiedPolicyEvaluation() {
		double utility = 0;
		// STORE OLD UTILITY
		for (int a = 0; a < getK(); a++) {
			storeOldUtility();
			// FOREACH STATE CALCULATE THE UTILITY WITH POLICY
			for (int i = 0; i < gw.getCols(); i++)
				for (int j = 0; j < gw.getRows(); j++) {
					// update using up action
					if (gw.states[i][j].getBestAction() == UP)
						utility = gw.states[i][j].getReward() + DISCOUNT * (LEFT_CHANCE * getLeft(i, j)
								+ RIGHT_CHANCE * getRight(i, j) + FRONT_CHANCE * getUp(i, j));
					else if (gw.states[i][j].getBestAction() == DOWN)
						utility = gw.states[i][j].getReward() + DISCOUNT * (LEFT_CHANCE * getRight(i, j)
								+ RIGHT_CHANCE * getLeft(i, j) + FRONT_CHANCE * getDown(i, j));
					else if (gw.states[i][j].getBestAction() == RIGHT)
						utility = gw.states[i][j].getReward() + DISCOUNT * (LEFT_CHANCE * getUp(i, j)
								+ RIGHT_CHANCE * getDown(i, j) + FRONT_CHANCE * getRight(i, j));
					else if (gw.states[i][j].getBestAction() == LEFT)
						utility = gw.states[i][j].getReward() + DISCOUNT * (LEFT_CHANCE * getDown(i, j)
								+ RIGHT_CHANCE * getUp(i, j) + FRONT_CHANCE * getLeft(i, j));

					// UPDATE UTILITY
					gw.states[i][j].setUtility(utility);
					// System.out.println("s("+i+","+j+") :" +
					// gw.states[i][j].getUtility());
				}
		}
	}

	// POLICY IMPROVEMENT, SET BEST ACTION-HIGHEST EXPECTED UTILITY FOREACH
	// STATE s
	// Policy(s) = arg(MAX(expected utility of possible action))
	public void policyImprovement() {
		double actionUtility[] = new double[4];
		// FOREACH STATE UPDATE
		for (int i = 0; i < gw.getCols(); i++)
			for (int j = 0; j < gw.getRows(); j++) {
				// IGNORE WALL
				if (gw.states[i][j].isWall())
					continue;
				// RESET AUXILLIARY ARRAY
				Arrays.fill(actionUtility, 0);

				// CALCULATE UTILITY FOREACH ACTION
				// if north has wall
				if (gw.states[i][j].isNorthWall()) {
					actionUtility[UP] += FRONT_CHANCE * gw.states[i][j].getUtility();
					actionUtility[LEFT] += RIGHT_CHANCE * gw.states[i][j].getUtility();
					actionUtility[RIGHT] += LEFT_CHANCE * gw.states[i][j].getUtility();
				} else {// no wall on north
					actionUtility[UP] += FRONT_CHANCE * gw.states[i][j - 1].getUtility();
					actionUtility[LEFT] += RIGHT_CHANCE * gw.states[i][j - 1].getUtility();
					actionUtility[RIGHT] += LEFT_CHANCE * gw.states[i][j - 1].getUtility();
				}
				// if south has wall
				if (gw.states[i][j].isSouthWall()) {
					actionUtility[DOWN] += FRONT_CHANCE * gw.states[i][j].getUtility();
					actionUtility[LEFT] += LEFT_CHANCE * gw.states[i][j].getUtility();
					actionUtility[RIGHT] += RIGHT_CHANCE * gw.states[i][j].getUtility();
				} else {// no wall on south
					actionUtility[DOWN] += FRONT_CHANCE * gw.states[i][j + 1].getUtility();
					actionUtility[LEFT] += LEFT_CHANCE * gw.states[i][j + 1].getUtility();
					actionUtility[RIGHT] += RIGHT_CHANCE * gw.states[i][j + 1].getUtility();
				}
				// if west has wall
				if (gw.states[i][j].isWestWall()) {
					actionUtility[DOWN] += RIGHT_CHANCE * gw.states[i][j].getUtility();
					actionUtility[LEFT] += FRONT_CHANCE * gw.states[i][j].getUtility();
					actionUtility[UP] += LEFT_CHANCE * gw.states[i][j].getUtility();
				} else {// no wall on west
					actionUtility[DOWN] += RIGHT_CHANCE * gw.states[i - 1][j].getUtility();
					actionUtility[LEFT] += FRONT_CHANCE * gw.states[i - 1][j].getUtility();
					actionUtility[UP] += LEFT_CHANCE * gw.states[i - 1][j].getUtility();
				}
				// if east has wall
				if (gw.states[i][j].isEastWall()) {
					actionUtility[DOWN] += LEFT_CHANCE * gw.states[i][j].getUtility();
					actionUtility[RIGHT] += FRONT_CHANCE * gw.states[i][j].getUtility();
					actionUtility[UP] += RIGHT_CHANCE * gw.states[i][j].getUtility();
				} else {// no wall on east
					actionUtility[DOWN] += LEFT_CHANCE * gw.states[i + 1][j].getUtility();
					actionUtility[RIGHT] += FRONT_CHANCE * gw.states[i + 1][j].getUtility();
					actionUtility[UP] += RIGHT_CHANCE * gw.states[i + 1][j].getUtility();
				}

				// SET BEST ACTION TO BE THE ONE WITH HIGHEST UTILITY
				// IF THERE IS A TIE BETWEEN ACTION, ORDER CHOOSEN WILL BE:
				// UP->RIGHT->LEFT->DOWN
				gw.states[i][j].setBestAction(DOWN);
				if (actionUtility[LEFT] > actionUtility[gw.states[i][j].getBestAction()])
					gw.states[i][j].setBestAction(LEFT);
				if (actionUtility[RIGHT] > actionUtility[gw.states[i][j].getBestAction()])
					gw.states[i][j].setBestAction(RIGHT);
				if (actionUtility[UP] > actionUtility[gw.states[i][j].getBestAction()])
					gw.states[i][j].setBestAction(UP);

				// IF UTILITY OF BEST ACTION GREATER THAN PREVIOUS BEST ACTION
				// UTILITY, unchange = false, do not update best action
				if (actionUtility[gw.states[i][j].getBestAction()] > actionUtility[previousPolicy[i][j]])
					unchanged = false;
				else
					gw.states[i][j].setBestAction(previousPolicy[i][j]);
			}
	}

	// STORE OLD POLICY
	public void storeOldPolicy() {
		for (int i = 0; i < gw.getCols(); i++)
			for (int j = 0; j < gw.getRows(); j++) {
				// IGNORE WALL
				if (gw.states[i][j].isWall())
					continue;
				previousPolicy[i][j] = gw.states[i][j].getBestAction();
			}
	}

	// STORE OLD UTILITY
	public void storeOldUtility() {
		for (int i = 0; i < gw.getCols(); i++)
			for (int j = 0; j < gw.getRows(); j++) {
				// IGNORE WALL
				if (gw.states[i][j].isWall())
					continue;
				oldUtility[i][j] = gw.states[i][j].getUtility();
			}
	}

	// GET UTILITY OF MOVING UP
	public double getUp(int col, int row) {
		return (row > 0 && !gw.states[col][row - 1].isWall()) ? oldUtility[col][row - 1] : oldUtility[col][row];
	}

	// GET UTILITY OF MOVING DOWN
	public double getDown(int col, int row) {
		return (row < gw.getRows() - 1 && !gw.states[col][row + 1].isWall()) ? oldUtility[col][row + 1]
				: oldUtility[col][row];
	}

	// GET UTILITY OF MOVING LEFT
	public double getLeft(int col, int row) {
		return (col > 0 && !gw.states[col - 1][row].isWall()) ? oldUtility[col - 1][row] : oldUtility[col][row];
	}

	// GET UTILITY OF MOVING RIGHT
	public double getRight(int col, int row) {
		return (col < gw.getCols() - 1 && !gw.states[col + 1][row].isWall()) ? oldUtility[col + 1][row]
				: oldUtility[col][row];
	}

	/* START OF GETTER AND SETTER */
	public static int getK() {
		return k;
	}

	public static void setK(int k) {
		PolicyIteration.k = k;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}
}
