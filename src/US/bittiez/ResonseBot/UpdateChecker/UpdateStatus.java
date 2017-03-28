package US.bittiez.ResonseBot.UpdateChecker;

public class UpdateStatus {
    public String RemoteVersion;
    public String LocalVersion;
    public boolean HasUpdate;
    public boolean HasError;

    public UpdateStatus(String RemoteVersion, String LocalVersion, Boolean HasUpdate, Boolean HasError){
        this.RemoteVersion = RemoteVersion;
        this.LocalVersion = LocalVersion;
        this.HasUpdate = HasUpdate;
        this.HasError = HasError;
    }
}
