package io.github.rypofalem.weighted_table;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class WeightedTable<T> {
    private Random random;
    private final String name;
    private final WeightedBranch<T> trunk;
    private boolean oddsNeedUpdate = true;
    private Map<T, Double> oddsMap;

    public WeightedTable(String name){
        trunk = new WeightedBranch<>(1);
        this.name = name;
    }

    public Random getRandom() {
        return random == null ? random = new Random() : random;
    }

    public void setRandom(Random random){
        if(random != null) this.random = random;
    }

    public boolean addElement(String keypath, T element, double weight){
        if(keypath.contains(".")){
            String path = keypath.substring(0, keypath.lastIndexOf("."));
            String key = (keypath.lastIndexOf(".") == keypath.length()-1)
                    ? ""
                    : keypath.substring(keypath.lastIndexOf(".")+1);
            return addElement(path, key, element, weight);
        } else return addElement("", keypath, element, weight);
    }

    public boolean addElement(String path, String key, T element, double weight){
        WeightedNode<T> node = getNode(path);
        if(node.isBranch()){
            return ((WeightedBranch<T>)node).addChild(key, new WeightedLeaf<T>(weight, element));
        }
        return false;
    }

    public boolean addBranch(String keypath, double weight){
        if(keypath.contains(".")){
            String path = keypath.substring(0, keypath.lastIndexOf("."));
            String key = (keypath.lastIndexOf(".") == keypath.length()-1)
                    ? ""
                    : keypath.substring(keypath.lastIndexOf(".")+1);
            return addBranch(path, key, weight);
        } else return addBranch("", keypath,  weight);
    }

    public boolean addBranch(String path, String key, double weight) {
        WeightedNode<T> node = getNode(path);
        if(node.isBranch()){
            return ((WeightedBranch<T>)node).addChild(key, new WeightedBranch<>(weight));
        }
        return false;
    }

    private WeightedNode<T> getNode(String path){
        if(path == null) throw new NullPointerException();
        if(path.isEmpty()) return trunk;
        return trunk.getNode(path);
    }


    // Returns a weighted random element from the nested tables
    // If no leaf element exists, return null
    public T pickRandomElement(){
        double chance = 0;
        double rand = getRandom().nextDouble();
        T t = null;
        updateOdds();
        Iterator<T> keyIterator = oddsMap.keySet().iterator();
        while(rand > chance && keyIterator.hasNext()){
            t = keyIterator.next();
            chance += oddsMap.get(t);
        }
        return t;
    }


    // If you modify the table without the manager somehow, set this.
    // if you simply use Manager methods, you'll never need to
    public void setNeedUpdate(){
        oddsNeedUpdate = true;
    }

    // update odds only if needed (it's a fairly intense operation)
    private void updateOdds(){
        updateOdds(false);
    }

    private void updateOdds(boolean force){
        if(force || oddsNeedUpdate){
            oddsMap = trunk.getOdds();
            oddsNeedUpdate = false;
        }
    }

    //TODO: clone keys
    // returns a cloned copy of it's internal oddsmap
    // The keys, values and map itself are either clones or immutable so no alterations will affect this object's version
    public Map<T, Double> getOdds(){
        updateOdds();
        HashMap<T, Double> clone = new HashMap<>();
        for(T key : oddsMap.keySet()){
            clone.put(key, clone.get(key));
        }
        return clone;
    }
}
