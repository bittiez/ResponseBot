package US.bittiez.ResonseBot;

import org.apache.http.client.fluent.Request;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by gamer on 3/11/2017.
 */
public class checkAPI implements Runnable{
    public String message;
    public Player player;
    public FileConfiguration config;
    public static Logger log;
    public Plugin plugin;

    @Override
    public void run() {
        this.message = ChatColor.stripColor(this.message);

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

            String response = fulfillment.get("speech").toString();
            if(response.length() > 0) {
                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    public void run() {
                        if (config.getBoolean("replyToPlayer", false)) {
                            player.sendMessage(genResponse(response));
                        } else {
                            plugin.getServer().broadcastMessage(genResponse(response));
                        }
                    }
                }, 20);
            }

        } catch (Exception err) {
            log.log(Level.WARNING, "ResponseBot encountered an unknown error.");
            err.printStackTrace();
        }
    }

    private String genResponse(String response){
        return ChatColor.translateAlternateColorCodes('&',
                config.getString("chatFormat")
                        .replace("[BOTNAME]", config.getString("botname"))
                        .replace("[RESPONSE]", response));
    }

}
