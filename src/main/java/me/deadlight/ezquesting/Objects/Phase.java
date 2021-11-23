package me.deadlight.ezquesting.Objects;

import me.deadlight.ezquesting.Enums.QuestObjectives;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Phase {
    public int phaseID;
    public String name;
    public String description;
    public List<String> pre_conversation;
    public List<String> post_conversation;
    public QuestObjectives objective;
    public HashMap<String, Object> objectiveData;
    public List<String> completed_commands;


    public Phase(int phaseID, String name, String description, List<String> pre_conversation, List<String> post_conversation, QuestObjectives objective, HashMap<String, Object> objectiveData, List<String> completed_commands) {
        this.phaseID = phaseID;
        this.name = name;
        this.description = description;
        this.pre_conversation = pre_conversation;
        this.post_conversation = post_conversation;
        this.objective = objective;
        this.objectiveData = objectiveData;
        this.completed_commands = completed_commands;
    }



    public boolean hasPassedTheRequirements(HashMap<String, Object> data) {
        if (objective.equals(QuestObjectives.COMMAND)) {
            boolean executed = (boolean) data.get("command");
            return executed;
        } else {
            int requiredAmount = (int) objectiveData.get("count");
            int dataAmount = (int) data.get("count");
            return dataAmount >= requiredAmount;
        }
    }

    public HashMap<String, Object> getObjectiveDataStructure() {
        HashMap<String, Object> data = new HashMap<>();
        if (objective.equals(QuestObjectives.WALK)) {
            data.put("count", 0);
        } else if (objective.equals(QuestObjectives.KILL)) {
            data.put("count", 0);
            data.put("types", new ArrayList<EntityType>());
        } else if (objective.equals(QuestObjectives.BREAK)) {
            data.put("count", 0);
            data.put("types", new ArrayList<Material>());
        } else if (objective.equals(QuestObjectives.PLACE)) {
            data.put("count", 0);
            data.put("types", new ArrayList<Material>());
        } else if (objective.equals(QuestObjectives.COMMAND)) {
            data.put("command", false);
        }

        return data;
    }




}
