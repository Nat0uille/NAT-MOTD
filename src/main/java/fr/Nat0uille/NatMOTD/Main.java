package fr.Nat0uille.NatMOTD;

import fr.Nat0uille.NatMOTD.Commands.MOTDCommand;
import fr.Nat0uille.NatMOTD.Listeners.MOTDListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        getLogger().info(getDescription().getName() + " by " + String.join(", ", getDescription().getAuthors()));
        getLogger().info("Version: " + getDescription().getVersion());

        getCommand("Nat-MOTD").setExecutor(new MOTDCommand(this));

        getServer().getPluginManager().registerEvents(new MOTDListener(this), this);
    }

    @Override
    public void onDisable() {
        getLogger().info(getDescription().getName() + " disabled!");
    }
}
