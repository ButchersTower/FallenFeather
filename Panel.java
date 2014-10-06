package FallenFeather;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class Panel extends JPanel implements Runnable, MouseListener,
		KeyListener {
	// This is going to be

	int width = 700;
	int height = 450;

	Image[] imageAr;

	Thread thread;
	Image image;
	Graphics g;

	// Vars for gLoop Below
	int tps = 10;
	int mpt = 1000 / tps;
	long lastTick = 0;
	int sleepTime = 0;
	long lastSec = 0;
	int ticks = 0;
	long startTime;
	long runTime;
	long nextTick = 0;
	boolean running = false;

	// Vars for gLoop Above

	public Panel() {
		super();

		setPreferredSize(new Dimension(width, height));
		setFocusable(true);
		requestFocus();
	}

	public void addNotify() {
		super.addNotify();
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}

	public void run() {
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		g = (Graphics2D) image.getGraphics();
		this.setSize(new Dimension(width, height));

		addMouseListener(this);
		addKeyListener(this);

		startTime = System.currentTimeMillis();
		gStart();
	}

	/**
	 * Methods go below here.
	 * 
	 */

	public void gStart() {
		imageInit();

		running = true;
		gLoop();
	}

	public void gLoop() {
		while (running) {

			// Do the things you want the gLoop to do below here

			// System.out.println("runTime: " + timer());

			// And above here.
			drwGm(g);

			ticks++;

			// Runs once a second and keeps track of ticks;
			// 1000 ms since last output
			if (timer() - lastSec > 1000) {
				if (ticks < tps - 1 || ticks > tps + 1) {
					if (timer() - startTime < 2000) {
						System.out.println("Ticks this second: " + ticks);
						System.out.println("timer(): " + timer());
						System.out.println("nextTick: " + nextTick);
					}
				}

				ticks = 0;
				lastSec = (System.currentTimeMillis() - startTime);
			}

			// Limits the ticks per second

			// if nextTick is later then timer then sleep till next tick
			// System.out.println("nextTick: " + nextTick);
			// System.out.println("timer: " + timer());
			if (nextTick > timer()) {
				sleepTime = (int) (nextTick - timer());
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
				}

				nextTick += mpt;
			}

			// if next tick is past the current time then don't sleep and add
			// time so it runs a tick after adding.
			if (nextTick < timer()) {
				nextTick += mpt;
			}

			// if the game is more than 2 seconds behind it updates the ticks.
			if (nextTick + 2000 < timer()) {
				nextTick = timer() + mpt;
			}
		}
	}

	void renderGame() {
		g.setColor(Color.ORANGE);
		g.drawRect(40, 50, 20, 10);
		g.drawRect(80, 40, 10, 20);
		g.setColor(Color.BLUE);
		g.drawOval(60, 80, 40, 40);
	}

	/**
	 * Methods go above here.
	 * 
	 */

	public long timer() {
		return System.currentTimeMillis() - startTime;

	}

	public void drwGm(Graphics g) {
		Graphics g2 = this.getGraphics();
		g2.drawImage(image, 0, 0, null);
		g2.dispose();
	}

	public void imageInit() {

		// imageAr = new Image[1];
		// ImageIcon ie = new ImageIcon(this.getClass().getResource(
		// "res/image.png"));
		// imageAr[0] = ie.getImage();

	}

	@Override
	public void mousePressed(MouseEvent me) {
		float[][] seg1 = { { 2, 1 }, { 4, 4 } };
		float[][] seg2 = { { 1, 4 }, { 2, 3 } };
		JaMa.distSegmenttoSegment(seg1, seg2);

		renderGame();
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}
}
