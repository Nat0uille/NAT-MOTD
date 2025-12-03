package fr.Nat0uille.NATMOTD;

import com.google.inject.Inject;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import fr.Nat0uille.NATMOTD.Commands.MOTDCommand;

import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import fr.Nat0uille.NATMOTD.Listeners.MOTDListener;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.ConfigurationNode;

@Plugin(
        id = "natmotd",
        name = "Nat-MOTD",
        version = "${version}",
        authors = {"Nat0uille"}
)
public class Main {

    private final ProxyServer server;
    private ConfigurationNode config;
    private final Path configPath;

    @Inject
    public Main(ProxyServer server, @DataDirectory Path dataDirectory) {
        this.server = server;
        try {
            Files.createDirectories(dataDirectory);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.configPath = dataDirectory.resolve("config.yml");
        loadConfig();
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        // Envoie les messages colorés à la console
        server.getConsoleCommandSource().sendMessage(Component.empty());
        server.getConsoleCommandSource().sendMessage(
            Component.text("          ___   Nat-MOTD", NamedTextColor.GREEN)
        );
        server.getConsoleCommandSource().sendMessage(
            Component.text("|\\ |  /\\   |    ", NamedTextColor.GREEN)
                .append(Component.text("Made by ", NamedTextColor.DARK_GREEN))
                .append(Component.text("Nat0uille", NamedTextColor.GREEN))
        );
        server.getConsoleCommandSource().sendMessage(
            Component.text("| \\| /~~\\  |    ", NamedTextColor.GREEN)
                .append(Component.text("Version ", NamedTextColor.DARK_GREEN))
                .append(Component.text("1.2.0", NamedTextColor.GREEN))
        );
        server.getConsoleCommandSource().sendMessage(Component.empty());

        server.getCommandManager().register(
            server.getCommandManager().metaBuilder("nat-motd").aliases("motd").build(),
            new MOTDCommand(this)
        );
        server.getEventManager().register(this, new MOTDListener(this));
    }

    public ConfigurationNode getConfig() {
        return config;
    }

    public void reloadConfig() {
        loadConfig();
    }

    private YamlConfigurationLoader createLoader() {
        return YamlConfigurationLoader.builder()
                .path(configPath)
                .nodeStyle(NodeStyle.BLOCK)
                .indent(2)
                .build();
    }

    public void updateSelectedMotd(int selected) {
        try {
            List<String> lines = Files.readAllLines(configPath, StandardCharsets.UTF_8);
            Pattern pattern = Pattern.compile("^(\\s*selected:\\s*)\\d+(.*)$");

            for (int i = 0; i < lines.size(); i++) {
                Matcher matcher = pattern.matcher(lines.get(i));
                if (matcher.matches()) {
                    lines.set(i, matcher.group(1) + selected + matcher.group(2));
                    break;
                }
            }

            Files.write(configPath, lines, StandardCharsets.UTF_8);
            reloadConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadConfig() {
        try {
            Files.createDirectories(configPath.getParent());
            YamlConfigurationLoader loader = createLoader();
            if (!Files.exists(configPath)) {
                try (var in = getClass().getResourceAsStream("/config.yml")) {
                    if (in != null) {
                        Files.copy(in, configPath);
                    } else {
                        config = loader.createNode();
                        loader.save(config);
                    }
                }
                config = loader.load();
            } else {
                config = loader.load();
            }
        } catch (IOException e) {
            e.printStackTrace();
            config = null;
        }
    }
}