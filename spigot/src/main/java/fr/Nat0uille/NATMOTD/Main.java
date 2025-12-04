package fr.Nat0uille.NATMOTD;

import fr.Nat0uille.NATMOTD.Commands.MOTDCommand;
import fr.Nat0uille.NATMOTD.Listeners.MOTDListener;
import fr.Nat0uille.NATMOTD.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private CheckVersion checkVersion;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
        console.sendMessage("");
        console.sendMessage("          §a___   "+ getDescription().getName());
        console.sendMessage("§a|\\ |  /\\   |    §2Made by §aNat0uille");
        console.sendMessage("§a| \\| /~~\\  |    §2Version §a" + getDescription().getVersion());
        console.sendMessage("");

        Metrics metrics = new Metrics(this,28216);
        getCommand("Nat-MOTD").setExecutor(new MOTDCommand(this));

        getServer().getPluginManager().registerEvents(new MOTDListener(this), this);

        checkVersion = new CheckVersion();
        CheckVersion.startVersionCheck(this, checkVersion);
    }

    @Override
    public void onDisable() {
        getLogger().info(getDescription().getName() + " disabled!");
    }

    public CheckVersion getCheckVersion() {
        return checkVersion;
    }
}
