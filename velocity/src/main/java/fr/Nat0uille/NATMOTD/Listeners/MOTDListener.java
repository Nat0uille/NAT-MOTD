package fr.Nat0uille.NATMOTD.Listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import fr.Nat0uille.NATMOTD.Main;
import fr.Nat0uille.NatMOTD.CenterMOTD;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.spongepowered.configurate.ConfigurationNode;
import com.velocitypowered.api.proxy.server.ServerPing;

public class MOTDListener {
    private final Main main;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public MOTDListener(Main main) {
        this.main = main;
    }

    @Subscribe
    public void onProxyPing(ProxyPingEvent event) {
        ConfigurationNode config = main.getConfig();
        int selected = config.node("motd", "selected").getInt(1);
        String rawLine1 = config.node("motd", String.valueOf(selected), "line1", "text").getString();
        String rawLine2 = config.node("motd", String.valueOf(selected), "line2", "text").getString();
        boolean center1 = config.node("motd", String.valueOf(selected), "line1", "center").getBoolean();
        boolean center2 = config.node("motd", String.valueOf(selected), "line2", "center").getBoolean();
        int playersMax = config.node("motd", String.valueOf(selected), "players-max").getInt();

        Component comp1 = mm.deserialize(rawLine1);
        Component comp2 = mm.deserialize(rawLine2);
        String legacy1 = LegacyComponentSerializer.legacySection().serialize(comp1);
        String legacy2 = LegacyComponentSerializer.legacySection().serialize(comp2);
        if (center1) legacy1 = CenterMOTD.CenterMOTD(legacy1);
        if (center2) legacy2 = CenterMOTD.CenterMOTD(legacy2);
        Component final1 = LegacyComponentSerializer.legacySection().deserialize(legacy1);
        Component final2 = LegacyComponentSerializer.legacySection().deserialize(legacy2);
        Component motd = final1.append(Component.newline()).append(final2);

        ServerPing originalPing = event.getPing();
        ServerPing.Builder builder = ServerPing.builder()
            .version(originalPing.getVersion())
            .description(motd)
            .favicon(originalPing.getFavicon().orElse(null))
            .maximumPlayers(playersMax);
        event.setPing(builder.build());
    }
}
