package com.qbay.qbayApps.ilpcsm.Services;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import com.qbay.qbayApps.ilpcsm.R;

/**
 * Created by benchmark on 17/03/18.
 */

public class LoadingDialogue {

    public void showDialog(Activity activity){

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.loading_view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
    }

}
