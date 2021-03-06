import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;

import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

public class Main {
	private static final int PAST_FRAME_SIZE = 25;

	private static final int MIN_PAST_FRAME_SIZE = 5;

	private static final int MAX_PAST_FRAME_SIZE = 35;

	private static final int HIDDEN_LAYER_SIZE = 26;

	private static final int MIN_HIDDEN_LAYER_SIZE = 1;

	private static final int MAX_HIDDEN_LAYER_SIZE = 30;

	private static final int FUTURE_FRAME_SIZE = 5;

	private static final double TRAINING_STOP_ERROR = 0.0001;

	private static final long TRAINING_TIMEOUT = 1000 * 60 * 60;

	private static Integer TIME_SERIES[] = { 3938, 1317, 4021, 10477, 9379,
			7707, 9507, 4194, 2681, 3522, 5599, 5641, 6737, 7781, 2044, 1501,
			6586, 4915, 5918, 6132, 9394, 2113, 935, 9729, 5236, 8815, 3169,
			5888, 5722, 191, 9539, 3384, 6006, 7139, 7285, 136, 1843, 5094,
			3795, 5985, 5566, 3545, 965, 14, 3738, 4645, 8439, 6390, 13842,
			7754, 11440, 7572, 4876, 3206, 5577, 2734, 1169, 20, 5049, 6612,
			2685, 7000, 6711, 4091, 26, 5383, 5516, 7185, 6118, 4484, 2178,
			754, 8104, 8209, 6159, 11137, 8994, 5172, 425, 8082, 5337, 5712,
			7157, 6385, 3343, 4196, 5957, 8581, 3686, 0, 254, 1819, 1071, 876,
			3509, 2777, 1474, 4945, 3971, 21, 5466, 5509, 1316, 5653, 2775,
			797, 22, 5601, 6177, 5662, 5132, 6543, 1700, 4361, 6951, 7734,
			3451, 5385, 6358, 6838, 19, 6460, 5813, 6839, 6335, 2105, 8, 6,
			9530, 1250, 5668, 5595, 6008, 2315, 1712, 8553, 5570, 5979, 4818,
			6745, 5250, 43, 5727, 7416, 5888, 6270, 4931, 0, 31, 6190, 11164,
			5768, 7307, 5412, 2716, 35, 8391, 6054, 2796, 5081, 6646, 4597,
			1978, 7570, 5909, 9581, 3571, 6740, 1702, 1080, 6719, 963, 6781,
			7544, 7708, 1993, 597, 2394, 5516, 12966, 723, 6528, 2476, 86,
			5956, 5820, 6995, 6682, 2460, 2479, 56, 7095, 7255, 6310, 9971,
			3725, 5400, 452, 6018, 5803, 6673, 6098, 9476, 692, 20, 7855,
			11970, 10557, 5696, 7765, 3847, 47, 6020, 6037, 5684, 7089, 6372,
			970, 861, 3590, 7672, 3730, 10689, 9428, 1514, 2062, 6154, 5234,
			6160, 5134, 879, 1079, 9164, 6338, 6687, 8195, 6351, 1123, 4216,
			3759, 9372, 7782, 3143, 4773, 6993, 849, 906, 6385, 7512, 8824,
			8150, 12464, 7726, 8745, 13594, 6589, 6524, 2784, 0, 1785, 688,
			7998, 6797, 8289, 10815, 10280, 4839, 3928, 10935, 4588, 5785,
			6771, 7628, 2908, 11391, 6637, 5585, 7454, 5828, 8259, 6644, 2436,
			7055, 7206, 7873, 7368, 6239, 3595, 3166, 1846, 2301, 21, 1600,
			2390, 1894, 1469, 9097, 8401, 2034, 3244, 8811, 2979, 20, 7808,
			7698, 11031, 4556, 7149, 3745, 5563, 9673, 8149, 12158, 7043, 6273,
			1855, 80, 10729, 5880, 9327, 6343, 7227, 3522, 1244, 6382, 7186,
			4964, 6162, 7435, 10524, 2449, 7437, 11970, 6661, 6122, 7323, 6707,
			25, 2270, 5117, 6676, 5317, 7032, 7689, 4891, 8051, 5699, 4927,
			11553, 6418, 2968, 11338, 7662, 9976, 5526, 14341, 4331, 10026,
			1672, 5199, 4699, 7774, 7958, 7720, 2499, 10745, 19609, 15896,
			5705, 6207, 7699, 2543, 32, 3642, 6307, 7491, 6236, 8644, 2121,
			1448, 7838, 5434, 5945, 6074, 6962, 5441, 42, 7424, 5818, 8877,
			5743, 7980, 3140, 3046, 8329, 8186, 5994, 2931, 7309, 862, 145,
			8141, 6252, 9536, 6213, 7150, 2718, 1687, 5000, 6068, 5918, 10652,
			12257, 1505, 2421, 10518, 2368, 7341, 8137, 7997, 3437, 2009, 5468,
			3947, 5836, 8567, 11039, 3726, 746, 3417, 8649, 8016, 7652, 8298,
			1306, 4031, 5525, 6203, 11847, 7688, 10911, 1080, 1001, 12315,
			6084, 6529, 4074, 8526, 3161, 2184, 7400, 4916, 4521, 1523, 398,
			1364, 925, 38, 2580, 1039, 6556, 2040, 1166, 825, 7672, 7177, 6104,
			7928, 6240, 1420, 1214, 10638, 10726, 2323, 6113, 8112, 2757, 3761,
			6982, 5680, 7793, 8983, 8546, 1335, 817, 6136, 3778, 6639, 6548,
			6120, 3648, 584, 9099, 6434, 8828, 9988, 6066, 2575, 2237, 5114,
			5879, 4094, 9309, 8008, 1614, 4307, 5801, 8006, 6344, 4803, 10904,
			1339, 411, 8468, 6945, 5471, 8828, 4157, 1134, 1071, 5542, 2213,
			5633, 9245, 2145, 4901, 39, 10430, 7941, 6189, 7985, 8296, 614,
			894, 6236, 1704, 4257, 7707, 8388, 1050, 855, 9352, 4801, 7088,
			8466, 470, 2433, 1036, 392, 2169, 84, 5316, 8339, 4272, 2617, 1840,
			7254, 5999, 6178, 4563, 3370, 756, 2773, 6610, 8967, 6182, 7452,
			2570, 1443, 6537, 5338, 9158, 3870, 12036, 3574, 864, 10135, 5595,
			8643, 2287, 9918, 2484, };

