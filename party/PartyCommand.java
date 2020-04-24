package de.yarotu.party;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class PartyCommand extends Command {

    private final PartyManager partyManager;
    private final String prefix = "§7[§5Party§7]";

    public PartyCommand(String name, PartyManager partyManager) {
        super(name);
        this.partyManager = partyManager;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            if (args.length == 0) {
                player.sendMessage(new TextComponent(prefix + "§e/party create"));
                player.sendMessage(new TextComponent(prefix + "§e/party delete"));
                player.sendMessage(new TextComponent(prefix + "§e/party leave"));
                player.sendMessage(new TextComponent(prefix + "§e/party list"));
                player.sendMessage(new TextComponent(prefix + "§e/party invite (player)"));
                player.sendMessage(new TextComponent(prefix + "§e/party accept (player)"));
                player.sendMessage(new TextComponent(prefix + "§e/party deny (player)"));
                player.sendMessage(new TextComponent(prefix + "§e/party kick (player)"));
                player.sendMessage(new TextComponent(prefix + "§e/party promote (player)"));
                player.sendMessage(new TextComponent(prefix + "§e/party demote (player)"));

            } else if (args.length == 1) {


                if (args[0].equalsIgnoreCase("create")) {
                    if (partyManager.createParty(player) != null) {
                        player.sendMessage(new TextComponent(prefix + "§aDeine Party wurde erstellt."));
                    } else {
                        player.sendMessage(new TextComponent(prefix + "§cDu bist bereits in einer Party!"));
                    }
                } else if (args[0].equalsIgnoreCase("delete")) {
                    if (partyManager.deleteParty(player)) {
                        player.sendMessage(new TextComponent(prefix + "§cParty wurde geläscht."));
                    } else {
                        player.sendMessage(new TextComponent(prefix + "§cEin Fehler ist aufgetreten."));
                    }
                } else if (args[0].equalsIgnoreCase("leave")) {
                    Party party = partyManager.getParty(player);
                    if (party != null) {
                        party.getPlayers().remove(player);
                        party.sendMessage(prefix + "§a"+player.getName() + " §7hat die Party verlassen.");
                    } else {
                        player.sendMessage(new TextComponent(prefix + "§cDu bist in keiner Party!"));
                    }
                } else if (args[0].equalsIgnoreCase("list")) {
                    Party party = partyManager.getParty(player);
                    if (party != null) {
                        player.sendMessage(new TextComponent(prefix + "§eParty Mitglieder:"));
                        for (ProxiedPlayer pp : party.getPlayers().keySet()) {
                            player.sendMessage(new TextComponent("§7" + pp.getName()));
                        }
                    } else {
                        player.sendMessage(new TextComponent(prefix + "§cDu bist in keiner Party!"));
                    }
                }
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("invite")) {
                    Party party = partyManager.getParty(player);
                    if (party == null)
                        party = partyManager.createParty(player);
                    if (party.getRank(player) == PartyRank.USER) {
                        player.sendMessage(new TextComponent(prefix + "§cNur der Party-Gr§§nder oder PArty-Moderatoren d§§rfen dies tun!"));
                        return;
                    }
                    if (party != null) {
                        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[1]);
                        if (target != null) {
                            if (partyManager.getParty(target) != null) {
                                player.sendMessage(new TextComponent(prefix + "§cDu bist bereits in einer Party!"));
                            } else {
                                party.getInvites().add(target);
                                player.sendMessage(new TextComponent(prefix + "§7Du hast §§a" + target.getName() + " §§7eingeladen."));
                                target.sendMessage(new TextComponent(prefix + "§7Du wurdest von §§a" + player.getName() + " §§7zu einer Party eingeladen."));
                                TextComponent textComponent = new TextComponent();
                                TextComponent accept = new TextComponent("§7[§aAnnehmen§7] ");
                                accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party accept " + player.getName()));
                                accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§§aKlicke hier um die Anfrage anzunehmen.").create()));
                                TextComponent deny = new TextComponent(" §7[§cAblehnen§7]");
                                deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party deny " + player.getName()));
                                deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§§aKlicke hier um die Anfrage abzulehnen.").create()));
                                textComponent.addExtra(accept);
                                textComponent.addExtra(deny);
                                target.sendMessage(textComponent);
                            }
                        } else {
                            player.sendMessage(new TextComponent(prefix + "§cDer Spieler ist nicht online!"));
                        }
                    }
                } else if (args[0].equalsIgnoreCase("accept")) {
                    if (partyManager.getParty(player) != null) {
                        player.sendMessage(new TextComponent(prefix + "§cDu bist bereits in einer Party!"));
                        return;
                    }
                    ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[1]);
                    if (target != null) {
                        Party party = partyManager.getParty(target);
                        if (party.getInvites().contains(player)) {
                            party.getInvites().remove(player);
                            party.getPlayers().put(player, PartyRank.USER);
                            party.sendMessage(prefix + "§a" + player.getName() + " §7ist der Party beigetreten.");
                        }
                    } else {
                        player.sendMessage(new TextComponent(prefix + "§cDer Spieler ist nicht online!"));
                    }
                } else if (args[0].equalsIgnoreCase("deny")) {
                    ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[1]);
                    if (target != null) {
                        Party party = partyManager.getParty(target);
                        if (party.getInvites().contains(player)) {
                            party.getInvites().remove(player);
                            target.sendMessage(new TextComponent(prefix + "§a" + player.getName() + "§7 hat die Anfrage abgelehnt."));
                            player.sendMessage(new TextComponent(prefix + "§7Du hast die Anfrage von §a" + target.getName() + " §§7abgelehnt."));
                        }
                    } else {
                        player.sendMessage(new TextComponent(prefix + "§cDer Spieler ist nicht online!"));
                    }
                } else if (args[0].equalsIgnoreCase("kick")) {
                    Party party = partyManager.getParty(player);
                    if (party.getRank(player) == PartyRank.LEADER || party.getRank(player) == PartyRank.MOD) {
                        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[1]);
                        if (target != null) {
                            if (party.getPlayers().containsKey(target) && party.getRank(target) != PartyRank.LEADER) {
                                party.getPlayers().remove(target);
                                party.sendMessage(prefix + "§7Der Spieler §§a" + target.getName() + " §7wurde aus der Party geworfen!");
                                target.sendMessage(new TextComponent(prefix + "§7Du wurdest aus der Party geworfen!"));
                            }
                        } else {
                            player.sendMessage(new TextComponent(prefix + "§cDer Spieler ist nicht online!"));
                        }
                    } else {
                        player.sendMessage(new TextComponent(prefix + "§cNur der Party-Gründer oder Party-Moderatoren d§§rfen dies tun!"));
                    }
                } else if (args[0].equalsIgnoreCase("promote")) {
                    Party party = partyManager.getParty(player);
                    if (party.getRank(player) == PartyRank.LEADER) {
                        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[1]);
                        if (target != null) {
                            if (partyManager.getParty(target) == party) {
                                if (party.getPlayers().get(target) == PartyRank.MOD) {
                                    party.getPlayers().put(target, PartyRank.LEADER);
                                    party.getPlayers().put(player, PartyRank.MOD);
                                    party.sendMessage(prefix + "§7Der Spieler §a" + target.getName() + " §7wurde zum Leader befördert!");

                                } else {
                                    party.getPlayers().put(target, PartyRank.MOD);
                                    party.sendMessage(prefix + "§7Der Spieler §a" + target.getName() + " §7wurde zum Moderator befördert!");
                                    target.sendMessage(new TextComponent(prefix + "§§aDu wurdest befördert!"));
                                }
                            } else {
                                player.sendMessage(new TextComponent(prefix + "§cIhr seid nicht in der gleichen Party!"));
                            }
                        } else {
                            player.sendMessage(new TextComponent(prefix + "§cDer Spieler ist nicht online!"));
                        }
                    } else {
                        player.sendMessage(new TextComponent(prefix + "§cNur der Party-Gründer darf dies tun!"));
                    }
                } else if (args[0].equalsIgnoreCase("demote")) {
                    Party party = partyManager.getParty(player);
                    if (party.getRank(player) == PartyRank.LEADER) {
                        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[1]);
                        if (target != null) {
                            if (partyManager.getParty(target) == party) {
                                if (party.getPlayers().get(target) != PartyRank.MOD) {
                                    player.sendMessage(new TextComponent(prefix + "§7Der Spieler kann nicht degradiert werden!"));
                                } else {
                                    party.getPlayers().put(target, PartyRank.USER);
                                    party.sendMessage(prefix + "§7Der Spieler §a" + target.getName() + " §7wurde zum User degradiert!");
                                    target.sendMessage(new TextComponent(prefix + "§§cDu wurdest degradiert!"));
                                }
                            } else {
                                player.sendMessage(new TextComponent(prefix + "§cIhr seid nicht in der gleichen Party!"));
                            }
                        } else {
                            player.sendMessage(new TextComponent(prefix + "§cDer Spieler ist nicht online!"));
                        }
                    } else {
                        player.sendMessage(new TextComponent(prefix + "§cNur der Party-Gründer darf dies tun!"));
                    }
                }
            }
        }
    }
}
