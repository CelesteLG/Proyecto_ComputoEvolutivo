package com.fciencias.evolutivo.implementations.tarea1;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.fciencias.evolutivo.basics.NormalRandomDistribution;
import com.fciencias.evolutivo.basics.RandomDistribution;
import com.fciencias.evolutivo.binaryRepresentation.BinaryMappingState;
import com.fciencias.evolutivo.evalFunctions.*;
import com.fciencias.evolutivo.libraries.FileManager;


public class RandomOptimization implements Runnable
{
    private EvalFunction evalFunction;

    private double[] interval;

    private long iterations;

    private int representationalBits;

    private int dimension;

    private BinaryMappingState globalBinaryMappingState;

    private Map<String,Object> globalParams;

    private int hilo;

    private static final String FINALIZED_THREADS = "Finalized threads";
    
    private static final String MINIMUN_VALUE = "Min value";

    private static final String MAXIMUN_VALUE = "Maximun value";

    private static final String MEAN_VALUE = "Mean value";

    private static final String TOTAL_ITERATIONS = "Total iterations";

    private static final int TOTAL_THREADS = 25;

    private static final String OUTPUT_FILE_NAME = "evaluationTracking.txt";

    private static final String PROGRESS_INDICATOR = "progress indicator";


    

    public RandomOptimization(EvalFunction evalFunction, double[] interval, long iterations, int representationalBits,int dimension) {
        this.evalFunction = evalFunction;
        this.interval = interval;
        this.iterations = iterations;
        this.representationalBits = representationalBits;
        this.dimension = dimension;
    }

    public RandomOptimization(EvalFunction evalFunction, double[] interval, long iterations, int representationalBits,BinaryMappingState globalState, int dimension, int hilo) {
        this.evalFunction = evalFunction;
        this.interval = interval;
        this.iterations = iterations;
        this.representationalBits = representationalBits;
        this.globalBinaryMappingState = globalState;
        this.dimension = dimension;
        this.hilo = hilo;
    }

    public RandomOptimization(EvalFunction evalFunction, double[] interval, long iterations, int representationalBits,int dimension, BinaryMappingState globalBinaryMappingState) {
        this.evalFunction = evalFunction;
        this.interval = interval;
        this.iterations = iterations;
        this.representationalBits = representationalBits;
        this.dimension = dimension;
        this.globalBinaryMappingState = globalBinaryMappingState;
    }

    public EvalFunction getEvalFunction() {
        return evalFunction;
    }

    public void setEvalFunction(EvalFunction evalFunction) {
        this.evalFunction = evalFunction;
    }

    public double[] getInterval() {
        return interval;
    }

    public void setInterval(double[] interval) {
        this.interval = interval;
    }

    public long getIterations() {
        return iterations;
    }

    public void setIterations(long iterations) {
        this.iterations = iterations;
    }

    public int getRepresentationalBits() {
        return representationalBits;
    }

    public void setRepresentationalBits(int representationalBits) {
        this.representationalBits = representationalBits;
    }

    
    public BinaryMappingState getGlobalBinaryMappingState() {
        return globalBinaryMappingState;
    }

    public void setGlobalBinaryMappingState(BinaryMappingState globalBinaryMappingState) {
        this.globalBinaryMappingState = globalBinaryMappingState;
    }

    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public Map<String, Object> getGlobalParams() {
        return globalParams;
    }

    public void setGlobalParams(Map<String, Object> globalParams) {
        this.globalParams = globalParams;
    }

    public double[] getRandomVector(double[] interval,int n, int randomize)
    {
        Date date = new Date();
        long time = date.getTime();
        Random rand = new Random(time*randomize);

        double[] randVector = new double[n];
        for(int i = 0; i < n; i ++)
        {
            randVector[i] = rand.nextDouble()*(interval[1] - interval[0]) + interval[0];
        }
        return randVector;
    }

    public BinaryMappingState getRandomState(double[] interval, int dimension, int bits,int randomize, RandomDistribution randomDistribution)
    {
        return new BinaryMappingState(getRandomVector(interval, dimension,randomize),bits, interval,randomDistribution);
    }

