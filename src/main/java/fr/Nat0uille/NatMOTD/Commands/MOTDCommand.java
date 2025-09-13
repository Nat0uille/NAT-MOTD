package fr.Nat0uille.NatMOTD.Commands;

import fr.Nat0uille.NatMOTD.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MOTDCommand implements CommandExecutor {

    public Main Main;

    public MOTDCommand(Main Main) {
        this.Main = Main;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        MiniMessage mm = MiniMessage.miniMessage();
        Component prefix = mm.deserialize(Main.getConfig().getString("messages.prefix"));
        Component nopermission = mm.deserialize(Main.getConfig().getString("messages.nopermission"));
        if (!sender.hasPermission("natmotd.reload")) {
            sender.sendMessage(prefix.append(nopermission));
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(prefix.append(mm.deserialize(Main.getConfig().getString("messages.usage"))));
            return true;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            Main.reloadConfig();
            Component reloadMessage = mm.deserialize(Main.getConfig().getString("messages.reload"));
            sender.sendMessage(prefix.append(reloadMessage));
            return true;
        }
        return false;
    }
}
