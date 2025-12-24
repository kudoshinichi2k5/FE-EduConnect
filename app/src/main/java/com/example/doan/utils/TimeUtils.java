package com.example.doan.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TimeUtils {

    public static String formatTimeAgo(String isoTime) {
        try {
            SimpleDateFormat sdf =
                    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

            Date createdDate = sdf.parse(isoTime);
            Date now = new Date();

            long diffMillis = now.getTime() - createdDate.getTime();
            long diffMinutes = TimeUnit.MILLISECONDS.toMinutes(diffMillis);
            long diffHours = TimeUnit.MILLISECONDS.toHours(diffMillis);
            long diffDays = TimeUnit.MILLISECONDS.toDays(diffMillis);

            if (diffMinutes < 1) {
                return "Vừa xong";
            } else if (diffMinutes < 60) {
                return diffMinutes + " phút trước";
            } else if (diffHours < 24) {
                return diffHours + " giờ trước";
            } else if (diffDays == 1) {
                return "Hôm qua";
            } else if (diffDays < 7) {
                return diffDays + " ngày trước";
            } else {
                SimpleDateFormat out =
                        new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                return out.format(createdDate);
            }

        } catch (ParseException e) {
            return "";
        }
    }
}
