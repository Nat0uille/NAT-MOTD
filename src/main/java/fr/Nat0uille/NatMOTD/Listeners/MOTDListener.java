package fr.Nat0uille.NatMOTD.Listeners;

import fr.Nat0uille.NatMOTD.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class MOTDListener implements Listener {

    public Main Main;

    public MOTDListener(Main Main) {
        this.Main = Main;
    }

    @EventHandler
    public void onServerListPing(ServerListPingEvent event) {
        MiniMessage mm = MiniMessage.miniMessage();
        Component line1 = mm.deserialize(Main.getConfig().getString("motd.line1.text"));
        Component line2 = mm.deserialize(Main.getConfig().getString("motd.line2.text"));

        String motd = LegacyComponentSerializer.legacySection().serialize(line1.append(Component.newline()).append(line2));
        event.setMotd(motd);
    }
}
