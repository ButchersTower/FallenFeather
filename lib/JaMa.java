package FallenFeather.lib;

import FallenFeather.lib.Vect2d;

public class JaMa {

	// Jacob Math

	public static int[] appendIntAr(int[] st, int appendage) {
		int[] temp = new int[st.length + 1];
		for (int a = 0; a < st.length; a++) {
			temp[a] = st[a];
		}
		temp[temp.length - 1] = appendage;
		return temp;
	}

	public static int[][] appendIntArAr(int[][] st, int[] appendage) {
		int[][] temp = new int[st.length + 1][];
		for (int a = 0; a < st.length; a++) {
			temp[a] = st[a];
		}
		temp[temp.length - 1] = appendage;
		return temp;
	}

	public static float[] appendFloatAr(float[] st, float appendage) {
		float[] temp = new float[st.length + 1];
		for (int a = 0; a < st.length; a++) {
			temp[a] = st[a];
		}
		temp[temp.length - 1] = appendage;
		return temp;
	}

	public static float[] appendArFloatAr(float[] st, float[] appendage) {
		float[] temp = new float[st.length + appendage.length];
		for (int a = 0; a < st.length; a++) {
			temp[a] = st[a];
		}
		for (int a = 0; a < appendage.length; a++) {
			temp[st.length + a] = appendage[a];
		}
		return temp;
	}

	public static float[][] appendFloatArAr(float[][] st, float[] appendage) {
		float[][] temp = new float[st.length + 1][];
		for (int a = 0; a < st.length; a++) {
			temp[a] = st[a];
		}
		temp[temp.length - 1] = appendage;
		return temp;
	}

	public static float[][][] appendFloatArArAr(float[][][] st,
			float[][] appendage) {
		float[][][] temp = new float[st.length + 1][][];
		for (int a = 0; a < st.length; a++) {
			temp[a] = st[a];
		}
		temp[temp.length - 1] = appendage;
		return temp;
	}

	public static int[] injectIntAr(int[] ar, int app, int loc) {
		System.out.println("ar.l: " + ar.length);
		int[] buff = new int[ar.length + 1];
		boolean added = false;
		for (int a = 0; a < buff.length; a++) {
			if (a == loc) {
				buff[a] = app;
				added = true;
			} else {
				if (added) {
					buff[a] = ar[a - 1];
				} else {
					buff[a] = ar[a];
				}
			}
		}
		if (!added) {
			buff[loc] = app;
		}
		return buff;
	}

	public static int[][] injectIntArAr(int[][] ar, int[] app, int loc) {
		int[][] buff = new int[ar.length + 1][];
		boolean added = false;
		for (int a = 0; a < buff.length; a++) {
			if (a == loc) {
				buff[a] = app;
				added = true;
			} else {
				if (added) {
					buff[a + 1] = ar[a];
				} else {
					buff[a] = ar[a];
				}
			}
		}
		if (!added) {
			buff[loc] = app;
		}
		return buff;
	}

	public static float[] injectFloatAr(float[] ar, float app, int loc) {
		// System.out.println("ar.l: " + ar.length);
		float[] buff = new float[ar.length + 1];
		boolean added = false;
		for (int a = 0; a < buff.length; a++) {
			if (a == loc) {
				buff[a] = app;
				added = true;
			} else {
				if (added) {
					buff[a] = ar[a - 1];
				} else {
					buff[a] = ar[a];
				}
			}
		}
		if (!added) {
			buff[loc] = app;
		}
		return buff;
	}

	public static float[][] injectFloatArAr(float[][] ar, float[] app, int loc) {
		// System.out.println("ar.l: " + ar.length);
		float[][] buff = new float[ar.length + 1][];
		boolean added = false;
		for (int a = 0; a < buff.length; a++) {
			if (a == loc) {
				buff[a] = app;
				added = true;
			} else {
				if (added) {
					buff[a] = ar[a - 1];
				} else {
					buff[a] = ar[a];
				}
			}
		}
		if (!added) {
			buff[loc] = app;
		}
		return buff;
	}

