package org.mygeotrust.indoor.utils.dialogue;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.fhc25.percepcion.osiris.mapviewer.R;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

/**
 * Created by Dr. Mahbubul Syeed on 22.7.2016.
 */
public class Dialogs {
    private static Dialogs ourInstance = new Dialogs();

    public static Dialogs getInstance() {
        return ourInstance;
    }

    private Dialogs() {
    }

    private IDialogs observer = null;

    public final void showInfoDialog(Context activityContext, String title, String msg, int icon, int color) {
        new LovelyInfoDialog(activityContext)
                .setTopColorRes(color)
                .setIcon(icon)
                //This will add Don't show again checkbox to the dialog. You can pass any ID as argument
                //.setNotShowAgainOptionEnabled(0)
                .setTitle(title)
                .setMessage(msg)
                .show();
    }


    public final void showStandardDialog(IDialogs observer, String title, String message, String txtPositiveBtn, String txtNegBtn) {
        this.observer = observer;

        new LovelyStandardDialog((Activity)observer)
                .setTopColorRes(R.color.wallet_holo_blue_light)
                .setButtonsColorRes(R.color.wallet_holo_blue_light)
                .setIcon(R.drawable.ic_info)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(txtPositiveBtn, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        notifyObserver(IDialogs.SelectionStatus.ok_pressed);
                    }
                })
                .setNegativeButton(txtNegBtn, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        notifyObserver(IDialogs.SelectionStatus.cancel_pressed);
                    }
                })
                .show();
    }


    private void notifyObserver(IDialogs.SelectionStatus selectionStatus)
    {
        if(observer != null)
            observer.onDialogOptionSelected(selectionStatus);
    }

    public final void unregisterObserver()
    {
        observer = null;
    }
}
