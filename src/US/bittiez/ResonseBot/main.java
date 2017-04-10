package US.bittiez.ResonseBot;

import US.bittiez.ResonseBot.UpdateChecker.UpdateChecker;
import US.bittiez.ResonseBot.UpdateChecker.UpdateStatus;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class main extends JavaPlugin implements Listener {
    private static Logger log;
    public FileConfiguration config = getConfig();
    public boolean hasUpdate = false;
    private Logger messageLogger = Logger.getLogger("interactions");
    private FileHandler interactionFile;

    @Override
    public void onEnable() {
        log = getLogger();
        try {
            DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd");
            Date date = new Date();
            File logFile = new File(getDataFolder() + File.separator + "logs" + File.separator);
            if (!logFile.exists())
                logFile.mkdirs();
            interactionFile = new FileHandler(logFile.getAbsolutePath() + File.separator + dateFormat.format(date) + "-interactions.log");
            messageLogger.addHandler(interactionFile);
            SimpleFormatter formatter = new SimpleFormatter();
            interactionFile.setFormatter(formatter);
            messageLogger.setUseParentHandlers(false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        createConfig();
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(this, this);

        UpdateStatus update = new UpdateChecker("https://github.com/bittiez/ResponseBot/raw/master/src/plugin.yml", getDescription().getVersion()).getStatus();
        if (update.HasUpdate) {
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
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (hasUpdate) {
            if (e.getPlayer().isOp() || e.getPlayer().hasPermission("ResponseBot.updates")) {
                e.getPlayer().sendMessage(ChatColor.GOLD + "Your version of ResponseBot is outdated, you can check for updates at: https://github.com/bittiez/ResponseBot/releases or https://www.spigotmc.org/resources/responsebot.37901/");
            }
        }
        if (config.getBoolean("on_join.enable")) {
            String msg = ChatColor.translateAlternateColorCodes('&', config.getString("on_join.msg")
                    .replace("[PLAYER]", e.getPlayer().getName())
                    .replace("[BOTNAME]", config.getString("botname"))
                    .replace("[PREFIX]", config.getString("requiredPrefix")));
            if (config.getBoolean("on_join.send_to_server")) {
                for (Player p : getServer().getOnlinePlayers()) {
                    p.sendMessage(msg);
                }
            } else {
                e.getPlayer().sendMessage(msg);
            }
        }
    }

    @EventHandler
    public void onPlayerChatEvent(AsyncPlayerChatEvent e) {
        if (!e.isCancelled()) {
            if (!e.getMessage().isEmpty()) {
                if (e.getMessage().startsWith(config.getString("requiredPrefix", "")) && e.getMessage().length() > config.getString("requiredPrefix", "").length()) {
                    checkAPI API = new checkAPI();
                    API.player = e.getPlayer();
                    API.message = e.getMessage().substring(config.getString("requiredPrefix", "").length());
                    API.plugin = this;
                    API.config = config;
                    API.messageLogger = messageLogger;
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
