package me.deadlight.ezquesting.Utils;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import me.deadlight.ezquesting.Database.DatabaseProvider;
import me.deadlight.ezquesting.Enums.QuestObjectives;
import me.deadlight.ezquesting.Enums.QuestStatus;
import me.deadlight.ezquesting.EzQuesting;
import me.deadlight.ezquesting.Objects.Phase;
import me.deadlight.ezquesting.Objects.PlayerQuestData;
import me.deadlight.ezquesting.Objects.Quest;
import me.deadlight.ezquesting.Objects.QuestProcess;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Utils {

    public static String colorify(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    public static List<Quest> activeQuests = new ArrayList<>();
    public static HashMap<UUID, List<PlayerQuestData>> playerQuestsData = new HashMap<>();
    public static List<QuestProcess> questProcesses = new ArrayList<>();

    public static FileConfiguration checkForQuestsYml() {
        File questsYml = new File(EzQuesting.pluginInstance.getDataFolder(), "quests.yml");
        if (!questsYml.exists()) {
            EzQuesting.logConsole("&eCreating a new quests data file...");
            try {
                questsYml.createNewFile();
                EzQuesting.logConsole("&aCreating a new quests data was successful.");
            } catch (IOException e) {
                EzQuesting.logConsole("&c4Failed creating a new quests data file! Please fix this and try again.");
                EzQuesting.disablePlugin();
                return null;
            }
            FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(questsYml);
            fileConfiguration.set("quests", new ArrayList<>());
            questsYml = new File(EzQuesting.pluginInstance.getDataFolder(), "quests.yml");
            try {
                fileConfiguration.save(questsYml);
            } catch (IOException e) {
                EzQuesting.logConsole("&4There was a problem with saving a data file! Please fix that and try again...");
                EzQuesting.disablePlugin();
                return null;
            }

        }
        return YamlConfiguration.loadConfiguration(questsYml);
    }

    public static int loadQuests() {
        FileConfiguration questsYML = EzQuesting.questsYml;
        if (!questsYML.isConfigurationSection("quests")) {
            return 0;
        }
        for (String questID : questsYML.getConfigurationSection("quests").getKeys(false)) {
            //for each quest section
            EzQuesting.logConsole("&eloading quest " + questID);
            String questRoute = "quests." + questID + ".";
            UUID questUUID = UUID.fromString(questID); //Final Quest UUID
            boolean isReady = questsYML.getBoolean(questRoute + "ready"); //final ready
            if (!isReady) {
                continue;
            }
            String name = questsYML.getString(questRoute + "name"); //final quest name
            UUID creator = UUID.fromString(questsYML.getString(questRoute + "creator")); //final creator uuid
            int npcID = questsYML.getInt(questRoute + "assigned-npc", -1); //final assigned npc
            String description = questsYML.getString(questRoute + "description"); //final quest description
            List<String> post_complete_commands = questsYML.getStringList(questRoute + "post-complete-commands"); //final post_comleted_commands
            List<String> pre_conversation = questsYML.getStringList(questRoute + "pre-conversation"); //final pre_conversation
            List<String> post_conversation = questsYML.getStringList(questRoute + "post-conversation"); //final post_conversation
            List<Phase> phases = getPhases(questRoute, questsYML);
            Quest quest = new Quest(name, description, questUUID, creator, post_complete_commands, pre_conversation, post_conversation, phases, isReady);
            Utils.activeQuests.add(quest);

        }
        return Utils.activeQuests.size();

    }

    public static List<Phase> getPhases(String questRoute, FileConfiguration questYML) {
        List<Phase> phases = new ArrayList<>();
        for (String phaseID : questYML.getConfigurationSection(questRoute + "phases").getKeys(false)) {
            String phaseRoute = questRoute + "phases." + phaseID + ".";
            String name = questYML.getString(phaseRoute + "name"); //final phase name
            String description = questYML.getString(phaseRoute + "description"); //final description
            List<String> pre_conversation = questYML.getStringList(phaseRoute + "pre-conversation"); //final pre_conv
            List<String> post_conversation = questYML.getStringList(phaseRoute + "post-conversation"); //final post_conv\
            List<String> post_completion_commands = questYML.getStringList(phaseRoute + "post-completion-commands"); //final post_completion_commands
            QuestObjectives objective = QuestObjectives.valueOf(questYML.getString(phaseRoute + "objective")); //final objective
            HashMap<String, Object> objectiveData = new HashMap<>(); //final objective data
            for (String dataKey : questYML.getConfigurationSection(phaseRoute + "objective-data").getKeys(false)) {
                Object data = questYML.get(phaseRoute + "objective-data." + dataKey);
                objectiveData.put(dataKey, data);
            }

            phases.add(new Phase(Integer.parseInt(phaseID), name, description, pre_conversation, post_conversation, objective, objectiveData, post_completion_commands));
        }

        return phases;
    }

    public static DBObject getPlayerData(UUID uuid) {
        DBObject query = new BasicDBObject("playeruuid", uuid.toString());
        return DatabaseProvider.userData.findOne(query);
    }

    public static PlayerQuestData getFreshPlayerDataForQuest(Player player, Quest quest) {
        DBObject data = getPlayerData(player.getUniqueId());
        DBObject playerQuests = (DBObject) data.get("quests");
        if (playerQuests.containsField(quest.questID.toString())) {
            DBObject questData = (DBObject) playerQuests.get(quest.questID.toString());
            int statusNum = (int) questData.get("status");
            int phaseLvl = (int) questData.get("currentphase");

            return new PlayerQuestData(quest, player.getUniqueId(), QuestStatus.statusNumber(statusNum));
        }
        //means haven't started yet
        return new PlayerQuestData(quest, player.getUniqueId(), QuestStatus.NOTSTARTED);
    }


    public static List<String> generateLoreForObjective(QuestObjectives objective, HashMap<String, Object> objective_data) {
        List<String> finalLore = new ArrayList<>();
        finalLore.add("");
        if (objective == QuestObjectives.WALK) {
            int count = (int) objective_data.get("count");
            finalLore.add(Utils.colorify("&7You have to walk &a" + count));
            finalLore.add(Utils.colorify("&7blocks in order to finish this phase."));
        } else if (objective == QuestObjectives.KILL) {
            int count = (int) objective_data.get("count");
            List<String> allowedTypes = (List<String>) objective_data.get("types");
            String allowedTypesString = allowedTypes.toString().replace("[", "").replace("]", "");
            finalLore.add(Utils.colorify("&7You have to kill &a" + count));
            finalLore.add(Utils.colorify("&7mobs in order to pass this phase."));
            finalLore.add("");
            finalLore.add(Utils.colorify("&6Allowed Mob(s): "));
            finalLore.add(Utils.colorify("&7" + allowedTypesString));
        } else if (objective == QuestObjectives.BREAK) {
            int count = (int) objective_data.get("count");
            List<String> allowedTypes = (List<String>) objective_data.get("types");
            String allowedTypesString = allowedTypes.toString().replace("[", "").replace("]", "");
            finalLore.add(Utils.colorify("&7You have to break &a" + count));
            finalLore.add(Utils.colorify("&7blocks in order to pass this phase"));
            finalLore.add("");
            finalLore.add(Utils.colorify("&7Allowed Block(s): "));
            finalLore.add(Utils.colorify("&7" + allowedTypesString));
        } else if (objective == QuestObjectives.PLACE) {
            int count = (int) objective_data.get("count");
            List<String> allowedTypes = (List<String>) objective_data.get("types");
            String allowedTypesString = allowedTypes.toString().replace("[", "").replace("]", "");
            finalLore.add(Utils.colorify("&7You have to place &a" + count));
            finalLore.add(Utils.colorify("&7blocks in order to pass this phase"));
            finalLore.add("");
            finalLore.add(Utils.colorify("&7Allowed Block(s): "));
            finalLore.add(Utils.colorify("&7" + allowedTypesString));
        } else if (objective == QuestObjectives.COMMAND) {
            String command = (String) objective_data.get("command");
            finalLore.add(Utils.colorify("&7You have to execute the following command"));
            finalLore.add(Utils.colorify("&7in order to pass this phase"));
            finalLore.add("");
            finalLore.add(Utils.colorify("&6" + command));
        } else {
            finalLore.add(Utils.colorify("&cInvalid phase"));
        }

        return finalLore;
    }

}
