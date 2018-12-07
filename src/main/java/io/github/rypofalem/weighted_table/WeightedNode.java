package io.github.rypofalem.weighted_table;

/*
 * TODO:
 * Serialize/Deserialize
 */
public abstract class WeightedNode <T>{
    protected double weight;

    WeightedNode(double weight){
        if(weight <= 0) throw new IllegalArgumentException("Weight must be above 0");
        this.weight = weight;
    }

    public double getWeight(){
        return weight;
    }

    public final boolean isLeaf(){
        return this instanceof WeightedLeaf;
    }

    public final boolean isBranch(){
        return this instanceof WeightedBranch;
    }

}
