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
        String prefixStr = main.getConfig().getString("messages.prefix");
        String nopermissionStr = main.getConfig().getString("messages.nopermission");
        Component prefix = mm.deserialize(prefixStr != null ? prefixStr : "");
        Component nopermission = mm.deserialize(nopermissionStr != null ? nopermissionStr : "");

        if (args.length == 0) {
            String usageStr = main.getConfig().getString("messages.usage");
            sender.sendMessage(prefix.append(mm.deserialize(usageStr != null ? usageStr : "")));
            return;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("natmotd.reload")) {
                sender.sendMessage(prefix.append(nopermission));
                return;
            }
            main.reloadConfig();
            String reloadStr = main.getConfig().getString("messages.reload");
            Component reloadMessage = mm.deserialize(reloadStr != null ? reloadStr : "");
            sender.sendMessage(prefix.append(reloadMessage));
            return;
        }
        if (args[0].equalsIgnoreCase("select")) {
            if (!sender.hasPermission("natmotd.select")) {
                sender.sendMessage(prefix.append(nopermission));
                return;
            }
            if (args.length == 1) {
                String usageSelectStr = main.getConfig().getString("messages.usage-select");
                sender.sendMessage(prefix.append(mm.deserialize(usageSelectStr != null ? usageSelectStr : "")));
                return;
            }
            try {
                int selected = Integer.parseInt(args[1]);
                try {
                    main.getConfig().node("motd", "selected").set(selected);
                    main.saveConfig();
                    String selectStr = main.getConfig().getString("messages.select");
                    Component selectMessage = mm.deserialize((selectStr != null ? selectStr : "").replace("{number}", args[1]));
                    sender.sendMessage(prefix.append(selectMessage));
                } catch (org.spongepowered.configurate.serialize.SerializationException se) {
                    sender.sendMessage(prefix.append(mm.deserialize("<red>Erreur lors de la sauvegarde de la sélection MOTD.</red>")));
                }
            } catch (NumberFormatException e) {
                String usageSelectStr = main.getConfig().getString("messages.usage-select");
                sender.sendMessage(prefix.append(mm.deserialize(usageSelectStr != null ? usageSelectStr : "")));
            }
            return;
        }
        sender.sendMessage(prefix.append(mm.deserialize(main.getConfig().getString("messages.usage"))));
    }
}
