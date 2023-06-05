package com.fciencias.evolutivo.basics.optimizator;

import com.fciencias.evolutivo.basics.*;
import com.fciencias.evolutivo.binaryRepresentation.BinaryRepresentation;
import com.fciencias.evolutivo.evalFunctions.EvalFunction;

public abstract class RecocidoSimulado extends AbstractOptimizator{

    protected static final double INIT_TEMP = 20.0;

    protected static final double FINISH_TEMP = 0.00005;

    protected static final String TEMPERATURA = "TEMP";

    protected double temp;

    private double alpha;

    protected int totalIterations;

    protected RecocidoSimulado(EvalFunction evalFunction, long iterations, int representationalBits, int dimension, int hilo) {
        
        super(evalFunction, iterations, representationalBits, dimension, hilo);
        temp = INIT_TEMP;
        alpha = Math.pow(FINISH_TEMP/INIT_TEMP, 2.0/(iterations));
        totalIterations = 0;
    }

    protected RecocidoSimulado(EvalFunction evalFunction, long iterations, int representationalBits, int dimension, BinaryRepresentation globalBinaryRepresentationState, int hilo) {
        
        super(evalFunction, iterations, representationalBits, dimension, globalBinaryRepresentationState, hilo);
        temp = INIT_TEMP;
        alpha = Math.pow(FINISH_TEMP/INIT_TEMP, 2.0/(iterations));
        totalIterations = 0;
    }

    @Override
    protected boolean brakCondition()
    {
        return ((double)globalParams.get(TEMPERATURA)) < FINISH_TEMP ;
    }
    
    @Override
    public boolean isMoreOptimunState(BinaryRepresentation state) {
        
        boolean isMoreOptimun = false;
        RandomDistribution heatProbability = new HeatDistribution(new double[]{temp});
        double p = heatProbability.getRandomValue();
        long fileIndexOptimLog = fileManager.openFile(outputPath + "_RC_ImproveState.txt", true);
        long fileIndexOptimTab = fileManager.openFile(outputPath + "_RC_ImproveStateTab.txt", true);
        if(isValidState(state))
        {
            double stateEvaluation = evalFunction.evalSoution(state.getRealValue());
            if(optimizeDirection ^ (stateEvaluation < (double)globalParams.get(MAXIMUN_VALUE)))
            {
                globalParams.replace(MAXIMUN_VALUE, stateEvaluation);
                bestValue = stateEvaluation;
                isMoreOptimun = true;
                
                maximumValue = stateEvaluation;
                fileManager.writeLine(fileIndexOptimTab,globalParams.get(MAXIMUN_VALUE) + "");
                fileManager.writeLine(fileIndexOptimLog,globalParams.get(TEMPERATURA) + ", " + globalBinaryRepresentationState + " -> " + state + " : " + globalParams.get(MAXIMUN_VALUE));

            }
            else
            {
                if(p > ((double)globalParams.get(MAXIMUN_VALUE) - stateEvaluation))
                {
                    globalParams.replace(MAXIMUN_VALUE, stateEvaluation);
                    bestValue = stateEvaluation;
                    isMoreOptimun = true;
                    
                    maximumValue = stateEvaluation;
                    fileManager.writeLine(fileIndexOptimTab,globalParams.get(MAXIMUN_VALUE) + "");
                    fileManager.writeLine(fileIndexOptimLog,globalParams.get(TEMPERATURA) + ", " + globalBinaryRepresentationState + " ---> " + state + " : " + globalParams.get(MAXIMUN_VALUE));
                }
            }
        }
        globalParams.replace(TEMPERATURA, (double)globalParams.get(TEMPERATURA)*alpha);
        temp = temp*alpha;
        return isMoreOptimun;
    }

}
