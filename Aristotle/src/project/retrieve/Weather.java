package project.retrieve;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import project.tools.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Weather {

    private final DecimalFormat format = new DecimalFormat("#.00");
    private final String[] specific = {"temperature", "pressure", "humidity", "highs", "lows", "high", "low", "tomorrow", "forecast", "sunset", "sunrise", "wind"};
    private final String[] BADWORDS = {"for","on", "what", "is", "the", "in", "at", "outside", "weather","of", "like", "how's"};
    private final String[] dayOfweek = {"tomorrow'*s*", "sunday'*s*", "monday'*s*", "tuesday'*s*", "wednesday'*s*", "thursday'*s*", "friday'*s*", "saturday'*s*"};
    private String content, location, tomorrow, today;
    private String answer = "";
    private boolean success = true;
    private Double ans = 0.0;
    private final String[] patterns = {"xmlns:yweather=\".*?\"", "(?s).*?(</ttl>)",
        "(<link>).*?(</link>)", "(?s)(<image>).*?(</image>)", "(?s)(<item>).*?(</pubDate>)",
        "(?s)(<description>).*?(<BR />)", "<yweather:",
        "code=\"\\d+\"", "units.*", "<.*?/>", "/>", "date=\".+?\"", "(?s)\\(provided.*"};
    private Matcher m = null;

    public Weather(List query) {
        today = new SimpleDateFormat("EEE").format(new Date());

        success = true;
        answer = "";

        List<String> sentence = query;
        String keyword = Tools.findKeyword(specific, sentence);
        String later = Tools.findKeyword(dayOfweek, sentence);
        sentence.remove(later);
        sentence.removeAll(Arrays.asList(specific));
        sentence.removeAll(Arrays.asList(BADWORDS));

        location = Location.getCity(String.join(" ", sentence));
        String address = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22" + location.replace(",", "").replaceAll("\\s", "%20") + "%2C%20%22)&format=xml&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";

        content = Tools.getPageContent(address);
        if (!content.contains("temp")) {
            answer = "Cannot find a location named " + "\"" + location + "\"";
            success = false;
        } else {
            for (String pat : patterns) {
                content = content.replaceAll(pat, "");
            }
            content = content.replaceAll("\n+", "\n");
            content = content.replaceAll(" +", " ");

            try {
                if (!later.isEmpty()) {
                    Forecast(later);
                } else {
                    switch (keyword) {
                        case "humidity":
                            Humidity();
                            break;
                        case "temperature":
                            Temperature();
                            break;
                        case "pressure":
                            Pressure();
                            break;
                        case "highs":
                        case "high":
                            High();
                            break;
                        case "lows":
                        case "low":
                            Low();
                            break;
                        case "tomorrow":
                            Tomorrow();
                            break;
                        case "sunrise":
                            Sunrise();
                            break;
                        case "sunset":
                            Sunset();
                            break;
                        case "wind":
                            Windspeed();
                            break;
                        default:
                            Forecast();
                            break;
                    }
                }
            } catch (Exception e) {
                answer = "Cannot get that information at the moment.";
                success = false;
            }

            if (answer.isEmpty()) {
                answer = "I could not get the " + keyword + " for " + location + " at this time";
                success = false;
            }
        }

        Aristotle.setAnswer(answer, success);
        Aristotle.setMemory(new String[]{"weather", keyword});
    }

    private void Temperature() {
        m = Pattern.compile("temp=\"(.+?)\"").matcher(content);
        if (m.find()) {
            ans = Double.parseDouble(m.group(1));
            m = Pattern.compile("text=\"(.+?)\"").matcher(content);
            if (m.find()) {
                answer = "and " + m.group(1);
            }
            answer = "It's currently " + format.format(ans) + " °F " + answer + " in " + location;
            Suggestion.setAnswerMemory(ans.toString());
            Suggestion.setUnitMemory("fahrenheit");
        }
    }

    private void Pressure() {
        m = Pattern.compile("pressure=\"(.+?)\"").matcher(content);
        if (m.find()) {
            ans = Double.parseDouble(m.group(1)) * 0.0334172218;
            answer = "Pressure is currently " + format.format(ans) + " atm" + " in " + location;
        }
    }

    private void Sunset() {
        m = Pattern.compile("sunset=\"(.+?)\"").matcher(content);
        if (m.find()) {
            String sunset = m.group(1);
            answer = "Sunset is at " + sunset + " in " + location;
        }
    }

    private void Sunrise() {
        m = Pattern.compile("sunrise=\"(.+?)\"").matcher(content);
        if (m.find()) {
            String sunrise = m.group(1);
            answer = "Sunrise is at " + sunrise + " in " + location;
        }
    }

    private void Windspeed() {
        m = Pattern.compile("speed=\"(.+?)\"").matcher(content);
        if (m.find()) {
            ans = Double.parseDouble(m.group(1));
            m = Pattern.compile("wind chill=\"(.+?)\"").matcher(content);
            if (m.find()) {
                answer = " with a wind chill of " + format.format(m.group(1)) + " °F";
            }
            answer = "Wind speed is at " + format.format(ans) + " mph" + answer + " in " + location;
        }
    }

    private void High() {
        m = Pattern.compile("High: (\\d+)").matcher(content);
        if (m.find()) {
            ans = Double.parseDouble(m.group(1));
            m = Pattern.compile("Low: (\\d+)").matcher(content);
            if (m.find()) {
                answer = "and " + format.format(m.group(1)) + " °F " + " for the lows ";
            }
            answer = "The high for today is " + format.format(ans) + " °F " + answer + "in " + location;
            Suggestion.setAnswerMemory(ans.toString());
            Suggestion.setUnitMemory("fahrenheit");
        }
    }

    private void Low() {
        m = Pattern.compile("Low:(\\d?)").matcher(content);
        if (m.find()) {
            ans = Double.parseDouble(m.group(1));
            m = Pattern.compile("High:(\\d?)").matcher(content);
            if (m.find()) {
                answer = "and " + format.format(m.group(1)) + " °F " + " for the highs ";
            }
            answer = "The low for today is " + format.format(ans) + " °F " + answer + "in " + location;
            Suggestion.setAnswerMemory(ans.toString());
            Suggestion.setUnitMemory("fahrenheit");
        }
    }

    private void Humidity() {
        m = Pattern.compile("humidity=\"(.+?)\"").matcher(content);
        if (m.find()) {
            ans = Double.parseDouble(m.group(1));
            answer = "It is currently " + format.format(ans) + " % humid " + "in " + location;
        }
    }

    private void Tomorrow() {
        answer = "The forecast for tomorrow is " + tomorrow + " in " + location;
    }

    private void Forecast(String day) {
        if (day.matches("tomorrow'*s*")) {
            Calendar calendar = Calendar.getInstance();
            day = dayOfweek[calendar.get(Calendar.DAY_OF_WEEK) + 1];
            day = day.replace("'*s*", "");
        }
        String d = day.substring(0, 1).toUpperCase() + day.substring(1, 3).toLowerCase();
        if (!content.contains(d)) {
            answer = "I'm sorry, I cannot look that far ahead.";
            success = false;
        } else {
            day = day.replace("'s", "");
            answer = day.substring(0, 1).toUpperCase() + day.substring(1, day.length()) + "'s forecast for " + location + ":";
            m = Pattern.compile(d + " - (.*?)\\.").matcher(content);
            if (m.find()) {
                answer += "\n" + m.group(1);
                m = Pattern.compile(d + " - .*? High: (\\d+?) ").matcher(content);
                if (m.find()) {
                    answer += "\nHighs: " + m.group(1) + " °F";
                }
                m = Pattern.compile(d + " - .*? High: .*? Low: (\\d+)").matcher(content);
                if (m.find()) {
                    answer += "\nLows: " + m.group(1) + " °F";
                }
            }
        }
    }

    private void Forecast() {
        m = Pattern.compile(today + " - (.*?)\\.").matcher(content);
        if (m.find()) {
            answer = "Today's forecast for " + location + ":";
            answer += " " + m.group(1);
            m = Pattern.compile(today + " - .*? High: (\\d+?) ").matcher(content);
            if (m.find()) {
                answer += "\nHighs: " + m.group(1) + " °F";
            }
            m = Pattern.compile(today + " - .*? High: .*? Low: (\\d+)").matcher(content);
            if (m.find()) {
                answer += "\nLows: " + m.group(1) + " °F";
            }

            m = Pattern.compile("temp=\"(.+?)\"").matcher(content);
            if (m.find()) {
                ans = Double.parseDouble(m.group(1));
                answer += "\nIt's currently " + format.format(ans)  + " °F ";
                m = Pattern.compile("text=\"(.+?)\"").matcher(content);
                if (m.find()) {
                    answer += "and " + m.group(1);
                }
                Suggestion.setAnswerMemory(ans.toString());
            Suggestion.setUnitMemory("fahrenheit");
            }
        }
    }
}
