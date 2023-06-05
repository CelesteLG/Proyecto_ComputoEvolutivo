package com.fciencias.evolutivo.implementations.tarea1;
import java.util.HashMap;
import java.util.Map;

import com.fciencias.evolutivo.basics.optimizator.AbstractOptimizator;
import com.fciencias.evolutivo.binaryRepresentation.BinaryMappingState;
import com.fciencias.evolutivo.binaryRepresentation.BinaryRepresentation;
import com.fciencias.evolutivo.evalFunctions.EvalFunction;
import com.fciencias.evolutivo.evalFunctions.SphereFunction;
import com.fciencias.evolutivo.libraries.ParamsValidator;

public class NeighborhoodOptimization extends AbstractOptimizator
{

    public NeighborhoodOptimization(EvalFunction evalFunction, double[] interval, long iterations, int representationalBits, int dimension, Map<String,Object> globalParams, int hilo) {
        
        super(evalFunction, iterations, representationalBits, dimension,hilo);
    }


    @Override
    public BinaryRepresentation[] getNewStates() {
        
        return globalBinaryRepresentationState.getNeighborhoods(0.1, 2);
    }

    @Override
    public boolean compareStates(BinaryRepresentation state1, BinaryRepresentation state2) {
        
        double valuation1 = evalFunction.evalSoution(state1.getRealValue());
        double valuation2 = evalFunction.evalSoution(state2.getRealValue());
        return ( (optimizeDirection && (valuation1 > valuation2)) || (!optimizeDirection && (valuation1 < valuation2)) );
    }

    @Override
    public void initOptimizator() {
        
        globalBinaryRepresentationState = new BinaryMappingState(representationalBits,interval,dimension);
        bestValue = evalFunction.evalSoution(globalBinaryRepresentationState.getRealValue());
    }

    

    @Override
    public AbstractOptimizator createOptimizator(int hilo, boolean logTrack) {
        
        NeighborhoodOptimization neighborhoodOptimization = new NeighborhoodOptimization(evalFunction, interval, iterations, representationalBits, dimension, globalParams, hilo);
        neighborhoodOptimization.setLogTrack(logTrack);
        neighborhoodOptimization.globalParams.replace(MINIMUN_VALUE,bestValue);
        return neighborhoodOptimization;
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
        NeighborhoodOptimization neighborhoodOptimization = new NeighborhoodOptimization(evalFunction, interval, iterations, representationalBits, dimension,globalParams,0);
        long deltaTime = neighborhoodOptimization.startMultiThreadOptimization(false,false);

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
