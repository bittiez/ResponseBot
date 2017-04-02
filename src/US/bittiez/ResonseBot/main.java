package US.bittiez.ResonseBot;

import US.bittiez.ResonseBot.UpdateChecker.UpdateChecker;
import US.bittiez.ResonseBot.UpdateChecker.UpdateStatus;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;


public class main extends JavaPlugin implements Listener{
    private static Logger log;
    public FileConfiguration config = getConfig();
    public boolean hasUpdate = false;

    @Override
    public void onEnable() {
        log = getLogger();
        createConfig();
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(this, this);

        UpdateStatus update = new UpdateChecker("https://github.com/bittiez/ResponseBot/raw/master/src/plugin.yml", getDescription().getVersion()).getStatus();
        if(update.HasUpdate){
            hasUpdate = true;
            log.warning("ResponseBot is outdated, you can check out the update at: https://github.com/bittiez/ResponseBot/releases or https://www.spigotmc.org/resources/responsebot.37901/");
        }
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
    public void onPlayerJoin(PlayerJoinEvent e){
        if(hasUpdate){
            if(e.getPlayer().isOp() || e.getPlayer().hasPermission("ResponseBot.updates")){
                e.getPlayer().sendMessage(ChatColor.GOLD + "Your version of ResponseBot is outdated, you can check for updates at: https://github.com/bittiez/ResponseBot/releases or https://www.spigotmc.org/resources/responsebot.37901/");
            }
        }
    }
    @EventHandler
    public void onPlayerChatEvent(AsyncPlayerChatEvent e){
        if(!e.isCancelled()) {
            if(!e.getMessage().isEmpty()) {
                if(e.getMessage().startsWith(config.getString("requiredPrefix", "")) && e.getMessage().length() > config.getString("requiredPrefix", "").length()) {
                    checkAPI API = new checkAPI();
                    API.player = e.getPlayer();
                    API.message = e.getMessage().substring(config.getString("requiredPrefix", "").length());
                    API.plugin = this;
                    API.config = config;
                    API.log = log;

                    new Thread(API).start();
                }
            }
        }
    }

    private void createConfig() {
        config.options().copyDefaults();
        saveDefaultConfig();
    }
}
