package com.fciencas.evolutivo.implementations.drones;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.fciencias.evolutivo.basics.DiscreteDistribution;
import com.fciencias.evolutivo.basics.NormalRandomDistribution;
import com.fciencias.evolutivo.basics.RandomDistribution;
import com.fciencias.evolutivo.basics.optimizator.AbstractOptimizator;
import com.fciencias.evolutivo.basics.optimizator.GeneticOptimizator;
import com.fciencias.evolutivo.binaryRepresentation.BinaryMappingState;
import com.fciencias.evolutivo.binaryRepresentation.BinaryRepresentation;
import com.fciencias.evolutivo.evalFunctions.BinaryRepresentationSorter;
import com.fciencias.evolutivo.evalFunctions.EvalFunction;
import com.fciencias.evolutivo.evalFunctions.ExternalValuationFunction;
import com.fciencias.evolutivo.evalFunctions.IdentityEvaluation;
import com.fciencias.evolutivo.libraries.FileManager;

public class DronesOptimizator extends GeneticOptimizator{

    public static final String EVALUATION_INTERFACE = "../unity/data.txt";
    public static final String STATUS_INTERFACE = "../unity/status.txt";
    public static final String PARAMS_INTERFACE = "../unity/params.txt";

    private int[] integrantes;
    private List<BinaryRepresentation> representantes;
    
    protected DronesOptimizator(EvalFunction evalFunction, long iterations, int representationalBits, int dimension,
            int populationSize, int hilo,double[] interval) {
        super(evalFunction, iterations, representationalBits, dimension, populationSize, hilo);
        setInterval(interval);
        optimizeToMin();
    }

    @Override
    public void initOptimizator() {
        
        population = new LinkedList<>();
        this.interval =  new double[]{0, 20};

        if(fileManager == null)
            fileManager = new FileManager();

        long paramsIterface = fileManager.openFile(PARAMS_INTERFACE, false);
        fileManager.writeLine(paramsIterface,"population size:" + populationSize);

        for(int i=0; i < populationSize;i++)
        {
            double[] realValue = new double[dimension];
            for(int j = 0; j < dimension; j++)
                realValue[j] = Math.floor(Math.random()*dimension);

            BinaryRepresentation individualState = new BinaryMappingState(realValue,representationalBits,interval);
            population.add(individualState);

            StringBuilder paramsRecord = new StringBuilder();
            for(int j = 0; j < dimension; j ++)
            {
                paramsRecord.append(individualState.getRealValue()[j]);
                if(j < dimension -1)
                    paramsRecord.append(",");
            }
            fileManager.writeLine(paramsIterface,paramsRecord.toString());
        }
        fileManager.closeFile(paramsIterface);

        long statusIterface = fileManager.openFile(STATUS_INTERFACE, false);
        fileManager.writeFile(statusIterface,"optimizator status:READY\nexcecution status:PENDING",false);
        fileManager.closeFile(statusIterface);
        
        
    }

