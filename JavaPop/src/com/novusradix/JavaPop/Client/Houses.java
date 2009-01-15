package com.novusradix.JavaPop.Client;

import com.novusradix.JavaPop.Math.Matrix4;
import com.novusradix.JavaPop.Math.Vector3;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import java.util.Map.Entry;
import javax.media.opengl.GL;

public class Houses implements AbstractHouses, GLObject {

    private Game game;
    private int[][] map;
    private Map<Integer, House> houses;
    private static final int TEAMS = 4;
    private static final int EMPTY = 0;
    private static final int FARM = EMPTY + 1;
    private static final int HOUSE = FARM + TEAMS;
    private static final int NEXT = HOUSE + TEAMS;
    private XModel houseModel;
    private XModel ankhModel;
    private Map<Player, House> leaderHouses;

    public Houses(Game g) {
        game = g;
        map = new int[game.heightMap.getWidth()][game.heightMap.getBreadth()];
        houses = new HashMap<Integer, House>();
        houseModel = new XModel("/com/novusradix/JavaPop/models/house1.x", "/com/novusradix/JavaPop/textures/house1.png");
        ankhModel = new XModel("/com/novusradix/JavaPop/models/ankh.x", "/com/novusradix/JavaPop/textures/marble.png");
        leaderHouses = new HashMap<Player, House>();
    }

    public void updateHouse(int id, Point pos, Player p, int level) {
        synchronized (houses) {
            if (level < 0) {
                //remove
                if (houses.containsKey(id)) {
                    houses.remove(id);
                }
            } else {
                houses.put(id, new House(pos, p, level));
            }
        }
    }

    public boolean canBuild(Point p) {
        if (game.heightMap.tileInBounds(p)) {
            return (map[p.x][p.y] == EMPTY && game.heightMap.getHeight(p) > 0 && game.heightMap.isFlat(p));

        }
        return false;
    }

    public void display(GL gl, float time) {
         synchronized (houses) {
        if (houses != null) {
            gl.glUseProgram(0);

            for (House h : houses.values()) {
                Vector3 p;
                Matrix4 basis;
                p = new Vector3(h.pos.x + 0.5f, h.pos.y + 0.5f, game.heightMap.getHeight(h.pos));

                basis = new Matrix4(Matrix4.identity);

                if (h.level > 9) {
                    basis.scale(3.0f, 3.0f, 1.0f);
                }
                if (h.level == 49) {
                    basis.scale(1.0f, 1.0f, 2.0f);
                }

                gl.glColor3f(1, 1, 1);
                gl.glEnable(GL.GL_LIGHTING);
                houseModel.display(p, basis, gl, time);

            }
            gl.glDisable(GL.GL_LIGHTING);
            gl.glDisable(GL.GL_TEXTURE_2D);
            gl.glUseProgram(0);
            for (House h : houses.values()) {
                gl.glMatrixMode(GL.GL_MODELVIEW);

                gl.glPushMatrix();
                gl.glTranslatef(h.pos.x + 0.5f, h.pos.y + 0.5f, game.heightMap.getHeight(h.pos));
                if (h.level > 9) {
                    gl.glScalef(3.0f, 3.0f, 1.0f);
                }
                if (h.level == 49) {
                    gl.glScalef(1.0f, 1.0f, 2.0f);
                }

                gl.glColor3fv(h.player.colour, 0);
                gl.glBegin(GL.GL_TRIANGLES);
                gl.glVertex3f(0.3f, -0.3f, 1.3f);
                gl.glVertex3f(0.3f, -0.3f, 1.5f);
                gl.glVertex3f(0.4f, -0.4f, 1.4f);
                gl.glEnd();
                gl.glPopMatrix();
            }
        }
        for (House h : leaderHouses.values()) {
            if (h != null) {
                Vector3 pos = new Vector3();
                Matrix4 basis = new Matrix4(Matrix4.identity);
                pos.x = h.pos.x + 0.5f;
                pos.y = h.pos.y + 0.5f;
                pos.z = game.heightMap.getHeight(pos.x, pos.y) + 1.0f;

                ankhModel.display(pos, basis, gl, time);
            }
        }
    }
    }

    public class House {

        private Point pos;
        private int level;
        private Player player;

        public House(Point p, Player player, int level) {
            map[p.x][p.y] = HOUSE;
            pos = (Point) p.clone();
            this.level = level;
            this.player = player;
        }
    }

    public void init(GL gl) {
        houseModel.init(gl);
        ankhModel.init(gl);
    }

    public void setLeaders(Map<Integer, Integer> leaders) {
        leaderHouses.clear();
        for (Entry<Integer, Integer> e : leaders.entrySet()) {
            leaderHouses.put(game.players.get(e.getKey()), houses.get(e.getValue()));
        }
    }
}