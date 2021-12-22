package me.deadlight.ezquesting.ui;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import me.deadlight.ezquesting.objects.Phase;
import me.deadlight.ezquesting.objects.Quest;
import me.deadlight.ezquesting.utils.Utils;
import me.deadlight.ezquesting.utils.utilities.XMaterial;
import me.deadlight.ezquesting.utils.utilities.XSound;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.entity.Player;

import java.util.List;

public class PhaseGUI {

    public static void showGUI(Player player, Quest quest) {
        Phase phase = Quest.getCurrentPhaseForPlayer(player, quest);

        Gui gui = new Gui(3, Utils.colorify("&e" + quest.name + "'s &cphase page"));

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

        GuiItem backToQuestPage = ItemBuilder.skull().texture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTAzNWM1MjgwMzZiMzg0YzUzYzljOGExYTEyNTY4NWUxNmJmYjM2OWMxOTdjYzlmMDNkZmEzYjgzNWIxYWE1NSJ9fX0=").name(Component.text("Back to quest page")).asGuiItem();
        backToQuestPage.setAction(event -> {
            event.setCancelled(true);
            XSound.ITEM_BOOK_PAGE_TURN.play(player);
            QuestGUI.showGUI(player, quest);
        });
        gui.setItem(1, backToQuestPage);

        GuiItem name_and_description = ItemBuilder.from(XMaterial.OAK_SIGN.parseMaterial()).setName(Utils.colorify("&d" + phase.name)).setLore(Utils.colorify("&e" + phase.description)).asGuiItem();
        name_and_description.setAction(event -> {
            event.setCancelled(true);
        });
        gui.setItem(12, name_and_description);

        GuiItem objective = ItemBuilder.from(XMaterial.PAPER.parseMaterial()).setName(Utils.colorify("&7Objective: &6" + phase.objective.name())).setLore(getObjectiveLore(phase, player)).asGuiItem();
        objective.setAction(event -> {
            event.setCancelled(true);
        });
        gui.setItem(14, objective);

        GuiItem currentPhase = ItemBuilder.from(XMaterial.COMPASS.parseMaterial()).glow().setAmount(phase.phaseID).setName(Utils.colorify("&7Current phase: &d" + phase.phaseID)).setLore(Utils.colorify("\n &7Out of &e"+ quest.questPhases.size() + " &7phases.")).asGuiItem();
        currentPhase.setAction(event -> {
            event.setCancelled(true);
        });
        gui.setItem(13, currentPhase);

        gui.open(player);

    }

    public static List<String> getObjectiveLore(Phase phase, Player player) {
        //maybe the progress later?
        return Utils.generateLoreForObjective(phase.objective, phase.objectiveData);
    }



}
