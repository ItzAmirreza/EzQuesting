package me.deadlight.ezquesting.UI;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import me.deadlight.ezquesting.Objects.Quest;
import me.deadlight.ezquesting.Utils.Utils;
import me.deadlight.ezquesting.Utils.Utilities.XMaterial;
import me.deadlight.ezquesting.Utils.Utilities.XSound;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuestsGUI {

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

        //setting quests from here
        List<Quest> playerActiveQuests = Quest.getPlayerActiveQuests(player);
        for (Quest quest : playerActiveQuests) {

            GuiItem activeQuest = ItemBuilder.from(XMaterial.BOOK.parseMaterial()).glow().setName(Utils.colorify("&a&l" + quest.name)).setLore(getTheRightLore(quest, "&6Active")).asGuiItem();
            activeQuest.setAction(event -> {
                event.setCancelled(true);
                XSound.BLOCK_LEVER_CLICK.play(player);
                QuestGUI.showGUI(player, quest);
            });
            gui.addItem(activeQuest);
        }
        for (Quest quest : Quest.getNotActiveQuests(player)) {

            GuiItem notActiveQuest = ItemBuilder.from(XMaterial.WRITABLE_BOOK.parseMaterial()).setName(Utils.colorify("&e" + quest.name)).setLore(getTheRightLore(quest, "&cNot Started")).asGuiItem();
            notActiveQuest.setAction(event -> {
                event.setCancelled(true);
                XSound.BLOCK_LEVER_CLICK.play(player);
                QuestGUI.showGUI(player, quest);
            });
            gui.addItem(notActiveQuest);
        }

        for (Quest quest : Quest.getCompletedQuests(player)) {

            GuiItem completedQuests = ItemBuilder.from(XMaterial.BOOK.parseMaterial()).setName(Utils.colorify("&e" + quest.name)).setLore(getTheRightLore(quest, "&aCompleted")).asGuiItem();
            completedQuests.setAction(event -> {
                event.setCancelled(true);
                XSound.BLOCK_LEVER_CLICK.play(player);
                QuestGUI.showGUI(player, quest);
            });
            gui.addItem(completedQuests);
        }



        gui.open(player);
        XSound.BLOCK_NOTE_BLOCK_HARP.play(player);


    }


    public static List<String> getTheRightLore(Quest quest, String status) {
        List<String> finalLore = new ArrayList<>();
        List<String> description = Arrays.asList(quest.description.split("\n"));
        for (String aLore : description) {
            finalLore.add(Utils.colorify("&7" + aLore));
        }
        finalLore.add("");
        finalLore.add("");
        //kinda space

        String statusString = Utils.colorify("&7Status: " + status);
        finalLore.add(statusString);
        return finalLore;
    }


}
