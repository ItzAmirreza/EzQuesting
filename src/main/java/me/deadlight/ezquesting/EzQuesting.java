package me.deadlight.ezquesting;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import me.deadlight.ezquesting.commands.EzQuestingCommand;
import me.deadlight.ezquesting.database.DatabaseProvider;
import me.deadlight.ezquesting.enums.QuestStatus;
import me.deadlight.ezquesting.listeners.*;
import me.deadlight.ezquesting.objects.PlayerQuestData;
import me.deadlight.ezquesting.objects.Quest;
import me.deadlight.ezquesting.objects.QuestProcess;
import me.deadlight.ezquesting.tasks.QuestProcessingTask;
import me.deadlight.ezquesting.utils.Utils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class EzQuesting extends JavaPlugin {

    public static EzQuesting pluginInstance;
    public static FileConfiguration questsYml;


    @Override
    public void onEnable() {
        pluginInstance = this;
        // Plugin startup logic
        saveDefaultConfig();
        String connectionString = getConfig().getString("database.connectionString");
        //Now giving the job to DatabaseProvider
        logConsole("&eInitializing connection to the database... Please wait ^_^");
        try {
            DatabaseProvider.connectToDatabase(connectionString);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            logConsole("&cError while connecting to database...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        logConsole("&eChecking data files...");
        questsYml = Utils.checkForQuestsYml();
        int questCount = Utils.loadQuests();
        registerEvents();
        registerCommands();
        addOnlinePlayersToDatabase();
        QuestProcessingTask.initializeLoop();
        logConsole("&aLoading completed, &d" + questCount + " &aquests was loaded.");

    }


    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new EntityDeathListener(), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(), this);
        getServer().getPluginManager().registerEvents(new CommandExecutionListener(), this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
    }

    private void registerCommands() {
        getServer().getPluginCommand("eq").setExecutor(new EzQuestingCommand());
    }

    private void addOnlinePlayersToDatabase() {
        for (Player onlinePlayer : getServer().getOnlinePlayers()) {
            if (!Utils.playerQuestsData.containsKey(onlinePlayer.getUniqueId())) {

                DBObject object = Utils.getPlayerData(onlinePlayer.getUniqueId());
                if (object == null) {
                    DBObject goingObject = new BasicDBObject();
                    goingObject.put("playeruuid", onlinePlayer.getUniqueId().toString());
                    DBObject emptyQuestsObject = new BasicDBObject();
                    goingObject.put("quests", emptyQuestsObject);
                    DatabaseProvider.userData.insert(goingObject);
                    object = goingObject;
                }
                DBObject questsObject = (DBObject) object.get("quests");
                List<PlayerQuestData> questList = new ArrayList<>();
                for (String questUUID : questsObject.keySet()) {
                    Quest foundQuest = Quest.getQuestWithID(UUID.fromString(questUUID));
                    DBObject questObject = (DBObject) questsObject.get(questUUID);
                    int statusNum = (int) questObject.get("status");
                    if (foundQuest != null) {
                        if (statusNum == 1) {
                            questList.add(new PlayerQuestData(foundQuest, onlinePlayer.getUniqueId(), QuestStatus.ACTIVE));
                            QuestProcess process = new QuestProcess(onlinePlayer.getUniqueId(), foundQuest);
                            Utils.questProcesses.add(process);
                        } else if (statusNum == 2) {
                            questList.add(new PlayerQuestData(foundQuest, onlinePlayer.getUniqueId(), QuestStatus.COMPLETED));
                        } else if (statusNum == 0) {
                            questList.add(new PlayerQuestData(foundQuest, onlinePlayer.getUniqueId(), QuestStatus.NOTSTARTED));
                        }
                    }
                }

                Utils.playerQuestsData.put(onlinePlayer.getUniqueId(), questList);

            }
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getServer().getScheduler().cancelTasks(this);
        for (Player onlinePlayer : getServer().getOnlinePlayers()) {
            onlinePlayer.closeInventory();
        }
        logConsole("&4Closing connection to database...");
        try {
            DatabaseProvider.mongoClient.close();
        } catch (Exception exception) {
            //nothing just don't spam the console please
        }
        logConsole("&eEzQuesting unloaded.");
    }

    public static void logConsole(String str){
        String prefix = "&d[&eEz&bQuesting&d]&r ";
        pluginInstance.getServer().getConsoleSender().sendMessage(Utils.colorify(prefix + str));
    }

    public static void disablePlugin() {
        pluginInstance.getServer().getPluginManager().disablePlugin(pluginInstance);
    }
}