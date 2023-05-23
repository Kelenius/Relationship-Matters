package data.scripts;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.intel.punitive.PunitiveExpeditionManager;
import com.fs.starfarer.api.impl.campaign.intel.punitive.RM_PunitiveExpeditionManager;

public class RelationshipMattersPlugin extends BaseModPlugin {
    private JSONObject settings;

    /* Settings */
    public static boolean enabled;
    public static float antiCompetitionRelBase;
    public static float antiCompetitionRelScaling;
    public static float antiFreePortRelBase;
    public static float antiFreePortRelScaling;

    @Override
    public void onGameLoad(boolean newGame) {
        try {
            LoadSettings();
        }
        catch (Exception ex) {
            Global.getLogger(this.getClass()).error("Failed to load settings", ex);
            return;
        }

        PunitiveExpeditionManager prev = PunitiveExpeditionManager.getInstance();
        if (prev != null) {
            if (prev.getClass() == PunitiveExpeditionManager.class && enabled) {
                Global.getLogger(this.getClass()).info(
                "Replacing PunitiveExpeditionManager with RM version, copying data.");
                RM_PunitiveExpeditionManager rmManager = new RM_PunitiveExpeditionManager();
                rmManager.copyOriginalData(prev);
                Global.getSector().removeScript(prev);
                Global.getSector().addScript(rmManager);
            }
            else if (prev.getClass() == RM_PunitiveExpeditionManager.class && !enabled) {
                Global.getLogger(this.getClass()).info(
                "Replacing RM_PunitiveExpeditionManager with original version.");
                Global.getSector().removeScript(prev);
                Global.getSector().addScript(new PunitiveExpeditionManager());
            }
        }
    }

    private void LoadSettings() throws IOException, JSONException {
        settings = Global.getSettings().getMergedJSONForMod("settings.json", "kel_relationship_matters");
        enabled = settings.getBoolean("enabled");
        antiCompetitionRelBase = (float)settings.getDouble("antiCompetitionRelBase");
        antiCompetitionRelScaling = (float)settings.getDouble("antiCompetitionRelScaling");
        antiFreePortRelBase = (float)settings.getDouble("antiFreePortRelBase");
        antiFreePortRelScaling = (float)settings.getDouble("antiFreePortRelScaling");
    }
}
