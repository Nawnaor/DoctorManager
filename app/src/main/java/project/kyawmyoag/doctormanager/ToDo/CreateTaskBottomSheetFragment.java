package project.kyawmyoag.doctormanager.ToDo;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import project.kyawmyoag.doctormanager.Database.DatabaseClient;
import project.kyawmyoag.doctormanager.R;

public class CreateTaskBottomSheetFragment extends BottomSheetDialogFragment {

    Unbinder unbinder;
    @BindView(R.id.addTaskTitle)
    EditText addTaskTitle;
    @BindView(R.id.addPhone)
    EditText phone;
    @BindView(R.id.addTaskDescription)
    EditText addTaskDescription;
    @BindView(R.id.taskDate)
    EditText taskDate;
    @BindView(R.id.taskTime)
    EditText taskTime;
    @BindView(R.id.taskEvent)
    EditText taskEvent;
    @BindView(R.id.charges)
    EditText charges;
    @BindView(R.id.addTask)
    Button addTask;
    int taskId;
    boolean isEdit;
    Task task;
    int mYear,mMonth,mDay;
    int mHour,mMinute;
    ToDo activity,swipeRefreshLayout;

    TimePickerDialog timePickerDialog;
    DatePickerDialog datePickerDialog;

    public static int count =0;

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new
            BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        dismiss();
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                }
            };

    public void setTaskId(int taskId, boolean isEdit, ToDo swipeRefreshLayout, ToDo activity) {
        this.taskId = taskId;
        this.isEdit = isEdit;
        this.activity = activity;
        this.swipeRefreshLayout = swipeRefreshLayout;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint({"RestrictedApi","ClickableViewAccessibility"})
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(),R.layout.fragment_create_task,null);
        unbinder = ButterKnife.bind(this,contentView);
        dialog.setContentView(contentView);

        addTask.setOnClickListener(view ->{
            if (validateFields())
                createTask();
        });
        if (isEdit) {
            showTaskFromId();

        }

        taskDate.setOnTouchListener((view,motionEvent)->{
            if (motionEvent.getAction()== MotionEvent.ACTION_UP) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(getActivity(),
                        (view1, year, monthOfYear, dayOfMonth) -> {
                            taskDate.setText(dayOfMonth + "-" + (monthOfYear + 1)+"-"+year);
                            datePickerDialog.dismiss();
                        },mYear,mMonth,mDay);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()-1000);
                datePickerDialog.show();
            }
            return true;
        });



    }

    public boolean validateFields() {
        if (addTaskTitle.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(activity, "Customer အမည်ထည့်ပါ။", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (addTaskDescription.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(activity, "ပျက်သည့်အကြောင်းအရာထည့်ပါ။", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (phone.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(activity, "ဖုန်းနံပါတ်ထည့်ပါ။", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (taskDate.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(activity, "လက်ခံသည့်နေ့စွဲထည့်ပါ။", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (taskTime.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(activity, "အချိန်ထည့်ပါ။", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (taskEvent.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(activity, "ပြင်မည့်အကြောင်းအရာထည့်ပါ။", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (charges.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(activity, "ကျသင့်ငွေထည့်ပါ။", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void createTask() {
        class saveTaskInBackend extends AsyncTask<Void,Void, Void> {

            @SuppressLint("WrongThread")
            @Override
            protected Void doInBackground(Void... voids) {
                Task createTask = new Task();
                createTask.setTaskTitle(addTaskTitle.getText().toString());
                createTask.setTaskDescription(addTaskDescription.getText().toString());
                createTask.setPhone(phone.getText().toString());
                createTask.setDate(taskDate.getText().toString());

                createTask.setEvent(taskEvent.getText().toString());
                createTask.setCharges(charges.getText().toString());


                if (!isEdit)
                    DatabaseClient.getInstance(getActivity()).getAppDatabase()
                    .dataBaseAction()
                    .insertDataIntoTaskList(createTask);

                else
                    DatabaseClient.getInstance(getActivity()).getAppDatabase()
                    .dataBaseAction()
                    .updateAnExistingRow(taskId,addTaskTitle.getText().toString(),
                            addTaskDescription.getText().toString(),
                            phone.getText().toString(),
                            taskDate.getText().toString(),
                            taskEvent.getText().toString(),
                            charges.getText().toString());

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {

                }
                swipeRefreshLayout.refresh();
                Toast.makeText(getActivity(), "ထည့်သွင်းပြီးပါပြီ", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        }
        saveTaskInBackend st = new saveTaskInBackend();
        st.execute();
    }

    private void showTaskFromId() {
        class showTaskFromId extends AsyncTask<Void, Void, Void>{

            @Override
            protected Void doInBackground(Void... voids) {
                task = DatabaseClient.getInstance(getActivity()).getAppDatabase()
                        .dataBaseAction().selectDataFromAnId(taskId);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                setDataInUi();
            }
        }
        showTaskFromId st = new showTaskFromId();
        st.execute();
    }

    private void setDataInUi() {
        addTaskTitle.setText(task.getTaskTitle());
        addTaskDescription.setText(task.getTaskDescription());
        phone.setText(task.getPhone());
        taskDate.setText(task.getDate());

        taskEvent.setText(task.getEvent());
        charges.setText(task.getCharges());
    }


    public interface swipeRefreshListener {
        void refresh();
    }
}
