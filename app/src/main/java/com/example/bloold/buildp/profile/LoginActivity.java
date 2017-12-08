package com.example.bloold.buildp.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bloold.buildp.R;
import com.example.bloold.buildp.common.Settings;
import com.example.bloold.buildp.ui.MainActivity;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import cz.msebera.android.httpclient.Header;
import ru.ok.android.sdk.Odnoklassniki;
import ru.ok.android.sdk.OkAuthListener;
import ru.ok.android.sdk.util.OkAuthType;
import ru.ok.android.sdk.util.OkScope;

public class LoginActivity extends AppCompatActivity {
    private Toolbar toolbar;
    CallbackManager callbackManager;
    EditText etEmail, etPassword;

    String Email = "";
    String Password = "";
    String AuthTokenresp = null;

    public static final String AuthToken = "AuthToken";
    private String[] scope = new String[]{
            VKScope.AUDIO,
            VKScope.FRIENDS,
            VKScope.WALL
    };
    protected static final String APP_ID = "1256634880";
    protected static final String APP_KEY = "D126C3FC68EA3E9A7F54197C";
    protected static final String REDIRECT_URL = "ok1256634880://authorize";
    protected Odnoklassniki odnoklassniki;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
           // getSupportActionBar().setTitle("Авторизация");
            TextView tvTitle = toolbar.findViewById(R.id.tvTitle);
            tvTitle.setText("Авторизация");
            tvTitle.setVisibility(View.VISIBLE);
        }
        final LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        ImageView ivFb = (ImageView)findViewById(R.id.ivFb);
        ivFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginButton.performClick();
            }
        });
        ImageView ivOk = (ImageView) findViewById(R.id.ivOk);
        ivOk.setOnClickListener(new LoginClickListener(OkAuthType.ANY));
        odnoklassniki = Odnoklassniki.createInstance(this, APP_ID, APP_KEY);


        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });

        loginButton.setReadPermissions("email");
        // If using in a fragment

        // Other app specific specialization

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
            new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    SocAuth("fb", loginResult.getAccessToken().getToken());
                }

                @Override
                public void onCancel() {
                    // App code
                }

                @Override
                public void onError(FacebookException exception) {
                    // App code
                }
            });

        etEmail = (EditText)findViewById(R.id.etEmail);
        etPassword = (EditText)findViewById(R.id.etPassword);
        Button btnOk = (Button)findViewById(R.id.btnOk);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etEmail.getText().toString().equals("") || etPassword.getText().toString().equals("")) {
                    Toast.makeText(LoginActivity.this, "Не все поля заполнены", Toast.LENGTH_SHORT).show();
                }else{
                    Email =  etEmail.getText().toString();
                    Password = etPassword.getText().toString();
                    new LoginActivity.JSONLogging().execute("http://ruinnet.idefa.ru/api_app/user/auth/email/");
                }

            }
        });
        final ImageView ivVk = (ImageView)findViewById(R.id.ivVk);
        ivVk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VKSdk.login(LoginActivity.this, scope);
                VKRequest request = VKApi.users().get();
                request.executeWithListener(new VKRequest.VKRequestListener() {

                    @Override
                    public void onComplete(VKResponse response) {
                        Log.d("response",response.toString());
                        // Здесь обрабатываем полученный response.
                    }

                    @Override
                    public void onError(VKError error) {
                        // Ошибка. Сообщаем пользователю об error.
                    }
                    @Override
                    public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                        // Неудачная попытка. В аргументах имеется номер попытки и общее их количество.
                    }

                });
            }
        });

        final int[] num = {2};
        final ImageView seePassword = (ImageView)findViewById(R.id.seePassword);
        seePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( num[0] % 2 == 0 ){
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                num[0] = num[0] +1;
            }
        });

        final TextView tvReg = (TextView) findViewById(R.id.tvReg);
        tvReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegistrationActivity.class);
                startActivity(intent);
            }
        });

        final TextView tvRestPas = (TextView) findViewById(R.id.tvRestPas);
        tvRestPas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RestoreActivity.class);
                startActivity(intent);
            }
        });

    }


    private OkAuthListener getAuthListener() {
        return new OkAuthListener() {
            @Override
            public void onSuccess(final JSONObject json) {
                try {
                    String acceess_token = json.getString("access_token");
                    SocAuth("ok",acceess_token);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {

            }

            @Override
            public void onCancel(String error) {

            }
        };
    }

    protected class LoginClickListener implements View.OnClickListener {
        private OkAuthType authType;

        public LoginClickListener(OkAuthType authType) {
            this.authType = authType;
        }

        @Override
        public void onClick(final View view) {
            odnoklassniki.requestAuthorization(LoginActivity.this, REDIRECT_URL, authType, OkScope.VALUABLE_ACCESS);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (Odnoklassniki.getInstance().isActivityRequestOAuth(requestCode)) {
            Odnoklassniki.getInstance().onAuthActivityResult(requestCode, resultCode, data, getAuthListener());
        }
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                SocAuth("vk",res.accessToken);
            }
            @Override
            public void onError(VKError error) {
// Произошла ошибка авторизации (например, пользователь запретил авторизацию)
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);

        }
    }

    private void SocAuth(String s, String k)
    {   AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://ruinnet.idefa.ru/api_app/user/auth/social/"+s.toString()+"/";

        RequestParams params = new RequestParams();
        params.put("TOKEN", k.toString());


        client.setBasicAuth("defa","defa");
        client.addHeader("Device-Id", Settings.INSTANCE.getUdid());

        final Context context = this;
        client.post(url, params, new TextHttpResponseHandler() {

            @Override
            public void onStart() {
                // Initiated the request
            }

            @Override
            public void onFinish() {
                // Completed the request (either success or failure)
            }
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                JSONObject response = null;
                try {
                    response = new JSONObject(responseString);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String image = null;
                String code = null;
                JSONObject data = null;

                if (response !=null){

                    try {
                        code = response.getString("CODE");

                        if(code.equals("200")) {
                            AuthTokenresp = response.getString("AUTH_TOKEN");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(code.equals("200")){
                        SaveToken(AuthTokenresp);
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);
                        finishAffinity();
                    }else{
                        Toast.makeText(LoginActivity.this, "Ошибка авторизации", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(LoginActivity.this, "Ошибка авторизации", Toast.LENGTH_LONG).show();
                }

            }
        });
    }
    public class  JSONLogging extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            String output ="";
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                String urlParameters =  "EMAIL="+Email+"&PASSWORD=" + Password;
                //String urlParameters =  "EMAIL=viktor.kolotiy@defa.ru&PASSWORD=815406";
                connection.setRequestMethod("POST");
                connection.setRequestProperty("USER-AGENT","Mozilla/5.0");
                connection.setRequestProperty("ACCEPT-LANGUAGE","en-US,en;0.5");
                connection.setRequestProperty("Device-Id",Settings.INSTANCE.getUdid());
                String header = "Basic " + new String(android.util.Base64.encode("defa:defa".getBytes(), android.util.Base64.NO_WRAP));
                connection.addRequestProperty("Authorization", header);
                connection.setDoOutput(true);
                DataOutputStream dStream = new DataOutputStream(connection.getOutputStream());
                dStream.writeBytes(urlParameters);
                dStream.flush();
                dStream.close();

                //connection.connect();
                int responseCode = connection.getResponseCode();

                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = "";
                StringBuilder responseOutput = new StringBuilder();
                while((line = br.readLine()) != null ) {
                    responseOutput.append(line);
                }
                br.close();

                output = responseOutput.toString();

                return output;

            }catch (MalformedURLException e){
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return output;

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String finalJson = result.toString();
            JSONObject response = null;
            try {
                response = new JSONObject(result);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (response !=null){

                String code = null;
                try {
                    code = response.getString("CODE");

                    if(code.equals("200")) {
                        AuthTokenresp = response.getString("AUTH_TOKEN");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(code.equals("200")){
                    SaveToken(AuthTokenresp);

                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    finishAffinity();
                }else{
                    Toast.makeText(LoginActivity.this, "Ошибка авторизации", Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(LoginActivity.this, "Ошибка авторизации", Toast.LENGTH_LONG).show();
            }
        }
    }
    public void SaveToken(String s) {
        SharedPreferences sPref = getSharedPreferences("main", MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(AuthToken, s.toString());
        ed.commit();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
