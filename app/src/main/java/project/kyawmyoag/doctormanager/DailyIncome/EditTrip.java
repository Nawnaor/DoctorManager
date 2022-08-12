package project.kyawmyoag.doctormanager.DailyIncome;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import project.kyawmyoag.doctormanager.R;

public class EditTrip extends AppCompatActivity {

    private ArrayList<String>newExpenseList;
    public static final String EDIT_TRIP_PRICE="project.kyawmyoag.DoctorManager.EDITTRIPPRICE";
    public static final String EDIT_TRIP_LIST="project.kyawmyoag.DoctorManager.EDITTRIPLIST";
    public static final String EDIT_TRIP_DATE="project.kyawmyoag.DoctorManager.EDITTRIPDATE";
    private String date;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_trip);

        Intent intent = getIntent();
        date = intent.getStringExtra(DailyMain.CURRENT_DATE);
        String price = intent.getStringExtra(DailyMain.CURRENT_PRICE);
        String expenseListString = intent.getStringExtra(DailyMain.CURRENT_LIST);
        expenseListString = expenseListString.substring(1,expenseListString.length()-1);
        preFillFields(expenseListString);

        newExpenseList = new ArrayList<>();
        TextView textView = (TextView) findViewById(R.id.currentDateLabel);
        textView.setText(date);
    }

    public void preFillFields(String listAsString){
        String items[] = listAsString.split(",");
        int tag=1;
        EditText typeField;
        EditText amountField;
        for (String item : items){
            item = item.trim();
            String[]parts = item.split(":");
            typeField = findViewById(getResources().getIdentifier("type" + tag, "id",
                    this.getPackageName()));
            amountField = findViewById(getResources().getIdentifier("amount" + tag, "id",
                    this.getPackageName()));
            typeField.setText(parts[0].trim());
            amountField.setText(parts[1].trim().substring(1));
            tag++;
        }
    }
    public void onSaveEvent(View view){
        double totalPrice = 0;
        boolean allGood = true;
        for(int i=1; i<=5; i++){
            EditText typeField = (EditText) findViewById(getResources().getIdentifier("type" + i, "id",
                    this.getPackageName()));
            EditText amountField = (EditText) findViewById(getResources().getIdentifier("amount" + i, "id",
                    this.getPackageName()));
            String expenseType = typeField.getText().toString().trim();
            String amountText = amountField.getText().toString().trim();

            if((expenseType.length()!=0) && (amountText.length()!=0)){
                if(expenseType.indexOf(',') >= 0) {
                    Toast.makeText(this, "(, ;)ထိုသင်္ကေတများအားခွင့်မပြုပါ!", Toast.LENGTH_SHORT).show();
                    allGood=false;
                }
                totalPrice += Double.parseDouble(amountText);

                StringBuilder typeAndPrice = new StringBuilder(expenseType);
                typeAndPrice.append(" : $");
                typeAndPrice.append(amountText);

                newExpenseList.add(typeAndPrice.toString());
            }
            if(  (expenseType.length()!=0 && amountText.length()==0) ||
                    (amountText.length()!=0 && expenseType.length()==0)  )
                allGood = false;
        }
        
        //error if user didn't enter anything
        if(newExpenseList.size()==0 && allGood) {
            Toast.makeText(this, "ပစ္စည်းအမျိုးအစားနှင့် ကျသင့်ငွေ(ကျပ်)ထည့်ပေးပါ", Toast.LENGTH_SHORT).show();
            allGood = false;
        }
        //error if user didnt enter either price or expense type
        if(!allGood) {
            Toast.makeText(this, "ပစ္စည်းအမျိုးအစားနှင့် ကျသင့်ငွေ(ကျပ်)နှစ်ခုလုံးထည့်ပေးပါ", Toast.LENGTH_SHORT).show();
        }

        //every expense type entered as corresponding $ amount
        if(allGood) {

            String dateAsString = formatDate(date);

            Intent returnIntent = new Intent();
            returnIntent.putExtra(EDIT_TRIP_PRICE, Double.toString(totalPrice)); //send price to main activity
            returnIntent.putExtra(EDIT_TRIP_LIST, newExpenseList.toString()); //send expenses to main activity
            returnIntent.putExtra(EDIT_TRIP_DATE, dateAsString); //send trip date to main activity
            setResult(RESULT_OK, returnIntent);
            finish(); //exit, go back to main activity
        }
    }

    public void onCrossButton(View view){
        Button deleteButton = (Button) findViewById(view.getId());
        String tag = deleteButton.getTag().toString();

        EditText typeField = (EditText) findViewById(getResources().getIdentifier("type" + tag, "id",
                this.getPackageName()));
        EditText amountField = (EditText) findViewById(getResources().getIdentifier("amount" + tag, "id",
                this.getPackageName()));
        String expenseType = typeField.getText().toString().trim();
        String amountText = amountField.getText().toString().trim();

        if(expenseType.length()!=0)
            typeField.setText("");
        if(amountText.length()!=0)
            amountField.setText("");
    }
    
    public void onCancelEvent(View view){
        Toast.makeText(this, "ပြင်ဆင်မှုအား မမှတ်သားပါ", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "ပြင်ဆင်မှုအား မမှတ်သားပါ", Toast.LENGTH_SHORT).show();
        finish();
    }

    public String formatDate(String dateAsString)  {

        SimpleDateFormat sdf1 = new SimpleDateFormat("MMM dd, yyyy (E)");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        StringBuilder result = new StringBuilder("");
        Date date;
        try {
            date = sdf1.parse(dateAsString);
            result.append(sdf2.format(date));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return result.toString();
    }
}
