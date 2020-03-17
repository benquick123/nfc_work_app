package com.app.nfcwork;

public class WorkingEntry {
    public final static int NORMAL_TYPE = 0;
    public final static int TOP_TYPE = 1;
    public final static int ERROR_TYPE = 2;

    private String userId;
    private String arriveTime;
    private String lastArriveTime;
    private String departureTime;
    private String date;
    private String totalTime;
    private String errorMessage;
    private boolean isSunday;
    private int entryType;
    private int worktimeInMinutes;

    public void setUserId(String userId1) {
        userId = userId1;
    }

    public void setArriveTime(String arriveTime1) {
        arriveTime = arriveTime1;
    }

    public void setDepartureTime(String departureTime1) {
        departureTime = departureTime1;
    }

    public void setTotalTime(String totalTime1) {
        totalTime = totalTime1;
    }

    public void setErrorMessage(String errorMessage1) { errorMessage = errorMessage1; }

    public void setLastArriveTime(String lastArriveTime1) { lastArriveTime = lastArriveTime1; }

    public void setEntryType(int entryType1) { entryType = entryType1; }

    public void setDate(String date1) { date = date1; }

    public void setIsSunday(boolean isSunday1) { isSunday = isSunday1; }

    public void setWorktimeInMinutes(int worktimeInMinutes1) { worktimeInMinutes = worktimeInMinutes1;}

    public String getUserId() {
        return userId;
    }

    public String getArriveTime() {
        return arriveTime;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public String getErrorMessage() { return errorMessage; }

    public String getLastArriveTime() { return lastArriveTime; }

    public int getEntryType() { return entryType; }

    public String getDate() { return date; }

    public boolean getIsSunday() {return isSunday; }

    public int getWorktimeInMinutes() { return worktimeInMinutes; }
}
