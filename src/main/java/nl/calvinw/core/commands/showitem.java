package nl.calvinw.core.commands;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Map;
import java.util.Set;

import net.md_5.bungee.api.chat.BaseComponent;

public class showitem implements CommandExecutor {

    private final JavaPlugin plugin;

    public showitem(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(getMessage("general.only-users"));
            return true;
        }

        Player player = (Player) sender;
        ItemStack heldItem = player.getInventory().getItemInMainHand();

        if (heldItem == null || heldItem.getType() == Material.AIR) {
            player.sendMessage(getMessage("showitem.no-item"));
            return true;
        }

        // Get item details
        String itemName = getItemName(heldItem);
        String enchantmentText = getEnchantments(heldItem);
        String loreText = getLore(heldItem);

        // Format the message
        String messageTemplate = getMessage("showitem.message");
        String formattedMessage = messageTemplate
                .replace("{player}", player.getName())
                .replace("{item}", heldItem.getAmount() + "x " + itemName);

        // Create a TextComponent for the message
        TextComponent textComponent = new TextComponent(ChatColor.translateAlternateColorCodes('&', formattedMessage));

        // Create hover text (enchantments and lore)
        TextComponent hoverText = createHoverText(itemName, enchantmentText, loreText);

        // Set hover event
        HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{hoverText});
        textComponent.setHoverEvent(hoverEvent);

        // Send the message to the player
        for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
            onlinePlayer.spigot().sendMessage(textComponent);
        }

        return true;
    }

    private String getItemName(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasDisplayName()) {
            return ChatColor.translateAlternateColorCodes('&', meta.getDisplayName());
        } else {
            String defaultName = item.getType().toString().replace("_", " ");
            return capitalize(defaultName);
        }
    }

    private String getEnchantments(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        StringBuilder enchantmentList = new StringBuilder();

        if (meta != null) {
            // Handle enchanted books using EnchantmentStorageMeta
            if (item.getType() == Material.ENCHANTED_BOOK && meta instanceof org.bukkit.inventory.meta.EnchantmentStorageMeta) {
                org.bukkit.inventory.meta.EnchantmentStorageMeta enchantmentStorageMeta = (org.bukkit.inventory.meta.EnchantmentStorageMeta) meta;
                for (Map.Entry<org.bukkit.enchantments.Enchantment, Integer> enchantment : enchantmentStorageMeta.getStoredEnchants().entrySet()) {
                    String enchantmentName = enchantment.getKey().getKey().toString().replace("minecraft:", "");
                    enchantmentName = capitalize(enchantmentName);
                    int level = enchantment.getValue();
                    String romanLevel = toRoman(level);
                    enchantmentList.append(enchantmentName).append(" ").append(romanLevel).append("\n");
                }
            } else if (!meta.getEnchants().isEmpty()) {
                // Handle other item enchantments (non-enchanted books)
                Set<Map.Entry<org.bukkit.enchantments.Enchantment, Integer>> enchantments = meta.getEnchants().entrySet();
                for (Map.Entry<org.bukkit.enchantments.Enchantment, Integer> enchantment : enchantments) {
                    String enchantmentName = enchantment.getKey().getKey().toString().replace("minecraft:", "");
                    enchantmentName = capitalize(enchantmentName);
                    int level = enchantment.getValue();
                    String romanLevel = toRoman(level);
                    enchantmentList.append(enchantmentName).append(" ").append(romanLevel).append("\n");
                }
            }
        }

        return enchantmentList.toString().trim();
    }

    private String toRoman(int level) {
        String[] romanNumerals = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
        return (level >= 1 && level <= 10) ? romanNumerals[level] : String.valueOf(level);
    }

    private String getLore(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        return (meta != null && meta.hasLore()) ? String.join(" ", meta.getLore()) : "";
    }

    private TextComponent createHoverText(String itemName, String enchantmentText, String loreText) {
        TextComponent hoverText = new TextComponent(ChatColor.RESET + itemName);

        if (!enchantmentText.isEmpty()) {
            String[] enchantments = enchantmentText.split("\n");
            for (String enchantment : enchantments) {
                hoverText.addExtra("\n" + ChatColor.YELLOW + enchantment);
            }
        }

        if (!loreText.isEmpty()) {
            hoverText.addExtra("\n" + ChatColor.GRAY + "Lore: " + loreText);
        }

        return hoverText;
    }

    private String getMessage(String path) {
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false); // Save default if not found
        }

        YamlConfiguration messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
        String message = messagesConfig.getString(path, "Message not found for path: " + path);
        String prefix = plugin.getConfig().getString("prefix", "&8[&fCore&8] &7»&f");
        return ChatColor.translateAlternateColorCodes('&', prefix) + " " + ChatColor.translateAlternateColorCodes('&', message);
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }
}
