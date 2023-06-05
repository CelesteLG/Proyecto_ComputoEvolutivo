package com.fciencias.evolutivo.binaryRepresentation;

public class BinaryRepresentationNode {
    
    private BinaryRepresentationNode leftNode = null;
    private BinaryRepresentationNode rightNode = null;
    private BinaryRepresentationNode parentNode = null;

    private BinaryRepresentation binaryRepresentation;
    private boolean visited = false;

    
    public BinaryRepresentationNode(BinaryRepresentation binaryRepresentation) {
        this.binaryRepresentation = binaryRepresentation;
    }

    public BinaryRepresentationNode getLeftNode() {
        return leftNode;
    }
    public void setLeftNode(BinaryRepresentationNode leftNode) {
        this.leftNode = leftNode;
        leftNode.setParentNode(this);
    }
    public BinaryRepresentationNode getRightNode() {
        return rightNode;
    }
    public void setRightNode(BinaryRepresentationNode rightNode) {
        this.rightNode = rightNode;
        rightNode.setParentNode(this);
    }
    public BinaryRepresentationNode getParentNode() {
        return parentNode;
    }
    public void setParentNode(BinaryRepresentationNode parentNode) {
        this.parentNode = parentNode;
    }
    public BinaryRepresentation getBinaryRepresentation() {
        return binaryRepresentation;
    }
    public void setBinaryRepresentation(BinaryRepresentation binaryRepresentation) {
        this.binaryRepresentation = binaryRepresentation;
    }
    public boolean isVisited() {
        return visited;
    }
    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    
}
