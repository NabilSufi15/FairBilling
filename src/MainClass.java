import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalTime;
import java.util.Scanner;
import java.util.HashMap;
import java.util.LinkedList;

public class MainClass {
    public static void main(String[] args) throws FileNotFoundException {

        //Scanner to read file
        //Scanner input = new Scanner(new File("out/production/Fair Billing/test.txt"));

        // Map to hold each user's session info
        HashMap<String, UserSessionInfo> users = new HashMap<>();
        // earliest start time
        LocalTime earliestStartTime = null;
        //latest start time
        LocalTime latestTime = null;

        //if empty
        if (args.length == 0) {
            System.out.println("Please specify a file path: ");
            System.out.println("java MainClass.java <filepath>");
            return;
        }

        String filePath = args[0];
        File file= new File(filePath);
        if (!file.exists()) {
            System.out.println("Error: File '" + filePath + "' does not exist.");
            System.out.println("Please enter a valid filepath.");
            return;
        }

        //reads input file
        Scanner input = new Scanner(file);

        //loop through each line of the file
        while (input.hasNextLine()) {
            String line = input.nextLine();
            if (line.isEmpty()) continue;

            String[] parts = line.split(" ");
            if (parts.length != 3) continue;

            String timeStr = parts[0];
            String user = parts[1];
            String sessionState = parts[2];

            //if time is not a valid format continue to next line
            if (!isValidTimeStamp(timeStr)){
                //System.out.println("Error: Invalid time format. "+timeStr);
                continue;
            }

            //convert string to LocalTime
            LocalTime timeStamp = LocalTime.parse(timeStr);

            //if value is not start and end then continue to next line
            if (!sessionState.equalsIgnoreCase("Start") && !sessionState.equalsIgnoreCase("End")){
                //System.out.println("Error: must be Start or End. "+timeStr);
                continue;
            }

            // Update earliest time
            if (earliestStartTime == null || timeStamp.isBefore(earliestStartTime)) {
                earliestStartTime = timeStamp;
            }

            //store latest time stamp
            if  (latestTime == null || timeStamp.isAfter(latestTime)) {
                latestTime = timeStamp;
            }

            // Create user if not exist
            users.putIfAbsent(user, new UserSessionInfo(user));
            //get current user
            UserSessionInfo sessionInfo = users.get(user);

            // Handle Start / End events
            if (sessionState.equalsIgnoreCase("Start")) {
                sessionInfo.addStart(timeStamp);
            } else if (sessionState.equalsIgnoreCase("End")) {
                sessionInfo.addEnd(timeStamp, earliestStartTime);
            }
        }

        // Handle any open sessions that are still running
        for (UserSessionInfo sessionInfo : users.values()) {
            LinkedList<LocalTime> openSessions = sessionInfo.getOpenSessions();
            for (LocalTime startTime : openSessions) {
                sessionInfo.addEnd(latestTime, startTime);
            }
        }

        //System.out.println("Earliest Start Time: " + earliestStartTime + ", Latest Time: " + latestTime);
        //System.out.println("User Sessions Report:");
        for (UserSessionInfo sessionInfo : users.values()) {
            System.out.println(sessionInfo.getUserInfo());
        }
    }

    public static boolean isValidTimeStamp(String s) {
        return s.matches("^([01]\\d|2[0-3]):[0-5]\\d:[0-5]\\d$");
    }
}
