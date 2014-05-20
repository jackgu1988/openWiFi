package com.org.openwifi.openwifi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.ScanResult;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.org.openwifi.interfaces.IDialogue;

import java.util.List;

/**
 * Created by jack gurulian
 */
public class ConnectionDialogue extends Dialogue implements IDialogue {

    private Context context;
    private String wifiName;
    private Connector connector;
    private List<ScanResult> results;
    private int pos;
    private AlertDialog alertConnect;

    protected ConnectionDialogue(Context context, String wifiName, Connector connector,
                                 List<ScanResult> results, int pos) {
        super(context);

        this.context = context;
        this.wifiName = wifiName;
        this.connector = connector;
        this.results = results;
        this.pos = pos;
    }

    private CheckBox createShowPassCheckBox() {
        CheckBox showPass = new CheckBox(context);
        showPass.setSelected(false);
        showPass.setText(context.getString(R.string.show_pass));
        showPass.callOnClick();
        showPass.setTextColor(0xFF000000);

        return showPass;
    }

    private EditText createInput() {
        EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setTextColor(0xFF000000);

        return input;
    }

    private CheckBox createAdvanced() {
        CheckBox advanced = new CheckBox(context);
        advanced.setSelected(false);
        advanced.setText(context.getString(R.string.advanced));
        advanced.callOnClick();
        advanced.setTextColor(0xFF000000);

        return advanced;
    }

    private LinearLayout createLayout(String security, CheckBox showPass, EditText input,
                                      CheckBox advanced) {

        LinearLayout boxFields = new LinearLayout(context);
        boxFields.setOrientation(LinearLayout.VERTICAL);
        if (getSecurityTypeAsInt(security) != 0) {
            boxFields.addView(input);
            boxFields.addView(showPass);
            boxFields.addView(advanced);
        }

        return boxFields;
    }

    @Override
    public void build() {

        final String security = results.get(pos).capabilities;

        CheckBox showPass = createShowPassCheckBox();
        final EditText input = createInput();
        CheckBox advanced = createAdvanced();

        LinearLayout boxFields = createLayout(security, showPass, input, advanced);

        Builder alert = this.createDialogue(wifiName, (getSecurityTypeAsInt(security) != 0 ?
                context.getString(R.string.enter_pass) : context.getString(R.string.open)), boxFields, 0)
                .setPositiveButton(context.getString(R.string.connect), new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String pass = input.getText().toString();
                        Toast.makeText(context, context.getString(R.string.connecting),
                                Toast.LENGTH_LONG).show();
                        connector.connectToAp(wifiName, pass, getSecurityTypeAsInt(security));
                    }
                })
                .setNegativeButton(context.getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }
                );

        alertConnect = alert.create();
        alertConnect.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                if ((getSecurityTypeAsInt(security) == 2 || getSecurityTypeAsInt(security) == 3)
                        && input.getText().length() < 8)
                    alertConnect.getButton(BUTTON_POSITIVE).setEnabled(false);
            }
        });

        showPassListener(showPass, input);
        passwordListener(input, security);
    }

    private int getSecurityTypeAsInt(String sec) {
        if (sec.contains("WPA2"))
            return 3;
        else if (sec.contains("WPA"))
            return 2;
        else if (sec.contains("WEP"))
            return 1;
        else
            return 0;
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

    private void showPassListener(CheckBox showPass, final EditText input) {
        showPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    input.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                } else {
                    input.setInputType(InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });
    }

    private void passwordListener(final EditText input, final String security) {
        input.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (getSecurityTypeAsInt(security) == 2 || getSecurityTypeAsInt(security) == 3)
                    if (input.getText().length() >= 8)
                        alertConnect.getButton(BUTTON_POSITIVE).setEnabled(true);
                    else
                        alertConnect.getButton(BUTTON_POSITIVE).setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    alertConnect.getWindow().setSoftInputMode(
                            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
    }
}
