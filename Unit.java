package FallenFeather;

import java.awt.Color;
import java.awt.Graphics;

import FallenFeather.lib.IndieShell;

public class Unit {
	private float[] loc;
	private float radius;
	private float speed;
	private Color color;

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

	public Unit(float[] loc, float radius, float speed, Color color) {
		this.loc = loc;
		this.radius = radius;
		this.speed = speed;
		this.color = color;
	}

	void addItem(int item) {
		// use the next available slot.
		loopa: for (int i = 0; i < inv.length; i++) {
			if (inv[i][0] == 0) {
				inv[i][0] = item;
				inv[i][1] = 1;
				break loopa;
			}
		}
	}

	void addItem(int item, int quantity) {
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

	void addItem(int item, int quantity, int slot) {
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

	float[] getLoc() {
		return loc;
	}

	float getRadius() {
		return radius;
	}

	int[] getInvInfo() {
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

	void drawInv(Graphics g) {
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
				g.drawImage(Panel.imageAr[0], drawX, drawY, null);
			}

			column++;
		}
	}

	void setInvLoc(int x, int y) {
		invInfo[0] = x;
		invInfo[1] = y;
	}

	void moveInvLoc(int deltaX, int deltaY) {
		invInfo[0] += deltaX;
		invInfo[1] += deltaY;
	}

}
