package fr.Nat0uille.NatMOTD.Listeners;

import fr.Nat0uille.NatMOTD.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
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
        String rawLine1 = Main.getConfig().getString("motd.line1.text");
        String rawLine2 = Main.getConfig().getString("motd.line2.text");

        String combined = rawLine1 + "\n<reset>" + rawLine2;

        Component motd = mm.deserialize(combined);


        event.motd(motd);
    }
}
