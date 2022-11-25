package com.geostat.census_2024.ui.feature;

import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.lang.ref.WeakReference;

public class AlertFeature {

    private static volatile AlertFeature INSTANCE;

    private String title;
    private String text;
    private final WeakReference<AppCompatActivity> refer;

    int neutral;
    private CharSequence textNeutral;
    private DialogInterface.OnClickListener listenerNeutral;

    int negative;
    private CharSequence textNegative;
    private DialogInterface.OnClickListener listenerNegative;

    int positive;
    private CharSequence textPositive;
    private DialogInterface.OnClickListener listenerPositive;

    public AlertDialog alerter;

    public AlertFeature(AppCompatActivity app) {
        refer = new WeakReference<>(app);
    }

    public static AlertFeature getInstance(final AppCompatActivity app) {
        if (INSTANCE == null || (INSTANCE.refer != null && !INSTANCE.refer.get().getLocalClassName().equals(app.getLocalClassName()))) {
            synchronized (AlertFeature.class) {
                if (INSTANCE == null || (INSTANCE.refer != null && !INSTANCE.refer.get().getLocalClassName().equals(app.getLocalClassName()))) {
                    INSTANCE = new AlertFeature(app); } } }

        return INSTANCE;
    }

    public AlertFeature init () {

        dismiss();
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(refer.get());
        alertDialogBuilder.setTitle(getTitle());
        alertDialogBuilder.setMessage(getText());

        if (this.neutral > 0) alertDialogBuilder.setNeutralButton(new StringBuilder(textNeutral), listenerNeutral);
        if (this.negative > 0) alertDialogBuilder.setNegativeButton(new StringBuilder(textNegative), listenerNegative);
        if (this.positive > 0) alertDialogBuilder.setPositiveButton(new StringBuilder(textPositive), listenerPositive);
        negative = 0; positive = 0; neutral = 0;

        alerter = alertDialogBuilder.create();
        alerter.setCancelable(false);

        return INSTANCE;
    }

    public static AlertDialog getAlerter() {
        return  INSTANCE.alerter;
    }

    public static boolean isShowing () {
        return INSTANCE != null && INSTANCE.alerter != null && INSTANCE.alerter.isShowing();
    }

    private void dismiss () {
        if (isShowing()) alerter.dismiss(); cancel();
    }

    private void cancel () {
        if (isShowing()) INSTANCE.alerter.cancel();
    }

    public static AlertFeature show () {
        INSTANCE.cancel(); if (INSTANCE != null && INSTANCE.alerter != null) {
            INSTANCE.refer.get().runOnUiThread(() -> INSTANCE.alerter.show());
        }
        return INSTANCE;
    }

    public AlertFeature setNeutralButton(@NonNull CharSequence text, final DialogInterface.OnClickListener listener) {
        this.neutral = 1;
        this.textNeutral = text;
        this.listenerNeutral = listener;

        return INSTANCE;
    }

    public AlertFeature setNegativeButton(@NonNull CharSequence text, final DialogInterface.OnClickListener listener) {
        this.negative = 1;
        this.textNegative = text;
        this.listenerNegative = listener;

        return INSTANCE;
    }

    public AlertFeature setPositiveButton(@NonNull CharSequence text, final DialogInterface.OnClickListener listener) {
        this.positive = 1;
        this.textPositive = text;
        this.listenerPositive = listener;

        return INSTANCE;
    }

    public String getTitle() {
        return this.title;
    }

    public AlertFeature setTitle(String title) {
        this.title = title;
        return INSTANCE;
    }

    public String getText() {
        return text;
    }

    public AlertFeature setText(String text) {
        this.text = text;
        return INSTANCE;
    }

    public static void clear () {
        if (INSTANCE != null && INSTANCE.refer != null) INSTANCE.refer.clear();
        if (INSTANCE != null && INSTANCE.alerter != null) INSTANCE.alerter = null;
        INSTANCE = null;
    }
}
