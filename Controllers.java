package FallenFeather;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import FallenFeather.lib.Indie;
import FallenFeather.lib.Vect2d;

public class Controllers {
	/**
	 * Selected only handles units of this controller
	 */

	// 0 = controller
	// 1 = unit
	int[] unitSelected = new int[2];

	private float[] cameraLoc;

	private List<Unit> units;

	// 0 = x, 1 = y, 2 = button.
	private ArrayList<int[]> mPress;
	private ArrayList<int[]> mRelease;
	private int[] mDrag;
	private int[] lastDragLoc;
	private boolean mD;

	private void drawInv(Graphics g, int[][] inv, int x, int y) {
		// I should even out the margins on both sides of the inv slots
		// draw x and y
		int column = 0;
		int row = -1;
		for (int i = 0; i < inv.length; i++) {
			if (i % invColumns == 0) {
				row++;
				column = 0;
			}
			g.setColor(Color.LIGHT_GRAY);
			int drawX = x + margin + invMargin + column
					* (invMargin + itemWidth);
			int drawY = y + margin + topMargin + invMargin + row
					* (invMargin + itemWidth);
			g.fillRect(drawX, drawY, itemWidth, itemWidth);
			if (inv[i][0] == 1) {
				g.drawImage(Panel.getImageAr()[0], drawX, drawY, null);
			}

			column++;
		}
	}

	private Color unitColor;
	// -1 = init
	// 1 = left click
	// 3 = right click
	// 4 = dragging
	private int lastClick;

	private List<Indie> allPanels;
	private List<Integer> openPanels;

	private int topMargin = 12;
	private int margin = 4;

	private int invWidth = 240;
	private int invHeight = 300;

	// ( x, y, width, height, closed/open, clickTop
	private int[] invInfo = { 700 - 40 - invWidth, 20, invWidth, invHeight, 0,
			0 };

	private int invMargin = 6;
	private int itemWidth = 32;

	private int invColumns = (invWidth - 2 * margin) / (margin + itemWidth);

	private int screenWidth = 0;
	private int screenHeight = 0;

	Color treeInner = new Color(210, 180, 140);

	public Controllers(float cx, float cy) {
		cameraLoc = new float[] { cx, cy };
		units = new ArrayList<Unit>();
		mPress = new ArrayList<int[]>();
		mRelease = new ArrayList<int[]>();
		mDrag = new int[3];
		lastDragLoc = new int[2];
		mD = false;
		lastClick = -1;
	}

	public Controllers(float cx, float cy, int screenWidth, int screenHeight,
			Color color) {
		cameraLoc = new float[] { cx, cy };
		units = new ArrayList<Unit>();
		mPress = new ArrayList<int[]>();
		mRelease = new ArrayList<int[]>();
		mDrag = new int[3];
		lastDragLoc = new int[2];
		mD = false;
		lastClick = -1;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		unitColor = color;
	}

	public void tic() {
		qPress();
		qDrag();
		qRelease();
		for (int u = 0; u < units.size(); u++) {
			units.get(u).followPath();
		}
	}

	private void qPress() {
		for (int p = 0; p < mPress.size(); p++) {
			if (mPress.get(p)[2] == 1) {
				clearSelected();
				lastClick = 1;
				for (int u = 0; u < units.size(); u++) {
					if (units.get(u).overlap(mPress.get(p)[0] + cameraLoc[0],
							mPress.get(p)[1] + cameraLoc[1])) {
						units.get(u).setSelected(true);
					}
				}
			} else if (mPress.get(p)[2] == 3) {
				// Check for tree overlap.

				lastClick = 3;
				// 3 is right mouse button
				System.out.println("3p");
				// set target and at the beginning of the next tick make
				// paths
				// for all selected units.
				makePaths(mPress.get(p)[0] + (int) cameraLoc[0],
						mPress.get(p)[1] + (int) cameraLoc[1]);
			}
			lastDragLoc[0] = mPress.get(p)[0];
			lastDragLoc[1] = mPress.get(p)[1];
		}
		mPress.clear();
	}

