package com.basestructure.syncadapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.basestructure.R;
import com.basestructure.base.ApplicationClass;
import com.basestructure.image.ImageUtil;
import com.basestructure.restclient.RESTCommand;
import com.basestructure.util.AppGeneralUtils;
import com.basestructure.util.AppLoginSession;
import com.basestructure.util.AppMessage;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Provides utility methods for communicating with the server.
 */
final public class NetworkUtilities {
    /** The tag used to log to adb console. */
    private static final String TAG = "NetworkUtilities";
    /** POST parameter name for the user's account name */
    public static final String PARAM_USERNAME = "username";
    /** POST parameter name for the user's password */
    public static final String PARAM_PASSWORD = "password";
    /** POST parameter name for the user's authentication token */
    public static final String PARAM_AUTH_TOKEN = "authtoken";
    /** POST parameter name for the client's last-known sync state */
    public static final String PARAM_SYNC_STATE = "syncstate";
    /** POST parameter name for the sending client-edited contact info */
    public static final String PARAM_CONTACTS_DATA = "contacts";
    /** Timeout (in ms) we specify for each http request */
    public static final int HTTP_REQUEST_TIMEOUT_MS = 30 * 1000;

    private static NetworkUtilities m_networkUtilities=null;
    public static NetworkUtilities getInstance() {
        if(m_networkUtilities==null)
            m_networkUtilities= new NetworkUtilities();
        return m_networkUtilities;
    }

    private NetworkUtilities() {}

    /**
     * Configures the httpClient to connect to the URL provided.
     */
    public static HttpClient getHttpClient() {
        HttpClient httpClient = new DefaultHttpClient();
        final HttpParams params = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(params, HTTP_REQUEST_TIMEOUT_MS);
        HttpConnectionParams.setSoTimeout(params, HTTP_REQUEST_TIMEOUT_MS);
        ConnManagerParams.setTimeout(params, HTTP_REQUEST_TIMEOUT_MS);
        return httpClient;
    }

    public static byte[] downloadAvatar(final String avatarUrl) {
        // If there is no avatar, we're done
        if (TextUtils.isEmpty(avatarUrl)) {
            return null;
        }

        try {
            Log.i(TAG, "Downloading avatar: " + avatarUrl);
            // Request the avatar image from the server, and create a bitmap
            // object from the stream we get back.
            URL url = new URL(avatarUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            try {
                final BitmapFactory.Options options = new BitmapFactory.Options();
                final Bitmap avatar = BitmapFactory.decodeStream(connection.getInputStream(),
                        null, options);

                // Take the image we received from the server, whatever format it
                // happens to be in, and convert it to a JPEG image. Note: we're
                // not resizing the avatar - we assume that the image we get from
                // the server is a reasonable size...
                Log.i(TAG, "Converting avatar to JPEG");
                ByteArrayOutputStream convertStream = new ByteArrayOutputStream(
                        avatar.getWidth() * avatar.getHeight() * 4);
                avatar.compress(Bitmap.CompressFormat.JPEG, 95, convertStream);
                convertStream.flush();
                convertStream.close();
                // On pre-Honeycomb systems, it's important to call recycle on bitmaps
                avatar.recycle();
                return convertStream.toByteArray();
            } finally {
                connection.disconnect();
            }
        } catch (MalformedURLException muex) {
            // A bad URL - nothing we can really do about it here...
            Log.e(TAG, "Malformed avatar URL: " + avatarUrl);
        } catch (IOException ioex) {
            // If we're unable to download the avatar, it's a bummer but not the
            // end of the world. We'll try to get it next time we sync.
            Log.e(TAG, "Failed to download user avatar: " + avatarUrl);
        }
        return null;
    }

    public static Bitmap downloadImage(final String imageUrl)
    {
        if (TextUtils.isEmpty(imageUrl)) {
            return null;
        }
        try {
            Log.i(TAG, "Downloading avatar: " + imageUrl);
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            try {
                final Bitmap avatar = BitmapFactory.decodeStream(connection.getInputStream());
                return avatar;
            } finally {
                connection.disconnect();
            }
        } catch (MalformedURLException muex) {
            Log.e(TAG, "Malformed avatar URL: " + imageUrl);
        } catch (IOException ioex) {
            Log.e(TAG, "Failed to download user avatar: " + imageUrl);
        }
        return null;
    }

    public static Bitmap downloadAndSaveImage(final String
                                                      imageUrl, final String imageID, final String imageSize) {
        if (TextUtils.isEmpty(imageUrl)) {
            return null;
        }
        try {
            Log.i(TAG, "Downloading avatar: " + imageUrl);
            HttpURLConnection connection = getGetHttpURLConnection(imageUrl);
            try {
                ImageUtil.saveInputStreamToFile(connection.getInputStream(),
                        imageID, imageSize);
            } finally {
                connection.disconnect();
            }
        } catch (MalformedURLException muex) {
            Log.e(TAG, "Malformed avatar URL: " + imageUrl);
        } catch (IOException ioex) {
            Log.e(TAG, "Failed to download user avatar: " + imageUrl);
        }
        return null;
    }

    /**
     * Extend the Apache HttpPost method to implement an HttpPost
     * method.
     */
    public static class HttpPatch extends HttpPost {
        public HttpPatch(String uri) {
            super(uri);
        }

        public String getMethod() {
            return "PATCH";
        }
    }

    public static HttpPatch getHttpPatch(String strUrl) {
        StringBuilder url = new StringBuilder( strUrl );
        final HttpPatch patch = new HttpPatch( url.toString() );
        patch.addHeader(
                WebApiConstants.HEADER_ACCESS_TOKEN_PARM,
                WebApiConstants.HEADER_TOKEN_PREFIX  );
        patch.setHeader("Cookie", "JSESSIONID=" + AppLoginSession.getSessionID());
        return patch;
    }

    public String getBody(InputStream inputStream) {
        String result = "";
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(inputStream)
            );
            String inputLine;
            while ( (inputLine = in.readLine() ) != null ) {
                result += inputLine;
                result += "\n";
            }
            in.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return result;
    }


