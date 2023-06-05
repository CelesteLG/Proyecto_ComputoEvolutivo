package com.fciencias.evolutivo.basics.optimizator;

import java.util.LinkedList;
import java.util.List;

import com.fciencias.evolutivo.basics.DiscreteDistribution;
import com.fciencias.evolutivo.basics.RandomDistribution;
import com.fciencias.evolutivo.binaryRepresentation.BinaryMappingState;
import com.fciencias.evolutivo.binaryRepresentation.BinaryRepresentation;
import com.fciencias.evolutivo.evalFunctions.BinaryRepresentationSorter;
import com.fciencias.evolutivo.evalFunctions.EvalFunction;
import com.fciencias.evolutivo.evalFunctions.QueensFunction;
import com.fciencias.evolutivo.libraries.FileManager;

public class NQuinsOptimizator extends GeneticOptimizator{

    public NQuinsOptimizator(EvalFunction evalFunction, long iterations, int representationalBits, int dimension, int populationSize,int hilo) {
        super(evalFunction, iterations, representationalBits, dimension, populationSize, hilo);
        setInterval(interval);
        optimizeToMin();
    }

    @Override
    public void initOptimizator() {

        population = new LinkedList<>();
        // int bits = (int)Math.round(Math.log(dimension)/Math.log(2.0)) + 1;
        this.interval =  new double[]{0, Math.pow(2,representationalBits)};
        for(int i=0; i < populationSize;i++)
        {
            double[] realValue = new double[dimension];
            for(int j = 0; j < dimension; j++)
                realValue[j] = Math.floor(Math.random()*dimension);

            BinaryRepresentation individualState = new BinaryMappingState(realValue,representationalBits,interval);
            population.add(individualState);
        }
    }

