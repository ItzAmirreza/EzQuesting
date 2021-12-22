package me.deadlight.ezquesting.listeners;

import me.deadlight.ezquesting.enums.QuestObjectives;
import me.deadlight.ezquesting.objects.Phase;
import me.deadlight.ezquesting.objects.Quest;
import me.deadlight.ezquesting.objects.QuestProcess;
import me.deadlight.ezquesting.utils.utilities.XSound;
import me.deadlight.ezquesting.utils.Utils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.ArrayList;
import java.util.List;

public class BlockPlaceListener implements Listener {
    List<Material> validTypes = new ArrayList<>();


    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (Quest.getPlayerActiveQuests(event.getPlayer()).size() == 0) {
            return;
        }
        for (QuestProcess questProcess : Utils.questProcesses) {
            if (questProcess.player.equals(event.getPlayer().getUniqueId())) {
                if (questProcess.current_objective == null) {
                    return;
                }
                if (questProcess.current_objective.equals(QuestObjectives.PLACE)) {

                    Phase currentPhase = Quest.getCurrentPhaseForPlayer(event.getPlayer(), questProcess.quest);

                    if (validTypes.contains(event.getBlock().getType())) {

                        int previousCount = (int) questProcess.objective_data.get("count");
                        questProcess.objective_data.put("count", previousCount + 1);
                        XSound.ENTITY_CHICKEN_EGG.play(event.getPlayer());

                    }
                    validTypes.clear();
                }
            }
        }



    }

}
