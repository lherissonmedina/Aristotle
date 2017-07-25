/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.tools;

import static com.sun.prism.impl.Disposer.cleanUp;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import project.calculate.*;
import project.retrieve.*;

public class Guess implements Runnable{
        public static boolean showClock = false;
    
        @Override
        public void run() {
            showClock = false;
            try{
        String s = Aristotle.getQuery();
        
        String [] memoryWords = {"how", "what","about", "now"};
        
        List<String> sentence = new LinkedList<>(Arrays.asList(s.toLowerCase().split("\\s*\\s\\s*")));
        
        if (((sentence.get(0).equals("what") || sentence.get(0).equals("how")) && sentence.get(1).equals("about")) || sentence.get(0).equals("now"))
        {
            sentence.removeAll(Arrays.asList(memoryWords));
            sentence.addAll(0, Aristotle.getMemory());
            s = String.join(" ", sentence);
        }
        String guess = Parse.parse(s);
        
        Object answer;
        switch (guess){
            case "automotive":
                 answer = new Automotive(sentence);
                break;
            case "calculate":
                 answer = new Calculation(sentence);
                break;
            case "location":
                 answer = new Location(sentence);
                break;
            case "definition":
                 answer = new Definition(sentence);
                break;
            case "translate":
                 answer = new Translation(sentence);
                break;
            case "stocks":
                 answer = new Stocks(sentence);
                break;
            case "search":
                 answer = new Search(sentence);
                break;
            case "time":
                 answer = new Time(sentence);
                break;
            case "simulate":
                 answer = new Simulation(sentence);
                break;
            case "convert":
                 answer = new Conversion(sentence);
                break;
            case "weather":
                 answer = new Weather(sentence);
                break;
            default:
                 Aristotle.setAnswer("I'm sorry, I do not understand.", false);
                break;
        }
        
        String finalanswer = Aristotle.getAnswer().trim();
        if(finalanswer.equals("Let me see...")){
            Aristotle.setAnswer("I'm sorry, I do not understand; can you try rephrasing your question?", false);
        }
        
//        if (sentence.get(0).equals("search")) {
//            sentence.remove("search");
//            sentence.removeAll(Arrays.asList(badwords));
//            
//        } 
//        else if (sentence.get(0).equals("calculate")) {
//            sentence.remove("calculate");
//            sentence.removeAll(Arrays.asList(badwords));
//            
//        } else if (!Collections.disjoint(sentence, Arrays.asList(new String[]{"*", "+", "-", "/", "^","!", "x"}))){
//           
//        }else if ((sentence.get(0).contains("delete") || sentence.get(0).contains("remove")) && !Collections.disjoint(sentence, Calculation.variables.keySet())){
//            sentence.remove("delete");
//            sentence.remove("remove");
//            Calculation.removeVariable(sentence);
//        }
//        
//        else if (sentence.get(0).equals("location")) {
//            Location.printLocation();
//        } 
//        
//        else if ((sentence.get(0).equals("set") || sentence.get(0).equals("cancel") || sentence.get(0).equals("stop"))) {
//            
//            if(sentence.contains("timer"))
//            Time.setTimer(sentence);
//            else if (sentence.contains("alarm"))
//                Time.setAlarm(sentence);
//        } 
//        
//        else if (sentence.get(0).equals("translate") || sentence.get(0).equals("memtranslate")) {
//            sentence.remove(Translation.specific);
//            Translation.printTranslation(sentence);
//        } 
//        
//        else if (sentence.contains("far")) {
//            sentence.removeAll(Arrays.asList(badwords));
//            Location.printDistance(sentence);
//            
//        } 
//        
//        else if (sentence.get(0).equals("locate") || sentence.get(0).equals("where")) {
//            sentence.removeAll(Arrays.asList(badwords));
//            Location.locate(sentence);
//            
//        } 
//        
//        else if (sentence.get(0).equals("flip")) {
//            sentence.removeAll(Arrays.asList(badwords));
//            Simulation.flip(sentence);
//        } 
//        
//        else if (sentence.get(0).equals("roll")) {
//            sentence.removeAll(Arrays.asList(badwords));
//            Simulation.roll(sentence);
//        }
//        
//        else if (sentence.get(0).equals("choose")) {
//            Simulation.choose(sentence);
//        }
//        
//        else if (sentence.contains("define")){
//            sentence.removeAll(Arrays.asList(badwords));
//            Definition.define(sentence);
//           
//        }else if (sentence.contains("price") && (sentence.contains("gas")|| sentence.contains("fuel"))){
//            Automotive.fuelPrice(sentence);
//        }
//        
//        else if (sentence.containsAll(Arrays.asList(new String[]{"time"}))){
//            showClock = true;
//           Time.getTime(sentence);
//           
//        }
//        
//        else if (!Collections.disjoint(sentence, WEATHER)) {
//            sentence.remove("weather");
//            sentence.removeAll(Arrays.asList(badwords));
//            Weather.weather(sentence);
//        } 
//        
//        else if (!Collections.disjoint(sentence, STOCKS) || sentence.contains("stock")) {
//            sentence.removeAll(Arrays.asList(badwords));
//            Stocks.stocks(sentence);
//            
//        }
    } finally{
                cleanUp();
            }
        }
        
       
}
