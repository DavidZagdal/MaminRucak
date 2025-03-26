package com.example.myapplication;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
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
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ScrollingActivity extends AppCompatActivity {

    //private YouTubePlayerView youTubePlayerView;

    private static String url = BuildConfig.URL;
    private static String url2_poDanima = BuildConfig.URL_PO_DANIMA;
    private static String masterkey = BuildConfig.MASTER_KEY;
    private String resultInString = "";
    private String resultForDays = "";
    private ArrayList<String> resultArrayList = new ArrayList<>();
    private ArrayList<String> descriptionArray = new ArrayList<>();

    private DatePickerDialog datePickerDialog;

    private static final String RECORD_KEY = "record_key";
    public static final String SHARED_PREFS ="sharedPrefs";

    private int maxJela = 1;
    private int maxObjekta = 1;

    public String jelo_prilog = "jelo";

    public ArrayList<String> svaImena = new ArrayList<String>();

    public int jeloRedni = 0;

    private int korisnik = 1;
    private int moze_dodaj = 0;

    public EditText input;
    public EditText input_fromT;

    private String datum = "Ponedjeljak";

    String jsonRezultat = "";

    Button btnHit, btnDodaj, btnMakni, btnDan,btnDodajDan, btnTablica;
    TextView txtJson;
    TextView txtPrilog;
    TextView txtJelo;
    TextView txtDan;
    ProgressDialog pd;

    final String[] Options = {"Red", "Blue"};
    AlertDialog.Builder window;
    AlertDialog.Builder window_dan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_default_custom_ui_example);
        resultArrayList.add("First");
        descriptionArray.add("First");


        new JsonTask().execute(url);

        btnHit = (Button) findViewById(R.id.btnJelo);
        txtJelo = (TextView) findViewById(R.id.txt_jelo);
        txtPrilog = (TextView) findViewById(R.id.txt_prilog);
        txtDan = (TextView) findViewById(R.id.txt_dan);

        Locale locale = getResources().getConfiguration().locale;
        Locale.setDefault(locale);

        initDatePicker();

        input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);

        input_fromT = new EditText(this);
        input_fromT.setInputType(InputType.TYPE_CLASS_NUMBER);

        btnDodaj = findViewById(R.id.btnDodaj);
        btnMakni = findViewById(R.id.btnMakni);
        btnDan = findViewById(R.id.btnDan);
        btnDodajDan = findViewById(R.id.btnDodajDan);
        btnTablica = findViewById(R.id.btnTablica);

        btnDodaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScrollingActivity.this, Unos.class);
                startActivity(intent);
            }
        });

        btnMakni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScrollingActivity.this, KriviUnos.class);
                startActivity(intent);
            }
        });

        btnTablica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScrollingActivity.this, PoDanima.class);
                startActivity(intent);
            }
        });

        btnHit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //samo jelo
                jelo_prilog = "jelo";
                nadiJeloPrilog();
            }
        });

        btnDan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dodaj u drugi JSON odabrani datum, prvo popup s biranjem datuma
                otvoriDatePopup();
            }
        });

        btnDodajDan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //neki popup di potvrduje unos ili pita za redni broj jela
                otvoriPopup();
            }
        });

        txtJelo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(input_fromT.getParent()!=null)
                    ((ViewGroup)input_fromT.getParent()).removeView(input_fromT);

                AlertDialog d1 = new AlertDialog.Builder(ScrollingActivity.this)
                        .setTitle("Napisi redni broj jela")
                        .setView(input_fromT)
                        .setMessage("Brojevi od 1 - " +maxJela)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                jeloRedni = Integer.parseInt(input_fromT.getText().toString());

                                txtJelo.setText(svaImena.get(jeloRedni-1));
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Do nothing.
                            }
                        }).show();
            }
        });



        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(url)
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

    private void otvoriPopup() {

        if(txtDan.getText().equals("")){
            Toast.makeText(getApplicationContext(), "Nije odabran dan!", Toast.LENGTH_LONG).show();
            return;
        }




        window = new AlertDialog.Builder(this);
        window.setTitle("Potvrdi odabir za dodavanje u bazu");

        String[] Options2 = {"Odabir je tocan: " +txtDan.getText() +" --> " +txtJelo.getText(), "Odabir s rednim brojem", "EXIT"};

        window.setItems(Options2, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    if(txtJelo.getText().equals("")){
                        Toast.makeText(getApplicationContext(), "Nije odabrano jelo da ta opcija radi", Toast.LENGTH_LONG).show();
                        return;
                    }
                    //first option clicked, do this...
                    //Toast.makeText(getApplicationContext(), "Prva opcija", Toast.LENGTH_LONG).show();
                    //sada dodati u bazu za taj dan, ili overwrite
                    moze_dodaj = 1;
                    //my task
                    try {
                        new MyTask().execute(url2_poDanima);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println(e);
                    }
                }else if(which == 1){
                    //second option clicked, do this...
                    if(input.getParent()!=null)
                        ((ViewGroup)input.getParent()).removeView(input);

                    AlertDialog d1 = new AlertDialog.Builder(ScrollingActivity.this)
                            .setTitle("Napisi redni broj jela")
                            .setView(input)
                            .setMessage("Brojevi od 1 - " +maxJela)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                    jeloRedni = Integer.parseInt(input.getText().toString());
                                    System.out.println("REDNI " +jeloRedni);
                                    System.out.println("JELO +0"+svaImena.get(jeloRedni));
                                    System.out.println("JELO +1"+svaImena.get(jeloRedni+1));

                                    if(jeloRedni >= 1 && jeloRedni <= maxJela){
                                        moze_dodaj = 2;
                                        //my task
                                        try {
                                            new MyTask().execute(url2_poDanima);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            System.out.println(e);
                                        }
                                    }else{
                                        Toast.makeText(getApplicationContext(), "Broj jela ne postoji.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    // Do nothing.
                                }
                            }).show();


                }else if(which == 2){
                    //second option clicked, do this...
                    //Toast.makeText(getApplicationContext(), "EXITED", Toast.LENGTH_LONG).show();
                }else{
                    //theres an error in what was selected
                    Toast.makeText(getApplicationContext(), "Hmmm I messed up. I detected that you clicked on : " + which + "?", Toast.LENGTH_LONG).show();
                }
            }
        });

        window.show();
    }

    private void otvoriDatePopup(){
        window_dan = new AlertDialog.Builder(this);
        window_dan.setTitle("Odaberi dan");

        String[] Options2 = {"PONEDJELJAK","UTORAK","SRIJEDA","ČETVRTAK", "PETAK", "SUBOTA", "NEDJELJA", "EXIT"};

        window_dan.setItems(Options2, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    //pon
                    txtDan.setText("Ponedjeljak");
                }else if(which == 1){
                    //uto
                    txtDan.setText("Utorak");
                }else if(which == 2){
                    //sri
                    txtDan.setText("Srijeda");
                }else if(which == 3){
                    //cet
                    txtDan.setText("Četvrtak");
                }else if(which == 4){
                    //pet
                    txtDan.setText("Petak");
                }else if(which == 5){
                    //sub
                    txtDan.setText("Subota");
                }else if(which == 6){
                    //ned
                    txtDan.setText("Nedjelja");
                }else if(which == 7){
                    //second option clicked, do this...
                    //Toast.makeText(getApplicationContext(), "EXITED", Toast.LENGTH_LONG).show();
                }else{
                    //theres an error in what was selected
                    Toast.makeText(getApplicationContext(), "Hmmm I messed up. I detected that you clicked on : " + which + "?", Toast.LENGTH_LONG).show();
                }
            }
        });

        window_dan.show();
    }


    private void initDatePicker(){
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                //String date = makeDateString(dayOfMonth, month, year);
                String dayName = "";
                SimpleDateFormat inFormat = new SimpleDateFormat("dd-MM-yyyy");
                try {
                    Date myDate = inFormat.parse(dayOfMonth+"-"+month+"-"+year);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE");
                    dayName=simpleDateFormat.format(myDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String imeD = translateEngCro(dayName);

                datum = dayOfMonth +"/"+month +"/"+year;

                txtDan.setText(imeD+" "+dayOfMonth +"."+month +"."+year+".");
            }
        };
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        //datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
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

    private String makeDateString(int day, int month, int year)
    {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month)
    {
        if(month == 1)
            return "SIJ";
        if(month == 2)
            return "VELJ";
        if(month == 3)
            return "OŽU";
        if(month == 4)
            return "TRA";
        if(month == 5)
            return "SVI";
        if(month == 6)
            return "LIP";
        if(month == 7)
            return "SRP";
        if(month == 8)
            return "KOL";
        if(month == 9)
            return "RUJ";
        if(month == 10)
            return "LIS";
        if(month == 11)
            return "STU";
        if(month == 12)
            return "PRO";

        //default should never happen
        return "SIJ";
    }

    public void openDatePicker(View view)
    {
        datePickerDialog.show();
    }


    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(ScrollingActivity.this);
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

                svaImena.removeAll(svaImena);

                for(int i = 1; i <= uniObject.length(); i++){
                    String ime = "";
                    String id = "";

                    JSONObject uniObject2 = uniObject.getJSONObject(""+i);
                    id = uniObject2.getString("id");
                    ime = uniObject2.getString("ime");

                    svaImena.add(ime);
                }

                Collections.sort(svaImena);

                for(int i = 0; i < uniObject.length(); i++){
                    if(i == 0) txtPrilog.setText("1. " + svaImena.get(i));
                    else txtPrilog.setText(txtPrilog.getText()+"\n"+(i+1)+". "+svaImena.get(i));
                }


            } catch (Exception e) {
                e.printStackTrace();
            }



        }
    }

    private void nadiJeloPrilog(){
        try{
            JSONObject mainObject = new JSONObject(jsonRezultat);

            int max = maxJela, min = 1;
            JSONObject uniObject = mainObject.getJSONObject("record");

            String provjeriTe = "";
            if(jelo_prilog.equals("jelo")){
                provjeriTe = txtJelo.toString();
            }else if(jelo_prilog.equals("prilog")){
                provjeriTe = txtPrilog.toString();
            }


            //for(int i = 1; i <= uniObject.length(); i++){
            String ime = "";
            String id = "";
            while(!id.equals(jelo_prilog) || provjeriTe.equals(ime)){
                Random rn = new Random();
                int i = rn.nextInt(max - min + 1) + min;
                JSONObject uniObject2 = uniObject.getJSONObject(""+i);
                id = uniObject2.getString("id");
                ime = uniObject2.getString("ime");
            }

            int tempRedni = 0;
            if(jelo_prilog.equals("jelo")){
                txtJelo.setText(ime);
                for(int i = 0; i < maxJela; i++){
                    if(svaImena.get(i).equals(ime)) tempRedni = i+1;
                }
                input_fromT.setText(tempRedni+"");
            }else if(jelo_prilog.equals("prilog")){
                txtPrilog.setText(ime);
            }

        } catch (Exception e) {
            e.printStackTrace();
            txtJson.setText(e.toString());
        }

    }

    public String doSomeWork(String str1){

        return str1;
    }

    private class MyTask
            extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(url2_poDanima);
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
            System.out.println("TEST");
            URL object2 = null;
            HttpURLConnection con2 = null;
            if(moze_dodaj == 1){
                try{
                    StrictMode.ThreadPolicy gfgPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(gfgPolicy);
                    object2=new URL(url2_poDanima);

                    System.out.println("uso sam");
                    JSONObject mainObject2 = new JSONObject(result+"");

                    JSONObject uniObject2 = mainObject2.getJSONObject("record");
                    maxObjekta = uniObject2.length();

                    con2 = (HttpURLConnection) object2.openConnection();
                    con2.setDoOutput(true);
                    con2.setRequestProperty("Content-Type", "application/json");
                    con2.addRequestProperty("X-Master-Key", ""+masterkey);
                    con2.setRequestMethod("PUT");

                    JSONObject id   = new JSONObject();

                    id.put("datum", ""+txtDan.getText());
                    id.put("jelo", ""+txtJelo.getText()+"");

                    try{
                        int izbrisati_id = 0;
                        for(int i = 1; i<=maxObjekta; i++){

                            if(txtDan.getText().equals(uniObject2.getJSONObject(""+i).getString("datum"))){
                                //overwrite, pa delete taj datum onda
                                System.out.println("TEST USAO JEDAN DA");
                                System.out.println(uniObject2.getJSONObject(""+i).toString());
                                uniObject2.remove(""+i);
                                izbrisati_id = i;
                            }
                        }
                        if(izbrisati_id == 0) {


                        }
                        else{
                            maxObjekta-=1;
                            for(int i = izbrisati_id+1; i <= maxJela; i++){
                                JSONObject id3   = uniObject2.getJSONObject(""+i);
                                JSONObject tempObject = new JSONObject();
                                String datum_pr = id3.getString("datum");
                                String jelo_pr = id3.getString("jelo");

                                tempObject.put("datum",""+datum_pr);
                                tempObject.put("jelo", ""+jelo_pr);

                                uniObject2.remove(""+i);
                                uniObject2.put(""+(i-1), tempObject);

                            }
                        }


                    }catch(Exception e){
                        System.out.println(e+" er2");
                    }
                    System.out.println("MAX OBJEKT: " +maxObjekta);
                    maxObjekta+=1;
                    System.out.println("MAX OBJEKT2: " +maxObjekta);
                    uniObject2.put(""+(maxObjekta), id);
                    //mainObject.put("record", uniObject);

                    OutputStreamWriter wr2= new OutputStreamWriter(con2.getOutputStream());
                    wr2.write(uniObject2.toString());
                    wr2.flush();

                    con2.getResponseCode();

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
                    if (con2 != null) {
                        con2.disconnect();
                    }
                }
            }else if(moze_dodaj == 2){
                try{
                    StrictMode.ThreadPolicy gfgPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(gfgPolicy);
                    object2=new URL(url2_poDanima);

                    System.out.println("uso sam");
                    JSONObject mainObject2 = new JSONObject(result+"");

                    JSONObject uniObject2 = mainObject2.getJSONObject("record");
                    maxObjekta = uniObject2.length();

                    con2 = (HttpURLConnection) object2.openConnection();
                    con2.setDoOutput(true);
                    con2.setRequestProperty("Content-Type", "application/json");
                    con2.addRequestProperty("X-Master-Key", ""+masterkey);
                    con2.setRequestMethod("PUT");

                    JSONObject id   = new JSONObject();

                    id.put("datum", ""+txtDan.getText());

                    id.put("jelo", ""+svaImena.get(jeloRedni-1)+""); //tu staviti jelo od id broja, popup za broj

                    txtJelo.setText(svaImena.get(jeloRedni-1));

                    try{
                        int izbrisati_id = 0;
                        for(int i = 1; i<=maxObjekta; i++){

                            if(txtDan.getText().equals(uniObject2.getJSONObject(""+i).getString("datum"))){
                                //overwrite, pa delete taj datum onda
                                System.out.println("TEST USAO JEDAN DA");
                                System.out.println(uniObject2.getJSONObject(""+i).toString());
                                uniObject2.remove(""+i);

                                izbrisati_id = i;
                            }
                        }
                        if(izbrisati_id == 0) {


                        }
                        else{
                            maxObjekta-=1;
                            for(int i = izbrisati_id+1; i <= maxJela; i++){
                                JSONObject id3   = uniObject2.getJSONObject(""+i);
                                JSONObject tempObject = new JSONObject();
                                String datum_pr = id3.getString("datum");
                                String jelo_pr = id3.getString("jelo");

                                tempObject.put("datum",""+datum_pr);
                                tempObject.put("jelo", ""+jelo_pr);

                                uniObject2.remove(""+i);
                                uniObject2.put(""+(i-1), tempObject);

                            }
                        }


                    }catch(Exception e){
                        System.out.println(e+" er2");
                    }
                    System.out.println("MAX OBJEKT: " +maxObjekta);
                    maxObjekta+=1;
                    System.out.println("MAX OBJEKT2: " +maxObjekta);
                    uniObject2.put(""+(maxObjekta), id);
                    //mainObject.put("record", uniObject);

                    OutputStreamWriter wr2= new OutputStreamWriter(con2.getOutputStream());
                    wr2.write(uniObject2.toString());
                    wr2.flush();

                    con2.getResponseCode();

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
                    if (con2 != null) {
                        con2.disconnect();
                    }
                }
            }
        }
    }


    public void saveData(){//sprema broj rekord
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //editor.putInt(RECORD_KEY,record);
        editor.putInt("korisnik", korisnik); //kasnije ce korisnik biti 1 = mama, 2 = bilo tko drugo (gost)

        editor.apply();
    }

    public void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        korisnik = sharedPreferences.getInt("korisnik",1); //vratiti default value na gosta
        //record = recordSave;
        //recordText.setText("Record: "+record);
    }
}
