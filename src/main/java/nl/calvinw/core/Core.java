package nl.calvinw.core;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

// Import commands
import nl.calvinw.core.commands.chelp;
import nl.calvinw.core.commands.report;
import nl.calvinw.core.commands.vote;
import nl.calvinw.core.commands.website;
import nl.calvinw.core.commands.showitem;

//Import events
import nl.calvinw.core.events.joinleaveevent;
import nl.calvinw.core.events.commandoverrider;

public final class Core extends JavaPlugin {

    @Override
    public void onEnable() {
        // Save default config if it doesn't exist
        saveDefaultConfig();

        vote voteCommandExecutor = new vote(this);

        // Register event listeners
        getServer().getPluginManager().registerEvents(new joinleaveevent(this), this);

        // Register the command override listener
        getServer().getPluginManager().registerEvents(new commandoverrider(this, voteCommandExecutor), this);


        saveResource("messages.yml", false);

        this.getCommand("chelp").setExecutor(new chelp(this));
        this.getCommand("report").setExecutor(new report(this));
        this.getCommand("vote").setExecutor(voteCommandExecutor);
        this.getCommand("website").setExecutor(new website(this));
        this.getCommand("showitem").setExecutor(new showitem(this));

        // Plugin startup messages
        getLogger().info("/************   Core -v1-  ************/");
        getLogger().info("/*             By CalvinW              /");
        getLogger().info("/*              Started!               /");
        getLogger().info("/**************************************/");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown messages
        getLogger().info("/************   Core -v1-  ************/");
        getLogger().info("/*             By CalvinW              /");
        getLogger().info("/*              Stopped!               /");
        getLogger().info("/**************************************/");
    }

    public String getMessage(String path) {
        // Load messages.yml file
        File messagesFile = new File(getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            saveResource("messages.yml", false); // Save default if not found
        }

        // Load YamlConfiguration
        YamlConfiguration messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);

        // Get the message from messages.yml, or return the default if not found
        String message = messagesConfig.getString(path, "Message not found for path: " + path);

        // Retrieve prefix from config and convert color codes
        String prefix = getConfig().getString("prefix", "&8[&fCore&8] &7»&f");
        prefix = ChatColor.translateAlternateColorCodes('&', prefix); // Convert color codes in the prefix

        // Return the message with the prefix added
        return prefix + " " + ChatColor.translateAlternateColorCodes('&', message); // Add prefix and translate color codes
    }

}
