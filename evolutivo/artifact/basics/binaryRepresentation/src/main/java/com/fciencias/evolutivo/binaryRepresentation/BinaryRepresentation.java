package com.fciencias.evolutivo.binaryRepresentation;

import com.fciencias.evolutivo.basics.RandomDistribution;

public interface BinaryRepresentation {
    
    public double[] getRealValue();

    public double getEvaluation();

    public void setEvaluation(double evaluation);

    public int getRepresentationalBits();

    public String getBinaryString();

    public boolean[][] getBinaryArray(); 
    
    public BinaryRepresentation[] getNeighborhoods(double radius, int n);

    public double realDifference(BinaryRepresentation binaryRepresentation);

    public int discreteDifference(BinaryRepresentation binaryRepresentation);
    
    public BinaryRepresentation getRandomState(double radius, double[] mu);

    public BinaryRepresentation getRandomState(double radius);

    public RandomDistribution getRandomDistribution();

    public String printRealValue();
}
