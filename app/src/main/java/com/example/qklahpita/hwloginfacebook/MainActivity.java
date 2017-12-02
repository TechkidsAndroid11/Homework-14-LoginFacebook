package com.example.qklahpita.hwloginfacebook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private CallbackManager callbackManager;
    private boolean isLogged;

    private ImageView ivAva;
    private TextView tvName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivAva = findViewById(R.id.iv_ava);
        tvName = findViewById(R.id.tv_name);

        if (checkIfLogged()) {
            loadDataFromFacebookUser();
        } else {
            setupLoginCallback();
        }
    }

    private void setupLoginCallback() {
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "onSuccess: " + loginResult.getAccessToken().toString());
                loadDataFromFacebookUser();
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "onCancel: ");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "onError: " + error.toString());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private boolean checkIfLogged() {
        if (AccessToken.getCurrentAccessToken() != null) {
            return true;
        }
        return false;
    }

    private void loadDataFromFacebookUser() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        try {
                            Picasso.with(MainActivity.this)
                                    .load(response.getJSONObject().getJSONObject("picture")
                                            .getJSONObject("data").getString("url"))
                                    .into(ivAva);

                            tvName.setText(response.getJSONObject().getString("name"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,picture.width(500).height(500)");
        request.setParameters(parameters);
        request.executeAsync();

    }
}
