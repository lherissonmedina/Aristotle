/*
 * This class is used to calculate input such as equations, variables, prime factors, etc.
 */
package project.calculate;

import static com.sun.prism.impl.Disposer.cleanUp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import project.tools.Aristotle;
import project.tools.Suggestion;
import project.tools.Tools;

public class Calculation {

    private static final ArrayList<String> operands = new ArrayList<>(Arrays.asList(new String[]{"^", "*", "/", "+", "-"}));
    public static final Map<String, Double> variables = new HashMap<>();
    private static String eq;
    private static String answer;
    private static boolean success = true;
    private static Double ans;
    private final static String[] BADWORDS = {"calculate", "what", "does", "equal", "equation"};
    private static final DecimalFormat sciformat = new DecimalFormat("0.00E0");
    private static final Pattern numPattern = Pattern.compile("(\\d+\\.*\\d*)");

    public Calculation(List query) {

        query.removeAll(Arrays.asList(BADWORDS));
        String sentence = String.join("", query);

        answer = "";
        success = true;
        ans = null;
        double num = 0;
        if (sentence.contains("=")) {
            setVariable(sentence);
        } else if ((query.contains("delete") || query.contains("remove")) && !Collections.disjoint(query, Calculation.variables.keySet())) {
            removeVariable(query);
        } else {
            for (String variable : variables.keySet()) {
                if (sentence.contains(variable)) {
                    sentence = sentence.replaceAll(variable, variables.get(variable).toString());
                }
            }
            try {
                Matcher matcher;
                matcher = numPattern.matcher(sentence);

                if (matcher.find()) {
                    num = Double.parseDouble(matcher.group());
                }
                if (sentence.contains("factors")) {
                    primeFactors((int) num);

                    if (num == 1) {
                        answer = "" + 1;
                    }
                    answer = "Prime Factors of " + matcher.group(0) + ":\n" + answer;

                } else if (sentence.contains("root")) {
                    if (sentence.contains("square")) {
                        root(num, 2);
                        answer = "Square root of " + matcher.group() + ":\n";
                    } else if (sentence.contains("cube")) {
                        root(num, 3);
                        answer = "Cube root of " + matcher.group() + ":\n";
                    }
                } else {
                    infix(sentence);
                }

                if (ans != null) {
                    ans = Double.parseDouble(sciformat.format(ans));
                    Suggestion.setAnswerMemory(ans.toString());
                    answer = answer + ans;
                }

                Aristotle.setMemory(new String[]{"calculate"});

            } catch (Exception e) {
                answer = "I do not understand \"" + sentence + "\"";
                success = false;
            }
        }
        Aristotle.setAnswer(answer, success);

    }
    /*Saves a variable to Hashmap "variables" and sends information to notification bar*/

    public static void setVariable(String s) {
        success = true;
        String expression = s;
        Matcher m = Pattern.compile("(.*?)\\s*=\\s*(.*)").matcher(expression);

        if (m.find()) {
            String a = m.group(1).trim();
            String b = m.group(2).trim();
            if (a.matches("\\d+\\.*\\d*")) {
                Double num = Double.parseDouble(a);
                String name = b;
                variables.put(name, num);
                Tools.addCount();
                Tools.addNotif(name, name + " :\n" + String.valueOf(num));
                answer = "I'll remember " + num + " as " + name;
            } else if (b.matches("\\d+\\.*\\d*")) {

                Double num = Double.parseDouble(b);
                String name = a;
                variables.put(name, num);
                Tools.addCount();
                Tools.addNotif(name, name + " :\n" + String.valueOf(num));
                answer = "I'll remember " + num + " as " + name;
            } else if (expression.startsWith("=") && !Suggestion.getAnswerMemory().equals("")) {
                Double num = Double.parseDouble(Suggestion.getAnswerMemory());
                String name = b;
                variables.put(name, num);
                Tools.addCount();
                Tools.addNotif(name, name + " :\n" + String.valueOf(num));
                answer = "I'll remember " + num + " as " + name;
            } else {
                answer = "I can't find a number to remember";
                success = false;
            }
        } else {
            answer = "What number would you like me to remember?";
            success = false;
        }
        if (Tools.getCount() == 1) {
            Tools.updateNotification();
        }
    }
    /*Removes a variable from Hashmap "variables" and sends information to notification bar*/

    public static void removeVariable(List<String> query) {
        for (String var : query) {
            if (variables.containsKey(var)) {
                variables.remove(var);
                Tools.removeCount();
                Tools.removeNotif(var);
                answer = "I'll stop remembering " + var;
                return;
            }
        }
        answer = "I hadn't memorized " + String.join(" ", query) + " for you.";
        success = false;
    }
    /*Calculates prime factors using recursion*/

    private static void primeFactors(int num) {
        int i = 2;
        if (num != 1) {
            while (num % i != 0) {
                i++;
            }
            answer += i + " ";
            primeFactors(num / i);
        }
    }
    /*Calculates roots based on input*/

    private static void root(double num, int r) {
        ans = Math.pow(num, (1.0 / r));
    }
    /*Converts infix to post postfix*/

