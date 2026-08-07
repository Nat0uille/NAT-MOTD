package fr.Nat0uille.NATMOTD;

import fr.Nat0uille.NATMOTD.Commands.MOTDCommand;
import fr.Nat0uille.NATMOTD.Listeners.MOTDListener;
import fr.Nat0uille.NATMOTD.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.CachedServerIcon;

import java.io.File;

public final class Main extends JavaPlugin {

    private CheckVersion checkVersion;
    private CachedServerIcon serverIcon;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadServerIcon();

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

    public void reloadServerIcon() {
        serverIcon = null;

        int selected = getConfig().getInt("motd.selected", 1);
        String iconPath = getConfig().getString("motd." + selected + ".icon", "");

        if (iconPath == null || iconPath.isBlank()) {
            return;
        }

        File iconFile = new File(getDataFolder(), iconPath);
        if (!iconFile.exists()) {
            getLogger().warning("MOTD icon file not found: " + iconFile.getPath());
            return;
        }

        try {
            serverIcon = getServer().loadServerIcon(iconFile);
        } catch (Exception e) {
            getLogger().warning("Failed to load MOTD icon '" + iconPath + "': " + e.getMessage());
        }
    }

    public CachedServerIcon getServerIcon() {
        return serverIcon;
    }

    public File getPluginFile() {
        return getFile();
    }
}
