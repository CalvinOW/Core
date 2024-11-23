package nl.calvinw.core.commands;

import nl.calvinw.core.Core;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class chelp implements CommandExecutor {

    private final Core plugin;

    public chelp(Core plugin) {
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
        prefix = ChatColor.translateAlternateColorCodes('&', prefix); // Convert color codes in the prefix

        // Get all commands dynamically from plugin.yml
        Map<String, Map<String, Object>> commands = plugin.getDescription().getCommands();

        // If no command names are found
        if (commands.isEmpty()) {
            player.sendMessage(prefix + " " + ChatColor.RED + "No commands available.");
            return true;
        }

        // Loop through all commands in the plugin.yml and show their usage and description
        for (String cmdName : commands.keySet()) {
            Map<String, Object> commandDetails = commands.get(cmdName);

            String description = (String) commandDetails.get("description");
            String usage = (String) commandDetails.get("usage");

            // Send command info to the player with prefix
            player.sendMessage(prefix + " " + ChatColor.DARK_PURPLE + usage + ChatColor.GRAY + ": " + description);
        }

        return true;
    }
}
