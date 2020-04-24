package de.yarotu.party;

import com.sun.xml.internal.ws.wsdl.writer.document.Part;
import de.yarotu.Main;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashSet;
import java.util.Set;

public class PartyManager {

    private final Set<Party> parties = new HashSet<>();

    public Set<Party> getParties(){
        return parties;
    }

    public Party getParty(ProxiedPlayer player) {
        for(Party party : parties) {
            if(party.getPlayers().containsKey(player))
                return party;
        }
        return null;
    }

    public Party createParty(ProxiedPlayer player) {
        if(getParty(player) != null) return null;
        Party party = new Party(player);
        parties.add(party);
        return party;
    }

    public boolean deleteParty(ProxiedPlayer player) {
        Party party = getParty(player);
        if(party == null)
            return false;
        if(party.getRank(player) != PartyRank.LEADER)
            return false;
        for(ProxiedPlayer proxiedPlayer : party.getPlayers().keySet())
            proxiedPlayer.sendMessage(new TextComponent("§7[§5Party§7] §c" + player.getName() + " §7hat die Party aufgelöst!"));
        party.getPlayers().clear();
        parties.remove(party);
        return true;
    }

}
