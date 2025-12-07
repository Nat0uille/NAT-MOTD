package fr.Nat0uille.NATMOTD.Listeners;

import fr.Nat0uille.NATMOTD.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.scheduler.BukkitRunnable;

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

        int selected = Main.getConfig().getInt("motd.selected", 1);

        String pathPrefix = "motd." + selected + ".";
        String rawLine1 = Main.getConfig().getString(pathPrefix + "line1.text");
        String rawLine2 = Main.getConfig().getString(pathPrefix + "line2.text");

        boolean center1 = Main.getConfig().getBoolean(pathPrefix + "line1.center");
        boolean center2 = Main.getConfig().getBoolean(pathPrefix + "line2.center");

        int playersMax = Main.getConfig().getInt(pathPrefix + "players-max", 0);

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
    }
}