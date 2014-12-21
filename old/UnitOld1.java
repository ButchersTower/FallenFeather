package FallenFeather;

import java.awt.Color;
import java.awt.Graphics;

import FallenFeather.lib.IndieShell;
import FallenFeather.lib.JaMa;
import FallenFeather.lib.Vect2d;

public class UnitOld {
	private float[] loc;
	private float radius;
	private float speed;
	private Color color;

	private boolean selected;

	private float playSpeedLeft;

	/**
	 * Inventory variables.
	 */

	// 12 inventory slots
	private int[][] inv = new int[12][2];

	private int topMargin = 12;
	private int margin = 4;

	private int invWidth = 240;
	private int invHeight = 300;

	// ( x, y, width, height, closed/open, clickTop
	private int[] invInfo = { 700 - 40 - invWidth, 20, invWidth, invHeight, 0,
			0 };

	private IndieShell shell = new IndieShell(margin, topMargin, 18, 2, 6);

	private int invMargin = 6;
	private int itemWidth = 32;

	private int invColumns = (invWidth - 2 * margin) / (margin + itemWidth);

	/**
	 * Pathing variables
	 */

	private float[] path;
	private boolean moving;

	public UnitOld(float[] loc, float radius, float speed, Color color) {
		this.loc = loc;
		this.radius = radius;
		this.speed = speed;
		this.color = color;
		selected = false;
		moving = false;
		path = new float[0];
	}

	public void addItem(int item) {
		// use the next available slot.
		loopa: for (int i = 0; i < inv.length; i++) {
			if (inv[i][0] == 0) {
				inv[i][0] = item;
				inv[i][1] = 1;
				break loopa;
			}
		}
	}

	public void addItem(int item, int quantity) {
		// See if the quantity causes it to stack over. If so keep going to fill
		// the next available box.
		// use the next available slot.
		loopa: for (int i = 0; i < inv.length; i++) {
			if (inv[i][0] == 0) {
				inv[i][0] = item;
				inv[i][1] = quantity;
				break loopa;
			}
		}
	}

	public void addItem(int item, int quantity, int slot) {
		// if slot is full then use the next available slot.
		if (inv[slot][0] == 0) {
			inv[slot][0] = item;
			inv[slot][1] = quantity;
		} else {
			loopa: for (int i = 0; i < inv.length; i++) {
				if (inv[i][0] == 0) {
					inv[i][0] = item;
					inv[i][1] = quantity;
					break loopa;
				}
			}
		}
	}

	public boolean clickHandle(int mouseX, int mouseY) {
		// Checks for clicking on any panels and handles them appropriately.

		// checks area for click, if so carry one and return true, else return
		// false;
		if (lapCheck(mouseX, mouseY)) {
			// first check to see if user has clicked on the exit button.
			float hype = (float) Math.sqrt(Math.pow((invInfo[0] + invInfo[2]
					- shell.exitXundent + shell.exitRadius)
					- mouseX, 2)
					+ Math.pow(
							(invInfo[1] + shell.exitYindent + shell.exitRadius)
									- mouseY, 2));
			if (hype <= shell.exitRadius) {
				System.out.println("EXIT");
				invInfo[4] = 0;
			}

			// sees if the button press is on the top bar of the Sleak
			if (mouseY - invInfo[1] >= 0
					&& mouseY - (invInfo[1] + topMargin) <= 2
					&& mouseX - invInfo[0] >= 0
					&& mouseX - (invInfo[0] + invInfo[2]) <= 0) {
				// clickTop = true;
				invInfo[5] = 1;
			} else {
				// clickTop = false;
				invInfo[5] = 0;
			}
			return true;
		} else {
			return false;
		}
	}

