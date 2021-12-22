package me.deadlight.ezquesting.listeners;
import me.deadlight.ezquesting.enums.QuestObjectives;
import me.deadlight.ezquesting.objects.Phase;
import me.deadlight.ezquesting.objects.Quest;
import me.deadlight.ezquesting.objects.QuestProcess;
import me.deadlight.ezquesting.utils.utilities.XSound;
import me.deadlight.ezquesting.utils.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandExecutionListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCommandExecute(PlayerCommandPreprocessEvent event) {
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
                if (questProcess.current_objective.equals(QuestObjectives.COMMAND)) {

                    Phase currentPhase = Quest.getCurrentPhaseForPlayer(event.getPlayer(), questProcess.quest);
                    String requiredCommand = (String) currentPhase.objectiveData.get("command");
                    if (event.getMessage().equalsIgnoreCase(requiredCommand)) {
                        questProcess.objective_data.put("command", true);
                        XSound.ENTITY_CHICKEN_EGG.play(event.getPlayer());

                    }
                }

                }

            }

    }

}
