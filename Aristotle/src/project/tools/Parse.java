
package project.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import project.calculate.Calculation;

public class Parse {
    private Parse(){}
    
    public static final List<String> CALCULATE = new ArrayList<>(Arrays.asList(new String[]{"calculate","delete", "remove","(",")" ,"^", "*", "/", "+", "-"}));
    public static final List<String> WEATHER = new ArrayList<>(Arrays.asList(new String[]{"weather","temperature", "pressure", "humidity", "highs", "lows", "high", "low", "tomorrow", "forecast", "sunset", "sunrise", "wind", "for"}));
    public static final List<String> AUTOMOTIVE = new ArrayList<>(Arrays.asList(new String[]{"price","gas", "fuel",  "prices","gasoline","oil", "much", "how"}));
    public static final List<String> LOCATION = new ArrayList<>(Arrays.asList(new String[]{"locate", "far", "away", "from", "here", "me", "distance", "location", "where", "how"}));
    public static final List<String> STOCKS = new ArrayList<>(Arrays.asList(new String[]{"stock", "price", "name", "symbol", "volume", "ticker", "quote"}));
    public static final List<String> DEFINITION = new ArrayList<>(Arrays.asList(new String[]{"define","definition","word","mean", "meaning"}));
    public static final List<String> SEARCH = new ArrayList<>(Arrays.asList(new String[]{"search","go", "to", "google", "bing", "yahoo", "duckduckgo"}));
    public static final List<String> TRANSLATION = new ArrayList<>(Arrays.asList(new String[]{"translate", "say","translation","translated", "into", "in"}));
    public static final List<String> CONVERT = new ArrayList<>(Arrays.asList(new String[]{"convert","converted","into"}));
    public static final List<String> TIME = new ArrayList<>(Arrays.asList(new String[]{"set", "timer", "alarm","time", "wake", "for"}));
    public static final List<String> SIMULATE = new ArrayList<>(Arrays.asList(new String[]{"flip", "coin", "roll", "dice", "die", "times", "choose", "between", "or", "times"}));
    public static final List<String> BADWORDS = new ArrayList<>(Arrays.asList(new String[]{"what", "is", "the"}));
    public static final List<String> EQUATIONS = new ArrayList<>(Arrays.asList(new String[]{"\\d+\\.*\\d*","\\(","\\*","/","\\+","\\^","!","-","\\)"}));
    
       public static String parse(String sentence){
            Map<String, Integer> results = new HashMap<>();
            Stack<String> stack = new Stack();
            
            results.put("ERROR", 0);
            results.put("automotive",inCommon(AUTOMOTIVE,sentence));
            results.put("calculate",inCommon(CALCULATE,sentence));
            results.put("location",inCommon(LOCATION,sentence));
            results.put("definition",inCommon(DEFINITION,sentence));
            results.put("translate",inCommon(TRANSLATION,sentence));
            results.put("stocks",inCommon(STOCKS,sentence));
            results.put("search",inCommon(SEARCH,sentence));
            results.put("time",inCommon(TIME,sentence));
            results.put("simulate",inCommon(SIMULATE,sentence));
            results.put("convert",inCommon(CONVERT,sentence));
            results.put("weather",inCommon(WEATHER,sentence));
            
            stack.push("ERROR");
            for(String name: results.keySet())
            {
                if(results.get(stack.peek()) < results.get(name)){
                    stack.push(name);
                }  
            }
            
            return stack.peek();
            
            
        }
        
        private static int inCommon(List<String> A, String b){
            List<String> s = new ArrayList<>(Arrays.asList(b.toLowerCase().split("\\s*\\s\\s*")));
            s.removeAll(BADWORDS);
            double score = 0;
            
            if (A.contains("calculate")) {
                for (String symbol : EQUATIONS) {
                    for (String character : b.split("")) {
                        if (character.matches(symbol)) {
                            score += 1;
                        }
                        else if (character.equals("=")) {
                            score += 20;
                        }
                    }
                }
                if (!Collections.disjoint(s, Calculation.variables.keySet()))
                    score += 5;
                
                    }
            
            if(!s.isEmpty()){
            if (A.contains(s.get(0)))
                score += 10.0*(10.0-((((double)A.indexOf(s.get(0))+1)/A.size())*10.0));
            
            for(String word: s)
                if(A.contains(word)){
                    score += (((double)A.size()-(double)A.indexOf(word)+1)/(double)A.size())*10.0;
                }
            }     
            return (int)Math.round(score);
        }


}
