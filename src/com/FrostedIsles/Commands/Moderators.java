package com.FrostedIsles.Commands;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.FrostedIsles.Comp.ConfigurationManager;
import com.FrostedIsles.Comp.Main;
import com.FrostedIsles.Comp.Rank;
import com.FrostedIsles.Comp.Util;

public class Moderators implements CommandExecutor {

	private static ConfigurationManager config;

	public static Inventory targetInv;
	public static HumanEntity playerClicked;
	
	@Override
	public boolean onCommand(CommandSender sender, Command c, String cmd, String[] args) {
		config = new ConfigurationManager();
		config.setup(new File(Main.getPlugin(Main.class).getDataFolder(), "config.yml"));

		Player p = null;
		boolean console = true;
		Rank rank = null;

		try {
			p = Bukkit.getPlayer(sender.getName());
			String rankStr = config.getData().getString(p.getUniqueId().toString() + ".rank");
			rank = Rank.valueOf(rankStr);
			console = false;
		} catch (Exception e) {
		}

		if (cmd.equalsIgnoreCase("ci") || cmd.equalsIgnoreCase("clear")) {
			clearInv(p, sender, args, console, rank);
		}

		if (cmd.equalsIgnoreCase("fly")) {
			fly(p, sender, args, console, rank);
		}

		if (cmd.equalsIgnoreCase("invsee")) {
			invsee(p, sender, args, console, rank);
		}

		return true;
	}

	private void invsee(Player p, CommandSender sender, String[] args, boolean console, Rank rank) {
		if (console) {
			Util.sendMsg(sender, Util.pd);
		} else {
			if (rank.getRank() >= Rank.Moderator()) {
				if (args.length != 1) {
					Util.sendMsg(sender, "&cUsage: &a>>&7/invsee {PLAYER}");
				} else {

					if (args.length == 1) {
						try {
							if (args[0].equals(sender.getName())) {
								Util.sendMsg(sender, "&cError:&7 You cannot open your own inventory!");
							} else {
								Player t = Bukkit.getPlayer(args[0]);
								Inventory targetInv = t.getInventory();
								Moderators.targetInv = targetInv;
								Moderators.playerClicked = (HumanEntity) p;
								p.openInventory(targetInv);
							}
						} catch (Exception e) {
							Util.sendMsg(sender, Util.pnf);
						}
					}
				}
			} else {
				Util.sendMsg(sender, Util.pd);
			}
		}

	}

	private void fly(Player p, CommandSender sender, String[] args, boolean console, Rank rank) {
		if (console) {
			Util.sendMsg(sender, "&cError: You must be a player to use this command");
		} else {
			if (rank.getRank() >= Rank.Moderator()) {
				if (!p.getAllowFlight() && !p.isFlying()) {
					p.setAllowFlight(true);
					p.setFlying(true);
					Util.sendMsg(p, "&cFlight mode Enabled!");
				} else {
					p.setAllowFlight(false);
					p.setFlying(false);
					Util.sendMsg(p, "&cFlight mode Disabled!");
				}
			} else {
				Util.sendMsg(sender, Util.pd);
			}
		}
	}

	private void clearInv(Player p, CommandSender sender, String[] args, boolean console, Rank rank) {
		if (console) {
			if (args.length != 1) {
				Util.sendMsg(sender, "&cUsage: &a>>&7ci {PLAYER}");
			} else {
				try {
					Player t = Bukkit.getPlayer(args[0]);
					t.getInventory().clear();
					Util.sendMsg(sender, "&7Success, You have cleared " + t.getName() + "'s Inventory!");
					Util.sendMsg(t, "&7Your Inventory has been cleared!");
				} catch (Exception e) {
					Util.sendMsg(sender, Util.pnf);
				}
			}
		} else if (rank.getRank() >= Rank.Moderator() || rank.getRank() == Rank.Builder()) {
			if (args.length == 0) {
				p.getInventory().clear();
				Util.sendMsg(sender, "&7Success, You have cleared your Inventory!");
			} else if (args.length == 1) {
				try {
					Player t = Bukkit.getPlayer(args[0]);
					t.getInventory().clear();
					Util.sendMsg(sender, "&7Success, You have cleared " + t.getName() + "'s Inventory!");
					Util.sendMsg(t, "&7Your Inventory has been cleared!");
				} catch (Exception e) {
					Util.sendMsg(sender, Util.pnf);
				}
			} else
				Util.sendMsg(sender, "&cUsage: &7/ci [PLAYER]");
		} else {
			Util.sendMsg(sender, Util.pd);
		}
	}

}
