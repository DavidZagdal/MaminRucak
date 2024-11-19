package com.example.myapplication;

public class Jela implements Comparable{//
    private String datum_1;
    private String jelo_1;

    public Jela(String datum, String jelo){
        datum_1 = datum;
        jelo_1 = jelo;
    }

    public void setDatum_1(String datum){
        datum_1 = datum;
    }

    public void setJelo_1(String jelo_1) {
        this.jelo_1 = jelo_1;
    }

    public String getDatum_1() {
        return datum_1;
    }

    public String getJelo_1() {
        return jelo_1;
    }

    /*@Override
    public int compareTo(Object o) {
        Jela j1 = (Jela)o;

        String day = j1.getDatum_1().substring(0,j1.getDatum_1().indexOf("/"));
        String month = j1.getDatum_1().replace(day,"");
        month = month.substring(1,month.length());
        month = month.substring(0,month.indexOf("/"));
        String year = j1.getDatum_1().replace(day+"/"+month+"/","");

        String day2 = datum_1.substring(0,datum_1.indexOf("/"));
        String month2 = datum_1.replace(day2,"");
        month2 = month2.substring(1,month2.length());
        month2 = month2.substring(0,month2.indexOf("/"));
        String year2 = datum_1.replace(day2+"/"+month2+"/","");

        int day_1 = Integer.parseInt(day);
        int month_1 = Integer.parseInt(month);
        int year_1 = Integer.parseInt(year);

        int day_2 = Integer.parseInt(day2);
        int month_2 = Integer.parseInt(month2);
        int year_2 = Integer.parseInt(year2);


        if(year_1 > year_2) return 1;
        else if(month_1 > month_2 && year_1 == year_2) return 1;
        else if(day_1 > day_2 && month_1 == month_2 && year_1 == year_2) return 1;
        else if(day_1 == day_2 && month_1 == month_2 && year_1 == year_2) return 0;
        else return -1;
    }*/

    public int compareTo(Object o) {
        Jela j1 = (Jela)o;

        int brojcic = 0;
        int brojcic_domaci = 0;

        if(j1.datum_1.equals("Ponedjeljak")){
            brojcic = 1;
        }else if(j1.datum_1.equals("Utorak")){
            brojcic = 2;
        }else if(j1.datum_1.equals("Srijeda")){
            brojcic = 3;
        }else if(j1.datum_1.equals("Četvrtak")){
            brojcic = 4;
        }else if(j1.datum_1.equals("Petak")){
            brojcic = 5;
        }else if(j1.datum_1.equals("Subota")){
            brojcic = 6;
        }else if(j1.datum_1.equals("Nedjelja")){
            brojcic = 7;
        }

        if(datum_1.equals("Ponedjeljak")){
            brojcic_domaci = 1;
        }else if(datum_1.equals("Utorak")){
            brojcic_domaci = 2;
        }else if(datum_1.equals("Srijeda")){
            brojcic_domaci = 3;
        }else if(datum_1.equals("Četvrtak")){
            brojcic_domaci = 4;
        }else if(datum_1.equals("Petak")){
            brojcic_domaci = 5;
        }else if(datum_1.equals("Subota")){
            brojcic_domaci = 6;
        }else if(datum_1.equals("Nedjelja")){
            brojcic_domaci = 7;
        }



        if(brojcic < brojcic_domaci) return 1;
        else if(brojcic == brojcic_domaci) return 0;
        else return -1;
    }
}
