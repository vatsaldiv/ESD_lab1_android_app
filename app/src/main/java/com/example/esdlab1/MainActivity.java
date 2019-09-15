package com.example.esdlab1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    DatabaseReference database;

    List<Data> dataList;

    float currentTemp=0.0f,currentLight=0.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database = FirebaseDatabase.getInstance().getReference().child("Data");
        dataList = new ArrayList<>();

        PopulateData();

        Spinner vTemp,vLight;
        vTemp=findViewById(R.id.spinner_1_3);
        vLight = findViewById(R.id.spinner_2_3);

       vTemp.setOnItemSelectedListener(UpdateViewTemp);
       vLight.setOnItemSelectedListener(UpdateViewLight);


    }

    public void PopulateData()
    {

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                dataList.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    if(snapshot.hasChild("data1"))
                    {
                        Data d = new Data(
                                Integer.parseInt(snapshot.getKey().toString()),
                                Float.parseFloat(snapshot.child("data1").getValue().toString()),
                                Float.parseFloat(snapshot.child("data2").getValue().toString()),
                                snapshot.child("data3").getValue().toString()
                                );

                        dataList.add(d);
                        currentLight = d.light;
                        currentTemp = d.temp;
                        ShowData();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void ShowData()
    {
        TextView vTemp = findViewById(R.id.textView_3_1);
        TextView vLight = findViewById(R.id.textView_4_1);
        TextView vAvgTemp = findViewById(R.id.textView_3_2);
        TextView vAvgLight = findViewById(R.id.textView_4_2);
        TextView _vAvgTemp = findViewById(R.id.textView_3_3);
        TextView _vAvgLight = findViewById(R.id.textView_4_3);


        vTemp.setText(currentTemp+"");
        vLight.setText(currentLight+"");
        vAvgTemp.setText("" + getAverageData("","").getTemp());
        vAvgLight.setText(""+getAverageData("","").getLight());

        Data d = getAverageData(Temp_pos,"");
        _vAvgTemp.setText(d.getTemp()+"");

         d = getAverageData(Light_pos,"");
        _vAvgLight.setText(d.getLight()+"");


    }

    private void UpdateData(TextView v,String data)
    {
        v.setText(data);
    }

    private Data getAverageData(String hr,String min)
    {

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        String _date = formatter.format(date)+"_"+hr+":"+min;
        if(TextUtils.isEmpty(hr)&&TextUtils.isEmpty(min))_date = _date.split("_")[0];

        Iterator<Data> iterator = dataList.iterator();

        Data Avg = new Data(0,0,0,"yo");

        while (iterator.hasNext())
        {

            Data d = iterator.next();

            if(!d.getDateTime().trim().contains(_date))continue;

            Avg.setTemp(Avg.getTemp()*Avg.getCount() + d.getTemp());
            Avg.setLight(Avg.getLight()*Avg.getCount() + d.getLight());

            Avg.setCount(Avg.getCount()+1);

            Avg.setTemp(Avg.getTemp()/Avg.getCount());
            Avg.setLight(Avg.getLight()/Avg.getCount());
        }

        return Avg;

    }

    class Data
    {
        private int count;
        private float temp;
        private float light;
        private String dateTime;

        public Data(int count, float temp, float light, String dateTime) {
            this.count = count;
            this.temp = temp;
            this.light = light;
            this.dateTime = dateTime;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public float getTemp() {
            return temp;
        }

        public void setTemp(float temp) {
            this.temp = temp;
        }

        public float getLight() {
            return light;
        }

        public void setLight(float light) {
            this.light = light;
        }

        public String getDateTime() {
            return dateTime;
        }

        public void setDateTime(String dateTime) {
            this.dateTime = dateTime;
        }
    }



    //Listners

    String Temp_pos="00";
    String Light_pos ="00";

    AdapterView.OnItemSelectedListener UpdateViewTemp = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            String _pos = ""+position;
            if(position<10)_pos = 0+_pos;

            Temp_pos = _pos;

            ShowData();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    AdapterView.OnItemSelectedListener UpdateViewLight = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            String _pos = ""+position;
            if(position<10)_pos = 0+_pos;

            Light_pos = _pos;

            ShowData();

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

}
