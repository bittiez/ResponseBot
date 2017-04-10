package US.bittiez.ResonseBot.UpdateChecker;

import org.apache.commons.io.IOUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.net.URI;

public class UpdateChecker {
    public String remotePluginYml;
    public String thisPluginVersion;

    /**
     * @param remotePluginYml   remote url to direct link to updated plugin.yml, like github raw version(github.com/you/repo/raw/branch/plugin.yml)
     * @param thisPluginVersion The current plugin version, Plugin.getDescription.getVersion()
     */
    public UpdateChecker(String remotePluginYml, String thisPluginVersion) {
        this.remotePluginYml = remotePluginYml;
        this.thisPluginVersion = thisPluginVersion;
    }

    /**
     * @return Will return false if version is different, or if an error occurs.
     */
    public UpdateStatus getStatus() {
        try {
            FileConfiguration updated = new YamlConfiguration();
            updated.loadFromString(IOUtils.toString(URI.create(remotePluginYml)));
            String updatedVersion = updated.getString("version");
            if (!thisPluginVersion.equals(updatedVersion))
                return new UpdateStatus(updatedVersion, thisPluginVersion, true, false);
            else
                return new UpdateStatus("[ERROR]", thisPluginVersion, false, false);
        } catch (Exception e) {
            e.printStackTrace();
            return new UpdateStatus("[ERROR]", thisPluginVersion, true, true);
        }
    }
}
