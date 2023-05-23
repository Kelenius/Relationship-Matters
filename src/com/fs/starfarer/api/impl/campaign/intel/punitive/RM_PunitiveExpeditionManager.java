package com.fs.starfarer.api.impl.campaign.intel.punitive;

import java.util.LinkedHashMap;
import java.util.List;

import com.fs.starfarer.api.campaign.FactionAPI;

import data.scripts.RelationshipMattersPlugin;

public class RM_PunitiveExpeditionManager extends PunitiveExpeditionManager {
    @Override
    public List<PunExReason> getExpeditionReasons(PunExData curr) {
        List<PunExReason> result = super.getExpeditionReasons(curr);

        float curRel = curr.faction.getRelToPlayer().getRel();
        for (PunExReason reason : result) {
            float scale = 1;
            if (reason.type == PunExType.ANTI_COMPETITION) {
                scale = 1 - (curRel - RelationshipMattersPlugin.antiCompetitionRelBase) * RelationshipMattersPlugin.antiCompetitionRelScaling / 100;
            }
            else if (reason.type == PunExType.ANTI_FREE_PORT) {
                scale = 1 - (curRel - RelationshipMattersPlugin.antiFreePortRelBase) * RelationshipMattersPlugin.antiFreePortRelScaling / 100;
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

        // For whatever reason these are not accessible
        // even though I should have access to it because this is a child of that class
        
        //this.timeout = original.timeout;
        //this.numSentSinceTimeout = original.numSentSinceTimeout;
    }
}
