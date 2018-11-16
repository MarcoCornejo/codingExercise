package exercise;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

public class Exercise {
    public static void main(String[] args) {
        try {
            String url = "https://api.donorschoose.org/common/json_feed.html?keywords=\"SEARCHQUERY\"&max=5&state=CA&costToCompleteRange=0+TO+2000&APIKey=DONORSCHOOSE";
            Scanner scanner = new Scanner(System.in);
            
            System.out.println("Please enter a search query:");
            String searchQuery = scanner.next();
            url = url.replaceFirst("SEARCHQUERY", searchQuery);
            
            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder res = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                res.append(inputLine);
            }
            in.close();
            connection.disconnect();
            
            JSONObject response = new JSONObject(res.toString());
            assert (response.getInt("max") <= 5) : "ERROR: FIVE RESULTS MAXIMUM";
            JSONArray proposal = response.getJSONArray("proposals");
            
            Map<String, Double> averages = new HashMap<>();
            averages.put("percentFunded", 0d);
            averages.put("numDonors", 0d);
            averages.put("costToComplete", 0d);
            averages.put("numStudents", 0d);
            averages.put("totalPrice", 0d);
            
            for (int i=0;i<proposal.length();i++){ 
                JSONObject temp = proposal.getJSONObject(i);
                assert (temp.get("state").equals("CA")) : "ERROR: RESULTS ARE LIMITED TO CALIFORNIA";
                assert (temp.getDouble("costToComplete") > 0 && temp.getDouble("costToComplete") <= 2000) : "ERROR: FUNDING COSTS SHOULD BE WITHIN $0 TO $2000";
                System.out.println("======{ " + (i+1) + " }======");
                System.out.println("Title: " + temp.get("title"));
                System.out.println("#Donors: " + temp.get("numDonors"));
                System.out.println("Cost: $" + temp.get("costToComplete"));
                System.out.println("#Students: " + temp.get("numStudents"));
                for (Map.Entry<String, Double> entry : averages.entrySet()) {
                    entry.setValue(entry.getValue() + temp.getDouble(entry.getKey()));
                }
            }
            System.out.println("======{ Averages }======");
            averages.entrySet().forEach((entry) -> {
                entry.setValue(entry.getValue() / averages.size());
                System.out.println(entry.getKey() + ": "+ entry.getValue());
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
