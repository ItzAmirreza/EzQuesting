package me.deadlight.ezquesting.ui;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import me.deadlight.ezquesting.enums.QuestStatus;
import me.deadlight.ezquesting.objects.PlayerQuestData;
import me.deadlight.ezquesting.objects.Quest;
import me.deadlight.ezquesting.utils.Utils;
import me.deadlight.ezquesting.utils.utilities.XMaterial;
import me.deadlight.ezquesting.utils.utilities.XSound;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class QuestGUI {

    public static void showGUI(Player player, Quest quest) {

        Gui gui = new Gui(3, Utils.colorify("&e" + quest.name));

        GuiItem dummyItem = ItemBuilder.from(XMaterial.GRAY_STAINED_GLASS_PANE.parseMaterial()).setName(Utils.colorify("&d")).asGuiItem();
        dummyItem.setAction(event -> {
            event.setCancelled(true);
        });
        gui.getFiller().fill(dummyItem);
        //setting real deals
        GuiItem backToQuests = ItemBuilder.skull().texture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTM3NDFkODM2MGFmYjg3MDZkYmU4YzI4NjAxZjY4ODk4YjkyMTQ2MDMwOWQ4YjU1MGI1MDNmY2MxMjdhZWUxMiJ9fX0=").color(Color.AQUA).name(Component.text("Back to quests")).asGuiItem();
        backToQuests.setAction(event -> {
            event.setCancelled(true);
            XSound.ITEM_BOOK_PAGE_TURN.play(player);
            QuestsGUI.showGui(player);
        });
        gui.setItem(0, backToQuests);

        GuiItem questInfo = ItemBuilder.from(XMaterial.PAPER.parseMaterial()).setName(Utils.colorify("&e&lQuest information")).setLore(getQuestInformationLore(quest)).asGuiItem();
        questInfo.setAction(event -> {
            event.setCancelled(true);
        });
        gui.setItem(11, questInfo);

        GuiItem questStatus = generateQuestStatusItemStack(quest, player);
        gui.setItem(13, questStatus);

        GuiItem phaseItem = generatePhaseItemStack(quest, player);
        gui.setItem(15, phaseItem);


        gui.open(player);

    }

    public static List<String> getQuestInformationLore(Quest quest) {
        List<String> finalLore = new ArrayList<>();
        finalLore.add("");
        finalLore.add(Utils.colorify("&eQuest name: &7" + quest.name));
        finalLore.add("");
        finalLore.add(Utils.colorify("&7" + quest.description));
        return finalLore;
    }

    public static GuiItem generateQuestStatusItemStack(Quest quest, Player player) {
        PlayerQuestData data = Utils.getFreshPlayerDataForQuest(player, quest);
        if (data.status.equals(QuestStatus.ACTIVE)) {
            GuiItem item = ItemBuilder.from(XMaterial.YELLOW_CONCRETE.parseMaterial()).glow().setName(Utils.colorify("&eQuest status: &aActive")).setLore(Utils.colorify("&7Right click to stop the quest")).asGuiItem();
            item.setAction(event -> {
                event.setCancelled(true);
                if (event.getClick().isRightClick()) {
                    //stop the quest later :D
                    XSound.BLOCK_NOTE_BLOCK_GUITAR.play(player);
                    quest.stopQuest(player);
                }
            });
            return item;
        } else if (data.status.equals(QuestStatus.NOTSTARTED)) {
            GuiItem item = ItemBuilder.from(XMaterial.RED_CONCRETE.parseMaterial()).setName(Utils.colorify("&eQuest status: &cNot started")).setLore(Utils.colorify("&7Click to start the quest")).asGuiItem();
            item.setAction(event -> {
                event.setCancelled(true);
                XSound.BLOCK_NOTE_BLOCK_PLING.play(player);
                //start the quest for player
                quest.startQuest(player);
            });
            return item;
        } else if (data.status.equals(QuestStatus.COMPLETED)) {
            GuiItem item = ItemBuilder.from(XMaterial.LIME_CONCRETE.parseMaterial()).setName(Utils.colorify("&eQuest status: &6Completed")).asGuiItem();
            item.setAction(event -> {
                event.setCancelled(true);
                player.sendMessage(Utils.colorify("&aYou have already completed this quest."));
            });
            return item;
        } else {
            return null;
        }

    }

    public static GuiItem generatePhaseItemStack(Quest quest, Player player) {
        PlayerQuestData data = Utils.getFreshPlayerDataForQuest(player, quest);

            GuiItem item = ItemBuilder.from(XMaterial.COMPASS.parseMaterial()).setName(Utils.colorify("&d&lPhase information")).setLore(phaseLoreGenerator(data.status, quest, player)).asGuiItem();
            item.setAction(event -> {
                event.setCancelled(true);
                if (data.status.equals(QuestStatus.ACTIVE)) {
                    PhaseGUI.showGUI(player, quest);
                    XSound.ITEM_BOOK_PAGE_TURN.play(player);
                }
            });
            return item;
    }

    public static List<String> phaseLoreGenerator(QuestStatus status, Quest quest, Player player) {
        List<String> finalLore = new ArrayList<>();
        finalLore.add("");
        if (status.equals(QuestStatus.NOTSTARTED)) {
            finalLore.add(Utils.colorify("&7You haven't started the quest."));
            finalLore.add(Utils.colorify("&7Please start the quest, to"));
            finalLore.add(Utils.colorify("&7access further information"));
            return finalLore;
        } else if (status.equals(QuestStatus.ACTIVE)) {
            finalLore.add(Utils.colorify("&7There are totally &e" + quest.questPhases.size()));
            finalLore.add(Utils.colorify("&7phases in this quest."));
            finalLore.add(Utils.colorify(""));
            finalLore.add(Utils.colorify("&aClick for more information"));
            return finalLore;
        } else if (status.equals(QuestStatus.COMPLETED)) {
            finalLore.add(Utils.colorify("&aYou have completed this quest"));
        }
        return finalLore;
    }

}
