package FallenFeather;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import FallenFeather.lib.Vect2d;

public class ControllersOld {
	/**
	 * Selected only handles units of this controller
	 */

	// 0 = controller
	// 1 = unit
	int[] unitSelected = new int[2];

	private float[] cameraLoc;

	private List<Unit> units;

	// 0 = x, 1 = y, 2 = button.
	ArrayList<int[]> mPress;
	ArrayList<int[]> mRelease;
	int[] mDrag;
	int[] lastDragLoc;
	boolean mD;
	int lastClick;

	public ControllersOld(float cx, float cy) {
		cameraLoc = new float[] { cx, cy };
		units = new ArrayList<Unit>();
		mPress = new ArrayList<int[]>();
		mRelease = new ArrayList<int[]>();
		mDrag = new int[3];
		lastDragLoc = new int[2];
		mD = false;
		lastClick = -1;
	}

	public void tic() {
		qPress();
		qDrag();
		qRelease();
		for (int u = 0; u < units.size(); u++) {
			units.get(u).followPath();
		}
	}

	void qPress() {
		for (int p = 0; p < mPress.size(); p++) {
			if (mPress.get(p)[2] == 1) {
				lastClick = 1;
				// 1 is left mouse button
				clearSelected();
				// Check to see if it click on one of its units.
				// Check to see if it click on the unit of another controller.
				for (int u = 0; u < units.size(); u++) {
					if (units.get(u).overlap(mPress.get(p)[0] + cameraLoc[0],
							mPress.get(p)[1] + cameraLoc[1])) {
						units.get(u).setSelected(true);
					}
				}
			} else if (mPress.get(p)[2] == 3) {
				lastClick = 3;
				// 3 is right mouse button
				System.out.println("3p");
				// set target and at the beginning of the next tick make paths
				// for all selected units.
				makePaths(mPress.get(p)[0] + (int) cameraLoc[0],
						mPress.get(p)[1] + (int) cameraLoc[1]);
			}
		}
		mPress.clear();
	}

	void qDrag() {
		if (mD) {
			if (lastClick == 1) {
				int[] delta = { mDrag[0] - lastDragLoc[0],
						mDrag[1] - lastDragLoc[1] };
				Vect2d.sayVect("delta", delta);
				cameraLoc[0] -= delta[0];
				cameraLoc[1] -= delta[1];
			} else if (lastClick == 3) {
				makePaths(mDrag[0] + (int) cameraLoc[0], mDrag[1]
						+ (int) cameraLoc[1]);
			}
			mD = false;
			lastDragLoc[0] = mDrag[0];
			lastDragLoc[1] = mDrag[1];
		}
	}

	void qRelease() {
		for (int r = 0; r < mRelease.size(); r++) {

		}
		mRelease.clear();
	}

	// NOT CURRENTLY USED
	void drawAll(Graphics g, List<Controllers> conts, float[] treeInfo,
			int thisContNumber) {

		// plug in: Graphics, Controllers, Trees, This controller number.
		drawUnits(g);
		drawTrees(g);
	}

	void drawAll(Graphics g, float[][][] compressedU, float[] treeInfo,
			int thisContNumber) {
		// plug in: Graphics, Controllers, Trees, This controller number.
		drawUnits(g, compressedU, thisContNumber);
		drawTrees(g, treeInfo);
	}

	void drawUnits(Graphics g, float[][][] compressedU, int thisContNumber) {
		for (int c = 0; c < compressedU.length; c++) {
			for (int u = 0; u < compressedU[c].length; u++) {
				if (c == thisContNumber) {
					if (units.get(u).getSelected()) {
						fillCircleRel(g, Color.YELLOW, compressedU[c][u][0],
								compressedU[c][u][1], compressedU[c][u][2] + 2);
						// fillCircleRel(g, Color.YELLOW,
						// units.get(i).getLoc()[0],
						// units.get(i).getLoc()[1], units.get(i)
						// .getRadius() + 2);
					}
					// fillCircleRel(g, Color.BLUE, units.get(i).getLoc()[0],
					// units.get(i).getLoc()[1], units.get(i).getRadius());
				}
				fillCircleRel(g, Color.YELLOW, compressedU[c][u][0],
						compressedU[c][u][1], compressedU[c][u][2] + 2);

			}
		}

		// Need trash collection
		for (int i = 0; i < units.size(); i++) {
			if (units.get(i).getSelected()) {
				fillCircleRel(g, Color.YELLOW, units.get(i).getLoc()[0], units
						.get(i).getLoc()[1], units.get(i).getRadius() + 2);
			}
			fillCircleRel(g, Color.BLUE, units.get(i).getLoc()[0], units.get(i)
					.getLoc()[1], units.get(i).getRadius());
		}
	}

