package project.kyawmyoag.doctormanager.Does;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Random;

import project.kyawmyoag.doctormanager.R;

public class NewTaskAct extends AppCompatActivity {

    private EditText CustomerName,PhoneNo,Customerpass,About,Imei,Date,
            Time,Service,Fee,conditionbutton;
    private TextView cancel;
    private Button addTask2;
    private DatabaseReference reference;
    Integer doesNum = new Random().nextInt();
    String keydoes = Integer.toString(doesNum);

    private static final String TAG = "NewTaskAct";
    private DatePickerDialog.OnDateSetListener taskDate2Listener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        CustomerName = findViewById(R.id.addTaskTitle2);
        PhoneNo = findViewById(R.id.addPhone2);
        Customerpass = findViewById(R.id.customerpass);
        About = findViewById(R.id.addTaskDescription2);
        Imei = findViewById(R.id.imei);
        Date = findViewById(R.id.taskDate2);
        Time = findViewById(R.id.taskTime2);
        Service = findViewById(R.id.taskEvent2);
        Fee = findViewById(R.id.charges2);
        addTask2 = findViewById(R.id.put);
        cancel = findViewById(R.id.cancel);
        conditionbutton = findViewById(R.id.conditionbutton);

        addTask2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validateFields()){

                    reference = FirebaseDatabase.getInstance().getReference().child("JOB LIST").
                            child("Job"+doesNum);

                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            dataSnapshot.getRef().child("CustomerName").setValue(CustomerName.getText().toString());
                            dataSnapshot.getRef().child("PhoneNo").setValue(PhoneNo.getText().toString());
                            dataSnapshot.getRef().child("Customerpass").setValue(Customerpass.getText().toString());
                            dataSnapshot.getRef().child("About").setValue(About.getText().toString());
                            dataSnapshot.getRef().child("IMEI").setValue(Imei.getText().toString());
                            dataSnapshot.getRef().child("Date").setValue(Date.getText().toString());
                            dataSnapshot.getRef().child("Time").setValue(Time.getText().toString());
                            dataSnapshot.getRef().child("Service").setValue(Service.getText().toString());
                            dataSnapshot.getRef().child("Fee").setValue(Fee.getText().toString());
                            dataSnapshot.getRef().child("Condition").setValue(conditionbutton.getText().toString());
                            dataSnapshot.getRef().child("keydoes").setValue(keydoes);


                            Intent a= new Intent(NewTaskAct.this,Home.class);
                            a.addFlags(a.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(a);
                            Toast.makeText(NewTaskAct.this, "ထည့်သွင်းပြီးပါပြီ", Toast.LENGTH_SHORT).show();
                            finish();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

            }
        });

        Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(NewTaskAct.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,taskDate2Listener,year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setTitle("လက်ခံသည့်နေ့စွဲ");
                dialog.show();
            }
        });
        taskDate2Listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG,"onDateSet: dd/mm/yyyy:"+day+"/"+month+"/"+year);
                String date = day+"ရက်" + "\n" + month+"လ" + "\n" + year;
                Date.setText(date);
            }
        };

        Time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int hour = cal.get(Calendar.HOUR);
                int minute = cal.get(Calendar.MINUTE);
                TimePickerDialog dialog;

                dialog = new TimePickerDialog(NewTaskAct.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hour, int minute) {
                        Log.d(TAG,"onTimeSet:hh:mm"+hour+":"+minute);
                        String time = hour+":"+minute+"-နာရီ";
                        Time.setText(time);
                    }
                },hour,minute,false);
                dialog.setTitle("အချိန်");
                dialog.show();

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(NewTaskAct.this, "မထည့်တော့ပါ", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        conditionbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConditionDialog();
            }
        });

    }
    private void showConditionDialog() {
        final int[] checkedItem = {-1};
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(NewTaskAct.this,R.style.AlertDialogCustom);
        alertDialog.setTitle("အခြေအနေ ရွေးပါ");
        alertDialog.setIcon(R.drawable.condition_24);
        final String[] listItems = new String[]{"ပြုပြင်နေဆဲ","ပြုပြင်ပြီး","ပြင်၍မရပါ"};
        alertDialog.setSingleChoiceItems(listItems, checkedItem[0], new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkedItem[0]=which;
                conditionbutton.setText(""+listItems[which]);
                dialog.dismiss();
            }
        });
        alertDialog.setNegativeButton("မထည့်ပါ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog customAlertDialog = alertDialog.create();
        customAlertDialog.show();
    }

    public boolean validateFields() {
        if (CustomerName.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "Customer အမည်ထည့်ပါ။", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (PhoneNo.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "ဖုန်းနံပါတ်ထည့်ပါ", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (About.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "အကြောင်းအရာထည့်ပါ။", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (Date.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "နေ့စွဲထည့်ပါ။", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (Time.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "အချိန်ထည့်ပါ။", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (Service.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "ပြင်မည့်အကြောင်းအရာထည့်ပါ။", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (Fee.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "ကျသင့်ငွေထည့်ရန်။", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (conditionbutton.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "အခြေအနေထည့်ပါ။", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}