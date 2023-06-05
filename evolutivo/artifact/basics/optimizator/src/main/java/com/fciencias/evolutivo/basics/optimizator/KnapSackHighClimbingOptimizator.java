package com.fciencias.evolutivo.basics.optimizator;

import com.fciencias.evolutivo.binaryRepresentation.BinaryDiscreteState;
import com.fciencias.evolutivo.binaryRepresentation.BinaryRepresentation;
import com.fciencias.evolutivo.evalFunctions.EvalFunction;

public class KnapSackHighClimbingOptimizator extends AbstractOptimizator
{

    protected double maxCost;
    protected EvalFunction weightCalculator;
    
    public KnapSackHighClimbingOptimizator(EvalFunction evalFunction, long iterations, int representationalBits,int dimension, int hilo) {

        super(evalFunction, iterations, representationalBits, dimension, hilo);
        optimizeToMax();
    }

    public KnapSackHighClimbingOptimizator(EvalFunction evalFunction, long iterations, int representationalBits,int dimension, BinaryRepresentation globalBinaryRepresentationState, int hilo) {

        super(evalFunction, iterations, representationalBits, dimension, globalBinaryRepresentationState,hilo);
        optimizeToMax();
    }

    public void setMaxCost(double maxCost)
    {
        this.maxCost = maxCost;
    }

    public void setWeightCalculator(EvalFunction weightCalculator) {
        this.weightCalculator = weightCalculator;
    }

    @Override
    public void initOptimizator() {
        
        BinaryDiscreteState binaryDiscreteState = new BinaryDiscreteState(representationalBits);
        globalBinaryRepresentationState = binaryDiscreteState.getRandomState(1);
        bestValue = evalFunction.evalSoution(globalBinaryRepresentationState.getRealValue());
        globalParams.replace(OPTIMUM_OBJECT, globalBinaryRepresentationState);
        globalParams.replace(optimizeDirection ?  MAXIMUN_VALUE : MINIMUN_VALUE, bestValue);
    }

    @Override
    public BinaryRepresentation[] getNewStates() {

        return globalBinaryRepresentationState.getNeighborhoods(1, representationalBits); 
    }

    @Override
    public boolean isValidState(BinaryRepresentation state) {
        
        return weightCalculator.evalSoution(state.getRealValue()) < maxCost;
    }

    @Override
    protected BinaryRepresentation cheapSolution() {
        
        return new BinaryDiscreteState(representationalBits);
    }

    @Override
    public AbstractOptimizator createOptimizator(int hilo, boolean logTrack) {
        
        KnapSackHighClimbingOptimizator knapsackOptimizator = new KnapSackHighClimbingOptimizator(evalFunction, iterations, representationalBits, dimension, globalBinaryRepresentationState, hilo);
        knapsackOptimizator.setLogTrack(logTrack);
        knapsackOptimizator.setMaxCost(maxCost);
        knapsackOptimizator.setWeightCalculator(weightCalculator);
        knapsackOptimizator.setTotalThreads(totalThreads);
        knapsackOptimizator.setOutputPath(outputPath);
        knapsackOptimizator.setGlobalParams(globalParams);
        knapsackOptimizator.bestValue = bestValue;
        knapsackOptimizator.globalParams.replace(MINIMUN_VALUE, bestValue);
        knapsackOptimizator.globalParams.replace(MAXIMUN_VALUE, bestValue);
        return knapsackOptimizator;
    }


}