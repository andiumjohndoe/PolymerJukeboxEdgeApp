package com.andium.edge.polymer.jukeboxedgeapp;

import com.andium.edge.appapi.AndiumAppID;
import com.andium.edge.appapi.AndiumApplication;
import com.andium.edge.appapi.AndiumEdgeApp;
import com.andium.edge.appapi.Context;
import com.andium.edge.appapi.entity.Pier;
import com.andium.edge.appapi.entity.PierDataType;
import com.andium.edge.appapi.logging.Logger;
import com.andium.edge.appapi.messaging.PierCoapMessage;
import com.andium.edge.appapi.services.PierService;
import com.andium.edge.appapi.services.PierServiceListener;
import org.json.JSONArray;
import org.json.JSONObject;
/**
 * Created by @Author lukewhittington
 * on 2016/09/29
 * Copyright of Andium, Inc.
 */
@AndiumApplication(author = "Luke", version=1, id="urn:andium:appid:JukeBoxEdgeExample")
public class Application implements AndiumEdgeApp {
    private AndiumAppID id;
    private Logger logger;
    private PierService pierService;
    private boolean happyDooley = false;
    private YoutubeChromecastClient client;
    private static final String CCAST_IP_ADDRESS = "10.0.0.62";

    @Override
    public AndiumAppID getID() {
        return id;
    }

    @Override
    public void setID(AndiumAppID id) {
        this.id = id;
    }

    @Override
    public void start(Context context) {
        logger = context.getLogger();
        pierService = context.getPierService();
        pierService.setListener(pierListener);
        happyDooley = true;
        client = new YoutubeChromecastClient(CCAST_IP_ADDRESS);
    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isRunning() {
        return happyDooley;
    }

    private PierServiceListener pierListener = new PierServiceListener() {
        @Override
        public void pierAdded(Pier p) {
            logger.info("Edge app, new pier!");
        }

        @Override
        public void pierDataReceived(Pier p, PierDataType type, String value) {
            logger.info("Pier Data received from Edge");
            checkAndSendData(type, value);
        }
    };

    private void checkAndSendData(PierDataType type, String value){
        JSONObject root;
        JSONArray arr;
        JSONObject reading;
        String key = "";
        String valueKey = "";

        logger.info("Type: " + type.getName());
        if (type.getName().equals("button")){
            key = "speaker";
        } else if (type.getName().equals("pir")){
            //send BLE
            pierService.sendPierMessage(new PierCoapMessage("[2001:db8::235:2aff:fe77:4ad1]", value, "ble", "lights/led3"));
            pierService.sendPierMessage(new PierCoapMessage("[2001:db8::235:2aff:fe77:4ad1]", value, "ble", "lights/led4"));
        } else if (type.getName().equals("rfid1")){
            String[] split = value.split(":");
            valueKey = split[0];
            if (split.length > 0) {
                 if (valueKey.equals("cd")){
                    key = "speaker";
                } else if (valueKey.equals("dvd")){
                    key = "";
                     logger.info("dvd -playing now");
                    // Shia Surprise
                    client.play(split[1]);
                    // send to chromecast
                }
                value = split[1];
            }
        }

        for (Pier p : pierService.getCurrentlyKnownPiers()){
            for (PierDataType t : p.getTypes()){
                if (t.getName().equals(key) && t.getDirection().toLowerCase().equals(PierDataType.DIR_IN)){
                    reading = new JSONObject();
                    root = new JSONObject();
                    arr = new JSONArray();
                    reading.put("tstamp", (System.currentTimeMillis() / 1000L));
                    reading.put("value", value);
                    reading.put("channel", t.getChannel());
                    reading.put("dataType", t.getName());
                    arr.put(reading);
                    root.put("data", arr);
                    String json = root.toString();

                    pierService.sendPierMessage(new PierCoapMessage(p.getHost(), json, "", "data_in"));
                }
            }
        }
    }
}

