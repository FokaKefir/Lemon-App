package com.example.lemon_app.logic.database;


import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RegisterRequest extends StringRequest {

    // region 0. Constants

    // TODO Change the URL
    private static final String REGISTER_REQUEST_URL = "http://192.168.1.3/lemon_app/register.php";

    // endregion

    // region 1. Decl and Init

    private Map<String, String> params;

    // endregion

    // region 2. Constructor

    public RegisterRequest(String name, String password, String email, String image, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, REGISTER_REQUEST_URL, listener, errorListener);

        this.params = new HashMap<>();
        this.params.put("name", name);
        this.params.put("password", password);
        this.params.put("email", email);
        this.params.put("image", image);
    }

    // endregion

    // region 3. Getters and Setters

    @Override
    protected Map<String, String> getParams() {
        return this.params;
    }

    // endregion


}
