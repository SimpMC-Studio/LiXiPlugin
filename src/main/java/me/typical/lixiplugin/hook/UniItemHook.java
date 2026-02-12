package me.typical.lixiplugin.hook;

import io.github.projectunified.uniitem.all.AllItemProvider;
import io.github.projectunified.uniitem.api.ItemKey;
import io.github.projectunified.uniitem.api.ItemProvider;
import me.typical.lixiplugin.LXPlugin;
import me.typical.lixiplugin.config.types.MainConfig;
import me.typical.lixiplugin.service.IService;
import me.typical.lixiplugin.util.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Hook for creating custom envelope items.
 * Currently supports vanilla Minecraft items with custom display names and lore.
 * Can be extended to support ItemsAdder, Nexo, Oraxen when needed.
 */
public class UniItemHook implements IService {

    private final LXPlugin plugin = LXPlugin.getInstance();
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private NamespacedKey lixiIdKey;
    private ItemProvider uniItemProvider;

    @Override
    public void setup() {
        lixiIdKey = new NamespacedKey(plugin, "lixi-id");
        MessageUtil.info("UniItem hook initialized");

         uniItemProvider = new AllItemProvider();
    }

    @Override
    public void shutdown() {
        // Nothing to clean up
    }

    /**
     * Create an envelope item with the specified amount
     *
     * @param amount The amount of money in the envelope
     * @param id     The unique envelope ID
     * @return ItemStack representing the envelope
     */
    public ItemStack createEnvelopeItem(double amount, UUID id) {
        MainConfig.EnvelopeConfig envelopeConfig = plugin.getConfigManager()
                .getConfig(MainConfig.class)
                .getEnvelope();

        ItemStack item = createItem(envelopeConfig);

        // Apply custom display name and lore
        item.editMeta(meta -> {
            // Set display name
            String displayName = envelopeConfig.getDisplayName();
            Component nameComponent = miniMessage.deserialize(displayName);
            meta.displayName(nameComponent);

            // Set lore with amount placeholder
            String[] loreTemplate = envelopeConfig.getLore();
            List<Component> loreComponents = new ArrayList<>();
            String formattedAmount = plugin.getService(VaultHook.class).format(amount);

            for (String loreLine : loreTemplate) {
                String processedLine = loreLine.replace("%amount%", formattedAmount);
                loreComponents.add(miniMessage.deserialize(processedLine));
            }
            meta.lore(loreComponents);

            // Add envelope ID to PDC
            meta.getPersistentDataContainer().set(lixiIdKey, PersistentDataType.STRING, id.toString());
        });

        return item;
    }

    /**
     * Create an item using UniItem API with plugin priority
     * Priority order: ItemsAdder → Oraxen → Nexo → Vanilla Paper
     *
     * @param config The envelope configuration
     * @return ItemStack from the first configured plugin or vanilla paper fallback
     */
    private ItemStack createItem(MainConfig.EnvelopeConfig config) {
        // Try ItemsAdder first
        String itemsAdderItemId = config.getItemsadder().getItemId();
        if (itemsAdderItemId != null && !itemsAdderItemId.trim().isEmpty()) {
            ItemStack item = uniItemProvider.item(new ItemKey("itemsadder", itemsAdderItemId));
            if (item != null) {
                return item;
            } else {
            }
        }

        // Try Oraxen second
        String oraxenItemId = config.getOraxen().getItemId();
        if (oraxenItemId != null && !oraxenItemId.trim().isEmpty()) {
            ItemStack item = uniItemProvider.item(new ItemKey("oraxen", oraxenItemId));
            if (item != null) {
                return item;
            } else {
            }
        }

        // Try Nexo third
        String nexoItemId = config.getNexo().getItemId();
        if (nexoItemId != null && !nexoItemId.trim().isEmpty()) {
            ItemStack item = uniItemProvider.item(new ItemKey("nexo", nexoItemId));
            if (item != null) {
                return item;
            }
        }

        // Fallback to vanilla paper
        return new ItemStack(Material.RED_CANDLE);
    }

    /**
     * Detect the ItemKey from an ItemStack using UniItem API
     *
     * @param item The item to detect
     * @return ItemKey representing the item, or null if vanilla/unknown
     */
    public ItemKey detectItemKey(ItemStack item) {
        if (item == null || item.getType().isAir()) {
            return null;
        }
        return uniItemProvider.key(item);
    }

    /**
     * Check if an item is a lixi envelope
     *
     * @param item The item to check
     * @return true if the item is a lixi envelope
     */
    public boolean isLixiEnvelope(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        return item.getItemMeta()
                .getPersistentDataContainer()
                .has(lixiIdKey, PersistentDataType.STRING);
    }

    /**
     * Get the envelope ID from an item
     *
     * @param item The item
     * @return The envelope UUID, or null if not a lixi envelope
     */
    public UUID getEnvelopeId(ItemStack item) {
        if (!isLixiEnvelope(item)) {
            return null;
        }
        String idString = item.getItemMeta()
                .getPersistentDataContainer()
                .get(lixiIdKey, PersistentDataType.STRING);
        try {
            return UUID.fromString(idString);
        } catch (IllegalArgumentException e) {
            return null;
        }
        
    }

}

