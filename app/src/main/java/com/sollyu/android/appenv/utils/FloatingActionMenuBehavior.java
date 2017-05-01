package com.sollyu.android.appenv.utils;


import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.view.View;


/**
 * Created by Andrey Kalashnikov on 08.10.16.
 * KSystems
 * andrey.k@ksyste.ms
 */

public class FloatingActionMenuBehavior extends CoordinatorLayout.Behavior<com.github.clans.fab.FloatingActionMenu> {

    public FloatingActionMenuBehavior(Context context, AttributeSet attrs) {
        super();
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, com.github.clans.fab.FloatingActionMenu child, View dependency) {
        return dependency instanceof Snackbar.SnackbarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, com.github.clans.fab.FloatingActionMenu child, View dependency) {
        float translationY = Math.min(0, dependency.getTranslationY() - dependency.getHeight());
        child.setTranslationY(translationY);
        return true;
    }

    @Override
    public void onDependentViewRemoved(CoordinatorLayout parent, com.github.clans.fab.FloatingActionMenu child, View dependency) {
        float translationY = Math.max(0, dependency.getTranslationY() - dependency.getHeight());
        child.setTranslationY(translationY);
        super.onDependentViewRemoved(parent, child, dependency);
    }
}