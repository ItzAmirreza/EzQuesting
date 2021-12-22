package me.deadlight.ezquesting.listeners;

import me.deadlight.ezquesting.enums.QuestObjectives;
import me.deadlight.ezquesting.objects.Phase;
import me.deadlight.ezquesting.objects.Quest;
import me.deadlight.ezquesting.objects.QuestProcess;
import me.deadlight.ezquesting.utils.utilities.XSound;
import me.deadlight.ezquesting.utils.Utils;
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
