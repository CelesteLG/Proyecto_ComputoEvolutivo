package com.fciencias.evolutivo.binaryRepresentation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class BinaryDiscreteState extends AbstractBinaryRepresentation{

    private int[] elementos;

    public BinaryDiscreteState(int representationalBits)
    {
        this.representationalBits = representationalBits;
        this.binaryArray = new boolean[1][this.representationalBits];
        this.binaryString = arrayToString(this.binaryArray[0]);
        this.realValue = arrayToRealVector(binaryArray[0]);
    }

    public BinaryDiscreteState(int[] elementos)
    {
        this.elementos = elementos;
        this.representationalBits = elementos.length;
        encodeToBinary();
        this.binaryString = arrayToString(this.binaryArray[0]);
        this.realValue = arrayToRealVector(this.binaryArray[0]);
    }
    
    public BinaryDiscreteState(String binaryStrig)
    {
        super(binaryStrig);
        representationalBits = binaryStrig.length();
        binaryArray = new boolean[1][representationalBits];
        binaryArray[0] = stringToArray(binaryStrig);
        List<Integer> idElementos = new ArrayList<>();

        for(int i = 0; i < representationalBits; i++)
        {
            if(binaryArray[0][i])
                idElementos.add(i);
        }
        elementos = new int[idElementos.size()];
        for(int i = 0; i < elementos.length; i++)
            elementos[i] = idElementos.get(i);

        this.realValue = arrayToRealVector(binaryArray[0]);
    }

    public void encodeToBinary() {
        
        binaryArray = new boolean[1][representationalBits];
        for(int elemento : elementos)
            binaryArray[0][elemento] = true;
        
    }

    private double[] arrayToRealVector(boolean[] binaryarray)
    {
        double[] realVector = new double[representationalBits];
        for(int i = 0; i < representationalBits; i++)
            realVector[i] = (binaryarray[i] ? 1.0 : 0.0);

        return realVector;
    }

    @Override
    public String toString() {
        
        StringBuilder stringState = new StringBuilder(representationalBits*2 + 2).append("[");
        for(int i = 0; i < representationalBits; i++)
        {
            if(binaryArray[0][i])
                stringState.append(i).append(i == (representationalBits - 1) ? "" : ",");
        }
        stringState.append("]");
        return stringState.toString();
    }

    @Override
    public String printRealValue()
    {
        StringBuilder stringRealValue = new StringBuilder("(");
        int i = 0;
        for(double xi : realValue)
        {
            if(xi > 0)
                stringRealValue.append(i).append(" ");
            i++;
        }
        stringRealValue.append(")");
        return stringRealValue.toString();
    }

    @Override
    public BinaryRepresentation[] getNeighborhoods(double radius, int n) {
        
        if(n >= representationalBits)
        {
            
            BinaryRepresentation[] neighborhoods = new BinaryDiscreteState[representationalBits];
            
            boolean[] newBinaryArray = Arrays.copyOf(binaryArray[0], binaryArray[0].length);

            for(int i = 0; i < representationalBits; i ++)
            {
                if(i > 0)
                    newBinaryArray[i-1] = !newBinaryArray[i-1];   

                newBinaryArray[i] = !binaryArray[0][i];
                neighborhoods[i] = new BinaryDiscreteState(arrayToString(newBinaryArray));
                
            }
            return neighborhoods;
        }

        BinaryRepresentation[] neighborhoods = new BinaryDiscreteState[n];

        for(int i = 0; i < n; i ++)
            neighborhoods[i] = getRandomState(radius,realValue);
        return neighborhoods;

    }

    @Override
    public BinaryRepresentation getRandomState(double radius, double[] mu) {
        
        boolean[] newBinaryArray = new boolean[representationalBits];
        for(int i = 0; i < representationalBits; i++)
            newBinaryArray[i] = mu[i]==1;


        int randomBits = Math.min((int)(Math.round(radius)) ,representationalBits);
        List<Integer> changedBits = new ArrayList<>();

        int bitsCounter = 0;
        while(bitsCounter < randomBits)
        {
            final int bit = (int)(Math.random()*representationalBits);
            if(!changedBits.contains(bit))
            {
                newBinaryArray[bit] = !newBinaryArray[bit];
                changedBits.add(bit);
                bitsCounter++;
            }
        }
        
        String newBinaryString = arrayToString(newBinaryArray);
        return new BinaryDiscreteState(newBinaryString);
    }


    @Override
    public BinaryRepresentation getRandomState(double radius) {
        
        boolean[] newBinaryArray = new boolean[representationalBits];
        for(int i = 0; i < representationalBits; i++)
            newBinaryArray[i] = realValue[i]==1;


        int randomBits = Math.min((int)(Math.round(radius)) ,representationalBits);
        List<Integer> changedBits = new ArrayList<>();

        int bitsCounter = 0;
        while(bitsCounter < randomBits)
        {
            final int bit = (int)(Math.random()*representationalBits);
            if(!changedBits.contains(bit))
            {
                newBinaryArray[bit] = !newBinaryArray[bit];
                changedBits.add(bit);
                bitsCounter++;
            }
        }
        
        String newBinaryString = arrayToString(newBinaryArray);
        return new BinaryDiscreteState(newBinaryString);
    }
    
}
