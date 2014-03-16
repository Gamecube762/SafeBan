package com.github.Gamecube762.SafeBan;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Gamecube762.
 * Utils class used for multiple functions.
 */

public class Utils {

    public static long parseTime(String s){
        if(s.equalsIgnoreCase("day")) return 0;
        if(s.equalsIgnoreCase("night")) return 12500;
        return 0;
    }
    public static long parseTimeExtra(String s){
        if(s.equalsIgnoreCase("day")) return 0;
        if(s.equalsIgnoreCase("night")) return 12500;
        if(s.equalsIgnoreCase("dawn")) return 22500;
        if(s.equalsIgnoreCase("dusk")) return 12500;
        return 0;
    }
    public static long parseTimeNew(String s){
        if(s.equalsIgnoreCase("dawn")) return 22500;
        if(s.equalsIgnoreCase("dusk")) return 12500;
        return 0;
    }

    public static boolean IsBoolean(String s) { return ( s.equalsIgnoreCase("true") || s.equalsIgnoreCase("false") ); }
    public static boolean IsBoolean2(String s) { return ( s.equalsIgnoreCase("on") || s.equalsIgnoreCase("off") ); }

    public static boolean IsBoolean(int i) { return ( i==1 || i==0 ); }

    public static boolean parseBoolean(String s) {return (s.equalsIgnoreCase("true"));}
    public static boolean parseBoolean2(String s) {return (s.equalsIgnoreCase("on"));}
    public static boolean parseBoolean(int i) {return (i==1);}
    public static boolean parseBoolean(double i) {return (i==1);}
    public static boolean parseBoolean(long i) {return (i==1);}

    public static String parseBooleanToString(boolean b) {return (b) ? "true" : "false";}
    public static String parseBooleanToString2(boolean b) {return (b) ? "on" : "off";}
	
	public static int toInt(boolean b) {if (b) return 1; return 0;}

    public static boolean isPlayer(CommandSender sender) {if (sender instanceof Player) return true; return false;}

    public static boolean isPlayer(String p) {return (Bukkit.getPlayer(p)!=null);}

    public static boolean isCmdBlock(CommandSender sender) {if (sender instanceof CommandBlock) return true; return false;}

    public static Block returnCBBlock(BlockCommandSender sender) {if (sender instanceof CommandBlock) return sender.getBlock(); return null;}

    public static boolean hasPermission(CommandSender sender, String cmdPerm) {if (!isPlayer(sender))return true; return sender.hasPermission(cmdPerm);}

    public static Location sLocation(Player Sender) {if (isPlayer(Sender)) return Sender.getLocation(); return null;}

    public static boolean isInteger(String s) {try {Integer.parseInt(s);} catch (NumberFormatException e) {return false;} return true;}

    public static boolean isDouble(String s) {try {Double.parseDouble(s);} catch (NumberFormatException e) {return false;} return true;}

    public static boolean isLong(String s) {try {Long.parseLong(s);} catch (NumberFormatException e) {return false;} return true;}

}
