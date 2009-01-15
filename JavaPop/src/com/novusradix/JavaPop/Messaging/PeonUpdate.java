/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Messaging;

import com.novusradix.JavaPop.Math.Vector2;
import com.novusradix.JavaPop.Server.Peons;
import com.novusradix.JavaPop.Server.Peons.Peon;
import com.novusradix.JavaPop.Server.Player;
import java.awt.Point;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author mom
 */
public class PeonUpdate extends Message implements Externalizable {

    private static final long serialVersionUID = 1L;
    Collection<Detail> details;
    Map<Integer, Integer> leaders;
    private static Map<Thread, Detail[]> ds; //re-used buffer
    private static Map<Thread, Integer> dcap;
    private int dlimit = 0; //the number of good records and capacity
    

    static {
        ds = new HashMap<Thread, Detail[]>();
        dcap = new HashMap<Thread, Integer>();
    }

    @SuppressWarnings("unchecked")
    public PeonUpdate(Collection<Detail> pds, Map<Player, Peon> leaderMap) {
        details = new ArrayList(pds);
        leaders = new HashMap<Integer, Integer>();
        for (Entry<Player, Peon> e : leaderMap.entrySet()) {
            leaders.put(e.getKey().getId(), e.getValue().id);
        }
    }

    @Override
    public void execute() {
        Detail[] tds;
        tds = ds.get(Thread.currentThread());
        for (int n = 0; n < dlimit; n++) {
            client.game.peons.Update(tds[n]);
        }

        client.game.peons.setLeaders(leaders);
    }

    public static class Detail {

        public int id;
        public Peons.State state;
        public float dx,  dy;
        public int destx,  desty;
        public float posx,  posy;
        public int playerId;

        public Detail(int id, Peons.State state, float posx, float posy, int destx, int desty, float dx, float dy, int playerId) {
            this.id = id;
            this.state = state;
            this.dx = dx;
            this.dy = dy;
            this.destx = destx;
            this.desty = desty;
            this.posx = posx;
            this.posy = posy;
            this.playerId = playerId;
        }

        private Detail() {
        }

        @Override
        public String toString() {
            return id + ": " + state.toString();
        }
    }

    public PeonUpdate() {
    }

    public void writeExternal(ObjectOutput out) throws IOException {

        out.writeInt(details.size());
        for (Detail d : details) {
            out.writeInt(d.id);
            out.writeInt(d.state.ordinal());
            out.writeFloat(d.dx);
            out.writeFloat(d.dy);
            out.writeFloat(d.posx);
            out.writeFloat(d.posy);
            out.writeInt(d.destx);
            out.writeInt(d.desty);
            out.writeInt(d.playerId);
        }

        out.writeInt(leaders.size());
        for (Entry<Integer, Integer> e : leaders.entrySet()) {
            out.writeInt(e.getKey());
            out.writeInt(e.getValue());
        }
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

        int i = in.readInt();
        if (!dcap.containsKey(Thread.currentThread()) || i > dcap.get(Thread.currentThread())) {
            ds.put(Thread.currentThread(), new Detail[i]);
            dcap.put(Thread.currentThread(), i);
        }
        dlimit = i;
        Detail[] tds = ds.get(Thread.currentThread());
        for (int n = 0; n < i; n++) {
            if (tds[n] == null) {
                tds[n] = new Detail();
            }
            Detail d = tds[n];
            d.id = in.readInt();
            d.state = Peons.State.values()[in.readInt()];
            d.dx = in.readFloat();
            d.dy = in.readFloat();
            d.posx = in.readFloat();
            d.posy = in.readFloat();
            d.destx = in.readInt();
            d.desty = in.readInt();
            d.playerId = in.readInt();

        }
        i = in.readInt();
        leaders = new HashMap<Integer, Integer>();
        for (; i > 0; i--) {
            leaders.put(in.readInt(), in.readInt());
        }
    }
}