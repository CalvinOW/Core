package nl.calvinw.core;

import nl.calvinw.core.commands.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import net.milkbowl.vault.economy.Economy;

import java.io.File;

import org.bukkit.plugin.RegisteredServiceProvider;

// Import events
import nl.calvinw.core.events.joinleaveevent;
import nl.calvinw.core.events.commandoverrider;
import nl.calvinw.core.events.sellwand;

public final class Core extends JavaPlugin {

    private Economy economy;  // Declare the economy field

    @Override
    public void onEnable() {
        // Save default config if it doesn't exist
        saveDefaultConfig();

        vote voteCommandExecutor = new vote(this);

        // Register event listeners
        getServer().getPluginManager().registerEvents(new joinleaveevent(this), this);

        // Register the command override listener
        getServer().getPluginManager().registerEvents(new commandoverrider(this, voteCommandExecutor), this);

        // Check if Vault is available and set up economy
        if (!setupEconomy()) {
            getLogger().severe("Vault is required for this plugin to function!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Register SellWand event
        getServer().getPluginManager().registerEvents(new sellwand(this, economy), this);

        // Save default messages.yml if it doesn't exist
        saveResource("messages.yml", false);

        // Register commands
        this.getCommand("chelp").setExecutor(new chelp(this));
        this.getCommand("report").setExecutor(new report(this));
        this.getCommand("vote").setExecutor(voteCommandExecutor);
        this.getCommand("website").setExecutor(new website(this));
        this.getCommand("showitem").setExecutor(new showitem(this));
        this.getCommand("sellwand").setExecutor(new givesellwand(this));

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

    public String getMessageWithout(String path) {
        // Load messages.yml file
        File messagesFile = new File(getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            saveResource("messages.yml", false); // Save default if not found
        }

        // Load YamlConfiguration
        YamlConfiguration messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);

        // Get the message from messages.yml, or return the default if not found
        String message = messagesConfig.getString(path, "Message not found for path: " + path);

        // Return the message with the prefix added
        return ChatColor.translateAlternateColorCodes('&', message); // Add prefix and translate color codes
    }

    private boolean setupEconomy() {
        // Check if Vault is installed
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        // Get the Economy service from Vault
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }

        // Get the economy provider
        economy = rsp.getProvider();
        return economy != null;
    }
}
