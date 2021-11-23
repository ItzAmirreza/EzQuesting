package me.deadlight.ezquesting.Commands;

import me.deadlight.ezquesting.EzQuesting;
import me.deadlight.ezquesting.UI.EditorGUI;
import me.deadlight.ezquesting.UI.QuestsGUI;
import me.deadlight.ezquesting.Utils.Utils;
import me.deadlight.ezquesting.Utils.Utilities.XSound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class MainCommands implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            //commmands
            //eq editor
            //eq quests
            //eq about
            List<String> possibleArgs = Arrays.asList("about", "editor", "quests");
            Player player = (Player) sender;
            if (args.length == 0) {
                sendHelp(player);
                return true;
            }
            String firstArg = args[0].toLowerCase();
            if (possibleArgs.contains(firstArg)) {

                if (firstArg.equalsIgnoreCase("editor")) {
                    if (player.hasPermission("eq.admin")) {
                        //open editor gui
                        EditorGUI.showGui(player);

                    } else {
                        player.sendMessage(Utils.colorify("&cYou don't have permission to execute this command."));
                        return true;
                    }
                } else if (firstArg.equalsIgnoreCase("quests")) {
                    //open quests gui
                    QuestsGUI.showGui(player);
                    return true;
                } else if (firstArg.equalsIgnoreCase("about")) {
                    //show about
                    player.sendTitle(Utils.colorify("&aEz&eQuesting"), Utils.colorify("&dBy Dead_Light"));
                    player.sendMessage(Utils.colorify("&7Created as a trail plugin for &e&lDevRoom &7team. \n&7You can check out my GitHub: https://github.com/ItzAmirreza"));
                    XSound.BLOCK_NOTE_BLOCK_BASS.play(player);
                    return true;
                } else {
                    sendHelp(player);
                    return true;
                }

            } else {
                sendHelp(player);
                return true;
            }




        } else {
            EzQuesting.logConsole("&eSorry! You can not execute any commands from console.");
            return false;
        }
        return false;
    }


    public void sendHelp(Player player) {
        String helpMessage = "&eEzQuesting plugin by &bDead_Light\n" +
                "&7/eq editor | Open editor to see quests/edit them/create/remove\n" +
                "&7/eq quests | Open a list of available quests, and their status\n" +
                "&7/eq about | About the plugin";
        player.sendMessage(Utils.colorify(helpMessage));
    }
}
