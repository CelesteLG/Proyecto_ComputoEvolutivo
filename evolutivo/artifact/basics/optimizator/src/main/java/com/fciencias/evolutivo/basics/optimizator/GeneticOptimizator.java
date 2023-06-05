package com.fciencias.evolutivo.basics.optimizator;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.fciencias.evolutivo.binaryRepresentation.BinaryRepresentation;
import com.fciencias.evolutivo.evalFunctions.EvalFunction;
import com.fciencias.evolutivo.libraries.FileManager;

public abstract class GeneticOptimizator extends AbstractOptimizator{

    protected List<BinaryRepresentation> population;
    
    protected double maxExecutionTime;
    protected int populationSize = 1;

    protected static final String BEST_POPULATION = "best_population";


    protected GeneticOptimizator(EvalFunction evalFunction, long iterations, int representationalBits, int dimension, int populationSize ,int hilo)
    {
        this.evalFunction = evalFunction;
        this.iterations = iterations;
        this.representationalBits = representationalBits;
        this.dimension = dimension;
        this.hilo = hilo;
        this.populationSize = populationSize;
        this.globalParams = new HashMap<>();
        globalParams.put(BEST_POPULATION, new LinkedList<>());
        fileManager = new FileManager();
        resetGlobalParams();
        initOptimizator();
    }

    public void setMaxExecutionTime(double maxExecutionTime)
    {
        this.maxExecutionTime = maxExecutionTime;
    }

    public void setPopulationSize(int populationSize)
    {
        this.populationSize = populationSize;
    }

   
    @Override
    protected boolean brakCondition()
    {
        return ((long)globalParams.get(EXECUTION_TIME)) >= maxExecutionTime ;
    }

    @Override
    public boolean isValidState(BinaryRepresentation state) {
        
        List<BinaryRepresentation> bestPopulation = (List<BinaryRepresentation>)globalParams.get(BEST_POPULATION);
        try 
        {
            for(BinaryRepresentation existingState : bestPopulation)
                if(state.equals(existingState))
                {
                    return false;
                }
        } catch (Exception e) {
           System.err.println("Colisiono pero no hay falla, el programa sigue.");
        }
        
              
        double evaluation =evalFunction.evalSoution(state.getRealValue());
        if(evaluation > -1.0)
        {
            System.out.println("Encontrado valor chido: " + evaluation + ", estados encontrados: " + ((List<BinaryRepresentation>)globalParams.get(BEST_POPULATION)).size());

        }
        return  (evaluation > -1.0);
    }
    
    @Override
    public boolean isMoreOptimunState(BinaryRepresentation state) {
        
        boolean isMoreOptimun = false;
        long fileIndexOptimLog = fileManager.openFile(outputPath + "_RC_ImproveState.txt", true);
        long fileIndexOptimTab = fileManager.openFile(outputPath + "_RC_ImproveStateTab.txt", true);
        if(isValidState(state))
        {
            double stateEvaluation = evalFunction.evalSoution(state.getRealValue());
            if(optimizeDirection ^ (stateEvaluation <= (double)globalParams.get(MAXIMUN_VALUE)))
            {
                globalParams.replace(MAXIMUN_VALUE, stateEvaluation);
                bestValue = stateEvaluation;
                isMoreOptimun = true;
                maximumValue = stateEvaluation;
                fileManager.writeLine(fileIndexOptimTab,globalParams.get(MAXIMUN_VALUE) + "");
                fileManager.writeLine(fileIndexOptimLog,globalBinaryRepresentationState + " -> " + state + " : " + globalParams.get(MAXIMUN_VALUE));
                ((List<BinaryRepresentation>)globalParams.get(BEST_POPULATION)).add(state);
            }
        }
        
        return isMoreOptimun;
    }

    @Override
    public void optimize() {
        
        while(!brakCondition())
        {
            long initIterationTime = new Date().getTime();
            List<BinaryRepresentation> parents = selectBestParents();
            
            List<BinaryRepresentation> newGen = new LinkedList<>();
            int parentsSize = parents.size();
            for(int i =0; i < parentsSize; i++)
            {
                for(int j = (i+1); j < parentsSize; j++)
                {
                    if(!(parents.get(i).equals(parents.get(j))) || true)
                    {
                        List<BinaryRepresentation> childrens = pairStates(parents.get(i), parents.get(j));
                        for(BinaryRepresentation child : childrens)
                        {
                            child = mutateState(child);
                            newGen.add(child);
                        }
                    }
                }
            }
           
            population = selectBestChildrens(newGen);

            for(BinaryRepresentation bestState : population)
                isMoreOptimunState(bestState);
            long finishIterationTime = new Date().getTime();
            
            globalParams.replace(TOTAL_ITERATIONS,(long)globalParams.get(TOTAL_ITERATIONS) + 1L);
            globalParams.replace(EXECUTION_TIME,(long)globalParams.get(EXECUTION_TIME) + (finishIterationTime - initIterationTime)/totalThreads);
        }
    }

    @Override
    public Object getResult()
    {
        return globalParams.get(BEST_POPULATION);
    }

    

    protected abstract List<BinaryRepresentation> selectBestParents();

    protected abstract List<BinaryRepresentation> pairStates(BinaryRepresentation firstParent, BinaryRepresentation secondParent);

    protected abstract BinaryRepresentation mutateState(BinaryRepresentation child);

    protected abstract List<BinaryRepresentation> selectBestChildrens(List<BinaryRepresentation> childrens);

    
}
