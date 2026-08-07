package fr.Nat0uille.NATMOTD.Listeners;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import fr.Nat0uille.NATMOTD.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.CachedServerIcon;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import fr.Nat0uille.NatMOTD.TimerMOTD;

import static fr.Nat0uille.NatMOTD.CenterMOTD.CenterMOTD;


public class MOTDListener implements Listener {

    public Main Main;
    MiniMessage mm = MiniMessage.miniMessage();

    public MOTDListener(Main Main) {
        this.Main = Main;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (event.getPlayer().hasPermission("natwhitelist.admin")) {
                    if (Main.getCheckVersion().outdated()) {
                        event.getPlayer().sendMessage(mm.deserialize(Main.getConfig().getString("messages.prefix") + Main.getConfig().getString("messages.outdated")
                                .replace("{latest}", Main.getCheckVersion().getRemoteVersion())
                                .replace("{local}", Main.getCheckVersion().getLocalVersion())));
                    }
                }
            }
        }.runTaskLater(Main, 20);
    }

    @EventHandler
    public void onServerListPing(ServerListPingEvent event) {
        MiniMessage mm = MiniMessage.miniMessage();

        int selected = resolveSelectedMotd(event.getHostname());

        String pathPrefix = "motd." + selected + ".";
        String rawLine1 = Main.getConfig().getString(pathPrefix + "line1.text");
        String rawLine2 = Main.getConfig().getString(pathPrefix + "line2.text");

        boolean center1 = Main.getConfig().getBoolean(pathPrefix + "line1.center");
        boolean center2 = Main.getConfig().getBoolean(pathPrefix + "line2.center");

        boolean playersMaxOnline = Main.getConfig().getBoolean(pathPrefix + "players-max-online", false);
        int playersOnline = event.getNumPlayers();
        int playersMax = playersMaxOnline
                ? playersOnline + 1
                : Main.getConfig().getInt(pathPrefix + "players-max", 0);

        String version = Main.getDescription().getVersion();
        if (rawLine1 != null) rawLine1 = rawLine1.replace("{version}", version)
                .replace("{online}", String.valueOf(playersOnline))
                .replace("{max}", String.valueOf(playersMax));
        if (rawLine2 != null) rawLine2 = rawLine2.replace("{version}", version)
                .replace("{online}", String.valueOf(playersOnline))
                .replace("{max}", String.valueOf(playersMax));

        rawLine1 = TimerMOTD.replaceTimers(rawLine1,
                name -> Main.getConfig().getString("timers." + name + ".end"),
                name -> Main.getConfig().getString("timers." + name + ".expired"));
        rawLine2 = TimerMOTD.replaceTimers(rawLine2,
                name -> Main.getConfig().getString("timers." + name + ".end"),
                name -> Main.getConfig().getString("timers." + name + ".expired"));

        Component comp1 = mm.deserialize(rawLine1);
        Component comp2 = mm.deserialize(rawLine2);

        String legacy1 = LegacyComponentSerializer.legacySection().serialize(comp1);
        String legacy2 = LegacyComponentSerializer.legacySection().serialize(comp2);

        if (center1) legacy1 = CenterMOTD(legacy1);
        if (center2) legacy2 = CenterMOTD(legacy2);

        Component final1 = LegacyComponentSerializer.legacySection().deserialize(legacy1);
        Component final2 = LegacyComponentSerializer.legacySection().deserialize(legacy2);

        Component motd = final1.append(Component.newline()).append(final2);
        event.motd(motd);
        event.setMaxPlayers(playersMax);

        CachedServerIcon icon = Main.getServerIcon();
        if (icon != null) {
            event.setServerIcon(icon);
        }

        if (event instanceof PaperServerListPingEvent paperEvent && Main.getConfig().getBoolean(pathPrefix + "hover.enabled", false)) {
            List<PaperServerListPingEvent.ListedPlayerInfo> listed = paperEvent.getListedPlayers();
            listed.clear();
            for (String line : Main.getConfig().getStringList(pathPrefix + "hover.lines")) {
                String text = line.replace("{version}", version)
                        .replace("{online}", String.valueOf(playersOnline))
                        .replace("{max}", String.valueOf(playersMax));
                text = TimerMOTD.replaceTimers(text,
                        name -> Main.getConfig().getString("timers." + name + ".end"),
                        name -> Main.getConfig().getString("timers." + name + ".expired"));
                String legacyLine = LegacyComponentSerializer.legacySection().serialize(mm.deserialize(text));
                listed.add(new PaperServerListPingEvent.ListedPlayerInfo(legacyLine, UUID.randomUUID()));
            }
        }
    }

    private int resolveSelectedMotd(String hostname) {
        int fallback = Main.getConfig().getInt("motd.selected", 1);
        if (hostname == null) {
            return fallback;
        }

        String host = hostname.split(":")[0].toLowerCase();
        for (Map<?, ?> entry : Main.getConfig().getMapList("motd.hostnames")) {
            Object entryHost = entry.get("hostname");
            Object entryMotd = entry.get("motd");
            if (entryHost != null && entryMotd instanceof Number && host.equalsIgnoreCase(String.valueOf(entryHost))) {
                return ((Number) entryMotd).intValue();
            }
        }
        return fallback;
    }
}