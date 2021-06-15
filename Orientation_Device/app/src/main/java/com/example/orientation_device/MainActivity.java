package com.example.orientation_device;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    // Variables de Sensores
    SensorManager sensor_manager;
    Sensor acelerometer_sensor;
    Sensor gravity_sensor;

    // Valores de Sensores
    String position;

    // Variables Auxiliares
    boolean on;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensor_manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        acelerometer_sensor = sensor_manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gravity_sensor = sensor_manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        on=true;
        tarea.execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensor_manager.unregisterListener(this);
        on=false;
        tarea.cancel(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensor_manager.registerListener(this,acelerometer_sensor,sensor_manager.SENSOR_DELAY_UI);
        on=true;
        if(!(tarea.getStatus() ==(AsyncTask.Status.RUNNING))){
            tarea.execute();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            if(event.values[1]>8){
                position="Movil Parado";
            }else if(event.values[0]>8){
                position="Movil Hechado de lado Izquierdo";
            }else if(event.values[1]<-8){
                position="Movil de Cabeza";
            }else if(event.values[0]<-8){
                position="Movil Hechado de lado Derecho";
            }else if(event.values[2]>8){
                position="Movil Hechado de Frente";
            }else if(event.values[2]<-8){
                position="Movil Hechado de Espalda";
            }else{
                position="Posición Neutra";
            }
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void closeApp(View v){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    AsyncTask tarea = new AsyncTask() {
        @Override
        protected Object doInBackground(Object[] objects) {
            while(on){
                publishProgress();
                try {
                    Thread.sleep(500);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);
            TextView acimut = findViewById(R.id.orientation_value);

            acimut.setText(position);
        }
    };
}