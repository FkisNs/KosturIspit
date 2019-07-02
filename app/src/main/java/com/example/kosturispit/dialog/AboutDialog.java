package com.example.kosturispit.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

public class AboutDialog extends AlertDialog.Builder {
    public AboutDialog(@NonNull Context context) {
        super(context);
        setTitle("About the app");
        setMessage("App za kontrolnu tacku 3 by Fedor Kis");
        setPositiveButton("U redu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }
}
