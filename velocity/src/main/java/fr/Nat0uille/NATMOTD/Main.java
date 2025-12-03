package fr.Nat0uille.NATMOTD;

import com.google.inject.Inject;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.proxy.ProxyServer;

import fr.Nat0uille.NATMOTD.Commands.MOTDCommand;

import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;
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
    private Path configPath;

    @Inject
    public Main(ProxyServer server) {
        this.server = server;
        this.configPath = Path.of("config.yml");
        loadConfig();
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        server.getCommandManager().register(
            server.getCommandManager().metaBuilder("nat-motd").aliases("motd").build(),
            new MOTDCommand(this)
        );
        server.getEventManager().register(this, new fr.Nat0uille.NATMOTD.Listeners.MOTDListener(this));
    }

    public ConfigurationNode getConfig() {
        return config;
    }

    public void reloadConfig() {
        loadConfig();
    }

    public void saveConfig() {
        try {
            YamlConfigurationLoader loader = YamlConfigurationLoader.builder().path(configPath).build();
            loader.save(config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadConfig() {
        try {
            YamlConfigurationLoader loader = YamlConfigurationLoader.builder().path(configPath).build();
            if (!Files.exists(configPath)) {
                config = loader.createNode();
                loader.save(config);
            } else {
                config = loader.load();
            }
        } catch (IOException e) {
            e.printStackTrace();
            config = null;
        }
    }
}