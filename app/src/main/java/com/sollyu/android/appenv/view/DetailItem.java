package com.sollyu.android.appenv.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.sollyu.android.appenv.R;
import com.sollyu.android.appenv.helper.OtherHelper;
import com.sollyu.android.logg.Logg;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.lang.reflect.Method;


/**
 * 作者: Sollyu
 * 时间: 16/10/24
 * 联系: sollyu@qq.com
 * 说明:
 */
public class DetailItem extends LinearLayout {

    @ViewInject(R.id.tvIcon)     AwesomeTextView   iconAwesomeTextView   = null;
    @ViewInject(R.id.tvButton)  AwesomeTextView   buttonAwesomeTextView = null;
    @ViewInject(R.id.etContent)   AppCompatEditText editText              = null;

    private String onClickMethodName = null;

    private static final String ANDROID_NAME_SPACE = "http://schemas.android.com/apk/res/android";

    public DetailItem(Context context) {
        super(context);
    }

    public DetailItem(final Context context, AttributeSet attrs) {
        super(context, attrs);

        View rootView = inflate(context, R.layout.item_detail, this);
        x.view().inject(rootView);

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
        buttonAwesomeTextView.setOnClickListener(v -> {
            try {
                Method method = context.getClass().getMethod(onClickMethodName, View.class);
                method.invoke(context, DetailItem.this);
            } catch (Exception e) {
                Logg.L.error(e.getMessage());
            }
        });

        typedArray.recycle();
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