    @Override
    public BinaryRepresentation[] getNewStates() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getNewStates'");
    }

    @Override
    protected BinaryRepresentation cheapSolution() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'cheapSolution'");
    }

    @Override
    public AbstractOptimizator createOptimizator(int hilo, boolean logTrack) {
        
        NQuinsOptimizator nQuinsOptimizator = new NQuinsOptimizator(evalFunction, iterations, representationalBits, dimension, populationSize, hilo);
        nQuinsOptimizator.setLogTrack(logTrack);
        nQuinsOptimizator.setTotalThreads(totalThreads);
        nQuinsOptimizator.setOutputPath(outputPath);
        nQuinsOptimizator.setGlobalParams(globalParams);
        nQuinsOptimizator.setMaxExecutionTime(maxExecutionTime);
        nQuinsOptimizator.setInterval(interval);
        nQuinsOptimizator.bestValue = bestValue;
        nQuinsOptimizator.globalParams.replace(MINIMUN_VALUE, bestValue);
        nQuinsOptimizator.globalParams.replace(MAXIMUN_VALUE, bestValue);
        return nQuinsOptimizator;
    }

    @Override
    protected List<BinaryRepresentation> selectBestParents() {
       
        double[] density = new double[population.size()];
        double[] distribution = new double[population.size()];
        List<BinaryRepresentation> adaptedParents = new LinkedList<>();

        density[0] = evalFunction.evalSoution(population.get(0).getRealValue());
        distribution[0] = density[0];
        for(int i =1; i < population.size(); i++)
        {
            density[i] = evalFunction.evalSoution(population.get(i).getRealValue());
            distribution[i] = distribution[i - 1] + density[i];
        }

        double totalSum = distribution[distribution.length - 1];

        for(int i =0; i < population.size(); i++)
        {
            density[i] = density[i]/totalSum;
            distribution[i] = distribution[i]/totalSum;
        }

        RandomDistribution randomDistribution = new DiscreteDistribution(distribution);
        int n = 2*((int)Math.round(Math.sqrt(population.size())) + 1);
        for(int i =0; i < n; i++)
        {
            int adaptedParentIndex = (int)Math.round(randomDistribution.getRandomValue());
            BinaryRepresentation adaptedParent = new BinaryMappingState(population.get(adaptedParentIndex));
            adaptedParents.add(adaptedParent);
        }

        return adaptedParents;
    }

    @Override
    protected List<BinaryRepresentation> pairStates(BinaryRepresentation firstParent, BinaryRepresentation secondParent) {
        
        List<BinaryRepresentation> childrens = new LinkedList<>();
        int crossPoint = (int)Math.round(Math.random()*dimension);

        double[] fChildValue = new double[dimension];
        double[] sChildValue = new double[dimension];
        
        for(int i = 0; i < firstParent.getRealValue().length; i++)
        {
            if(i < crossPoint)
            {
                fChildValue[i] = firstParent.getRealValue()[i];
                sChildValue[i] = secondParent.getRealValue()[i];
            }
            else
            {
                fChildValue[i] = secondParent.getRealValue()[i];
                sChildValue[i] = firstParent.getRealValue()[i]; 
            }
        }

        BinaryRepresentation firstChild = new BinaryMappingState(fChildValue,representationalBits,interval);
        BinaryRepresentation secondChild = new BinaryMappingState(sChildValue,representationalBits,interval);
        
        childrens.add(firstChild);
        childrens.add(secondChild);
        return childrens;
    }

    @Override
    protected BinaryRepresentation mutateState(BinaryRepresentation child) {
        
        int mutatePoint = (int)Math.floor(Math.random()*dimension);
        double[] originalChildValue = child.getRealValue();
        originalChildValue[mutatePoint] = (int)Math.floor(Math.random()*dimension);
        return new BinaryMappingState(originalChildValue,representationalBits,interval);
    }

    @Override
    protected List<BinaryRepresentation> selectBestChildrens(List<BinaryRepresentation> childrens) 
    {
        BinaryRepresentationSorter listSorter = new BinaryRepresentationSorter(evalFunction);
        List<BinaryRepresentation> sortedChildrens = listSorter.sortList(childrens);
        
        List<BinaryRepresentation> bestChildrens = new LinkedList<>();
        for(int i = 0; i < populationSize; i++)
        {
            bestChildrens.add(sortedChildrens.get(i));
        }

        return bestChildrens;
    }
    
    public static void main(String[] args) {
        
        int dimension = 15;
        int populationSize = 60;
        int maxTime = 10;
        int totalThreads = 20;
        int representationalBits = (int)Math.ceil(Math.log(dimension)/Math.log(2.0)); 
        NQuinsOptimizator nQuinsOptimizator = new NQuinsOptimizator(new QueensFunction(),10000, representationalBits, dimension, populationSize, 0);
        nQuinsOptimizator.setMaxExecutionTime(maxTime*1000.0);
        nQuinsOptimizator.setTotalThreads(totalThreads);
        nQuinsOptimizator.resetGlobalParams();
        long time = nQuinsOptimizator.startMultiThreadOptimization(false, true);
        System.out.println(time);
        List<BinaryRepresentation> resultList = (List<BinaryRepresentation>)nQuinsOptimizator.getResult();
        long iterations = (long)nQuinsOptimizator.globalParams.get(TOTAL_ITERATIONS);
        

        FileManager fileManager = new FileManager();
        long fileIndex = fileManager.openFile(OUTPUT_FILE_PATH + "tarea4/genetic/resultVectors_" + dimension + ".txt", false);
        long reportIndex = fileManager.openFile(OUTPUT_FILE_PATH + "tarea4/genetic/globalReport_" + dimension + ".txt", true);
        for(BinaryRepresentation result : resultList)
            fileManager.writeLine(fileIndex,result.printRealValue() + " , " + result.getBinaryString());

        fileManager.writeLine(reportIndex,dimension + "," + iterations + "," + resultList.size() + "," + populationSize);

        fileManager.closeFile(fileIndex);
        fileManager.closeFile(reportIndex);
        System.out.println("Soluciones encontradas: " + resultList.size());
    }
    
}
