package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.customviews.CheckBox;
import com.vijay.jsonwizard.expressions.JsonExpressionResolver;
import com.vijay.jsonwizard.i18n.JsonFormBundle;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.vijay.jsonwizard.utils.FormUtils.*;

/**
 * Created by vijay on 24-05-2015.
 */
public class CheckBoxFactory implements FormWidgetFactory {

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JSONObject jsonObject, CommonListener listener, JsonFormBundle bundle,JsonExpressionResolver resolver, int visualizationMode) throws JSONException {
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
        views.add(getTextViewWith(context, 16, bundle.resolveKey(jsonObject.getString("label")), jsonObject.getString("key"),
                jsonObject.getString("type"), getLayoutParams(MATCH_PARENT, WRAP_CONTENT, 0, 0, 0, 0),
                FONT_BOLD_PATH));
        JSONArray options = jsonObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
        for (int i = 0; i < options.length(); i++) {
            JSONObject item = options.getJSONObject(i);
            CheckBox checkBox = (CheckBox) LayoutInflater.from(context).inflate(R.layout.item_checkbox, null);
            checkBox.setText(bundle.resolveKey(item.getString("text")));
            checkBox.setTag(R.id.key, jsonObject.getString("key"));
            checkBox.setTag(R.id.type, jsonObject.getString("type"));
            checkBox.setTag(R.id.childKey, item.getString("key"));
            checkBox.setGravity(Gravity.CENTER_VERTICAL);
            checkBox.setTextSize(16);
            checkBox.setTypeface(Typeface.createFromAsset(context.getAssets(), FONT_REGULAR_PATH));
            checkBox.setOnCheckedChangeListener(listener);
            if (!TextUtils.isEmpty(item.optString("value"))) {
                checkBox.setChecked(item.optBoolean("value"));
            }
            if (i == options.length() - 1) {
                checkBox.setLayoutParams(getLayoutParams(MATCH_PARENT, WRAP_CONTENT, 0, 0, 0, (int) context
                        .getResources().getDimension(R.dimen.extra_bottom_margin)));
            }
            views.add(checkBox);
        }
        return views;
    }

    private List<View> getReadOnlyViewsFromJson(Context context, JSONObject jsonObject, JsonFormBundle bundle) throws JSONException {
        List<View> views = new ArrayList<>(1);
        views.add(getTextViewWith(context, 16, bundle.resolveKey(jsonObject.getString("label")), jsonObject.getString("key"),
                jsonObject.getString("type"), getLayoutParams(MATCH_PARENT, WRAP_CONTENT, 0, 0, 0, (int) context
                        .getResources().getDimension(R.dimen.extra_bottom_margin)),
                FONT_BOLD_PATH));
        JSONArray options = jsonObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
        for (int i = 0; i < options.length(); i++) {
            JSONObject item = options.getJSONObject(i);
            if (!TextUtils.isEmpty(item.optString("value")) && item.optBoolean("value")) {
                views.add(getTextViewWith(context, 16, bundle.resolveKey(item.getString("text")), item.getString("key"),
                        null, getLayoutParams(MATCH_PARENT, WRAP_CONTENT, 0, 0, 0, (int) context
                                .getResources().getDimension(R.dimen.default_bottom_margin)), FONT_REGULAR_PATH));
            }
        }
        return views;
    }
}
