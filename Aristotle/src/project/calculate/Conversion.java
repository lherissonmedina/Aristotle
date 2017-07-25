/*This class converts units. It can convert distances, masses, temperatures, and time*/
package project.calculate;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import project.tools.Aristotle;
import project.tools.Suggestion;

public class Conversion {

    private final String[] BADWORDS = {"convert", "conver", "conversion", "converted", "replace", "degrees", "degree", "that"};
    private final String[] unitDistance = {"distance", "meter", "inches", "feet", "yard", "mile"};
    private final String[] unitMass = {"mass", "grams", "ounces", "pounds", "tons"};
    private final String[] unitTemperature = {"temperature", "fahrenheit", "celcius", "kelvin", "°F", "°C", "°K"};
    private final String[] unitTime = {"time", "seconds", "minutes", "hours", "days", "months", "years"};
    private final ArrayList<String[]> flavors = new ArrayList<String[]>() {
        {
            add(unitDistance);
            add(unitMass);
            add(unitTemperature);
            add(unitTime);
        }
    };
    private String answer = "";
    private boolean success = true;
    String inputUnit = null, targetUnit = null;
    Double value = null, ans = null;
    private static DecimalFormat sciformat = new DecimalFormat("0.00E0");

    public Conversion(List query) {
        try {
            List<String> sentence = query;
            sentence.removeAll(Arrays.asList(BADWORDS));
            String sent = String.join(" ", sentence);
            sent = sent.replace("into", "in");
            sent = sent.replace("to ", "in ");
            sent = sent.replace("foot", "feet");

            /*Checks if sentence contains actual units and numbers, acts accordingly*/
            if (sent.contains("in ")) {
                if (sent.startsWith("in ")) {
                    sent = Suggestion.getAnswerMemory() + " " + Suggestion.getUnitMemory() + " " + sent;
                }
                Matcher m = Pattern.compile("(.*) in (.*)").matcher(sent);

                if (m.find()) {
                    inputUnit = m.group(1).trim();
                    targetUnit = m.group(2).trim();

                    if (inputUnit.isEmpty()) {

                    } else {
                        m = Pattern.compile("(\\d+\\.*\\d*)").matcher(inputUnit);
                        if (m.find()) {
                            value = Double.parseDouble(m.group(1).trim());
                            inputUnit = inputUnit.replaceAll("(\\d+\\.*\\d*)", "").trim();
                        }
                    }
                }
            }

            if (value != null && inputUnit != null && targetUnit != null) {

                String inputFlavor = whatFlavor(inputUnit);

                if (inputFlavor.equals(whatFlavor(targetUnit))) {

                    /*formats units to make it easier*/
                    if (!inputFlavor.equals("temperature")) {
                        if (!inputUnit.endsWith("s") && !inputUnit.equals("feet")) {
                            inputUnit = inputUnit + "s";
                        }
                        if (!targetUnit.endsWith("s") && !targetUnit.equals("feet")) {
                            targetUnit = targetUnit + "s";
                        }
                    } else {
                        if (!inputUnit.contains("°")) {
                            inputUnit = "°" + inputUnit.substring(0, 1).toUpperCase();
                        }
                        if (!targetUnit.contains("°")) {
                            targetUnit = "°" + targetUnit.substring(0, 1).toUpperCase();
                        }
                    }
                    /*Checks what it needs to convert*/
                    if (inputUnit.equals(targetUnit)) {
                        ans = value;
                    } else {
                        switch (inputFlavor) {
                            case "distance":
                                convertDistance();
                                break;
                            case "mass":
                                convertMass();
                                break;
                            case "temperature":
                                convertTemperature();
                                break;
                            case "time":
                                convertTime();
                                break;
                        }
                    }
                    if (ans != null) {
                        ans = Double.parseDouble(sciformat.format(ans));
                        answer = value + " " + inputUnit + " equals " + ans + " " + targetUnit;
                        Aristotle.setMemory(new String[]{"convert"});
                        Suggestion.setAnswerMemory(String.valueOf(ans));
                        Suggestion.setUnitMemory(targetUnit);
                    } else {
                        answer = "I'm sorry I'm having trouble converting that";
                        success = false;
                    }
                } else {
                    answer = "I can't convert " + inputUnit + " to " + targetUnit;
                    success = false;
                }
            } else {
                answer = "I can't find anything to convert.";
                success = false;
            }
        } catch (Exception e) {
            answer = "What would you like me to conver?";
            success = false;
        }
        Aristotle.setAnswer(answer, success);

    }
    /*Checks against Arraylist up top to see what category unit belongs to*/

