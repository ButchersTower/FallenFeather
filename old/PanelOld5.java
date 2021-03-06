package FallenFeather.old;

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

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import FallenFeather.lib.Vect2d;
import FallenFeather.lib.JaMa;

import FallenFeather.UnitOld;

public class PanelOld5 extends JPanel implements Runnable, MouseListener,
		KeyListener, MouseMotionListener {
	// This is going to be

	int width = 700;
	int height = 450;

	public static Image[] imageAr;

	Thread thread;
	Image image;
	Graphics g;

	// Vars for gLoop Below
	int tps = 20;
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

	float tarX = 0;
	float tarY = 0;

	// [0] = x
	// [1] = y
	// [2] = radius
	float[] treeInfo = { 120, 150, 12 };

	float[][] direction = new float[0][0];
	int pathPart = 0;

	int lowest = 0;
	float playSpeedLeft;
	float[] path = new float[0];
	float[] myPath;
	boolean setPath = false;
	boolean moving = false;
	boolean pathing = false;

	// top left corner of camera.
	float[] cameraLoc = { 0, 0 };

	// player selected.
	boolean playSel = false;

	UnitOld play;

	int[] mouseLast = new int[2];

	boolean b1press = false;
	boolean shiftP = false;

	String[] items = { "Axe", "Plank" };

	public PanelOld5() {
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
	 * 
	 */

	public void gStart() {
		imageInit();

		initPlayer();

		running = true;
		gLoop();
	}

	public void gLoop() {
		while (running) {

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

			/**
			 * Do the things you want the gLoop to do below here
			 */

			// System.out.println("runTime: " + timer());

			// Mouse Handle
			// for (int m = 0; m < mouseQ.size(); m++) {
			while (mousePQ.size() > 0) {
				mouseP(mousePQ.get(0));
				mousePQ.remove(0);
			}
			// }

			if (mouseD) {
				mouseD();
				mouseD = false;
			}

			while (mouseRQ.size() > 0) {
				mouseR(mouseRQ.get(0));
				mouseRQ.remove(0);
			}

			if (setPath) {
				playMoveWhole();
				setPath = false;
			}
			if (pathing) {
				// System.out.println("pathing");
				followPath();
				// draw = true;
			}
			// if (draw) {
			draw();
			drwGm();
			// }

			/**
			 * And above here.
			 */

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

	ArrayList<int[]> mousePQ = new ArrayList<int[]>();
	ArrayList<int[]> mouseRQ = new ArrayList<int[]>();

	// if the last click was on a panel.
	boolean clickedOnPanel = false;

	boolean charInfo = false;
	float[] infoLoc = { 400, 80, 200, 300 };
	boolean mouseD = false;
	boolean justDragged = false;

	int lastButton = -1;
	int[] dragLoc = new int[2];

	int[] lastDragLoc = new int[2];

	void mouseP(int[] mo) {
		clickedOnPanel = false;
		justDragged = false;
		float[] mouseRel = { cameraLoc[0] + mo[0], cameraLoc[1] + mo[1] };
		// Vect2d.sayVect("mouseRel", mouseRel);

		// mouseLast[0] = mo[0];
		// mouseLast[1] = mo[1];

		if (mo[2] == MouseEvent.BUTTON3) {
			// If right click on tree then do an axe check and go there and
			// start cutting trees once there.
			lastButton = 3;
			// If you are right clicking on a panel then don't move.
			if (!play.lapCheck(mo[0], mo[1])) {
				// If your unit is not selected then dont move it.
				if (playSel) {
					setPath = true;
					tarX = mouseRel[0];
					tarY = mouseRel[1];
				}
			}
		} else if (mo[2] == MouseEvent.BUTTON1) {
			lastButton = 1;
			boolean panelTouch = false;
			if (play.getInvInfo()[4] == 1) {
				if (play.clickHandle(mo[0], mo[1])) {
					panelTouch = true;
				}
			}
			if (panelTouch) {
				clickedOnPanel = true;
			} else {
				b1press = true;
			}
		}
	}

	void mouseD() {
		justDragged = true;
		if (lastButton == 1) {
			int[] delta = { dragLoc[0] - lastDragLoc[0],
					dragLoc[1] - lastDragLoc[1] };
			if (b1press) {
				cameraLoc[0] -= delta[0];
				cameraLoc[1] -= delta[1];
			} else {
				if (play.getInvInfo()[5] == 1) {
					play.moveInvLoc(delta[0], delta[1]);
				}
			}
		} else if (lastButton == 3) {
			// If you are right clicking on a panel then don't move.
			// if (!panels[0].lapCheck(dragLoc[0], dragLoc[1])) {
			if (!play.lapCheck(dragLoc[0], dragLoc[1])) {
				// If your unit is not selected then dont move it.
				if (playSel) {
					setPath = true;
					tarX = cameraLoc[0] + dragLoc[0];
					tarY = cameraLoc[1] + dragLoc[1];
				}
			}
		}
		lastDragLoc = dragLoc.clone();
	}

	void mouseR(int[] mo) {
		if (mo[2] == MouseEvent.BUTTON1) {
			if (!justDragged && !clickedOnPanel) {
				entitySel(new float[] { mo[0], mo[1] });
			}
			b1press = false;
		}
	}

	void followPath() {
		playSpeedLeft = play.getSpeed();
		while (myPath.length > 0 && playSpeedLeft > 0) {
			sortPath();
		}
		// System.out.println("myPath.length: " + myPath.length);
		if (myPath.length == 0) {
			pathing = false;
			// System.out.println("pathing: " + pathing);
		}
		moving = false;
	}

	void sortPath() {
		if (myPath[0] == 0) {
			// linear so go straight
			if (myPath[3] > playSpeedLeft) {
				play.getLoc()[0] += myPath[1] * playSpeedLeft;
				play.getLoc()[1] += myPath[2] * playSpeedLeft;
				myPath[3] -= playSpeedLeft;
				playSpeedLeft = 0;
			} else {
				play.getLoc()[0] += myPath[1] * myPath[3];
				play.getLoc()[1] += myPath[2] * myPath[3];
				playSpeedLeft -= myPath[3];
				// delete the four. and move on.
				myPath = JaMa.removeFirstFloatAr(myPath, 4);
			}
		} else if (myPath[0] == 1) {
			System.out.println("path 1");
			// around edge
			float edgeLength = Math.abs(Vect2d.theaSub(myPath[1], myPath[2])
					* myPath[3]);
			System.out.println("edgeLength: " + edgeLength);
			if (edgeLength > playSpeedLeft) {
				// figure out if plus thea is closer or minus thea, then move
				// accordingly inorder to fuffil moveSpeedLeft.
				float possableThea = playSpeedLeft / myPath[3];
				System.out.println("possableThea: " + possableThea);
				float newThea;
				if (lowest == 1) {
					newThea = Vect2d.theaAdd(myPath[1], possableThea);
					// add thea
				} else {
					newThea = Vect2d.theaSub(myPath[1], possableThea);
					// sub thea
				}
				myPath[1] = newThea;
				float[] newLoc = Vect2d.theaToPoint(newThea, myPath[3]);
				g.setColor(Color.MAGENTA);
				g.drawOval((int) (treeInfo[0] + newLoc[0]) - 3,
						(int) (treeInfo[1] + newLoc[1]) - 3, 6, 6);
				play.getLoc()[0] = treeInfo[0] + newLoc[0];
				play.getLoc()[1] = treeInfo[1] + newLoc[1];
				System.out.println("play.getLoc()[0]: " + play.getLoc()[0]
						+ ",   play.getLoc()[1]: " + play.getLoc()[1]);
				// pathing = false;
				playSpeedLeft = 0;
			} else {
				float[] newLoc = Vect2d.theaToPoint(myPath[2], myPath[3]);
				play.getLoc()[0] = treeInfo[0] + newLoc[0];
				play.getLoc()[1] = treeInfo[1] + newLoc[1];
				myPath = JaMa.removeFirstFloatAr(myPath, 4);
				playSpeedLeft -= edgeLength;
			}
		}
	}

	void playMoveWhole() {
		// Plus thea from player should get sub thea from tar.

		direction = new float[0][];
		// get delta vector. scale to moveSpeed.
		float[] deltaVect = { tarX - play.getLoc()[0], tarY - play.getLoc()[1] };

		if (distPointToVect(Vect2d.vectSub(new float[] { treeInfo[0],
				treeInfo[1] },
				new float[] { play.getLoc()[0], play.getLoc()[1] }), deltaVect) < treeInfo[2]
				+ play.getRadius()) {
			// if the target point was inside of a tree then project it out.
			float[] tarRelTree = new float[] { tarX - treeInfo[0],
					tarY - treeInfo[1] };
			if (Vect2d.norm(tarRelTree) <= play.getRadius() + treeInfo[2]) {
				// add small num to bypass rounding mistakes.
				// float[] pushedTar = Vect2d.theaToPoint(
				// Vect2d.pointToThea(tarRelTree), play.getRadius() +
				// treeInfo[2]
				// + smallNum);
				/**
				 * Instead of pushing it out pick the closest point between play
				 * and tree.
				 */
				// treeInfo.l is 3 but Ve2d only reads the first two increments.
				float[] treeRelPlay = Vect2d.vectSub(treeInfo, new float[] {
						play.getLoc()[0], play.getLoc()[1] });
				// deltaVect scaled down by plaR + treR.
				float ta = Vect2d.norm(treeRelPlay);
				System.out.println("b4 ta: " + ta);
				treeRelPlay = Vect2d.vectMultScalar(
						(ta - (play.getRadius() + treeInfo[2])) / ta,
						treeRelPlay);
				ta = Vect2d.norm(treeRelPlay);
				System.out.println("cd ta: " + ta);
				// tarX = play.getLoc()[0] + treeRelPlay[0];
				// tarY = play.getLoc()[1] + treeRelPlay[1];
				// tarX = treeInfo[0] + pushedTar[0];
				// tarY = treeInfo[1] + pushedTar[1];
				// playMoveWhole();
				// direction = JaMa.appendFloatArAr(direction, new float[] { 0,
				// treeRelPlay[0], treeRelPlay[1], ta });
				myPath = new float[] { 0, treeRelPlay[0] / ta,
						treeRelPlay[1] / ta, ta };
				g.setColor(Color.ORANGE);
				g.drawOval((int) (play.getLoc()[0] + myPath[0]) - 2,
						(int) (play.getLoc()[1] + myPath[1]) - 2, 4, 4);
				pathing = true;
				return;
			}
			// cant move
			// moving = false;
			float[] tangents = myAngleThing(new float[] { play.getLoc()[0],
					play.getLoc()[1] }, play.getRadius(), new float[] {
					treeInfo[0], treeInfo[1] }, treeInfo[2]);
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

			tangents = myAngleThing(new float[] { tarX, tarY },
					play.getRadius(), new float[] { treeInfo[0], treeInfo[1] },
					treeInfo[2]);
			System.out.println("tangents[2]: " + tangents[2]);
			System.out.println("tangents[5]: " + tangents[5]);
			// tangents from tar
			// plusThea from player should get subThea from tar.
			direction[0] = JaMa.appendArFloatAr(direction[0], new float[] {
					tangents[5], play.getRadius() + treeInfo[2] });
			direction[1] = JaMa.appendArFloatAr(direction[1], new float[] {
					tangents[2], play.getRadius() + treeInfo[2] });

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
			myPath = direction[lowest];
			pathing = true;
		} else {
			float deltaVecta = Vect2d.norm(deltaVect);
			deltaVect = Vect2d.normalize(deltaVect);
			myPath = new float[] { 0, deltaVect[0], deltaVect[1], deltaVecta };
			pathing = true;
		}
	}

	float distPointToVect(float[] point, float[] vect) {
		// project, is projection scalar is farther than the line then take
		// hypotnuse of closest and edge and point. if the scalar is on the line
		// then reject and that is dist.

		float dist;
		float projScalar = Vect2d.scalarOfProject(point, vect);
		// System.out.println("projScalar: " + projScalar);
		if (projScalar > 1) {
			// get dist from the end of seg.
			dist = Vect2d.norm(Vect2d.vectSub(point, vect));
		} else if (projScalar < 0) {
			// get the dist from the start of seg.
			dist = Vect2d.norm(point);
		} else {
			// dist is point rej proj
			dist = Vect2d.norm(Vect2d.vectSub(point,
					Vect2d.vectMultScalar(projScalar, vect)));
		}
		return dist;
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

	float[] myAngleThing(float[] play, float pRad, float[] tree, float tRad) {
		// Returnes the two point tangental play and tree

		// if |delta| < pRad + tRad
		// get thea of play and project it out from tree to a dist of pRad+tRad.
		// This is the plusPoint and subPoint.
		float[] delta = Vect2d.vectSub(tree, play);
		float hyp = Vect2d.norm(delta);
		float opp = pRad + tRad;
		float adj = (float) Math.sqrt(Math.abs(hyp * hyp - opp * opp));
		float treeThea = Vect2d.pointToThea(delta);
		float shapeThea = Vect2d.pointToThea(new float[] { adj, opp });
		// how to tell is to subtract or add shape thea.
		// the two possible points are plus shape thea and minus shape thea
		// scaled to adjacent and added to play.
		// return float[]
		// [0 + 1] is (x, y) of plus thea
		// [2 + 3] is (x, y) of minus thea
		// [4] is the length from play to each point.
		// System.out.println("treeThea: " + treeThea);
		// System.out.println("shapeThea: " + shapeThea);
		float addThea = Vect2d.theaAdd(treeThea, shapeThea);
		// System.out.println("addThea: " + addThea);
		float subThea = Vect2d.theaSub(treeThea, shapeThea);
		float[] addPoint = Vect2d.theaToPoint(addThea, adj);
		float[] subPoint = Vect2d.theaToPoint(subThea, adj);
		// make sub thea and plus thea relative to tree.
		// plus point minus tree
		float[] relAddPoint = Vect2d.vectSub(Vect2d.vectAdd(play, addPoint),
				tree);
		float[] relSubPoint = Vect2d.vectSub(Vect2d.vectAdd(play, subPoint),
				tree);
		g.setColor(Color.GREEN);
		g.drawOval((int) (relAddPoint[0] + tree[0]) - 4,
				(int) (relAddPoint[1] + tree[1]) - 4, 8, 8);
		g.setColor(Color.CYAN);
		g.drawOval((int) (relSubPoint[0] + tree[0]) - 4,
				(int) (relSubPoint[1] + tree[1]) - 4, 8, 8);
		float relAddThea = Vect2d.pointToThea(relAddPoint);
		float relSubThea = Vect2d.pointToThea(relSubPoint);
		addPoint = Vect2d.normalize(addPoint);
		subPoint = Vect2d.normalize(subPoint);
		System.out.println("subPoint[0]: " + subPoint[0]);
		return new float[] { addPoint[0], addPoint[1], relAddThea, subPoint[0],
				subPoint[1], relSubThea, adj };
	}

	void draw() {
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, width, height);
		if (playSel == true) {
			fillCircleRel(Color.YELLOW, play.getLoc()[0], play.getLoc()[1],
					play.getRadius() + 2);
		}
		fillCircleRel(Color.BLUE, play.getLoc()[0], play.getLoc()[1],
				play.getRadius());

		fillCircleRel(new Color(66, 33, 00), treeInfo[0], treeInfo[1],
				treeInfo[2]);
		fillCircleRel(Color.GREEN, treeInfo[0], treeInfo[1], treeInfo[2] - 2);

		/**
		 * Overlaying panels
		 */
		// Draws players inventory.
		if (play.getInvInfo()[4] == 1) {
			play.drawInv(g);
		}
	}

	void entitySel(float[] clickLoc) {
		// Finds if any entities are located at the click location and sets them
		// to selected.
		// Vect2d.sayVect("clickLoc", clickLoc);
		// Vect2d.sayVect("cameraLoc", cameraLoc);
		// System.out.println("playLoc (" + play.getLoc()[0] + ", " +
		// play.getLoc()[1] +
		// ")");
		float dist = (float) Math.sqrt(Math.pow(
				(cameraLoc[0] + clickLoc[0] - play.getLoc()[0]), 2)
				+ Math.pow((cameraLoc[1] + clickLoc[1] - play.getLoc()[1]), 2));
		// System.out.println("Dist: " + dist);
		if (dist <= play.getRadius()) {
			// System.out.println("playClicked");
			playSel = true;
		} else {
			playSel = false;
			// charInfo = false;
			// panels[0].closed = true;
			play.getInvInfo()[4] = 0;
			System.out.println("close");

		}
	}

	/**
	 * Initiations
	 */

	void initPlayer() {
		play = new UnitOld(new float[] { 60, 90 }, 20, 12, Color.BLUE);
		// panels = new IndieInv[1];
		int invWidth = 240;
		int invHeight = 300;
		// panels[0] = new IndieInv(width - 40 - invWidth, 20, invWidth,
		// invHeight, 4, 12, 18, 2, 6, true);
		play.addItem(1);
	}

	/**
	 * Drawing methods
	 */

	void drawCircle(Color color, float[] circLoc, float radius) {
		g.setColor(color);
		g.drawOval((int) (circLoc[0] - radius + .5f), (int) (circLoc[1]
				- radius + .5f), (int) (radius * 2), (int) (radius * 2));
	}

	void drawCircleRel(Color color, float[] circLoc, float radius) {
		g.setColor(color);
		float deltax = cameraLoc[0];
		float deltay = cameraLoc[1];
		g.drawOval((int) (circLoc[0] - radius - deltax + .5f),
				(int) (circLoc[1] - radius + deltay + .5f), (int) (radius * 2),
				(int) (radius * 2));
	}

	void drawCircleRel(Color color, float circX, float circY, float radius) {
		g.setColor(color);
		float deltax = cameraLoc[0];
		float deltay = cameraLoc[1];
		g.drawOval((int) (circX - radius - deltax + .5f), (int) (circY - radius
				- deltay + .5f), (int) (radius * 2), (int) (radius * 2));
	}

	void fillCircleRel(Color color, float circX, float circY, float radius) {
		g.setColor(color);
		float deltax = cameraLoc[0];
		float deltay = cameraLoc[1];
		g.fillOval((int) (circX - radius - deltax + .5f), (int) (circY - radius
				- deltay + .5f), (int) (radius * 2), (int) (radius * 2));
	}

	/**
	 * Methods go above here.
	 * 
	 */

	public long timer() {
		return System.currentTimeMillis() - startTime;

	}

	public void drwGm() {
		Graphics g2 = this.getGraphics();
		g2.drawImage(image, 0, 0, null);
		g2.dispose();
	}

	public void imageInit() {
		imageAr = new Image[1];
		ImageIcon ie = new ImageIcon(this.getClass().getResource(
				"res/Wepons/icon_axe1.png"));
		imageAr[0] = ie.getImage();
	}

	@Override
	public void mousePressed(MouseEvent me) {
		if (!mouseD) {
			lastDragLoc[0] = me.getX();
			lastDragLoc[1] = me.getY();
		}
		mousePQ.add(new int[] { me.getX(), me.getY(), me.getButton() });
	}

	@Override
	public void mouseReleased(MouseEvent me) {
		mouseRQ.add(new int[] { me.getX(), me.getY(), me.getButton() });
	}

	@Override
	public void mouseClicked(MouseEvent me) {
		// if (me.getButton() == MouseEvent.BUTTON1) {
		// entitySel(new float[] { me.getX(), me.getY() });
		// }
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
	public void keyPressed(KeyEvent ke) {
		if (ke.getKeyCode() == KeyEvent.VK_C) {
			if (shiftP) {
				if (playSel) {
					// panels[0].closed = false;
					play.getInvInfo()[4] = 1;
					// panels[0].setX(width - 40 - panels[0].getWidth());
					// panels[0].setY(20);
					play.setInvLoc(width - 40 - play.getInvInfo()[2], 20);
				}
			} else {
				if (playSel) {
					// charInfo = true;
					// System.out.println("true");
					// panels[0].closed = !panels[0].closed;
					play.getInvInfo()[4] = play.getInvInfo()[4] == 0 ? 1 : 0;
				}
			}
		} else if (ke.getKeyCode() == KeyEvent.VK_SHIFT) {
			shiftP = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent ke) {
		if (ke.getKeyCode() == KeyEvent.VK_SHIFT) {
			shiftP = true;
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(MouseEvent me) {
		// System.out.println("lastB: " + lastButton);
		dragLoc[0] = me.getX();
		dragLoc[1] = me.getY();
		// Vect2d.sayVect("dragLoc", dragLoc);
		// Vect2d.sayVect("lastDragLoc", lastDragLoc);
		mouseD = true;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

	}
}
