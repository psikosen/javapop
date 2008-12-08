package com.novusradix.JavaPop.Client;

import com.novusradix.JavaPop.Math.Vector2;
import com.novusradix.JavaPop.Messaging.PeonUpdate.Detail;
import com.novusradix.JavaPop.Server.Peons.State;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.media.opengl.GL;

public class Peons {

    public Game game;
    private static Map<Integer, Peon> peons;

    public Peons(Game g) {
        game = g;
        peons = new HashMap<Integer, Peon>();
    }

    public void Update(Detail d) {
        synchronized (peons) {
            if (peons.containsKey(d.id)) {
                if (d.state == State.DEAD || d.state == State.SETTLED) {
                    peons.remove(d.id);
                } else {
                    peons.get(d.id).Update(d);
                }
            } else {
                peons.put(d.id, new Peon(d));
            }
        }
    }

    public void display(GL gl) {
        synchronized (peons) {
            for (Peon p : peons.values()) {
                gl.glPushMatrix();
                gl.glTranslatef(p.pos.x, p.pos.y, game.heightMap.getHeight(p.pos.x, p.pos.y));
                p.display(gl);
                gl.glPopMatrix();
            }
        }
    }

    void step(float seconds) {
        synchronized (peons) {
            for (Peon p : peons.values()) {

                p.step(seconds);
            }
        }
    }

    private class Peon {

        private Vector2 pos;
        private Point dest;
        private float dx,  dy;
        private State state;

        public Peon(Detail d) {
            pos = d.pos;
            dest = d.dest;
            dx = d.dx;
            dy = d.dy;
            state = d.state;
        }

        public void Update(Detail d) {
            pos = d.pos;
            dest = d.dest;
            dx = d.dx;
            dy = d.dy;
            state = d.state;
        }

        private void display(GL gl) {
            gl.glBegin(GL.GL_TRIANGLES);
            gl.glColor3f(0, 0, 1);

            gl.glVertex3f(0, 0, 0.3f);
            gl.glVertex3f(0.1f, -0.1f, 0);
            gl.glVertex3f(-0.1f, +0.1f, 0);

            gl.glEnd();

        }

        public void step(float seconds) {
            switch (state) {
                case WALKING:
                    pos.x += seconds * dx;
                    pos.y += seconds * dy;
                    break;

            }
        }
    }
}