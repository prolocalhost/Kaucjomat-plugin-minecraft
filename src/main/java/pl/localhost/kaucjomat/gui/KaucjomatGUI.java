package pl.localhost.kaucjomat.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.localhost.kaucjomat.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KaucjomatGUI implements Listener {

    private final Main plugin;
    public KaucjomatGUI(Main plugin) {
        this.plugin = plugin;
    }

    public static void openGUI(Main plugin, Player player) {
        String title = plugin.color(plugin.getConfig().getString("gui.title", "&8Kaucjomat"));
        int size = plugin.getConfig().getInt("gui.size", 27);
        Inventory inv = Bukkit.createInventory(null, size, title + " §0" );

        List<Integer> inputSlots = plugin.getConfig().getIntegerList("gui.input-slots");
        int buttonSlot = plugin.getConfig().getInt("gui.button-slot", 22);

        Material bgMat = Material.getMaterial(plugin.getConfig().getString("gui.background.material", "GRAY_STAINED_GLASS_PANE"));
        if (bgMat == null) bgMat = Material.GRAY_STAINED_GLASS_PANE;
        ItemStack bgItem = new ItemStack(bgMat);
        ItemMeta bgMeta = bgItem.getItemMeta();
        if (bgMeta != null) {
            bgMeta.setDisplayName(plugin.color(plugin.getConfig().getString("gui.background.name", " ")));
            bgItem.setItemMeta(bgMeta);
        }

        for (int i = 0; i < size; i++) {
            if (!inputSlots.contains(i) && i != buttonSlot) {
                inv.setItem(i, bgItem);
            }
        }

        Material btnMat = Material.getMaterial(plugin.getConfig().getString("gui.button.material", "EMERALD_BLOCK"));
        if (btnMat == null) btnMat = Material.EMERALD_BLOCK;
        ItemStack btnItem = new ItemStack(btnMat);
        ItemMeta btnMeta = btnItem.getItemMeta();
        if (btnMeta != null) {
            btnMeta.setDisplayName(plugin.color(plugin.getConfig().getString("gui.button.name", "&aWymień")));

            int req = plugin.getConfig().getInt("economy.bottles-required", 5);
            double rew = plugin.getConfig().getDouble("economy.money-reward", 10.0);

            List<String> lore = plugin.getConfig().getStringList("gui.button.lore").stream()
                    .map(line -> plugin.color(line.replace("{BOTTLES}", String.valueOf(req)).replace("{MONEY}", String.valueOf(rew))))
                    .collect(Collectors.toList());
            btnMeta.setLore(lore);
            btnItem.setItemMeta(btnMeta);
        }
        inv.setItem(buttonSlot, btnItem);

        player.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getView().getTitle().endsWith("§0" )) {
            Player player = (Player) event.getWhoClicked();
            int clickedSlot = event.getRawSlot();
            List<Integer> inputSlots = plugin.getConfig().getIntegerList("gui.input-slots");
            int buttonSlot = plugin.getConfig().getInt("gui.button-slot", 22);

            if (event.isShiftClick() && event.getRawSlot() >= event.getInventory().getSize()) {
                ItemStack item = event.getCurrentItem();
                if (item != null && !plugin.getBottleManager().isBottle(item)) {
                    event.setCancelled(true);
                    return;
                }
            }

            if (clickedSlot >= event.getInventory().getSize()) {
                return;
            }

            if (clickedSlot < event.getInventory().getSize()) {
                if (inputSlots.contains(clickedSlot)) {
                    ItemStack cursor = event.getCursor();
                    if (cursor != null && cursor.getType() != Material.AIR) {
                        if (!plugin.getBottleManager().isBottle(cursor)) {
                            event.setCancelled(true);
                        }
                    }
                    return;
                }

                event.setCancelled(true);

                if (clickedSlot == buttonSlot) {
                    processExchange(player, event.getInventory(), inputSlots);
                }
            }
        }
    }

    private void processExchange(Player player, Inventory inv, List<Integer> inputSlots) {
        int reqBottles = plugin.getConfig().getInt("economy.bottles-required", 5);
        double moneyReward = plugin.getConfig().getDouble("economy.money-reward", 10.0);
        String prefix = plugin.getConfig().getString("messages.prefix", "");

        int totalBottles = 0;
        List<ItemStack> bottlesFound = new ArrayList<>();

        for (int slot : inputSlots) {
            ItemStack item = inv.getItem(slot);
            if (plugin.getBottleManager().isBottle(item)) {
                totalBottles += item.getAmount();
                bottlesFound.add(item);
            }
        }

        if (totalBottles < reqBottles) {
            String msg = plugin.getConfig().getString("messages.not-enough-bottles", "&cZa mało butelek!");
            player.sendMessage(plugin.color(prefix + msg.replace("{MIN}", String.valueOf(reqBottles))));
            return;
        }

        int sets = totalBottles / reqBottles;
        int usedBottles = sets * reqBottles;
        int remainingBottles = totalBottles % reqBottles;
        double payout = sets * moneyReward;

        for (int slot : inputSlots) {
            inv.setItem(slot, null);
        }

        if (remainingBottles > 0) {
            ItemStack restBottle = plugin.getBottleManager().createBottle();
            restBottle.setAmount(remainingBottles);
            inv.setItem(inputSlots.get(0), restBottle);
        }

        plugin.getEconomy().depositPlayer(player, payout);

        String msg = plugin.getConfig().getString("messages.success", "&aWymieniono {AMOUNT} szt. za {MONEY}$");
        player.sendMessage(plugin.color(prefix + msg
                .replace("{AMOUNT}", String.valueOf(usedBottles))
                .replace("{MONEY}", String.format("%.2f", payout))));
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getView().getTitle().endsWith("§0")) {
            Player player = (Player) event.getPlayer();
            List<Integer> inputSlots = plugin.getConfig().getIntegerList("gui.input-slots");

            boolean itemsDropped = false;

            for (int slot : inputSlots) {
                ItemStack item = event.getInventory().getItem(slot);
                if (item != null && item.getType() != Material.AIR) {
                    Map<Integer, ItemStack> leftover = player.getInventory().addItem(item);
                    if (!leftover.isEmpty()) {
                        for (ItemStack drop : leftover.values()) {
                            player.getWorld().dropItemNaturally(player.getLocation(), drop);
                        }
                        itemsDropped = true;
                    }
                    event.getInventory().setItem(slot, null);
                }
            }

            if (itemsDropped) {
                String prefix = plugin.getConfig().getString("messages.prefix", "");
                String fullMsg = plugin.getConfig().getString("messages.inventory-full", "&cEQ pełne, przedmioty wypadły!");
                player.sendMessage(plugin.color(prefix + fullMsg));
            }
        }
    }
}