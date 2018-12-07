package io.github.rypofalem.weighted_map;

import java.io.Serializable;
import java.util.*;

public final class WeightedBranch<T> extends WeightedNode<T>{
    private Map<String, WeightedNode<T>> children;

    WeightedBranch(double weight){
        super(weight);
    }

    public Map<String, WeightedNode<T>> getChildren(){
        return children == null ? children = new HashMap<>() : children;
    }

    // returns the node indicated by the given string path
    // returns null if path is invalid or does not exist
    public WeightedNode<T> getNode(String path){
        if(path == null || path.isEmpty()) return null;
        else return getNode(path.split("\\."), 0);
    }

    WeightedNode<T> getNode(String[] keys, int index){
        WeightedNode<T> node = getChildren().get(keys[index]);
        index++;

        // This is the node described by the path, stop recursion and return
        if(index == keys.length) return node;
        // node is a branch and there are more keys in the path, continue recursion!
        else if(node.isBranch()) return ((WeightedBranch<T>)node).getNode(keys, index);
        // node is not a branch but there's a longer path, (path doesn't exist)
        else return null;
    }

    // Add a child node with a name unique to this branch
    // if either the name or node is null, fail silently
    boolean addChild(String name, WeightedNode<T> node){
        if(name == null || node == null) return false;
        getChildren().put(name, node);
        return true;
    }

    // Creates a Map of all unique elements and weighted odds. Odds add up to 1
    Map<T, Double> getOdds(){
        Map<T, Double> map = new HashMap<>();
        double totalWeight = getTotalChildWeight();
        for(WeightedNode<T> node : getChildren().values()){
            if(node.isLeaf()){
                addTo(map, ((WeightedLeaf<T>)node).get(), node.getWeight()/totalWeight);
            }else if(node.isBranch()){
                WeightedBranch<T> childMap = ((WeightedBranch<T>)node);
                Map<T, Double> childsOdds = childMap.getOdds();
                for(T t : childsOdds.keySet()){
                    addTo(map, t, childsOdds.get(t) * childMap.getWeight() / totalWeight);
                }
            }
        }
        return map;
    }

    private Map<T, Double> addTo(Map<T, Double> map, T t, double doub){
        if(map.containsKey(t)) map.put(t, map.get(t) + doub);
        else map.put(t, doub);
        return map;
    }

    private double getTotalChildWeight(){
        double w = 0;
        for(WeightedNode<T> node : getChildren().values()){
            w += node.getWeight();
        }
        return w;
    }
}
