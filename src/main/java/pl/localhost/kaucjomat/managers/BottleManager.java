package pl.localhost.kaucjomat.managers;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import pl.localhost.kaucjomat.Main;

import java.util.List;
import java.util.stream.Collectors;

public class BottleManager {

    private final Main plugin;
    private final NamespacedKey bottleKey;

    public BottleManager(Main plugin) {
        this.plugin = plugin;
        this.bottleKey = new NamespacedKey(plugin, "is_kaucjomat_bottle");
    }

    public ItemStack createBottle() {
        String matName = plugin.getConfig().getString("bottle.material", "GLASS_BOTTLE");
        Material mat = Material.getMaterial(matName.toUpperCase());
        if (mat == null) mat = Material.GLASS_BOTTLE;

        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(plugin.color(plugin.getConfig().getString("bottle.name")));
            List<String> lore = plugin.getConfig().getStringList("bottle.lore").stream()
                    .map(plugin::color)
                    .collect(Collectors.toList());
            meta.setLore(lore);

            meta.getPersistentDataContainer().set(bottleKey, PersistentDataType.BYTE, (byte) 1);
            item.setItemMeta(meta);
        }
        return item;
    }

    public boolean isBottle(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(bottleKey, PersistentDataType.BYTE);
    }
}