    @Override
    public BinaryRepresentation[] getNewStates() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getNewStates'");
    }

    @Override
    public boolean isValidState(BinaryRepresentation state) {

        double[] params = state.getRealValue();
        for(double param : params)
        {
            if(param < 0)
                return false;
        }
        return true;
    }

    protected List<BinaryRepresentation> readPopulationEvaluation()
    {
        long dataFile = fileManager.openFile(EVALUATION_INTERFACE, true);
        int popSize = Integer.parseInt(fileManager.readFileLine(dataFile, 0).split(":")[1]);
        List<BinaryRepresentation> newPopulation = new ArrayList<>(popSize);
        for(int i = 0; i < popSize; i++)
        {
            String[] dataValues = fileManager.readFileLine(dataFile, i+1).split(",");
            double[] values = new double[10];
            for(int j = 0; j < 10; j++)
                values[j] = Double.parseDouble(dataValues[j]);

            double[] individualParams = Arrays.copyOf(values, 9);
            double evaluation = values[9];
            BinaryRepresentation newIndividual = new BinaryMappingState(individualParams, representationalBits, interval);
            newIndividual.setEvaluation(evaluation);
            newPopulation.add(newIndividual);
            
        }
        fileManager.closeFile(dataFile);
        return newPopulation;
    }


    @Override
    protected List<BinaryRepresentation> selectBestParents() {
       
        population = readPopulationEvaluation();
        double[] density = new double[population.size()];
        double[] distribution = new double[population.size()];
        List<BinaryRepresentation> adaptedParents = new LinkedList<>();

        density[0] = 1/population.get(0).getEvaluation();
        
        distribution[0] = density[0];
        for(int i =1; i < population.size(); i++)
        {
            density[i] = 1.0/population.get(i).getEvaluation();
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
            adaptedParent.setEvaluation(population.get(adaptedParentIndex).getEvaluation());
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
        
        RandomDistribution normalDistribution = new NormalRandomDistribution(new double[]{0,2.0});
        double[] currentVal = child.getRealValue();
        for(int i =0; i < currentVal.length; i++)
        {
            currentVal[i]+= normalDistribution.getRandomValue();
            currentVal[i] = Math.max(currentVal[i], 0);
        }
        return new BinaryMappingState(currentVal,representationalBits,interval);
    }

    @Override
    protected boolean brakCondition()
    {
        return ((long)globalParams.get(TOTAL_ITERATIONS)) >=  iterations;
    }

    @Override
    protected List<BinaryRepresentation> selectBestChildrens(List<BinaryRepresentation> childrens) 
    {
        BinaryRepresentationSorter listSorter = new BinaryRepresentationSorter(new IdentityEvaluation(),false);
        List<BinaryRepresentation> sortedChildrens = listSorter.sortList(childrens);
        
        List<BinaryRepresentation> bestChildrens = new LinkedList<>();
        for(int i = 0; i < Math.min(populationSize,sortedChildrens.size()); i++)
        {
            bestChildrens.add(sortedChildrens.get(i));
        }

        return bestChildrens;
    }
    @Override
    protected BinaryRepresentation cheapSolution() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'cheapSolution'");
    }

    @Override
    public AbstractOptimizator createOptimizator(int hilo, boolean logTrack) {
        
        DronesOptimizator dronesOptimizator = new DronesOptimizator(evalFunction, iterations, representationalBits, dimension, populationSize, hilo,interval);
        dronesOptimizator.setLogTrack(logTrack);
        dronesOptimizator.setTotalThreads(totalThreads);
        dronesOptimizator.setOutputPath(outputPath);
        dronesOptimizator.setGlobalParams(globalParams);
        dronesOptimizator.setMaxExecutionTime(maxExecutionTime);
        dronesOptimizator.setInterval(interval);
        dronesOptimizator.bestValue = bestValue;
        dronesOptimizator.globalParams.replace(MINIMUN_VALUE, bestValue);
        dronesOptimizator.globalParams.replace(MAXIMUN_VALUE, bestValue);
        dronesOptimizator.optimizeToMin();
        return dronesOptimizator;
    }

    @Override
    public void optimize() {
        

        
        while(!brakCondition())
        {

            System.out.println("Generacion: " + globalParams.get(TOTAL_ITERATIONS));
            long statusIterface;
        
            String status = "";
    
            while(!status.equals("READY"))
            {
                statusIterface = fileManager.openFile(STATUS_INTERFACE, true);
                status = fileManager.readFileLine(statusIterface,1).split(":")[1];
                fileManager.closeFile(statusIterface);
                try 
                {
                    Thread.sleep(1000);
                } 
                catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
                System.out.println(status);
            }
        
                
            long initIterationTime = new Date().getTime();
            List<BinaryRepresentation> parents = selectBestParents();
            System.out.println("Seleccion de mejores padres");
            
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
            System.out.println("Nueva Generacion: " + newGen.size());



            population = selectBestChildrens(newGen);
            System.out.println("Seleccion de mejores hijos");
            
            population = diversityPreserve();
            System.out.println("Preservacion de diversidad");
            for(int integrantsSize : integrantes)
            {
                System.out.print(integrantsSize + " ");
            }

            System.out.println();
            for(BinaryRepresentation representante : representantes)
            {
                for(int i = 0; i < 9; i++)
                {
                    if(representante.getRealValue()[i] == 0)
                        System.out.print("000.0000 ");
                    else
                    {
                        if(representante.getRealValue()[i] < 10)
                            System.out.print("00");
                        else if(representante.getRealValue()[i] < 100)
                            System.out.print("0");

                        System.out.print(Math.round(representante.getRealValue()[i]*10000.0)/10000.0 + " ");
                    }

                }
                System.out.println();
            }

            long paramsIterface = fileManager.openFile(PARAMS_INTERFACE, false);
            fileManager.writeLine(paramsIterface,"population size:" + population.size());
            for(BinaryRepresentation individual : population)
            {
                StringBuilder paramsRecord = new StringBuilder();
                for(int i = 0; i < dimension; i ++)
                {
                    paramsRecord.append(individual.getRealValue()[i]);
                    if(i < dimension -1)
                        paramsRecord.append(",");
                }
                fileManager.writeLine(paramsIterface,paramsRecord.toString());
                    
            }
            fileManager.closeFile(paramsIterface);

            
            statusIterface = fileManager.openFile(STATUS_INTERFACE, false);
            fileManager.writeFile(statusIterface,"optimizator status:READY\nexcecution status:PENDING",false);
            fileManager.closeFile(statusIterface);
        
                
            long finishIterationTime = new Date().getTime();
            
            globalParams.replace(TOTAL_ITERATIONS,(long)globalParams.get(TOTAL_ITERATIONS) + 1L);
            globalParams.replace(EXECUTION_TIME,(long)globalParams.get(EXECUTION_TIME) + (finishIterationTime - initIterationTime)/totalThreads);
            
        }
    }

    public List<BinaryRepresentation> diversityPreserve()
    {   
        int nSize = 5;
        int mSize = populationSize/nSize;
        representantes = new ArrayList<>();
        integrantes = new int[nSize];
        double umbral = 36;
        List<BinaryRepresentation> diversePopulation = new ArrayList<>();
        representantes.add(population.get(0));
        for(int i =1; i < population.size(); i++)
        {
            double d = population.get(i).realDifference(representantes.get(0));
            int k = 0;
            for(int j = 1; j < representantes.size(); j++)
            {
                double dj = population.get(i).realDifference(representantes.get(j));
                if(dj < d)
                {
                    k = j;
                    d = dj;
                }
                    
            }
            if(d < umbral || representantes.size() >= nSize)
            {
                if(integrantes[k] < mSize)
                {
                    integrantes[k]++;
                    diversePopulation.add(population.get(i));
                }
                
            }
            else{
                integrantes[representantes.size()]++;
                representantes.add(population.get(i));
                diversePopulation.add(population.get(i));
            }

            
        }
        return diversePopulation;
    }
    

    public static void main(String[] args) {
        
        int dimension = 9;
        int populationSize = 900;
        int totalThreads = 1;
        int representationalBits = 30; 
        int totalGenerations = 2000;
        DronesOptimizator dronesOptimizator = new DronesOptimizator(new ExternalValuationFunction(EVALUATION_INTERFACE,100000000.0),totalGenerations,representationalBits, dimension, populationSize, 0, new double[]{0,20});
        dronesOptimizator.setTotalThreads(totalThreads);
        dronesOptimizator.resetMetaParams();
        dronesOptimizator.startMultiThreadOptimization(false, false);
    }
    
}
