package com.bukkit.mattbot.RandomNameColor;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;

public class RandomNameColorPlayerListener extends PlayerListener{
	public static RandomNameColor plugin;
	public RandomNameColorPlayerListener(RandomNameColor instance) {
		plugin = instance;
	}
	
	public void onPlayerCommand(PlayerChatEvent event) {
		event.getPlayer().setDisplayName(plugin.userColors.get(event.getPlayer()) + event.getPlayer().getName() + ChatColor.WHITE);
		if (!event.isCancelled()){
			String[] split = event.getMessage().split(" ");
			Player player = event.getPlayer();
			if (split[0].equalsIgnoreCase("/shufflecolors")) {
				if(!(plugin.GMrunning && RandomNameColor.Permissions.has(player, "randomnamecolor.shuffle"))){
					player.sendMessage(ChatColor.RED+"You do not have permission to do that");
					return;				
				}
								
				if (split.length > 1) {
					player.sendMessage("command /shufflecolors doesn't accept arguments");
				} else {
					player.sendMessage(ChatColor.YELLOW + "Change places!");
					plugin.assignColors();
				}
				
				event.setCancelled(true);
			} else if (split[0].equalsIgnoreCase("/changecolor")) {
				String helpMsg = "Syntax: /changecolor [player] color [overwrite]";
				if (split.length==2) { //assign color to self
					if(!(plugin.GMrunning && RandomNameColor.Permissions.has(player,"randomnamecolor.change.self.unique"))){
						player.sendMessage(ChatColor.RED+"You do not have permission to do that");
						return;				
					}
					
					if (plugin.validColor(split[1])) {
						if (plugin.availColor(split[1])) {
							plugin.setColor(player, ChatColor.valueOf(split[1].toUpperCase()));
						} else {
							player.sendMessage(split[1] + " is not available. Add overwrite flag to assign anyway.");
						}
					} else {
						player.sendMessage(split[1] + " is not a valid color. Use /listcolors [inuse,avail]");
					}
				} else if (split.length==3) { //assign color to player OR assign color to self, overwrite				
					if (Arrays.asList(plugin.getServer().getOnlinePlayers()).contains(plugin.getServer().getPlayer(split[1]))) { //first param is an online player
						if(!(plugin.GMrunning && RandomNameColor.Permissions.has(player, "randomnamecolor.change.others"))){
							player.sendMessage(ChatColor.RED+"You do not have permission to do that");
							return;				
						}
						
						if (plugin.validColor(split[2])) {
							plugin.freeColor(plugin.getServer().getPlayer(split[1]));
							plugin.setColor(plugin.getServer().getPlayer(split[1]), ChatColor.valueOf(split[2].toUpperCase()));
						} else {
							player.sendMessage(helpMsg);
						}
					} else if (plugin.validColor(split[1])) { //first param is color
						if(!(plugin.GMrunning && RandomNameColor.Permissions.has(player, "randomnamecolor.change.self.overwrite"))){
							player.sendMessage(ChatColor.RED+"You do not have permission to do that");
							return;				
						}
						
						if (split[2] == "1" || split[2]== "true" || split[2] == "t") {
							plugin.freeColor(player);
							plugin.setColor(player,ChatColor.valueOf(split[2].toUpperCase()));
						} else {
							player.sendMessage(helpMsg);
						}
					} else { //first param is offline player, invalid color, or something irrelevant
						player.sendMessage(helpMsg);
					}
				} else if (split.length==4) { //assign color to player, overwrite
					if(!(plugin.GMrunning && RandomNameColor.Permissions.has(player, "randomnamecolor.change.others"))){
						player.sendMessage(ChatColor.RED+"You do not have permission to do that");
						return;				
					}
					
					if (Arrays.asList(plugin.getServer().getOnlinePlayers()).contains(plugin.getServer().getPlayer(split[1])) && plugin.validColor(split[2]) && (split[3] == "1" || split[3]== "true" || split[3] == "t")) {
						plugin.freeColor(plugin.getServer().getPlayer(split[1]));
						plugin.setColor(plugin.getServer().getPlayer(split[1]),ChatColor.valueOf(split[2].toUpperCase()));
					} else {
						player.sendMessage(helpMsg);
					}
				} else { //help
					player.sendMessage(helpMsg);
				}
				
				
				
				event.setCancelled(true);
			} else if (split[0].equalsIgnoreCase("/listcolors")) {
				
				if(!(plugin.GMrunning && RandomNameColor.Permissions.has(player, "randomnamecolor.list"))){
					player.sendMessage(ChatColor.RED+"You do not have permission to do that");
					return;					
				}
				
				
				if (split.length == 1) {
					String all = "";
					for(ChatColor c: plugin.colors) {
						all += c.name().toLowerCase() + " ";					
					}
					//System.out.println(all);
					
					player.sendMessage(all);
				} else if (split[1].equalsIgnoreCase("inuse")) {
					String inuse = "";
					for(Player p: plugin.getServer().getOnlinePlayers()) {
						if (!inuse.contains(plugin.userColors.get(p).name().toLowerCase())) {
							inuse += plugin.userColors.get(p).name().toLowerCase() + " ";
						}
					}
					player.sendMessage(inuse);
				} else if (split[1].equalsIgnoreCase("avail")) {
					String avail = "";
					for(ChatColor c: plugin.colors) {
						if (!plugin.userColors.containsValue(c)) {
							avail += c.name().toLowerCase() + " ";
						}
					}
					player.sendMessage(avail);
				} else {
					player.sendMessage("Syntax: /listcolors [inuse|avail]");
				}
				event.setCancelled(true);
			}
		}
	}
	
	public void onPlayerJoin(PlayerEvent event) {
		plugin.assignPlayerColor(event.getPlayer());		
	}
	
	public void onPlayerQuit(PlayerEvent event) {
		plugin.freeColor(event.getPlayer());
	}
}