    private String whatFlavor(String unit) {
        String flave = null;

        for (String[] flavor : flavors) {
            for (String units : flavor) {
                if (unit.contains(units) || units.contains(unit)) {
                    return flavor[0];
                }
            }
        }
        return null;
    }
    /*Convert distances*/

    private void convertDistance() {

        switch (inputUnit) {
            case "millimeters":
                switch (targetUnit) {
                    case "centimeters":
                        ans = value * 0.1D;
                        break;
                    case "meters":
                        ans = value * 0.001D;
                        break;
                    case "kilometers":
                        ans = value * 1.0E-6D;
                        break;
                    case "inches":
                        ans = value * 0.0393701D;

                        break;
                    case "feet":
                        ans = value * 0.00328084D;
                        break;
                    case "yards":
                        ans = value * 0.00109361D;
                        break;
                    case "miles":
                        ans = value * 6.2137E-7D;
                        break;
                }
                break;
            case "centimeters":
                switch (targetUnit) {
                    case "millimeters":
                        ans = value * 10;
                        break;
                    case "meters":
                        ans = value * 0.01D;
                        break;
                    case "kilometers":
                        ans = value * 1.0E-5D;
                        break;
                    case "inches":
                        ans = value * 0.393701D;
                        break;
                    case "feet":
                        ans = value * 0.0328084D;
                        break;
                    case "yards":
                        ans = value * 0.0109361D;
                        break;
                    case "miles":
                        ans = value * 6.21371E-6D;
                        break;
                }
                break;
            case "meters":
                switch (targetUnit) {
                    case "centimeters":
                        ans = value * 100.0D;
                        break;
                    case "millimeters":
                        ans = value * 1000.0D;
                        break;
                    case "kilometers":
                        ans = value * 0.001D;
                        break;
                    case "inches":
                        ans = value * 39.3701D;
                        break;
                    case "feet":
                        ans = value * 3.28084D;
                        break;
                    case "yards":
                        ans = value * 1.09361D;
                        break;
                    case "miles":
                        ans = value * 6.21371E-4D;
                        break;
                }
                break;
            case "kilometers":
                switch (targetUnit) {
                    case "centimeters":
                        ans = value * 100000.0D;
                        break;
                    case "meters":
                        ans = value * 1000.0D;
                        break;
                    case "millimeters":
                        ans = value * 1000000.0D;
                        break;
                    case "inches":
                        ans = value * 39370.1D;
                        break;
                    case "feet":
                        ans = value * 3280.84D;
                        break;
                    case "yards":
                        ans = value * 1093.61D;
                        break;
                    case "miles":
                        ans = value * 0.621371D;
                        break;
                }
                break;
            case "inches":

                switch (targetUnit) {
                    case "centimeters":
                        ans = value * 2.54D;
                        break;
                    case "meters":
                        ans = value * 0.0254D;
                        break;
                    case "kilometers":
                        ans = value * 2.54E-5D;
                        break;
                    case "millimeters":
                        ans = value * 25.4D;
                        break;
                    case "feet":

                        ans = value * 0.08333D;
                        break;
                    case "yards":
                        ans = value * 0.0277778D;
                        break;
                    case "miles":
                        ans = value * 1.5783E-5D;
                        break;
                }
                break;
            case "feet":
                switch (targetUnit) {
                    case "centimeters":
                        ans = value * 30.48D;
                        break;
                    case "meters":
                        ans = value * 0.3048D;
                        break;
                    case "kilometers":
                        ans = value * 3.048E-4D;
                        break;
                    case "inches":
                        ans = value * 12.0D;
                        break;
                    case "millimeters":
                        ans = value * 304.8D;
                        break;
                    case "yards":
                        ans = value * 0.3333D;
                        break;
                    case "miles":
                        ans = value * 1.89394E-4D;
                        break;
                }
                break;
            case "yards":
                switch (targetUnit) {
                    case "centimeters":
                        ans = value * 91.44D;
                        break;
                    case "meters":
                        ans = value * 0.9144D;
                        break;
                    case "kilometers":
                        ans = value * 9.144E-4D;
                        break;
                    case "inches":
                        ans = value * 0.36D;
                        break;
                    case "feet":
                        ans = value * 0.3D;
                        break;
                    case "millimeters":
                        ans = value * 914.4D;
                        break;
                    case "miles":
                        ans = value * 5.68182E-4D;
                        break;
                }
                break;
            case "miles":
                switch (targetUnit) {
                    case "centimeters":
                        ans = value * 160934.0D;
                        break;
                    case "meters":
                        ans = value * 1609.34D;
                        break;
                    case "kilometers":
                        ans = value * 1.60934D;
                        break;
                    case "inches":
                        ans = value * 63360.0D;
                        break;
                    case "feet":
                        ans = value * 5280.0D;
                        break;
                    case "yards":
                        ans = value * 1760.0D;
                        break;
                    case "millimeters":
                        ans = value * 1609340.0D;
                        break;
                }
                break;
        }
    }
    /*Convert masses*/

