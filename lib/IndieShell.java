package FallenFeather.lib;

import java.awt.Color;
import java.awt.Graphics;

import FallenFeather.Panel;

public class IndieShell {
	public int margin, topMargin, exitXundent, exitYindent, exitRadius;

	public IndieShell(int margin, int topMargin, int exitXundent,
			int exitYindent, int exitRadius) {
		this.margin = margin;
		this.topMargin = topMargin;
		this.exitXundent = exitXundent;
		this.exitYindent = exitYindent;
		this.exitRadius = exitRadius;

		// the -invMargin is to make sure there is proper margin on the right
		// side.
		// invColumns = (width - 2 * margin - invMargin) / (invMargin +
		// itemWidth);
	}

	public void draw(Graphics g, int[] invInfo) {
		int x = invInfo[0];
		int y = invInfo[1];
		int width = invInfo[2];
		int height = invInfo[3];
		// draws out line
		g.setColor(Color.DARK_GRAY);
		g.fillRoundRect(x, y, width, height, 12, 12);
		// draws inner panel
		g.setColor(Color.WHITE);
		g.fillRoundRect(x + margin, y + margin + topMargin, width - 2 * margin,
				height - 2 * margin - topMargin, 12, 12);
		// draw an exit button. top right.
		g.setColor(Color.RED);
		g.fillOval(x + width - exitXundent, y + exitYindent, exitRadius * 2,
				exitRadius * 2);
	}

	public boolean lapCheck(int mouseX, int mouseY, int[] panelInfo) {
		if (mouseX - panelInfo[0] >= 0
				&& mouseX - panelInfo[0] - panelInfo[2] <= 0
				&& mouseY - panelInfo[1] >= 0
				&& mouseY - panelInfo[1] - panelInfo[3] <= 0) {
			// overlap
			return true;
		} else {
			return false;
		}
	}

	public int topCheck(int mouseX, int mouseY, int[] panelInfo) {
		// 0 for nothing
		// 1 for move
		// 2 for close
		if (mouseY > panelInfo[1]) {
			if (mouseY - 2 < panelInfo[1] + topMargin) {
				float hype = (float) Math.sqrt(Math.pow((panelInfo[0]
						+ panelInfo[2] - exitXundent + exitRadius)
						- (mouseX - 1), 2)
						+ Math.pow((panelInfo[1] + exitYindent + exitRadius)
								- (mouseY - 2), 2));
				if (hype <= exitRadius) {
					return 2;
				}
				return 1;
			}
		}

		return 0;
	}

	void drawInv(Graphics g, int[][] inv, int x, int y) {
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
}
