package US.bittiez.ResonseBot;

import org.apache.http.client.fluent.Request;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

;

public class main extends JavaPlugin implements Listener{
    private static Logger log;
    public FileConfiguration config = getConfig();

    @Override
    public void onEnable() {
        log = getLogger();
        createConfig();
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(this, this);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String args[]) {
        if (cmd.getName().equalsIgnoreCase("rb") && args[0].equalsIgnoreCase("reload")) {
            if (sender.hasPermission("ResponseBot.reload")) {
                this.reloadConfig();
                config = getConfig();
                sender.sendMessage("Config reloaded!");
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void onPlayerChatEvent(AsyncPlayerChatEvent e){
        checkAPI API = new checkAPI();
        API.player = e.getPlayer();
        API.message = e.getMessage();
        API.plugin = this;
        API.config = config;
        API.log = log;

        new Thread(API).start();
    }

    private void createConfig() {
        config.options().copyDefaults();
        saveDefaultConfig();
    }
}
