/*
 * This class handles the networking functions of the client on the server side. 
 * It also (somewhat akwardly) contains per player in-game data.
 */
package com.novusradix.JavaPop.Server;

import com.novusradix.JavaPop.Messaging.Lobby.GameList;
import com.novusradix.JavaPop.Messaging.Message;
import com.novusradix.JavaPop.Messaging.PlayerUpdate;
import java.awt.Point;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gef
 */
public class Player implements Runnable {

    private static float[][] defaultColors = {{0, 0, 1}, {1, 0, 0}, {0, 1, 0}};
    boolean human;

    public enum PeonMode {

        SETTLE, ANKH, FIGHT, GROUP
    }
    public Server s;
    public Socket socket;
    public Game currentGame;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private static int nextId = 1;
    private int id;
    public boolean ready = false;
    Info info;
    public PeonMode peonMode;

    public Player(Server s, Socket sock) {
        this.s = s;
        this.socket = sock;
        id = nextId++;
        info = new Info(id, "Player " + id, new Point(0, 0), defaultColors[id % 3], 0);
        peonMode = PeonMode.SETTLE;
        try {
            socket.setTcpNoDelay(true);
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.flush();
            ois = new ObjectInputStream(socket.getInputStream());

            oos.writeObject(info);
            human = ois.readBoolean();
            (new Thread(this, "Server Player")).start();
            sendMessage(new GameList(s.getGames()));
        } catch (IOException ioe) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ioe);
        }
    }

    public void MoveAnkh(Point where) {
        info.ankh = where;
    }

    public int getId() {
        return id;
    }

    private Message getMessage() throws IOException, ClassNotFoundException //method only exists to sepearate out blocking call for profiling purposes
    {
        return (Message) ois.readObject();
    }

    public void run() {
        //Message loop
        try {

            Message message;
            do {
                message = getMessage();
                if (message != null) {
                    message.setServer(s, currentGame, this);
                    message.execute();
                }
            } while (message != null);
        } catch (IOException ioe) {
        } catch (ClassNotFoundException cnfe) {
        }

        if (currentGame != null) {
            currentGame.removePlayer(this);
        }
        s.removePlayer(this);
        try {
            socket.close();
        //Disconnect
        } catch (IOException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public synchronized void sendMessage(Message m) {
        try {
            oos.writeObject(m);
            oos.flush();
            oos.reset();
            System.out.println("Server sent " + m.getClass().getName());
        } catch (IOException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public synchronized void sendMessage(byte[] bytes) {
        try {
            socket.getOutputStream().write(bytes);
            socket.getOutputStream().flush();
            oos.reset();
        } catch (IOException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static class Info implements Externalizable {

        public int id;
        public String name;
        public Point ankh;
        public float[] colour;
        public double mana;
        public int leaderPeonId,  leaderHouseId;

        public Info() {//todo move externalization into this class and privatise this constructor.
        }

        private Info(int id, String name, Point ankh, float[] colour, double mana) {
            this.id = id;
            this.name = name;
            this.ankh = ankh;
            this.colour = colour;
            this.mana = mana;
        }

        public void writeExternal(ObjectOutput o) throws IOException {
            o.writeInt(id);
            o.writeUTF(name);
            o.writeInt(ankh.x);
            o.writeInt(ankh.y);
            o.writeFloat(colour[0]);
            o.writeFloat(colour[1]);
            o.writeFloat(colour[2]);
            o.writeDouble(mana);
        }

        public void readExternal(ObjectInput i) throws IOException, ClassNotFoundException {
            id = i.readInt();
            name = i.readUTF();
            ankh = new Point();
            ankh.x = i.readInt();
            ankh.y = i.readInt();
            colour = new float[3];
            colour[0] = i.readFloat();
            colour[1] = i.readFloat();
            colour[2] = i.readFloat();
            mana = i.readDouble();
        }
    }
}