    public static HttpGet getHttpGet(String strUrl) {
        StringBuilder url = new StringBuilder( strUrl );
        final HttpGet get = new HttpGet( url.toString() );
        get.addHeader(
                WebApiConstants.HEADER_ACCESS_TOKEN_PARM,
                WebApiConstants.HEADER_TOKEN_PREFIX  );
        get.setHeader("Cookie", "JSESSIONID=" + AppLoginSession.getSessionID());
        return get;
    }

    public static HttpPut getHttpPut(String strUrl) {
        StringBuilder url = new StringBuilder( strUrl );
        final HttpPut put = new HttpPut( url.toString() );
        put.addHeader(
                WebApiConstants.HEADER_ACCESS_TOKEN_PARM,
                WebApiConstants.HEADER_TOKEN_PREFIX  );
        put.addHeader(
                WebApiConstants.HEADER_ACCEPT_TOKEN_PARM,
                WebApiConstants.HEADER_TOKEN_PREFIX  );
        put.setHeader("Cookie", "JSESSIONID=" + AppLoginSession.getSessionID());
        return put;
    }

    public static HttpPost getHttpPost(String strUrl) {
        StringBuilder url = new StringBuilder( strUrl );
        final HttpPost post = new HttpPost( url.toString() );
        post.addHeader(
                WebApiConstants.HEADER_ACCESS_TOKEN_PARM,
                WebApiConstants.HEADER_TOKEN_PREFIX  );
        post.addHeader(
                WebApiConstants.HEADER_ACCEPT_TOKEN_PARM,
                WebApiConstants.HEADER_TOKEN_PREFIX  );
        //post.setHeader("Cookie", "JSESSIONID=" + SpLoginSession.getSessionID());
        return post;
    }

    public static HttpDelete getHttpDelete(String strUrl) {
        StringBuilder url = new StringBuilder( strUrl );
        final HttpDelete delete = new HttpDelete( url.toString() );
        delete.addHeader(
                WebApiConstants.HEADER_ACCESS_TOKEN_PARM,
                WebApiConstants.HEADER_TOKEN_PREFIX  );
        //delete.setHeader("Cookie", "JSESSIONID=" + SpLoginSession.getSessionID());
        return delete;
    }

    public static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    static AsyncTask<Void, Void, AppMessage> executePostInBC;
    public static void executePostInBC ( final String url, final JSONObject patchObj ) {
        executePostInBC = new AsyncTask<Void, Void, AppMessage >() {
            @Override
            protected void onPreExecute() {
            }

            @Override
            protected AppMessage doInBackground(Void... args) {
                return getPostResponse(url, patchObj);
            }

            @Override
            protected void onPostExecute(AppMessage appMessage) {
            }
        };
        executePostInBC.execute(null, null, null);
    }

    public static AppMessage getPostResponse(String optUrl) {
        return getPostResponse(optUrl,  null);
    }

