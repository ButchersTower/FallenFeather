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
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public class PanelOld extends JPanel implements Runnable, MouseListener,
		KeyListener, MouseMotionListener {
	/**
	 * is used : Controllers(), Unit()
	 */

	// This parted when implementing multiple units by different inputs.
	// All click and keyboard inputs are for the player1 controller.
	//

	private int width = 700;
	private int height = 450;

	private static Image[] imageAr;

	private Thread thread;
	private Image image;
	private Graphics g;

	// Vars for gLoop Below
	private int ticksPerSecond = 20;
	private int mpt = 1000 / ticksPerSecond;
	private int sleepTime = 0;
	private long lastSec = 0;
	private int ticks = 0;
	private long startTime;
	private long nextTick = 0;
	private boolean running = false;

	// Vars for gLoop Above

	// 0 = computer : 1 = play1 : 2 = play2
	private List<Controllers> conts;

	public PanelOld() {
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

		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);

		startTime = System.currentTimeMillis();
		gStart();
	}

	/**
	 * Methods go below here.
	 */

	public void gStart() {
		initControllers();

		// Give player 1 a character.
		conts.get(1).addUnit(new float[] { 100, 100 }, 20, 12, Color.BLUE);
		// Spawn a tree.

		running = true;
		gLoop();
	}

	public void gLoop() {
		while (running) {

			// Runs once a second and keeps track of ticks;
			// 1000 ms since last output
			if (timer() - lastSec > 1000) {
				if (ticks < ticksPerSecond - 1 || ticks > ticksPerSecond + 1) {
					if (timer() - startTime < 2000) {
						System.out.println("Ticks this second: " + ticks);
						System.out.println("timer(): " + timer());
						System.out.println("nextTick: " + nextTick);
					}
				}
				ticks = 0;
				lastSec = (System.currentTimeMillis() - startTime);
			}

			/**
			 * Do the things you want the gLoop to do below here
			 */

			// System.out.println("runTime: " + timer());

			for (int c = 0; c < conts.size(); c++) {
				conts.get(c).tic();
			}
			drawScene();

			/**
			 * And above here.
			 */
			drwGm();

			ticks++;

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

	// [0] = x
	// [1] = y
	// [2] = radius
	// [3] = height
	private static float[] treeInfo = { 120, 150, 12, 160 };

	// DRAW EVERYTHING RELATIVE TO CONTROLLER (1)
	public void drawScene() {
		// draw background
		g.setColor(new Color(99, 143, 66));
		g.fillRect(0, 0, width, height);
		/**
		 * Only drawing relative to controller (1)
		 */

		// How to send entities of other controllers to each controller to draw
		// relatively.
		// plug in: Graphics, Controllers, Trees, This controller number.
		// Compress the units of every controller and plug it in.
		float[][][] compressedU = new float[conts.size()][][];
		for (int c = 0; c < conts.size(); c++) {
			compressedU[c] = conts.get(c).compressUnits();
		}
		conts.get(1).drawAll(g, compressedU, treeInfo, 1);

	}

	/**
	 * And above here
	 */

	public long timer() {
		return System.currentTimeMillis() - startTime;

	}

	public void drwGm() {
		Graphics g2 = this.getGraphics();
		g2.drawImage(image, 0, 0, null);
		g2.dispose();
	}

	/**
	 * Initiations
	 */

	void initControllers() {
		conts = new ArrayList<Controllers>(3);
		conts.add(new Controllers(0, 0));
		conts.add(new Controllers(0, 0));
		conts.add(new Controllers(0, 0));
	}

	/**
	 * Drawing
	 */

	/**
	 * Getters
	 */

	static float[] getTreeInfo() {
		return treeInfo;
	}

	static Image[] getImageAr() {
		return imageAr;
	}

	/**
	 * Inputs
	 */

	@Override
	public void keyPressed(KeyEvent ke) {
		if (ke.getKeyCode() == KeyEvent.VK_A) {
			conts.get(1).addUnit(new float[] { 300, 300 }, 24, 14, Color.BLUE);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent me) {
		// Pass to controller to handle.
		conts.get(1).mousePress(me.getX(), me.getY(), me.getButton());
	}

	@Override
	public void mouseReleased(MouseEvent me) {
		conts.get(1).mouseRelease(me.getX(), me.getY(), me.getButton());
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(MouseEvent me) {
		conts.get(1).mouseDrag(me.getX(), me.getY(), me.getButton());
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
