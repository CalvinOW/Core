package nl.calvinw.core.commands;

import nl.calvinw.core.Core;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.io.File;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import org.bukkit.enchantments.Enchantment;

public class givesellwand implements CommandExecutor {

    private final Core plugin;

    public givesellwand(Core plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check for permission
        if (!sender.hasPermission("sellwand.give")) {
            sender.sendMessage(getMessage("general.no-permission"));
            return true;
        }

        // Determine the target player
        Player targetPlayer;
        if (args.length > 0) {
            // Get target player from argument
            targetPlayer = Bukkit.getPlayer(args[0]);
            if (targetPlayer == null || !targetPlayer.isOnline()) {
                sender.sendMessage(getMessage("sellwand.player-not-found").replace("{player}", args[0]));
                return true;
            }
        } else {
            // Use sender as target if no argument provided and sender is a player
            if (sender instanceof Player) {
                targetPlayer = (Player) sender;
            } else {
                sender.sendMessage(getMessage("sellwand.no-player"));
                return true;
            }
        }

        // Create the Sell Wand item
        Material sellWandMaterial = getSellWandMaterial();
        ItemStack sellWand = new ItemStack(sellWandMaterial);
        ItemMeta meta = sellWand.getItemMeta();

        if (meta == null) {
            sender.sendMessage(getMessage("sellwand.error"));  // Show error if the item meta is null
            return false;
        }

        // Set display name from messages.yml
        meta.setDisplayName(getMessageWithout("sellwand.item-name"));

        // Fetch lore from messages.yml
        List<String> lore = getMessageList("sellwand.item-lore");
        if (lore == null || lore.isEmpty()) {
            lore = new ArrayList<>();
        }

        // Fetch max uses from config
        int maxUses = plugin.getConfig().getInt("sellwand.max-uses", 10);  // Default to 10 if not found

        // Add dynamic lore for initial uses
        lore.add(getMessageWithout("sellwand.lore-uses")
                .replace("{current}", "0")
                .replace("{max}", String.valueOf(maxUses)));

        // Set the lore
        meta.setLore(lore);

        // Add initial uses metadata
        NamespacedKey usesKey = new NamespacedKey(plugin, "sellwand_uses");
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(usesKey, PersistentDataType.INTEGER, 0); // Start at 0

        // Add enchantment to make the wand enchanted (using a dummy enchantment here)
        meta.addEnchant(Enchantment.UNBREAKING, 1, true);  // You can replace with any enchantment you prefer
        meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);  // Hide enchantment from the item tooltip

        sellWand.setItemMeta(meta);

        // Give the Sell Wand to the target player
        targetPlayer.getInventory().addItem(sellWand);

        // Notify the sender and target player
        targetPlayer.sendMessage(getMessage("sellwand.given-target"));
        if (!targetPlayer.equals(sender)) {
            sender.sendMessage(getMessage("sellwand.given").replace("{player}", targetPlayer.getName()));
        }
        return true;
    }

    // Fetch Material for the Sell Wand from config.yml
    private Material getSellWandMaterial() {
        String materialName = plugin.getConfig().getString("sellwand.material", "BLAZE_ROD"); // Default to BLAZE_ROD if not found
        Material material = Material.getMaterial(materialName.toUpperCase());
        if (material == null) {
            plugin.getLogger().warning("Invalid material specified for Sell Wand in config.yml: " + materialName);
            material = Material.BLAZE_ROD; // Fallback to default material if invalid
        }
        return material;
    }

    // Fetch a message string from messages.yml
    private String getMessage(String path) {
        return plugin.getMessage(path); // Assuming plugin.getMessage() is already implemented
    }

    // Fetch a message string from messages.yml without certain formatting
    private String getMessageWithout(String path) {
        return plugin.getMessageWithout(path); // Assuming plugin.getMessageWithout() is already implemented
    }

    private List<String> getMessageList(String path) {
        // Load messages.yml file
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false); // Save default if not found
        }

        // Load YamlConfiguration
        YamlConfiguration messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);

        // Get the list of messages from messages.yml, or return an empty list if not found
        List<String> messages = messagesConfig.getStringList(path);
        if (messages.isEmpty()) {
            plugin.getLogger().warning("No messages found for path: " + path);
        }

        // Translate color codes for each message
        List<String> coloredMessages = new ArrayList<>();
        for (String message : messages) {
            coloredMessages.add(ChatColor.translateAlternateColorCodes('&', message));
        }

        return coloredMessages;
    }
}
