package nl.calvinw.core.commands;

import nl.calvinw.core.Core;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Set;

public class vote implements CommandExecutor {

    private final Core plugin;

    public vote(Core plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessage("general.only-users"));
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
            player.sendMessage(plugin.getMessage("vote.no-site"));
            return true;
        }

        // Retrieve the "message" key
        player.sendMessage(plugin.getMessage("vote.message"));

        // Retrieve all keys under "vote" except the specific ones like "message", "no-site", and "no-url"
        Set<String> voteSites = messagesConfig.getConfigurationSection("vote").getKeys(false);
        boolean siteFound = false;

        for (String key : voteSites) {
            if (key.startsWith("site")) {
                String url = plugin.getMessage("vote." + key);
                player.sendMessage(ChatColor.GRAY + url);
                siteFound = true;
            }
        }

        if (!siteFound) {
            player.sendMessage(plugin.getMessage("vote.no-url"));
        }

        return true;
    }
}
