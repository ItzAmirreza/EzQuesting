package me.deadlight.ezquesting.Listeners;

import me.deadlight.ezquesting.Enums.QuestObjectives;
import me.deadlight.ezquesting.Objects.Phase;
import me.deadlight.ezquesting.Objects.Quest;
import me.deadlight.ezquesting.Objects.QuestProcess;
import me.deadlight.ezquesting.Utils.Utilities.XSound;
import me.deadlight.ezquesting.Utils.Utils;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.ArrayList;
import java.util.List;

public class EntityDeathListener implements Listener {

    List<EntityType> validTypes = new ArrayList<>();

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {

        LivingEntity entity = event.getEntity();
        if (entity.getKiller() == null) {
            return;
        }
        if (Quest.getPlayerActiveQuests(entity.getKiller()).size() == 0) {
            return;
        }
        for (QuestProcess questProcess : Utils.questProcesses) {
            if (questProcess.player.equals(entity.getKiller().getUniqueId())) {
                if (questProcess.current_objective == null) {
                    return;
                }
                if (questProcess.current_objective.equals(QuestObjectives.KILL)) {

                    Phase currentPhase = Quest.getCurrentPhaseForPlayer(entity.getKiller(), questProcess.quest);
                    List<String> validTypesString = (List<String>) currentPhase.objectiveData.get("types");
                    if (validTypesString == null) {
                        return;
                    }
                    for (String vType : validTypesString) {
                        try {
                            validTypes.add(EntityType.valueOf(vType.toUpperCase()));
                        } catch (Exception e) {
                            System.out.println("invalid EntityType");
                            System.out.println(e);
                        }
                    }
                    if (validTypes.contains(entity.getType())) {

                        int previousCount = (int) questProcess.objective_data.get("count");
                        questProcess.objective_data.put("count", previousCount + 1);
                        XSound.ENTITY_CHICKEN_EGG.play(entity.getKiller());

                    }
                    validTypes.clear();

                }
            }
        }



    }

}
