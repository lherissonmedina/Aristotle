package project.retrieve;

import java.awt.Image;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import project.tools.*;

public class Location {

    private final DecimalFormat format = new DecimalFormat("#.00");
    private static String address = "", place;
    private static int mapZoom = 13;
    private final int earthRadius = 6371;
    public String[] BADWORDS = {"locate", "far", "away", "here", "me", "distance", "location", "where", "is", "what", "how", "my"};
    public static final Map<String, String> My = new HashMap<>();
    private static String answer = "";
    private Image img;
    private static boolean success = true;
    private Double ans = 0.0;
    private List<String> sentence;
    private static String THAT;
    
    public Location(List query){
        success = true;
        sentence = query;
        if ((query.contains("far") ||query.contains("distance")) && query.contains("car")) {
            sentence.removeAll(Arrays.asList(BADWORDS));
            printCarDistance();
        } 
        if ((query.contains("far") ||query.contains("distance"))) {
            sentence.removeAll(Arrays.asList(BADWORDS));
            printDistance();
        } 
        else if (query.get(0).equals("locate") || query.get(0).equals("where")) {
            sentence.removeAll(Arrays.asList(BADWORDS));
            locate();
            
        } else if (query.get(0).equals("location")) {
            sentence.removeAll(Arrays.asList(BADWORDS));
            printLocation();
        } 
        
    }
    private void printCarDistance(){
        double[] coord1 = null;
        double[] coord2 = null;
        String adr1 = null, adr2 = null;
        String sent = String.join(" ", sentence);
        String distance = null, duration = null;
        
        if(sent.startsWith("from"))
        sent = sent.substring(0,sent.length()-1);
        
        sent = sent.replace("by", "");
        sent = sent.replace("car", "");
        Matcher m1 = Pattern.compile("(.*) to (.*)").matcher(sent);
        Matcher m2 = Pattern.compile("(.*) from (.*)").matcher(sent);
        if (m1.find()){
            getCoordinates(m1.group(1).trim());
            adr1 = address;
            getCoordinates(m1.group(2).trim());
            adr2 = address;
            if(sentence.contains("that") && !THAT.isEmpty()){
            adr1 = THAT;
        }
        }else if (m2.find()){
            getCoordinates(m2.group(1).trim());
            adr1 = address;
            getCoordinates(m2.group(2).trim());
            adr2 = address;
            if(sentence.contains("that") && !THAT.isEmpty()){
            adr1 = THAT;
        }
        }else{
            getMyLocation();
            adr1 = My.get("city") + " " + My.get("region");
            getCoordinates(sent.trim());
            adr2 = address;
            if(sentence.contains("that") && !THAT.isEmpty()){
            adr2 = THAT;
        }
        }
        
        coord1 = getCoordinates(adr1);
        coord2 = getCoordinates(adr2);
            
        try {
            String url = "https://maps.googleapis.com/maps/api/directions/xml?origin=" + adr1.replaceAll("\\s", "%20").replace(",", "") + "&destination=" + adr2.replaceAll("\\s", "%20");
            String content = Tools.getPageContent(url);

            content = content.replaceAll("(?s)<step>.*</step>", "");
            m2 = Pattern.compile("(?s)<duration>.*?<value>.*?</value>.*?<text>(.*?)</text>.*?</duration>").matcher(content);
            m1 = Pattern.compile("(?s)<distance>.*?<value>.*?</value>.*?<text>(.*?)</text>.*?</distance>").matcher(content);
            if (m1.find()) {
                distance = m1.group(1).replace(",", "");
                distance = distance.replace("mi", "").trim();
                answer = adr1 + " is " + distance + " miles ";
                if (m2.find()) {
                    duration = m2.group(1);
                    answer += "(" + duration + ")";
                }
                answer += " away from " + adr2 + " by car";
                
                m1 = Pattern.compile("(\\d+,*\\d*\\.*\\d*)").matcher(distance);
                if (m1.find()){
                Suggestion.setAnswerMemory(m1.group(1));
                Suggestion.setUnitMemory("miles");
                }
                getImage(coord1[0], coord1[1],coord2[0], coord2[1], true);
            }
        }catch(NullPointerException e){
            answer = "I'm having trouble finding that distance for you.";
            success = false;
        }
        
        if(img != null)
        Aristotle.setAnswer(answer,img, success);
        else
        Aristotle.setAnswer("Unfortunately, I can't find driving distance to that location.", false);
    }
    private void printDistance() {
        double distance;
        getMyLocation();
        double[] coord1 = {Double.parseDouble(My.get("lat")), Double.parseDouble(My.get("lon"))};
        double[] coord2 = null;
        String adr1 = null, adr2 = null;
        
        if (sentence.contains("from")){
            String places = String.join(" ", sentence);
            Matcher m = Pattern.compile("(.*) from (.*)").matcher(places);
            if(m.find()){
                String place1 = m.group(1);
                String place2 = m.group(2);
                if(sentence.contains("that") && !THAT.isEmpty()){
                place1 = THAT;
            }
                coord1 = getCoordinates(place1);
                adr1 = address;
                coord2 = getCoordinates(place2);
                adr2 = address;
            }
            
        }else{
            String place2 = String.join(" ", sentence);
            if(sentence.contains("that") && !THAT.isEmpty()){
                place2 = THAT;
            }
            coord2 = getCoordinates(place2);
            adr2 = address;
            adr1 = "your approximate location";
        }
        if (coord2 != null && coord1 != null && !sentence.isEmpty()) {
            getMyLocation();

            double dLat = Math.toRadians(coord2[0] - coord1[0]);
            double dLon = Math.toRadians(coord2[1] - coord1[1]);
            double rlat = Math.toRadians(coord1[0]);
            double rlat2 = Math.toRadians(coord2[0]);

            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(rlat) * Math.cos(rlat2);
            double c = 2 * Math.asin(Math.sqrt(a));
            ans = Double.parseDouble(format.format(earthRadius * c));
            
            getImage(coord1[0], coord1[1], coord2[0], coord2[1]);
            answer = adr2 + " is about " + ans + " km away from " + adr1;
        } else {
            if(sentence.isEmpty()){
                answer = "What distance would you like me to calculate?";
            }else if (place.isEmpty()) {
                answer = "I'm sorry, I don't understand.";
            } else{
                answer = "Could not find a location named \"" + place + "\"";
            }
            success = false;
        }

        Aristotle.setMemory(new String[]{"how","far","is"});
        Aristotle.setAnswer(answer, img, success);
        Suggestion.setAnswerMemory(ans.toString());
        Suggestion.setUnitMemory("kilometers");
    }

