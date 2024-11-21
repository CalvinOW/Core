package nl.calvinw.core.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Set;

public class vote implements CommandExecutor {

    private final JavaPlugin plugin;

    public vote(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(getMessage("general.only-users"));
            return true;
        }

        Player player = (Player) sender;

        // Retrieve prefix from config and convert color codes
        String prefix = plugin.getConfig().getString("prefix", "&8[&fCore&8] &7»&f");
        prefix = ChatColor.translateAlternateColorCodes('&', prefix);

        // Load messages.yml
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }

        YamlConfiguration messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);

        // Check if the "vote" section exists
        if (!messagesConfig.contains("vote")) {
            player.sendMessage(getMessage("vote.no-site"));
            return true;
        }

        // Retrieve the "message" key
        player.sendMessage(getMessage("vote.message"));

        // Retrieve all keys under "vote" except the specific ones like "message", "no-site", and "no-url"
        Set<String> voteSites = messagesConfig.getConfigurationSection("vote").getKeys(false);
        boolean siteFound = false;

        for (String key : voteSites) {
            if (key.startsWith("site")) {
                String url = getMessage("vote." + key);
                player.sendMessage(ChatColor.GRAY + url);
                siteFound = true;
            }
        }

        if (!siteFound) {
            player.sendMessage(getMessage("vote.no-url"));
        }

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
