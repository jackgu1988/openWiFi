package com.org.openwifi.openwifi;

import android.app.AlertDialog;
import android.content.Context;
import android.view.ContextThemeWrapper;
import android.widget.LinearLayout;

/**
 * Created by jack gurulian
 */
public class Dialogue extends AlertDialog {

    private Context context;
    private Builder alert;

    protected Dialogue(Context context) {
        super(context);

        this.context = context;
    }

    /**
     * Creates the dialogue
     *
     * @param title   dialogue title
     * @param message dialogue message
     * @param content dialogue content elements
     * @param col     colour (0: white / 1: black)
     * @return the dialogue
     */
    public Builder createDialogue(String title, String message, LinearLayout content, int col) {
        // 0: white
        // 1: dark
        if (col == 0)
            alert = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.DiscDialog));
        else
            alert = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.Theme_Base));
        alert.setTitle(title)
                .setView(content);

        if (message != null && !message.equals(""))
            alert.setMessage(message);

        return alert;
    }

    public AlertDialog createAlert() {
        return alert.create();
    }

}