	private void qDrag() {
		if (mD) {
			if (lastClick == 1) {
				int[] delta = { mDrag[0] - lastDragLoc[0],
						mDrag[1] - lastDragLoc[1] };

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

	private void qRelease() {
		for (int r = 0; r < mRelease.size(); r++) {

		}
		mRelease.clear();
	}

	public void drawAll(Graphics g, float[][] treeInfo) {
		// Does not draw units of other controllers
		drawUnits(g);
		drawTrees(g, treeInfo);
	}

	private void drawAll(Graphics g, float[][][] compressedU,
			float[][] treeInfo, int thisContNumber) {
		// plug in: Graphics, Controllers, Trees, This controller number.
		drawUnits(g, compressedU, thisContNumber);
		drawTrees(g, treeInfo);
	}

	private void drawUnits(Graphics g, float[][][] compressedU,
			int thisContNumber) {
		for (int c = 0; c < compressedU.length; c++) {
			for (int u = 0; u < compressedU[c].length; u++) {
				if (c == thisContNumber) {
					if (units.get(u).getSelected()) {
						fillCircleRel(g, Color.YELLOW, compressedU[c][u][0],
								compressedU[c][u][1], compressedU[c][u][2] + 2);
					}
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

	private void drawUnits(Graphics g) {
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

	private void drawTrees(Graphics g, float[][] treeInfo) {
		// only 1 tree right now.
		for (int t = 0; t < treeInfo.length; t++) {
			fillCircleRel(g, new Color(66, 33, 00), treeInfo[t][0],
					treeInfo[t][1], treeInfo[t][2]);
			fillCircleRel(g, treeInner, treeInfo[t][0], treeInfo[t][1],
					treeInfo[t][2] - 2);
		}
	}

	private void drawTrees(Graphics g) {
	}

	private void clearSelected() {
		for (int i = 0; i < units.size(); i++) {
			units.get(i).setSelected(false);
		}
	}

	private void makePaths(int tarX, int tarY) {
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
	 * Inits
	 */

	private void initPanel() {
		// make the help panel.
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
		System.out.println("(x, y): (" + x + ", " + y + ")  button: " + button);
		Vect2d.sayVect("cameraLoc", cameraLoc);
		if (!mD) {
			lastDragLoc[0] = x;
			lastDragLoc[1] = y;
			Vect2d.sayVect("lastDragLocSet", lastDragLoc);
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

	public void keyPressed(int ke) {
		// System.out.println("ke code: " + ke);
		if (ke == KeyEvent.VK_A) {
			addUnit(new float[] { 300, 300 }, 24, 14, unitColor);
		}
	}

	/**
	 * Drawing Methods
	 */

	public void drawScene(Graphics g) {
		// Doesnt work
		// draw background
		g.setColor(new Color(99, 143, 66));
		g.fillRect(0, 0, screenWidth, screenHeight);
		/**
		 * Only drawing relative to controller (1)
		 */

		// How to send entities of other controllers to each controller to draw
		// relatively.
		// plug in: Graphics, Controllers, Trees, This controller number.
		// Compress the units of every controller and plug it in.

		drawUnits(g);

		// conts.get(1).drawAll(g, compressedU, treeInfo, 1);
	}

	private void drawCircleRel(Graphics g, Color color, float[] circLoc,
			float radius) {
		g.setColor(color);
		float deltax = cameraLoc[0];
		float deltay = cameraLoc[1];
		g.drawOval((int) (circLoc[0] - radius - deltax + .5f),
				(int) (circLoc[1] - radius + deltay + .5f), (int) (radius * 2),
				(int) (radius * 2));
	}

	private void drawCircleRel(Graphics g, Color color, float circX,
			float circY, float radius) {
		g.setColor(color);
		float deltax = cameraLoc[0];
		float deltay = cameraLoc[1];
		g.drawOval((int) (circX - radius - deltax + .5f), (int) (circY - radius
				- deltay + .5f), (int) (radius * 2), (int) (radius * 2));
	}

	private void fillCircleRel(Graphics g, Color color, float circX,
			float circY, float radius) {
		g.setColor(color);
		float deltax = cameraLoc[0];
		float deltay = cameraLoc[1];
		g.fillOval((int) (circX - radius - deltax + .5f), (int) (circY - radius
				- deltay + .5f), (int) (radius * 2), (int) (radius * 2));
	}

}
