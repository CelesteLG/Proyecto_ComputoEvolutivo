package com.fciencias.evolutivo.implementations.tarea1;

import java.util.HashMap;
import java.util.Map;

import com.fciencias.evolutivo.basics.optimizator.AbstractOptimizator;
import com.fciencias.evolutivo.binaryRepresentation.BinaryMappingState;
import com.fciencias.evolutivo.binaryRepresentation.BinaryRepresentation;
import com.fciencias.evolutivo.evalFunctions.EvalFunction;
import com.fciencias.evolutivo.evalFunctions.SphereFunction;
import com.fciencias.evolutivo.libraries.ParamsValidator;

public class RandomOptimizationV2 extends AbstractOptimizator{

    public RandomOptimizationV2(EvalFunction evalFunction, double[] interval, long iterations, int representationalBits, int dimension, Map<String,Object> globalParams, int hilo) {
        
        super(evalFunction, iterations, representationalBits, dimension,hilo);
    }

    @Override
    public void initOptimizator() {
        
        globalBinaryRepresentationState = new BinaryMappingState(representationalBits,interval,dimension);
        bestValue = evalFunction.evalSoution(globalBinaryRepresentationState.getRealValue());
    }

    @Override
    public BinaryRepresentation[] getNewStates() {
        
        double[] origin = new double[globalBinaryRepresentationState.getRealValue().length];
        for(int i = 0; i < origin.length; i++)
            origin[i] = 0;
        
        int newstatescount = 1;
        BinaryRepresentation[] newStates = new BinaryRepresentation[newstatescount];
        for(int i = 0; i < newstatescount; i++)
        {
            newStates[i] = globalBinaryRepresentationState.getRandomState(interval[1] - interval[0], origin);
        }
        return newStates;
        
    }

    @Override
    public boolean compareStates(BinaryRepresentation state1, BinaryRepresentation state2) {

        double valuation1 = evalFunction.evalSoution(state1.getRealValue());
        double valuation2 = evalFunction.evalSoution(state2.getRealValue());
        return ( (optimizeDirection && (valuation1 > valuation2)) || (!optimizeDirection && (valuation1 < valuation2)) );
    }

    @Override
    public AbstractOptimizator createOptimizator(int hilo, boolean logTrack) {

        RandomOptimizationV2 randomOptimization = new RandomOptimizationV2(evalFunction, interval, iterations, representationalBits, dimension, globalParams, hilo);
        randomOptimization.setLogTrack(logTrack);
        randomOptimization.globalParams.replace(MINIMUN_VALUE,bestValue);
        return randomOptimization;
    }
    
    public static void main( String[] args )
    {
        ParamsValidator.validate(args);
        int dimension = ParamsValidator.getDimension();
        int representationalBits = ParamsValidator.getRepresentationalBits();
        long iterations = ParamsValidator.getIterations();
        
        System.out.println("Parametros de ejecucion: ");
        System.out.println("\tDimension:  " + dimension);
        System.out.println("\tLongitud de bits:  " + representationalBits);
        System.out.println("\tIteraciones:  " + iterations);
        
        Map<String,Object> globalParams = new HashMap<>();
        EvalFunction evalFunction = new SphereFunction();
        double[] interval = new double[]{-5.12,5.12};
        
        RandomOptimizationV2 randomOptimizationV2 = new RandomOptimizationV2(evalFunction, interval, iterations, representationalBits, dimension,globalParams,0);
        randomOptimizationV2.setLogTrack(true);
        long deltaTime = randomOptimizationV2.startMultiThreadOptimization(false,true);

        StringBuilder endMessage = new StringBuilder("Results for " + evalFunction.getFunctionName() + " using dimension " + dimension + " and " + iterations+ " iterations \n")
        .append("Execution time: " + deltaTime/1000.0 + "s \n")
        .append("Best value: " + globalParams.get(MINIMUN_VALUE) + "\n")
        .append("Avg value: " + globalParams.get(MEAN_VALUE) + "\n")
        .append("Worst value: " + globalParams.get(MAXIMUN_VALUE) + "\n");

        System.out.println(endMessage.toString());

    }

    @Override
    protected BinaryRepresentation cheapSolution() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'cheapSolution'");
    }

    @Override
    public boolean isValidState(BinaryRepresentation state) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isValidState'");
    }
}
