package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.util.ViewUtil;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.i18n.JsonFormBundle;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.utils.ValidationStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.ganfra.materialspinner.MaterialSpinner;

/**
 * Created by nipun on 30/05/15.
 */
public class SpinnerFactory implements FormWidgetFactory {

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JSONObject jsonObject, CommonListener listener, JsonFormBundle bundle, int visualizationMode) throws JSONException {
        List<View> views = null;
        switch (visualizationMode){
            case JsonFormConstants.VISUALIZATION_MODE_READ_ONLY :
                views = getReadOnlyViewsFromJson(context, jsonObject, bundle);
                break;
            default:
                views = getEditableViewsFromJson(context, jsonObject, listener, bundle);
        }
        return views;
    }

    private List<View> getEditableViewsFromJson(Context context, JSONObject jsonObject, CommonListener listener, JsonFormBundle bundle) throws JSONException {
        List<View> views = new ArrayList<>(1);
        MaterialSpinner spinner = (MaterialSpinner) LayoutInflater.from(context).inflate(R.layout.item_spinner, null);

        final String hint = bundle.resolveKey(jsonObject.optString("hint"));
        if (!TextUtils.isEmpty(hint)) {
            spinner.setHint(hint);
            spinner.setFloatingLabelText(hint);
        }

        spinner.setId(ViewUtil.generateViewId());

        spinner.setTag(R.id.key, jsonObject.getString("key"));
        spinner.setTag(R.id.type, jsonObject.getString("type"));

        JSONObject requiredObject = jsonObject.optJSONObject("v_required");
        if (requiredObject != null) {
            String requiredValue = requiredObject.getString("value");
            if (!TextUtils.isEmpty(requiredValue)) {
                spinner.setTag(R.id.v_required, requiredValue);
                spinner.setTag(R.id.error, bundle.resolveKey(requiredObject.optString("err")));
            }
        }

        String valueToSelect = "";
        int indexToSelect = -1;
        final String value = jsonObject.optString("value");
        if (!TextUtils.isEmpty(value)) {
            valueToSelect = value;
        }

        JSONArray valuesJson = jsonObject.optJSONArray("values");
        final int valuesJsonLength = valuesJson.length();
        String[] values = null;
        if (valuesJson != null && valuesJsonLength > 0) {
            values = new String[valuesJsonLength];
            for (int i = 0; i < valuesJsonLength; i++) {
                values[i] = valuesJson.optString(i);
                if (valueToSelect.equals(values[i])) {
                    indexToSelect = i;
                    break;
                }
            }
        }

        if (values != null) {
            spinner.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, values));
            spinner.setSelection(indexToSelect + 1, true);
            spinner.setOnItemSelectedListener(listener);
        }
        views.add(spinner);
        return views;
    }

    private List<View> getReadOnlyViewsFromJson(Context context, JSONObject jsonObject, JsonFormBundle bundle) throws JSONException {
        List<View> views = new ArrayList<>(1);
        MaterialEditText editText = (MaterialEditText) LayoutInflater.from(context).inflate(
                R.layout.item_edit_text, null);
        editText.setId(ViewUtil.generateViewId());
        final String hint = jsonObject.getString("hint");
        editText.setHint(hint);
        editText.setFloatingLabelText(hint);
        editText.setTag(R.id.key, jsonObject.getString("key"));
        editText.setTag(R.id.type, jsonObject.getString("type"));

        editText.setText(jsonObject.optString("value"));
        editText.setEnabled(false);
        views.add(editText);
        return views;
    }

    public static ValidationStatus validate(MaterialSpinner spinner) {
        if (!(spinner.getTag(R.id.v_required) instanceof String) || !(spinner.getTag(R.id.error) instanceof String)) {
            return new ValidationStatus(true, null);
        }
        Boolean isRequired = Boolean.valueOf((String) spinner.getTag(R.id.v_required));
        if (!isRequired) {
            return new ValidationStatus(true, null);
        }
        int selectedItemPosition = spinner.getSelectedItemPosition();
        if(selectedItemPosition > 0) {
            return new ValidationStatus(true, null);
        }
        return new ValidationStatus(false, (String) spinner.getTag(R.id.error));
    }
}
