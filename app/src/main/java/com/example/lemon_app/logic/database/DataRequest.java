package com.example.lemon_app.logic.database;


import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

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

    // endregion


}
