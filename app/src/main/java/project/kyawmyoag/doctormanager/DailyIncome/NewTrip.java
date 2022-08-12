package project.kyawmyoag.doctormanager.DailyIncome;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import project.kyawmyoag.doctormanager.R;

public class NewTrip extends AppCompatActivity {

    private ArrayList<String>expenseList = new ArrayList<>();
    public static final String TRIP_PRICE = "project.kyawmyoag.DoctorManager.TRIPPRICE";
    public static final String TRIP_LIST = "project.kyawmyoag.DoctorManager.TRIPLIST";
    public static final String TRIP_DATE = "project.kyawmyoag.DoctorManager.TRIPDATE";
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_trip);

        //get selected date from main activity
        Intent intent = getIntent();
        date = intent.getStringExtra(DailyMain.CURRENT_DATE);

        TextView textView = (TextView) findViewById(R.id.currentDateLabel);
        textView.setText(date);

        addAutoSuggestions();
    }

    public void addAutoSuggestions(){
        String[] expenseTypes = {"Charger","အားသွင်းကြိုး","ဘက်ထရီ","နားကြပ်","မှန်မကွဲ",
        "Hardware service","Software service","Power Bank","ဖုန်းကာဗာ","ပင်"};

        ArrayAdapter<String>adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item,expenseTypes);

        for(int tag=1; tag<=5; tag++)
        {
            AutoCompleteTextView typeField = (AutoCompleteTextView) findViewById(getResources().getIdentifier("type" + tag, "id",
                    this.getPackageName()));
            typeField.setThreshold(1);//will start working from first character
            typeField.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        }
    }
    public void onSaveEvent(View view) {

        double totalPrice = 0;
        boolean allGood = true;

        for(int i=1; i<=5; i++)
        {
            EditText typeField = (EditText) findViewById(getResources().getIdentifier("type" + i, "id",
                    this.getPackageName()));
            EditText amountField = (EditText) findViewById(getResources().getIdentifier("amount" + i, "id",
                    this.getPackageName()));
            String expenseType = typeField.getText().toString().trim();
            String amountText = amountField.getText().toString().trim();

            if((expenseType.length()!=0) && (amountText.length()!=0))
            {
                if(expenseType.indexOf(',') >= 0) {
                    Toast.makeText(this, "(, ;)ထိုသင်္ကေတများအားခွင့်မပြုပါ!", Toast.LENGTH_SHORT).show();
                    allGood=false;
                }

                totalPrice += Double.parseDouble(amountText);

                StringBuilder typeAndPrice = new StringBuilder(expenseType);
                typeAndPrice.append(" : $");
                typeAndPrice.append(amountText);

                expenseList.add(typeAndPrice.toString());
            }

            if(  (expenseType.length()!=0 && amountText.length()==0) ||
                    (amountText.length()!=0 && expenseType.length()==0)  )
                allGood = false;
        }
        if(expenseList.size()==0 && allGood) {
            Toast.makeText(this, "ပစ္စည်းအမျိုးအစားနှင့် ကျသင့်ငွေ(ks)ထည့်ပေးပါ", Toast.LENGTH_SHORT).show();
            allGood = false;
        }
        //error if user didnt enter either price or expense type
        if(!allGood) {
            Toast.makeText(this, "ပစ္စည်းအမျိုးအစားနှင့် ကျသင့်ငွေ(ks)နှစ်ခုလုံးထည့်ပေးပါ", Toast.LENGTH_SHORT).show();
        }


        //every expense type entered as corresponding $ amount
        if(allGood) {

            String dateAsString = formatDate(date);

            Intent returnIntent = new Intent();
            returnIntent.putExtra(TRIP_PRICE, Double.toString(totalPrice)); //send price to main activity
            returnIntent.putExtra(TRIP_LIST, expenseList.toString()); //send expenses to main activity
            returnIntent.putExtra(TRIP_DATE, dateAsString); //send trip date to main activity
            setResult(RESULT_OK, returnIntent);
            finish(); //exit, go back to main activity
        }
    }

    public void onCrossButton(View view) {

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
        Toast.makeText(this, "အရောင်းထည့်သွင်းမှူ ဖျက်သိမ်းလိုက်သည်", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onBackPressed() {
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