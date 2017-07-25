/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.retrieve;

import java.awt.Image;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import project.tools.Aristotle;
import project.tools.Suggestion;
import project.tools.Tools;

/**
 *
 * @author Lherisson
 */
public class Automotive {
        private String answer = "";
    private boolean success = true;
    private Double ans = 0.0;
    private final Pattern gradePattern = Pattern.compile("(<regular>|<premium>|<midgrade>|<diesel>)(\\d+\\.*\\d*)(</regular>|</premium>|</midgrade>|</diesel>)");
    
    public Automotive(List query){
        success = true;
        if (!Collections.disjoint(query, Arrays.asList(new String[]{"fuel", "gas","price"})))
            fuelPrice(query);
        
            
        
        
        Aristotle.setAnswer(answer, success);
    }
    
    private void fuelPrice(List query){
         List<String> sentence = query;
         
        String address = "http://www.fueleconomy.gov/ws/rest/fuelprices";
        List<String> items = Tools.pageContent(address);
        
        Matcher matcher = null;
        
         if (sentence.contains("premium")){
             answer = "premium";
             matcher = Pattern.compile("(<premium>)(\\d+\\.*\\d*)(</premium>)").matcher(String.join("", items)); 
         }else if (sentence.contains("diesel")){
             answer = "diesel";
             matcher = Pattern.compile("(<diesel>)(\\d+\\.*\\d*)(</diesel>)").matcher(String.join("", items)); 
         }else if (sentence.contains("midgrade") || sentence.contains("mid-grade")){
             answer = "midgrade";
             matcher = Pattern.compile("(<midgrade>)(\\d+\\.*\\d*)(</midgrade>)").matcher(String.join("", items)); 
         }else{
            answer = "regular";
            matcher = Pattern.compile("(<regular>)(\\d+\\.*\\d*)(</regular>)").matcher(String.join("", items)); 
         }
         
        if (matcher.find()){
        ans = Double.parseDouble(matcher.group(2));
        Suggestion.setAnswerMemory(ans.toString());
        answer = "The national average price for " + answer + " fuel is $" + ans;
        }
        else{
        answer = "Could not get national gas price for that grade";
        success = false;
        }
    }
}