	public static float[] sortLowToHigh(float[] a) {
		// run through and find the lowest a's
		//
		// [0] = a
		// [1] = o
		float[] order = { a[0] };
		for (int o = 1; o < a.length; o++) {
			boolean stuckIn = false;
			bloop: for (int l = 0; l < order.length; l++) {
				if (a[o] < order[l]) {
					// stick in before and kill loop
					order = JaMa.injectFloatAr(order, a[o], l);
					stuckIn = true;
					break bloop;
				} else {
					// check the next
				}
			}
			if (stuckIn == false) {
				order = JaMa.appendFloatAr(order, a[o]);
			}
		}
		return order;
	}

	public int[] shortenIntAr(int[] in, int numToRemove) {
		// This removed the [numToRemove] variable from an AR and compresses
		int[] temp = new int[in.length - 1];
		boolean reachedYet = false;
		for (int a = 0; a < in.length; a++) {
			System.out.println("a: " + a);
			if (a == numToRemove) {
				reachedYet = true;
				a++;
				System.out.println("newA: " + a);
			}
			if (a < in.length) {
				if (reachedYet) {
					temp[a - 1] = in[a];
				} else {
					temp[a] = in[a];
				}
			}
		}
		return temp;
	}

	public static float[] removeFirstFloatAr(float[] ar, int upTo) {
		// removes the first (upTo) values of the Ar.
		float[] temp = new float[ar.length - upTo];
		for (int i = 0; i < temp.length; i++) {
			temp[i] = ar[i + upTo];
		}
		return temp;
	}

	public static float distPointToVect(float[] point, float[] vect) {
		float dist;
		float projScalar = Vect2d.scalarOfProject(point, vect);
		if (projScalar > 1) {
			dist = Vect2d.norm(Vect2d.vectSub(point, vect));
		} else if (projScalar < 0) {
			dist = Vect2d.norm(point);
		} else {
			dist = Vect2d.norm(Vect2d.vectSub(point,
					Vect2d.vectMultScalar(projScalar, vect)));
		}
		return dist;
	}

	public static float[] myAngleThing(float[] play, float pRad, float[] tree,
			float tRad) {
		// Returnes the two point tangental play and tree

		// if |delta| < pRad + tRad
		// get thea of play and project it out from tree to a dist of pRad+tRad.
		// This is the plusPoint and subPoint.
		float[] delta = Vect2d.vectSub(tree, play);
		float hyp = Vect2d.norm(delta);
		float opp = pRad + tRad;
		float adj = (float) Math.sqrt(Math.abs(hyp * hyp - opp * opp));
		float treeThea = Vect2d.pointToThea(delta);
		float shapeThea = Vect2d.pointToThea(new float[] { adj, opp });
		// how to tell is to subtract or add shape thea.
		// the two possible points are plus shape thea and minus shape thea
		// scaled to adjacent and added to play.
		// return float[]
		// [0 + 1] is (x, y) of plus thea
		// [2 + 3] is (x, y) of minus thea
		// [4] is the length from play to each point.
		// System.out.println("treeThea: " + treeThea);
		// System.out.println("shapeThea: " + shapeThea);
		float addThea = Vect2d.theaAdd(treeThea, shapeThea);
		// System.out.println("addThea: " + addThea);
		float subThea = Vect2d.theaSub(treeThea, shapeThea);
		float[] addPoint = Vect2d.theaToPoint(addThea, adj);
		float[] subPoint = Vect2d.theaToPoint(subThea, adj);
		// make sub thea and plus thea relative to tree.
		// plus point minus tree
		float[] relAddPoint = Vect2d.vectSub(Vect2d.vectAdd(play, addPoint),
				tree);
		float[] relSubPoint = Vect2d.vectSub(Vect2d.vectAdd(play, subPoint),
				tree);
		// g.setColor(Color.GREEN);
		// g.drawOval((int) (relAddPoint[0] + tree[0]) - 4,
		// (int) (relAddPoint[1] + tree[1]) - 4, 8, 8);
		// g.setColor(Color.CYAN);
		// g.drawOval((int) (relSubPoint[0] + tree[0]) - 4,
		// (int) (relSubPoint[1] + tree[1]) - 4, 8, 8);
		float relAddThea = Vect2d.pointToThea(relAddPoint);
		float relSubThea = Vect2d.pointToThea(relSubPoint);
		addPoint = Vect2d.normalize(addPoint);
		subPoint = Vect2d.normalize(subPoint);
		System.out.println("subPoint[0]: " + subPoint[0]);
		return new float[] { addPoint[0], addPoint[1], relAddThea, subPoint[0],
				subPoint[1], relSubThea, adj };
	}

}
