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

import javax.swing.JPanel;

import FallenFeather.lib.JaMa;
import FallenFeather.lib.Vect2d;

public class PanelOld3 extends JPanel implements Runnable, MouseListener,
		KeyListener, MouseMotionListener {
	// This is going to be

	int width = 700;
	int height = 450;

	Image[] imageAr;

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

	float pX = 60;
	float pY = 90;
	float pRadius = 20;
	float pSpeed = 12;

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

	Indie[] panels = new Indie[1];
	Unit play;

	int[] mouseLast = new int[2];

	boolean b1press = false;

	public PanelOld3() {
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

	ArrayList<int[]> mousePQ = new ArrayList<int[]>();
	ArrayList<int[]> mouseRQ = new ArrayList<int[]>();

	// if the last click was on a panel.
	boolean clickedOnPanel = false;

	void mouseP(int[] mo) {
		clickedOnPanel = false;
		justDragged = false;
		float[] mouseRel = { cameraLoc[0] + mo[0], cameraLoc[1] + mo[1] };
		// Vect2d.sayVect("mouseRel", mouseRel);

		// mouseLast[0] = mo[0];
		// mouseLast[1] = mo[1];

		if (mo[2] == MouseEvent.BUTTON3) {
			lastButton = 3;
			// If you are right clicking on a panel then don't move.
			if (!panels[0].lapCheck(mo[0], mo[1])) {
				setPath = true;
				tarX = mouseRel[0];
				tarY = mouseRel[1];
			}
		} else if (mo[2] == MouseEvent.BUTTON1) {
			lastButton = 1;
			boolean panelTouch = false;
			for (Indie p : panels) {
				// if (!p.closed) {
				// panelTouch = p.clickHandle(mo[0], mo[1]) ? true
				// : panelTouch;
				panelTouch = p.closed ? panelTouch : p
						.clickHandle(mo[0], mo[1]) ? true : panelTouch;
				// }
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
			// Vect2d.sayVect("delta", delta);
			if (b1press) {
				cameraLoc[0] -= delta[0];
				cameraLoc[1] -= delta[1];
				// Vect2d.sayVect("cameraLoc", cameraLoc);
			} else {
				for (int p = 0; p < panels.length; p++) {
					if (panels[p].clickTop) {
						panels[p].x += delta[0];
						panels[p].y += delta[1];
					}
				}
			}
		} else if (lastButton == 3) {
			if (!panels[0].lapCheck(dragLoc[0], dragLoc[1])) {
				setPath = true;
				tarX = cameraLoc[0] + dragLoc[0];
				tarY = cameraLoc[1] + dragLoc[1];
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
		playSpeedLeft = pSpeed;
		while (myPath.length > 0 && playSpeedLeft > 0) {
			// System.out.println("while");
			sortPath();
		}
		System.out.println("myPath.length: " + myPath.length);
		if (myPath.length == 0) {
			pathing = false;
			System.out.println("pathing: " + pathing);
		}
		moving = false;
	}

	void sortPath() {
		if (myPath[0] == 0) {
			System.out.println("path 0");
			// System.out.println("line");
			// linear so go straight
			if (myPath[3] > playSpeedLeft) {
				System.out.println("playSpeedLeft : " + playSpeedLeft);
				System.out.println("myPath[3]: " + myPath[3]);
				// Vect2d.sayVect("myPath", myPath);
				pX += myPath[1] * playSpeedLeft;
				pY += myPath[2] * playSpeedLeft;
				System.out.println("myPath[1]: " + myPath[1]);
				System.out.println("myPath[2]: " + myPath[2]);
				// System.out.println("xAdd: " + myPath[1] * playSpeedLeft);
				// System.out.println("yAdd: " + myPath[2] * playSpeedLeft);
				myPath[3] -= playSpeedLeft;
				playSpeedLeft = 0;
			} else {
				pX += myPath[1] * myPath[3];
				pY += myPath[2] * myPath[3];
				System.out.println("tarX: " + tarX);
				System.out.println("tarY: " + tarY);
				System.out.println("pX: " + pX);
				System.out.println("pY: " + pY);
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
				pX = treeInfo[0] + newLoc[0];
				pY = treeInfo[1] + newLoc[1];
				System.out.println("pX: " + pX + ",   pY: " + pY);
				// pathing = false;
				playSpeedLeft = 0;
			} else {
				float[] newLoc = Vect2d.theaToPoint(myPath[2], myPath[3]);
				pX = treeInfo[0] + newLoc[0];
				pY = treeInfo[1] + newLoc[1];
				myPath = JaMa.removeFirstFloatAr(myPath, 4);
				playSpeedLeft -= edgeLength;
			}
		}
	}

	void playMoveWhole() {
		// Plus thea from player should get sub thea from tar.

		direction = new float[0][];
		// get delta vector. scale to moveSpeed.
		float[] deltaVect = { tarX - pX, tarY - pY };

		if (distPointToVect(Vect2d.vectSub(new float[] { treeInfo[0],
				treeInfo[1] }, new float[] { pX, pY }), deltaVect) < treeInfo[2]
				+ pRadius) {
			// if the target point was inside of a tree then project it out.
			float[] tarRelTree = new float[] { tarX - treeInfo[0],
					tarY - treeInfo[1] };
			if (Vect2d.norm(tarRelTree) <= pRadius + treeInfo[2]) {
				// add small num to bypass rounding mistakes.
				// float[] pushedTar = Vect2d.theaToPoint(
				// Vect2d.pointToThea(tarRelTree), pRadius + treeInfo[2]
				// + smallNum);
				/**
				 * Instead of pushing it out pick the closest point between play
				 * and tree.
				 */
				// treeInfo.l is 3 but Ve2d only reads the first two increments.
				float[] treeRelPlay = Vect2d.vectSub(treeInfo, new float[] {
						pX, pY });
				// deltaVect scaled down by plaR + treR.
				float ta = Vect2d.norm(treeRelPlay);
				System.out.println("b4 ta: " + ta);
				treeRelPlay = Vect2d.vectMultScalar(
						(ta - (pRadius + treeInfo[2])) / ta, treeRelPlay);
				ta = Vect2d.norm(treeRelPlay);
				System.out.println("cd ta: " + ta);
				// tarX = pX + treeRelPlay[0];
				// tarY = pY + treeRelPlay[1];
				// tarX = treeInfo[0] + pushedTar[0];
				// tarY = treeInfo[1] + pushedTar[1];
				// playMoveWhole();
				// direction = JaMa.appendFloatArAr(direction, new float[] { 0,
				// treeRelPlay[0], treeRelPlay[1], ta });
				myPath = new float[] { 0, treeRelPlay[0] / ta,
						treeRelPlay[1] / ta, ta };
				g.setColor(Color.ORANGE);
				g.drawOval((int) (pX + myPath[0]) - 2,
						(int) (pY + myPath[1]) - 2, 4, 4);
				pathing = true;
				return;
			}
			// cant move
			// moving = false;
			float[] tangents = myAngleThing(new float[] { pX, pY }, pRadius,
					new float[] { treeInfo[0], treeInfo[1] }, treeInfo[2]);
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

			tangents = myAngleThing(new float[] { tarX, tarY }, pRadius,
					new float[] { treeInfo[0], treeInfo[1] }, treeInfo[2]);
			System.out.println("tangents[2]: " + tangents[2]);
			System.out.println("tangents[5]: " + tangents[5]);
			// tangents from tar
			// plusThea from player should get subThea from tar.
			direction[0] = JaMa.appendArFloatAr(direction[0], new float[] {
					tangents[5], pRadius + treeInfo[2] });
			direction[1] = JaMa.appendArFloatAr(direction[1], new float[] {
					tangents[2], pRadius + treeInfo[2] });

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
		// if |delta| < pRad + tRad
		// get thea of play and project it out from tree to a dist of pRad+tRad.
		// This is the plusPoint and subPoint.
		float[] delta = Vect2d.vectSub(tree, play);
		float hyp = Vect2d.norm(delta);
		float opp = pRad + tRad;
		// System.out.println("hyp * hyp: " + hyp * hyp);
		// System.out.println("opp * opp: " + opp * opp);
		float adj = (float) Math.sqrt(Math.abs(hyp * hyp - opp * opp));
		// System.out.println("adj: " + adj);
		// System.out.println("opp: " + opp);
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
		// Vect2d.sayVect("play", play);
		// Vect2d.sayVect("addPoint", addPoint);
		// Vect2d.sayVect("relAddPoint", relAddPoint);
		float[] relSubPoint = Vect2d.vectSub(Vect2d.vectAdd(play, subPoint),
				tree);
		g.setColor(Color.GREEN);
		g.drawOval((int) (relAddPoint[0] + tree[0]) - 4,
				(int) (relAddPoint[1] + tree[1]) - 4, 8, 8);
		g.setColor(Color.CYAN);
		g.drawOval((int) (relSubPoint[0] + tree[0]) - 4,
				(int) (relSubPoint[1] + tree[1]) - 4, 8, 8);
		float relAddThea = Vect2d.pointToThea(relAddPoint);
		// System.out.println("relAddThea: " + relAddThea);
		float relSubThea = Vect2d.pointToThea(relSubPoint);
		addPoint = Vect2d.normalize(addPoint);
		// Vect2d.sayVect("addPoint", addPoint);
		subPoint = Vect2d.normalize(subPoint);
		System.out.println("subPoint[0]: " + subPoint[0]);
		return new float[] { addPoint[0], addPoint[1], relAddThea, subPoint[0],
				subPoint[1], relSubThea, adj };
	}

	void draw() {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);
		// g.setColor(Color.BLUE);
		// g.drawOval((int) (pX - pRadius + .5f), (int) (pY - pRadius + .5f),
		// (int) (pRadius * 2 + .5f), (int) (pRadius * 2 + .5f));
		fillCircleRel(Color.BLUE, pX, pY, pRadius);
		if (playSel == true) {
			drawCircleRel(Color.YELLOW, pX, pY, pRadius + 1);
		}
		// g.setColor(Color.GREEN);
		// g.fillOval((int) (treeInfo[0] - treeInfo[2]),
		// (int) (treeInfo[1] - treeInfo[2]), (int) treeInfo[2] * 2,
		// (int) treeInfo[2] * 2);
		drawCircleRel(Color.GREEN, treeInfo[0], treeInfo[1], treeInfo[2]);

		// drawCharInfo();

		// draws panels
		for (int p = 0; p < panels.length; p++) {
			panels[p].draw(g);
		}
	}

	boolean charInfo = false;
	float[] infoLoc = { 400, 80, 200, 300 };

	void drawCharInfo() {
		if (charInfo) {
			g.setColor(Color.WHITE);
			g.fillRect((int) infoLoc[0], (int) infoLoc[1], (int) infoLoc[2],
					(int) infoLoc[3]);
		}
	}

	void entitySel(float[] clickLoc) {
		// Finds if any entities are located at the click location and sets them
		// to selected.
		// Vect2d.sayVect("clickLoc", clickLoc);
		// Vect2d.sayVect("cameraLoc", cameraLoc);
		// System.out.println("playLoc (" + pX + ", " + pY + ")");
		float dist = (float) Math.sqrt(Math.pow(
				(cameraLoc[0] + clickLoc[0] - pX), 2)
				+ Math.pow((cameraLoc[1] + clickLoc[1] - pY), 2));
		// System.out.println("Dist: " + dist);
		if (dist <= pRadius) {
			// System.out.println("playClicked");
			playSel = true;
		} else {
			playSel = false;
			// charInfo = false;
			panels[0].closed = true;
			System.out.println("close");

		}
	}

	/**
	 * Inits
	 */

	void initPlayer() {
		play = new Unit(new float[] { 50, 50 }, 18, 20, Color.BLUE);
		panels = new Indie[1];
		// for (int p = 0; p < panels.length; p++) {
		// panels[p] = new Indie(width - 40 - 180, 20 + p * 24, 120, 180, 4,
		// 12, 18, 2, 6);
		// }
		int invWidth = 240;
		int invHeight = 300;
		panels[0] = new Indie(width - 40 - invWidth, 20, invWidth, invHeight,
				4, 12, 18, 2, 6, true);
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

		// imageAr = new Image[1];
		// ImageIcon ie = new ImageIcon(this.getClass().getResource(
		// "res/image.png"));
		// imageAr[0] = ie.getImage();

	}

	boolean mouseD = false;
	boolean mouseR = false;

	@Override
	public void mousePressed(MouseEvent me) {
		if (!mouseD) {
			lastDragLoc[0] = me.getX();
			lastDragLoc[1] = me.getY();
		}
		mousePQ.add(new int[] { me.getX(), me.getY(), me.getButton() });
	}

	boolean justDragged = false;

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
			if (playSel) {
				// charInfo = true;
				// System.out.println("true");
				panels[0].closed = !panels[0].closed;
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	int lastButton = -1;
	int[] dragLoc = new int[2];

	int[] lastDragLoc = new int[2];

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
