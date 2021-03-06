package US.bittiez.ResonseBot;

import ai.api.AIConfiguration;
import ai.api.AIDataService;
import ai.api.AIServiceException;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;

public class checkAPI implements Runnable {
    public static Logger log;
    public static Logger messageLogger;
    public String message;
    public Player player;
    public FileConfiguration config;
    public Plugin plugin;

    @Override
    public void run() {
        AIConfiguration aiConfiguration = new AIConfiguration(config.getString("accesstoken"), AIConfiguration.SupportedLanguages.fromLanguageTag(config.getString("lang", "en")));
        AIDataService dataService = new AIDataService(aiConfiguration);
        AIRequest request = new AIRequest(ChatColor.stripColor(this.message));
        request.setSessionId(player.getUniqueId().toString());
        try {
            AIResponse response = dataService.request(request);
            if (response.getStatus().getCode() == 200) {
                String res = response.getResult().getFulfillment().getSpeech();
                if (res.length() > 0) {
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                        public void run() {
                            if (config.getBoolean("replyToPlayer", false)) {
                                player.sendMessage(genResponse(res));
                            } else {
                                plugin.getServer().broadcastMessage(genResponse(res));
                            }
                        }
                    }, 20);
                    if (messageLogger != null && config.getBoolean("log_response", false)) {
                        messageLogger.info(player.getName() + ": " + ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', this.message)));
                        messageLogger.info(ChatColor.stripColor(genResponse(res)));
                    }
                }
            } else {
                plugin.getLogger().info("There was an error with ResponseBot");
                plugin.getLogger().info(response.getStatus().getErrorDetails());
            }


        } catch (AIServiceException e) {
            plugin.getLogger().warning("We ran into an error trying to get a response, please check that your client access token is correctly configured!");
        }
    }

    private String genResponse(String response) {
        return ChatColor.translateAlternateColorCodes('&',
                config.getString("chatFormat")
                        .replace("[BOTNAME]", config.getString("botname"))
                        .replace("[RESPONSE]", response)
                        .replace("[DEFAULT]", config.getString("defaultColor", "&4"))
                        .replace("[PLAYER]", this.player.getName())
        );
    }

}
