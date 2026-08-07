package fr.Nat0uille.NATMOTD;

import fr.Nat0uille.NatMOTD.ModrinthUpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.nio.file.Path;

public class CheckVersion {
    private static final String MODRINTH_PROJECT = "nat-motd";
    private static final String MODRINTH_LOADER = "paper";
    private static final long PERIOD_TICKS = 3 * 60 * 60 * 20L;

    public boolean outdated;
    public String remoteVersion;
    public String localVersion;

    public static void startVersionCheck(Main plugin, CheckVersion checkVersion) {
        new BukkitRunnable() {
            @Override
            public void run() {
                checkVersion.localVersion = plugin.getDescription().getVersion();

                if (!plugin.getConfig().getBoolean("update-checker.enabled", true)) {
                    return;
                }

                try {
                    ModrinthUpdateChecker.LatestVersion latest = ModrinthUpdateChecker.fetchLatest(MODRINTH_PROJECT, MODRINTH_LOADER);
                    if (latest == null) {
                        return;
                    }
                    checkVersion.remoteVersion = latest.versionNumber;

                    if (checkVersion.remoteVersion.equals(checkVersion.localVersion)) {
                        return;
                    }

                    checkVersion.outdated = true;
                    Bukkit.getLogger().warning("[NAT-MOTD] The plugin is not up to date. Local version: " + checkVersion.localVersion + ", latest version: " + checkVersion.remoteVersion);

                    if (plugin.getConfig().getBoolean("update-checker.auto-download", true)) {
                        Path target = Bukkit.getUpdateFolderFile().toPath().resolve(plugin.getPluginFile().getName());
                        ModrinthUpdateChecker.downloadTo(latest.downloadUrl, target);
                        Bukkit.getLogger().info("[NAT-MOTD] Downloaded version " + checkVersion.remoteVersion + " to " + target + ". It will be installed automatically on the next restart.");
                    }
                } catch (Exception e) {
                    Bukkit.getLogger().severe("[NAT-MOTD] Error while checking for updates: " + e.getMessage());
                }
            }
        }.runTaskTimerAsynchronously(plugin, 0L, PERIOD_TICKS);
    }

    public boolean outdated() {
        return outdated;
    }

    public String getRemoteVersion() {
        return remoteVersion;
    }

    public String getLocalVersion() {
        return localVersion;
    }
}
