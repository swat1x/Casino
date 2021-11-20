package ru.swat1x.casino.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.swat1x.casino.Casino;
import ru.swat1x.casino.game.jackpot.Jackpot;

public class BetCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        Jackpot jackpot = Casino.getJackpotTableNearPlayer(player);
        FileConfiguration cfg = Casino.getInstance().getConfig();
        if(jackpot == null){
            sender.sendMessage(cfg.getString("gameSettings.messages.jackpot.goToTable").replace("&", "§"));
            return false;
        }
        if(args.length == 0){
            sender.sendMessage(cfg.getString("gameSettings.messages.jackpot.insertAmount").replace("&", "§"));
            return false;
        }
        int amount;
        try {
            amount = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage(cfg.getString("gameSettings.messages.jackpot.notInteger").replace("&", "§"));
            return false;
        }
        if(amount < jackpot.getMin()){
            sender.sendMessage(cfg.getString("gameSettings.messages.jackpot.tooLow").replace("&", "§").replace("%amount", jackpot.getMin()+""));
            return false;
        }
        if(amount > jackpot.getMax()){
            sender.sendMessage(cfg.getString("gameSettings.messages.jackpot.tooHigh").replace("&", "§").replace("%amount", jackpot.getMax()+""));
            return false;
        }
        if(!jackpot.addBet(sender.getName(), amount)){
            sender.sendMessage(cfg.getString("gameSettings.messages.jackpot.alreadyBet").replace("&", "§"));
        }
        else{
            sender.sendMessage(cfg.getString("gameSettings.messages.jackpot.bet").replace("&", "§").replace("%amount", amount+""));
        }
        return false;
    }
}
