package com.example.bloold.buildp.profile;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bloold.buildp.common.Settings;
import com.example.bloold.buildp.ui.MainActivity;
import com.example.bloold.buildp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RestoreActivity extends AppCompatActivity {
    private Toolbar toolbar;
    EditText etRestEmail;
    String RestEmail = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restore);
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        Button btnRest = (Button)findViewById(R.id.btnRest);
        etRestEmail = (EditText)findViewById(R.id.etRestEmail);
        btnRest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(etRestEmail.getText().toString().equals("")) {
                    Toast.makeText(RestoreActivity.this, "Не все поля заполнены", Toast.LENGTH_SHORT).show();
                }else{
                    RestEmail =  etRestEmail.getText().toString();
                    new JSONRestore().execute("http://ruinnet.idefa.ru/api_app/user/forgot-password/");
                }

            }
        });
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            TextView tvTitle = toolbar.findViewById(R.id.tvTitle);
            tvTitle.setText("Восстановление пароля");
            tvTitle.setVisibility(View.VISIBLE);
          //  getSupportActionBar().setTitle("Восстановление пароля");
        }
    }
    public class  JSONRestore extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            String output ="";
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                String urlParameters =  "EMAIL="+RestEmail;
                connection.setRequestMethod("POST");
                connection.setRequestProperty("USER-AGENT","Mozilla/5.0");
                connection.setRequestProperty("ACCEPT-LANGUAGE","en-US,en;0.5");
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

                    AlertDialog.Builder builder = new AlertDialog.Builder(RestoreActivity.this);
                    builder.setTitle("Информационное сообщение!")
                            .setMessage("Вам было отправлено письмо со ссылкой для подтверждения регистрации. Если Вы не получили письмо, отправьте запрос повторно или проверьте папку «Спам» Вашего почтового клиента.")
                            //.setIcon(R.drawable.ic_android)
                            .setCancelable(false)
                            .setNegativeButton("Ok",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            Intent intent = new Intent(RestoreActivity.this,MainActivity.class);
                                            startActivity(intent);
                                        }
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    Toast.makeText(RestoreActivity.this, "Ошибка при восстановлении пароля.", Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(RestoreActivity.this, "Ошибка при восстановлении пароля.", Toast.LENGTH_LONG).show();
            }
        }
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
