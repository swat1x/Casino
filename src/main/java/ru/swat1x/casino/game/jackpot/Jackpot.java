package ru.swat1x.casino.game.jackpot;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import ru.swat1x.casino.Casino;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Jackpot {

    private final String tableName;

    private static HashMap<String, List<Bet>> betsMap = new HashMap<>();

    private static HashMap<String, Hologram> holograms = new HashMap<>();
    private static List<Jackpot>  allJackpots = new ArrayList<>();

    private static List<Bet> bets = new ArrayList<>();

    private ConfigurationSection getSection(){
        return Casino.getInstance().getConfig().getConfigurationSection("jackpot."+tableName);
    }

    public Jackpot(String name){
        tableName = name;
        bets = getBets();
    }

    public String getName(){
        return tableName;
    }

    public List<String> getPlayers(){
        List<String> list = new ArrayList<>();
        for(Bet bet : bets){
            list.add(bet.getPlayer());
        }
        return list;
    }

    public List<Bet> getBets(){
        if(!betsMap.containsKey(tableName)){
            return new ArrayList<>();
        }
        return betsMap.get(tableName);
    }

    public boolean addBet(String username, int amount){
        Bet bet = new Bet(username, amount);
        for(Bet b : bets){
            if(b.getPlayer().equalsIgnoreCase(bet.getPlayer())){
                return false;
            }
        }
        bets.add(bet);
        savePlayers();
        recreateHologram();
        if(bets.size() == 2){
            new JackpotGameStartTask(this).runTaskTimer(Casino.getInstance(), 0, 20);
        }
        return true;
    }

    public void clear(){
        bets.clear();
        betsMap.remove(tableName);
        recreateHologram();
    }

    public int getMin(){
        return Casino.getInstance().getConfig().getInt("jackpot."+tableName+".bet.min");
    }
    public int getMax(){
        return Casino.getInstance().getConfig().getInt("jackpot."+tableName+".bet.max");
    }

    public JackpotVictory initFinal(){
        double x = (Math.random() * ((getBank() - 0) + 1)) + 0;
        int a = 0;
        JackpotVictory jv = null;
        for(Bet bet : bets){
            a = a + bet.getBet();
            if(a >= x){
                jv = new JackpotVictory(bet.getPlayer(), bet.getChanceFrom(getBank()), getBank());
                break;
            }
        }
        return jv;
    }

    public boolean containsBetFromPlayer(String player){
        for(Bet b : bets){
            if(b.getPlayer().equalsIgnoreCase(player)){
                return false;
            }
        }
        return true;
    }

    public int getBank(){
        if(bets.isEmpty()){
            return 0;
        }
        int a = 0;
        for(Bet bet : bets){
            a = a+bet.getBet();
        }
        return a;
    }

    private void savePlayers(){
        betsMap.remove(tableName);
        betsMap.put(tableName, bets);
    }

    public Location getLocation(){
        if(getSection() == null){
            return null;
        }
        return new Location(
                Bukkit.getWorld(getSection().getString("place.world", "world")),
                getSection().getDouble("place.x"),
                getSection().getDouble("place.y"),
                getSection().getDouble("place.z")
        );
    }

    public void recreateHologram(){
        FileConfiguration data = Casino.getInstance().getConfig();
        List<String> list = data.getStringList("jackpot."+tableName+".hologram");
        if(holograms.containsKey(tableName)){
            holograms.get(tableName).delete();
        }
        Hologram hologram = HologramsAPI.createHologram(Casino.getInstance(), getLocation().add(0, 2, 0));
        holograms.remove(tableName);
        holograms.put(tableName, hologram);
        for(String s : list){
            s = s.replace("&", "§");
            s = s.replace("%usersAmount", getPlayers().size()+"");
            s = s.replace("%bank", getBank()+"");
            hologram.appendTextLine(s.replace("&", "§"));
        }
    }

    public static List<Jackpot> getAll(){
        return allJackpots;
    }

    public static void createTable(String name, Location location){
        File file = new File(Casino.getInstance().getDataFolder()+File.separator+"config.yml");
        FileConfiguration data = Casino.getInstance().getConfig();
        ConfigurationSection c = data.getConfigurationSection("jackpot");

        c.set(name+".bet.min", 10);
        c.set(name+".bet.max", 500);
        c.set(name+".hologram", Lists.newArrayList(
                "&7> &eИгральный стол &7<",
                "&7От 10$ до 500$",
                "",
                "Встаньте рядом со столом",
                "и напишите &e/bet <сумма>",
                "",
                "В игре &e%usersAmount&f человек",
                "Общий банк &e%bank&f долларов"));
        c.set(name+".place.world", location.getWorld().getName());
        c.set(name+".place.x", location.getX());
        c.set(name+".place.y", location.getY());
        c.set(name+".place.z", location.getZ());

        try {
            data.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Casino.getInstance().reloadConfig();
        allJackpots.add(new Jackpot(name));
        new Jackpot(name).recreateHologram();
    }

    public static boolean remove(String name){
        File file = new File(Casino.getInstance().getDataFolder()+File.separator+"config.yml");
        FileConfiguration data = Casino.getInstance().getConfig();
        ConfigurationSection c = data.getConfigurationSection("jackpot."+name);

        if(c == null){
            return false;
        }

        data.set("jackpot."+name, null);

        try {
            data.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Casino.reload();
        return true;
    }

    public static void enable(){
        for(Hologram hologram : holograms.values()){
            hologram.delete();
        }
        holograms.clear();
        Bukkit.getConsoleSender().sendMessage("§7[§cCasino§7] §fНачинаю создание голограмм казино...");
        FileConfiguration data = Casino.getInstance().getConfig();
        ConfigurationSection c = data.getConfigurationSection("jackpot");
        for(String j : c.getKeys(false)){
            Jackpot jackpot = new Jackpot(j);
            allJackpots.add(jackpot);
            Bukkit.getConsoleSender().sendMessage("§7[§cCasino§7] §fСоздан Jackpot стол §e"+jackpot.tableName);
            jackpot.recreateHologram();
        }
        Bukkit.getConsoleSender().sendMessage("§7[§cCasino§7] §fГолограммы созданы");
    }

}
