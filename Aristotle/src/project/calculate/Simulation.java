/*This class allows you to do many simulations, from dice rolls, to coin flips, 
 to choosing. It checks how many dice/coins and how many times the user want them to be flipped/rolled*/
package project.calculate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import project.tools.*;

public class Simulation {

    public static final String[] specific = {"flip", "coin", "roll", "dice", "die", "times", "choose", "between", "or"};
    private static boolean success = true;
    private String answer;

    /*Tries to figure out what user wants*/
    public Simulation(List query) {
        success = true;
        if (query.contains("flip")) {
            flip(query);
            Aristotle.setMemory(new String[]{"flip"});
        } else if (query.contains("roll")) {
            roll(query);
            Aristotle.setMemory(new String[]{"roll"});
        } else if (query.contains("choose")) {
            choose(query);
            Aristotle.setMemory(new String[]{"choose"});
        } else {
            answer = "What would you like me to simulate?";
            success = false;
        }

        Aristotle.setAnswer(answer, success);
    }
    /*Flips coins*/

    private void flip(List query) {

        List<String> sentence = query;
        ArrayList<Boolean> results = new ArrayList<>();
        int numCoins = 1, numFlips = 1;
        int sum = 0;
        String most;
        Random r = new Random();

        if (sentence.contains("times")) {
            try {
                numFlips = Integer.parseInt(sentence.get(sentence.indexOf("times") - 1));
            } catch (Exception e) {
                numFlips = 2;
            }
        }

        if (sentence.contains("coins")) {
            try {
                numCoins = Integer.parseInt(sentence.get(sentence.indexOf("coins") - 1));
            } catch (Exception e) {
                numCoins = 2;
            }
        }

        for (int i = 1; i <= numCoins * numFlips; i++) {
            results.add(r.nextBoolean());
        }

        int heads = Collections.frequency(results, true);
        int tails = Collections.frequency(results, false);

        if (heads > tails) {
            most = "heads";
        } else {
            most = "tails";
        }

        if (numFlips > 1) {
            if (numCoins == 1) {
                answer = "Flipped coin " + numFlips + " times. The results were " + heads + " heads and " + tails + " tails.";
            } else {
                answer = "Flipped " + numCoins + " coins " + numFlips + " times. The results were " + heads + " heads and " + tails + " tails.";
            }
        } else {
            if (numCoins == 1) {
                answer = "Coin flipped " + most + ".";
            } else {
                answer = "Flipped " + numCoins + " coins and the results were " + heads + " heads and " + tails + " tails.";
            }
        }
    }
    /*Rolls dice*/

    private void roll(List query) {
        List<String> sentence = query;
        ArrayList<Integer> results = new ArrayList<>();
        int numDice = 1, numRolls = 1;
        int sum = 0;
        Random r = new Random();

        if (sentence.contains("times")) {
            try {
                numRolls = Integer.parseInt(sentence.get(sentence.indexOf("times") - 1));
            } catch (Exception e) {
                numRolls = 2;
            }
        }

        if (sentence.contains("dice")) {
            try {
                numDice = Integer.parseInt(sentence.get(sentence.indexOf("dice") - 1));
            } catch (Exception e) {
                numDice = 2;
            }
        }

        for (int i = 1; i <= numDice * numRolls; i++) {
            results.add(r.nextInt(6) + 1);
            sum += results.get(i - 1);
        }

        if (numRolls > 1) {
            if (numDice == 1) {
                if (numRolls == 2) {
                    answer = "Rolled " + results.get(0) + " and " + results.get(1) + ".";
                } else {
                    answer = "Die rolled " + numRolls + " times and added up to " + sum;
                }
            } else {
                answer = "Rolled " + numDice + " dice " + numRolls + " times and added up to " + sum;
            }
        } else {
            if (numDice == 1) {
                answer = "Die rolled a " + results.get(0);
            } else if (numDice == 2) {
                answer = "Rolled " + results.get(0) + " and " + results.get(1) + ".";
            } else {
                answer = "Rolled " + numDice + " dice and added up to " + sum + ".";
            }
        }
    }
    /*Chooses from a list*/

    private void choose(List query) {
        List<String> sentence = query;
        String[] badwords = {"choose", "between", "and", "or"};
        sentence.removeAll(Arrays.asList(badwords));
        sentence = Arrays.asList(String.join(" ", sentence).split("\\s*,\\s\\s*"));
        Random r = new Random();
        int numChoices = sentence.size();
        int choice = r.nextInt(numChoices);
        answer = "I choose " + sentence.get(choice) + ".";

    }
}
