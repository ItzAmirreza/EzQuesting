package me.deadlight.ezquesting.tasks;
import me.deadlight.ezquesting.enums.QuestObjectives;
import me.deadlight.ezquesting.EzQuesting;
import me.deadlight.ezquesting.objects.QuestProcess;
import me.deadlight.ezquesting.utils.utilities.XSound;
import me.deadlight.ezquesting.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import java.util.List;

public class QuestProcessingTask {

    public static void initializeLoop() {
        EzQuesting.pluginInstance.getServer().getScheduler().runTaskTimer(EzQuesting.pluginInstance, new Runnable() {
            int passedTicks = 0;
            @Override
            public void run() {
                passedTicks++;

                if (passedTicks%50==0) {
                    //conversation tick
                    for (QuestProcess questProcess : Utils.questProcesses) {
                        if (!questProcess.current_conversation.isEmpty()) {
                            for (List<String> strings : questProcess.current_conversation.keySet()) {
                                if (strings.size() == 0) {
                                    questProcess.current_conversation.clear();
                                    questProcess.doNextJob = true;
                                    continue;
                                }
                                int conversationIndex = questProcess.current_conversation.get(strings);
                                String conversationLine = strings.get(conversationIndex);
                                Player player = Bukkit.getPlayer(questProcess.player);
                                if (player == null || !player.isOnline()) {
                                    continue;
                                }
                                XSound.ENTITY_VILLAGER_AMBIENT.play(player);
                                player.sendMessage(Utils.colorify("&7" + conversationLine).replace("%player%", player.getName()));
                                if (conversationIndex + 1 == strings.size()) {
                                    questProcess.current_conversation.clear();
                                    questProcess.doNextJob = true;
                                } else {
                                    questProcess.current_conversation.replace(strings, conversationIndex + 1);
                                }
                            }
                        }
                    }
                }

                if (passedTicks%20==0) {
                    //special tick
                    for (QuestProcess questProcess : Utils.questProcesses) {
                        questProcess.nextPhase();
                        //for walking
                        if (!questProcess.doNextJob) {
                            continue;
                        }
                        if (questProcess.current_objective.equals(QuestObjectives.WALK)) {
                            //for walk
                            Player player = Bukkit.getPlayer(questProcess.player);
                            if (player == null || !player.isOnline()) {
                                return;
                            }
                            int walkedBlocksInThisPeriod = (player.getStatistic(Statistic.WALK_ONE_CM) /100 + player.getStatistic(Statistic.SPRINT_ONE_CM)/100) - questProcess.previousCount;
                            if (walkedBlocksInThisPeriod != 0) {
                                questProcess.previousCount = player.getStatistic(Statistic.WALK_ONE_CM) /100 + player.getStatistic(Statistic.SPRINT_ONE_CM) /100;
                                int previousObjectiveDataCount = (int) questProcess.objective_data.get("count");
                                questProcess.objective_data.put("count", previousObjectiveDataCount + walkedBlocksInThisPeriod);
                            }

                        }


                    }


                }
            }
        }, 0, 1);
    }
}
