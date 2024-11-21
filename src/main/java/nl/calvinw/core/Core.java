package nl.calvinw.core;

import org.bukkit.plugin.java.JavaPlugin;

// Import commands
import nl.calvinw.core.commands.chelp;
import nl.calvinw.core.commands.report;
import nl.calvinw.core.commands.vote;
import nl.calvinw.core.commands.website;

public final class Core extends JavaPlugin {

    @Override
    public void onEnable() {
        // Save default config if it doesn't exist
        saveDefaultConfig();
        saveResource("messages.yml", false);

        // Register the command executor for chelp
        if (getCommand("chelp") != null) {
            getCommand("chelp").setExecutor(new chelp(this));
        } else {
            getLogger().warning("Command 'chelp' is not registered in plugin.yml!");
        }

        // Register the command executor for report
        if (getCommand("report") != null) {
            getCommand("report").setExecutor(new report(this));
        } else {
            getLogger().warning("Command 'report' is not registered in plugin.yml!");
        }

        // Register the command executor for vote
        if (getCommand("vote") != null) {
            getCommand("vote").setExecutor(new vote(this));
        } else {
            getLogger().warning("Command 'vote' is not registered in plugin.yml!");
        }

        // Register the command executor for website
        if (getCommand("website") != null) {
            getCommand("website").setExecutor(new website(this));
        } else {
            getLogger().warning("Command 'website' is not registered in plugin.yml!");
        }

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
}
