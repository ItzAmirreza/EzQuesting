package me.deadlight.ezquesting.listeners;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import me.deadlight.ezquesting.database.DatabaseProvider;
import me.deadlight.ezquesting.enums.QuestStatus;
import me.deadlight.ezquesting.objects.PlayerQuestData;
import me.deadlight.ezquesting.objects.Quest;
import me.deadlight.ezquesting.objects.QuestProcess;
import me.deadlight.ezquesting.utils.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerJoinListener implements Listener {
    List<PlayerQuestData> questList = new ArrayList<>();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!Utils.playerQuestsData.containsKey(event.getPlayer().getUniqueId())) {

            DBObject object = Utils.getPlayerData(event.getPlayer().getUniqueId());
            if (object == null) {
                DBObject goingObject = new BasicDBObject();
                goingObject.put("playeruuid", event.getPlayer().getUniqueId().toString());
                DBObject emptyQuestsObject = new BasicDBObject();
                goingObject.put("quests", emptyQuestsObject);
                DatabaseProvider.userData.insert(goingObject);
                object = goingObject;
            }
            DBObject questsObject = (DBObject) object.get("quests");
            for (String questUUID : questsObject.keySet()) {
                Quest foundQuest = Quest.getQuestWithID(UUID.fromString(questUUID));
                DBObject questObject = (DBObject) questsObject.get(questUUID);
                int statusNum = (int) questObject.get("status");
                if (foundQuest != null) {
                    if (statusNum == 1) {
                        questList.add(new PlayerQuestData(foundQuest, event.getPlayer().getUniqueId(), QuestStatus.ACTIVE));
                        QuestProcess process = new QuestProcess(event.getPlayer().getUniqueId(), foundQuest);
                        Utils.questProcesses.add(process);
                    } else if (statusNum == 2) {
                        questList.add(new PlayerQuestData(foundQuest, event.getPlayer().getUniqueId(), QuestStatus.COMPLETED));
                    } else if (statusNum == 0) {
                        questList.add(new PlayerQuestData(foundQuest, event.getPlayer().getUniqueId(), QuestStatus.NOTSTARTED));
                    }
                }
                questList.clear();
            }

            Utils.playerQuestsData.put(event.getPlayer().getUniqueId(), questList);

        }
    }

}
