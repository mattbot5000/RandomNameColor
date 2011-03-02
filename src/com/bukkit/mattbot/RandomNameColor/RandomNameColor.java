package com.bukkit.mattbot.RandomNameColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;


import com.nijikokun.bukkit.Permissions.Permissions;
import com.nijiko.permissions.PermissionHandler;
import org.anjocaido.groupmanager.GroupManager;


public class RandomNameColor extends JavaPlugin{
	public static PermissionHandler Permissions = null;
	private final RandomNameColorPlayerListener playerListener = new RandomNameColorPlayerListener(this);
    //private final RandomNameColorBlockListener blockListener = new RandomNameColorBlockListener(this);
    public final HashMap<Player,ChatColor> userColors = new HashMap<Player, ChatColor>();
    //private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
    //public final ChatColor[] colors = {ChatColor.AQUA,ChatColor.BLACK,ChatColor.BLUE,ChatColor.DARK_AQUA,ChatColor.DARK_BLUE,ChatColor.DARK_GRAY,ChatColor.DARK_GREEN,ChatColor.DARK_PURPLE,ChatColor.DARK_RED,ChatColor.GOLD,ChatColor.GRAY,ChatColor.GREEN,ChatColor.LIGHT_PURPLE,ChatColor.RED,ChatColor.WHITE,ChatColor.YELLOW};
    public final ChatColor[] colors = {ChatColor.AQUA,ChatColor.BLUE,ChatColor.DARK_AQUA,ChatColor.DARK_BLUE,ChatColor.DARK_GRAY,ChatColor.DARK_GREEN,ChatColor.DARK_PURPLE,ChatColor.DARK_RED,ChatColor.GOLD,ChatColor.GRAY,ChatColor.GREEN,ChatColor.RED,ChatColor.WHITE,ChatColor.YELLOW};
    public boolean GMrunning;
    //public PermissionHandler Security;
    
   public void setupPermissions() {
    	Plugin test = this.getServer().getPluginManager().getPlugin("Permissions");


    	if(RandomNameColor.Permissions == null) {
    	    if(test != null) {
    	    	RandomNameColor.Permissions = ((Permissions)test).getHandler();
    	    } else {
    	    	//System.out.println("[RandomNameColor] Permission system not enabled. Disabling plugin.");
    	    	//this.getServer().getPluginManager().disablePlugin(this);
    	    }
    	}
    }
    /*
	public RandomNameColor(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin, ClassLoader cLoader) {
        super(pluginLoader, instance, desc, folder, plugin, cLoader);
        // TODO: Place any custom initialization code here

        // NOTE: Event registration should be done in onEnable not here as all events are unregistered when a plugin is disabled
    }*/

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
		//System.out.println("RandomNameColor Disabled");
	}

	@Override
	public void onEnable() {
		//setupPermissions();
		GMrunning = setupGroupManager();
		
		PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Highest, this);
        pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Highest, this);
        pm.registerEvent(Event.Type.PLAYER_COMMAND, playerListener, Priority.Highest, this);
        pm.registerEvent(Event.Type.PLAYER_CHAT, playerListener, Priority.Highest, this);
        
        PluginDescriptionFile pdfFile = this.getDescription();
        //this.getServer().broadcastMessage(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled");
        if (!GMrunning) {
        	System.out.println( "[" + pdfFile.getName() + "] GroupManager not running. Commands available to ALL users.");
        }
        System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled" );
        assignColors();
	}
	
	public boolean setupGroupManager() {
		 Plugin p = this.getServer().getPluginManager().getPlugin("GroupManager");
	        if (p != null) {
	            if (!p.isEnabled()) {
	                this.getServer().getPluginManager().enablePlugin(p);
	            }
	            GroupManager gm = (GroupManager) p;
	            //groupManager = gm;
	            Permissions = gm.getPermissionHandler();
	            return true;
	        } else {
	            return false;
	        	//this.getPluginLoader().disablePlugin(this);
	        }
	}
	
	/*
	public boolean isDebugging(final Player player) {
        if (debugees.containsKey(player)) {
            return debugees.get(player);
        } else {
            return false;
        }
    }

    public void setDebugging(final Player player, final boolean value) {
        debugees.put(player, value);
    }
    */
    
    public void assignColors() {
    	//System.out.println("MIXING COLORS");
    	this.userColors.clear();
    	Player[] players = this.getServer().getOnlinePlayers();
    	for (Player p : players) {
    		assignPlayerColor(p);
    	}
    }
    
    public void assignPlayerColor(Player player) {
    	ChatColor colorToSet = null;
    	ArrayList<ChatColor> freeColors = new ArrayList<ChatColor>();
    	
    	for (ChatColor color : colors) {
    		if (!this.userColors.containsValue(color)) {
    			freeColors.add(color);
    		}
    	}
    	
    	Random gen = new Random();
    	if (freeColors.size()==0) {    		
    		colorToSet = colors[gen.nextInt(colors.length)];
    	} else {
    		colorToSet = freeColors.get(gen.nextInt(freeColors.size()));
    	}
    	setColor(player,colorToSet);

    }
    
    public void setColor(Player p, ChatColor c) {
    	this.userColors.put(p,c);
    	p.setDisplayName(c + p.getName() + ChatColor.WHITE);
    }
    
    public void releaseColor(Player player) {
    	this.userColors.remove(player);
    }
    
    public boolean validColor(String c) {
    	//return ChatColor.valueOf(c.toUpperCase()) != null;
    	try {    		
    		ChatColor color = ChatColor.valueOf(c.toUpperCase());
    		for (ChatColor a : colors) {
    			if (a==color) {
    				return true;
    			}
    		}
    		
    		return false;
    	} catch(java.lang.IllegalArgumentException e) {
    		return false;
    	}    	
    }
    
    public boolean availColor(String c) {
    	return !userColors.containsValue(ChatColor.valueOf(c.toUpperCase()));
    }
    public boolean isPlayer(String p) {
    	return Arrays.asList(getServer().getOnlinePlayers()).contains(getServer().getPlayer(p));
    }
}
