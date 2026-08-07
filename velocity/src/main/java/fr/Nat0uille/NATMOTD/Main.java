package fr.Nat0uille.NATMOTD;

import com.google.inject.Inject;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.util.Favicon;
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
    private final Path dataDirectory;
    private Favicon serverFavicon;
    private CheckVersion checkVersion;

    @Inject
    public Main(ProxyServer server, @DataDirectory Path dataDirectory) {
        this.server = server;
        try {
            Files.createDirectories(dataDirectory);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.dataDirectory = dataDirectory;
        this.configPath = dataDirectory.resolve("config.yml");
        loadConfig();
        reloadServerIcon();
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
                .append(Component.text(getVersion(), NamedTextColor.GREEN))
        );
        server.getConsoleCommandSource().sendMessage(Component.empty());

        server.getCommandManager().register(
            server.getCommandManager().metaBuilder("nat-motd").aliases("motd").build(),
            new MOTDCommand(this)
        );
        server.getEventManager().register(this, new MOTDListener(this));

        checkVersion = new CheckVersion();
        CheckVersion.startVersionCheck(this, checkVersion);
    }

    public ProxyServer getServer() {
        return server;
    }

    public Path getDataDirectory() {
        return dataDirectory;
    }

    public CheckVersion getCheckVersion() {
        return checkVersion;
    }

    public ConfigurationNode getConfig() {
        return config;
    }

    public void reloadConfig() {
        loadConfig();
        reloadServerIcon();
    }

    public void reloadServerIcon() {
        serverFavicon = null;

        if (config == null) {
            return;
        }

        int selected = config.node("motd", "selected").getInt(1);
        String iconPath = config.node("motd", selected, "icon").getString("");

        if (iconPath == null || iconPath.isBlank()) {
            return;
        }

        Path iconFile = dataDirectory.resolve(iconPath);
        if (!Files.exists(iconFile)) {
            server.getConsoleCommandSource().sendMessage(
                    Component.text("MOTD icon file not found: " + iconFile, NamedTextColor.YELLOW));
            return;
        }

        try {
            serverFavicon = Favicon.create(iconFile);
        } catch (IOException e) {
            server.getConsoleCommandSource().sendMessage(
                    Component.text("Failed to load MOTD icon '" + iconPath + "': " + e.getMessage(), NamedTextColor.YELLOW));
        }
    }

    public Favicon getServerFavicon() {
        return serverFavicon;
    }

    public String getVersion() {
        return server.getPluginManager().fromInstance(this)
                .flatMap(container -> container.getDescription().getVersion())
                .orElse("unknown");
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