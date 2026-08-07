package fr.Nat0uille.NATMOTD;

import fr.Nat0uille.NatMOTD.ModrinthUpdateChecker;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

public class CheckVersion {
    private static final String MODRINTH_PROJECT = "nat-motd";
    private static final String MODRINTH_LOADER = "velocity";

    public boolean outdated;
    public String remoteVersion;
    public String localVersion;

    public static void startVersionCheck(Main plugin, CheckVersion checkVersion) {
        plugin.getServer().getScheduler().buildTask(plugin, () -> {
            checkVersion.localVersion = plugin.getVersion();

            if (!plugin.getConfig().node("update-checker", "enabled").getBoolean(true)) {
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
                plugin.getServer().getConsoleCommandSource().sendMessage(Component.text(
                        "[NAT-MOTD] The plugin is not up to date. Local version: " + checkVersion.localVersion
                                + ", latest version: " + checkVersion.remoteVersion, NamedTextColor.RED));

                if (plugin.getConfig().node("update-checker", "auto-download").getBoolean(true)) {
                    Path target = plugin.getDataDirectory().resolve("update").resolve(latest.fileName);
                    ModrinthUpdateChecker.downloadTo(latest.downloadUrl, target);
                    plugin.getServer().getConsoleCommandSource().sendMessage(Component.text(
                            "[NAT-MOTD] Downloaded version " + checkVersion.remoteVersion + " to " + target
                                    + ". Velocity has no auto-update folder: move this file into your plugins/ folder and restart the proxy to apply it.",
                            NamedTextColor.YELLOW));
                }
            } catch (Exception e) {
                plugin.getServer().getConsoleCommandSource().sendMessage(Component.text(
                        "[NAT-MOTD] Error while checking for updates: " + e.getMessage(), NamedTextColor.RED));
            }
        }).delay(0, TimeUnit.SECONDS).repeat(3, TimeUnit.HOURS).schedule();
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
