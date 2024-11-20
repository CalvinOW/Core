package nl.calvinw.core;

import org.bukkit.plugin.java.JavaPlugin;

// Import commands
import nl.calvinw.core.commands.chelp;
import nl.calvinw.core.commands.report;

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

        // Plugin startup messages
        getLogger().info("/************ Custom Tools ************/");
        getLogger().info("/*             By CalvinW              /");
        getLogger().info("/*              Started!               /");
        getLogger().info("/**************************************/");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown messages
        getLogger().info("/************ Custom Tools ************/");
        getLogger().info("/*             By CalvinW              /");
        getLogger().info("/*              Stopped!               /");
        getLogger().info("/**************************************/");
    }
}
