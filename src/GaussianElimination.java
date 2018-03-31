

/**
 * @author GOH KA HIAN NANYANG TECHNOLOGICAL UNIVERSITY
 *
 */

public class GaussianElimination {
	private static final double EPSILON = 1e-10;

	// GAUSSIAN ELIMINATION WITH PARTIAL PIVOTING
	public static double[] lsolve(double[][] A, double[] b) {
		int N = b.length;

		for (int p = 0; p < N; p++) {

			// FIND PIVOT ROW AND SWAP
			int max = p;
			for (int i = p + 1; i < N; i++) {
				if (Math.abs(A[i][p]) > Math.abs(A[max][p])) {
					max = i;
				}
			}
			double[] temp = A[p];
			A[p] = A[max];
			A[max] = temp;
			double t = b[p];
			b[p] = b[max];
			b[max] = t;

			// SINGULAR OR NEARLY SINGULAR
			if (Math.abs(A[p][p]) <= EPSILON) {
				Main.gaussianError();
				throw new RuntimeException("Matrix is singular or nearly singular");
			}

			// PIVOT WITHIN A AND B
			for (int i = p + 1; i < N; i++) {
				double alpha = A[i][p] / A[p][p];
				b[i] -= alpha * b[p];
				for (int j = p; j < N; j++) {
					A[i][j] -= alpha * A[p][j];
				}
			}
		}

		// BACK SUBSTITUTION
		double[] x = new double[N];
		for (int i = N - 1; i >= 0; i--) {
			double sum = 0.0;
			for (int j = i + 1; j < N; j++) {
				sum += A[i][j] * x[j];
			}
			x[i] = (b[i] - sum) / A[i][i];
		}
		return x;
	}
}
