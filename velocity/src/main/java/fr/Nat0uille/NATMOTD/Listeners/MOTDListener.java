package fr.Nat0uille.NATMOTD.Listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.InboundConnection;
import com.velocitypowered.api.util.Favicon;
import fr.Nat0uille.NATMOTD.Main;
import fr.Nat0uille.NatMOTD.CenterMOTD;
import fr.Nat0uille.NatMOTD.TimerMOTD;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.spongepowered.configurate.ConfigurationNode;
import com.velocitypowered.api.proxy.server.ServerPing;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MOTDListener {
    private final Main main;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public MOTDListener(Main main) {
        this.main = main;
    }

    @Subscribe
    public void onProxyPing(ProxyPingEvent event) {
        ConfigurationNode config = main.getConfig();
        if (config == null) {
            return;
        }

        int selected = resolveSelectedMotd(config, event.getConnection());
        String rawLine1 = config.node("motd", selected, "line1", "text").getString("");
        String rawLine2 = config.node("motd", selected, "line2", "text").getString("");
        boolean center1 = config.node("motd", selected, "line1", "center").getBoolean(false);
        boolean center2 = config.node("motd", selected, "line2", "center").getBoolean(false);
        int playersMax = config.node("motd", selected, "players-max").getInt(0);
        boolean playersMaxOnline = config.node("motd", selected, "players-max-online").getBoolean(false);

        if (rawLine1.isEmpty() && rawLine2.isEmpty()) {
            return;
        }

        ServerPing originalPing = event.getPing();
        int onlinePlayers = originalPing.getPlayers().map(ServerPing.Players::getOnline).orElse(0);
        int maxPlayers = playersMaxOnline
                ? onlinePlayers + 1
                : (playersMax > 0 ? playersMax : originalPing.getPlayers().map(ServerPing.Players::getMax).orElse(20));

        String version = main.getVersion();
        rawLine1 = rawLine1.replace("{version}", version)
                .replace("{online}", String.valueOf(onlinePlayers))
                .replace("{max}", String.valueOf(maxPlayers));
        rawLine2 = rawLine2.replace("{version}", version)
                .replace("{online}", String.valueOf(onlinePlayers))
                .replace("{max}", String.valueOf(maxPlayers));

        rawLine1 = TimerMOTD.replaceTimers(rawLine1,
                name -> config.node("timers", name, "end").getString(""),
                name -> config.node("timers", name, "expired").getString(""));
        rawLine2 = TimerMOTD.replaceTimers(rawLine2,
                name -> config.node("timers", name, "end").getString(""),
                name -> config.node("timers", name, "expired").getString(""));

        Component comp1 = mm.deserialize(rawLine1);
        Component comp2 = mm.deserialize(rawLine2);

        String legacy1 = LegacyComponentSerializer.builder()
                .hexColors()
                .useUnusualXRepeatedCharacterHexFormat()
                .build()
                .serialize(comp1);
        String legacy2 = LegacyComponentSerializer.builder()
                .hexColors()
                .useUnusualXRepeatedCharacterHexFormat()
                .build()
                .serialize(comp2);

        if (center1) legacy1 = CenterMOTD.CenterMOTD(legacy1);
        if (center2) legacy2 = CenterMOTD.CenterMOTD(legacy2);

        Component final1 = LegacyComponentSerializer.builder()
                .hexColors()
                .useUnusualXRepeatedCharacterHexFormat()
                .build()
                .deserialize(legacy1);
        Component final2 = LegacyComponentSerializer.builder()
                .hexColors()
                .useUnusualXRepeatedCharacterHexFormat()
                .build()
                .deserialize(legacy2);

        Component motd = final1.append(Component.newline()).append(final2);

        ServerPing.Builder builder = ServerPing.builder()
            .version(originalPing.getVersion())
            .description(motd)
            .maximumPlayers(maxPlayers);

        Favicon customFavicon = main.getServerFavicon();
        if (customFavicon != null) {
            builder.favicon(customFavicon);
        } else {
            originalPing.getFavicon().ifPresent(builder::favicon);
        }

        originalPing.getPlayers().ifPresent(players ->
            builder.onlinePlayers(players.getOnline())
        );

        if (config.node("motd", selected, "hover", "enabled").getBoolean(false)) {
            List<ServerPing.SamplePlayer> sample = new ArrayList<>();
            for (ConfigurationNode lineNode : config.node("motd", selected, "hover", "lines").childrenList()) {
                String text = lineNode.getString("").replace("{version}", version)
                        .replace("{online}", String.valueOf(onlinePlayers))
                        .replace("{max}", String.valueOf(maxPlayers));
                text = TimerMOTD.replaceTimers(text,
                        name -> config.node("timers", name, "end").getString(""),
                        name -> config.node("timers", name, "expired").getString(""));
                String legacyLine = LegacyComponentSerializer.builder()
                        .hexColors()
                        .useUnusualXRepeatedCharacterHexFormat()
                        .build()
                        .serialize(mm.deserialize(text));
                sample.add(new ServerPing.SamplePlayer(legacyLine, UUID.randomUUID()));
            }
            builder.samplePlayers(sample);
        }

        event.setPing(builder.build());
    }

    private int resolveSelectedMotd(ConfigurationNode config, InboundConnection connection) {
        int fallback = config.node("motd", "selected").getInt(1);

        String host = connection.getVirtualHost().map(InetSocketAddress::getHostString).orElse(null);
        if (host == null) {
            return fallback;
        }

        for (ConfigurationNode entry : config.node("motd", "hostnames").childrenList()) {
            String entryHost = entry.node("hostname").getString("");
            int entryMotd = entry.node("motd").getInt(-1);
            if (entryMotd > 0 && host.equalsIgnoreCase(entryHost)) {
                return entryMotd;
            }
        }
        return fallback;
    }
}