    public static AppMessage getPostResponse(String optUrl,  JSONObject dataTobeSent) {
        AppMessage response=null;
        HttpURLConnection connection = createConnection(optUrl, dataTobeSent);
        int responseCode=HttpURLConnection.HTTP_OK;
        try {
            responseCode = connection.getResponseCode();
        }
        catch (IOException io) {
            if(io !=null && io.getCause() !=null)
                return new AppMessage(AppMessage.MessageTypeEnum.ERROR,io.getCause().getLocalizedMessage());
            else
                return new AppMessage(AppMessage.MessageTypeEnum.ERROR,"Network Error");
        }
        response = parseMessage(connection);
        connection.disconnect();
        return response;
    }

    public static AppMessage parseMessage(HttpURLConnection connection) {
        String response = null;
        String errorMsg = "";
        AppMessage hxMsg = new AppMessage(AppMessage.MessageTypeEnum.ERROR, "");
        try {
            response = convertStreamToString(connection.getInputStream());
            hxMsg.setMessageType(AppMessage.MessageTypeEnum.SUCCESS);
        } catch (IOException e) {

            if (e != null && e.getCause() != null)
                errorMsg = e.getCause().getLocalizedMessage();
            else
                errorMsg = "Server Error, please try after sometime";
            hxMsg.setShortMessage(errorMsg);
            response = convertStreamToString(connection.getErrorStream());
            hxMsg.setMessageType(AppMessage.MessageTypeEnum.ERROR);
        }

        JSONObject  jObj=null;
        if(response!=null) {
            try {
                jObj = new JSONObject(response);
                try{
                    jObj=jObj.getJSONObject("message");
                }catch (JSONException e){}
                try {hxMsg.setKey(jObj.getString("key"));} catch (JSONException e) {}
                try {hxMsg.setMessageType(AppMessage.MessageTypeEnum.valueOf(jObj.getString
                        ("messageType")));} catch (JSONException e) {}
                try {hxMsg.setShortMessage(jObj.getString("message"));} catch (JSONException e) {}
                try {hxMsg.setLongMessage(jObj.getString("longMessage"));} catch (JSONException e) {}

            } catch (JSONException e) {
                hxMsg.setShortMessage(errorMsg);
            }
        }

        return hxMsg;
    }

