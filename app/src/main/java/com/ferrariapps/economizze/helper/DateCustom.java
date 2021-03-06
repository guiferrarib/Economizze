package com.ferrariapps.economizze.helper;

import android.icu.text.DateFormat;
import android.os.Build;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class DateCustom {

    public static String dataAtual(){

        long date = System.currentTimeMillis();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault());
        return simpleDateFormat.format(date);
    }

    public static String mesAnoDataEscolhida(String data){

        String[] retornoData = data.split("/");
        String dia = retornoData[0];
        String mes = retornoData[1];
        String ano = retornoData[2];

        return mes+ano;

    }

}
