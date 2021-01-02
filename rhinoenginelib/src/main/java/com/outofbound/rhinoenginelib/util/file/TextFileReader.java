package com.outofbound.rhinoenginelib.util.file;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TextFileReader {

    public static String getString(Context context, String file ){

        StringBuilder buf = new StringBuilder();
        InputStream text;
        BufferedReader in;
        String str;

        try {
            text = context.getAssets().open(file);
            in = new BufferedReader(new InputStreamReader(text, "UTF-8"));
            while ( (str=in.readLine()) != null ) {
                str = str+'\n';
                buf.append(str);
            }
            in.close();

            return buf.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }
}

