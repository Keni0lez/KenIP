package org.keni.kenip;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class KeniP extends JavaPlugin implements Listener {
    private Map<String, String> playerIPs = new HashMap<>();
    private File dataFile;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        dataFile = new File(getDataFolder(), "player_ips.yml");
        loadPlayerIPs();
    }

    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        String playerName = event.getName();
        String playerIP = event.getAddress().getHostAddress();

        if (playerIPs.containsKey(playerName)) {
            String storedIP = playerIPs.get(playerName);
            if (!playerIP.equals(storedIP)) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "Другой IP-адрес уже связан с этим именем пользователя.");
            }
        } else {
            playerIPs.put(playerName, playerIP);
            savePlayerIPs();
        }
    }

    private void loadPlayerIPs() {
        if (dataFile.exists()) {
            try {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(dataFile);
                if (config.isConfigurationSection("playerIPs")) {
                    for (String playerName : config.getConfigurationSection("playerIPs").getKeys(false)) {
                        String ip = config.getString("playerIPs." + playerName);
                        playerIPs.put(playerName, ip);
                    }
                }
            } catch (Exception e) {
                getLogger().log(Level.SEVERE, "Не удалось загрузить IP-адреса игроков из файла.", e);
            }
        }
    }

    private void savePlayerIPs() {
        try {
            YamlConfiguration config = new YamlConfiguration();
            config.createSection("playerIPs", playerIPs);
            config.save(dataFile);
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Не удалось сохранить IP-адреса игроков в файл.", e);
        }
    }
}