    private static HttpURLConnection createConnection(String url, JSONObject dataTobeSent){
        HttpURLConnection connection=null;
        try {
            URL u = new URL(url);
            connection = (HttpURLConnection) u.openConnection();
            connection.setRequestMethod("POST");

           // connection.setRequestProperty("Cookie", "JSESSIONID=" + SpLoginSession.getSessionID());
            connection.setRequestProperty(WebApiConstants.HEADER_ACCESS_TOKEN_PARM,
                    "application/x-www-form-urlencoded");
            connection.setDoOutput(true);
            connection.setDoInput(true);

            if(dataTobeSent!=null){
                connection.setRequestProperty(WebApiConstants.HEADER_ACCESS_TOKEN_PARM, WebApiConstants.HEADER_TOKEN_PREFIX);
                connection.setRequestProperty(WebApiConstants.HEADER_ACCEPT_TOKEN_PARM, WebApiConstants.HEADER_TOKEN_PREFIX);
                connection.setRequestProperty("Content-Length", "" + Integer.toString
                        (dataTobeSent.toString().getBytes().length));
                connection.setRequestProperty("Content-Language", "en-US");
                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(wr, "UTF-8"));
                writer.write(dataTobeSent.toString());
                writer.close();
                //wr.writeBytes(dataTobeSent.toString());
                //wr.flush();
                wr.close();
            }

            connection.connect();
        }
        catch (IOException io) {
            io.printStackTrace();
        }
        return connection;
    }

    public static HttpURLConnection getPostHttpURLConnection(String strUrl, JSONObject postData) {
        return createConnection(strUrl,postData);
    }

    public static HttpURLConnection getPostHttpURLConnection(String strUrl) {
        return createConnection(strUrl, null);
    }

    public static HttpURLConnection getGetHttpURLConnection(String strUrl) {
        HttpURLConnection connection=null;
        try
        {
            URL url = new URL(strUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Cookie", "JSESSIONID=" + AppLoginSession.getSessionID());
            connection.setRequestProperty(WebApiConstants.HEADER_ACCESS_TOKEN_PARM,
                    "application/x-www-form-urlencoded");
            connection.connect();
        }
        catch (IOException io) {
            io.printStackTrace();
        }
        return connection;
    }

    public static String getResponse(String desiredUrl, JSONObject dataToBeSent)
    {
        BufferedReader reader = null;
        StringBuilder stringBuilder;
        String response="";
        try
        {
            URL url = new URL(desiredUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            //connection.setRequestProperty("Cookie", "JSESSIONID=" + SpLoginSession.getSessionID());
            connection.setRequestProperty(WebApiConstants.HEADER_ACCESS_TOKEN_PARM,
                    "application/x-www-form-urlencoded");

            if(dataToBeSent!=null){
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestProperty(WebApiConstants.HEADER_ACCESS_TOKEN_PARM, WebApiConstants.HEADER_TOKEN_PREFIX);
                connection.setRequestProperty(WebApiConstants.HEADER_ACCEPT_TOKEN_PARM, WebApiConstants.HEADER_TOKEN_PREFIX);
                connection.setRequestProperty("Content-Length", "" + Integer.toString
                        (dataToBeSent.toString().getBytes().length));
                connection.setRequestProperty("Content-Language", "en-US");
                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes(dataToBeSent.toString());
                wr.flush();
                wr.close();
            }

            connection.connect();
            int responseCode= connection.getResponseCode();
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            stringBuilder = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null)
            {
                stringBuilder.append(line + "\n");
            }
            connection.disconnect();

            response= stringBuilder.toString();

        }
        catch (Exception e){
            AppGeneralUtils.printError(TAG, e.toString());
        }
        finally
        {
            if (reader != null)
            {
                try
                {
                    reader.close();
                }
                catch (IOException ioe){
                    AppGeneralUtils.printError(TAG, ioe.toString());

                }
            }
        }
        return response;
    }

    public static String getResponse(String desiredUrl) {
        return getResponse(desiredUrl, null);
    }

    public static JSONObject getGetResponse(String desiredUrl)
    {
        String  response = getResponse( desiredUrl);
        JSONObject  jObj=null;
        if(StringUtils.isNotEmpty(response)) {
            try {
                jObj = new JSONObject(response);
            } catch (JSONException e) {
                AppGeneralUtils.printError(TAG, e.toString());
            }
        }
        return jObj;
    }


    public static JSONArray getGetArrayResponse(String desiredUrl, JSONObject dataToBeSent)
    {
        String  response = getResponse( desiredUrl, dataToBeSent);
        JSONArray  jObj=null;
        if(StringUtils.isNotEmpty(response)) {
            try {
                jObj = new JSONArray(response);
            } catch (JSONException e) {
                AppGeneralUtils.printError(TAG, e.toString());
            }
        }
        return jObj;
    }

    public static JSONArray getGetArrayResponse(String desiredUrl)
    {
        String  response = getResponse( desiredUrl);
        JSONArray  jObj=null;
        if(StringUtils.isNotEmpty(response)) {
            try {
                jObj = new JSONArray(response);
            } catch (JSONException e) {
                AppGeneralUtils.printError(TAG, e.toString());
            }
        }
        return jObj;
    }

    public static AppMessage executePostResponse(String link, JSONObject data) {
        AppMessage etMessage = new AppMessage(AppMessage.MessageTypeEnum.ERROR, "failed");
        JSONObject jsonObject = new JSONObject();

        Integer statusCode = HttpStatus.SC_BAD_REQUEST;
        HttpResponse response=null;
        HttpPost httpPost = NetworkUtilities.getHttpPost(link);

        try
        {
            StringEntity body = new StringEntity(data.toString(), "UTF-8");
            body.setContentType("application/json");
            httpPost.setEntity(body);
        }
        catch (Exception e) {
            etMessage.setLongMessage(e.toString());
            AppGeneralUtils.printError(TAG, e.toString());
        }

        RESTCommand.createHttpClient();
        try {
            response = RESTCommand.mHttpClient.execute(httpPost);
        } catch (IOException e) {
            etMessage.setLongMessage(e.toString());
            AppGeneralUtils.printError(TAG, e.toString());
        }

        if(response!=null && response.getStatusLine() !=null)
            statusCode = response.getStatusLine().getStatusCode();
        else
            return etMessage;

        try {
            if(response.getEntity() !=null) {
                jsonObject = new JSONObject(EntityUtils.toString(response.getEntity()));
                response.getEntity().consumeContent();
            }
        } catch ( IOException | JSONException e ) {
            etMessage.setLongMessage(e.toString());
            AppGeneralUtils.printError(TAG, e.toString());
        }

        try {
            jsonObject.putOpt("responseCode", statusCode);
        } catch (JSONException e) {
            etMessage.setLongMessage(e.toString());
            AppGeneralUtils.printError(TAG, e.toString());
        }

        statusCode = jsonObject.optInt("responseCode");
        if( statusCode.equals(HttpStatus.SC_NO_CONTENT)||statusCode.equals(HttpStatus.SC_CREATED)||
                statusCode.equals(HttpStatus.SC_OK))
            etMessage.setMessageType(AppMessage.MessageTypeEnum.SUCCESS);
        etMessage.setResponse(jsonObject);
        return etMessage;
    }

    static AsyncTask<Void, Void, AppMessage> executePatchInBC;
    public static void executePatchInBC ( final String url, final JSONObject patchObj ) {
        executePatchInBC = new AsyncTask<Void, Void, AppMessage >() {
            @Override
            protected void onPreExecute() {
            }

            @Override
            protected AppMessage doInBackground(Void... args) {
                try {
                    return executePatchCommand(url, patchObj);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(AppMessage appMessage) {
            }
        };
        executePatchInBC.execute(null, null, null);
    }

    public static AppMessage executePatchCommand(String url, JSONObject patchObj){
        AppMessage etMessage = new AppMessage(AppMessage.MessageTypeEnum.ERROR, "failed");
        Integer statusCode = 404;
        HttpResponse response = null;
        HttpPatch httpPatch = getHttpPatch(url);

        try {
            StringEntity body = new StringEntity(patchObj.toString(), "UTF-8");
            body.setContentType("application/json");
            httpPatch.setEntity(body);
        }
        catch (Exception e) {
            etMessage.setLongMessage(e.toString());
            AppGeneralUtils.printError(TAG, e.toString());
        }

        RESTCommand.createHttpClient();
        try {
            response = RESTCommand.mHttpClient.execute(httpPatch);
        } catch (IOException e) {
            AppGeneralUtils.printError("Patch", e.toString());
        }

        if(response!=null && response.getStatusLine() !=null)
            statusCode = response.getStatusLine().getStatusCode();
        else
            return etMessage;

        try {
            if (response.getEntity() != null)
                response.getEntity().consumeContent();
        } catch (IOException e) {
            AppGeneralUtils.printError("Patch", e.toString());
        }
        if( statusCode.equals(HttpStatus.SC_NO_CONTENT)||statusCode.equals(HttpStatus.SC_CREATED)||
                statusCode.equals(HttpStatus.SC_OK))
            etMessage.setMessageType(AppMessage.MessageTypeEnum.SUCCESS);
        return etMessage;
    }

    public AppMessage executeDeleteCommand( String url)
    {
        Integer statusCode = HttpStatus.SC_BAD_REQUEST;
        HttpResponse response=null;
        AppMessage etMessage = new AppMessage(AppMessage.MessageTypeEnum.ERROR, "failed");

        RESTCommand.createHttpClient();

        HttpDelete httpDelete = getHttpDelete(url);
        try {
            response = RESTCommand.mHttpClient.execute(httpDelete);
        }
        catch (IOException e) {
            String msg = "Delete method failed: Cannot connect to network.";
            Log.i(TAG, msg, e);
        }

        if(response!=null && response.getStatusLine() !=null)
            statusCode = response.getStatusLine().getStatusCode();
        else
            return etMessage;

        try {
            if(response.getEntity() !=null)
                response.getEntity().consumeContent();
        } catch ( IOException e ) {
            String msg = "Delete method executed: No content.";
            Log.e(TAG, msg, e);
        }
        if ( Log.isLoggable( TAG, Log.INFO ) ) {
            Log.i( TAG, "HTTP statusCode[" + statusCode + "]" );
        }
        if( statusCode.equals(HttpStatus.SC_NO_CONTENT)||statusCode.equals(HttpStatus.SC_CREATED)||
                statusCode.equals(HttpStatus.SC_OK))
            etMessage.setMessageType(AppMessage.MessageTypeEnum.SUCCESS);
        return etMessage;
    }

    public static boolean isNetWorkAvailable(boolean showMessage) {
        ConnectivityManager connMgr = (ConnectivityManager) ApplicationClass.getAppContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else if(showMessage){
            Toast.makeText(ApplicationClass.getAppContext(),
                    ApplicationClass.getAppContext().getString(R.string.hint_networkError),
                    Toast.LENGTH_LONG).show();
        }
        return false;
    }
}
