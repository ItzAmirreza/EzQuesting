package me.deadlight.ezquesting.UI;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import me.deadlight.ezquesting.Objects.Quest;
import me.deadlight.ezquesting.Utils.Utilities.XMaterial;
import me.deadlight.ezquesting.Utils.Utilities.XSound;
import me.deadlight.ezquesting.Utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.entity.Player;

public class QuestManageUI {

    public static void showGUI(Player player, Quest quest) {

        Gui gui = new Gui(3, Utils.colorify("&c" + quest.name + "'s Manage"));
        GuiItem dummyItem = ItemBuilder.from(XMaterial.GRAY_STAINED_GLASS_PANE.parseMaterial()).setName(Utils.colorify("&d")).asGuiItem();
        dummyItem.setAction(event -> {
            event.setCancelled(true);
        });
        gui.getFiller().fill(dummyItem);
        GuiItem backToQuests = ItemBuilder.skull().texture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTM3NDFkODM2MGFmYjg3MDZkYmU4YzI4NjAxZjY4ODk4YjkyMTQ2MDMwOWQ4YjU1MGI1MDNmY2MxMjdhZWUxMiJ9fX0=").color(Color.AQUA).name(Component.text("Back to editor")).asGuiItem();
        backToQuests.setAction(event -> {
            event.setCancelled(true);
            XSound.ITEM_BOOK_PAGE_TURN.play(player);
            EditorGUI.showGui(player);
        });
        gui.setItem(0, backToQuests);

        //12 - 14
        GuiItem deleteQuest = ItemBuilder.from(XMaterial.BARRIER.parseMaterial()).glow().setName(Utils.colorify("&cDelete quest")).asGuiItem();
        deleteQuest.setAction(event -> {
            event.setCancelled(true);
            quest.deleteQuest(player);
        });
        gui.setItem(12, deleteQuest);

        GuiItem editQuest = ItemBuilder.from(XMaterial.PLAYER_HEAD.parseMaterial()).glow().setName(Utils.colorify("&eEdit quest")).asGuiItem();
        editQuest.setAction(event -> {
            event.setCancelled(true);
            player.closeInventory();
            XSound.BLOCK_NOTE_BLOCK_SNARE.play(player);
            player.sendMessage(Utils.colorify("&eFor now, you have to edit the quests from &aQuests.yml &efile in the config folder of the EzQuesting."));
            player.sendMessage(Utils.colorify("&bQuest UUID: &7" + quest.questID));
        });
        gui.setItem(14, editQuest);


        gui.open(player);

    }
}