    public BinaryMappingState optimize()
    {
        for(int i = 0; i < iterations; i ++)
        {
            
            BinaryMappingState binaryMappingState = getRandomState(interval,dimension,representationalBits,hilo,globalBinaryMappingState.getRandomDistribution());
            double currentStateValue = evalFunction.evalSoution(binaryMappingState.getRealValue()); 
            StringBuilder vector = new StringBuilder("(");
            for(double xi : binaryMappingState.getRealValue())
                vector.append(xi).append(" ");
            vector.append(")");
            
            int totaIterations = (Integer)globalParams.get(TOTAL_ITERATIONS);
            globalParams.replace(TOTAL_ITERATIONS, (Integer)globalParams.get(TOTAL_ITERATIONS) + 1);
            globalParams.replace(PROGRESS_INDICATOR, (Double)globalParams.get(PROGRESS_INDICATOR) + 1);
            double mean = (((Double)globalParams.get(MEAN_VALUE))*totaIterations);
            mean = mean + currentStateValue;
            mean = mean/(totaIterations + 1);
            globalParams.replace(MEAN_VALUE, mean);

            double minValue = ((Double)globalParams.get(MINIMUN_VALUE));
            double maxValue = ((Double)globalParams.get(MAXIMUN_VALUE));
            if(currentStateValue < minValue)
            {
                globalParams.replace(MINIMUN_VALUE, currentStateValue);
                globalBinaryMappingState = binaryMappingState;
                minValue = currentStateValue;
                FileManager fileManager = new FileManager();
                String fileName = OUTPUT_FILE_NAME;
                long fileIndex = fileManager.openFile(fileName,true);
                fileManager.writeFile(fileIndex,"Function: "+ evalFunction.getFunctionName() + ", Thread: "+ hilo + ",Iteration: " + i + ",Vector: " + vector  + ",Value: " + minValue + "\n",true);
            }
            else if(currentStateValue > maxValue)
            {
                globalParams.replace(MAXIMUN_VALUE, currentStateValue);
            }

        }

        return globalBinaryMappingState;
    }

    @Override
    public void run() {
        
        optimize();
        ((boolean[])(globalParams.get(FINALIZED_THREADS)))[hilo-1] = true;
    }

    public static String runEvaluationProcess(EvalFunction evalFunction, int dimension, double[] interval, int representationalBits, long iterations, boolean appendFile, Map<String,Object> globalParams)
    {

        FileManager fileManager = new FileManager();
        long fileIndex = fileManager.openFile(OUTPUT_FILE_NAME,appendFile);
        fileManager.writeFile(fileIndex,(appendFile ? "\n" : "") + "Tracking for " + evalFunction.getFunctionName() + "\n",appendFile);

        RandomOptimization optimumRandomOptimization = new RandomOptimization(evalFunction, interval, iterations, representationalBits,dimension);
        BinaryMappingState optimuBinaryMappingState = optimumRandomOptimization.getRandomState(interval, dimension, representationalBits,0,new NormalRandomDistribution(new double[]{0,1}));
        optimumRandomOptimization.setGlobalBinaryMappingState(optimuBinaryMappingState);

        
        double initValue = evalFunction.evalSoution(optimuBinaryMappingState.getRealValue());
        globalParams.replace(MAXIMUN_VALUE, initValue);
        globalParams.replace(MINIMUN_VALUE, initValue);
        globalParams.replace(MEAN_VALUE, initValue);

        long initTime = new Date().getTime();

        for(int k = 1; k < TOTAL_THREADS + 1; k++)
        {
            RandomOptimization randomOptimization = new RandomOptimization(evalFunction, interval, iterations, representationalBits,optimuBinaryMappingState,dimension,k);
            randomOptimization.setGlobalParams(globalParams);
            new Thread(randomOptimization).start();
        }

        boolean allThreads = false;
        while(!allThreads)
        {
            boolean currentAll = true;
            for(boolean threadFinished : (boolean[])globalParams.get(FINALIZED_THREADS))
            {
                currentAll = currentAll&&threadFinished;
            }
            allThreads = currentAll;
        }
        long finalTime = new Date().getTime();

        long deltatime = finalTime - initTime;

        StringBuilder endMessage = new StringBuilder("Results for " + evalFunction.getFunctionName() + " using dimension " + dimension + " and " + iterations+ " iterations \n")
        .append("Execution time: " + deltatime/1000.0 + "s \n")
        .append("Best value: " + globalParams.get(MINIMUN_VALUE) + "\n")
        .append("Avg value: " + globalParams.get(MEAN_VALUE) + "\n")
        .append("Worst value: " + globalParams.get(MAXIMUN_VALUE) + "\n");
        return endMessage.toString();
    }

