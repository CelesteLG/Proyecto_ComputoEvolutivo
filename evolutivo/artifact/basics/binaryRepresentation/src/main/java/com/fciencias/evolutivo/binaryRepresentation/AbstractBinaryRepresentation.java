package com.fciencias.evolutivo.binaryRepresentation;

import java.security.InvalidParameterException;
import java.util.Arrays;

import com.fciencias.evolutivo.basics.NormalRandomDistribution;
import com.fciencias.evolutivo.basics.RandomDistribution;

public abstract class AbstractBinaryRepresentation implements BinaryRepresentation{

    protected double[] realValue;
    protected int representationalBits;
    protected String binaryString;
    protected boolean[][] binaryArray;
    RandomDistribution randomDistribution;


    protected AbstractBinaryRepresentation(BinaryRepresentation binaryRepresentation){
        
        this.realValue = Arrays.copyOf(binaryRepresentation.getRealValue(),binaryRepresentation.getRealValue().length);
        this.representationalBits = binaryRepresentation.getRepresentationalBits();
        this.binaryString = binaryRepresentation.getBinaryString();
        this.binaryArray = Arrays.copyOf(binaryRepresentation.getBinaryArray(), binaryRepresentation.getBinaryArray().length);
        this.randomDistribution = binaryRepresentation.getRandomDistribution();
    }

    protected AbstractBinaryRepresentation(){
        representationalBits = 8;
        this.randomDistribution = new NormalRandomDistribution(new double[]{0,1});
    }

    protected AbstractBinaryRepresentation(RandomDistribution randomDistribution){
        representationalBits = 8;
        this.randomDistribution = randomDistribution;
    }
    
    protected AbstractBinaryRepresentation(String binaryString)
    {
        this.binaryString = binaryString;
        randomDistribution = new NormalRandomDistribution(new double[]{0,1});

    }

    public double[] getRealValue() {
        return realValue;
    }

    public double getEvaluation()
    {
        return 0;
    }

    public void setEvaluation(double evalFunction)
    {
        
    }

    public int getRepresentationalBits() {
        return representationalBits;
    }

    public String getBinaryString() {
        return binaryString;
    }

    public boolean[][] getBinaryArray() {
        return binaryArray;
    }

    public RandomDistribution getRandomDistribution() {
        return randomDistribution;
    }

    protected boolean[] stringToArray(String stringValue) throws InvalidParameterException
    {
        int i = stringValue.length() - 1;
        boolean[] booleanArray = new boolean[stringValue.length()];
        for(char bit : stringValue.toCharArray())
        {
            if(bit != '1' && bit != '0')
                throw new InvalidParameterException();

            booleanArray[i--] = (bit == '1');
            
        }
        return booleanArray;
    }
    
    protected int arrayToInteger(boolean[] booleanArray)
    {
        int totalSum = 0;
        int i = booleanArray.length - 1;

        for(boolean bit : booleanArray)
        
            totalSum += (bit ? Math.pow(2,i--) : 0);

        return totalSum;
        
    }

    protected String arrayToString(boolean[] booleanArray)
    {
        StringBuilder stringRep = new StringBuilder();
    
        for(int i = booleanArray.length - 1; i >= 0; i --)
        
            stringRep.append(booleanArray[i] ? "1" : "0");

        return stringRep.toString();
        
    }

    @Override
    public double realDifference(BinaryRepresentation binaryRepresentation) {
        
        double sx = 0;
        for(int i = 0; i < realValue.length; i++)
            sx+= Math.pow(realValue[i] - binaryRepresentation.getRealValue()[i],2.0);

        return Math.sqrt(sx);
    }

    @Override
    public int discreteDifference(BinaryRepresentation binaryRepresentation) {
        
        int differentBitsCounter = 0;
        for(int i = 0; i < binaryArray.length; i++)
        {
            for(int j = 0; j < binaryArray[i].length; j++)
            {
                differentBitsCounter += (binaryArray[i][j] == binaryRepresentation.getBinaryArray()[i][j] ? 1 : 0);
            }
        }
        return differentBitsCounter;
            
    }

    @Override
    public String printRealValue()
    {
        StringBuilder stringRealValue = new StringBuilder("(");
        for(double xi : realValue)
            stringRealValue.append(xi).append(" ");
        stringRealValue.append(")");
        return stringRealValue.toString();
    }

    @Override
    public boolean equals(Object state)
    {
        if(!(state instanceof BinaryRepresentation))
            return false;

        BinaryRepresentation binaryRepresentation = (BinaryRepresentation)state;

        return binaryString.equals(binaryRepresentation.getBinaryString());
    }

    @Override
    public int hashCode()
    {
        return binaryString.hashCode();
    }
    
}
