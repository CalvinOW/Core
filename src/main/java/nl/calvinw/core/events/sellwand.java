package nl.calvinw.core.events;

import net.milkbowl.vault.economy.Economy;
import nl.calvinw.core.Core;
import me.gypopo.economyshopgui.api.EconomyShopGUIHook;
import me.gypopo.economyshopgui.api.objects.SellPrices;
import me.gypopo.economyshopgui.util.EconomyType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Container;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class sellwand implements Listener {

    private final Core plugin;
    private final Economy economy;

    public sellwand(Core plugin, Economy economy) {
        this.plugin = plugin;
        this.economy = economy;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        ItemStack item = player.getInventory().getItemInMainHand();

        // Ensure the player is holding the Sell Wand
        Material sellWandMaterial = getSellWandMaterial();
        String sellWandName = getMessageWithout("sellwand.item-name");


        if (item == null || item.getType() != sellWandMaterial || item.getItemMeta() == null || !item.getItemMeta().hasDisplayName()) {
            return;
        }
        if (!item.getItemMeta().getDisplayName().equals(sellWandName)) {
            return;
        }

        // Check if the player has the required permission (only when holding the correct item)
        if (!player.hasPermission("sellwand.use")) {
            player.sendMessage(getMessage("general.no-permission")); // Message when lacking permission
            return;
        }

        // Ensure the player is right-clicking a container (e.g., chest, barrel)
        Block block = event.getClickedBlock();
        if (block == null || !(block.getState() instanceof Container)) {
            return;
        }

        Container container = (Container) block.getState();
        Inventory inventory = container.getInventory();

        double totalValue = 0.0;
        Map<ItemStack, Integer> itemsToRemove = new HashMap<>();

        // Iterate through chest contents and calculate total value
        for (ItemStack stack : inventory.getContents()) {
            if (stack == null || stack.getType() == Material.AIR) {
                continue;
            }

            // Get the sell prices of the item from EconomyShopGUI
            SellPrices transaction = EconomyShopGUIHook.getSellPrices(player, stack);

            if (transaction != null) {
                Double pricePerItem = transaction.getPrice(EconomyType.getFromString("VAULT"));

                if (pricePerItem != null && pricePerItem > 0) {
                    totalValue += pricePerItem * stack.getAmount();
                    itemsToRemove.put(stack, stack.getAmount());
                }
            }
        }

        // Remove items from the chest and update the inventory
        for (Map.Entry<ItemStack, Integer> entry : itemsToRemove.entrySet()) {
            inventory.removeItem(entry.getKey());
        }

        // Finalize chest contents and send messages
        if (totalValue > 0) {
            economy.depositPlayer(player, totalValue);
            String successMessage = getMessage("sellwand.sold").replace("{amount}", String.format("%.2f", totalValue));
            player.sendMessage(successMessage);
        } else {
            player.sendMessage(getMessage("sellwand.no-items"));
        }

        // Handle wand usage count and deletion if needed
        handleWandUsage(player, item);

        // Prevent default interaction
        event.setCancelled(true);
    }

    private void handleWandUsage(Player player, ItemStack wand) {
        ItemMeta meta = wand.getItemMeta();
        if (meta == null) return;

        // Get current uses and max uses from config
        int currentUses = meta.getPersistentDataContainer().getOrDefault(getNamespaceKey("uses"), PersistentDataType.INTEGER, 0);
        int maxUses = plugin.getConfig().getInt("sellwand.max-uses", 10);

        // Increment current uses
        currentUses++;
        if (currentUses > maxUses) {
            // If wand has reached max uses, remove it from the player's inventory and send a message
            player.getInventory().removeItem(wand);
            player.sendTitle(
                    getMessageWithout("sellwand.broken.title"),
                    getMessageWithout("sellwand.broken.subtitle"),
                    10, 70, 20
            );
            return;
        }

        // Update PersistentDataContainer with new usage count
        meta.getPersistentDataContainer().set(getNamespaceKey("uses"), PersistentDataType.INTEGER, currentUses);

        // Update lore to reflect remaining uses
        List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
        String usesLore = getMessage("sellwand.lore-uses")
                .replace("{current}", String.valueOf(currentUses))
                .replace("{max}", String.valueOf(maxUses));
        if (lore.size() > 0) {
            lore.set(lore.size() - 1, usesLore); // Update last lore line
        } else {
            lore.add(usesLore); // Add if no lore
        }
        meta.setLore(lore);

        wand.setItemMeta(meta); // Update the item with the new meta
    }

    private Material getSellWandMaterial() {
        String materialName = plugin.getConfig().getString("sellwand.material", "BLAZE_ROD");
        Material material = Material.getMaterial(materialName.toUpperCase());
        if (material == null) {
            plugin.getLogger().warning("Invalid material specified for Sell Wand in config.yml: " + materialName);
            material = Material.BLAZE_ROD;
        }
        return material;
    }

    private String getMessage(String path) {
        return plugin.getMessage(path);
    }

    private String getMessageWithout(String path) {
        return plugin.getMessageWithout(path);
    }

    private NamespacedKey getNamespaceKey(String key) {
        return new NamespacedKey(plugin, key);
    }
}
