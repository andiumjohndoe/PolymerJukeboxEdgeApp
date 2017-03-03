package com.andium.edge.polymer.jukeboxedgeapp;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 *
 * @author dools
 */
public class YoutubeChromecastClient {
    private final String addr;


    public YoutubeChromecastClient(String addr) {
        this.addr = addr;
    }


    private void doUpdateState(String surl, String requestBody, String method) {
        HttpURLConnection connection = null;

        try {
            //Create connection
            URL url = new URL(surl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);

            connection.setRequestProperty("Content-Type", "application/json");
            //connection.setRequestProperty("Content-Length", Integer.toString(urlParameters.getBytes().length));
            //connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoOutput(true);

            //Send request
            if (requestBody != null) {
                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes(requestBody);
                wr.close();
            }


            //Get Response

            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = rd.readLine()) != null) {
                //System.out.println(line);
            }
            rd.close();
            //return response.toString();
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public void play(String videoID) {
        String surl = "http://" + this.addr + ":8008/apps/YouTube";
        String requestBody = "v=" + videoID;
        doUpdateState(surl, requestBody, "POST");
    }

    public void stop() {
        String surl = "http://" + this.addr + ":8008/apps/YouTube";

        doUpdateState(surl, null, "DELETE");
    }

}