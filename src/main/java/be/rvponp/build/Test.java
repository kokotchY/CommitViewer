package be.rvponp.build;

import org.joda.time.DateTime;
import org.joda.time.DurationFieldType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: canas
 * Date: 7/11/13
 * Time: 10:45 AM
 * To change this template use File | Settings | File Templates.
 */
public class Test {
    public static void main(String[] args) {
        new Test();
    }

    public Test() {
        String date = "2013-07-09T08:13:55.242656Z";
//        String date = "2013-07-09T08:13:55";
        SimpleDateFormat revisionFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS");
//        SimpleDateFormat revisionFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        try {
            Date parse = revisionFormat.parse(date);
            DateTime dateTime = new DateTime(parse);
            DateTime dateTime1 = dateTime.withFieldAdded(DurationFieldType.hours(), 2);
            System.out.println(dateTime1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
