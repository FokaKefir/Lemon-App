package com.example.lemon_app.database;

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.lemon_app.constants.Constants.LOGIN_REQUEST_URL;
import static com.example.lemon_app.constants.Constants.REGISTER_REQUEST_URL;

public class DatabaseManager {

    // region 1. Login manager

    public static class LoginManager implements Response.Listener<String>, Response.ErrorListener {
        private OnResponseListener onResponseListener;
        private Context context;

        public LoginManager(OnResponseListener onResponseListener, Context context) {
            this.onResponseListener = onResponseListener;
            this.context = context;
        }

        public void login(String name, String password) {
            Map<String, String> params = new HashMap<>();
            params.put("name", name);
            params.put("password", password);
            DataRequest dataRequest = new DataRequest(params, LOGIN_REQUEST_URL, this, this);
            Volley.newRequestQueue(this.context).add(dataRequest);
        }

        @Override
        public void onResponse(String response) {
            try {
                JSONObject jsonResponse = new JSONObject(response);
                boolean success = jsonResponse.getBoolean("success");

                if (success){
                    int userId = jsonResponse.getInt("id");
                    this.onResponseListener.onSuccessfulLoginResponse(userId);

                } else {
                    String errorMessage = jsonResponse.getString("message");
                    this.onResponseListener.onFailedLoginResponse(errorMessage);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            this.onResponseListener.onErrorResponse(error);
        }

        public interface OnResponseListener {
            void onSuccessfulLoginResponse(int userId);
            void onFailedLoginResponse(String errorMessage);
            void onErrorResponse(VolleyError error);
        }
    }

    // endregion

    // region 2. Register manager

    public static class RegisterManager implements  Response.Listener<String>, Response.ErrorListener {

        private OnResponseListener onResponseListener;
        private Context context;

        public RegisterManager(OnResponseListener onResponseListener, Context context) {
            this.onResponseListener = onResponseListener;
            this.context = context;
        }

        public void register(String name, String password, String email, String image) {
            Map<String, String> params = new HashMap<>();
            params.put("name", name);
            params.put("password", password);
            params.put("email", email);
            params.put("image", image);
            DataRequest dataRequest = new DataRequest(params, REGISTER_REQUEST_URL, this, this);
            Volley.newRequestQueue(this.context).add(dataRequest);
        }

        @Override
        public void onResponse(String response) {
            try {
                JSONObject jsonResponse = new JSONObject(response);
                boolean success = jsonResponse.getBoolean("success");

                if (success) {
                    this.onResponseListener.onSuccessfulRegisterResponse();
                } else {
                    String errorMessage = jsonResponse.getString("message");
                    this.onResponseListener.onFailedRegisterResponse(errorMessage);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onErrorResponse(VolleyError error) {
            this.onResponseListener.onErrorResponse(error);
        }

        public interface OnResponseListener{
            void onSuccessfulRegisterResponse();
            void onFailedRegisterResponse(String errorMessage);
            void onErrorResponse(VolleyError error);
        }
    }

    // endregion
}
