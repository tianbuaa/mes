package com.qcadoo.mes.productionCounting.newstates;

import com.qcadoo.mes.newstates.BasicStateService;
import com.qcadoo.mes.productionCounting.states.constants.ProductionTrackingStateChangeDescriber;
import com.qcadoo.mes.productionCounting.states.constants.ProductionTrackingStateStringValues;
import com.qcadoo.mes.productionCounting.states.listener.ProductionTrackingListenerService;
import com.qcadoo.mes.states.StateChangeEntityDescriber;
import com.qcadoo.model.api.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductionTrackingStateService extends BasicStateService implements ProductionTrackingStateServiceMarker {

    @Autowired
    private ProductionTrackingStateChangeDescriber productionTrackingStateChangeDescriber;

    @Autowired
    private ProductionTrackingListenerService productionTrackingListenerService;

    @Override
    public StateChangeEntityDescriber getChangeEntityDescriber() {
        return productionTrackingStateChangeDescriber;
    }

    @Override
    public Entity onValidate(Entity entity, String sourceState, String targetState) {

        switch (targetState) {
            case ProductionTrackingStateStringValues.ACCEPTED:
                productionTrackingListenerService.validationOnAccept(entity);
                break;
        }

        return entity;
    }

    @Override
    public Entity onBeforeSave(Entity entity, String sourceState, String targetState) {
        if (ProductionTrackingStateStringValues.DRAFT.equals(sourceState)) {
            productionTrackingListenerService.onLeavingDraft(entity);
        }
        
        return entity;
    }

    @Override
    public Entity onAfterSave(Entity entity, String sourceState, String targetState) {

        switch (targetState) {
            case ProductionTrackingStateStringValues.ACCEPTED:
                productionTrackingListenerService.onAccept(entity);
                break;

            case ProductionTrackingStateStringValues.DECLINED:
                if (ProductionTrackingStateStringValues.ACCEPTED.equals(sourceState)) {
                    productionTrackingListenerService.onChangeFromAcceptedToDeclined(entity);
                }
                break;
        }

        return entity;
    }
}
