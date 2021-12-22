package me.deadlight.ezquesting.managers;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class QuestManager {
    public List<Material> validBreakMaterialTypse = new ArrayList<>();
    public List<Material> validPlaceMaterialTypse = new ArrayList<>();
    public List<EntityType> validEntityTypes = new ArrayList<>();
    public QuestManager(List<Material> validBreakMaterialTypse, List<Material> validPlaceMaterialTypes, List<EntityType> validEntityTypes) {
        this.validEntityTypes = validEntityTypes;
        this.validBreakMaterialTypse = validBreakMaterialTypse;
        this.validPlaceMaterialTypse = validPlaceMaterialTypes;
    }
}
