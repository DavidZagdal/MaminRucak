package com.example.myapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PoDanima extends AppCompatActivity {

    Button backImg;

    private static String url2 = "";
    private static String url_tjedan = "";
    private static String masterkey = "";
    private static String acceskey = "";
    private String resultInString = "";

    String jsonRezultat = "";
    String jsonRezTjedan = "";

    ProgressDialog pd;
    ProgressDialog pd2;


    public static int maxJela = 1;
    public static int maxTjedana = 1;
    public static int odabraniTjedan = 1;


    Button btn_provjera;
    Button btn_dodaj_tjedan;
    Button btn_lista_tjedana;
    TextView txtPon;
    TextView txtUto;
    TextView txtSri;
    TextView txtCet;
    TextView txtPet;
    TextView txtSub;
    TextView txtNed;

    AlertDialog.Builder window_dan;
    public static int moze_dodaj = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tablica_dana);

        new PoDanima.JsonTask().execute(url2);
        new PoDanima.JsonTaskTjedni().execute(url_tjedan);

        backImg = findViewById(R.id.btn_back3);
        txtPon = findViewById(R.id.txt_pon);
        txtUto = findViewById(R.id.txt_uto);
        txtSri = findViewById(R.id.txt_sri);
        txtCet = findViewById(R.id.txt_cet);
        txtPet = findViewById(R.id.txt_pet);
        txtSub = findViewById(R.id.txt_sub);
        txtNed = findViewById(R.id.txt_ned);


        btn_dodaj_tjedan = findViewById(R.id.btnTablicaDodaj);
        btn_lista_tjedana = findViewById(R.id.btnTablicaTjedana);

        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PoDanima.this, ScrollingActivity.class);
                startActivity(intent);
            }
        });

        btn_dodaj_tjedan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //pitaj zele li dodati da ne

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                moze_dodaj = 1;
                                new PoDanima.MyTask().execute(url_tjedan);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(PoDanima.this);
                builder.setMessage("Želite li dodati ovaj tjedan (još nema brisanja tjedana)").setPositiveButton("Da", dialogClickListener)
                        .setNegativeButton("Ne", dialogClickListener).show();


            }
        });
        btn_lista_tjedana.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otvoriPopupZaOdabirTjedana();
            }
        });


        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(url2)
                .method("GET", null)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                response.body().string();
            }
        });




    }




    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(PoDanima.this);
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
            System.out.println(resultInString);
            //JSONObject mainObject;
            try {
                JSONObject mainObject = new JSONObject(jsonRezultat);

                JSONObject uniObject = mainObject.getJSONObject("record");
                maxJela = uniObject.length();

                ArrayList<Jela> svaImena = new ArrayList<Jela>();

                for(int i = 1; i <= uniObject.length(); i++){

                    String datum = "";
                    String jelo = "";

                    Jela j1 = new Jela(datum,jelo);

                    JSONObject uniObject2 = uniObject.getJSONObject(""+i);
                    datum = uniObject2.getString("datum");
                    jelo = uniObject2.getString("jelo");

                    j1.setDatum_1(datum);
                    j1.setJelo_1(jelo);

                    svaImena.add(j1);
                }


                Collections.sort(svaImena);

                Toast toast;
                for(int i = 0; i < uniObject.length(); i++){
                    if(i == 0) txtPon.setText(svaImena.get(i).getDatum_1()+": " + svaImena.get(i).getJelo_1());
                    else if(i == 1) txtUto.setText(svaImena.get(i).getDatum_1()+": " + svaImena.get(i).getJelo_1());
                    else if(i == 2) txtSri.setText(svaImena.get(i).getDatum_1()+": " + svaImena.get(i).getJelo_1());
                    else if(i == 3) txtCet.setText(svaImena.get(i).getDatum_1()+": " + svaImena.get(i).getJelo_1());
                    else if(i == 4) txtPet.setText(svaImena.get(i).getDatum_1()+": " + svaImena.get(i).getJelo_1());
                    else if(i == 5) txtSub.setText(svaImena.get(i).getDatum_1()+": " + svaImena.get(i).getJelo_1());
                    else if(i == 6) txtNed.setText(svaImena.get(i).getDatum_1()+": " + svaImena.get(i).getJelo_1());
                    else toast=Toast.makeText(getApplicationContext(),"Something is wrong",Toast.LENGTH_SHORT);

                }
                //txtJson.setText("Success!!");

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }



    private class JsonTaskTjedni extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();

            pd2 = new ProgressDialog(PoDanima.this);
            pd2.setMessage("Please wait");
            pd2.setCancelable(false);
            pd2.show();
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
            if (pd2.isShowing()) {
                pd2.dismiss();
            }
            jsonRezTjedan = result;
            System.out.println(jsonRezTjedan);
            //JSONObject mainObject;
            try {
                JSONObject mainObject = new JSONObject(jsonRezTjedan);

                JSONObject uniObject = mainObject.getJSONObject("record");
                maxTjedana = uniObject.length();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    public String doSomeWork(String str1){

        return str1;
    }


    private String translateEngCro(String dayName) {
        if(dayName.equals("Monday")) return "Ponedjeljak";
        if(dayName.equals("Tuesday")) return "Utorak";
        if(dayName.equals("Wednesday")) return "Srijeda";
        if(dayName.equals("Thursday")) return "Četvrtak";
        if(dayName.equals("Friday")) return "Petak";
        if(dayName.equals("Saturday")) return "Subota";
        if(dayName.equals("Sunday")) return "Nedjelja";

        return "error";
    }



    private void otvoriPopupZaOdabirTjedana(){
        window_dan = new AlertDialog.Builder(this);
        window_dan.setTitle("Odaberi tjedan");



        String[] Options2 = new String[maxTjedana];

        for(int i = 1; i<=maxTjedana; i++){
            Options2[i-1] = ""+i;
        }

        window_dan.setItems(Options2, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                odabraniTjedan = which+1;

                sveIhPopuni();
            }
        });

        window_dan.show();
    }

    private void sveIhPopuni(){

        try{
            JSONObject mainObject = new JSONObject(jsonRezTjedan);
            JSONObject uniObject = mainObject.getJSONObject("record");
            JSONObject lastObject = uniObject.getJSONObject(odabraniTjedan+"");

            System.out.println(lastObject.toString());

            ArrayList<Jela> svaImena = new ArrayList<Jela>();

            for(int i = 1; i <= lastObject.length(); i++){

                String datum = "";
                String jelo = "";

                Jela j1 = new Jela(datum,jelo);

                JSONObject lastObject2 = lastObject.getJSONObject(""+i);
                datum = lastObject2.getString("datum");
                jelo = lastObject2.getString("jelo");

                j1.setDatum_1(datum);
                j1.setJelo_1(jelo);

                svaImena.add(j1);
            }


            Collections.sort(svaImena);

            System.out.println(svaImena.toString());

            Toast toast;
            for(int i = 0; i < uniObject.length(); i++){
                if(i == 0) txtPon.setText(svaImena.get(i).getDatum_1()+": " + svaImena.get(i).getJelo_1());
                else if(i == 1) txtUto.setText(svaImena.get(i).getDatum_1()+": " + svaImena.get(i).getJelo_1());
                else if(i == 2) txtSri.setText(svaImena.get(i).getDatum_1()+": " + svaImena.get(i).getJelo_1());
                else if(i == 3) txtCet.setText(svaImena.get(i).getDatum_1()+": " + svaImena.get(i).getJelo_1());
                else if(i == 4) txtPet.setText(svaImena.get(i).getDatum_1()+": " + svaImena.get(i).getJelo_1());
                else if(i == 5) txtSub.setText(svaImena.get(i).getDatum_1()+": " + svaImena.get(i).getJelo_1());
                else if(i == 6) txtNed.setText(svaImena.get(i).getDatum_1()+": " + svaImena.get(i).getJelo_1());
                else toast=Toast.makeText(getApplicationContext(),"Something is wrong",Toast.LENGTH_SHORT);

            }


        }catch(Exception e){
            System.out.println(e);
        }


    }


    private class MyTask
            extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params)
        {


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
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
            // do something with the result
            URL object = null;
            HttpURLConnection con = null;
            if(moze_dodaj == 1){
                try{

                    jsonRezTjedan = result;

                    StrictMode.ThreadPolicy gfgPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(gfgPolicy);
                    object=new URL(url_tjedan);

                    System.out.println("uso sam");
                    JSONObject mainObject = new JSONObject(result);

                    JSONObject uniObject = mainObject.getJSONObject("record");
                    maxTjedana = uniObject.length();

                    //stari objekt
                    JSONObject mainObject2 = new JSONObject(jsonRezultat);
                    JSONObject uniObject2 = mainObject2.getJSONObject("record");

                    con = (HttpURLConnection) object.openConnection();
                    con.setDoOutput(true);
                    con.setRequestProperty("Content-Type", "application/json");
                    con.addRequestProperty("X-Master-Key", ""+masterkey);
                    con.setRequestMethod("PUT");


                    uniObject.put(""+(maxTjedana+1), uniObject2);
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
