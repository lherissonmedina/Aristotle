package project.retrieve;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import project.tools.Aristotle;
import project.tools.Tools;

public class Translation {
    
    public static String language, lang;
    public static final Map<String, String> supportedLanguages = new HashMap<>();
    private static final String[] BADWORDS = {"translate", "translated", "say"};
    private static final String[] BADWORDS2 = {"into", "to", "in"};
    private static String answer;
    private static boolean success = true;
    
    public Translation(List query) {
        List<String> sentence = query;
        success = true;
        try {
            
            if (supportedLanguages.isEmpty())
            getLanguages();
            
            if (supportedLanguages.containsKey(sentence.get(sentence.size()-1))){
              language = sentence.get(sentence.size() - 1);
              sentence.remove(language);
              
              for(String bad: BADWORDS){
                if(sentence.get(0).equals(bad))
                sentence.remove(bad);
            }
            
            for (String badword : BADWORDS2) {
                if (sentence.get(sentence.size() - 1).equals(badword)) {
                    sentence.remove(sentence.size() - 1);
                }
            }
            }
            else{
                sentence.remove("translate");
            }
            
            
            
            
            
            if (supportedLanguages.containsKey(language)) {
                lang = supportedLanguages.get(language);
                String phrase = String.join(" ", sentence);
                if (!supportedLanguages.get(language).equals("eng")) {
                    translate(phrase, phrase.replaceAll("\\s", "%20"));
                } else {
                    answer = "You can only translate from English to other languages";
                    success = false;
                }
            } else {
                answer = "I could not find a language named " + "\"" + language + "\"";
                success = false;
            }
        } catch (IndexOutOfBoundsException ex) {
            answer = "What would you like me to translate?\nFor example you can say: Translate cell phone into Yiddish";
        }
    Aristotle.setAnswer(answer, success);
    }
    
    private static void getLanguages() {
        success = true;
        BufferedReader langFile;
        try {
            langFile = new BufferedReader(new FileReader("SupportedLanguage.txt"));
            String data;
            String[] dataPairs;
            while ((data = langFile.readLine()) != null) {
                dataPairs = data.toLowerCase().split(",");
                supportedLanguages.put(dataPairs[0], dataPairs[1]);
            }
        } catch (Exception e) {
            answer = "You seem to be missing the language file included in this program";
            success = false;
        }

    }

    private static void translate(String phrase, String urlPhrase) {
        List<String> items;
        success = true;
        try {
            String address = "https://glosbe.com/gapi/translate?from=en&dest=" + lang + "&format=json&phrase=" + urlPhrase + "&pretty=true";

            items = Tools.pageContent(address);
            do {
                items.remove(0);
            } while (!items.get(0).equalsIgnoreCase("phrase") && items.size() > 1);
            do {
                items.remove(0);
            } while (!items.get(0).equalsIgnoreCase("phrase") && items.size() > 1);

            if (items.size() > 2) {
                answer = items.get(items.indexOf("text") + 1);
                answer = "\"" + phrase + "\" in " + language + " is " + "\"" + answer + "\"";
                Aristotle.setMemory(new String[]{"translate"});
            } else {
                answer = "Could not translate " + "\"" + phrase + "\" into " + language;
                success = false;
            }

        } catch (Exception ex) {
            answer = "I'm sorry, I'm having some difficulty translating right now";
            success = false;
        }
    }
}
