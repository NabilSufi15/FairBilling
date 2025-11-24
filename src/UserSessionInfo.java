import java.time.LocalTime;
import java.util.LinkedList;

public class UserSessionInfo {
    private String user;
    private int totalSessions;
    private long totalDuration; // in seconds
    private LinkedList<LocalTime> openSessions; // tracks active sessions

    public UserSessionInfo(String user) {
        this.user = user;
        this.totalSessions = 0;
        this.totalDuration = 0;
        this.openSessions = new LinkedList<>();
    }

    public String getUser() {
        return user;
    }

    public int getTotalSessions() {
        return totalSessions;
    }

    public long getTotalDuration() {
        return totalDuration;
    }

    public LinkedList<LocalTime> getOpenSessions() {
        return openSessions;
    }

    public void addStart(LocalTime startTime) {
        openSessions.add(startTime);
    }

    public void addEnd(LocalTime endTime, LocalTime earliestTime) {
        LocalTime startTime;
        if (!openSessions.isEmpty()) {
            startTime = openSessions.remove(); //remove earliest start timestamp
        } else {
            startTime = earliestTime; // use earliest start if no open session
        }
        long duration = java.time.Duration.between(startTime, endTime).getSeconds();
        totalDuration += duration;
        totalSessions += 1;
    }

    public String getUserInfo() {
        return user + " " + totalSessions + " " + totalDuration;
    }
}