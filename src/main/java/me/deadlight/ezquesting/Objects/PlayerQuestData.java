package me.deadlight.ezquesting.Objects;

import me.deadlight.ezquesting.Enums.QuestStatus;
import me.deadlight.ezquesting.Utils.Utils;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerQuestData {
    public Quest quest;
    public UUID player;
    public QuestStatus status;

    public PlayerQuestData(Quest quest, UUID player, QuestStatus status) {
        this.quest = quest;
        this.player = player;
        this.status = status;
    }


    public static void setPlayerQuestData(UUID uuid, Quest quest, QuestStatus status) {
        for (PlayerQuestData questData : Utils.playerQuestsData.get(uuid)) {
            if (questData.quest.questID.equals(quest.questID)) {
                questData.status = status;
                return;
            }
        }
        PlayerQuestData newQuestData = new PlayerQuestData(quest, uuid, status);
        Utils.playerQuestsData.get(uuid).add(newQuestData);

    }
}
