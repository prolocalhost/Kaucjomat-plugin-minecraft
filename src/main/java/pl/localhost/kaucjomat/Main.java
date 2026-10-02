package pl.localhost.kaucjomat;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import pl.localhost.kaucjomat.commands.KaucjomatCommand;
import pl.localhost.kaucjomat.gui.KaucjomatGUI;
import pl.localhost.kaucjomat.managers.BottleManager;

public class Main extends JavaPlugin {

    private Economy economy = null;
    private BottleManager bottleManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        if (!setupEconomy()) {
            getLogger().severe(String.format("KAUCJOMAT Wylaczanie z powodu braku Vault!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.bottleManager = new BottleManager(this);

        KaucjomatCommand cmd = new KaucjomatCommand(this);
        getCommand("kaucjomat").setExecutor(cmd);
        getCommand("kaucjomatdaj").setExecutor(cmd);
        getCommand("kaucjomatreload").setExecutor(cmd);

        getServer().getPluginManager().registerEvents(new KaucjomatGUI(this), this);

        getLogger().info("kaucjomacik smiga jak talalal!");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    public Economy getEconomy() {
        return economy;
    }

    public BottleManager getBottleManager() {
        return bottleManager;
    }

    public String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}