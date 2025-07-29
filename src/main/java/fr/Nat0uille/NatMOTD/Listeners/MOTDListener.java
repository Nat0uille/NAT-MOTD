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

        String rawLine1 = Main.getConfig().getString("motd.line1.text");
        String rawLine2 = Main.getConfig().getString("motd.line2.text");

        boolean center1 = Main.getConfig().getBoolean("motd.line1.center");
        boolean center2 = Main.getConfig().getBoolean("motd.line2.center");

        Component comp1 = mm.deserialize(rawLine1);
        Component comp2 = mm.deserialize(rawLine2);

        String legacy1 = LegacyComponentSerializer.legacySection().serialize(comp1);
        String legacy2 = LegacyComponentSerializer.legacySection().serialize(comp2);

        if (center1) legacy1 = centerMotd(legacy1);
        if (center2) legacy2 = centerMotd(legacy2);

        Component final1 = LegacyComponentSerializer.legacySection().deserialize(legacy1);
        Component final2 = LegacyComponentSerializer.legacySection().deserialize(legacy2);

        Component motd = final1.append(Component.newline()).append(final2);
        event.motd(motd);
    }

    public static String centerMotd(String message) {
        if (message == null || message.equals("")) return "";

        int CENTER_PX = 130;
        int messagePxSize = 0;

        boolean previousCode = false;
        boolean isBold = false;

        for (char c : message.toCharArray()) {
            if (c == '§') {
                previousCode = true;
                continue;
            } else if (previousCode) {
                previousCode = false;
                if (c == 'l' || c == 'L') {
                    isBold = true;
                } else {
                    isBold = false;
                }
                continue;
            }

            int charPx = getDefaultCharWidth(c);
            messagePxSize += isBold ? charPx + 1 : charPx;
            messagePxSize++;
        }

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceWidth = getDefaultCharWidth(' ');
        int compensated = 0;
        StringBuilder sb = new StringBuilder();

        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceWidth;
        }

        return sb + message;
    }

    public static int getDefaultCharWidth(char c) {
        switch (c) {
            case 'i': case 'l': case '.': case ',': case '!': case '|': return 2;
            case '\'': return 3;
            case ' ': return 4;
            case 't': case 'I': case '[': case ']': return 4;
            case 'k': case '{': case '}': case '<': case '>': return 5;
            default: return 6;
        }
    }
}
