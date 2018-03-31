
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author GOH KA HIAN NANYANG TECHNOLOGICAL UNIVERSITY
 *
 */
final public class Main {
	// GRIDWORLD AND ALGORITHM
	static GridWorld gw;
	static ValueIteration valueIterate;
	static PolicyIteration policyIterate;
	static boolean policyRunning = false;
	static boolean valueRunning = false;
	// JAVA SWING COMPONENTS
	static JFrame f;
	static Container contentPane;
	static JPanel panelNorth;
	static JPanel panelSouth;
	static JPanel panelEast;
	static JButton displayRewardButton;
	static JButton displayUtilityButton;
	static JButton displayPolicyButton;
	static JButton valueIterationButton;
	static JButton policyIterationButton;
	static JButton defaultConfiguration;
	static JButton randomConfiguration;
	static JLabel numStates;
	static JLabel numIteration;
	static JLabel method;
	static JLabel discount;
	static DecimalFormat df = new DecimalFormat("0.##");
	static JTextField errorAllowed;
	static JTextField numK;

	// MAIN
	public static void main(String[] args) {

		gw = new GridWorld(6, 6);
		valueIterate = new ValueIteration(gw);
		policyIterate = new PolicyIteration(gw);
		System.out.println("RUNNING CZ4046 ASSIGNMENT 1 , AUTHOR: GOH KA HIAN");
		initializeGui();
		
	//	uncomment for 100x100 GridWorld
	//	gw.setColsRowsSize(100);
	//	gw.stateSizeChange();
	}

