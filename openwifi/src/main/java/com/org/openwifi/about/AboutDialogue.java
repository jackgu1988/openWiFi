package com.org.openwifi.about;

import android.app.AlertDialog;
import android.content.Context;

import com.org.openwifi.interfaces.IDialogue;
import com.org.openwifi.openwifi.Dialogue;
import com.org.openwifi.openwifi.R;

/**
 * Created by jack gurulian
 */
public class AboutDialogue extends Dialogue implements IDialogue {

    private AlertDialog alertConnect;
    private Context context;

    public AboutDialogue(Context context) {
        super(context);

        this.context = context;

        build();
        showAlert();
    }

    @Override
    public void build() {
        this.createDialogue(context.getString(R.string.about_title),
                context.getString(R.string.about_text), null, 1);

        alertConnect = this.createAlert();
    }

    @Override
    public void showAlert() {
        alertConnect.show();
    }

    @Override
    public void hideAlert() {
        alertConnect.hide();
    }

    @Override
    public void dismissAlert() {
        alertConnect.dismiss();
    }
}
