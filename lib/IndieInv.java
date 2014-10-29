package FallenFeather.lib;

import java.awt.Color;
import java.awt.Graphics;

import FallenFeather.Panel;

public class IndieInv {
	public int x, y, width, height, margin, topMargin, exitXundent,
			exitYindent, exitRadius;
	public boolean closed = false;
	public boolean clickTop = false;

	int invMargin = 6;
	int itemWidth = 32;
	int invColumns;

	public IndieInv(int x, int y, int width, int height, int margin,
			int topMargin, int exitXundent, int exitYindent, int exitRadius) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.margin = margin;
		this.topMargin = topMargin;
		this.exitXundent = exitXundent;
		this.exitYindent = exitYindent;
		this.exitRadius = exitRadius;
		invColumns = (width - 2 * margin) / (invMargin + itemWidth);
		// the -invMargin is to make sure there is proper margin on the right
		// side.
		// invColumns = (width - 2 * margin - invMargin) / (invMargin +
		// itemWidth);
	}

	public IndieInv(int x, int y, int width, int height, int margin,
			int topMargin, int exitXundent, int exitYindent, int exitRadius,
			boolean closed) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.margin = margin;
		this.topMargin = topMargin;
		this.exitXundent = exitXundent;
		this.exitYindent = exitYindent;
		this.exitRadius = exitRadius;
		this.closed = closed;
		invColumns = (width - 2 * margin) / (invMargin + itemWidth);
	}

	public void draw(Graphics g, int[][] inv) {
		if (!closed) {
			// draws out line
			g.setColor(Color.DARK_GRAY);
			g.fillRoundRect(x, y, width, height, 12, 12);
			// draws inner panel
			g.setColor(Color.WHITE);
			g.fillRoundRect(x + margin, y + margin + topMargin, width - 2
					* margin, height - 2 * margin - topMargin, 12, 12);
			// draw an exit button. top right.
			g.setColor(Color.RED);
			g.fillOval(x + width - exitXundent, y + exitYindent,
					exitRadius * 2, exitRadius * 2);
			drawInv(g, inv);
		}
	}

	public boolean clickHandle(int mouseX, int mouseY) {
		// checks area for click, if so carry one and return true, else return
		// false;
		if (lapCheck(mouseX, mouseY)) {
			// first check to see if user has clicked on the exit button.
			float hype = (float) Math.sqrt(Math.pow(
					(x + width - exitXundent + exitRadius) - mouseX, 2)
					+ Math.pow((y + exitYindent + exitRadius) - mouseY, 2));
			if (hype <= exitRadius) {
				System.out.println("EXIT");
				closed = true;
			}

			// sees if the button press is on the top bar of the Sleak
			if (mouseY - y >= 0 && mouseY - (y + topMargin) <= 2
					&& mouseX - x >= 0 && mouseX - (x + width) <= 0) {
				clickTop = true;
				System.out.println("CTTTT");
			} else {
				clickTop = false;
			}
			return true;
		} else {
			return false;
		}
	}

	public boolean lapCheck(int mouseX, int mouseY) {
		if (!closed) {
			if (mouseX - x >= 0 && mouseX - x - width <= 0 && mouseY - y >= 0
					&& mouseY - y - height <= 0) {
				// overlap
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	void drawInv(Graphics g, int[][] inv) {
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
				g.drawImage(Panel.imageAr[0], drawX, drawY, null);
			}

			column++;
		}
	}
}
