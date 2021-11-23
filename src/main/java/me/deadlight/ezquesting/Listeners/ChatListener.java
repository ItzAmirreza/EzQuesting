package me.deadlight.ezquesting.Listeners;

import me.deadlight.ezquesting.UI.EditorGUI;
import me.deadlight.ezquesting.Utils.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.UUID;

public class ChatListener implements Listener {

    public static HashMap<UUID, String> chatResponse = new HashMap<>();

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {

        if (chatResponse.containsKey(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);

            String theMessage = event.getMessage();
            if (theMessage.length() > 20 || theMessage.length() < 1) {
                event.getPlayer().sendMessage(Utils.colorify("&cPlease keep the length between 1 and 20 characters"));
                return;
            }
            chatResponse.remove(event.getPlayer().getUniqueId());
            EditorGUI.createQuest(event.getPlayer(), theMessage);

        }


    }

}
