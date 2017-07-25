package project.tools;

import static com.sun.prism.impl.Disposer.cleanUp;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;

public class Tools{
    private static int count = 0;    
    private static final Map<String, String> Notifs = new HashMap<>();
    
    public static int getCount(){
        return count;
    }
    public static void addCount(){
        count ++;
    }
    public static void removeCount(){
        count --;
    }
    public static Map getNotif(){
        return Notifs;
    }
    public static void addNotif(String ID, String notification){
        Notifs.put(ID, notification);
    }
    public static void replaceNotif(String ID, String notification){
        Notifs.replace(ID, notification);
    }
    public static void removeNotif(String ID){
        Notifs.remove(ID);
    }
    
        public static String findKeyword(String[] keywords, List<String> query) {
        for (String key : keywords) {
            for(String q: query){
            if (q.matches(key)) {
                String keyword = q;
                return keyword;
            }
            }
        }
        return "";
    }
        
            public static Image getImage(String address) {
                Image img = null;
        try {
            URL site = new URL(address);
            img = ImageIO.read(site);
        } catch (Exception e) {
            return null;
        }
        
        return img;
    }
        
        public static String getPageContent(String address){
            String content = "";
            String line;
            
            try{
            URL url = new URL(address);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            
             while ((line = in.readLine()) != null) {
                content+= line + "\n";
            }
            }catch(Exception e){
                return null;
            }
            
            return content;
        }

    public static List pageContent(String address) {
        URL url = null;
        List<String> items = null;
        try {
            url = new URL(address);
        

        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        
            String str = "", line;
            while ((line = in.readLine()) != null) {
                str += line;
            }
        
            in.close();
        

        str = str.replaceAll("\\{", "").replaceAll("\\}", "").replaceAll("\\[", "");
        str = str.replaceAll("\\]", "").replaceAll("\"", "").replaceAll("\\:", ",");
        items = new LinkedList<String>(Arrays.asList(str.split("\\s*,\\s*")));
        } catch (Exception ex) {
            return null;
        }
        return items;
    }
    
     public static synchronized void updateNotification() {     
        new Thread(() -> {
            try {
                String notification = "";
                while(count != 0){
                    notification = "";
                    notification = Notifs.values().stream().map((str) -> str + "\n").reduce(notification, String::concat);
                    Aristotle.setNotification(notification, true);
                    Thread.sleep(1000);
                }
                Aristotle.setNotification("Done", false);
            } catch (Exception e) {
            } finally {
                cleanUp();
            }
        }).start();
    }
}
