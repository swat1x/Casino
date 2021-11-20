package ru.swat1x.casino.game.jackpot;

import com.google.common.collect.Lists;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.scheduler.BukkitRunnable;
import ru.swat1x.casino.Casino;

import java.text.DecimalFormat;
import java.util.List;

public class JackpotGameStartTask extends BukkitRunnable {

    private static final List<Integer> notificationSeconds = Lists.newArrayList(25, 20, 15, 10, 5, 4, 3, 2, 1);

    private final Jackpot jackpot;
    private Integer timeToEnd = 25;

    private boolean end = false;

    public JackpotGameStartTask(Jackpot jackpot){
        this.jackpot = jackpot;
    }

    @Override
    public void run() {
        if(timeToEnd == 0){
            if(end){
                return;
            }
            end = true;
            JackpotVictory win = jackpot.initFinal();
            DecimalFormat g = new DecimalFormat("##.##");
            String message = Casino.getInstance().getConfig().getString("gameSettings.messages.jackpot.victoryMessage")
                    .replace("&", "§")
                    .replace("%player", win.getWinner())
                    .replace("%amount", win.getAmount()+"")
                    .replace("%chanсe", g.format(win.getChance()));
            Bukkit.broadcastMessage(message);
            jackpot.clear();
            cancel();
            return;
        }
        if(notificationSeconds.contains(timeToEnd)){
            for(String s : jackpot.getPlayers()){
                Player player = Bukkit.getPlayer(s);
                if(player != null){
                    String message = Casino.getInstance().getConfig().getString("gameSettings.messages.jackpot.timeToEnd")
                            .replace("&", "§")
                            .replace("%time", timeToEnd+"");
                    player.sendMessage(message);
                }
            }
        }
        timeToEnd--;
    }
}
