package project.kyawmyoag.doctormanager.Chat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import project.kyawmyoag.doctormanager.R;

/**
 * Created by user on 9/24/18.
 */

public class MessageHolder extends RecyclerView.ViewHolder {

    TextView messageText, timeText;

    public MessageHolder(final View itemView) {
        super(itemView);
        messageText = (TextView) itemView.findViewById(R.id.text_message_body);
        timeText = (TextView) itemView.findViewById(R.id.text_message_time);
    }


    void bind(BaseMessage message) {
        messageText.setText(message.getMessage());

        timeText.setText(getDateCurrentTimeZone(Long.parseLong(message.getCreatedAt())));

    }

    public  String getDateCurrentTimeZone(long timestamp) {
        try{
            Calendar calendar = Calendar.getInstance();
            TimeZone tz = TimeZone.getDefault();
            calendar.setTimeInMillis(timestamp * 1000);
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy, HH:mm");
            Date currenTimeZone = (Date) calendar.getTime();
            return sdf.format(currenTimeZone);
        }catch (Exception e) {
        }
        return "";
    }
}