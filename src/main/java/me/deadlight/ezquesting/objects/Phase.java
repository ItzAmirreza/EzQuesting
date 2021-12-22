package me.deadlight.ezquesting.objects;

import me.deadlight.ezquesting.enums.QuestObjectives;
import me.deadlight.ezquesting.managers.QuestManager;
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
    public QuestManager manager;


    public Phase(int phaseID, String name, String description, List<String> pre_conversation, List<String> post_conversation, QuestObjectives objective, HashMap<String, Object> objectiveData, List<String> completed_commands) {
        this.phaseID = phaseID;
        this.name = name;
        this.description = description;
        this.pre_conversation = pre_conversation;
        this.post_conversation = post_conversation;
        this.objective = objective;
        this.objectiveData = objectiveData;
        this.completed_commands = completed_commands;
        manager = new QuestManager(returnBreakMaterialTypes(), returnPlaceMaterialTypes(), returnEntityTypes());
    }

    private List<Material> returnBreakMaterialTypes() {
        List<Material> finalList = new ArrayList<>();
        if (objective.equals(QuestObjectives.BREAK)) {
            List<String> validTypesString = (List<String>) objectiveData.get("types");
            if (validTypesString == null) {
                return null;
            }
            for (String vType : validTypesString) {
                try {
                    finalList.add(Material.valueOf(vType.toUpperCase()));
                } catch (Exception e) {
                    System.out.println("invalid Material");
                    System.out.println(e);
                }
            }
            return finalList;
        } else {
            return null;
        }
    }

    private List<Material> returnPlaceMaterialTypes() {
        List<Material> finalList = new ArrayList<>();
        if (objective.equals(QuestObjectives.PLACE)) {
            List<String> validTypesString = (List<String>) objectiveData.get("types");
            if (validTypesString == null) {
                return null;
            }
            for (String vType : validTypesString) {
                try {
                    finalList.add(Material.valueOf(vType.toUpperCase()));
                } catch (Exception e) {
                    System.out.println("invalid Material");
                    System.out.println(e);
                }
            }
            return finalList;
        } else {
            return null;
        }
    }

    private List<EntityType> returnEntityTypes() {
        List<EntityType> finalList = new ArrayList<>();
        if (objective.equals(QuestObjectives.KILL)) {
            List<String> validTypesString = (List<String>)objectiveData.get("types");
            if (validTypesString == null) {
                return null;
            }
            for (String vType : validTypesString) {
                try {
                    finalList.add(EntityType.valueOf(vType.toUpperCase()));
                } catch (Exception e) {
                    System.out.println("invalid EntityType");
                    System.out.println(e);
                }
            }
            return finalList;
        } else {
            return null;
        }
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
