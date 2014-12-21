package FallenFeather;

import java.awt.Color;
import java.awt.Graphics;

import FallenFeather.lib.IndieShell;
import FallenFeather.lib.JaMa;
import FallenFeather.lib.Vect2d;

public class UnitOld2 {
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

	/**
	 * Pathing variables
	 */

	private float[] path;
	private boolean moving;
	Pathfind find = new Pathfind(Panel.getTreeInfo());

	public UnitOld2(float[] loc, float radius, float speed, Color color) {
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

	void playMoveWhole(int tarX, int tarY) {
		System.out.println("tohe");
		path = find.magic(loc, radius, new int[] { tarX, tarY });
		moving = true;
	}

	// void followPath() {
	// playSpeedLeft = speed;
	// while (path.length > 0 && playSpeedLeft > 0) {
	// followPathParts();
	// }
	// // System.out.println("path.length: " + path.length);
	// if (path.length == 0) {
	// pathing = false;
	// // System.out.println("pathing: " + pathing);
	// }
	// moving = false;
	// }
	//
	// void followPathParts() {
	// if (path[0] == 0) {
	// // linear so go straight
	// if (path[3] > playSpeedLeft) {
	// loc[0] += path[1] * playSpeedLeft;
	// loc[1] += path[2] * playSpeedLeft;
	// path[3] -= playSpeedLeft;
	// playSpeedLeft = 0;
	// } else {
	// loc[0] += path[1] * path[3];
	// loc[1] += path[2] * path[3];
	// playSpeedLeft -= path[3];
	// path = JaMa.removeFirstFloatAr(path, 4);
	// }
	// } else if (path[0] == 1) {
	// // around edge
	// float edgeLength = Math.abs(Vect2d.theaSub(path[1], path[2])
	// * path[3]);
	// if (edgeLength > playSpeedLeft) {
	// // figure out if plus thea is closer or minus thea, then move
	// // accordingly inorder to fuffil moveSpeedLeft.
	// float possableThea = playSpeedLeft / path[3];
	// float newThea;
	// if (Vect2d.theaSub(path[1], path[2]) < 0) {
	// newThea = Vect2d.theaAdd(path[1], possableThea);
	// } else {
	// newThea = Vect2d.theaSub(path[1], possableThea);
	// }
	// path[1] = newThea;
	// float[] newLoc = Vect2d.theaToPoint(newThea, path[3]);
	// loc[0] = Panel.getTreeInfo()[0] + newLoc[0];
	// loc[1] = Panel.getTreeInfo()[1] + newLoc[1];
	// playSpeedLeft = 0;
	// } else {
	// float[] newLoc = Vect2d.theaToPoint(path[2], path[3]);
	// loc[0] = Panel.getTreeInfo()[0] + newLoc[0];
	// loc[1] = Panel.getTreeInfo()[1] + newLoc[1];
	// path = JaMa.removeFirstFloatAr(path, 4);
	// playSpeedLeft -= edgeLength;
	// }
	// }
	// }

	void followPath() {
		playSpeedLeft = speed;
		while (path.length > 0 && playSpeedLeft > 0) {
			sortFollowPath();
		}
		if (path.length == 0) {
			moving = false;
		}
	}

	private void sortFollowPath() {
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
				path = JaMa.removeFirstFloatAr(path, 4);
			}
		} else if (path[0] == 1) {
			// around edge
			float edgeLength = Math.abs(Vect2d.theaSub(path[1], path[2])
					* path[3]);
			if (edgeLength > playSpeedLeft) {
				// figure out if plus thea is closer or minus thea, then move
				// accordingly in order to fulfill moveSpeedLeft.
				float possableThea = playSpeedLeft / path[3];
				float newThea;
				if (Vect2d.theaSub(path[1], path[2]) < 0) {
					newThea = Vect2d.theaAdd(path[1], possableThea);
				} else {
					newThea = Vect2d.theaSub(path[1], possableThea);
				}
				path[1] = newThea;
				float[] newLoc = Vect2d.theaToPoint(newThea, path[3]);
				loc[0] = Panel.getTreeInfo()[(int) path[4]][0] + newLoc[0];
				loc[1] = Panel.getTreeInfo()[(int) path[4]][1] + newLoc[1];
				playSpeedLeft = 0;
			} else {
				float[] newLoc = Vect2d.theaToPoint(path[2], path[3]);
				loc[0] = Panel.getTreeInfo()[(int) path[4]][0] + newLoc[0];
				loc[1] = Panel.getTreeInfo()[(int) path[4]][1] + newLoc[1];
				path = JaMa.removeFirstFloatAr(path, 5);
				playSpeedLeft -= edgeLength;
			}
		}
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