    private void convertMass() {
        switch (inputUnit) {
            case "milligrams":
                switch (targetUnit) {
                    case "grams":
                        ans = value * .001D;
                        break;
                    case "kilograms":
                        ans = value * 1.0E-6D;
                        break;
                    case "ounces":
                        ans = value * 3.5274E-5D;
                        break;
                    case "pounds":
                        ans = value * 2.2046E-6D;
                        break;
                    case "tons":
                        ans = value * 1.1023E-9D;
                        break;
                }
                break;
            case "grams":
                switch (targetUnit) {
                    case "milligrams":
                        ans = value * 1000D;
                        break;
                    case "kilograms":

                        ans = value * 0.001D;
                        break;
                    case "ounces":
                        ans = value * 0.035274D;
                        break;
                    case "pounds":
                        ans = value * 0.00220462D;
                        break;
                    case "tons":
                        ans = value * 1.1023E-6D;
                        break;
                }
                break;
            case "kilograms":
                switch (targetUnit) {
                    case "grams":
                        ans = value * 1000.0D;
                        break;
                    case "milligrams":
                        ans = value * 1000000.0D;
                        break;
                    case "ounces":
                        ans = value * 35.274D;
                        break;
                    case "pounds":
                        ans = value * 2.20462D;
                        break;
                    case "tons":
                        ans = value * 0.00110231D;
                        break;
                }
                break;
            case "ounces":
                switch (targetUnit) {
                    case "grams":
                        ans = value * 28.3495D;
                        break;
                    case "kilograms":
                        ans = value * 0.0283495D;
                        break;
                    case "milligrams":
                        ans = value * 28349.5D;
                        break;
                    case "pounds":
                        ans = value * 0.0625D;
                        break;
                    case "tons":
                        ans = value * 3.125E-5D;
                        break;
                }
                break;
            case "pounds":
                switch (targetUnit) {
                    case "grams":
                        ans = value * 453.592D;
                        break;
                    case "kilograms":
                        ans = value * 0.453592D;
                        break;
                    case "ounces":
                        ans = value * 16.0D;
                        break;
                    case "milligrams":
                        ans = value * 453592.0D;
                        break;
                    case "tons":
                        ans = value * 0.0005D;
                        break;
                }
                break;
            case "tons":
                switch (targetUnit) {
                    case "grams":
                        ans = value * 907185D;
                        break;
                    case "kilograms":
                        ans = value * 907.185D;
                        break;
                    case "ounces":
                        ans = value * 32000D;
                        break;
                    case "pounds":
                        ans = value * 2000D;
                        break;
                    case "milligrams":
                        ans = value * 9.072E8D;
                        break;
                }
                break;
        }
    }
    /*Convert temperatures*/

