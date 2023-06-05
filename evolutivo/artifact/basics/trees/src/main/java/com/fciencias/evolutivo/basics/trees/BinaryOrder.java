package com.fciencias.evolutivo.basics.trees;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class BinaryOrder {

    private List<Node> initialList;
    private List<Node> orderedList;

    public BinaryOrder(List<Node> initialList)
    {
        this.initialList = initialList;
    }

    public BinaryOrder(Node[] initialList)
    {
        this.initialList = Arrays.asList(initialList);
    }

    public List<Node> getInitialList() {
        return initialList;
    }
    public void setInitialList(List<Node> initialList) {
        this.initialList = initialList;
    }
    public List<Node> getOrderedList() {
        return orderedList;
    }
    public void setOrderedList(List<Node> orderedList) {
        this.orderedList = orderedList;
    }

    public void addORderNode(Node node, Node parentNode)
    {
        if(parentNode.getChildrens().size() == 0)
        {
            parentNode.addChild(null);
            parentNode.addChild(null);
        }
        if(parentNode.compareNode(node))
        {
            if(parentNode.getChildren(1) != null)
                addORderNode(node,parentNode.getChildren(1));
            else
            {
                parentNode.getChildrens().set(1, node);
                node.setParent(parentNode);
            }
        }
        else
        {
            if(parentNode.getChildren(0) != null)
                addORderNode(node,parentNode.getChildren(0));
            else
            {
                parentNode.getChildrens().set(0, node);
                node.setParent(parentNode);
            }
        }
    }

    
    public void orderPath(Node node)
    {
        if(!node.getChildrens().isEmpty() && node.getChildren(0) != null)
            orderPath(node.getChildren(0));

        orderedList.add(node);

        if(node.getChildrens().size() > 1 && node.getChildren(1) != null)
            orderPath(node.getChildren(1));
    }

    public void orderList()
    {

        for(int i = 1; i < initialList.size(); i++)
            addORderNode(initialList.get(i), initialList.get(0));

        orderedList = new LinkedList<>();
        orderPath(initialList.get(0));

    }
    
}
