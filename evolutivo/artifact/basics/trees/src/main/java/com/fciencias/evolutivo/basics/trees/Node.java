package com.fciencias.evolutivo.basics.trees;

import java.util.List;
import java.util.Map;

public interface Node {
    
    public Node getParent();

    public void setParent(Node node);

    public List<Node> getNeighborhoods();

    public Node getNeighborhood(int index);

    public List<Node> getChildrens();

    public Node getChildren(int index);

    public List<Double> getNeighborhoodsWeights();
    
    public List<Double> getChildrensWeights();

    public Double getNeighborhoodsWeight(int index);

    public Double getChildrenWeight(int index);

    public Map<String,Object> getValue();

    public void addChild(Node node);

    public void addChild(Node node, double weight);

    public boolean compareNode(Node node);

}