    private void convertTemperature() {
        switch (inputUnit) {
            case "°F":
                switch (targetUnit) {
                    case "°C":
                        ans = (value - 32.0D) / 1.8D;
                        break;
                    case "°K":
                        ans = (value - 32.0D) * 5.0D / 9.0D + 273.15D;
                        break;
                }
                break;
            case "°C":
                switch (targetUnit) {
                    case "°F":
                        ans = value * 1.8D + 32.0D;
                        break;
                    case "°K":
                        ans = value + 274.15D;
                        break;
                }
                break;
            case "°K":
                switch (targetUnit) {
                    case "°F":
                        ans = (value - 273.15D) * 1.8D + 32.0D;
                        break;
                    case "°C":
                        ans = value - 273.15D;
                        break;
                }
                break;

        }
    }
    /*Convert time*/

    private void convertTime() {
        switch (inputUnit) {
            case "milliseconds":
                switch (targetUnit) {
                    case "seconds":
                        ans = value * 0.001D;
                        break;
                    case "minutes":
                        ans = value * 1.6667E-5D;
                        break;
                    case "hours":
                        ans = value * 2.7778E-7D;
                        break;
                    case "days":
                        ans = value * 1.1574E-8D;
                        break;
                    case "months":
                        ans = value * 3.8027E-10D;
                        break;
                    case "years":
                        ans = value * 3.1689E-11D;
                        break;
                }
                break;
            case "seconds":
                switch (targetUnit) {
                    case "milliseconds":
                        ans = value * 1000D;
                        break;
                    case "minutes":
                        ans = value * 0.0166667D;
                        break;
                    case "hours":
                        ans = value * 2.7778E-4D;
                        break;
                    case "days":
                        ans = value * 1.1574E-5D;
                        break;
                    case "months":
                        ans = value * 3.8027E-7D;
                        break;
                    case "years":
                        ans = value * 3.1689E-8D;
                        break;
                }
                break;
            case "minutes":
                switch (targetUnit) {
                    case "seconds":
                        ans = value * 60.0D;
                        break;
                    case "milliseconds":
                        ans = value * 60000;
                        break;
                    case "hours":
                        ans = value * 0.0166667D;
                        break;
                    case "days":
                        ans = value * 0.000694444D;
                        break;
                    case "months":
                        ans = value * 2.2816E-5D;
                        break;
                    case "years":
                        ans = value * 1.9013E-6D;
                        break;
                }
                break;
            case "hours":
                switch (targetUnit) {
                    case "seconds":
                        ans = value * 3600.0D;
                        break;
                    case "minutes":
                        ans = value * 60.0D;
                        break;
                    case "milliseconds":
                        ans = value * 3.6E6D;
                        break;
                    case "days":
                        ans = value * 0.0416667D;
                        break;
                    case "months":
                        ans = value * 0.00136895D;
                        break;
                    case "years":
                        ans = value * 0.00011408D;
                        break;
                }
                break;
            case "days":
                switch (targetUnit) {
                    case "seconds":
                        ans = value * 86400D;
                        break;
                    case "minutes":
                        ans = value * 1440D;
                        break;
                    case "hours":
                        ans = value * 24D;
                        break;
                    case "milliseconds":
                        ans = value * 8.64E+7D;
                        break;
                    case "months":
                        ans = value * 0.0328549D;
                        break;
                    case "years":
                        ans = value * 0.00273791D;
                        break;
                }
                break;
            case "months":
                switch (targetUnit) {
                    case "seconds":
                        ans = value * 2.63E6D;
                        break;
                    case "minutes":
                        ans = value * 43829.1D;
                        break;
                    case "hours":
                        ans = value * 730.484D;
                        break;
                    case "days":
                        ans = value * 30.4368D;
                        break;
                    case "milliseconds":
                        ans = value * 2.63E9D;
                        break;
                    case "years":
                        ans = value * 0.0833333;
                        break;
                }
                break;
            case "years":
                switch (targetUnit) {
                    case "seconds":
                        ans = value * 3.156E7D;
                        break;
                    case "minutes":
                        ans = value * 525949;
                        break;
                    case "hours":
                        ans = value * 8765.81D;
                        break;
                    case "days":
                        ans = value * 365.242D;
                        break;
                    case "months":
                        ans = value * 12;
                        break;
                    case "milliseconds":
                        ans = value * 3.156E10D;
                        break;
                }
                break;
        }

    }
}
