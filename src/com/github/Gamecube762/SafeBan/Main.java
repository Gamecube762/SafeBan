package com.github.Gamecube762.SafeBan;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener{
	
	YamlConfiguration config;
	File configFile;
	
	boolean 
		AllowOfflinePlayers,
		BannedReasonTellAll,
		BannedReasonTellConsole;
	
	String
		BannedReason,
		BannedReasonTell;
	
	
	List<BanRequest> banRequests = new ArrayList<BanRequest>();
	
	@Override
	public void onEnable() {
		
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		
		getDataFolder().mkdirs();
		configFile = new File(getDataFolder(), "config.yml");
		
		config = new YamlConfiguration();
		if (configFile.exists()) config = YamlConfiguration.loadConfiguration(configFile);
		
		updateConfig();
		loadConfig();
	}
	
	@Override
	public void onDisable() {
		saveConfigFile();
	}

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	String MessageHeader = ChatColor.GREEN + "Current number of ban requests: " + ChatColor.YELLOW + banRequests.size();
    	
		if (isBanRequestsEmpty()) {sender.sendMessage(MessageHeader + "\n" + ChatColor.GREEN + "No ban requests availible"); return true;}
    	if (args.length < 1) {
    		BanRequest a = banRequests.get(0);
    		
    		sender.sendMessage(MessageHeader + "\n" +
    					ChatColor.GREEN + "Request at the top of the list(" + ChatColor.YELLOW + "ID:0" + ChatColor.GREEN + "): " + ChatColor.AQUA + a.TimeStamp + "\n" +
    					ChatColor.YELLOW + "Requester:" + ChatColor.RESET + a.requesterName + " \n" +
    					ChatColor.YELLOW + "Defendant:" + ChatColor.RESET + a.defendantName + " \n" +
    					ChatColor.YELLOW + "Reason:" + ChatColor.RESET + a.reason
    		);
    	}
    	
    	else{
    			if(args[0].equalsIgnoreCase("accept") ) {
    				if (!(args.length > 1) ) {sender.sendMessage("Needs a BanID number!"); return true;}
    				if (!Utils.isInteger(args[1]) ) {sender.sendMessage(args[1] + "Is not a number!"); return true;}
    				int id = Integer.parseInt(args[1]);
    				if (id >= banRequests.size()) {sender.sendMessage("BanID is bigger than the Request List!"); return true;}
    				
        			BanRequest a = banRequests.get(id);
        			acceptBanRequest(sender, a);
        			banRequests.remove(id);
    			} else
    			if(args[0].equalsIgnoreCase("decline") ) {
    				if (!(args.length > 1) ) {sender.sendMessage("Needs a BanID number!"); return true;}
    				if (!Utils.isInteger(args[1]) ) {sender.sendMessage(args[1] + "Is not a number!"); return true;}
    				int id = Integer.parseInt(args[1]);
    				if (id >= banRequests.size()) {sender.sendMessage("BanID is bigger than the Request List!"); return true;}
    				
    				BanRequest a = banRequests.get(id);
    				sender.sendMessage(ChatColor.GREEN + "Declining Ban Request of " + a.requesterName);
        			banRequests.remove(id);
    			}else
    			if(args[0].equalsIgnoreCase("savelist") ) if (sender.hasPermission("SafeBan.command.savelist") ) saveConfigFile();
    			else
    			if(Utils.isInteger(args[0])){
    				int id = Integer.parseInt(args[1]);
    				if (id >= banRequests.size()) {sender.sendMessage("BanID is bigger than the Request List!"); return true;}
    	    		BanRequest a = banRequests.get(0);
    				
    	    		sender.sendMessage(MessageHeader + "\n" +
        					ChatColor.GREEN + "Request at the top of the list(" + ChatColor.YELLOW + "ID:" + args[0] + ChatColor.GREEN + "): " + ChatColor.AQUA + a.TimeStamp + "\n" +
        					ChatColor.YELLOW + "Requester:" + ChatColor.RESET + a.requesterName + " \n" +
        					ChatColor.YELLOW + "Defendant:" + ChatColor.RESET + a.defendantName + " \n" +
        					ChatColor.YELLOW + "Reason:" + ChatColor.RESET + a.reason
    	    		);
    			}
    			else sender.sendMessage(ChatColor.GREEN + "Available arguments: " + ChatColor.YELLOW + "accept <ID>, Decline <ID>, savelist, <ID>");
    	}
    	return true;
    }
    
    @EventHandler
    public void onPreCommand(PlayerCommandPreprocessEvent e){
    	String msg = e.getMessage();
    	Player p = e.getPlayer();
    	
    	if(msg.toLowerCase().startsWith("/ban")){
    		if(p.hasPermission("SafeBan.bypass")) {e.getPlayer().sendMessage("Ban bypassed SafeBan."); return;}
    		
    		String[] a = msg.substring(1).split(" ");// 0 = <commandname> | 1 = target | 2+ = reason  
    		
    		if(a.length < 3) {p.sendMessage(ChatColor.RED + "Needs arguments! \n" + ChatColor.RESET + "/" + a[0] + " <Player> <reason>"); e.setCancelled(true); return;}
    		if(!AllowOfflinePlayers) if(Bukkit.getPlayer(a[1]) == null) {p.sendMessage(ChatColor.RED + a[1] + " isn't found (not online?)"); e.setCancelled(true); return;}
    		
    		String reason = "";
    		for (int i = 2; i < a.length; i++) reason = reason + " " + a[i];
    		
    		addBanRequest(p.getName(), a[1], reason);
    		p.sendMessage("Created ban request of " + a[1] + " for " + reason);
    		e.setCancelled(true);
    	}
    }
    
    public void addBanRequest(String requester, String defendant, String reason) {
    	banRequests.add( new BanRequest(requester, defendant, reason) );
    }
    
    public void acceptBanRequest(CommandSender sender, BanRequest a){
		
		sender.sendMessage(ChatColor.GREEN + "Accepting ban request of " + a.defendantName);
		
		String acceptor = "Console";
		if (Utils.isPlayer(sender)) acceptor = ((Player)sender).getName();
		
		for (Player p : Bukkit.getOnlinePlayers())
			if (p.hasPermission("SafeBan.Alert.BanWasAccepted"))
				p.sendMessage( formatMessage(BannedReasonTell, a, acceptor) );
		
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ban " + a.getDefendantName() + " " + formatMessage(BannedReason, a, acceptor) );
    }
    
    public boolean isBanRequestsEmpty() {if (banRequests.size() > 0) return false; return true;}
    
    public String formatMessage(String s, BanRequest a, String acceptor) {
    	return ChatColor.translateAlternateColorCodes('&', s)
    			.replace("%Requester%", a.getRequesterName() )
    			.replace("%Defendant%", a.getDefendantName() )
    			.replace("%Reason%", a.getReason() )
    			.replace("%Accepter%", acceptor );
    }

    private void updateConfig() {
    	config.options().header("BanRequest config.yml | Plugin by Gamecube762\n" +
    			"Messages can be colorcoded(&0-9,a-f,k,m,r,l,n,o) and can use these placeholders: \n" +
    			"%Requester% %Reason% %Defendant% %Accepter% \n" +
    			"BannedReason is the reason for banning a player \n" +
    			"BannedReasonTell is what to tell everyone else"
    			);
    	
    	if (!config.contains("Settings.AllowOfflinePlayers")) config.set("Settings.AllowOfflinePlayers", false);
    	if (!config.contains("Settings.BannedReason")) config.set("Settings.BannedReason", "Banned by %Requester% for %Reason%");
    	if (!config.contains("Settings.BannedReasonTell")) config.set("Settings.BannedReasonTell", "%Defendant% was banned by %Requester% for %Reason% and approved by %Accepter%");
    	if (!config.contains("Settings.BannedReasonTellAll")) config.set("Settings.BannedReasonTell", false);
    	if (!config.contains("Settings.BannedReasonTellConsole")) config.set("Settings.BannedReasonTell", true);
    	

    	if (!config.contains("Settings.BanRequests")) config.set("Settings.BanRequests.Empty", true);
    	
    	try {config.save(configFile);} catch (IOException e) {getLogger().severe("Could not save config.yml!!");}
    }
    
    private void saveConfigFile() {
        config.set("Settings.AllowOfflinePlayers", AllowOfflinePlayers);
        
        config.set("Settings.BannedReasonTell", false);
        config.set("Settings.BannedReasonTell", true);
        
        //save request list
        int i = 0;
        for (BanRequest a : banRequests) {
            config.set("BanRequests." + i + ".requesterName", a.requesterName);
            config.set("BanRequests." + i + ".defendantName", a.defendantName);
            config.set("BanRequests." + i + ".reason"       , a.reason       );
            config.set("BanRequests." + i + ".TimeStamp"    , a.TimeStamp    );
            i++;
        }
    }
    
    private void loadConfig() {
    	AllowOfflinePlayers = config.getBoolean("Settings.AllowOfflinePlayers");
    	BannedReasonTellAll = config.getBoolean("Settings.BannedReasonTellAll");
    	BannedReasonTellConsole = config.getBoolean("Settings.BannedReasonTellConsole");
    
    	BannedReason = config.getString("Settings.BannedReason");
    	BannedReasonTell = config.getString("Settings.BannedReasonTell");
    
    	//Getting the request list of ban requests
    	if (config.getBoolean("Settings.BanRequests.Empty")) return;//no need to run code below if its empty
    	
    	Set<String> a = config.getConfigurationSection("Settings.BanRequests").getKeys(false);
    	String b = "config.yml | BanRequests. | Error in Request(s): ";
    	
    	for (String s : a) {
    		if (!config.contains("BanRequests." + s + ".requester") |
    			!config.contains("BanRequests." + s + ".defendant") |
    			!config.contains("BanRequests." + s + ".reason"   ) |
    			!config.contains("BanRequests." + s + ".TimeStamp") ) b = b + "\n " + s; 
    		else {
    			String
    				requester = config.getString("BanRequests." + s + ".requester"), 
    				defendant = config.getString("BanRequests." + s + ".defendant"), 
    				reason	  = config.getString("BanRequests." + s + ".reason"   ),
    				TimeStamp = config.getString("BanRequests." + s + ".TimeStamp");
    		
    			banRequests.add(new BanRequest(requester, defendant, reason, TimeStamp));
    		}
    	}
    	if (!b.equals("config.yml | BanRequests. | Error in formatting: ")) getLogger().severe(b);
    }
}
