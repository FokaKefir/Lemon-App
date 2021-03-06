package com.example.lemon_app.database;


import android.content.SharedPreferences;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.example.lemon_app.constants.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import static com.example.lemon_app.constants.Constants.TOKEN_SECRET;

public class DataRequest extends StringRequest {

    // region 1. Decl and Init

    private Map<String, String> params;

    // endregion

    // region 2. Constructor

    public DataRequest(Map<String, String> params, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, url, listener, errorListener);

        this.params = params;
    }

    // endregion

    // region 3. Getters and Setters

    @Override
    protected Map<String, String> getParams() {
        return this.params;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, Object> tokenHeader = new HashMap<>();

        tokenHeader.put("alg", "HS256");
        tokenHeader.put("typ", "JWT");

//        String token = Jwts.builder()
//                .claim("header", tokenHeader).claim("params", this.params)
//                .signWith(SignatureAlgorithm.HS256, TOKEN_SECRET)
//                .compact();
//        .setPayload(payload)

        String token = Jwts.builder()
                .setHeader(tokenHeader)
                .claim("password", "Szia12")
                .claim("name", "FokaKefir")
                .signWith(SignatureAlgorithm.HS256, TOKEN_SECRET)
                .compact();

        Map<String, String> headers = new HashMap<>();
        headers.put("token", token);

        return headers;

    }

    // endregion


}
