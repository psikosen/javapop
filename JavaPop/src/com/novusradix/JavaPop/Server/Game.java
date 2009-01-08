package com.novusradix.JavaPop.Server;

import com.novusradix.JavaPop.Messaging.EffectUpdate;
import com.novusradix.JavaPop.Messaging.Lobby.GameOver;
import com.novusradix.JavaPop.Messaging.Lobby.GameStarted;
import com.novusradix.JavaPop.Messaging.HeightMapUpdate;
import com.novusradix.JavaPop.Messaging.Lobby.JoinedGame;
import com.novusradix.JavaPop.Messaging.Message;
import com.novusradix.JavaPop.Messaging.PlayerUpdate;
import com.novusradix.JavaPop.Server.Effects.Effect;
import com.novusradix.JavaPop.Server.Effects.LightningEffect;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gef
 */
public class Game extends TimerTask {

    public volatile static int nextId = 0;
    private int id;
    public Vector<Player> players;
    private Player owner;
    private Server server;
    private Timer timer;
    private float seconds;
    public HeightMap heightMap;
    public Peons peons;
    public Houses houses;
    private int humancount;
    private Map<Integer, Effect> effects;
    private Map<Integer, Effect> newEffects;
    private Collection<Integer> deletedEffects;
    public Map<Player, LightningEffect> lightningEffects;

    public Game(Player owner) {
        this.owner = owner;
        server = owner.s;
        players = new Vector<Player>();
        owner.currentGame = this;
        id = nextId++;
        humancount = 0;
        addPlayer(owner);
        effects = new HashMap<Integer, Effect>();
        newEffects = new HashMap<Integer, Effect>();
        deletedEffects = new HashSet<Integer>();
        lightningEffects = new HashMap<Player, LightningEffect>();
    }

    public int getId() {
        return id;
    }

    public void addPlayer(Player p) {
        synchronized (players) {
            players.add(p);
            p.currentGame = this;
            p.sendMessage(new JoinedGame(this));
            if (p.human) {
                humancount++;
            }
        }
    }

    public void removePlayer(Player p) {
        synchronized (players) {
            players.remove(p);
            p.currentGame = null;
            if (p.human) {
                humancount--;
                if (humancount == 0) {
                    sendAllPlayers(new GameOver());
                }
            }

        }
    //TODO: send a message?
    }

    public void PlayerReady(Player p) {
        p.ready = true;
        for (Player pl : players) {
            if (!pl.ready) {
                return;
            }
        }
        startGame();
    }

    public void startGame() {
        heightMap = new HeightMap(new Dimension(64, 64));
        heightMap.randomize(1);
        peons = new Peons(this);
        houses = new Houses(this);

        if (players.size() == 1) {
            //Add an AI player
            new com.novusradix.JavaPop.Client.AI.Client(server.port, id);
        }

        try {
            while (players.size() == 1) {
                Thread.sleep(500);
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (Player p : players) {
            peons.addPeon(2.5f + p.getId(), 2.5f + p.getId() * 2, 200, p);
        }

        GameStarted go = new GameStarted(this);
        server.sendAllPlayers(go);
        HeightMapUpdate m = heightMap.GetUpdate();
        if (m != null) {
            sendAllPlayers(m);
        }
        timer = new Timer("Game " + id);
        seconds = 1.0f / 20.0f;
        timer.scheduleAtFixedRate(this, 0, (int) (seconds * 1000.0f));
    }

    public void run() {
        //start a clock,
        //move people.

        if (players.isEmpty()) {
            timer.cancel();
        }
        HeightMapUpdate m;
        synchronized (heightMap) {
            peons.step(seconds);
            houses.step(seconds);
            m = heightMap.GetUpdate();
        }
        if (m != null) {
            sendAllPlayers(m);
        }
        for (Effect e : effects.values()) {
            e.execute(this);
        }
        synchronized(effects){
        effects.putAll(newEffects);
        effects.keySet().removeAll(deletedEffects);
        EffectUpdate eu = new EffectUpdate(newEffects, deletedEffects);
        sendAllPlayers(eu);
        newEffects.clear();deletedEffects.clear();
        }
        ArrayList<Player.Info> is = new ArrayList<Player.Info>(players.size());
        for (Player p : players) {
            is.add(p.info);
        }
        sendAllPlayers(new PlayerUpdate(is));
    }

    public void sendAllPlayers(Message m) {
        synchronized (players) {
            for (Player pl : players) {
                pl.sendMessage(m);
            }
        }
    }

    public void addEffect(Effect e) {
        synchronized(effects)
        {
        newEffects.put(e.id, e);
        }
    }
    
    public void deleteEffect(Effect e)
    {
        synchronized(effects)
        {
        deletedEffects.add(e.id);
    
        }
    }
}
