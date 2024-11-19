package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Unos extends AppCompatActivity {

    Button backImg;
    SeekBar seekbar;

    private static String url2 = "";
    private static String masterkey = "";
    private static String acceskey = "";
    private String resultInString = "";

    String jsonRezultat = "";
    String ime_koje_dodaje = "";

    ProgressDialog pd;


    public static int maxJela = 1;

    EditText ime_jela;
    TextView slicnost;
    Button btn_dodaj_json;
    Button btn_provjera;

    public static int moze_dodaj = 0;
    public static int prilog_ili_jelo = -1; //jelo = 0, prilog = 1


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dodaj_jelo_prilog);

        new JsonTask().execute(url2);

        ime_jela = findViewById(R.id.ime_jela);

        backImg = findViewById(R.id.backImg);
        btn_dodaj_json = findViewById(R.id.btn_dodaj_u_json);
        slicnost = findViewById(R.id.text_slicnost);
        btn_provjera = findViewById(R.id.btn_provjeri);

        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Unos.this, ScrollingActivity.class);
                startActivity(intent);
            }
        });

        btn_provjera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text1 = ime_jela.getText().toString();
                if(text1.isEmpty() || text1.equals(" ")){

                }else{
                    provjeriIme();
                }

            }
        });


        btn_dodaj_json.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text1 = ime_jela.getText().toString();
                if(moze_dodaj == 1){
                    try {
                        new MyTask().execute(url2);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println(e);
                    }
                }
            }
        });
    }


    private void provjeriIme(){
        try{
            JSONObject mainObject = new JSONObject(jsonRezultat);

            int max = maxJela, min = 1;
            int slicnih = 0;
            int strogoSlican = 0;
            JSONObject uniObject = mainObject.getJSONObject("record");
            for(int i = min; i <= max; i++){
                String ime = "";
                JSONObject uniObject2 = uniObject.getJSONObject(""+i);
                ime = uniObject2.getString("ime");


                if(ime.contains(ime_jela.getText().toString()) || ime_jela.getText().toString().contains(ime)){
                    if(slicnih == 0){
                        slicnost.setText("Slicni: ");
                    }
                    slicnih++;
                    slicnost.setText(slicnost.getText()+" " +ime +",");
                }

                if(ime.equals(ime_jela.getText().toString())){
                    strogoSlican++;
                }
            }

            if(strogoSlican >= 1){
                slicnost.setText("Već je upisana ova hrana.");
                moze_dodaj = 0;
            }else{
                if(slicnih == 0){
                    slicnost.setText("Nema slicnih. Sada se može stisnuti dodaj.");
                    moze_dodaj = 1;
                    ime_koje_dodaje = ime_jela.getText().toString();
                }else{
                    int zarez = slicnost.getText().toString().lastIndexOf(',');
                    String temp = slicnost.getText().toString();

                    temp = temp.substring(0,zarez);
                    slicnost.setText(temp);

                    slicnost.setText(slicnost.getText()+". Ako nije isto, može se dalje.");
                    ime_koje_dodaje = ime_jela.getText().toString();

                    moze_dodaj = 1;
                }
            }




        } catch (Exception e) {
            e.printStackTrace();
        }

    }










    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(Unos.this);
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

                return buffer.toString();


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
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd.isShowing()) {
                pd.dismiss();
            }

            resultInString = result;
            jsonRezultat = resultInString;
            //JSONObject mainObject;
            try {
                JSONObject mainObject = new JSONObject(jsonRezultat);

                JSONObject uniObject = mainObject.getJSONObject("record");
                maxJela = uniObject.length();
                //txtJson.setText("Success!!");

            } catch (Exception e) {
                e.printStackTrace();
            }






        }

    }

    public String doSomeWork(String str1){

        return str1;
    }



    private class MyTask
            extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params)
        {
            String url = params[0];
            return doSomeWork(url);
        }

        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
            // do something with the result
            URL object = null;
            HttpURLConnection con = null;
            if(moze_dodaj == 1){
                try{
                    StrictMode.ThreadPolicy gfgPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(gfgPolicy);
                    object=new URL(url2);

                    System.out.println("uso sam");
                    JSONObject mainObject = new JSONObject(jsonRezultat);

                    JSONObject uniObject = mainObject.getJSONObject("record");
                    maxJela = uniObject.length();

                    con = (HttpURLConnection) object.openConnection();
                    con.setDoOutput(true);
                    con.setRequestProperty("Content-Type", "application/json");
                    con.addRequestProperty("X-Master-Key", ""+masterkey);
                    con.setRequestMethod("PUT");

                    JSONObject id   = new JSONObject();



                    id.put("id","jelo");



                    id.put("ime", ""+ime_koje_dodaje);

                    uniObject.put(""+(maxJela+1), id);
                    //mainObject.put("record", uniObject);

                    OutputStreamWriter wr= new OutputStreamWriter(con.getOutputStream());
                    wr.write(uniObject.toString());
                    wr.flush();

                    con.getResponseCode();

                    /*OutputStream os = con.getOutputStream();
                    os.write(uniObject.toString().getBytes("UTF-8"));
                    os.close();*/

                    moze_dodaj = 0;

                    Toast toast=Toast.makeText(getApplicationContext(),"Uspješno dodano!",Toast.LENGTH_SHORT);

                    toast.show();


                }catch(Exception e){
                    System.out.println(e);
                    Toast toast=Toast.makeText(getApplicationContext(),"Dogodila se greška!",Toast.LENGTH_SHORT);

                    toast.show();
                }finally{
                    if (con != null) {
                        con.disconnect();
                    }
                }
            }
        }
    }
















}