	void drawUnits(Graphics g) {
		// Need trash collection
		for (int i = 0; i < units.size(); i++) {
			if (units.get(i).getSelected()) {
				fillCircleRel(g, Color.YELLOW, units.get(i).getLoc()[0], units
						.get(i).getLoc()[1], units.get(i).getRadius() + 2);
			}
			fillCircleRel(g, Color.BLUE, units.get(i).getLoc()[0], units.get(i)
					.getLoc()[1], units.get(i).getRadius());
		}
	}

	void drawTrees(Graphics g, float[] treeInfo) {
		// only 1 tree right now.
		fillCircleRel(g, new Color(66, 33, 00), treeInfo[0], treeInfo[1],
				treeInfo[2]);
		fillCircleRel(g, Color.GREEN, treeInfo[0], treeInfo[1], treeInfo[2] - 2);
	}

	void drawTrees(Graphics g) {
	}

	void clearSelected() {
		for (int i = 0; i < units.size(); i++) {
			units.get(i).setSelected(false);
		}
	}

	void makePaths(int tarX, int tarY) {
		// for every selected unit.
		// Use every tree to do poly circ collision and pathfinding.

		for (int u = 0; u < units.size(); u++) {
			if (units.get(u).getSelected()) {
				units.get(u).playMoveWhole(tarX, tarY);
			}
		}
	}

	public float[][] compressUnits() {
		// need to give back: X, Y, Radius, Color (R, G, B).
		float[][] compU = new float[units.size()][5];
		for (int u = 0; u < units.size(); u++) {
			compU[u] = new float[] { units.get(u).getLoc()[0],
					units.get(u).getLoc()[1], units.get(u).getColor().getRed(),
					units.get(u).getColor().getGreen(),
					units.get(u).getColor().getBlue() };
		}
		return compU;
	}

	/**
	 * Adding
	 */

	void addUnit(float[] loc, float r, float ms, Color c) {
		units.add(new Unit(loc, r, ms, c));
	}

	/**
	 * Input handeling
	 */

	public void mousePress(int x, int y, int button) {
		System.out.println("(x, y): (" + x + ", " + y + ")");
		Vect2d.sayVect("cameraLoc", cameraLoc);
		if (!mD) {
			lastDragLoc[0] = x;
			lastDragLoc[1] = y;
		}
		mPress.add(new int[] { x, y, button });
	}

	public void mouseRelease(int x, int y, int button) {
		mRelease.add(new int[] { x, y, button });
	}

	public void mouseDrag(int x, int y, int button) {
		mDrag[0] = x;
		mDrag[1] = y;
		mDrag[2] = button;
		mD = true;
	}

	/**
	 * Drawing Methods
	 */

	void drawCircleRel(Graphics g, Color color, float[] circLoc, float radius) {
		g.setColor(color);
		float deltax = cameraLoc[0];
		float deltay = cameraLoc[1];
		g.drawOval((int) (circLoc[0] - radius - deltax + .5f),
				(int) (circLoc[1] - radius + deltay + .5f), (int) (radius * 2),
				(int) (radius * 2));
	}

	void drawCircleRel(Graphics g, Color color, float circX, float circY,
			float radius) {
		g.setColor(color);
		float deltax = cameraLoc[0];
		float deltay = cameraLoc[1];
		g.drawOval((int) (circX - radius - deltax + .5f), (int) (circY - radius
				- deltay + .5f), (int) (radius * 2), (int) (radius * 2));
	}

	void fillCircleRel(Graphics g, Color color, float circX, float circY,
			float radius) {
		g.setColor(color);
		float deltax = cameraLoc[0];
		float deltay = cameraLoc[1];
		g.fillOval((int) (circX - radius - deltax + .5f), (int) (circY - radius
				- deltay + .5f), (int) (radius * 2), (int) (radius * 2));
	}

}
