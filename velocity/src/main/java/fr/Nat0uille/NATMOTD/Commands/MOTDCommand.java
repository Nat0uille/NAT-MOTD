package fr.Nat0uille.NATMOTD.Commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import fr.Nat0uille.NATMOTD.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class MOTDCommand implements SimpleCommand {
    private final Main main;

    public MOTDCommand(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();
        MiniMessage mm = MiniMessage.miniMessage();
        String prefixStr = main.getConfig().node("messages", "prefix").getString("");
        String nopermissionStr = main.getConfig().node("messages", "nopermission").getString("");
        Component prefix = mm.deserialize(prefixStr.isEmpty() ? "<bold>[NAT-MOTD]</bold> <gray>-" : prefixStr);
        Component nopermission = mm.deserialize(nopermissionStr.isEmpty() ? "<red>No permission</red>" : nopermissionStr);

        if (args.length == 0) {
            String usageStr = main.getConfig().node("messages", "usage").getString("");
            sender.sendMessage(prefix.append(mm.deserialize(usageStr.isEmpty() ? "<red>Usage: /nat-motd [reload|select]</red>" : usageStr)));
            return;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("natmotd.reload")) {
                sender.sendMessage(prefix.append(nopermission));
                return;
            }
            main.reloadConfig();
            String reloadStr = main.getConfig().node("messages", "reload").getString("");
            Component reloadMessage = mm.deserialize(reloadStr.isEmpty() ? "<green>Config reloaded!</green>" : reloadStr);
            sender.sendMessage(prefix.append(reloadMessage));
            return;
        }
        if (args[0].equalsIgnoreCase("select")) {
            if (!sender.hasPermission("natmotd.select")) {
                sender.sendMessage(prefix.append(nopermission));
                return;
            }
            if (args.length == 1) {
                String usageSelectStr = main.getConfig().node("messages", "usage-select").getString("");
                sender.sendMessage(prefix.append(mm.deserialize(usageSelectStr.isEmpty() ? "<red>Usage: /nat-motd select <number></red>" : usageSelectStr)));
                return;
            }
            try {
                int selected = Integer.parseInt(args[1]);
                main.updateSelectedMotd(selected);
                String selectStr = main.getConfig().node("messages", "select").getString("");
                Component selectMessage = mm.deserialize((selectStr.isEmpty() ? "<green>MOTD {number} selected!</green>" : selectStr).replace("{number}", args[1]));
                sender.sendMessage(prefix.append(selectMessage));
            } catch (NumberFormatException e) {
                String usageSelectStr = main.getConfig().node("messages", "usage-select").getString("");
                sender.sendMessage(prefix.append(mm.deserialize(usageSelectStr.isEmpty() ? "<red>Usage: /nat-motd select <number></red>" : usageSelectStr)));
            }
            return;
        }
        sender.sendMessage(prefix.append(mm.deserialize(main.getConfig().node("messages", "usage").getString("<red>Usage: /nat-motd [reload|select]</red>"))));
    }
}
