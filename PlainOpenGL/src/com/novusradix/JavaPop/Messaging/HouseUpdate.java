/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Messaging;

import com.novusradix.JavaPop.Server.Player;
import java.awt.Point;
import java.io.Serializable;
import java.util.Vector;

/**
 *
 * @author mom
 */
public class HouseUpdate extends Message {

    public Vector<Detail> details;

    @SuppressWarnings("unchecked")
    public HouseUpdate(Vector<Detail> ds) {
        details = (Vector<Detail>) ds.clone();
    }

    @Override
    public void execute() {
        for (Detail d : details) {
            client.game.houses.updateHouse(d.pos, d.playerId, d.level);
        }
    }

    public static class Detail implements Serializable {

        Point pos;
        int level;
        int playerId;

        public Detail(Point pos, Player p, int level) {
            this.pos = pos;
            this.playerId = p.getId();
            this.level = level;
        }
    }
}
