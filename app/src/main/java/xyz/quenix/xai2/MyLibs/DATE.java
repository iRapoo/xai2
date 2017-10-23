package xyz.quenix.xai2.MyLibs;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DATE {

    //Получение номера дня недели
    public static int getWeek(Date date) {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow"), Locale.UK);

        c.setTime(date);

        int dow = (c.get(Calendar.DAY_OF_WEEK)-1);
        dow = (dow==0) ? 6 : dow-1;
        //dow = (dow == 6 || dow == 0 || (c.get(Calendar.HOUR_OF_DAY) >= 17) && dow == 5) ? 0 : (c.get(Calendar.HOUR_OF_DAY) >= 17) ? dow :(dow-1); //Выходные
        return dow;
    }

    //Получение типа недели
    public static int getWeekType(Date date) {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow"), Locale.UK);

        c.setTime(date);

        //////////////  параметры типа недели               //
        int tw = 0; //  0 - по умолчанию                    //
        int sw = 1; //  1 - по умолчанию                    //
        //////////////  поменять местами для смены позиции  //

        int dow = (c.get(Calendar.WEEK_OF_YEAR)%2);
        int tmp = (c.get(Calendar.DAY_OF_WEEK)-1);
        //dow = (tmp == 6 || tmp == 0 || (c.get(Calendar.HOUR_OF_DAY) > 17) && tmp == 5) ? ((dow == sw) ? 1 : 0 ) : ((dow == tw) ? 1 : 0);
        dow = (dow == tw) ? 1 : 0;
        return dow;
    }

    public static int getNowTime() {
        int time = -1;

        Date d = new Date();
        DateFormat f = new SimpleDateFormat("HH", Locale.UK);
        int hour = Integer.parseInt(f.format(d));
        f = new SimpleDateFormat("mm", Locale.UK);
        int minute = Integer.parseInt(f.format(d));


        DateFormat formatter = new SimpleDateFormat("HH:mm", Locale.UK);
        try {
            Date date = formatter.parse(hour+":"+minute);

            if ( formatter.parse("8:00").before(date) && formatter.parse("9:35").after(date) ) {
                time = 1; //Пара
            }
            /*if ( formatter.parse("9:35").before(date) && formatter.parse("9:50").after(date) ) {
                time = 1; //Перерыв
            }*/
            if ( formatter.parse("9:50").before(date) && formatter.parse("11:25").after(date) ) {
                time = 2; //Пара
            }
            /*if ( formatter.parse("11:25").before(date) && formatter.parse("11:55").after(date) ) {
                time = 3; //Перерыв
            }*/
            if ( formatter.parse("11:55").before(date) && formatter.parse("13:30").after(date) ) {
                time = 3; //Пара
            }
            /*if ( formatter.parse("13:30").before(date) && formatter.parse("13:45").after(date) ) {
                time = 5; //Перерыв
            }*/
            if ( formatter.parse("13:45").before(date) && formatter.parse("15:20").after(date) ) {
                time = 4; //Пара
            }
            /*if ( formatter.parse("15:20").before(date) && formatter.parse("17:00").after(date) ) {
                time = 7; //Перерыв
            }*/
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return time;
    }

    public static int getStudWeek(){ //Немер учебной недели
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow"), Locale.UK);
        Calendar c2 = Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow"), Locale.UK);

        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.MONTH, Calendar.SEPTEMBER);

        int week = c.get(Calendar.WEEK_OF_YEAR);
        int week2 = c2.get(Calendar.WEEK_OF_YEAR);

        int stud_week = (week2-week)+1;

        if(stud_week<0){
            c.set(Calendar.MONTH, Calendar.FEBRUARY);

            week = c.get(Calendar.WEEK_OF_YEAR);
            week2 = c2.get(Calendar.WEEK_OF_YEAR);

            stud_week = (week2-week)+1;
        }

        return stud_week;
    }

    public static int getYear(){ //Год
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
        return Integer.parseInt(dateFormat.format(new Date()));
    }
    public static int getMonth(){ //Месяц
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM");
        return Integer.parseInt(dateFormat.format(new Date()));
    }
    public static int getDay(){ //День
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
        return Integer.parseInt(dateFormat.format(new Date()));
    }

}
