package edu.utexas.colin.chaacapp.model.shared;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.lang.reflect.ParameterizedType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LocalDate {

    public static final String[] MONTHS = {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    };

	public static final SimpleDateFormat FORMATTER = new SimpleDateFormat("MM/dd/yyyy");

    private final int month;
    private final int day;
    private final int year;

    public LocalDate(int month, int day, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public LocalDate() {
        day = 1;
        month = 1;
        year = 2016;
    }

	@JsonIgnore
    public String dayOfWeek() {
		String date = String.format("%02d/%02d/%04d", month, day, year);
		try {
			Date d = FORMATTER.parse(date);
			Calendar c = Calendar.getInstance();
			c.setTime(d);
			return Integer.toString(c.get(Calendar.DAY_OF_WEEK));

		} catch (ParseException ex) {
			ex.printStackTrace();
		}

		return null;
	}

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

	@JsonIgnore
    public String getMonthAsString() {
        return MONTHS[month - 1];
    }

    public int getYear() {
        return year;
    }

    @Override
    public String toString() {
        return "LocalDate{" +
                "day=" + day +
                ", month=" + month +
                ", year=" + year +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocalDate birthdate = (LocalDate) o;

        if (day != birthdate.day) return false;
        if (month != birthdate.month) return false;
        return year == birthdate.year;

    }

    @Override
    public int hashCode() {
        int result = (int) day;
        result = 31 * result + (int) month;
        result = 31 * result + year;
        return result;
    }
}
