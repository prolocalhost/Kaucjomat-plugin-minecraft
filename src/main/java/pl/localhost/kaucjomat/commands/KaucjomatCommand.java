package pl.localhost.kaucjomat.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.localhost.kaucjomat.Main;
import pl.localhost.kaucjomat.gui.KaucjomatGUI;

public class KaucjomatCommand implements CommandExecutor {

    private final Main plugin;

    public KaucjomatCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String prefix = plugin.getConfig().getString("messages.prefix", "&8[&aKaucjomat&8] &7");
        String noPerm = plugin.getConfig().getString("messages.no-permission", "&cNie masz uprawnien!");

        if (command.getName().equalsIgnoreCase("kaucjomat")) {
            if (!(sender instanceof Player)) return true;
            Player player = (Player) sender;
            if (!player.hasPermission("kaucjomat.use")) {
                player.sendMessage(plugin.color(prefix + noPerm));
                return true;
            }
            KaucjomatGUI.openGUI(plugin, player);
            return true;
        }

        if (command.getName().equalsIgnoreCase("kaucjomatdaj")) {
            if (args.length > 0) {
                try {
                    int amount = Integer.parseInt(args[0]);
                    ItemStack bottle = plugin.getBottleManager().createBottle();
                    bottle.setAmount(amount);
                    ((Player) sender).getInventory().addItem(bottle);
                    sender.sendMessage("Dano " + amount + " butelek.");
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cPodaj liczbę!");
                }
            } else {
                ((Player) sender).getInventory().addItem(plugin.getBottleManager().createBottle());
            }
            return true;
        }

        if (command.getName().equalsIgnoreCase("kaucjomatreload")) {
            if (!sender.hasPermission("kaucjomat.reload")) {
                sender.sendMessage(plugin.color(prefix + noPerm));
                return true;
            }
            plugin.reloadConfig();
            sender.sendMessage(plugin.color(prefix + plugin.getConfig().getString("messages.reloaded")));
            return true;
        }

        return false;
    }
}