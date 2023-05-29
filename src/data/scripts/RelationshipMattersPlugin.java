package data.scripts;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.intel.inspection.HegemonyInspectionManager;
import com.fs.starfarer.api.impl.campaign.intel.inspection.RM_HegemonyInspectionManager;
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
    public static float inspectionRelBase;
    public static float inspectionRelScaling;

    @Override
    public void onGameLoad(boolean newGame) {
        try {
            LoadSettings();
        }
        catch (Exception ex) {
            Global.getLogger(this.getClass()).error("Failed to load settings.", ex);
            return;
        }

        PunitiveExpeditionManager curPunitive = PunitiveExpeditionManager.getInstance();
        if (curPunitive != null) {
            if (curPunitive.getClass() == PunitiveExpeditionManager.class && enabled) {
                Global.getLogger(this.getClass()).info(
                "Replacing PunitiveExpeditionManager with RM version, copying data.");
                RM_PunitiveExpeditionManager rmManager = new RM_PunitiveExpeditionManager();
                rmManager.copyOriginalData(curPunitive);
                Global.getSector().removeScript(curPunitive);
                Global.getSector().addScript(rmManager);
            }
            else if (curPunitive.getClass() == RM_PunitiveExpeditionManager.class && !enabled) {
                Global.getLogger(this.getClass()).info(
                "Replacing RM_PunitiveExpeditionManager with original version.");
                Global.getSector().removeScript(curPunitive);
                Global.getSector().addScript(new PunitiveExpeditionManager());
            }
        }

        HegemonyInspectionManager curInspection = HegemonyInspectionManager.getInstance();
        if (curInspection != null) {
            if (curInspection.getClass() == HegemonyInspectionManager.class && enabled) {
                Global.getLogger(this.getClass()).info(
                "Replacing HegemonyInspectionManager with RM version, copying data.");
                RM_HegemonyInspectionManager rmManager = new RM_HegemonyInspectionManager();
                rmManager.copyOriginalData(curInspection);
                Global.getSector().removeScript(curInspection);
                Global.getSector().addScript(rmManager);
            }
            else if (curInspection.getClass() == RM_HegemonyInspectionManager.class && !enabled) {
                Global.getLogger(this.getClass()).info(
                "Replacing RM_HegemonyInspectionManager with original version.");
                Global.getSector().removeScript(curInspection);
                Global.getSector().addScript(new HegemonyInspectionManager());
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
        inspectionRelBase = (float)settings.getDouble("inspectionRelBase");
        inspectionRelScaling = (float)settings.getDouble("inspectionRelScaling");
    }

    public enum RelScalingType {
        ANTI_COMPETITION,
        ANTI_FREE_PORT,
        AI_INSPECTION
    }

    public static float GetRelScaling(float rel, RelScalingType type) {
        float base; float scale;
        switch(type) {
            case ANTI_COMPETITION:
                base = antiCompetitionRelBase;
                scale = antiCompetitionRelScaling;
                break;
            case ANTI_FREE_PORT:
                base = antiFreePortRelBase;
                scale = antiFreePortRelScaling;
                break;
            case AI_INSPECTION:
                base = inspectionRelBase;
                scale = inspectionRelScaling;
                break;
            default:
                return 1;
        }
        float ret = 1 - (rel - base / 100) * scale;
        if (ret <= 0) return 0;
        else if (ret < 1) return ret;
        return 1;
    }
}
