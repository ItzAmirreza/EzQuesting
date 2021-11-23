package me.deadlight.ezquesting.Listeners;

import me.deadlight.ezquesting.Enums.QuestObjectives;
import me.deadlight.ezquesting.Objects.Phase;
import me.deadlight.ezquesting.Objects.Quest;
import me.deadlight.ezquesting.Objects.QuestProcess;
import me.deadlight.ezquesting.Utils.Utilities.XSound;
import me.deadlight.ezquesting.Utils.Utils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.ArrayList;
import java.util.List;

public class BlockPlaceListener implements Listener {

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
                    List<String> validTypesString = (List<String>) currentPhase.objectiveData.get("types");
                    if (validTypesString == null) {
                        return;
                    }
                    List<Material> validTypes = new ArrayList<>();
                    for (String vType : validTypesString) {
                        try {
                            validTypes.add(Material.valueOf(vType.toUpperCase()));
                        } catch (Exception e) {
                            System.out.println("invalid Material");
                            System.out.println(e);
                        }
                    }
                    if (validTypes.contains(event.getBlock().getType())) {

                        int previousCount = (int) questProcess.objective_data.get("count");
                        questProcess.objective_data.put("count", previousCount + 1);
                        XSound.ENTITY_CHICKEN_EGG.play(event.getPlayer());

                    }
                }
            }
        }



    }

}
