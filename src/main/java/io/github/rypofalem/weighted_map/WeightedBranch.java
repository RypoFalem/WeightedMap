package io.github.rypofalem.weighted_map;

import java.util.*;

public class WeightedBranch<T> extends WeightedNode<T> {
    private Map<String, WeightedNode<T>> children;

    protected WeightedBranch(double weight){
        super(weight);
    }

    public Map<String, WeightedNode<T>> getChildren(){
        return children == null ? children = new HashMap<>() : children;
    }

    public void addChild(String name, WeightedNode<T> node){
        if(name == null || node == null) return;
        getChildren().put(name, node);
    }

    private double getTotalChildWeight(){
        double w = 0;
        for(WeightedNode<T> node : getChildren().values()){
            w += node.getWeight();
        }
        return w;
    }

    // Creates a Map of all unique elements and weighted odds. Odds add up to 1
    public Map<T, Double> getOdds(){
        Map<T, Double> map = new HashMap<>();
        double totalWeight = getTotalChildWeight();
        for(WeightedNode<T> node : getChildren().values()){
            if(node.isLeaf()){
                addTo(map, ((WeightedLeaf<T>)node).get(), node.getWeight()/totalWeight);
            }else if(node.isMap()){
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
}
