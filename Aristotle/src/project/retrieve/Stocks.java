package project.retrieve;

import java.text.DecimalFormat;
import java.util.Arrays;
import project.tools.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Stocks {

    private String company, symbol;
    private double price;
            private int volume;
    private DecimalFormat format = new DecimalFormat("#.00");
    private final static String[] specific = {"name", "symbol", "price", "volume", "ticker"};
    private final static String[] BADWORDS = {"stock", "price", "for", "quote","of", "in", "like", "a", "with", "for", "is", "the", "how", "doing","what", "what's", "whats"};
    private String answer;
    private boolean success = true;
    private Double ans = 0.0;
    
    public Stocks(List query) {
        
            List<String> sentence = query;
            String keyword = Tools.findKeyword(specific, sentence);
            sentence.removeAll(Arrays.asList(BADWORDS));
            sentence.removeAll(Arrays.asList(specific));
            String address = "http://d.yimg.com/autoc.finance.yahoo.com/autoc?query=" + String.join(" ", sentence).replace("'s", "").replaceAll("\\s", "%20") + "&callback=YAHOO.Finance.SymbolSuggest.ssCallback";
            List<String> items = Tools.pageContent(address);
            
            if (items.size() > 6) {
                symbol = items.get(items.indexOf("symbol") + 1);
                
               address = "http://finance.yahoo.com/webservice/v1/symbols/" + symbol + "/quote?format=xml";
                String content = Tools.getPageContent(address);
                
                Matcher m = Pattern.compile("<field name=\"price\">(.*?)</field>").matcher(content);
        if (m.find()) {
           price = Double.parseDouble(m.group(1));
        }
        
        m = Pattern.compile("<field name=\"name\">(.*?)</field>").matcher(content);
        if (m.find()) {
           company = m.group(1);
        }
        
        m = Pattern.compile("<field name=\"volume\">(.*?)</field>").matcher(content);
        if (m.find()) {
           volume = Integer.parseInt(m.group(1));
        }
                
                switch(keyword){
                    case "price":
                        ans = price;
                answer = company + "'s stock price is $" + format.format(price);
                        break;
                    case "volume":
                        ans = (double)volume;
                        answer = company + "'s volume is currently at " + volume + " shares";
                        break;
                    case "ticker":
                    case "symbol":
                        answer = company + "'s stock ticker is " + symbol;
                        break;
                    case "name":
                        answer = symbol + " belongs to " + company;
                        break;
                    default:
                        answer = company + " is currently trading at $" + format.format(price) + " with a volume of " + volume + " shares";
                        break;
                }
                        
            } else {
                answer = "I could not find a corporation named " + company;
                success = false;
            }
        
        Aristotle.setMemory(new String[]{"stock"});
        Aristotle.setAnswer(answer, success);
        Suggestion.setAnswerMemory(ans.toString());
    }
}
