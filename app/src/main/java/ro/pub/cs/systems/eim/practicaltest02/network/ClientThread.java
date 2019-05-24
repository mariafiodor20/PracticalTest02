package ro.pub.cs.systems.eim.practicaltest02.network;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import cz.msebera.android.httpclient.client.ClientProtocolException;
import ro.pub.cs.systems.eim.practicaltest02.data.Alarm;
import ro.pub.cs.systems.eim.practicaltest02.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02.general.Utilities;

public class ClientThread extends Thread {
    private String address;
    private int port;
    //private String city;
    private int hour;
    private int minute;
    private String type;
   // private String informationType;
    private TextView weatherForecastTextView;

    private Socket socket;

    public ClientThread(String address, int port, int hour, int minute, String type, TextView weatherForecastTextView) {
        this.address = address;
        this.port = port;
        this.hour = hour;
        this.minute = minute;
        this.type = type;
        //this.informationType = informationType;
        this.weatherForecastTextView = weatherForecastTextView;
        Log.i(Constants.TAG, "[CLIENT THREAD] succes");

    }

    @Override
    public void run() {
        try {
            Log.i(Constants.TAG, "[CLIENT THREAD] succes");
            socket = new Socket(address, port);
            if (socket == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Could not create socket!");
                return;
            }
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            printWriter.println(type);
            printWriter.flush();
            printWriter.println(hour);
            printWriter.flush();
            printWriter.println(minute);
            printWriter.flush();

            //printWriter.println(informationType);
           // printWriter.flush();
            String weatherInformation = "";
            while ((weatherInformation = bufferedReader.readLine()) != null) {
                final String finalizedWeateherInformation = weatherInformation;
                weatherForecastTextView.post(new Runnable() {
                    @Override
                    public void run() {
                        weatherForecastTextView.setText(finalizedWeateherInformation);
                    }
                });
            }
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }


}

