package me.deadlight.ezquesting.Objects;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import me.deadlight.ezquesting.Database.DatabaseProvider;
import me.deadlight.ezquesting.Enums.QuestStatus;
import me.deadlight.ezquesting.EzQuesting;
import me.deadlight.ezquesting.Utils.Utilities.XSound;
import me.deadlight.ezquesting.Utils.Utils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class Quest {
    public String name;
    public String description;
    public UUID questID;
    public UUID creator;
    public List<String> complete_commands;
    public List<String> pre_conversation;
    public List<String> post_conversation;
    public List<Phase> questPhases;
    public boolean isReady = false;

    public Quest(String name, String description, UUID questID, UUID creator, List<String> complete_commands, List<String> pre_conversation, List<String> post_conversation, List<Phase> questPhases, boolean isReady) {
        this.name = name;
        this.description = description;
        this.questID = questID;
        this.creator = creator;
        this.complete_commands = complete_commands;
        this.pre_conversation = pre_conversation;
        this.post_conversation = post_conversation;
        this.questPhases = questPhases;
        this.isReady = isReady;
    }

    public void startQuest(Player player) {
        DBObject pData = Utils.getPlayerData(player.getUniqueId());
        DBObject quests = (DBObject) pData.get("quests");
        if (quests.containsField(this.questID.toString())) {
            DBObject questData = (DBObject) quests.get(this.questID.toString());
            questData.put("status", 1);
            questData.put("currentphase", 1);
        } else {
            DBObject questData = new BasicDBObject();
            questData.put("status", 1);
            questData.put("currentphase", 1);
            quests.put(this.questID.toString(), questData);
        }
        pData.put("quests", quests);
        DBObject query = new BasicDBObject("playeruuid", player.getUniqueId().toString());
        DatabaseProvider.userData.update(query, pData);
        PlayerQuestData.setPlayerQuestData(player.getUniqueId(), this, QuestStatus.ACTIVE);
        //PlayerQuestData.getCachedQuestDataForPlayer(player.getUniqueId(), this).status = QuestStatus.ACTIVE;
        player.closeInventory();
        player.sendTitle(Utils.colorify("&d"), Utils.colorify("&d" + this.name));
        XSound.BLOCK_NOTE_BLOCK_XYLOPHONE.play(player);
        player.sendMessage(Utils.colorify("&aYou have started the quest: &e" + this.name));
        player.sendMessage(Utils.colorify(Utils.colorify("&7" + this.description)));
        QuestProcess questProcess = new QuestProcess(player.getUniqueId(), this, 0);
        Utils.questProcesses.add(questProcess);

    }
    public void stopQuest(Player player) {
        DBObject pData = Utils.getPlayerData(player.getUniqueId());
        DBObject quests = (DBObject) pData.get("quests");
        if (quests.containsField(this.questID.toString())) {
            DBObject questData = (DBObject) quests.get(this.questID.toString());
            questData.put("status", 0);
            questData.put("currentphase", 0);
        }
        //PlayerQuestData.setPlayerQuestData(player.getUniqueId(), this, QuestStatus.NOTSTARTED);
        DBObject query = new BasicDBObject("playeruuid", player.getUniqueId().toString());
        DatabaseProvider.userData.update(query, pData);
        QuestProcess foundQP = null;
        PlayerQuestData.setPlayerQuestData(player.getUniqueId(), this, QuestStatus.NOTSTARTED);
        //PlayerQuestData.getCachedQuestDataForPlayer(player.getUniqueId(), this).status = QuestStatus.NOTSTARTED;
        for (QuestProcess questProcess : Utils.questProcesses) {
            if (questProcess.player.equals(player.getUniqueId())) {
                if (questProcess.quest.questID.equals(questID)) {
                    foundQP = questProcess;
                    break;
                }
            }
        }
        if (foundQP != null) {
            Utils.questProcesses.remove(foundQP);
        }
        player.closeInventory();
        player.sendMessage(Utils.colorify("&cYou have successfully stopped working on the quest: &e" + name));


    }

    public static List<Quest> getPlayerActiveQuests(Player player) {
        List<Quest> activeQuests = new ArrayList<>();
        if (Utils.playerQuestsData.get(player.getUniqueId()) == null) {
            return new ArrayList<>();
        }
        for (PlayerQuestData data : Utils.playerQuestsData.get(player.getUniqueId())) {
            if (data.status.equals(QuestStatus.ACTIVE)) {
                activeQuests.add(data.quest);
            }
        }
        return activeQuests;

    }

    public static List<Quest> getNotActiveQuests(Player player) {
        List<Quest> activeQuests = new ArrayList<>();
        List<Quest> databaseCompletedQuests = new ArrayList<>();
        if (Utils.playerQuestsData.get(player.getUniqueId()) == null) {
            return new ArrayList<>();
        }
        for (PlayerQuestData data : Utils.playerQuestsData.get(player.getUniqueId())) {
            if (data.status.equals(QuestStatus.ACTIVE)) {
                activeQuests.add(data.quest);
            } else if (data.status.equals(QuestStatus.COMPLETED)) {
                databaseCompletedQuests.add(data.quest);
            }
        }
        List<Quest> allQuests = new ArrayList<>(Utils.activeQuests);
        allQuests.removeAll(databaseCompletedQuests);
        allQuests.removeAll(activeQuests);
        return allQuests;
    }

    public static List<Quest> getCompletedQuests(Player player) {
        List<Quest> completedQuests = new ArrayList<>();
        if (Utils.playerQuestsData.get(player.getUniqueId()) == null) {
            return new ArrayList<>();
        }
        for (PlayerQuestData data : Utils.playerQuestsData.get(player.getUniqueId())) {
            if (data.status.equals(QuestStatus.COMPLETED)) {
                completedQuests.add(data.quest);
            }
        }
        return completedQuests;
    }

    public static Phase getCurrentPhaseForPlayer(Player player, Quest quest) {
        DBObject data = Utils.getPlayerData(player.getUniqueId());
        DBObject playerQuests = (DBObject) data.get("quests");
        DBObject thatQuest = (DBObject) playerQuests.get(quest.questID.toString());
        int phaseID = (int) thatQuest.get("currentphase");
        return getPhaseWithID(quest, phaseID);

    }

    public static Phase getPhaseWithID(Quest quest, int phaseID) {
        for (Phase phase : quest.questPhases) {
            if (phase.phaseID == phaseID) {
                return phase;
            }
        }
        return getPhaseWithID(quest, 1);
    }

    public static Quest getQuestWithID(UUID uuid) {
        for (Quest quest : Utils.activeQuests) {
            if (quest.questID.equals(uuid)) {
                return quest;
            }
        }
        return null;
    }

    public void deleteQuest(Player player) {

        File questsYml = new File(EzQuesting.pluginInstance.getDataFolder(), "quests.yml");
        if (!questsYml.exists()) {
            player.sendMessage(Utils.colorify("&cQuests.yml doesn't exist, please restart the server and try again."));
            return;
        }
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(questsYml);
        configuration.set("quests." + this.questID.toString(), null);
        try {
            configuration.save(questsYml);
            player.closeInventory();
            Utils.activeQuests.remove(this);
            Utils.questProcesses.removeIf(questProcess -> questProcess.quest.questID.equals(this.questID));
            for (List<PlayerQuestData> value : Utils.playerQuestsData.values()) {
                value.removeIf(questData -> questData.quest.questID.equals(this.questID));
            }
            player.sendMessage(Utils.colorify("&c" + this.name + "&7has been deleted."));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }







}