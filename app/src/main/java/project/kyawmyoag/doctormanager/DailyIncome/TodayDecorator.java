package project.kyawmyoag.doctormanager.DailyIncome;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import project.kyawmyoag.doctormanager.R;

public class TodayDecorator implements DayViewDecorator {
    private Context mainContext;

    public TodayDecorator(Context mainContext) {
        this.mainContext = mainContext;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return day.equals(CalendarDay.today()); //check if date equal to today
    }
    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(ContextCompat.getDrawable(mainContext, R.drawable.btn_background)); //add yellow backdrop to that cell
    }
}
