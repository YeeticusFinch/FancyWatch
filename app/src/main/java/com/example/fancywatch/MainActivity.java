package com.example.fancywatch;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fancywatch.ui.main.SectionsPagerAdapter;

import java.util.ArrayList;
import java.util.Collection;

import com.harrysoft.androidbluetoothserial.BluetoothManager;
import com.harrysoft.androidbluetoothserial.BluetoothSerialDevice;
import com.harrysoft.androidbluetoothserial.SimpleBluetoothDeviceInterface;

import org.w3c.dom.Text;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {

    int cTab = 0;
    int sn = -1;
    int lm = -1;
    int brightness = 30;
    int lightSpeed = 10;
    int bleID = -1;
    boolean tempDis = false;
    ArrayList<String> namesBLE = new ArrayList<String>();
    ArrayList<String> macsBLE = new ArrayList<String>();

    ArrayList<String> songs = new ArrayList<String>();
    ArrayList<String> lights = new ArrayList<String>();
    ArrayList<String> help = new ArrayList<String>();

    String lastTransmit = "";

    private SimpleBluetoothDeviceInterface deviceInterface;
    BluetoothManager bluetoothManager = BluetoothManager.getInstance();

    TextView cText;
    Spinner spin;
    EditText textInput;
    EditText c0, c1, c2, c3;
    TextView c0t, c1t, c2t, c3t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager__main);
        viewPager.setAdapter(sectionsPagerAdapter);
        final TabLayout tabs = findViewById(R.id.tabs);
        final Button button2 = findViewById(R.id.button2);
        final Button button4 = findViewById(R.id.button4);
        final Button button5 = findViewById(R.id.button5);
        final Button button6 = findViewById(R.id.button6);
        final Button button7 = findViewById(R.id.button7);
        final TextView text2 = findViewById(R.id.brightText);
        final TextView text3 = findViewById(R.id.lightText);
        final SeekBar seek = findViewById(R.id.seekBar);
        final SeekBar seek2 = findViewById(R.id.seekBar2);
        c0 = findViewById(R.id.c0);
        c1 = findViewById(R.id.c1);
        c2 = findViewById(R.id.c2);
        c3 = findViewById(R.id.c3);
        c0t = findViewById(R.id.c0t);
        c1t = findViewById(R.id.c1t);
        c2t = findViewById(R.id.c2t);
        c3t = findViewById(R.id.c3t);
        textInput = findViewById(R.id.textInput);

        spin = findViewById(R.id.spinner);
        final Spinner spinBLE = findViewById(R.id.spinnerBLE);
        cText = findViewById(R.id.consolText);

        //BLE STUFF

        Collection<BluetoothDevice> pairedDevices = bluetoothManager.getPairedDevicesList();
        namesBLE.add("Select MAC");
        macsBLE.add("EMPTY");
        for (BluetoothDevice device : pairedDevices) {
            namesBLE.add(device.getName());
            macsBLE.add(device.getAddress());
            Log.d("My Bluetooth App", "Device name: " + device.getName());
            Log.d("My Bluetooth App", "Device MAC Address: " + device.getAddress());
        }

        ArrayAdapter<CharSequence> adapterBLE = new ArrayAdapter(this,android.R.layout.simple_spinner_item,namesBLE.toArray());
        spinBLE.setAdapter(adapterBLE);

        //spin.setAdapter(getAdapter(R.array.songs_array));

        tabs.setupWithViewPager(viewPager);
        /*FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        tabs.getTabAt(0).setText("Music");
        tabs.getTabAt(1).setText("Lights");
        tabs.addTab(tabs.newTab());
        tabs.getTabAt(2).setText("System");
        button2.setText("Play/Pause");
        button4.setText("Orchestra Play");
        button5.setText("Toggle Repeat");
        button6.setText("Toggle Shuffle");
        button7.setText("Random Song");
        text2.setText("Brightness: " + brightness);
        text3.setText("Light Speed: " + lightSpeed);
        textInput.setVisibility(View.INVISIBLE);
        seek.setVisibility(View.INVISIBLE);
        text2.setVisibility(View.INVISIBLE);
        c0.setVisibility(View.INVISIBLE);
        c1.setVisibility(View.INVISIBLE);
        c2.setVisibility(View.INVISIBLE);
        c3.setVisibility(View.INVISIBLE);
        c0t.setVisibility(View.INVISIBLE);
        c1t.setVisibility(View.INVISIBLE);
        c2t.setVisibility(View.INVISIBLE);
        c3t.setVisibility(View.INVISIBLE);
        seek.setVisibility(View.INVISIBLE);
        text2.setVisibility(View.INVISIBLE);
        seek2.setVisibility(View.INVISIBLE);
        text3.setVisibility(View.INVISIBLE);
        //seek.setMin(0);
        seek.setMax(100);
        seek.setProgress(30);
        seek2.setMax(50);
        seek2.setProgress(10);

        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                brightness = i;
                text2.setText("Brightness: " + brightness);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                transmit("bright"+brightness);
            }
        });

        seek2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                lightSpeed = Math.max(1, i);
                text3.setText("Light Speed: " + lightSpeed);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                transmit("ls"+lightSpeed);
            }
        });

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                cTab = tabs.getSelectedTabPosition();
                switch(cTab) {
                    case 0: //Music
                        tempDis = true;
                        button2.setText("Play/Pause");
                        button4.setText("Orchestra Play");
                        button5.setText("Toggle Repeat");
                        button6.setText("Toggle Shuffle");
                        button7.setText("Random Song");

                        button4.setVisibility(View.VISIBLE);
                        button5.setVisibility(View.VISIBLE);
                        button6.setVisibility(View.VISIBLE);
                        button7.setVisibility(View.VISIBLE);
                        seek.setVisibility(View.INVISIBLE);
                        text2.setVisibility(View.INVISIBLE);
                        seek2.setVisibility(View.INVISIBLE);
                        text3.setVisibility(View.INVISIBLE);
                        c0.setVisibility(View.INVISIBLE);
                        c1.setVisibility(View.INVISIBLE);
                        c2.setVisibility(View.INVISIBLE);
                        c3.setVisibility(View.INVISIBLE);
                        c0t.setVisibility(View.INVISIBLE);
                        c1t.setVisibility(View.INVISIBLE);
                        c2t.setVisibility(View.INVISIBLE);
                        c3t.setVisibility(View.INVISIBLE);
                        textInput.setVisibility(View.INVISIBLE);

                        spin.setAdapter(adaptArrayList(songs));
                        spin.setSelection(sn);
                        break;
                    case 1: //Lights
                        button2.setText("Toggle Lights");
                        button6.setText("Update Colors");
                        button4.setVisibility(View.INVISIBLE);
                        button5.setVisibility(View.INVISIBLE);
                        button6.setVisibility(View.VISIBLE);
                        button7.setVisibility(View.INVISIBLE);
                        seek.setVisibility(View.VISIBLE);
                        text2.setVisibility(View.VISIBLE);
                        seek2.setVisibility(View.VISIBLE);
                        text3.setVisibility(View.VISIBLE);
                        textInput.setVisibility(View.INVISIBLE);
                        c0.setVisibility(View.VISIBLE);
                        c1.setVisibility(View.VISIBLE);
                        c2.setVisibility(View.VISIBLE);
                        c3.setVisibility(View.VISIBLE);
                        c0t.setVisibility(View.VISIBLE);
                        c1t.setVisibility(View.VISIBLE);
                        c2t.setVisibility(View.VISIBLE);
                        c3t.setVisibility(View.VISIBLE);

                        spin.setAdapter(adaptArrayList(lights));
                        spin.setSelection(lm);
                        break;
                    case 2: //System
                        button2.setText("Send Command");
                        button4.setVisibility(View.INVISIBLE);
                        button5.setVisibility(View.INVISIBLE);
                        button6.setVisibility(View.INVISIBLE);
                        button7.setVisibility(View.INVISIBLE);
                        seek.setVisibility(View.VISIBLE);
                        text2.setVisibility(View.VISIBLE);
                        seek2.setVisibility(View.VISIBLE);
                        text3.setVisibility(View.VISIBLE);
                        spin.setAdapter(adaptArrayList(help));
                        textInput.setVisibility(View.VISIBLE);
                        c0.setVisibility(View.INVISIBLE);
                        c1.setVisibility(View.INVISIBLE);
                        c2.setVisibility(View.INVISIBLE);
                        c3.setVisibility(View.INVISIBLE);
                        c0t.setVisibility(View.INVISIBLE);
                        c1t.setVisibility(View.INVISIBLE);
                        c2t.setVisibility(View.INVISIBLE);
                        c3t.setVisibility(View.INVISIBLE);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        spin.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (cTab == 0) {
                    if (tempDis)
                        tempDis = false;
                    else {
                        sn = i;
                        transmit("sn" + sn);
                    }
                } else if (cTab == 1) {
                    lm = i;
                    transmit("l" + lm);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

                sn = -1;
            }
        });

        spinBLE.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                bluetoothManager.close();
                if (i > 0) {
                    connectDevice(macsBLE.get(i));
                    bleID = i;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        cText.setText("Successfully Initialized, not connected");

    }

    public void buttonPress(View view) {
        if (cTab == 0) {
            //transmit("sn"+sn);
            transmit("p");
            cText.setText("playing song " + songs.get(sn));
        } else if (cTab == 1) {
            transmit("lights");
            cText.setText("turned on lights to " + lights.get(lm));
        } else if (cTab == 2) {
            cText.setText("Transmitting:"+textInput.getText().toString());
            transmit(textInput.getText().toString());
            textInput.setText("");
        }
    }

    public void buttonPressFour(View view) {
        if (cTab == 0) {
            //transmit("sn"+sn);
            long x = millisToMinutes(todayMillisex());
            x+=1;
            x = minutesToMillis(x);
            transmit("schp"+x);
            cText.setText("playing song " + songs.get(sn) + " at " + x);
        }
    }

    public void buttonPressFive(View view) {
        if (cTab == 0) {
            transmit("rep");
        }
    }

    public void buttonPressSix(View view) {
        if (cTab == 0) {
            transmit("shuf");
        } else if (cTab == 1) {
            transmit("c0"+c0.getText().toString());
            transmit("c1"+c1.getText().toString());
            transmit("c2"+c2.getText().toString());
            transmit("c3"+c3.getText().toString());
            cText.setText("Updated Colors");
        }
    }

    public void buttonPressSeven(View view) {
        if (cTab == 0) {
            transmit("rand");
        }
    }

    public void transmit(String message) {
        deviceInterface.sendMessage(message);
        lastTransmit = message;
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void disconnect(View view) {
        // Disconnect all devices
        bluetoothManager.close();
        cText.setText("Disconnected from BLE");
    }

    public ArrayAdapter<CharSequence> getAdapter(int resID) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                resID, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    private void connectDevice(String mac) {
        bluetoothManager.openSerialDevice(mac)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onConnected, this::onError);
        cText.setText("Trying to connect to device " + bleID);
    }

    private void onConnected(BluetoothSerialDevice connectedDevice) {
        // You are now connected to this device!
        // Here you may want to retain an instance to your device:
        deviceInterface = connectedDevice.toSimpleDeviceInterface();

        // Listen to bluetooth events
        deviceInterface.setListeners(this::onMessageReceived, this::onMessageSent, this::onError);

        // Let's send a message:
        //deviceInterface.sendMessage("lights");
        //deviceInterface.sendMessage("sn"+sn);
        transmit("ha");
        transmit("etime"+todayMillisex());
        transmit("time"+((todayMillisex()/3600000)%12+5)+":"+((todayMillisex()/60000)%60));
        transmit("songs");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //transmit("etime"+System.currentTimeMillis());
        cText.setText("Connected to " + namesBLE.get(bleID));
    }

    private int todayMillisex() {
        long x = System.currentTimeMillis();
        x/=86400000l;
        x*=86400000l;
        return (int)(System.currentTimeMillis()-x);
    }

    private void onMessageSent(String message) {
        // We sent a message! Handle it here.
        //Toast.makeText(context, "Sent a message! Message was: " + message, Toast.LENGTH_LONG).show(); // Replace context with your context instance.
    }

    private long millisToMinutes(long millis) {
        return (long)(millis/60000);
    }

    private long minutesToMillis(long mins) {
        return (long)(mins*60000);
    }

    private void onMessageReceived(String message) {
        // We received a message! Handle it here.
        //Toast.makeText(context, "Received a message! Message was: " + message, Toast.LENGTH_LONG).show(); // Replace context with your context instance.
        cText.setText(message);
        if (lastTransmit.equalsIgnoreCase("songs") && !(message.length()>4 && message.substring(0, 4).equals("hour"))) {
            if (message.charAt(message.indexOf(')')+1) == ' ')
                songs.add(message.substring(message.indexOf(')')+2));
            else
                songs.add(message.substring(message.indexOf(')')+1));
            if (message.charAt(message.length()-1) != '\n') {
                ArrayAdapter<CharSequence> adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,songs.toArray());
                spin.setAdapter(adapter);
                cText.setText("Reset Spinner");

            }
        }
        else if (lastTransmit.equalsIgnoreCase("lm")) {
            if (message.length() >= "Light Modes:".length() && message.substring(0, "Light Modes:".length()).equalsIgnoreCase("Light Modes:"))
                ;
            else {
                if (message.charAt(message.indexOf(')') + 1) == ' ')
                    lights.add(message.substring(message.indexOf(')') + 2));
                else
                    lights.add(message.substring(message.indexOf(')') + 1));
                if (message.charAt(message.length() - 1) != '\n') {
                    ArrayAdapter<CharSequence> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, lights.toArray());
                    spin.setAdapter(adapter);
                    cText.setText("Reset Spinner");

                }
            }
        }
        else if (lastTransmit.equalsIgnoreCase("help")) {

            help.add(message);
            if (message.charAt(message.length() - 1) != '\n') {
                ArrayAdapter<CharSequence> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, help.toArray());
                spin.setAdapter(adapter);
                cText.setText("Reset Spinner");
            }
        }

    }

    private ArrayAdapter<CharSequence> adaptArrayList(ArrayList<String> data) {

        if (cTab == 1 && (lights == null || lights.size() < 1)) {
            transmit("lm");
        }
        if (cTab == 2 && (help == null || help.size() < 1)) {
            transmit("help");
        }

        return new ArrayAdapter(this,android.R.layout.simple_spinner_item,data.toArray());
    }

    private void onError(Throwable error) {
        // Handle the error
        cText.setText("Error: " + error.toString());
        //cText.setText("Failed to connect to " + macsBLE.get(bleID));
    }

}

