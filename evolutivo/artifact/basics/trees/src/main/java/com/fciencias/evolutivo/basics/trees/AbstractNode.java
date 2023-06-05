package com.fciencias.evolutivo.basics.trees;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class AbstractNode implements Node{

    protected Node parent;

    protected double parentWeight;

    protected List<Node> childrens;

    protected List<Double> childrensWeights;

    protected Map<String,Object> values;

    protected AbstractNode()
    {
        this.childrens = new LinkedList<>();
        this.childrensWeights = new LinkedList<>();
    }

    @Override
    public Node getParent() {
        
        return parent;
    }

    @Override
    public void setParent(Node node)
    {
        this.parent = node;
    }

    @Override
    public List<Node> getNeighborhoods() {
        
        List<Node> neighborhoods = new LinkedList<>(childrens);
        neighborhoods.add(0,parent);
        return neighborhoods;
    }

    @Override
    public Node getNeighborhood(int index) {
        
        return getNeighborhood(index);
    }

    @Override
    public List<Node> getChildrens() {
        
        return childrens;
    }

    @Override
    public Node getChildren(int index) {
        
        return childrens.get(index);
    }

    @Override
    public List<Double> getNeighborhoodsWeights() {
        
        List<Double> neightborhoodWeights = new LinkedList<>(childrensWeights);
        neightborhoodWeights.add(0,parentWeight);
        return neightborhoodWeights;
    }

    @Override
    public List<Double> getChildrensWeights() {
        
        return childrensWeights;
    }

    @Override
    public Double getNeighborhoodsWeight(int index) {

        return getNeighborhoodsWeight(index);
    }

    @Override
    public Double getChildrenWeight(int index) {
        
        return childrensWeights.get(index);
    }
    

    @Override
    public void addChild(Node node, double weight)
    {
        if(node !=null)
            node.setParent(this);
        this.childrens.add(node);
        this.childrensWeights.add(weight);
    }

    @Override
    public void addChild(Node node)
    {
        addChild(node,1.0);
    }

    @Override
    public Map<String, Object> getValue() {
        
        return values;
    }
}
