package ru.swat1x.casino;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.swat1x.casino.command.BetCommand;
import ru.swat1x.casino.command.CasinoCommand;
import ru.swat1x.casino.game.jackpot.Jackpot;

public final class Casino extends JavaPlugin {

    private static Casino casinoPlugin;

    public static Casino getInstance(){
        return casinoPlugin;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        casinoPlugin = Casino.getPlugin(Casino.class);

        Jackpot.enable();

        getCommand("casino").setExecutor(new CasinoCommand());
        getCommand("bet").setExecutor(new BetCommand());
    }

    public static void reload(){
        getInstance().reloadConfig();
        Jackpot.enable();
    }

    public static Jackpot getJackpotTableNearPlayer(Player player){
        for(Jackpot jackpot : Jackpot.getAll()){
            if(jackpot.getLocation() == null){
                continue;
            }
            if(jackpot.getLocation().getWorld() != player.getLocation().getWorld()){
                continue;
            }
            if(jackpot.getLocation().distance(player.getLocation()) > 4){
                continue;
            }
            return jackpot;
        }
        return null;
    }

}
