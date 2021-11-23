package me.deadlight.ezquesting.Objects;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import me.deadlight.ezquesting.Database.DatabaseProvider;
import me.deadlight.ezquesting.Enums.QuestObjectives;
import me.deadlight.ezquesting.Enums.QuestStatus;
import me.deadlight.ezquesting.EzQuesting;
import me.deadlight.ezquesting.Utils.Utilities.XSound;
import me.deadlight.ezquesting.Utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class QuestProcess {

    public UUID player;
    public Quest quest;
    public int currentPhaseID;
    public int phaseBeingExecuted;
    public boolean doNextJob = true;
    public HashMap<List<String>, Integer> current_conversation = new HashMap<>();
    public QuestObjectives current_objective = null;
    public HashMap<String, Object> objective_data = new HashMap<>();
    public int previousCount = 0;
    private boolean ending = false;
    private boolean phaseEnding = false;
    private BukkitTask taskToCancel;
    private boolean doPhaseConversation;

    public QuestProcess(UUID player, Quest quest) {
        this.player = player;
        this.quest = quest;
        this.currentPhaseID = Quest.getCurrentPhaseForPlayer(Bukkit.getPlayer(player), quest).phaseID;
    }

    public QuestProcess(UUID player, Quest quest, int phaseID) {
        this.player = player;
        this.quest = quest;
        this.currentPhaseID = phaseID;
    }

    public void nextPhase() {
        if (!doNextJob) {
            return;
        }
        if (currentPhaseID == 0) {
            //it means it just started
            this.doNextJob = false;
            int nextPhaseID = getNextPhaseID();
            this.currentPhaseID = nextPhaseID;
            setPlayerQuestPhase(nextPhaseID);
            this.current_conversation.put(quest.pre_conversation, 0);
            return;
        }
        //have to look into this part later
        if (currentPhaseID == -1) {
            //quest is completed
            if (ending) {
                return;
            }
            endQuest();
            return;
        }

        //the phase
        Phase currentPhase = Quest.getPhaseWithID(quest, this.currentPhaseID);
        Player playerObject = Bukkit.getPlayer(player);
        if (playerObject == null || !playerObject.isOnline()) {
            return;
        }
        if (currentPhaseID != phaseBeingExecuted) {
            this.phaseBeingExecuted = this.currentPhaseID;
            current_objective = currentPhase.objective;
            if (current_objective.equals(QuestObjectives.WALK)) {
                previousCount =  playerObject.getStatistic(Statistic.SPRINT_ONE_CM) / 100 + playerObject.getStatistic(Statistic.WALK_ONE_CM) / 100;
            }
            objective_data = currentPhase.getObjectiveDataStructure();
            this.doNextJob = false;
            playerObject.sendMessage(Utils.colorify("&6Phase description: &7" + currentPhase.description));
            current_conversation.put(currentPhase.pre_conversation, 0);
            return;
        }
        if (currentPhase.hasPassedTheRequirements(objective_data) && !phaseEnding) {
            doNextJob = false;
            current_conversation.put(currentPhase.post_conversation, 0); //check if it is nothing 0 size
            this.phaseEnding = true;
            taskToCancel = Bukkit.getScheduler().runTaskTimer(EzQuesting.pluginInstance, new Runnable() {
                @Override
                public void run() {
                    if (doNextJob) {

                        Player targetPlayer = Bukkit.getPlayer(player);
                        if (targetPlayer == null || !targetPlayer.isOnline()) {
                            return;
                        }
                        targetPlayer.sendMessage(Utils.colorify("&aPhase &d" + currentPhase.phaseID + "&a completed!"));
                        XSound.BLOCK_NOTE_BLOCK_HAT.play(targetPlayer);
                        for (String completed_command : currentPhase.completed_commands) {
                            ConsoleCommandSender sender = Bukkit.getServer().getConsoleSender();
                            Bukkit.dispatchCommand(sender, completed_command.replace("/", "").replace("%player%", targetPlayer.getName()));
                        }
                        setPlayerQuestPhase(getNextPhaseID());
                        doNextJob = true;
                        phaseEnding = false;
                        currentPhaseID = getNextPhaseID();
                        cancelCurrentTask();


                    }
                }
            }, 0, 20);



        }

    }

    private void cancelCurrentTask() {
        this.taskToCancel.cancel();
    }

    public void endQuest() {
        this.doNextJob = false;
        this.ending = true;
        current_conversation.put(quest.post_conversation,0);
        taskToCancel = Bukkit.getScheduler().runTaskTimer(EzQuesting.pluginInstance, new Runnable() {
            @Override
            public void run() {
                if (doNextJob) {
                    doNextJob = false;
                    //executing post commands
                    Player targetPlayer = Bukkit.getPlayer(player);
                    if (targetPlayer == null || !targetPlayer.isOnline()) {
                        return;
                    }
                    for (String complete_command : quest.complete_commands) {
                        ConsoleCommandSender sender = Bukkit.getServer().getConsoleSender();

                        Bukkit.dispatchCommand(sender, complete_command.replace("/", "").replace("%player%", targetPlayer.getName()));
                    }
                    targetPlayer.sendTitle(Utils.colorify("&aQuest Completed"), Utils.colorify("&c" + quest.name));
                    XSound.ENTITY_PLAYER_LEVELUP.play(targetPlayer);
                    //set in the database
                    endQuestDatabase();
                }
            }
        }, 0, 20);

    }

    public void endQuestDatabase() {
        taskToCancel.cancel();
        Utils.questProcesses.remove(this); //from cache
        DBObject query = new BasicDBObject("playeruuid", player.toString());
        DBObject playerData = Utils.getPlayerData(player);
        DBObject playerQuestsData = (DBObject) playerData.get("quests");
        DBObject questData = (DBObject) playerQuestsData.get(quest.questID.toString());
        questData.put("status", 2);
        DatabaseProvider.userData.update(query, playerData);
        PlayerQuestData.setPlayerQuestData(player, quest, QuestStatus.COMPLETED);
        //PlayerQuestData.getCachedQuestDataForPlayer(player, quest).status = QuestStatus.COMPLETED;
    }

    public int getNextPhaseID() {
        int nextPhase = this.currentPhaseID + 1;
        if (nextPhase > this.quest.questPhases.size()) {
            return -1;//means there is no phase left
        } else {
            return nextPhase;
        }
    }

    private void setPlayerQuestPhase(int phaseID) {
        DBObject query = new BasicDBObject("playeruuid", player.toString());
        DBObject playerData = Utils.getPlayerData(player);
        DBObject playerQuestsData = (DBObject) playerData.get("quests");
        DBObject questData = (DBObject) playerQuestsData.get(quest.questID.toString());
        questData.put("currentphase", phaseID);
        DatabaseProvider.userData.update(query, playerData);
    }
    








}
