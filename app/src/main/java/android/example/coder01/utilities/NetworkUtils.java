package android.example.coder01.utilities;

import android.net.Uri;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class NetworkUtils {

    // Base URL of the server
    final static String SERVER_BASE_URL = "https://ide-api.sudipto1.repl.co/api";

    // Check status of server
    final static String STATUS_CHECK = SERVER_BASE_URL.concat("/status");

    // Run code by sending it to server
    final static String RUN_CODE = SERVER_BASE_URL.concat("/submit");

    // Sample JSON response for a code run query
    private static String JSON_RESPONSE;

    /**
     * Builds the URL used to query Server.
     */
    public static URL buildUrl(int task) {
        Uri builtStatusCheckUri = Uri.parse(STATUS_CHECK).buildUpon()
                .build();

        Uri builtRunCodeUri = Uri.parse(RUN_CODE).buildUpon()
                .build();

        Uri builtUri = null;
        switch (task) {
            case 1:
                builtUri = builtStatusCheckUri;
                break;
            case 2:
                builtUri = builtRunCodeUri;
                break;
        }

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * Get the response by querying the status API
     *
     * @param url Query URL where to send the POST
     * @return JSON response as String
     */
    public static int getStatusResponse(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setDoOutput(true);
        try {
            InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());

            String response = convertInputStreamToString(inputStream);
            return Integer.parseInt(response);
        } finally {
            urlConnection.disconnect();
        }
    }

    /**
     * Return the code output from the json response
     */
    public static String extractOutput(String jsonResponse) {
        // Save the response
        JSON_RESPONSE = jsonResponse;

        String output = "Fatal error.";
        try {
            JSONObject strJsonObject = new JSONObject(jsonResponse);

            String strId = strJsonObject.getString("id");
            int id = Integer.parseInt(strId);
            output = strJsonObject.getString("output");
            return output;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return output;
    }

    /**
     * Get the response by querying the submit API
     *
     * @param url Query URL where to send the POST submit
     * @return JSON response as String
     */
    public static String getRunCodeResponse(URL url, JSONObject postData) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setDoOutput(true);
        try {

            // Send the post body
            if (postData != null) {
                OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                writer.write(postData.toString());
                writer.flush();
            }


            // Get the response input stream
            InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());

            // Convert response to string response and return
            String response = convertInputStreamToString(inputStream);
            return response;
        } finally {
            urlConnection.disconnect();
        }
    }

    /**
     * Get the input stream and convert to string
     */
    private static String convertInputStreamToString(InputStream inputStream) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
