package nl.calvinw.core.commands;

import nl.calvinw.core.Core;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class website implements CommandExecutor {

    private final Core plugin;

    public website(Core plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessage("general.only-users"));
            return true;
        }

        Player player = (Player) sender;

        // Load the website URL from config.yml
        String websiteUrl = plugin.getConfig().getString("website_url", null);

        if (websiteUrl == null || websiteUrl.isEmpty()) {
            // If no website URL is configured, show "no-site" message
            player.sendMessage(plugin.getMessage("website.no-site"));
            return true;
        }

        // Get the website message and replace placeholder with the URL
        String websiteMessage = plugin.getMessage("website.message").replace("{website_url}", websiteUrl);

        // Send the formatted message to the player
        player.sendMessage(websiteMessage);

        return true;
    }
}
