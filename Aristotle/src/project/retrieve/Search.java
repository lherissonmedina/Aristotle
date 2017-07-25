package project.retrieve;

import project.tools.*;
import java.awt.Desktop;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Search {

    private final static String[] specific = {"google", "bing", "yahoo", "duckduckgo"};
    private final static String[] BADWORDS = {"search", "for", "go", "to"};
    private static String answer;
    private static boolean success = true;

    public Search(List<String> sen) {
        success = true;
        String query;
        List<String> sentence = sen;

        sentence.removeAll(Arrays.asList(BADWORDS));
        String keyword = Tools.findKeyword(specific, sentence);
        sentence.remove(keyword);
        query = String.join(" ", sentence).trim();

        try {
            if (query.matches(".*\\.+.*")) {
                query = query.replace(" ", "");
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().browse(new URI("http://" + query));
                }
            } else {
                query = query.replaceAll("\\s", "%20");
                switch (keyword) {
                    case "google":

                        google(query);
                        break;
                    case "bing":
                        bing(query);
                        break;
                    case "yahoo":
                        yahoo(query);
                        break;
                    case "duckduckgo":
                        duckduckgo(query);
                        break;
                    default:
                        google(query);
                        break;
                }
            }
            answer = "Here you go";
        } catch (Exception ex) {
            answer = "Could not open page";
            success = false;
        }

        Aristotle.setMemory(new String[]{"search"});
        Aristotle.setAnswer(answer, success);
    }

    private void google(String search) throws Exception {
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(new URI("https://www.google.com/?gws_rd=ssl#q=" + search));
        }
    }

    private void bing(String search) throws Exception {
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(new URI("http://www.bing.com/search?q=" + search));
        }
    }

    private void yahoo(String search) throws Exception {
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(new URI("https://search.yahoo.com/search;_ylt=AwrBT.GL0iVVVTQAYplXNyoA;_ylc=X1MDMjc2NjY3OQRfcgMyBGZyA3lmcC10LTkwMQRncHJpZANyN3AwM2Q4N1JqS2MwZVZkVEcwYmdBBG5fcnNsdAMwBG5fc3VnZwMxMARvcmlnaW4Dc2VhcmNoLnlhaG9vLmNvbQRwb3MDMARwcXN0cgMEcHFzdHJsAwRxc3RybAM0BHF1ZXJ5A2JsdWUEdF9zdG1wAzE0Mjg1NDIxMDI-?p=" + search));
        }
    }

    private void duckduckgo(String search) throws Exception {
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(new URI("https://duckduckgo.com/?q=" + search + "&ia=meanings"));
        }
    }

}
