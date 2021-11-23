package me.deadlight.ezquesting.UI;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import me.deadlight.ezquesting.EzQuesting;
import me.deadlight.ezquesting.Listeners.ChatListener;
import me.deadlight.ezquesting.Objects.Quest;
import me.deadlight.ezquesting.Utils.Utilities.XMaterial;
import me.deadlight.ezquesting.Utils.Utilities.XSound;
import me.deadlight.ezquesting.Utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EditorGUI {

    public static void showGui(Player player) {

        PaginatedGui gui = new PaginatedGui(6, Utils.colorify("&eQuest book"));
        GuiItem dummyItem = ItemBuilder.from(XMaterial.GRAY_STAINED_GLASS_PANE.parseMaterial()).setName(Utils.colorify("&d")).asGuiItem();
        dummyItem.setAction(event -> {
            event.setCancelled(true);
        });
        gui.getFiller().fillBottom(dummyItem);
        GuiItem nextItem = ItemBuilder.skull().texture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTYzMzlmZjJlNTM0MmJhMThiZGM0OGE5OWNjYTY1ZDEyM2NlNzgxZDg3ODI3MmY5ZDk2NGVhZDNiOGFkMzcwIn19fQ==").name(Component.text("Next Page")).asGuiItem();
        nextItem.setAction(event -> {
            event.setCancelled(true);
            XSound.ENTITY_EXPERIENCE_ORB_PICKUP.play(player);
            gui.next();
        });
        gui.setItem(52, nextItem);
        GuiItem previousItem = ItemBuilder.skull().texture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjg0ZjU5NzEzMWJiZTI1ZGMwNThhZjg4OGNiMjk4MzFmNzk1OTliYzY3Yzk1YzgwMjkyNWNlNGFmYmEzMzJmYyJ9fX0=").name(Component.text("Previous page")).asGuiItem();
        previousItem.setAction(event -> {
            event.setCancelled(true);
            XSound.ENTITY_EXPERIENCE_ORB_PICKUP.play(player);
            gui.previous();
        });
        gui.setItem(46, previousItem);

        //adding quest
        GuiItem addQuest = ItemBuilder.skull().texture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWEyZDg5MWM2YWU5ZjZiYWEwNDBkNzM2YWI4NGQ0ODM0NGJiNmI3MGQ3ZjFhMjgwZGQxMmNiYWM0ZDc3NyJ9fX0=").name(Component.text("Add quest")).asGuiItem();
        addQuest.setAction(event -> {
            event.setCancelled(true);
            XSound.ITEM_BOOK_PUT.play(player);
            player.closeInventory();
            player.sendMessage(Utils.colorify("&aPlease write the name of your desired quest in chat (from 1 to 20 characters)"));
            ChatListener.chatResponse.put(player.getUniqueId(), "");
        });
        gui.setItem(49, addQuest);


        //setting quests from here
        List<Quest> allActiveQuests = Utils.activeQuests;
        for (Quest quest : allActiveQuests) {

            GuiItem activeQuest = ItemBuilder.from(XMaterial.BOOK.parseMaterial()).setName(Utils.colorify("&a&l" + quest.name)).setLore(Utils.colorify("&7Click to manage")).asGuiItem();
            activeQuest.setAction(event -> {
                event.setCancelled(true);
                XSound.BLOCK_LEVER_CLICK.play(player);
                QuestManageUI.showGUI(player, quest);
            });
            gui.addItem(activeQuest);
        }



        gui.open(player);
        XSound.BLOCK_NOTE_BLOCK_HARP.play(player);


    }

    public static void createQuest(Player player, String name) {
        UUID questID = UUID.randomUUID();
        File questsYml = new File(EzQuesting.pluginInstance.getDataFolder(), "quests.yml");
        if (!questsYml.exists()) {
            player.sendMessage(Utils.colorify("&cQuests.yml file is missing, please restart your server and try again."));
            return;
        }
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(questsYml);
        String configRoute = "quests." + questID.toString() + ".";
        configuration.set(configRoute + "ready", false);
        configuration.set(configRoute + "name", name);
        configuration.set(configRoute + "creator", player.getUniqueId().toString());
        configuration.set(configRoute + "description", "Your custom description");
        configuration.set(configRoute + "post-complete-commands", new ArrayList<String>());
        configuration.set(configRoute + "pre-conversation", new ArrayList<String>());
        configuration.set(configRoute + "post-conversation", new ArrayList<String>());
        String phaseRoute = configRoute + "phases.1.";
        configuration.set(phaseRoute + "name", "Your custom phase name");
        configuration.set(phaseRoute + "description", "Your custom phase description");
        configuration.set(phaseRoute + "pre-conversation", new ArrayList<String>());
        configuration.set(phaseRoute + "post-conversation", new ArrayList<String>());
        configuration.set(phaseRoute + "post-completion-commands", new ArrayList<String>());
        configuration.set(phaseRoute + "objective", "WALK/BREAK/PLACE/COMMAND/KILL (CHOOSE ONE)");
        configuration.set(phaseRoute + "objective-data.count", 0);
        configuration.set(phaseRoute + "objective-data.types", new ArrayList<String>());
        configuration.set(phaseRoute + "objective-data.command", "This value is only for command objective (ex: /day)");
        try {
            configuration.save(questsYml);
            player.sendTitle("", Utils.colorify("&7Check &cQuests.yml"));
            XSound.BLOCK_DISPENSER_LAUNCH.play(player);
            player.sendMessage(Utils.colorify("&aGreat!, &7now it's time to head over quests.yml file, in the configuration folder of EzQuesting plugin, and find/edit the config as you wish. "));
            player.sendMessage(Utils.colorify("&7The &e" + name + "&7's quest id will be: &b" + questID));
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
