package com.huya.marksman;

import android.os.AsyncTask;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class LoadDexActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_load_dex);
        new LoadDexTask().execute();

    }

    class LoadDexTask extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                MultiDex.install(getApplication());
                Log.e("LoadDexActivity","LoadDexActivity loadDex: "+"install finish");
                ((MarkApplication)getApplication()).installFinish(getApplication());
            } catch (Exception e) {
                Log.e("LoadDex","LoadDexActivity loadDex: "+e.getLocalizedMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            Log.e( "LoadDexActivity", "get install finish");
            finish();
            System.exit(0);
        }
    }

    @Override
    public void onBackPressed() {
        //can not back press
    }
}
