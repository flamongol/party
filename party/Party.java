package de.yarotu.party;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Party {


    private final ProxiedPlayer leader;
    private final HashMap<ProxiedPlayer, PartyRank> players;
    private final Set<ProxiedPlayer> invites;

    public Party(ProxiedPlayer leader) {
        this.leader = leader;
        this.players = new HashMap<>();
        this.players.put(leader, PartyRank.LEADER);
        this.invites = new HashSet<>();
    }

    public ProxiedPlayer getOwner() {
        return leader;
    }

    public HashMap<ProxiedPlayer, PartyRank> getPlayers() {
        return players;
    }

    public PartyRank getRank(ProxiedPlayer player) {
        return players.get(player);
    }

    public Set<ProxiedPlayer> getInvites() {
        return invites;
    }

    public void sendMessage(String message){
        for(ProxiedPlayer player : players.keySet())
            player.sendMessage(new TextComponent(message));
    }
}