    public static void resetGlobalMap(Map<String,Object> globalMap)
    {
        if(!globalMap.containsKey(MAXIMUN_VALUE))
            globalMap.put(MAXIMUN_VALUE,0.0);
        else
            globalMap.replace(MAXIMUN_VALUE, 0.0);

        
        if(!globalMap.containsKey(MINIMUN_VALUE))
            globalMap.put(MINIMUN_VALUE,0.0);
        else
            globalMap.replace(MINIMUN_VALUE, 0.0);


        if(!globalMap.containsKey(MEAN_VALUE))
            globalMap.put(MEAN_VALUE,0.0);
        else
            globalMap.replace(MEAN_VALUE, 0.0);


        if(!globalMap.containsKey(TOTAL_ITERATIONS))
            globalMap.put(TOTAL_ITERATIONS,0);
        else
            globalMap.replace(TOTAL_ITERATIONS, 0);


        if(!globalMap.containsKey(PROGRESS_INDICATOR))
            globalMap.put(PROGRESS_INDICATOR,0.0);
        else
            globalMap.replace(PROGRESS_INDICATOR, 0.0);


        if(!globalMap.containsKey(FINALIZED_THREADS))
            globalMap.put(FINALIZED_THREADS,new boolean[TOTAL_THREADS]);
        else
            globalMap.replace(FINALIZED_THREADS, new boolean[TOTAL_THREADS]);

    }

    public static void main(String[] args) {
        
        int dimension = 3;
        int representationalBits = 64;
        long iterations = 10000;

        Map<String,Object> globalParams = new HashMap<>();

        FileManager fileManager = new FileManager();
        
        String results = "";
        
        RandomOptimization.resetGlobalMap(globalParams);
        EvalFunction evalFunction = new SphereFunction();
        double[] interval = new double[]{-5.12,5.12};
        results = results + RandomOptimization.runEvaluationProcess(evalFunction, dimension, interval, representationalBits, iterations,false,globalParams);

        RandomOptimization.resetGlobalMap(globalParams);
        evalFunction = new AckleyFunction();
        interval = new double[]{-30,30};
        results = results + "\n" + RandomOptimization.runEvaluationProcess(evalFunction, dimension, interval, representationalBits, iterations,true,globalParams);


        RandomOptimization.resetGlobalMap(globalParams);
        evalFunction = new GriewankFunction();
        interval = new double[]{-600,600};
        results = results + "\n" + RandomOptimization.runEvaluationProcess(evalFunction, dimension, interval, representationalBits, iterations,true,globalParams);

        RandomOptimization.resetGlobalMap(globalParams);
        evalFunction = new ThenthPowerFunction();
        interval = new double[]{-5.12,5.12};
        results = results + "\n" + RandomOptimization.runEvaluationProcess(evalFunction, dimension, interval, representationalBits, iterations,true,globalParams);


        RandomOptimization.resetGlobalMap(globalParams);
        evalFunction = new RastriginFunction();
        interval = new double[]{-5.12,5.12};
        results = results + "\n" + RandomOptimization.runEvaluationProcess(evalFunction, dimension, interval, representationalBits, iterations,true,globalParams);


        RandomOptimization.resetGlobalMap(globalParams);
        evalFunction = new RosenbrockFunction();
        interval = new double[]{-2.048,2.048};
        results = results + "\n" + RandomOptimization.runEvaluationProcess(evalFunction, dimension, interval, representationalBits, iterations,true,globalParams);
        fileManager.writeFile(fileManager.openFile("Results.txt", false),results,true);
    }

}