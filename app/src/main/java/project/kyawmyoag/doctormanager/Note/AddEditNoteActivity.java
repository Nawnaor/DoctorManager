package project.kyawmyoag.doctormanager.Note;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import project.kyawmyoag.doctormanager.Does.NewTaskAct;
import project.kyawmyoag.doctormanager.R;

public class AddEditNoteActivity extends AppCompatActivity {
    private TextInputEditText Title;
    private TextInputEditText Description;
    private TextView dateButton;
    private DatePickerDialog datePickerDialog;
    private Spinner spinner;
    private DatabaseReference reference;
    Integer noteNum = new Random().nextInt();
    String orderid = Integer.toString(noteNum);

    private static final String TAG = "AddEditNoteActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_note);
        initDatePicker();

        Title = findViewById(R.id.edit_text_title);
        Description = findViewById(R.id.edit_text_description);
        spinner = findViewById(R.id.spinnerPriority);
        dateButton = findViewById(R.id.datePickerButton);
        dateButton.setText(getTodayDate());

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);


    }

    private String getTodayDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day,month,year);
    }

    private void saveNote() {
        reference = FirebaseDatabase.getInstance().getReference().child("ORDER").
                child("Order"+noteNum);
        final ProgressDialog dialog = ProgressDialog.show(AddEditNoteActivity.this,"ဆောင်ရွက်နေသည်...","ခေတ္တစောင့်ပါ",true);
        dialog.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().child("TitleNote").setValue(Title.getText().toString());
                        dataSnapshot.getRef().child("Description").setValue(Description.getText().toString());
                        dataSnapshot.getRef().child("Priority").setValue(spinner.getSelectedItem().toString());
                        dataSnapshot.getRef().child("Date").setValue(dateButton.getText().toString());
                        dataSnapshot.getRef().child("NoteId").setValue(orderid);

                        Intent a = new Intent(AddEditNoteActivity.this,NoteActivity.class);
                        a.addFlags(a.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(a);
                        Toast.makeText(AddEditNoteActivity.this, "ထည့်သွင်းပြီးပါပြီ", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        },1000);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_note_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_note:saveNote();
            return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initDatePicker(){
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = makeDateString(day, month, year);
                dateButton.setText(date);
            }
        };
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int style = AlertDialog.BUTTON_NEGATIVE;
        datePickerDialog = new DatePickerDialog(this,style,dateSetListener,year,month,day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

    }

    private String makeDateString(int day, int month, int year){
        return getMonthFormat(month) + "." + day + "." + year;
    }

    private String getMonthFormat(int month){
        if (month == 1)
            return "ဇန်";
        if (month == 2)
            return "ဖေ";
        if (month == 3)
            return "မတ်";
        if (month == 4)
            return "ဧပြီ";
        if (month == 5)
            return "မေ";
        if (month == 6)
            return "ဇွန်";
        if (month == 7)
            return "ဇူလိုင်";
        if (month == 8)
            return "ဩဂုတ်";
        if (month == 9)
            return "စက်တင်";
        if (month == 10)
            return "အောက်တို";
        if (month == 11)
            return "နိုဝင်ဘာ";
        if (month == 12)
            return "ဒီဇင်";

        return "ဇန်";
    }

    public void openDatePicker(View view) {
        datePickerDialog.show();
    }
}