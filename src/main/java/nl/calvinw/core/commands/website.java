package nl.calvinw.core.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class website implements CommandExecutor {

    private final JavaPlugin plugin;

    public website(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(getMessage("general.only-users"));
            return true;
        }

        Player player = (Player) sender;

        // Load the website URL from config.yml
        String websiteUrl = plugin.getConfig().getString("website_url", null);

        if (websiteUrl == null || websiteUrl.isEmpty()) {
            // If no website URL is configured, show "no-site" message
            player.sendMessage(getMessage("website.no-site"));
            return true;
        }

        // Get the website message and replace placeholder with the URL
        String websiteMessage = getMessage("website.message").replace("{website_url}", websiteUrl);

        // Send the formatted message to the player
        player.sendMessage(websiteMessage);

        return true;
    }

    private String getMessage(String path) {
        // Load messages.yml file
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false); // Save default if not found
        }

        // Load YamlConfiguration
        YamlConfiguration messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);

        // Get the message from messages.yml, or return the default if not found
        String message = messagesConfig.getString(path, "Message not found for path: " + path);

        // Retrieve prefix from config and convert color codes
        String prefix = plugin.getConfig().getString("prefix", "&8[&fCore&8] &7»&f");
        prefix = ChatColor.translateAlternateColorCodes('&', prefix); // Convert color codes in the prefix

        // Return the message with the prefix added
        return prefix + " " + ChatColor.translateAlternateColorCodes('&', message); // Add prefix and translate color codes
    }
}
