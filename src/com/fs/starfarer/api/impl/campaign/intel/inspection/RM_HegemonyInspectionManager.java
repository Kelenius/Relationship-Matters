package com.fs.starfarer.api.impl.campaign.intel.inspection;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.ids.Factions;

import data.scripts.RelationshipMattersPlugin;
import data.scripts.RelationshipMattersPlugin.RelScalingType;

public class RM_HegemonyInspectionManager extends HegemonyInspectionManager {
    public void copyOriginalData(HegemonyInspectionManager original) {
        threshold = original.getThreshold();
        numAttempts = original.getNumAttempts();
    }

    @Override
    protected void checkInspection() {
        float susBeforeSuper = suspicion;
        super.checkInspection();
        if (suspicion > susBeforeSuper) {
            float curRel = Global.getSector().getFaction(Factions.HEGEMONY).getRelToPlayer().getRel();
            float scale = RelationshipMattersPlugin.GetRelScaling(curRel, RelScalingType.AI_INSPECTION);
            if (scale <= 0) {
                suspicion = susBeforeSuper;
            }
            else if (scale < 1) {
                suspicion = susBeforeSuper + (suspicion - susBeforeSuper) * scale;
            }
        }
    }
}
