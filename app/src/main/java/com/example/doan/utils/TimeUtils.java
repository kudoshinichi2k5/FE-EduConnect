package com.example.doan.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class TimeUtils {

    public static String formatTimeAgo(String isoTime) {
        if (isoTime == null || isoTime.isEmpty()) return "";

        try {
            // Hỗ trợ ISO có milliseconds + timezone (Z hoặc +07:00)
            SimpleDateFormat sdf = new SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss.SSSX",
                    Locale.getDefault()
            );
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

            Date createdDate = sdf.parse(isoTime);
            Date now = new Date();

            long diffMillis = now.getTime() - createdDate.getTime();

            // Nếu thời gian ở tương lai
            if (diffMillis < 0) {
                return "Vừa xong";
            }

            long diffMinutes = TimeUnit.MILLISECONDS.toMinutes(diffMillis);
            long diffHours   = TimeUnit.MILLISECONDS.toHours(diffMillis);
            long diffDays    = TimeUnit.MILLISECONDS.toDays(diffMillis);

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
            e.printStackTrace();
            return "";
        }
    }
}