	public boolean lapCheck(int mouseX, int mouseY) {
		// Checks to see if the click is on any panel.
		// if (!closed) {
		if (invInfo[4] == 1) {
			if (mouseX - invInfo[0] >= 0
					&& mouseX - invInfo[0] - invInfo[2] <= 0
					&& mouseY - invInfo[1] >= 0
					&& mouseY - invInfo[1] - invInfo[3] <= 0) {
				// overlap
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public boolean overlap(float clickx, float clicky) {
		// Checks overlap on the unit itself.
		if (Math.hypot(clickx - loc[0], clicky - loc[1]) <= radius) {
			return true;
		}
		return false;
	}

	public void moveInvLoc(int deltaX, int deltaY) {
		invInfo[0] += deltaX;
		invInfo[1] += deltaY;
	}

	/**
	 * Pathing
	 */

	float[][] direction;
	// int tarX;
	// int tarY;
	boolean pathing;
	int lowest = 0;

	void playMoveWhole(int tarX, int tarY) {
		// Part of setting the units path.
		// Plus thea from player should get sub thea from tar.

		direction = new float[0][];
		// get delta vector. scale to moveSpeed.
		float[] deltaVect = { tarX - loc[0], tarY - loc[1] };

		if (JaMa.distPointToVect(
				Vect2d.vectSub(
						new float[] { Panel.getTreeInfo()[0],
								Panel.getTreeInfo()[1] }, new float[] { loc[0],
								loc[1] }), deltaVect) < Panel.getTreeInfo()[2]
				+ radius) {
			// if the target point was inside of a tree then project it out.
			float[] tarRelTree = new float[] { tarX - Panel.getTreeInfo()[0],
					tarY - Panel.getTreeInfo()[1] };
			if (Vect2d.norm(tarRelTree) <= radius + Panel.getTreeInfo()[2]) {
				// add small num to bypass rounding mistakes.
				// float[] pushedTar = Vect2d.theaToPoint(
				// Vect2d.pointToThea(tarRelTree), radius +
				// Panel.getTreeInfo()[2]
				// + smallNum);
				/**
				 * Instead of pushing it out pick the closest point between play
				 * and tree.
				 */
				// Panel.getTreeInfo().l is 3 but Ve2d only reads the first two
				// increments.
				float[] treeRelPlay = Vect2d.vectSub(Panel.getTreeInfo(),
						new float[] { loc[0], loc[1] });
				// deltaVect scaled down by plaR + treR.
				float ta = Vect2d.norm(treeRelPlay);
				System.out.println("b4 ta: " + ta);
				treeRelPlay = Vect2d.vectMultScalar(
						(ta - (radius + Panel.getTreeInfo()[2])) / ta,
						treeRelPlay);
				ta = Vect2d.norm(treeRelPlay);
				System.out.println("cd ta: " + ta);
				// tarX = loc[0] + treeRelPlay[0];
				// tarY = loc[1] + treeRelPlay[1];
				// tarX = Panel.getTreeInfo()[0] + pushedTar[0];
				// tarY = Panel.getTreeInfo()[1] + pushedTar[1];
				// playMoveWhole();
				// direction = JaMa.appendFloatArAr(direction, new float[] { 0,
				// treeRelPlay[0], treeRelPlay[1], ta });
				path = new float[] { 0, treeRelPlay[0] / ta,
						treeRelPlay[1] / ta, ta };
				// g.setColor(Color.ORANGE);
				// g.drawOval((int) (loc[0] + path[0]) - 2, (int) (loc[1] +
				// path[1]) - 2, 4, 4);
				pathing = true;
				return;
			}
			// cant move
			// moving = false;
			float[] tangents = JaMa.myAngleThing(
					new float[] { loc[0], loc[1] }, radius,
					new float[] { Panel.getTreeInfo()[0],
							Panel.getTreeInfo()[1] }, Panel.getTreeInfo()[2]);
			path = new float[0];
			direction = JaMa.appendFloatArAr(direction, new float[] { 0,
					tangents[0], tangents[1], tangents[6] });
			direction = JaMa.appendFloatArAr(direction, new float[] { 0,
					tangents[3], tangents[4], tangents[6] });

			// tangents from play
			direction[0] = JaMa.appendArFloatAr(direction[0], new float[] { 1,
					tangents[2] });
			System.out.println("tangents[2]: " + tangents[2]);
			System.out.println("tangents[5]: " + tangents[5]);
			direction[1] = JaMa.appendArFloatAr(direction[1], new float[] { 1,
					tangents[5] });

			tangents = JaMa.myAngleThing(new float[] { tarX, tarY }, radius,
					new float[] { Panel.getTreeInfo()[0],
							Panel.getTreeInfo()[1] }, Panel.getTreeInfo()[2]);
			System.out.println("tangents[2]: " + tangents[2]);
			System.out.println("tangents[5]: " + tangents[5]);
			// tangents from tar
			// plusThea from player should get subThea from tar.
			direction[0] = JaMa.appendArFloatAr(direction[0], new float[] {
					tangents[5], radius + Panel.getTreeInfo()[2] });
			direction[1] = JaMa.appendArFloatAr(direction[1], new float[] {
					tangents[2], radius + Panel.getTreeInfo()[2] });

			direction[0] = JaMa.appendArFloatAr(direction[0], new float[] { 0,
					-tangents[3], -tangents[4], tangents[6] });
			direction[1] = JaMa.appendArFloatAr(direction[1], new float[] { 0,
					-tangents[0], -tangents[1], tangents[6] });
			System.out.println("direction[0][" + 9 + "]: " + direction[0][9]);
			System.out.println("direction[0][" + 10 + "]: " + direction[0][10]);
			System.out.println("direction[1][" + 9 + "]: " + direction[1][9]);
			System.out.println("direction[1][" + 10 + "]: " + direction[1][10]);

			System.out.println("makePath");
			sortDirections();
			for (int d = 0; d < direction[lowest].length; d++) {
				System.out.println("direction[" + d + "]: "
						+ direction[lowest][d]);
			}
			path = direction[lowest];
			pathing = true;
		} else {
			float deltaVecta = Vect2d.norm(deltaVect);
			deltaVect = Vect2d.normalize(deltaVect);
			path = new float[] { 0, deltaVect[0], deltaVect[1], deltaVecta };
			pathing = true;
		}
	}

	void followPath() {
		playSpeedLeft = speed;
		while (path.length > 0 && playSpeedLeft > 0) {
			sortPath();
		}
		// System.out.println("path.length: " + path.length);
		if (path.length == 0) {
			pathing = false;
			// System.out.println("pathing: " + pathing);
		}
		moving = false;
	}

	void sortPath() {
		if (path[0] == 0) {
			// linear so go straight
			if (path[3] > playSpeedLeft) {
				loc[0] += path[1] * playSpeedLeft;
				loc[1] += path[2] * playSpeedLeft;
				path[3] -= playSpeedLeft;
				playSpeedLeft = 0;
			} else {
				loc[0] += path[1] * path[3];
				loc[1] += path[2] * path[3];
				playSpeedLeft -= path[3];
				// delete the four. and move on.
				path = JaMa.removeFirstFloatAr(path, 4);
			}
		} else if (path[0] == 1) {
			System.out.println("path 1");
			// around edge
			float edgeLength = Math.abs(Vect2d.theaSub(path[1], path[2])
					* path[3]);
			System.out.println("edgeLength: " + edgeLength);
			if (edgeLength > playSpeedLeft) {
				// figure out if plus thea is closer or minus thea, then move
				// accordingly inorder to fuffil moveSpeedLeft.
				float possableThea = playSpeedLeft / path[3];
				System.out.println("possableThea: " + possableThea);
				float newThea;
				if (lowest == 1) {
					newThea = Vect2d.theaAdd(path[1], possableThea);
					// add thea
				} else {
					newThea = Vect2d.theaSub(path[1], possableThea);
					// sub thea
				}
				path[1] = newThea;
				float[] newLoc = Vect2d.theaToPoint(newThea, path[3]);
				// g.setColor(Color.MAGENTA);
				// g.drawOval((int) (Panel.getTreeInfo()[0] + newLoc[0]) - 3,
				// (int) (Panel.getTreeInfo()[1] + newLoc[1]) - 3, 6, 6);
				loc[0] = Panel.getTreeInfo()[0] + newLoc[0];
				loc[1] = Panel.getTreeInfo()[1] + newLoc[1];
				System.out.println("loc[0]: " + loc[0] + ",   loc[1]: "
						+ loc[1]);
				// pathing = false;
				playSpeedLeft = 0;
			} else {
				float[] newLoc = Vect2d.theaToPoint(path[2], path[3]);
				loc[0] = Panel.getTreeInfo()[0] + newLoc[0];
				loc[1] = Panel.getTreeInfo()[1] + newLoc[1];
				path = JaMa.removeFirstFloatAr(path, 4);
				playSpeedLeft -= edgeLength;
			}
		}
	}

	void sortDirections() {
		// get the shortest path

		float[] sums = new float[direction.length];
		for (int d = 0; d < direction.length; d++) {
			for (int i = 0; i < direction[d].length / 4; i++) {
				if (direction[d][i * 4] == 0) {
					sums[d] += direction[d][i * 4 + 3];
				} else {
					sums[d] += Math.abs(Vect2d.theaSub(direction[d][i * 4 + 1],
							direction[d][i * 4 + 2]) * direction[d][i * 4 + 3]);
				}
			}
		}
		// find the lowest sum and follow that.
		lowest = 0;
		// System.out.println("sums[" + 0 + "]: " + sums[0]);
		for (int s = 1; s < sums.length; s++) {
			// System.out.println("sums[" + s + "]: " + sums[s]);
			if (sums[s] < sums[lowest]) {
				lowest = s;
			}
		}
		// System.out.println("lowest: " + lowest);
	}

	/**
	 * Drawing
	 */

	public void drawInv(Graphics g) {
		shell.draw(g, invInfo);

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
			int drawX = invInfo[0] + margin + invMargin + column
					* (invMargin + itemWidth);
			int drawY = invInfo[1] + margin + topMargin + invMargin + row
					* (invMargin + itemWidth);
			g.fillRect(drawX, drawY, itemWidth, itemWidth);
			if (inv[i][0] == 1) {
				g.drawImage(Panel.getImageAr()[0], drawX, drawY, null);
			}

			column++;
		}
	}

	/**
	 * Getters
	 */

	public float[] getLoc() {
		return loc;
	}

	public float getRadius() {
		return radius;
	}

	public int[] getInvInfo() {
		return invInfo;
	}

	public float getSpeed() {
		return speed;
	}

	public Color getColor() {
		return color;
	}

	public int[][] getInv() {
		return inv;
	}

	public boolean getMoving() {
		return moving;
	}

	public boolean getSelected() {
		return selected;
	}

	/**
	 * Setters
	 */

	public void setInvLoc(int x, int y) {
		invInfo[0] = x;
		invInfo[1] = y;
	}

	public void setSelected(boolean b) {
		selected = b;
	}
}
