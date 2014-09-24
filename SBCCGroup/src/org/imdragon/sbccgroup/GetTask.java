package org.imdragon.sbccgroup;

import java.io.IOException;

import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.savagelook.android.UrlJsonAsyncTask;

import android.content.Context;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

public class GetTask extends UrlJsonAsyncTask {
    public GetTask(Context context) {
        super(context);
    }
    
    protected JSONObject doInBackground(String... urls) {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(urls[0]);
		String response = null;
		JSONObject json = new JSONObject();
		
		this.setConnectionParams(4000, 0, 0);
		
		try {
			try {
				// attach the cookies we got during signin to our GET request
				get.setHeader("Accept", "application/json");
				CookieManager cm = CookieManager.getInstance();
				CookieSyncManager.getInstance().startSync();
				CookieSyncManager.getInstance().sync();
				CookieSyncManager.getInstance().stopSync();
				// we could call .replace("request_method=POST; ", "") to remove the
				// cookie that appears to be getting added to this
				String cookies = cm.getCookie("pieapi.us.to"); 
//				Log.e("ClientProtocol", cookies);
				get.setHeader("Cookie", cookies);
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				response = client.execute(get, responseHandler);
				if (response.startsWith("["))
					response = "{\"data\":"+response+"}";
				Log.v("ClientProtocol", "JSON = "+response);
				json = new JSONObject(response);
			} catch (HttpResponseException e) {
				e.printStackTrace();
				Log.e("ClientProtocol", "" + e);
				json.put("info", "Failed.");
			} catch (IOException e) {
				e.printStackTrace();
				Log.e("IO", "" + e);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e("JSON", "" + e);
		}

		return json;
    }
}
