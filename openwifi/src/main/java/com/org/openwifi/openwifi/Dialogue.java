package com.org.openwifi.openwifi;

import android.app.AlertDialog;
import android.content.Context;
import android.view.ContextThemeWrapper;
import android.widget.ListView;

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

    public void createDialogue(String title, String message, ListView content) {
        alert = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.DiscDialog));
        alert.setTitle(title)
                .setView(content);

        if (message != null && !message.equals(""))
            alert.setMessage(message);
    }

    public AlertDialog createAlert() {
        return alert.create();
    }
}
