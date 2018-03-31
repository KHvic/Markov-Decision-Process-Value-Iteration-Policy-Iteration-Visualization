
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * @author GOH KA HIAN NANYANG TECHNOLOGICAL UNIVERSITY
 *
 */
public class State extends JPanel implements Constant {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double reward;
	// 0 = MOVE UP, 1 = MOVE DOWN, 2 = MOVE LEFT, 3 = MOVE RIGHT
	private double utility;
	private int bestAction;

	// IF STATE HAS NEIGHBOURING WALL
	private boolean northWall;
	private boolean southWall;
	private boolean eastWall;
	private boolean westWall;

	// COLOR OF STATE
	private Color color;
	private JLabel text = new JLabel();

	// CONSTRUCTOR
	public State(GridWorld gw) {
		setColor(WHITE);
		this.setLayout(new GridBagLayout());
		text.setFont(new Font("serif", Font.BOLD, 14)); 
		this.add(text,SwingConstants.CENTER);
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (color == WALL)
					setColor(WHITE);
				else if (color == WHITE)
					setColor(GREEN);
				else if (color == GREEN)
					setColor(BROWN);
				else
					setColor(WALL);
				// CHANGES OCCURED, DISABLE SHOWING OF UTILITY AND POLICY BUTTON
				Main.disableUtilityAndPolicy();
			}
		});
	}

	// START OF GETTER AND SETTER
	public double getReward() {
		return reward;
	}

	public void setReward(double reward) {
		this.reward = reward;
	}

	public int getBestAction() {
		return bestAction;
	}

	public void setBestAction(int bestAction) {
		this.bestAction = bestAction;
	}

	public boolean isNorthWall() {
		return northWall;
	}

	public void setNorthWall(boolean northWall) {
		this.northWall = northWall;
	}

	public boolean isSouthWall() {
		return southWall;
	}

	public void setSouthWall(boolean southWall) {
		this.southWall = southWall;
	}

	public boolean isEastWall() {
		return eastWall;
	}

	public void setEastWall(boolean eastWall) {
		this.eastWall = eastWall;
	}

	public boolean isWestWall() {
		return westWall;
	}

	public void setWestWall(boolean westWall) {
		this.westWall = westWall;
	}

	public JLabel getText() {
		return text;
	}

	public void setText(JLabel text) {
		this.text = text;
	}

	public double getUtility() {
		return utility;
	}

	public void setUtility(double utility) {
		this.utility = utility;
	}

	public boolean isWall() {
		if (color == WALL)
			return true;
		else
			return false;
	}

	public Color getColor() {
		return this.color;
	}

	public void setColor(Color c) {
		this.color = c;
		this.setBackground(color);
		if (color == WALL)
			reward = WALL_REWARD;
		else if (color == GREEN)
			reward = GREEN_REWARD;
		else if (color == BROWN)
			reward = BROWN_REWARD;
		else if (color == WHITE)
			reward = WHITE_REWARD;
		if (color != WALL)
			text.setText(Double.toString(reward));
		else
			text.setText("WALL");
	}
}
