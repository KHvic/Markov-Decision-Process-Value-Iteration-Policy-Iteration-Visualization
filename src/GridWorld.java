
import java.lang.Math;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * @author GOH KA HIAN NANYANG TECHNOLOGICAL UNIVERSITY
 *
 */
public class GridWorld extends JPanel implements Constant {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// X-AXIS
	private int cols;
	// Y-AXIS
	private int rows;

	protected JPanel[][] entireMap;
	protected State[][] states;

	// CONSTRUCTOR
	public GridWorld() {
		stateSizeChange();
	}

	public GridWorld(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;
		stateSizeChange();
	}

	// SET COLS AND ROWS
	public void setColsRowsSize(int xy) {
		this.rows = xy;
		this.cols = xy;
	}

	// INITIAL STATES OBJECT AND MAP
	protected void initiate() {
		this.removeAll();
		this.revalidate();
		this.repaint();
		this.setLayout(new GridLayout(cols, rows));
		for (int i = 0; i < cols; i++) {
			for (int j = 0; j < rows; j++) {
				states[i][j] = new State(this);
				states[i][j].setBorder(BorderFactory.createLineBorder(BORDER, 1));
				entireMap[i][j] = states[i][j];
			}
		}
		// DIFFERENT FORMAT FOR GUI (ROWS,COLS) INSTEAD OF (COLS,ROWS)
		for (int i = 0; i < cols; i++)
			for (int j = 0; j < rows; j++)
				this.add(states[j][i]);
	}

	// PERFORM UPDATE OF STATE SIZE AND MAP
	void modifyStateSize() {
		entireMap = new JPanel[cols][rows];
		states = new State[cols][rows];
		initiate();
		updateIsWall();
	}

	// THERE IS A CHANGE IN STATE SIZE, UPDATE
	void stateSizeChange() {
		modifyStateSize();
		if (!defaultConfig()) {
			randomizeConfig();
		}
	}

	// SET ALL STATES TO 'WHITE'
	void setEmpty() {
		for (int i = 0; i < cols; i++)
			for (int j = 0; j < rows; j++)
				states[i][j].setColor(WHITE);
	}

	// RANDOMIZE CONFIGURATION, CHANCES BASED ON DEFAULT CONFIGURATION
	// PERCENTAGE OF COLOR AND MATH.RANDOM
	void randomizeConfig() {
		double r;
		for (int i = 0; i < cols; i++) {
			for (int j = 0; j < rows; j++) {
				r = Math.random();
				if (r <= (PERCENT_WHITE))
					states[i][j].setColor(WHITE);
				else if (r <= (PERCENT_WHITE + PERCENT_GREEN))
					states[i][j].setColor(GREEN);
				else if (r <= (PERCENT_WHITE + PERCENT_GREEN + PERCENT_BROWN))
					states[i][j].setColor(BROWN);
				else
					states[i][j].setColor(WALL);
			}
		}
	}

	// RETURN TRUE IF THERE IS AN DEFAULT CONFIGURATION
	boolean defaultConfig() {

		// (COL,ROW)
		// THIS CONFIGURATION IS THE REQUIREMENTS FOR CZ4046 ASSIGNMENT 1
		if (cols == 6 && rows == 6) {
			// SET WHITE
			setEmpty();
			// SET WALL
			states[1][0].setColor(WALL);
			states[4][1].setColor(WALL);
			states[1][4].setColor(WALL);
			states[2][4].setColor(WALL);
			states[3][4].setColor(WALL);
			// SET BROWN
			states[1][1].setColor(BROWN);
			states[2][2].setColor(BROWN);
			states[3][3].setColor(BROWN);
			states[4][4].setColor(BROWN);
			states[5][1].setColor(BROWN);
			// SET GREEN
			states[0][0].setColor(GREEN);
			states[2][0].setColor(GREEN);
			states[5][0].setColor(GREEN);
			states[3][1].setColor(GREEN);
			states[4][2].setColor(GREEN);
			states[5][3].setColor(GREEN);

			return true;
		}
		return false;
	}

	// UPDATE STATES IF THEY HAVE NEIGHBORING WALL
	public void updateIsWall() {
		for (int i = 0; i < cols; i++) {
			for (int j = 0; j < rows; j++) {
				states[i][j].setNorthWall(false);
				states[i][j].setWestWall(false);
				states[i][j].setEastWall(false);
				states[i][j].setSouthWall(false);
				// CHECK IF STATE IS NEAR BORDER
				if (i == 0)
					states[i][j].setWestWall(true);
				if (i == cols - 1)
					states[i][j].setEastWall(true);
				if (j == 0)
					states[i][j].setNorthWall(true);
				if (j == rows - 1)
					states[i][j].setSouthWall(true);

				// CHECK IF STATE HAS WEST WALL
				if (i > 0)
					if (states[i - 1][j].isWall())
						states[i][j].setWestWall(true);
				// CHECK IF STATE HAS EAST WALL
				if (i < cols - 1)
					if (states[i + 1][j].isWall())
						states[i][j].setEastWall(true);
				// CHECK IF STATE HAS NORTH WALL
				if (j > 0)
					if (states[i][j - 1].isWall())
						states[i][j].setNorthWall(true);
				if (j < rows - 1)
					if (states[i][j + 1].isWall())
						states[i][j].setSouthWall(true);
			}
		}
	}

	// RETURN NUMBER OF NON-WALL STATES
	public int calcNumStates() {
		int a = 0;
		for (int i = 0; i < cols; i++) {
			for (int j = 0; j < rows; j++) {
				if (states[i][j].isWall())
					continue;
				a++;
			}
		}
		return a;
	}

	// DISPLAY OPTIMAL POLICY
	public void displayOptimalPolicy() {
		for (int i = 0; i < cols; i++) {
			for (int j = 0; j < rows; j++) {
				if (states[i][j].isWall())
					continue;
				else if (states[i][j].getBestAction() == UP)
					states[i][j].getText().setText("UP");
				else if (states[i][j].getBestAction() == DOWN)
					states[i][j].getText().setText("DOWN");
				else if (states[i][j].getBestAction() == RIGHT)
					states[i][j].getText().setText("RIGHT");
				else if (states[i][j].getBestAction() == LEFT)
					states[i][j].getText().setText("LEFT");
				
				Main.displayRewardButton.setEnabled(true);
				Main.displayUtilityButton.setEnabled(true);
				Main.displayPolicyButton.setEnabled(false);
			}
		}
	}

	// DISPLAY REWARD FOR STATES
	public void displayReward() {
		for (int i = 0; i < cols; i++) {
			for (int j = 0; j < rows; j++) {
				if (states[i][j].isWall())
					continue;
				states[i][j].getText().setText(Double.toString(states[i][j].getReward()));
				Main.displayRewardButton.setEnabled(false);
				Main.displayUtilityButton.setEnabled(true);
				Main.displayPolicyButton.setEnabled(true);
			}
		}
	}

	// DISPLAY UTILITY 4 DECIMAL PLACING
	public void displayUtility() {
		for (int i = 0; i < cols; i++) {
			for (int j = 0; j < rows; j++) {
				if (states[i][j].isWall())
					continue;
				states[i][j].getText().setText(

						Double.toString((double) Math.round(states[i][j].getUtility() * 10000) / 10000));

				Main.displayRewardButton.setEnabled(true);
				Main.displayUtilityButton.setEnabled(false);
				Main.displayPolicyButton.setEnabled(true);
			}
		}
	}

	// START OF GETTERS AND SETTERS
	public int getCols() {
		return cols;
	}

	public void setCols(int cols) {
		this.cols = cols;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

}
