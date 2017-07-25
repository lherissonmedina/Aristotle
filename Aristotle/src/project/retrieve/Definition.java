package project.retrieve;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import project.tools.Aristotle;
import project.tools.Tools;

public class Definition {

    public final String[] specific = {"define","definition","of", "word", "what", "is", "the"};
    private final String apikey = "a2a73e7b926c924fad7001ca3111acd55af2ffabf50eb4ae5";
    private String answer;

    public Definition(List query) {
        List<String> sentence = query;
        boolean success = true;
        List<String> definitions = new ArrayList<>();
        sentence.removeAll(Arrays.asList(specific));

        String word = String.join(" ", sentence);
        String address = "http://api.wordnik.com/v4/word.xml/" + word.replaceAll("\\s", "%20") + "/definitions?limit=200&includeRelated=false&sourceDictionaries=webster&useCanonical=false&includeTags=true&api_key=a2a73e7b926c924fad7001ca3111acd55af2ffabf50eb4ae5";
        String content = Tools.getPageContent(address);

        Matcher m = Pattern.compile("<text>(.*?)</text>").matcher(content);

        while (m.find()) {
            String def = m.group(1);
            if (def.length() > 10 && !def.toLowerCase().contains("&gt")) {
                definitions.add(def);
            }
        }

        if (definitions.isEmpty()) {
            answer = "Could not get definition of " + "\"" + word + "\"";
            success = false;
        } else {
            if (definitions.size() == 1) {
                answer = definitions.get(0);
            } else {
                answer = definitions.get(new Random().nextInt(definitions.size() - 1));
            }
            answer = "Definition of " + word + " is:\n" + answer;
        }

        Aristotle.setMemory(new String[]{"define"});
        Aristotle.setAnswer(answer, success);
    }
}
