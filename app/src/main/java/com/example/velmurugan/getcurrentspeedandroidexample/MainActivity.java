package com.example.velmurugan.getcurrentspeedandroidexample;

import java.math.BigDecimal;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements GPSCallback {

    private GPSManager gpsManager = null;
    private double speed = 0.0;
    Boolean isGPSEnabled = false;
    LocationManager locationManager;
    double currentSpeed, kmphSpeed;
    TextView txtview;

    //Var for Timer
    private TextView textTimer;
    private Button startButton;
    private Button pauseButton;
    private long startTime = 0L;
    private Handler myHandler = new Handler();
    long timeInMillies = 0L;
    long timeSwap = 0L;
    long finalTime = 0L;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtview = findViewById(R.id.info);

        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ///Timer

        textTimer = findViewById(R.id.textTimer);

//        final Button getCurrentSpeed=findViewById(R.id.getCurrentSpeed);
//        getCurrentSpeed.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (kmphSpeed==0.0){
//
//                    startTime = SystemClock.uptimeMillis();
//                    myHandler.postDelayed(updateTimerMethod, 0);
//                }
//                else{
//
//                    timeSwap += timeInMillies;
//                    myHandler.removeCallbacks(updateTimerMethod);
//
//                }
//            }
//        });


    }

    //Timer
//    private Runnable updateTimerMethod = new Runnable() {
//
//        public void run() {
//            timeInMillies = SystemClock.uptimeMillis() - startTime;
//            finalTime = timeSwap + timeInMillies;
//
//            int seconds = (int) (finalTime / 1000);
//            int minutes = seconds / 60;
//            seconds = seconds % 60;
//            int milliseconds = (int) (finalTime % 1000);
//            textTimer.setText(String.format("%d:%s:%s", minutes, String.format("%02d", seconds), String.format("%03d", milliseconds)));
//            myHandler.postDelayed(this, 0);
//        }
//    };

    public void getCurrentSpeed(View view) {

        txtview.setText(getString(R.string.info));

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        gpsManager = new GPSManager(MainActivity.this);
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (isGPSEnabled) {
            gpsManager.startListening(getApplicationContext());
            gpsManager.setGPSCallback(this);
        } else {
            gpsManager.showSettingsAlert();
        }
        if (kmphSpeed==0.0){
            startTime = SystemClock.uptimeMillis();
            myHandler.postDelayed(updateTimerMethod, 0);
        }
        else {
            timeSwap += timeInMillies;
            myHandler.removeCallbacks(updateTimerMethod);
        }
    }

    @Override
    public void onGPSUpdate(Location location) {
        speed = location.getSpeed();
        currentSpeed = round(speed, 3, BigDecimal.ROUND_HALF_UP);
        kmphSpeed = round((currentSpeed * 3.6), 3, BigDecimal.ROUND_HALF_UP);
        txtview.setText(kmphSpeed + "km/h");
//        startTime = SystemClock.uptimeMillis();
//        myHandler.postDelayed(updateTimerMethod, 0);

//        timeSwap += timeInMillies;

    }

    @Override
    protected void onDestroy() {
        gpsManager.stopListening();
        gpsManager.setGPSCallback(null);
        gpsManager = null;
        super.onDestroy();
    }

    public static double round(double unrounded, int precision, int roundingMode) {
        BigDecimal bd = new BigDecimal(unrounded);
        BigDecimal rounded = bd.setScale(precision, roundingMode);
        return rounded.doubleValue();
    }
    private Runnable updateTimerMethod = new Runnable() {

        public void run() {
            timeInMillies = SystemClock.uptimeMillis() - startTime;
            finalTime = timeSwap + timeInMillies;

            int seconds = (int) (finalTime / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            int milliseconds = (int) (finalTime % 1000);
            textTimer.setText(String.format("%d:%s:%s", minutes, String.format("%02d", seconds), String.format("%03d", milliseconds)));
            myHandler.postDelayed(this, 0);
        }
    };
}
