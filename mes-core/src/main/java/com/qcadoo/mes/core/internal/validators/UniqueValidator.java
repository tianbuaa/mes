package com.qcadoo.mes.core.internal.validators;

import com.qcadoo.mes.core.api.Entity;
import com.qcadoo.mes.core.model.DataDefinition;
import com.qcadoo.mes.core.model.FieldDefinition;
import com.qcadoo.mes.core.search.RestrictionOperator;
import com.qcadoo.mes.core.search.Restrictions;
import com.qcadoo.mes.core.search.SearchCriteriaBuilder;
import com.qcadoo.mes.core.search.SearchResult;
import com.qcadoo.mes.core.validation.FieldValidator;

public final class UniqueValidator implements FieldValidator {

    private static final String UNIQUE_ERROR = "commons.validate.field.error.duplicated";

    private String errorMessage = UNIQUE_ERROR;

    @Override
    public boolean validate(final DataDefinition dataDefinition, final FieldDefinition fieldDefinition, final Object value,
            final Entity validatedEntity) {
        return true;
    }

    @Override
    public boolean validate(final DataDefinition dataDefinition, final FieldDefinition fieldDefinition, final Entity entity) {
        SearchCriteriaBuilder searchCriteriaBuilder = dataDefinition.find()
                .restrictedWith(Restrictions.eq(fieldDefinition, entity.getField(fieldDefinition.getName()))).withMaxResults(1);
        if (entity.getId() != null) {
            searchCriteriaBuilder.restrictedWith(Restrictions.idRestriction(entity.getId(), RestrictionOperator.NE));
        }
        SearchResult results = searchCriteriaBuilder.list();
        if (results.getTotalNumberOfEntities() == 0) {
            return true;
        } else {
            entity.addError(fieldDefinition, errorMessage);
            return false;
        }
    }

    @Override
    public FieldValidator customErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

}
