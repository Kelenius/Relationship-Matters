package com.fs.starfarer.api.impl.campaign.intel.punitive;

import java.util.LinkedHashMap;
import java.util.List;

import com.fs.starfarer.api.campaign.FactionAPI;

import data.scripts.RelationshipMattersPlugin;
import data.scripts.RelationshipMattersPlugin.RelScalingType;

public class RM_PunitiveExpeditionManager extends PunitiveExpeditionManager {
    @Override
    public List<PunExReason> getExpeditionReasons(PunExData curr) {
        List<PunExReason> result = super.getExpeditionReasons(curr);

        float curRel = curr.faction.getRelToPlayer().getRel();
        for (PunExReason reason : result) {
            float scale = 1;
            if (reason.type == PunExType.ANTI_COMPETITION) {
                scale = RelationshipMattersPlugin.GetRelScaling(curRel, RelScalingType.ANTI_COMPETITION);
            }
            else if (reason.type == PunExType.ANTI_FREE_PORT) {
                scale = RelationshipMattersPlugin.GetRelScaling(curRel, RelScalingType.ANTI_FREE_PORT);
            }

            if (scale <= 0) {
                reason.weight = 0;
            }
            else if (scale < 1) {
                reason.weight *= scale;
            }
        }

        return result;
    }

    public void copyOriginalData(PunitiveExpeditionManager original) {
        this.data = new LinkedHashMap<FactionAPI, PunExData>(original.getData());
    }
}
