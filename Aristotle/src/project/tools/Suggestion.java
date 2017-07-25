package project.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

public class Suggestion {

    private static String suggestion;
    private static final Set<String> completions = new TreeSet<>();
    private final static String[] fallbackComp = {"translate", "locate", "choose between", "define", "flip a coin", "roll a die", "weather in", "set a timer", "stock price for", "search for", "how far", "where is"};
    private static String answerMemory = "", unitMemory = "";
//    public static void suggest(String word) {
//        boolean change = false;
//        if (word.startsWith("translate")) {
//            suggestion = "Suggestion:\nTranslate hello into german";
//            change = true;
//        } else if (word.startsWith("how far")) {
//            suggestion = "Suggestion:\nHow far is california";
//            change = true;
//        } else if (word.startsWith("set")) {
//            suggestion = "Suggestion:\nSet a timer for 2 minutes and 30 seconds";
//            change = true;
//        } else if (word.startsWith("flip")) {
//            suggestion = "Suggestion:\nFlip 20 coins";
//            change = true;
//        } else if (word.startsWith("roll")) {
//            suggestion = "Suggestion:\nRoll 150 dice";
//            change = true;
//        } else if (word.startsWith("search")) {
//            suggestion = "Suggestion:\nSearch Yahoo for watermelons";
//            change = true;
//        } else if (word.startsWith("locate")) {
//            suggestion = "Suggestion:\nLocate Paris France";
//            change = true;
//        } else if (word.startsWith("where")) {
//            suggestion = "Suggestion:\nWhere is Egypt";
//            change = true;
//        } else if (word.startsWith("choose")) {
//            suggestion = "Suggestion:\nChoose between soda, milkshake, or water";
//            change = true;
//        } else if (word.startsWith("define")) {
//            suggestion = "Suggestion:\nDefine Manatee";
//            change = true;
//        }
//
//        if (!change) {
//            suggestion = "How may I help you?";
//        }
//
//        Aristotle.setSuggestion(suggestion, change);
//    }
    public static void autoComplete(String word) {
        suggestion = "";
        if (completions.isEmpty()) {
            getAutocompletions();
        }

        boolean change = false;

        try {
            if (word.isEmpty()){
            Aristotle.setAutocomplete(answerMemory.substring(word.length()), true);
            }else{
            for (String completion : completions) {
                if (completion.startsWith(word) && !completion.equals(word)) {
                    suggestion = completion.substring(word.length());
                    change = true;
                    break;
                }else if(answerMemory.startsWith(word)){
                Aristotle.setAutocomplete(answerMemory.substring(word.length()), true);
                break;
                } }}
        } catch (StringIndexOutOfBoundsException e) {

        }
        if (change) {
            Aristotle.setAutocomplete(suggestion, change);
        } else {
            Aristotle.setAutocomplete("", change);
        }
    }

    private static void getAutocompletions() {
        BufferedReader compFile;
        try {
            compFile = new BufferedReader(new FileReader("autoComplete.txt"));
            String data;
            while ((data = compFile.readLine()) != null) {
                completions.add(data);
            }
            compFile.close();
        } catch (Exception e) {
            completions.addAll(Arrays.asList(fallbackComp));
            try {
                try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("autoComplete.txt", true)))) {
                    for (String newComp : fallbackComp) {
                        out.print(newComp + "\n");
                    }
                    out.flush();
                }
            } catch (IOException ex) {
            }

        }

    }

    public static void add2Autocomplete(String newComp) {
        if (!newComp.contains("now")) {
            if (!Aristotle.getMemory().contains("calculate")) {
                int listSize = completions.size();
                newComp = newComp.trim();
                
                completions.add(newComp);
                
                if (completions.size() != listSize) {
                    try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("autoComplete.txt", true)))) {
                        out.print(newComp + "\n");
                    } catch (IOException e) {
                    }
                }
            }
        }
    }
    
    public static void setAnswerMemory(String mem) {
        unitMemory = null;
        answerMemory = mem;
    }
    public static String getAnswerMemory() {
        return answerMemory;
    }
    public static void setUnitMemory(String mem) {
       unitMemory = mem;
    }
    public static String getUnitMemory() {
        return unitMemory;
    }
}
