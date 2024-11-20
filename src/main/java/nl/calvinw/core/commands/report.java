package nl.calvinw.core.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

public class report implements CommandExecutor {

    private final JavaPlugin plugin;

    public report(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // Check if the sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage(getMessage("general.only-users"));  // Correct path
            return true;
        }

        // Check if the correct number of arguments is provided
        if (args.length < 2) {
            sender.sendMessage(getMessage("report.usage"));
            return false;
        }

        Player player = (Player) sender;
        String reportedPlayerName = args[0];
        String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        // Fetch the Discord webhook URL from the config file
        String discordWebhookUrl = plugin.getConfig().getString("discord_webhook_url");

        if (discordWebhookUrl == null || discordWebhookUrl.isEmpty()) {
            sender.sendMessage(getMessage("report.missing-webhook"));
            return true;
        }

        // Send the report to Discord
        sendReportToDiscord(player.getName(), reportedPlayerName, reason, discordWebhookUrl);

        // Inform the player that the report was sent
        player.sendMessage(getMessage("report.report-sent"));

        return true;
    }

    private void sendReportToDiscord(String reporter, String reportedPlayer, String reason, String webhookUrl) {
        try {
            // Generate the thumbnail URL using the reported player's name
            String thumbnailUrl = "https://minotar.net/helm/" + reportedPlayer + "/600.png";

            // Create the JSON payload to send to Discord with the embed and thumbnail
            String json = "{"
                    + "\"embeds\": [{"
                    + "\"title\": \"**Report Submitted**\","
                    + "\"description\": \"**Reporter:** " + reporter + "\\n"
                    + "**Reported Player:** " + reportedPlayer + "\\n"
                    + "**Reason:** " + reason + "\","
                    + "\"thumbnail\": {"
                    + "\"url\": \"" + thumbnailUrl + "\""
                    + "}"
                    + "}]"
                    + "}";

            // Set up the connection to Discord's webhook URL
            URL url = new URL(webhookUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            // Send the JSON payload
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = json.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Get the response from the webhook (optional)
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Bukkit.getLogger().info("Report successfully sent to Discord!");
            } else if (responseCode == 204) {
                Bukkit.getLogger().info("Report sent successfully to Discord (No Content Response).");
            } else {
                Bukkit.getLogger().warning("Failed to send report to Discord. Response Code: " + responseCode);
            }
        } catch (Exception e) {
            Bukkit.getLogger().severe("Error sending report to Discord: " + e.getMessage());
        }
    }

    private String getMessage(String path) {
        // Retrieve prefix from config and convert color codes
        String value = plugin.getConfig().getString("prefix", "&8[&fCore&8] &7»&f");
        value = ChatColor.translateAlternateColorCodes('&', value); // Convert color codes in the prefix

        // Load messages.yml file
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false); // Save default if not found
        }

        // Load YamlConfiguration
        YamlConfiguration messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);

        // Get the message from messages.yml, or return the default if not found
        String message = messagesConfig.getString(path, "Message not found for path: " + path);

        // Return the message with the prefix added
        return value + " " + ChatColor.translateAlternateColorCodes('&', message); // Add prefix and translate color codes
    }
}