package ru.swat1x.casino.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.swat1x.casino.Casino;
import ru.swat1x.casino.game.jackpot.Jackpot;

public class CasinoCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        if (!sender.hasPermission("casino.use")){
            sender.sendMessage("§cНедостаточно прав");
            return false;
        }
        if(args.length == 0){
            sender.sendMessage("§fСоздать джекпот стол §7- §e/"+label+" create <название>");
            sender.sendMessage("§fУдалить джекпот стол §7- §e/"+label+" remove <название>");
            sender.sendMessage("§fПерезагрузить плагин §7- §e/"+label+" reload");
            return false;
        }
        if(args[0].equalsIgnoreCase("reload")){
            Casino.reload();
            sender.sendMessage("§7[§cCasino§7] §fПлагин перезагружен");
            return false;
        }
        if(args[0].equalsIgnoreCase("create")){
            if(args.length < 2){
                sender.sendMessage("§cВведите название стола");
                return false;
            }
            Jackpot.createTable(args[1].toLowerCase(), player.getLocation());
            sender.sendMessage("§7[§cCasino§7] §fДжекпот стол §e"+args[1].toLowerCase()+"§f успешно создан/перемещён");
            return false;
        }
        if(args[0].equalsIgnoreCase("remove")){
            if(args.length < 2){
                sender.sendMessage("§cВведите название стола");
                return false;
            }
            if(!Jackpot.remove(args[1].toLowerCase())){
                sender.sendMessage("§cСтол не найден");
                return false;
            }
            sender.sendMessage("§7[§cCasino§7] §fДжекпот стол §e"+args[1].toLowerCase()+"§f успешно удалён");
            return false;
        }
        return false;
    }
}
