package com.fciencias.evolutivo.evalFunctions;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.fciencias.evolutivo.binaryRepresentation.BinaryMappingState;
import com.fciencias.evolutivo.binaryRepresentation.BinaryRepresentation;
import com.fciencias.evolutivo.binaryRepresentation.BinaryRepresentationNode;

public class BinaryRepresentationSorter {
    
    private EvalFunction evalFunction;
    private BinaryRepresentationNode rootNode = null;
    private boolean optimizeDirection = true;

    public BinaryRepresentationSorter(EvalFunction evalFunction)
    {
        this.evalFunction = evalFunction;
    }


    public BinaryRepresentationSorter(EvalFunction evalFunction, boolean optimizeDirection)
    {
        this.evalFunction = evalFunction;
        this.optimizeDirection = optimizeDirection;
    }

    public BinaryRepresentationSorter(EvalFunction evalFunction, BinaryRepresentationNode rootNode)
    {
        this.evalFunction = evalFunction;
        this.rootNode = rootNode;
    }



    public List<BinaryRepresentation> orderedPath()
    {
        List<BinaryRepresentation> recorrido = new LinkedList<>();
        BinaryRepresentationNode nodoActual = rootNode;

        while(nodoActual != null)
        {
            if(nodoActual.getLeftNode() != null && !nodoActual.getLeftNode().isVisited())
            {
                nodoActual = nodoActual.getLeftNode();
            }
            else if(!nodoActual.isVisited())
            {
                nodoActual.setVisited(true);
                recorrido.add(nodoActual.getBinaryRepresentation());
            }
            else if(nodoActual.getRightNode() != null && !nodoActual.getRightNode().isVisited())
            {
                nodoActual = nodoActual.getRightNode();
            }
            else
            {
                nodoActual = nodoActual.getParentNode();
            }
        }
        return recorrido;
    }


    public void createTree(List<BinaryRepresentation> unsortedList)
    {
        rootNode = new BinaryRepresentationNode(unsortedList.get(0));

        for(BinaryRepresentation valor : unsortedList.subList(1, unsortedList.size()))
        {
            BinaryRepresentationNode newNode = new BinaryRepresentationNode(valor);

            addOrderedChild(rootNode,newNode);
        }
    }

    private boolean orderChiteria(BinaryRepresentationNode parentNode,BinaryRepresentationNode childNode)
    {
        if(optimizeDirection)
            return evalFunction.evalSoution(parentNode.getBinaryRepresentation()) > evalFunction.evalSoution(childNode.getBinaryRepresentation());
        
        return evalFunction.evalSoution(parentNode.getBinaryRepresentation()) < evalFunction.evalSoution(childNode.getBinaryRepresentation());
    }

    private void addOrderedChild(BinaryRepresentationNode parentNode,BinaryRepresentationNode childNode)
    {
        if(orderChiteria(parentNode, childNode))
        {
            if(parentNode.getLeftNode() == null)
                parentNode.setLeftNode(childNode);
            else
                addOrderedChild(parentNode.getLeftNode(),childNode);
        }
        else
        {
            if(parentNode.getRightNode() == null)
                parentNode.setRightNode(childNode);
            else
                addOrderedChild(parentNode.getRightNode(),childNode);
        }
    }



    public List<BinaryRepresentation> sortList(List<BinaryRepresentation> unsortedList)
    {
        createTree(unsortedList);
        return orderedPath();
    }

    public static void main(String[] args) {
        
        EvalFunction evalFunction = new SphereFunction();

        List<BinaryRepresentation> statesList = new ArrayList<>();

        BinaryRepresentation newState = new BinaryMappingState(3, new double[]{0,8}, 3);
        for(int i =0; i < 10; i++)
        {
            statesList.add(newState.getRandomState(4));
        }

        BinaryRepresentationSorter sorter = new BinaryRepresentationSorter(evalFunction,false);
        List<BinaryRepresentation> sortedStatesList = sorter.sortList(statesList);


        for(BinaryRepresentation state : statesList)
            System.out.println(evalFunction.evalSoution(state));

        System.out.println("\n\nLista ordenada: \n");
        for(BinaryRepresentation state : sortedStatesList)
            System.out.println(evalFunction.evalSoution(state));
    }
}
