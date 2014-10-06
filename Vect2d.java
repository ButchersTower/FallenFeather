package FallenFeather;

public class Vect2d {
	static float dot(float[] a, float[] b) {
		float dp = (a[0] * b[0]) + (a[1] * b[1]);
		return dp;
	}

	static float norm(float[] v) {
		return (float) Math.sqrt(dot(v, v));
	}

	static float[] vectMultScalar(float scalar, float[] vect) {
		// this only works for 2d arrays but can make one that is less efficient
		// that works for anything.
		return new float[] { vect[0] * scalar, vect[1] * scalar };
	}

	static float[] vectDivScalar(float scalar, float[] vect) {
		return new float[] { vect[0] / scalar, vect[1] / scalar };
	}

	static float[] vectDivScalar(float scalar, int[] vect) {
		return new float[] { (float) (vect[0]) / scalar,
				(float) vect[1] / scalar };
	}

	static float[] vectAdd(float[] a, float[] b) {
		return new float[] { a[0] + b[0], a[1] + b[1] };
	}

	static float[] vectSub(float[] a, float[] b) {
		// a minus b.
		// b subtracted from a.
		return new float[] { a[0] - b[0], a[1] - b[1] };
	}
}
