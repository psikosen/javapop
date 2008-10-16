import java.awt.Frame;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.nio.FloatBuffer;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector4f;

import com.sun.opengl.util.Animator;

public class GLWindow extends Frame implements GLEventListener, KeyListener,
		MouseMotionListener, MouseListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final float fHeightScale = 0.4082f;

	private Animator a;
	private HeightMap heightMap;
	private Water water;

	private float xPos, yPos, xOrig, yOrig;
	private int height, width;

	private Point dragOrigin;

//	private float xMouse, yMouse;
	private Point selected;

	private Boolean bMouseMoved;
	private Vector<Peon> peons;

	// private FloatBuffer mvp;
	Matrix4f mvpInverse;

	public static void main(final String[] args) {
		HeightMap h = new HeightMap(128, 128);
		new GLWindow(h);
	}

	public GLWindow(HeightMap h) {
		mvpInverse = new Matrix4f();
		heightMap = h;
		Peon.h = h;
		peons = new Vector<Peon>();
		water = new Water(heightMap);
		GLCanvas glC;
		selected = new Point();
		bMouseMoved = new Boolean(false);

		GLCapabilities caps = new GLCapabilities();
		caps.setSampleBuffers(true);
		caps.setNumSamples(8);

		glC = new GLCanvas(caps);
		// glC = new GLCanvas();

		glC.addGLEventListener(this);

		glC.addMouseListener(this);
		glC.addMouseMotionListener(this);
		glC.addKeyListener(this);

		super.setSize(1024, 768);
		add(glC, java.awt.BorderLayout.CENTER);

		glC.getBounds();
		setVisible(true);
		glC.requestFocus();
		a = new Animator(glC);
		a.start();
		xPos = 0;
		yPos = 0;

	}

	public void display(final GLAutoDrawable glAD) {
		// TODO Auto-generated method stub
		final GL gl = glAD.getGL();
		gl.glClearColor(0, 0, 0, 0);
		gl.glEnable(GL.GL_LIGHTING);

		gl.glShadeModel(GL.GL_FLAT);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glPushMatrix();

		gl.glRotatef(-60.0f, 1.0f, 0.0f, 0.0f);
		gl.glRotatef(45.0f, 0.0f, 0.0f, 1.0f);
		gl.glTranslatef((0.70711f * xPos - yPos / 0.70711f), -(yPos / 0.70711f)
				- xPos * 0.70711f, -25);

		float[] buf = new float[16];
		gl.glGetFloatv(GL.GL_MODELVIEW, buf, 0);

		mvpInverse.set(buf);
		mvpInverse.transpose();
		mvpInverse.invert();
		gl.glScalef(1.0f, 1.0f, fHeightScale);

		gl.glEnable(GL.GL_BLEND);
		gl.glEnable(GL.GL_MULTISAMPLE);

		heightMap.display(gl);
		bMouseMoved = false;

		gl.glDisable(GL.GL_LIGHTING);
		gl.glEnable(GL.GL_BLEND);
		gl.glShadeModel(GL.GL_SMOOTH);
		gl.glDisable(GL.GL_DEPTH_TEST);
		gl.glBegin(GL.GL_TRIANGLES);

		float cW, cH;
		cW = 0.02f;
		cH = 0.1f;

		gl.glColor4f(1.0f, 1, 1, 1);
		gl.glVertex3f(selected.x + cW, selected.y - cW, heightMap.getHeight(
				selected.x, selected.y));
		gl.glVertex3f(selected.x - cW, selected.y + cW, heightMap.getHeight(
				selected.x, selected.y));

		gl.glColor4f(1.0f, 1, 1, 0.0f);
		gl.glVertex3f(selected.x, selected.y, heightMap.getHeight(selected.x,
				selected.y)
				+ 2.0f * cH / fHeightScale);

		gl.glColor4f(1.0f, 1, 1, 1);
		gl.glVertex3f(selected.x + cW, selected.y - cW, heightMap.getHeight(
				selected.x, selected.y));
		gl.glVertex3f(selected.x - cW, selected.y + cW, heightMap.getHeight(
				selected.x, selected.y));

		gl.glColor4f(1.0f, 1, 1, 0);
		gl.glVertex3f(selected.x, selected.y, heightMap.getHeight(selected.x,
				selected.y)
				- 2.0f * cH / fHeightScale);

		gl.glColor4f(1.0f, 1, 1, 1);
		gl.glVertex3f(selected.x, selected.y, heightMap.getHeight(selected.x,
				selected.y)
				+ 2.0f * cW / fHeightScale);
		gl.glVertex3f(selected.x, selected.y, heightMap.getHeight(selected.x,
				selected.y)
				- 2.0f * cW / fHeightScale);

		gl.glColor4f(1.0f, 1, 1, 0);
		gl.glVertex3f(selected.x - cH, selected.y + cH, heightMap.getHeight(
				selected.x, selected.y));

		gl.glColor4f(1.0f, 1, 1, 1);
		gl.glVertex3f(selected.x, selected.y, heightMap.getHeight(selected.x,
				selected.y)
				+ 2.0f * cW / fHeightScale);
		gl.glVertex3f(selected.x, selected.y, heightMap.getHeight(selected.x,
				selected.y)
				- 2.0f * cW / fHeightScale);

		gl.glColor4f(1.0f, 1, 1, 0);
		gl.glVertex3f(selected.x + cH, selected.y - cH, heightMap.getHeight(
				selected.x, selected.y));

		gl.glEnd();

		gl.glBegin(GL.GL_TRIANGLES);
		gl.glColor3f(0, 0, 1);
		Peon p;
		for (Iterator<Peon> i = peons.iterator(); i.hasNext();) {
			p = i.next();
			gl.glVertex3f(p.x, p.y, heightMap.getHeight(p.x, p.y) + 0.3f);
			gl
					.glVertex3f(p.x + 0.1f, p.y - 0.1f, heightMap.getHeight(
							p.x, p.y));
			gl
					.glVertex3f(p.x - 0.1f, p.y + 0.1f, heightMap.getHeight(
							p.x, p.y));

			if (p.step() == Peon.DEAD) {
				i.remove();
			}
		}
		gl.glEnd();

		gl.glPopMatrix();
		gl.glFlush();
	}

	public void displayChanged(final GLAutoDrawable arg0, final boolean arg1,
			final boolean arg2) {
		// TODO Auto-generated method stub

	}

	public void init(final GLAutoDrawable glDrawable) {
		final GL gl = glDrawable.getGL();
		/*
		 * final boolean VBOsupported =
		 * gl.isFunctionAvailable("glGenBuffersARB") &&
		 * gl.isFunctionAvailable("glBindBufferARB") &&
		 * gl.isFunctionAvailable("glBufferDataARB") &&
		 * gl.isFunctionAvailable("glDeleteBuffersARB");
		 */

		gl.glEnable(GL.GL_LIGHTING);
		float global_ambient[] = { 0.0f, 0.1f, 0.0f, 1.0f };
		gl.glLightModelfv(GL.GL_LIGHT_MODEL_AMBIENT, FloatBuffer
				.wrap(global_ambient));

		gl.glLightfv(GL.GL_LIGHT1, GL.GL_DIFFUSE, FloatBuffer.wrap(new float[] {
				0.8f, 0.8f, 0.8f, 1.0f }));

		gl.glEnable(GL.GL_LIGHT1);
		gl.glEnable(GL.GL_COLOR_MATERIAL);
		gl.glColorMaterial(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT_AND_DIFFUSE);
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

		heightMap.init(glDrawable);
	}

	public void reshape(final GLAutoDrawable glDrawable, final int x,
			final int y, final int w, int h) {
		final GL gl = glDrawable.getGL();
		height = h;
		width = w;

		if (height <= 0) // avoid a divide by zero error!
			height = 1;
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(-width / 64.0f, width / 64.0f, -height / 64.0f,
				height / 64.0f, 1, 100);
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	public void keyPressed(final KeyEvent e) {
		// TODO Auto-generated method stub

		if (e.getKeyCode() == KeyEvent.VK_N) {
			peons.add(new Peon(selected.x, selected.y));
		}

		if (e.getKeyCode() == KeyEvent.VK_S) {
			water.addSpring(selected.x, selected.y);
		}

		if (e.getKeyCode() == KeyEvent.VK_W) {
			for (int n = 0; n < 10; n++) {
				Random r = new Random();
				water.drop(selected.x + r.nextFloat(), selected.y
						+ r.nextFloat());
			}
		}
	}

	public void keyReleased(final KeyEvent e) {
		// TODO Auto-generated method stub

	}

	public void keyTyped(final KeyEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		if ((e.getModifiersEx() & MouseEvent.BUTTON2_DOWN_MASK) != 0) {
			xPos = xOrig + (e.getX() - dragOrigin.x) / 32.0f;
			yPos = yOrig + (e.getY() - dragOrigin.y) / 32.0f;
		}
	}

	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		float xMouse,yMouse;
		xMouse = 16.0f*(2.0f * e.getX() / width - 1.0f);
		yMouse = 16.0f*(-2.0f * e.getY() / height + 1.0f);

		bMouseMoved = Boolean.TRUE;

		float l;
		Vector4f z0,z1, s;
		z0 = new Vector4f(xMouse, yMouse, 0,1);
		z1 = new Vector4f(xMouse, yMouse, 1,1);

		mvpInverse.transform(z0);
		mvpInverse.transform(z1);
		z1.sub(z0);
		l = -z0.z / z1.z;
		s = new Vector4f();
		s.scaleAdd(l, z1, z0);
		selected.x = (int) s.x;
		selected.y = (int) s.y;

	}

	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		dragOrigin = e.getPoint();
		if ((e.getModifiersEx() & MouseEvent.BUTTON2_DOWN_MASK) != 0) {
			xOrig = xPos;
			yOrig = yPos;
		}

	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		if (Math.abs(e.getX() - dragOrigin.x) < 16
				&& Math.abs(e.getY() - dragOrigin.y) < 16) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				heightMap.up(selected.x, selected.y);
			} else if (e.getButton() == MouseEvent.BUTTON3) {
				heightMap.down(selected.x, selected.y);
			}
		}
	}

}