    private static void infix(String str) {

        str = str.replace("x", "*");
        str = str.replace(" ", "");
        eq = str;
        str = str.replaceAll("pi", "3.14");
        str = str.replaceAll("\\)\\(", ")*(");
        str = str.replaceAll("sqrt\\(", "S");

        ArrayList<String> equation = mergeNum(new ArrayList<>(Arrays.asList(str.split(""))));
        ArrayList<String> postfix = new ArrayList<>();
        Stack<String> stack = new Stack();
        int para = 0;

        if (equation.size() == 1) {
            ans = Double.parseDouble(equation.get(0));
            answer = eq + " = ";
        } else {
            for (String character : equation) {
                if (character.matches("[\\-\\+]*(\\d+|\\d+\\.\\d+|\\d+\\.\\d+E\\d+)")) {
                    postfix.add(character);
                } else if (para > 0) {
                    if (character.equals("(")) {
                        stack.push(character);
                        para++;
                    } else if (!character.equals(")")) {
                        while (!stack.empty() && (operands.indexOf(stack.peek()) < operands.indexOf(character) && !stack.peek().equals("("))) {
                            postfix.add(stack.pop());
                        }
                        stack.push(character);
                    } else {
                        while (!stack.empty() && !stack.peek().equals("(")) {
                            postfix.add(stack.pop());
                        }

                        if (!stack.empty()) {
                            stack.pop();
                        }

                        para--;
                    }

                } else if (operands.contains(character)) {
                    if (stack.empty()) {
                        stack.push(character);
                    } else {
                        while (!stack.empty() && operands.indexOf(stack.peek()) < operands.indexOf(character)) {
                            postfix.add(stack.pop());
                        }
                        stack.push(character);
                    }
                } else if (character.equals("(")) {
                    stack.push(character);
                    para++;
                }
            }

            while (!stack.empty()) {
                postfix.add(stack.pop());
            }
            solve(postfix);
        }
    }
    /*Solve the postfix using stacks and reverse polish notation*/

    private static void solve(ArrayList<String> str) {

        try {
            Stack<Double> stack = new Stack();
            double temp1, temp2;
            for (String ints : str) {
                if (ints.matches("\\d+|\\d+\\.\\d+|\\d+\\.\\d+E\\d+|[-+]\\d|[-+]\\d+\\.\\d+|[-+]\\d+\\.\\d+E\\d+")) {
                    stack.push(Double.parseDouble(ints));
                } else {
                    temp1 = stack.pop();
                    temp2 = stack.pop();
                    switch (ints) {
                        case "+":
                            stack.push(temp1 + temp2);
                            break;
                        case "-":
                            stack.push(temp2 - temp1);
                            break;
                        case "*":
                            stack.push(temp1 * temp2);
                            break;
                        case "/":
                            stack.push(temp2 / temp1);
                            break;
                        case "^":
                            stack.push(Math.pow(temp2, temp1));
                            break;
                    }
                }

            }
            ans = stack.pop();
            answer = eq + " = ";
        } catch (EmptyStackException e) {
            answer = "Could not calculate " + eq;
            success = false;
        }
    }
    /*Gets equation and formats it so numbers/decimals are merged, along with negative number, E, and factorials*/

    private static ArrayList mergeNum(ArrayList<String> equation) {
        for (int n = 1; n < equation.size(); n++) {
            if (equation.get(n).matches("\\d+")) {
                if (equation.get(n - 1).matches("\\d+") || (equation.get(n - 1).matches("[\\+\\-]") && n == 1)) {
                    equation.set(n - 1, equation.get(n - 1) + equation.get(n));
                    equation.remove(n);
                    n--;
                }
            } else if (equation.get(n).matches("[\\+\\-]") && (operands.contains(equation.get(n - 1)) || equation.get(n - 1).equals("("))) {

                if (equation.get(n).equals("-")) {
                    equation.remove(n);
                    equation.set(n, "-" + equation.get(n));
                }
            } else if (n != equation.size() - 1) {
                if (equation.get(n).equals("(") && (equation.get(n - 1).matches("\\d+"))) {
                    equation.add(n, "*");
                } else if (equation.get(n).equals(")") && (equation.get(n + 1).matches("\\d+") || equation.get(n - 1).equals("("))) {
                    equation.add(n + 1, "*");
                }
            }

        }

        for (int i = 1; i < equation.size(); i++) {
            if (equation.get(i).equals(".") && isNumeric(equation.get(i - 1)) && isNumeric(equation.get(i + 1))) {
                equation.remove(i);
                equation.set(i - 1, equation.get(i - 1) + "." + equation.get(i));
                equation.remove(i);
            }
        }

        for (int n = 1; n < equation.size(); n++) {
            if (equation.get(n).matches("E|e")) {
                if (equation.get(n - 1).matches("\\d+\\.*\\d*") && equation.get(n + 1).matches("\\d+\\.*\\.*")) {
                    equation.set(n - 1, String.valueOf(Double.parseDouble(equation.get(n - 1)) * Math.pow(10, Double.parseDouble(equation.get(n + 1)))));
                    equation.remove(n);
                    equation.remove(n);
                    n--;
                }
            }
        }

        for (int i = 1; i < equation.size(); i++) {
            if (equation.get(i).equals("!") && isNumeric(equation.get(i - 1))) {
                equation.remove(i);
                equation.set(i - 1, factorial(equation.get(i - 1)));

            }
        }

        return equation;
    }
    /*Calculates factorial through recursion*/

    private static String factorial(String n) {
        double num = Math.round(Double.parseDouble(n));
        double fact = 1;
        for (double i = 1; i <= num; i++) {
            fact *= i;
        }
        return Double.toString(fact);

    }
    /*Checks if string is a number*/

    private static boolean isNumeric(String st) {
        try {
            double num = Double.parseDouble(st);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
    /*Updates variable if user wants to change it's value*/

    public static synchronized void updateVariables() {
        new Thread(() -> {
            try {
                String notification = "";
                while (variables.size() != 0) {
                    notification = "";
                    for (String var : variables.keySet()) {
                        notification += "\n" + var + " = " + variables.get(var);
                    }
                    Aristotle.setNotification(notification, true);
                    Thread.sleep(1000);
                }
                Aristotle.setNotification(null, false);
            } catch (Exception e) {
            } finally {
                cleanUp();
            }
        }).start();
    }

}
