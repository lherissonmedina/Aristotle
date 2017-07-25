/*
 * This class can give the user the current time and create and remove timers and alarms
 */
package project.calculate;

import static com.sun.prism.impl.Disposer.cleanUp;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import project.tools.Aristotle;
import project.tools.Tools;

public class Time {
    private static boolean success = true, alarmCancel = false, timerCancel = false;
    private static final Random r = new Random();
    public final static String[] badwords = {"set", "timer", "alarm", "for", "in", "at"};
    private static final String[] IDlan = {"a","b","c", "d", "e", "f", "g", "h", "i", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
    private static final SimpleDateFormat format = new SimpleDateFormat("h:mm:ss a");
    private static final Lock notifLock = new ReentrantLock();
    
    public Time(List query) {
        if (query.contains("set") || query.contains("cancel") || query.contains("stop")) {
            if (query.contains("timer")) {
                setTimer(query);
            } else if (query.contains("alarm")) {
                setAlarm(query);
            }
        } else if (query.containsAll(Arrays.asList(new String[]{"time"}))) {
            getTime(query);
        }else{
            Aristotle.setAnswer("You can set a Timer or an Alarm, you can also get the current time.", false);
        }
    }
    private void getTime(List query) {
        int queue = Aristotle.getQueue();
        Aristotle.setMemory(new String[]{"what", "time"});
        List<String> sentence = query;
        
        while(queue == Aristotle.getQueue()){
        String answer = "It's currently " + new SimpleDateFormat("h:mm:ss a").format(new Date());
        Aristotle.setAnswer(answer, success);
        try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Time.class.getName()).log(Level.SEVERE, null, ex);
                }
        }
    }
    
    private void setAlarm(List query) {
        String ID = "";
        for(int i = 0; i<10; i++)
            ID += IDlan[r.nextInt(17)];
        
        
        String hr="0", min="0";
        String post="";
        
        String str = String.join(" ", query);
        
        if (str.contains("cancel") || str.contains("stop")) {
            alarmCancel = true;
        } else {
            alarmCancel = false;
        }
        
        if(!alarmCancel){
        Matcher m = Pattern.compile("(\\d*):").matcher(str);
        if (m.find()) {
            hr = m.group(1);
            
                m = Pattern.compile(":(\\d*)").matcher(str);
                if (m.find()) {
                    min = m.group(1);
                    if (min.length()<2)
                min = String.valueOf(Integer.parseInt(min)*10);
            
                }
                m = Pattern.compile("(am)|(pm)|(morning)|(afternoon)").matcher(str);
                if (m.find()) {
                    post = m.group();
                }
        }
        
        switch(post){
                        case "pm":
                        case "afternoon":
                            post = "PM";
                            break;
                        default:
                            post = "AM";
                            break;
                    }
        
        String time = hr + ":" + min + " " + post;
        if(Integer.parseInt(hr)>12 || Integer.parseInt(min)>60 || Integer.parseInt(hr) == 0){
            Aristotle.setAnswer(hr + ":" + min + " " + post + " is not a proper time value", false);
        }else {
            notifLock.lock();
            Tools.addCount();
            Tools.addNotif(ID, "");
            notifLock.unlock();
            if(Tools.getCount() == 1) Tools.updateNotification();
            
            Aristotle.setAnswer(time + " Alarm set", success);
            while (!(format.format(new Date())).matches(hr + ":" + min + ":00 " + post) && !alarmCancel) {
                    String answer = "Alarm: " + time;
                    Tools.replaceNotif(ID, answer);
                
               try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Time.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
            notifLock.lock();
            Tools.removeCount();
            Tools.removeNotif(ID);
            notifLock.unlock();
            
            if(!alarmCancel){
            Aristotle.setAnswer(time + " Alarm ", success);
            play("alarm.wav");
            }else{
               Aristotle.setAnswer(time + " alarm stopped", success); 
               alarmCancel = false;
            }
        }
        }
        
        
    }

    private void setTimer(List query) {
        String ID = "";
        for(int i = 0; i<10; i++)
            ID += IDlan[r.nextInt(17)];
        
        success = true;
        
        List<String> sentence = query;
        if (sentence.contains("cancel") || sentence.contains("stop")) {
            timerCancel = true;
        } else {
            timerCancel = false;
        }

        if (!timerCancel) {
            Aristotle.setMemory(new String[]{"set", "timer"});
            String answer = "Set a timer for ";
            long time = 0;
            String sec = null, min = null, hrs = null;

            if (sentence.contains("hours")) {
                hrs = sentence.get(sentence.indexOf("hours") - 1);
                time += Long.parseLong(hrs) * 3.6e+6;
                answer += "\n" + hrs + " hours ";
            } else if (sentence.contains("hour")) {
                try {
                    hrs = sentence.get(sentence.indexOf("hour") - 1);
                    time += Long.parseLong(hrs) * 3.6e+6;
                    answer += "\n" + hrs + " hours ";
                } catch (Exception e) {
                    time += 3.6e+6;
                    answer += "\n" + "1 hour ";
                }
            }

            if (sentence.contains("minutes")) {
                min = sentence.get(sentence.indexOf("minutes") - 1);
                time += Long.parseLong(min) * 60000;
                answer += "\n" + min + " minutes ";
            } else if (sentence.contains("minute")) {
                try {
                    min = sentence.get(sentence.indexOf("minute") - 1);
                    time += Long.parseLong(min) * 60000;
                    answer += "\n" + min + " minutes ";
                } catch (Exception e) {
                    time += 60000;
                    answer += "\n" + "1 minute ";
                }
            }

            if (sentence.contains("seconds")) {
                sec = sentence.get(sentence.indexOf("seconds") - 1);
                time += Long.parseLong(sec) * 1000;
                answer += "\n" + sec + " seconds";
            } else if (sentence.contains("second")) {
                try {
                    sec = sentence.get(sentence.indexOf("second") - 1);
                    time += Long.parseLong(sec) * 1000;
                    answer += "\n" + sec + " seconds";
                } catch (Exception e) {
                    time += 1000;
                    answer += "\n" + "1 second";
                }
            }

            if (time == 0) {
                time = 60*1000;
                answer += "60 second";
            }
            
            long endTime = System.currentTimeMillis() + time;

            Aristotle.setAnswer(answer, success);
            notifLock.lock();
            Tools.addCount();
            Tools.addNotif(ID, "");
            notifLock.unlock();
            if(Tools.getCount() == 1) Tools.updateNotification();
            while (System.currentTimeMillis() < endTime && !timerCancel) {
                int timeRemaining = (int) (endTime - System.currentTimeMillis()) / 1000;
                Tools.replaceNotif(ID, "Timer: " + timeRemaining);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Time.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            notifLock.lock();
            Tools.removeCount();
            Tools.removeNotif(ID);
            notifLock.unlock();
            
            if(!timerCancel){
            play("alarm.wav");
            Aristotle.setAnswer("Timer complete", success);
            }else{
                timerCancel = false;
                Aristotle.setAnswer("Timer stopped", success);  
            }
        }
    }

    private synchronized void play(final String fileName) {     
     
                try {
                    Clip clip = AudioSystem.getClip();

                    AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File(fileName));

                    clip.open(inputStream);
                    clip.loop(2);
                    Thread.sleep(11000);

                    inputStream.close();
                    clip.stop();
                    clip.flush();
                    clip.close();
                } catch (Exception e) {
                } finally {
                    cleanUp();
                }
            }

}