package com.qcadoo.mes.core.internal.view;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import com.qcadoo.mes.core.api.Entity;
import com.qcadoo.mes.core.api.TranslationService;
import com.qcadoo.mes.core.model.HookDefinition;
import com.qcadoo.mes.core.view.RootComponent;
import com.qcadoo.mes.core.view.ViewDefinition;
import com.qcadoo.mes.core.view.ViewValue;

public final class ViewDefinitionImpl implements ViewDefinition {

    private final RootComponent root;

    private final String pluginIdentifier;

    private final String name;

    private HookDefinition viewHook;

    public ViewDefinitionImpl(final String name, final RootComponent root, final String pluginIdentifier) {
        this.name = name;
        this.root = root;
        this.pluginIdentifier = pluginIdentifier;
    }

    @Override
    public String getPluginIdentifier() {
        return pluginIdentifier;
    }

    @Override
    public RootComponent getRoot() {
        return root;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setViewHook(final HookDefinition viewHook) {
        this.viewHook = viewHook;
    }

    @Override
    public ViewValue<Object> castValue(final Map<String, Entity> selectedEntities, final JSONObject viewObject)
            throws JSONException {
        return wrapIntoViewValue(root.castValue(selectedEntities, viewObject != null ? viewObject.getJSONObject(root.getName())
                : null));
    }

    @Override
    public void cleanSelectedEntities(final Map<String, Entity> selectedEntities, final Set<String> pathsToUpdate) {
        if (pathsToUpdate != null && selectedEntities != null) {
            for (String pathToUpdate : pathsToUpdate) {
                selectedEntities.remove(pathToUpdate);
            }
        }
    }

    @Override
    public ViewValue<Object> getValue(final Entity entity, final Map<String, Entity> selectedEntities,
            final ViewValue<Object> globalViewValue, final Set<String> pathsToUpdate, final String triggerComponentName) {
        ViewValue<Object> value = wrapIntoViewValue(root.getValue(entity, selectedEntities,
                globalViewValue != null ? globalViewValue.getComponent(root.getName()) : null, pathsToUpdate));
        callOnViewHook(value, triggerComponentName);
        return value;

    }

    private void callOnViewHook(final ViewValue<Object> value, final String triggerComponentName) {
        if (viewHook != null) {
            viewHook.callWithViewValue(value, triggerComponentName);
        }
    }

    @Override
    public void addComponentTranslations(final Map<String, String> translationsMap, final TranslationService translationService,
            final Locale locale) {
        root.updateTranslations(translationsMap, translationService, locale);
    }

    private ViewValue<Object> wrapIntoViewValue(final ViewValue<?> viewValue) {
        ViewValue<Object> value = new ViewValue<Object>();
        value.addComponent(root.getName(), viewValue);
        return value;
    }

}
