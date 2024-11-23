package nl.calvinw.core.events;

import nl.calvinw.core.Core;
import nl.calvinw.core.commands.vote;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class commandoverrider implements Listener {

    private final Core plugin;
    private final vote voteCommand;

    public commandoverrider(Core plugin, vote voteCommand) {
        this.plugin = plugin;
        this.voteCommand = voteCommand;
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        // Check if the command starts with /vote
        String message = event.getMessage();
        if (message.equalsIgnoreCase("/vote") || message.toLowerCase().startsWith("/vote ")) {
            // Cancel other plugins from handling /vote
            event.setCancelled(true);

            // Get the player who issued the command
            Player player = event.getPlayer();

            // Log the override for debugging purposes
            Bukkit.getLogger().info(ChatColor.YELLOW + "[Core] Overriding /vote command issued by " + player.getName());

            // Execute the custom /vote command logic
            voteCommand.onCommand(player, null, "vote", new String[0]);
        }
    }
}
