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
import org.json.simple.JSONArray;
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
        String response = checkAPI(ChatColor.stripColor(e.getMessage()), e.getPlayer());
        if(response.length() > 0) {
            if (config.getBoolean("replyToPlayer", false)) {
                e.getPlayer().sendMessage(genResponse(response));
            } else {
                getServer().broadcastMessage(genResponse(response));
            }
        }
    }

    private String genResponse(String response){
        return ChatColor.translateAlternateColorCodes('&',
                config.getString("chatFormat")
                    .replace("[BOTNAME]", config.getString("botname"))
                    .replace("[RESPONSE]", response));
    }

    private String checkAPI(String message, Player player){
        String API_BASE = "https://api.api.ai/api";
        try {
            Request res = Request.Get(API_BASE
                    + "/query"
                    + "?v=20150910"
                    + "&query="
                        + URLEncoder.encode(message, "UTF-8")
                    + "&lang=" + config.getString("lang", "en")
                    + "&sessionId=" + player.getUniqueId()
            )
                .addHeader("Authorization", "Bearer " + config.getString("accesstoken"));
            log.log(Level.INFO, res.toString());

            String json = res.execute().returnContent().asString();
            JSONParser parser = new JSONParser();
            JSONObject parsed = (JSONObject)parser.parse(json);
            JSONObject result = (JSONObject)parsed.get("result");
            JSONObject fulfillment = (JSONObject)result.get("fulfillment");

            return fulfillment.get("speech").toString();

        } catch (Exception err) {
            log.log(Level.WARNING, "ResponseBot encountered an unknown error.");
            err.printStackTrace();
        }
        return "";
    }

    private void createConfig() {
        config.options().copyDefaults();
        saveDefaultConfig();
    }
}
