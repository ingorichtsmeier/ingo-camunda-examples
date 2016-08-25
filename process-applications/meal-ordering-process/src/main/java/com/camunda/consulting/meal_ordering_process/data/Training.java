package com.camunda.consulting.meal_ordering_process.data;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import spinjar.com.fasterxml.jackson.annotation.JsonIgnore;

public class Training implements Serializable {

  private static final long serialVersionUID = 1L;

  private String trainingID;
  private Date startDate;
  private Date endDate;

  private static DecimalFormat twoDigits = new DecimalFormat("00");

  public Training(String trainingID, Date startDate, Date endDate) {
    super();
    this.trainingID = trainingID;
    this.startDate = startDate;
    this.endDate = endDate;
  }

  public Training() {
    super();
  }

  public String getTrainingID() {
    return trainingID;
  }

  public void setTrainingID(String trainingID) {
    this.trainingID = trainingID;
  }

  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public Date getEndDate() {
    return endDate;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  @JsonIgnore
  public String getEndDateAsIso8601() {
    Calendar calendar = GregorianCalendar.getInstance();
    calendar.setTime(endDate);

    return calendar.get(Calendar.YEAR) + "-" + twoDigits.format(calendar.get(Calendar.MONTH) + 1) + "-" + twoDigits.format(calendar.get(Calendar.DAY_OF_MONTH))
        + "T" + twoDigits.format(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + twoDigits.format(calendar.get(Calendar.MINUTE)) + ":"
        + twoDigits.format(calendar.get(Calendar.SECOND));
  }

  @JsonIgnore
  public List<String> getWeekdays() {
    List<String> weekdays = new ArrayList<String>();

    Calendar start = Calendar.getInstance();
    start.setTime(startDate);
    Calendar end = Calendar.getInstance();
    end.setTime(endDate);

    for (Calendar date = start; start.before(end); date.add(Calendar.DATE, 1)) {
      switch (date.get(Calendar.DAY_OF_WEEK)) {
      case Calendar.SUNDAY:
        weekdays.add("Sunday");
        break;
      case Calendar.MONDAY:
        weekdays.add("Monday");
        break;
      case Calendar.TUESDAY:
        weekdays.add("Tuesday");
        break;
      case Calendar.WEDNESDAY:
        weekdays.add("Wednesday");
        break;
      case Calendar.THURSDAY:
        weekdays.add("Thursday");
        break;
      case Calendar.FRIDAY:
        weekdays.add("Friday");
        break;
      case Calendar.SATURDAY:
        weekdays.add("Saturday");
        break;
      default:
        break;
      }
    }
    return weekdays;
  }

}
