package FallenFeather;

import java.awt.Color;
import java.awt.Graphics;

public class Tree {
	private float[] loc;
	private float radius;
	private float height;

	public Tree(float[] loc, float radius, float height) {
		this.loc = loc;
		this.radius = radius;
		this.height = height;
	}

	public boolean overlap(int clickx, int clicky) {
		if (Math.hypot(clickx - loc[0], clicky - loc[1]) <= radius) {
			return true;
		}
		return false;
	}

	public void draw(Graphics g) {
		fillCircleRel(g, new Color(66, 33, 00), loc[0], loc[1], radius);
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
