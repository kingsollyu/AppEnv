package com.sollyu.android.appenv.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.sollyu.android.appenv.R;
import com.sollyu.android.appenv.helper.OtherHelper;

import java.lang.reflect.Method;


/**
 * 作者: Sollyu
 * 时间: 16/10/24
 * 联系: sollyu@qq.com
 * 说明:
 */
public class DetailItem extends LinearLayout {

    private AwesomeTextView iconAwesomeTextView   = null;
    private AwesomeTextView buttonAwesomeTextView = null;
    private EditText        editText              = null;

    private String onClickMethodName = null;

    private static final String ANDROID_NAME_SPACE = "http://schemas.android.com/apk/res/android";
    private static final String APP_NAME_SPACE     = "http://schemas.android.com/apk/res-auto";

    public DetailItem(Context context) {
        super(context);
    }

    public DetailItem(final Context context, AttributeSet attrs) {
        super(context, attrs);

        this.setOrientation(LinearLayout.HORIZONTAL);

        iconAwesomeTextView = new AwesomeTextView(context);
        editText = new EditText(context);
        buttonAwesomeTextView = new AwesomeTextView(context);

        int padding = dip2px(context, 10);
        iconAwesomeTextView.setPadding(padding, padding, padding, padding);

        LinearLayout.LayoutParams editLayoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        editLayoutParams.weight = 1;

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.detail_item);

        if (typedArray.getText(R.styleable.detail_item_icon_text) != null) {
            iconAwesomeTextView.setMarkdownText(typedArray.getText(R.styleable.detail_item_icon_text).toString());
        }

        if (typedArray.getText(R.styleable.detail_item_icon_button) != null) {
            buttonAwesomeTextView.setMarkdownText(typedArray.getText(R.styleable.detail_item_icon_button).toString());
        }

        if (typedArray.getText(R.styleable.detail_item_hint) != null) {
            editText.setHint(typedArray.getText(R.styleable.detail_item_hint).toString());
        }

        onClickMethodName = attrs.getAttributeValue(ANDROID_NAME_SPACE, "onClick");
        buttonAwesomeTextView.setTextSize(OtherHelper.getInstance().sp2px(this.getContext(), 10));
        buttonAwesomeTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Method method = context.getClass().getMethod(onClickMethodName, View.class);
                    method.invoke(context, DetailItem.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        typedArray.recycle();

        this.addView(iconAwesomeTextView);
        this.addView(editText, editLayoutParams);
        this.addView(buttonAwesomeTextView);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public AwesomeTextView getButton() {
        return buttonAwesomeTextView;
    }
    public AwesomeTextView getIcon() {
        return iconAwesomeTextView;
    }

    public EditText getEditText() {
        return editText;
    }


}
