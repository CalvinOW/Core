package nl.calvinw.core.events;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import nl.calvinw.core.Core;

public class joinleaveevent implements Listener {

    private final Core plugin;

    // Constructor to inject the main plugin instance
    public joinleaveevent(Core plugin) {
        this.plugin = plugin;
    }

    // Event handler for player join
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String playerName = event.getPlayer().getName();
        String joinMessage = plugin.getMessage("messages.join").replace("{player}", playerName);
        event.setJoinMessage(ChatColor.translateAlternateColorCodes('&', joinMessage));
    }

    // Event handler for player quit
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String playerName = event.getPlayer().getName();
        String leaveMessage = plugin.getMessage("messages.leave").replace("{player}", playerName);
        event.setQuitMessage(ChatColor.translateAlternateColorCodes('&', leaveMessage));
    }
}