	private static BasicNetwork network = new BasicNetwork();
	static {
		network.addLayer(new BasicLayer(null, true, PAST_FRAME_SIZE));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), true,
				HIDDEN_LAYER_SIZE));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), false,
				FUTURE_FRAME_SIZE));
		network.getStructure().finalizeStructure();
		network.reset();
	}

	private static void setupNetwork(int inputSize, int hiddenSize,
			int outputSize) {
		network = new BasicNetwork();
		network.addLayer(new BasicLayer(null, true, inputSize));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), true,
				hiddenSize));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), false,
				outputSize));
		network.getStructure().finalizeStructure();
		network.reset();
	}

	private static double distance(double[] a, double[] b) {
		double value = 0.0;
		for (int i = 0; i < a.length && i < b.length; i++) {
			value += (a[i] - b[i]) * (a[i] - b[i]);
		}
		return Math.sqrt(value);
	}

	private static void singleNetworkExperiment(PrintStream out,
			Integer[] series, int inputSize, int hiddenSize, int outputSize) {
		out.println("Setup network " + inputSize + "-" + hiddenSize + "-"
				+ outputSize + " ...");
		setupNetwork(inputSize, hiddenSize, outputSize);

		out.println("Finding min and max value ...");
		double min = (double) Collections.min(Arrays.asList(series));
		double max = (double) Collections.max(Arrays.asList(series));

		out.println("Normalizing ...");
		double normalized[] = new double[series.length];
		for (int i = 0; i < series.length; i++) {
			normalized[i] = (series[i] - min) / (max - min);
		}
		out.println("\t\t" + Arrays.toString(normalized));

		out.println("Splitting train and validation set ...");
		double training[] = new double[normalized.length * 2 / 3];
		System.arraycopy(normalized, 0, training, 0, training.length);
		double testing[] = new double[normalized.length * 1 / 3];
		System.arraycopy(normalized, normalized.length * 2 / 3, testing, 0,
				testing.length);

		/*
		 * Form training set.
		 */
		out.println("Training data set forming ...");
		double inputSet[][] = new double[normalized.length * 2 / 3][inputSize];
		double expectedSet[][] = new double[normalized.length * 2 / 3][outputSize];
		for (int i = 0; i < (normalized.length * 2 / 3) - inputSize; i++) {
			System.arraycopy(normalized, i, inputSet[i], 0, inputSet[i].length);
			System.arraycopy(normalized, i + inputSize, expectedSet[i], 0,
					expectedSet[i].length);
		}
		MLDataSet trainingSet = new BasicMLDataSet(inputSet, expectedSet);

		/*
		 * Train the network.
		 */
		out.println("Training ...");
		ResilientPropagation train = new ResilientPropagation(network,
				trainingSet);

		long epoch = 1;
		long start = System.currentTimeMillis();
		do {
			train.iteration();
			//System.err.println("" + epoch + "\t" + train.getError());
			epoch++;
		} while (train.getError() > TRAINING_STOP_ERROR
				&& System.currentTimeMillis() - start < TRAINING_TIMEOUT);
		train.finishTraining();
		out.println("Epochs\t" + epoch + "\t...");

		/*
		 * Form testing set.
		 */
		out.println("Testing data set forming ...");
		inputSet = new double[normalized.length * 1 / 3 - outputSize + 1][inputSize];
		expectedSet = new double[normalized.length * 1 / 3 - outputSize + 1][outputSize];
		for (int i = 0, j = (normalized.length * 2 / 3) - inputSize; j < normalized.length
				- (inputSize + outputSize - 1); i++, j++) {
			System.arraycopy(normalized, j, inputSet[i], 0, inputSet[i].length);
			System.arraycopy(normalized, j + inputSize, expectedSet[i], 0,
					expectedSet[i].length);
		}
		MLDataSet testingSet = new BasicMLDataSet(inputSet, expectedSet);

		out.println("Testing ...");
		double average = 0.0;
		for (MLDataPair pair : testingSet) {
			MLData output = network.compute(pair.getInput());
			double distance = distance(pair.getIdeal().getData(), output.getData());
			average += distance;
			out.println("\t"
					+ distance
					+ "\t" + Arrays.toString(pair.getInput().getData()) + "\t"
					+ Arrays.toString(output.getData()) + "\t"
					+ Arrays.toString(pair.getIdeal().getData()));
		}
		average /= Math.min(inputSet.length, expectedSet.length);
		out.println("Average\t" + average + "\t...");
	}

	private static void hiddenLayrSizeRangeExperiment() {
		for (int h = MAX_HIDDEN_LAYER_SIZE; h >= MIN_HIDDEN_LAYER_SIZE; h--) {
			singleNetworkExperiment(System.out, TIME_SERIES, PAST_FRAME_SIZE,
					h, FUTURE_FRAME_SIZE);
		}
	}

	private static void pastFrameSizeRangeExperiment(int hiddenSize,
			int outputSize) {
		for (int pfs = MAX_PAST_FRAME_SIZE; pfs >= MIN_PAST_FRAME_SIZE; pfs--) {
			singleNetworkExperiment(System.out, TIME_SERIES, pfs, hiddenSize,
					outputSize);
		}
	}

	public static void main(String[] args) {
		// singleNetworkExperiment(System.out, TIME_SERIES, PAST_FRAME_SIZE,
		// HIDDEN_LAYER_SIZE, FUTURE_FRAME_SIZE);
		// hiddenLayrSizeRangeExperiment();

		pastFrameSizeRangeExperiment(22, 5);
		pastFrameSizeRangeExperiment(12, 5);
		pastFrameSizeRangeExperiment(9, 5);

		Encog.getInstance().shutdown();
	}
}
