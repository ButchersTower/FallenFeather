package FallenFeather.old;

public class JaMaOld1 {
	// line line distance.
	// circle line distance.
	// Edge wall.

	static float distSegmenttoSegment(float[][] s1, float[][] s2) {
		double SMALL_NUM = 0.00000001;
		// Vector u = S1.P1 - S1.P0;
		float[] u = Vect2dOld1.vectSub(s1[1], s1[0]);
		// Vector v = S2.P1 - S2.P0;
		float[] v = Vect2dOld1.vectSub(s2[1], s2[0]);
		// Vector w = S1.P0 - S2.P0;
		float[] w = Vect2dOld1.vectSub(s1[0], s2[0]);
		float a = Vect2dOld1.dot(u, u); // always >= 0
		float b = Vect2dOld1.dot(u, v);
		float c = Vect2dOld1.dot(v, v); // always >= 0
		float d = Vect2dOld1.dot(u, w);
		float e = Vect2dOld1.dot(v, w);
		float D = a * c - b * b; // always >= 0
		System.out.println("D: " + D);
		float sc, sN, sD = D; // sc = sN / sD, default sD = D >= 0
		float tc, tN, tD = D; // tc = tN / tD, default tD = D >= 0

		// compute the line parameters of the two closest points
		if (D < SMALL_NUM) { // the lines are almost parallel
			sN = 0.0f; // force using point P0 on segment S1
			sD = 1.0f; // to prevent possible division by 0.0 later
			tN = e;
			tD = c;
		} else { // get the closest points on the infinite lines
			sN = (b * e - c * d);
			tN = (a * e - b * d);
			System.out.println("sN: " + sN);
			System.out.println("tN: " + tN);
			if (sN < 0.0f) { // sc < 0 => the s=0 edge is visible
				sN = 0.0f;
				tN = e;
				tD = c;
			} else if (sN > sD) { // sc > 1 => the s=1 edge is visible
				sN = sD;
				tN = e + b;
				tD = c;
			}
		}

		if (tN < 0.0f) { // tc < 0 => the t=0 edge is visible
			tN = 0.0f;
			// recompute sc for this edge
			if (-d < 0.0f)
				sN = 0.0f;
			else if (-d > a)
				sN = sD;
			else {
				sN = -d;
				sD = a;
			}
		} else if (tN > tD) { // tc > 1 => the t=1 edge is visible
			tN = tD;
			// recompute sc for this edge
			if ((-d + b) < 0.0f)
				sN = 0;
			else if ((-d + b) > a)
				sN = sD;
			else {
				sN = (-d + b);
				sD = a;
			}
		}

		// finally do the division to get sc and tc
		// sc = (float) (Math.abs(sN) < SMALL_NUM ? 0.0 : sN / sD);
		// tc = (float) (Math.abs(tN) < SMALL_NUM ? 0.0 : tN / tD);
		// BREAK INTO IF STATEMENT.
		if (Math.abs(sN) < SMALL_NUM) {
			sc = 0.0f;
		} else {
			sc = sN / sD;
		}

		if (Math.abs(tN) < SMALL_NUM) {
			tc = 0.0f;
		} else {
			tc = tN / tD;
		}

		// get the difference of the two closest points
		// Vector dP = w + (sc * u) - (tc * v); // = S1(sc) - S2(tc)
		float[] dP = Vect2dOld1.vectAdd(
				w,
				Vect2dOld1.vectSub(Vect2dOld1.vectMultScalar(sc, u),
						Vect2dOld1.vectMultScalar(tc, v))); // =
		// S1(sc)
		// -
		// S2(tc)

		// s1[0][0] + (s1[1] - s1[0]) * sc

		float[] s1loc = Vect2dOld1.vectAdd(s1[0],
				Vect2dOld1.vectMultScalar(sc, Vect2dOld1.vectSub(s1[1], s1[0])));
		// System.out.println("loc1 (" + s1loc[0] + ", " + s1loc[1] + ")");

		float[] s2loc = Vect2dOld1.vectAdd(s2[0],
				Vect2dOld1.vectMultScalar(tc, Vect2dOld1.vectSub(s2[1], s2[0])));

		return Vect2dOld1.norm(dP); // return the closest distance

	}
}