    public static void getMyLocation() {
        try {
            mapZoom = 13;
            String url;
            url = "http://ip-api.com/json";
            List<String> items = Tools.pageContent(url);
            
            My.put("city", items.get(items.indexOf("city") + 1));
            My.put("country", items.get(items.indexOf("country") + 1));
            My.put("region", items.get(items.indexOf("regionName") + 1));
            My.put("lon", items.get(items.indexOf("lon") + 1));
            My.put("lat", items.get(items.indexOf("lat") + 1));

        } catch (Exception ex) {
            answer = "Could not access your location";
            success = false;
        }
    }

    private static double[] getCoordinates(String plc) {
        String url;
        double lat = 0, lon=0;
        url = "http://maps.google.com/maps/api/geocode/xml?address=" + plc.replaceAll("\\s", "%20") + "&sensor=false";
        String content = Tools.getPageContent(url);
        
        Matcher m = Pattern.compile("<formatted_address>(.*)</formatted_address>").matcher(content);
        if (m.find()) {
           address = m.group(1);
        }
        m = Pattern.compile("<lat>(.*?)</lat>").matcher(content);
        if (m.find()) {
        lat = Double.parseDouble(m.group(1));
        }
        m = Pattern.compile("<lng>(.*?)</lng>").matcher(content);
        if (m.find()) {
        lon = Double.parseDouble(m.group(1));
        }
        
        if(address != null){
            return (new double[]{lat,lon});
        } else {
            return null;
        }
    }

    private void getImage(Double lat1, Double lon1, Double lat2, Double lon2, boolean markers) {
        String url;
        
        if(!markers){
         url = "https://maps.googleapis.com/maps/api/staticmap?center=" + lat1 + "," + lon1 + "&zoom=" + mapZoom + "&size=300x345&maptype=road" + "&markers=color:blue%7Clabel:%7C" + lat1 + "," + lon1;
        }else{
         url = "https://maps.googleapis.com/maps/api/staticmap?&size=300x345&maptype=roadmap&markers=color:blue%7Clabel:1%7C" + lat1 + "," + lon1 + "&markers=color:green%7Clabel:2%7C" + lat2 + "," + lon2;    
        }
        img = Tools.getImage(url);
        
         if (img == null){
            answer = "Could not get location.";
            success = false;
        }
    }
    
    private void getImage(double mylat, double mylon,double lat, double lon) {
        String url = "https://maps.googleapis.com/maps/api/staticmap?path=color:0xffffffff%7Cweight:3%7Cgeodesic:true%7C" + mylat + "," + mylon + "%7C" + lat + "," + lon + "&size=300x345&maptype=satellite";
        img = Tools.getImage(url);
        
        if (img == null){
            answer = "Could not get location.";
            success = false;
        }
    }


    private void locate() {
        try{
        if (!sentence.isEmpty()) {
            mapZoom = (int) (2.5 * sentence.size());
            place = String.join(" ", sentence);
            double[] coord = getCoordinates(place);
            getImage(coord[0], coord[1], null, null, false);
            THAT = address;
            answer = "Found place matching \"" + place + "\":\n" + address;
        } else {
            printLocation();
        }
        }catch(NullPointerException ex){
            answer = "Could not find place matching \"" + place + "\"";
            success = false;
        }
        
        Aristotle.setMemory(new String[]{"locate"});
        Aristotle.setAnswer(answer, img, success);
    }

    public static String getCity(String location) {
        if (location.isEmpty()) {
                getMyLocation();
                return (My.get("city") + " " + My.get("region"));
            
        } else {
            place = location;
            getCoordinates(place);
            return(address);
        }
    }

    private void printLocation() {
        if(My.isEmpty())
        getMyLocation();
        
        answer = "You are near " + My.get("city") + ", " + My.get("region") + " " + My.get("country") + "\nLongitude: " + My.get("lon")+ "\nLatitude: " + My.get("lat");
        getImage(Double.parseDouble(My.get("lat")), Double.parseDouble(My.get("lon")), null, null, false);
        Aristotle.setAnswer(answer, img, success);
    }

}
