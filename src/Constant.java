import java.awt.Color;
/**
 * @author GOH KA HIAN NANYANG TECHNOLOGICAL UNIVERSITY
 *
 */
public interface Constant {

	// COLORS OF CELLS
	static final Color WALL = Color.GRAY;
	static final Color WHITE = Color.WHITE;
	static final Color BROWN = new Color(255, 140, 0);
	static final Color GREEN = Color.GREEN;

	// REWARD OF COLORS
	static final double WHITE_REWARD = -0.04;
	static final double GREEN_REWARD = +1.0;
	static final double BROWN_REWARD = -1.0;
	static final double WALL_REWARD = 0.0;

	static final Color BORDER = new Color(0, 0, 0);

	// PERCENTAGE OF COLOR FOR DEFAULT CONFIGURATION
	static final double PERCENT_WHITE = 20.0 / 36;
	static final double PERCENT_WALL = 5.0 / 36;
	static final double PERCENT_BROWN = 5.0 / 36;
	static final double PERCENT_GREEN = 6.0 / 36;

	// DISCOUNT
	static final double DISCOUNT = 0.99;

	// ACTIONS
	static final int UP = 0;
	static final int DOWN = 1;
	static final int LEFT = 2;
	static final int RIGHT = 3;

	// NON-DETERMINISTIC CHANCE OF ACTION
	static final double RIGHT_CHANCE = 0.1;
	static final double LEFT_CHANCE = 0.1;
	static final double FRONT_CHANCE = 0.8;
}
