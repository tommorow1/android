package com.example.bloold.buildp.profile;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bloold.buildp.common.Settings;
import com.example.bloold.buildp.ui.AgreementActivity;
import com.example.bloold.buildp.ui.MainActivity;
import com.example.bloold.buildp.R;
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

public class RegistrationActivity extends AppCompatActivity {

    TextView etRegAgreement;
    EditText etRegEmail;
    EditText etRegPassword;
    EditText etFirstName;
    EditText etLastName;
    private Toolbar toolbar;
    String EmailReg;
    String RegPassword;
    String FirstName;
    String LastName;
    CallbackManager callbackManager;
    String AuthTokenresp = null;

    private static final String AuthToken = "AuthToken";
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
        setContentView(R.layout.activity_registration);

        etRegAgreement = (TextView)findViewById(R.id.etRegAgreement) ;
        etRegEmail = (EditText)findViewById(R.id.etRegEmail);
        etRegPassword = (EditText)findViewById(R.id.etRegPassword);
        etFirstName = (EditText)findViewById(R.id.etFirstName);
        etLastName = (EditText)findViewById(R.id.etLastName);
        final CheckBox cbAgr = (CheckBox)findViewById(R.id.cbAgr);
        final Button btnReg = (Button)findViewById(R.id.btnReg);

        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);


        etRegAgreement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationActivity.this, AgreementActivity.class);
                startActivity(intent);
            }
        });

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etRegEmail.getText().toString().equals("") || etRegPassword.getText().toString().equals("") || etFirstName.getText().toString().equals("") || etLastName.getText().toString().equals("")) {
                    Toast.makeText(RegistrationActivity.this, "Не все поля заполнены", Toast.LENGTH_SHORT).show();
                }else{
                    EmailReg = etRegEmail.getText().toString();
                    RegPassword = etRegPassword.getText().toString();
                    FirstName = etFirstName.getText().toString();
                    LastName = etLastName.getText().toString();

                    new RegistrationActivity.JSONReg().execute("http://ruinnet.idefa.ru/api_app/user/registration/");
                }

            }
        });

        cbAgr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    btnReg.setEnabled(true);
                } else {
                    btnReg.setEnabled(false);
                }
            }
        });

        final int[] num = {2};
        final ImageView ivRegSeePassword = (ImageView)findViewById(R.id.ivRegSeePassword);
        ivRegSeePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( num[0] % 2 == 0 ){
                    etRegPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
                    etRegPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                num[0] = num[0] +1;
            }
        });
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            //getSupportActionBar().setTitle("Регистрация");
            TextView tvTitle = toolbar.findViewById(R.id.tvTitle);
            tvTitle.setText("Регистрация");
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
        ivOk.setOnClickListener(new RegistrationActivity.LoginClickListener(OkAuthType.ANY));
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
        final ImageView ivVk = (ImageView)findViewById(R.id.ivVk);
        ivVk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VKSdk.login(RegistrationActivity.this, scope);
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
            odnoklassniki.requestAuthorization(RegistrationActivity.this, REDIRECT_URL, authType, OkScope.VALUABLE_ACCESS);
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
        client.addHeader("Device-Id",Settings.INSTANCE.getUdid());

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
                        Intent intent = new Intent(RegistrationActivity.this,MainActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(RegistrationActivity.this, "Ошибка авторизации", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(RegistrationActivity.this, "Ошибка авторизации", Toast.LENGTH_LONG).show();
                }

            }

        });
    }

    public class  JSONReg extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            String output = "";
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                String urlParameters = "EMAIL=" + EmailReg + "&PASSWORD=" + RegPassword + "&FIRST_NAME=" + FirstName + "&LAST_NAME=" + LastName + "&AGREEMENT=Y";
                connection.setRequestMethod("POST");
                connection.setRequestProperty("USER-AGENT", "Mozilla/5.0");
                connection.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");
                connection.setRequestProperty("Device-Id", Settings.INSTANCE.getUdid());
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
                while ((line = br.readLine()) != null) {
                    responseOutput.append(line);
                }
                br.close();

                output = responseOutput.toString();
                return output;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
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
            if (response !=null) {
                String code = null;
                try {
                    code = response.getString("CODE");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (code.equals("200")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
                    builder.setTitle("Информационное сообщение!")
                            .setMessage("Вам отправлено письмо с ссылкой для создания нового пароля. Если Вы не получили письмо, отправьте запрос повторно или проверьте папку «Спам» Вашего почтового клиента.")
                            //.setIcon(R.drawable.ic_android)
                            .setCancelable(false)
                            .setNegativeButton("Ok",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            Intent intent = new Intent(RegistrationActivity.this,MainActivity.class);
                                            startActivity(intent);
                                        }
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();

                } else {
                    Toast.makeText(RegistrationActivity.this, "Ошибка при регистрация.", Toast.LENGTH_LONG).show();
                }
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
