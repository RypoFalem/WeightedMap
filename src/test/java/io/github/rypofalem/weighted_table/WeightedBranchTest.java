package io.github.rypofalem.weighted_table;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Unit test for simple WeightedBranch.
 */
public class WeightedBranchTest
{
    // a simple test with 2 leaves attached to a main branch
    @Test
    public void simpleLeafTest(){
        WeightedLeaf<String> helmet = new WeightedLeaf<>(30, "HELMET");
        WeightedLeaf<String> sword = new WeightedLeaf<>(70, "SWORD");
        WeightedBranch<String> table = new WeightedBranch<>(1);

        table.addChild("helm", helmet);
        table.addChild("sword", sword);

        Map<String, Double> odds = table.getOdds();
        assertTrue(odds != null);
        assertTrue(aboutEquals(odds.get("HELMET"), 0.3));
        assertTrue(aboutEquals(odds.get("SWORD"), 0.7));
    }

    // a simple test with 1 leaf attached to main branch and 2 leaves attached to secondary branch
    @Test
    public void simpleBranchTest(){
        WeightedLeaf<String> helmet = new WeightedLeaf<>(30, "HELMET");
        WeightedBranch<String> swords = new WeightedBranch<>(70);
        WeightedLeaf<String> diamondSword = new WeightedLeaf<>(50, "DSWORD");
        WeightedLeaf<String> goldSword = new WeightedLeaf<>(50, "GSWORD");
        WeightedBranch<String> table = new WeightedBranch<>(1);

        swords.addChild("dSword", diamondSword);
        swords.addChild("gSword", goldSword);
        table.addChild("helm", helmet);
        table.addChild("swords", swords);

        Map<String, Double> odds = table.getOdds();
        assertTrue(odds != null);
        assertTrue(aboutEquals(odds.get("HELMET"), 0.3));
        assertTrue(aboutEquals(odds.get("DSWORD"), 0.35));
        assertTrue(aboutEquals(odds.get("GSWORD"), 0.35));
    }

    // Several odds of branches 1, 2 and 3 branches deep
    @Test
    public void deepBranchTest(){
        WeightedBranch<String> table = new WeightedBranch<>(1); //main branch

        // 2 child branches of main branch + 1 leaf
        WeightedBranch<String> weapons = new WeightedBranch<>(50);
        WeightedBranch<String> armor = new WeightedBranch<>(40);
        WeightedLeaf<String> diamond = new WeightedLeaf<>(10, "DIAMOND");
        table.addChild("weapons", weapons);
        table.addChild("armor", armor);
        table.addChild("diamond", diamond);

        // 2 child branches of weapons branch
        WeightedBranch<String> swords = new WeightedBranch<>(50);
        swords.addChild("dSword", new WeightedLeaf<>(100/3, "DSWORD"));
        swords.addChild("gSword", new WeightedLeaf<>(100/3, "GSWORD"));
        swords.addChild("iSword", new WeightedLeaf<>(100/3, "ISWORD"));
        WeightedBranch<String> axes = new WeightedBranch<>(50);
        axes.addChild("dAxe", new WeightedLeaf<>(80, "DAXE"));
        axes.addChild("gAxe", new WeightedLeaf<>(20, "GAXE"));
        weapons.addChild("swords", swords);
        weapons.addChild("axes", axes);

        // 4 leaves of armor branch
        armor.addChild("helmet", new WeightedLeaf<>(10, "HELM"));
        armor.addChild("chest", new WeightedLeaf<>(20, "CHEST"));
        armor.addChild("pants", new WeightedLeaf<>(30, "PANTS"));
        armor.addChild("boots", new WeightedLeaf<>(40, "BOOTS"));

        Map<String, Double> odds = table.getOdds();
        assertTrue(aboutEquals(odds.get("DIAMOND"), .1));

        assertTrue(aboutEquals(odds.get("DSWORD"), .5 * .5 * 1/3));
        assertTrue(aboutEquals(odds.get("GSWORD"), .5 * .5 * 1/3));
        assertTrue(aboutEquals(odds.get("ISWORD"), .5 * .5 * 1/3));

        assertTrue(aboutEquals(odds.get("DAXE"), .5 * .5 * .8));
        assertTrue(aboutEquals(odds.get("GAXE"), .5 * .5 * .2));

        assertTrue(aboutEquals(odds.get("HELM"), .4 * .1));
        assertTrue(aboutEquals(odds.get("CHEST"), .4 * .2));
        assertTrue(aboutEquals(odds.get("PANTS"), .4 * .3));
        assertTrue(aboutEquals(odds.get("BOOTS"), .4 * .4));
    }

    // test if duplicate element probabilities are correctly added
    @Test
    public void duplicateElementTest(){
        WeightedLeaf<String> helmet = new WeightedLeaf<>(10, "HELMET");
        WeightedLeaf<String> helmet2 = new WeightedLeaf<>(50, "HELMET"); //duplicate elements on different branches
        WeightedBranch<String> gear = new WeightedBranch<>(90);
        WeightedLeaf<String> diamondSword = new WeightedLeaf<>(50, "DSWORD");
        WeightedBranch<String> table = new WeightedBranch<>(1);

        gear.addChild("dSword", diamondSword);
        gear.addChild("helm2", helmet2);
        table.addChild("helm", helmet);
        table.addChild("gear", gear);

        Map<String, Double> odds = table.getOdds();
        assertTrue(odds != null);
        assertTrue(odds.get("HELMET")+"", aboutEquals(odds.get("HELMET"), 0.55)); // .1(helmet) + .9*.5(helmet2)
        assertTrue(aboutEquals(odds.get("DSWORD"), 0.45)); // .5*.9
    }

    //Make a weighted table to manage stuff and make sure the table's random picks align with the correct odds
    @Test
    public void weightedTableTest(){
        WeightedTable<String> table = new WeightedTable<>("table");
        table.addBranch("gear", 90);
        table.addElement("helm", "HELMET", 10);
        table.addElement("gear.dSword", "DSWORD", 50);
        table.addElement("gear.helm2", "HELMET", 50);

        // test if win% of each item is correct to .1% of expected value
        Map<String, Double> wins = testedOddsMap(table);
        double helmetOdds = wins.get("HELMET");
        double swordOdds = wins.get("DSWORD");
        assertTrue(aboutEquals(helmetOdds, .55, .55 * .001));
        assertTrue(aboutEquals(swordOdds, .45, .45 * .001));
        System.out.println(table.serialize());
    }

    // return average weighted random pickrate after 1000000  tests
    static Map<String, Double> testedOddsMap(WeightedTable<String> table){
        table.setRandom(new Random(0));
        double total = 1000000;
        Map<String, Double> wins = new HashMap<>();
        for(int i = 0; i < total; i++){
            String winner = table.pickRandomElement();
            if(wins.containsKey(winner)){
                wins.put(winner, wins.get(winner) +1);
            }else{
                wins.put(winner, 0.0);
            }
        }
        for(String key : wins.keySet()){
            wins.put(key, wins.get(key)/total);
        }
        return wins;
    }

    static boolean aboutEquals(double a, double b, double epsilon){
        return Math.abs(a-b) < epsilon;
    }

    static boolean aboutEquals(double a, double b){
        return aboutEquals(a, b, .00001); // 1/1000th of a %
    }
}