	public static void initializeGui() {
		// SET UP THE BACKGROUND
		f = new JFrame();
		f.setTitle("CZ4046 Assignment 1 - GOH KA HIAN");
		f.setSize(new Dimension(720, 720));

		contentPane = f.getContentPane();
		contentPane.add(gw, BorderLayout.CENTER);

		// START OF JPANEL
		panelNorth = new JPanel();
		panelSouth = new JPanel();
		panelEast = new JPanel();
		panelEast.setLayout(new BoxLayout(panelEast, BoxLayout.Y_AXIS));
		// EAST PANEL
		// DISPLAY REWARD
		displayRewardButton = new JButton("Display Reward");
		displayRewardButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gw.displayReward();
			}
		});
		// DISPLAY UTILITY
		displayUtilityButton = new JButton("Display Utility");
		displayUtilityButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gw.displayUtility();
			}
		});
		// DISPLAY OPTIMAL POLICY
		displayPolicyButton = new JButton("Display Policy");
		displayPolicyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gw.displayOptimalPolicy();
			}
		});

		// NORTH PANEL
		// VALUE ITERATION
		valueIterationButton = new JButton("Value Iteration");
		valueIterationButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectError();
				if (policyRunning)
					PolicyIteration.policyIterationGraph.dispose();
				if (valueRunning)
					ValueIteration.valueIterationGraph.dispose();
				valueIterate.startIteration();
				enableUtilityAndPolicy();
				valueRunning = true;
				policyRunning = false;
				method.setText("<html><br><hr>Value Iteration<hr></html>");
				showPanel();
			}
		});

		// POLICY ITERATION
		policyIterationButton = new JButton("Policy Iteration");
		policyIterationButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectPolicyMethod();
				if (policyRunning)
					PolicyIteration.policyIterationGraph.dispose();
				if (valueRunning)
					ValueIteration.valueIterationGraph.dispose();
				policyIterate.startIteration();
				enableUtilityAndPolicy();
				// valueIterationButton.setEnabled(true);
				// policyIterationButton.setEnabled(false);

				valueRunning = false;
				policyRunning = true;
				method.setText("<html><br><hr>Policy Iteration<hr></html>");
				showPanel();

			}
		});

		// SOUTH PANEL
		// DEFAULT CONFIGURATION
		defaultConfiguration = new JButton("Default Configuration");
		defaultConfiguration.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (gw.defaultConfig())
					JOptionPane.showMessageDialog(null, "Loaded Configuration");
				else
					JOptionPane.showMessageDialog(null, "No configuration coded for this state size");
				disableUtilityAndPolicy();
			}
		});
		// RANDOM CONFIGURATION
		randomConfiguration = new JButton("Random Configuration");
		randomConfiguration.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gw.randomizeConfig();
				disableUtilityAndPolicy();
			}
		});
		// SLIDER
		JLabel numState = new JLabel("<html>No. of states<br> (rows x cols):</html>", SwingConstants.RIGHT);
		JSlider stateSize = new JSlider(JSlider.HORIZONTAL, 6, 20, 6);
		stateSize.setMajorTickSpacing(1);
		stateSize.setPaintTicks(true);
		stateSize.setPaintLabels(true);
		stateSize.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				gw.setColsRowsSize(stateSize.getValue());
				gw.stateSizeChange();
				disableUtilityAndPolicy();
			}
		});
		// STATISTIC LABELS
		method = new JLabel("", SwingConstants.CENTER);
		numIteration = new JLabel("", SwingConstants.CENTER);
		numStates = new JLabel("", SwingConstants.CENTER);
		discount = new JLabel("Discount: "+Double.toString(Constant.DISCOUNT), SwingConstants.CENTER);

		// COMPONENTS TO PANEL
		disableUtilityAndPolicy();
		panelNorth.add(valueIterationButton);
		panelNorth.add(policyIterationButton);
		panelSouth.add(defaultConfiguration);
		panelSouth.add(randomConfiguration);
		panelSouth.add(numState);
		panelSouth.add(stateSize);
		panelEast.add(displayRewardButton);
		panelEast.add(displayUtilityButton);
		panelEast.add(displayPolicyButton);
		panelEast.add(method);
		panelEast.add(numIteration);
		panelEast.add(numStates);
		panelEast.add(discount);

		// ADD PANELS
		contentPane.add(panelNorth, BorderLayout.NORTH);
		contentPane.add(panelSouth, BorderLayout.SOUTH);
		contentPane.add(panelEast, BorderLayout.EAST);
		// DISPLAY THE WINDOW
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	// DISABLE UTILITY ,REWARD BUTTON; STATISTIC LABEL
	public static void disableUtilityAndPolicy() {
		gw.displayReward();
		displayUtilityButton.setVisible(false);
		displayPolicyButton.setVisible(false);
		valueIterationButton.setEnabled(true);
		policyIterationButton.setEnabled(true);
		method.setVisible(false);
		numIteration.setVisible(false);
		numStates.setVisible(false);
		discount.setVisible(false);
	}

	// ENABLE UTILITY AND POLICY BUTTON
	public static void enableUtilityAndPolicy() {
		displayUtilityButton.setVisible(true);
		displayPolicyButton.setVisible(true);
	}

	@SuppressWarnings("deprecation")
	// PROMPT MODIFY MAXIMUM ERROR?
	public static void selectError() {
		JPanel panel = new JPanel();
		panel.add(new JLabel("Maximum Error allowed: (default 0.1)"));

		JButton decrement = new JButton("-0.01");

		decrement.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				errorAllowed.setText(df.format(Double.parseDouble(errorAllowed.getText()) - 0.01));
				ValueIteration.maximumErrorAllowed -= 0.01;
			}
		});

		JButton decrement2 = new JButton("-1");

		decrement2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				errorAllowed.setText(df.format(Double.parseDouble(errorAllowed.getText()) - 1));
				ValueIteration.maximumErrorAllowed -= 1;
			}
		});
		errorAllowed = new JTextField(Double.toString(ValueIteration.maximumErrorAllowed), 3);
		errorAllowed.enable(false);

		JButton increment = new JButton("+0.01");

		increment.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				errorAllowed.setText(df.format(Double.parseDouble(errorAllowed.getText()) + 0.01));
				ValueIteration.maximumErrorAllowed += 0.01;
			}
		});
		
		JButton increment2 = new JButton("+1");

		increment2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				errorAllowed.setText(df.format(Double.parseDouble(errorAllowed.getText()) + 1));
				ValueIteration.maximumErrorAllowed += 1;
			}
		});
		
		panel.add(decrement2);
		panel.add(decrement);
		panel.add(errorAllowed);
		panel.add(increment);
		panel.add(increment2);
		Object[] options = { "OK" };
		JOptionPane.showOptionDialog(null, panel, "Value Iteration - Select Maximum Error Allowed",
				JOptionPane.PLAIN_MESSAGE, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
	}

	@SuppressWarnings("deprecation")

	// SELECT POLICY
	public static void selectPolicyMethod() {

		Object[] options = { "Solve linear system using gaussian elimination",
				"Modified Policy Iteration " + "(k=" + PolicyIteration.getK() + "):" };
		int action = JOptionPane.showOptionDialog(null, null, "Policy Iteration - Select Policy Evaluation method",
				JOptionPane.PLAIN_MESSAGE, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		policyIterate.setMode(action);
		if (action == 1) {
			// PROMPT MODIFY K?
			JPanel panel = new JPanel();
			panel.add(new JLabel(
					"k ( number of times simplified Bellman update is executed to produce the next utility estimate, default 20), set high values = [guarantee optimality similar to value iteration]"));

			JButton decrement = new JButton("-");

			decrement.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					PolicyIteration.setK(PolicyIteration.getK() - 1);
					if (PolicyIteration.getK() < 1)
						PolicyIteration.setK(1);
					numK.setText(Integer.toString(PolicyIteration.getK()));
				}
			});

			numK = new JTextField(Integer.toString(PolicyIteration.getK()), 3);
			numK.enable(false);

			JButton increment = new JButton("+");

			increment.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					PolicyIteration.setK(PolicyIteration.getK() + 1);
					numK.setText(Integer.toString(PolicyIteration.getK()));
				}
			});
			panel.add(decrement);
			panel.add(numK);
			panel.add(increment);
			Object[] options2 = { "OK" };
			JOptionPane.showOptionDialog(null, panel, "Policy Iteration - Modified k value?", JOptionPane.PLAIN_MESSAGE,
					JOptionPane.QUESTION_MESSAGE, null, options2, options2[0]);
		}

	}

	// SHOW GAUSSIAN ERROR IF MATRIX IS SINGULAR
	public static void gaussianError() {
		JPanel panel = new JPanel();
		panel.add(new JLabel("Unable to solve linear system, most likely a linear system with inconsistency"));
		Object[] options = { "OK" };
		JOptionPane.showOptionDialog(null, panel, "Policy Iteration - Gaussian Elimination error",
				JOptionPane.PLAIN_MESSAGE, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
	}

	// SHOW STATISTIC
	public static void showPanel() {
		numIteration.setVisible(true);
		numStates.setVisible(true);
		method.setVisible(true);
		discount.setVisible(true);
	}

}
