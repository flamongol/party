package de.yarotu.party;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PartyListener implements Listener {


    private final PartyManager partyManager;

    public PartyListener(PartyManager partyManager) {
        this.partyManager = partyManager;
    }

    @EventHandler
    public void onServerSwitch(ServerSwitchEvent event) {
        Party party = partyManager.getParty(event.getPlayer());
        if (party != null) {
            if (party.getOwner().equals(event.getPlayer())) {
                if (event.getPlayer().getServer().getInfo().getName().toLowerCase().contains("lobby"))
                    return;
                for (ProxiedPlayer player : party.getPlayers().keySet()) {
                    player.connect(event.getPlayer().getServer().getInfo());
                }
                party.sendMessage("§7[§5Party§7] §7Die Party betritt den Server §a" + event.getPlayer().getServer().getInfo().getName() + "§7.");
            }
        }
    }

}
