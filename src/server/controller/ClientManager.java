/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.controller;

import java.util.ArrayList;
import java.util.List;
import server.db.layers.DTO.Player;

 
public class ClientManager {

    ArrayList<GameClient> clients;

    public ClientManager() {
        clients = new ArrayList<>();
    }

    public boolean add(GameClient c) {
        if (!clients.contains(c)) {
            clients.add(c);
            return true;
        }
        return true;
    }

    public boolean remove(GameClient c) {
        if (clients.contains(c)) {
            clients.remove(c);
            return true;
        }
        return false;
    }

    public GameClient find(String email) {
        for (GameClient c : clients) {
            if (c.getLoginPlayer() != null && c.getLoginPlayer().getUsername().equals(email)) {
                return c;
            }
        }
        return null;
    }

    public void broadcast(String msg) {
        clients.forEach((c) -> {
            c.sendData(msg);
        });
    }

    public GameClient findClientFindingMatch() {
        for (GameClient c : clients) {
            if (c.isFindingMatch()) {
                return c;
            }
        }

        return null;
    }
    
    public List<GameClient> findClientFinding(Player p) {
        List<GameClient> list = new ArrayList<>();
        for (GameClient c : clients) {
            if (list.size() == 3) {
                return list;
            }
            if (c.isFindingMatch() && !c.getLoginPlayer().equals(p)) {
                list.add(c);
            }
        }
        if (list.size() == 3) {
            return list;
        }
        return null;
    }
    
    public int getSize() {
        return clients.size();
    }
}
