package ro.pub.cs.systems.eim.practicaltest02.network;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.protocol.HTTP;
import ro.pub.cs.systems.eim.practicaltest02.data.Alarm;
import ro.pub.cs.systems.eim.practicaltest02.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02.general.Utilities;

public class CommunicationThread extends Thread {

    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }



    @Override
    public void run() {
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client (hour / minute type!");
            //String city = bufferedReader.readLine();
            //String informationType = bufferedReader.readLine();
            String type = bufferedReader.readLine();
            int hour = Integer.parseInt(bufferedReader.readLine());
            int minute = Integer.parseInt(bufferedReader.readLine());
            /*if (city == null || city.isEmpty() ) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (city / information type!");
                return;
            }*/
            String city = socket.getInetAddress().toString();

            HashMap<String, Alarm> data = serverThread.getData();
            Alarm alarm = new Alarm(hour, minute);
           /* if (!data.containsKey(city)) {
               // Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the cache...");
                data.put(city, alarm);
            } else {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the webservice...");
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Constants.WEB_SERVICE_ADDRESS);
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair(Constants.QUERY_ATTRIBUTE, city));
                UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                httpPost.setEntity(urlEncodedFormEntity);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String pageSourceCode = httpClient.execute(httpPost, responseHandler);
                if (pageSourceCode == null) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error getting the information from the webservice!");
                    return;
                }
                Document document = Jsoup.parse(pageSourceCode);
                Element element = document.child(0);
                Elements elements = element.getElementsByTag(Constants.SCRIPT_TAG);
                for (Element script: elements) {
                    String scriptData = script.data();
                    if (scriptData.contains(Constants.SEARCH_KEY)) {
                        int position = scriptData.indexOf(Constants.SEARCH_KEY) + Constants.SEARCH_KEY.length();
                        scriptData = scriptData.substring(position);
                        JSONObject content = new JSONObject(scriptData);
                        JSONObject currentObservation = content.getJSONObject(Constants.CURRENT_OBSERVATION);
                        String temperature = currentObservation.getString(Constants.TEMPERATURE);
                        String windSpeed = currentObservation.getString(Constants.WIND_SPEED);
                        String condition = currentObservation.getString(Constants.CONDITION);
                        String pressure = currentObservation.getString(Constants.PRESSURE);
                        String humidity = currentObservation.getString(Constants.HUMIDITY);
                        weatherForecastInformation = new WeatherForecastInformation(
                                temperature, windSpeed, condition, pressure, humidity
                        );
                        serverThread.setData(city, weatherForecastInformation);
                        break;
                    }
                }
            }*/
            if (alarm == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Alarm is null!");
                return;
            }

//            Log.e(Constants.TAG, "type este " + type);
//            String result = null;
//            data.put(city, alarm);
//            Log.e(Constants.TAG, "[APP] A fost adaugata o alarma");
//            result = "a fost adaugata o alarma";

            if(type.equals("set")){
                String result = null;
                data.put(city, alarm);

                Log.e(Constants.TAG, "[APP] A fost adaugata o alarma");

            }
            else
                if(type.equals("reset")){
                    data.remove(city);
                    Log.e(Constants.TAG, "[APP] A fost resetata o alarma");
                }
                else
                    if(type.equals("poll")){
                        /*String dayTimeProtocol = null;
                        try {
                            Socket socket = new Socket(Constants.NIST_SERVER_HOST, Constants.NIST_SERVER_PORT);
                            BufferedReader bufferedReaderaux = Utilities.getReader(socket);
                            bufferedReader.readLine();
                            dayTimeProtocol = bufferedReaderaux.readLine();
                            Log.d(Constants.TAG, "The server returned: " + dayTimeProtocol);
                        } catch (UnknownHostException unknownHostException) {
                            Log.d(Constants.TAG, unknownHostException.getMessage());
                            if (Constants.DEBUG) {
                                unknownHostException.printStackTrace();
                            }
                        } catch (IOException ioException) {
                            Log.d(Constants.TAG, ioException.getMessage());
                            if (Constants.DEBUG) {
                                ioException.printStackTrace();
                            }
                        }*/
                    }
            /*switch(informationType) {
                case Constants.ALL:
                    result = weatherForecastInformation.toString();
                    break;
                case Constants.TEMPERATURE:
                    result = weatherForecastInformation.getTemperature();
                    break;
                case Constants.WIND_SPEED:
                    result = weatherForecastInformation.getWindSpeed();
                    break;
                case Constants.CONDITION:
                    result = weatherForecastInformation.getCondition();
                    break;
                case Constants.HUMIDITY:
                    result = weatherForecastInformation.getHumidity();
                    break;
                case Constants.PRESSURE:
                    result = weatherForecastInformation.getPressure();
                    break;
                default:
                    result = "[COMMUNICATION THREAD] Wrong information type (all / temperature / wind_speed / condition / humidity / pressure)!";
            }
            printWriter.println(result);
            printWriter.flush();*/
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } /*catch (JSONException jsonException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + jsonException.getMessage());
            if (Constants.DEBUG) {
                jsonException.printStackTrace();
            }
        }*/ finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }

}

