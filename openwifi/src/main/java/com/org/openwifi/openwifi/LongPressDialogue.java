package com.org.openwifi.openwifi;

import android.app.AlertDialog;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.org.openwifi.interfaces.IDialogue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jack gurulian.
 */
public class LongPressDialogue extends Dialogue implements IDialogue {

    private final int FORGET_NETWORK_POSITION = 0;
    private final int MODIFY_NETWORK_POSITION = 1;
    private Context context;
    private String wifiName;
    private Connector connector;
    private List<ScanResult> results;
    private int pos;
    private AlertDialog alertConnect;

    /**
     * Constructor. Brings in all the necessary information in order to present the correct info on
     * the long press WiFi item dialogue.
     *
     * @param context   the application Context
     * @param wifiName  the name of the target WiFi
     * @param connector the connector for the WiFi
     * @param results   the list of scanned WiFis
     * @param pos       the position of the selected WiFi on the list
     */
    protected LongPressDialogue(Context context, String wifiName, Connector connector,
                                List<ScanResult> results, int pos) {
        super(context);

        this.context = context;
        this.wifiName = wifiName;
        this.connector = connector;
        this.results = results;
        this.pos = pos;
    }

    @Override
    public void build() {

        ArrayAdapter<String> optionAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_list_item_1, buildOptions());

        ListView optionList = new ListView(context);
        optionList.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        optionList.setAdapter(optionAdapter);

        LinearLayout options = new LinearLayout(context);
        options.addView(optionList);

        this.createDialogue(wifiName, null, options, 0);

        alertConnect = this.createAlert();

        keyListener(optionList);
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

    private ArrayList<String> buildOptions() {
        ArrayList<String> options = new ArrayList<String>();
        options.add(context.getString(R.string.forget));
        options.add(context.getString(R.string.modify));

        return options;
    }

    private void keyListener(ListView optionList) {

        optionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                switch (position) {
                    case MODIFY_NETWORK_POSITION:
                        ConnectionDialogue conDial = new ConnectionDialogue(context, wifiName,
                                connector, results, pos);
                        conDial.build();
                        conDial.showAlert();
                        dismissAlert();
                        break;
                    case FORGET_NETWORK_POSITION:
                        if (connector.forgetNetwork(wifiName))
                            dismissAlert();
                        else
                            Toast.makeText(context, context.getString(R.string.forget_fail),
                                    Toast.LENGTH_LONG).show();
                        break;
                    default:
                        break;
                }
            }
        });
    }
}
