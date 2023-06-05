package com.fciencias.evolutivo.basics.optimizator;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fciencias.evolutivo.basics.RandomDistribution;
import com.fciencias.evolutivo.binaryRepresentation.BinaryRepresentation;
import com.fciencias.evolutivo.evalFunctions.EvalFunction;
import com.fciencias.evolutivo.evalFunctions.SphereFunction;
import com.fciencias.evolutivo.libraries.FileManager;

/**
 * Hello world!
 *
 */
public abstract class AbstractOptimizator implements Optimizator, Runnable
{
    protected EvalFunction evalFunction;
    protected double[] interval;
    protected long iterations;
    protected int representationalBits;
    protected int dimension;
    protected boolean optimizeDirection = false;
    protected double minimumValue;
    protected double maximumValue;
    protected double bestValue;
    protected boolean logTrack;
    protected RandomDistribution randomDistribution;
    protected FileManager fileManager;
    


    protected BinaryRepresentation globalBinaryRepresentationState;

    protected Map<String,Object> globalParams;

    protected int hilo;

    protected int totalThreads = 32;

    protected String inputPath;

    protected String outputPath;

    

    protected static final String FINALIZED_THREADS = "Finalized threads";
    
    protected static final String MINIMUN_VALUE = "Min value";

    protected static final String MAXIMUN_VALUE = "Maximun value";

    protected static final String MEAN_VALUE = "Mean value";

    protected static final String TOTAL_ITERATIONS = "Total iterations";

    protected static final String OUTPUT_FILE_PATH = "outputs/";

    protected static final String PROGRESS_INDICATOR = "progress indicator";

    protected static final String OPTIMUM_OBJECT = "optimum";

    protected static final String EXECUTION_TIME = "exec_time";
    
    protected AbstractOptimizator()
    {
        this.evalFunction = new SphereFunction();
        this.iterations = 1000;
        this.representationalBits = 8;
        this.dimension = 3;
        initOptimizator();
        fileManager = new FileManager();
    }

    protected AbstractOptimizator(EvalFunction evalFunction, long iterations, int representationalBits, int dimension, int hilo) {
        this.evalFunction = evalFunction;
        this.iterations = iterations;
        this.representationalBits = representationalBits;
        this.dimension = dimension;
        this.hilo = hilo;
        this.globalParams = new HashMap<>();
        resetGlobalParams();
        initOptimizator();
        fileManager = new FileManager();
    }

    protected AbstractOptimizator(EvalFunction evalFunction, long iterations, int representationalBits, int dimension, BinaryRepresentation globalBinaryRepresentationState, int hilo) {
        this.evalFunction = evalFunction;
        this.iterations = iterations;
        this.representationalBits = representationalBits;
        this.dimension = dimension;
        this.globalBinaryRepresentationState = globalBinaryRepresentationState;
        this.hilo = hilo;
        this.globalParams = new HashMap<>();
        resetGlobalParams();
        bestValue = evalFunction.evalSoution(globalBinaryRepresentationState.getRealValue());
        
        globalParams.replace(OPTIMUM_OBJECT, globalBinaryRepresentationState);
        globalParams.replace(MAXIMUN_VALUE, bestValue);
        
        fileManager = new FileManager();
        
    }


    public void setInterval(double[] interval)
    {
        this.interval = interval;
    }
    
    public void setRandomDistribution(RandomDistribution randomDistribution) {
        this.randomDistribution = randomDistribution;
    }

    public void setGlobalBinaryRepresentationState(BinaryRepresentation globalBinaryRepresentationState)
    {
        this.globalBinaryRepresentationState = globalBinaryRepresentationState;
    }

    public BinaryRepresentation getGlobalBinaryRepresentationState()
    {
        return globalBinaryRepresentationState;
    }
    
    public void setGlobalParams(Map<String,Object> globalParams)
    {
        this.globalParams = globalParams;
    }

    public int getTotalThreads() {
        return totalThreads;
    }

    public long getIterations()
    {
        return  (long)(globalParams.get(TOTAL_ITERATIONS));
    }

    public void setIterations(long iterations)
    {
        globalParams.replace(TOTAL_ITERATIONS,iterations);
    }

    public void setInitialState(BinaryRepresentation initialState)
    {
        if(isValidState(initialState))
        {
            globalBinaryRepresentationState = initialState;
            bestValue = evalFunction.evalSoution(initialState.getRealValue());
            globalParams.replace(OPTIMUM_OBJECT, initialState);
            globalParams.replace(MAXIMUN_VALUE, bestValue);
        }
        
    }

    public BinaryRepresentation disturbState(double disturbRate)
    {
        BinaryRepresentation currentSolution = (BinaryRepresentation)globalParams.get(OPTIMUM_OBJECT);
        BinaryRepresentation distSolution;
        do
            distSolution = currentSolution.getRandomState(currentSolution.getRepresentationalBits()*disturbRate, currentSolution.getRealValue());
        while(!isValidState(distSolution));
        return distSolution;
    }

    public BinaryRepresentation disturbState(int disturbBits)
    {
        BinaryRepresentation currentSolution = (BinaryRepresentation)globalParams.get(OPTIMUM_OBJECT);
        BinaryRepresentation distSolution;
        do
            distSolution = currentSolution.getRandomState(disturbBits, currentSolution.getRealValue());
        while(!isValidState(distSolution));
        return distSolution;
    }

    public void resetMetaParams()
    {
        globalParams.replace(TOTAL_ITERATIONS,0L);
        globalParams.replace(FINALIZED_THREADS, new boolean[totalThreads]);
    }

    public String getInputPath() {
        return inputPath;
    }

    public void setInputPath(String inputPath) {
        this.inputPath = inputPath;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public void setTotalThreads(int totalThreads) {
        this.totalThreads = totalThreads;
    }

    @Override
    public void run() {
        
        optimize();
        ((boolean[])(globalParams.get(FINALIZED_THREADS)))[hilo-1] = true;
    }

    protected boolean brakCondition()
    {
        return false;
    }


    @Override
    public boolean compareStates(BinaryRepresentation state1, BinaryRepresentation state2) {
        double valuation1 = evalFunction.evalSoution(state1.getRealValue());
        double valuation2 = evalFunction.evalSoution(state2.getRealValue());
        return ( (optimizeDirection && (valuation1 > valuation2)) || (!optimizeDirection && (valuation1 < valuation2)) );
    }
    

    protected abstract BinaryRepresentation cheapSolution();

    @Override
    public void optimize() {
        
        while(((long)globalParams.get(TOTAL_ITERATIONS) < iterations) && !brakCondition())
        {
            BinaryRepresentation[] newElements = getNewStates();
            boolean foundBetter = false;
            for(BinaryRepresentation element : newElements)
            {    
                if(isMoreOptimunState(element))
                {
                    foundBetter = true;
                    globalParams.replace(OPTIMUM_OBJECT, element);
                    globalBinaryRepresentationState = (BinaryRepresentation)globalParams.get(OPTIMUM_OBJECT);
                    if(logTrack)
                    {
                        long fileIndex = fileManager.openFile(outputPath + "_log.txt",true);
                        fileManager.writeFile(fileIndex,"Function: "+ evalFunction.getFunctionName() + ", Thread: "+ hilo + ", Iteration: " + globalParams.get(TOTAL_ITERATIONS) + ", Vector: " + globalBinaryRepresentationState.printRealValue()  + ", Value: " + bestValue + "\n",true);
                    }
                    
                }
                if(brakCondition())
                    break;
            }
            if(!foundBetter && globalParams.get(OPTIMUM_OBJECT) == null)
            {
                globalParams.replace(OPTIMUM_OBJECT, cheapSolution());
                globalBinaryRepresentationState = (BinaryRepresentation)globalParams.get(OPTIMUM_OBJECT);
            }

            globalParams.replace(TOTAL_ITERATIONS,(long)globalParams.get(TOTAL_ITERATIONS) + 1L);
        }
    }

    @Override
    public boolean isMoreOptimunState(BinaryRepresentation state) {
        
        double valuation = evalFunction.evalSoution(state.getRealValue());
        boolean isMoreOptimun = false;
        long fileIndexOptimLog = fileManager.openFile(outputPath + "_RC_ImproveState.txt", true);
        long fileIndexOptimTab = fileManager.openFile(outputPath + "_RC_ImproveStateTab.txt", true);

        if(isValidState(state))
        {
            if(optimizeDirection)
            {
                
                if(valuation > (double)globalParams.get(MAXIMUN_VALUE))
                {
                    Object bestVal = globalParams.get(MAXIMUN_VALUE);
                    globalParams.replace(MAXIMUN_VALUE, valuation);
                    bestValue = valuation;
                    isMoreOptimun = true;
                    maximumValue = valuation;
                    fileManager.writeLine(fileIndexOptimTab,globalParams.get(MAXIMUN_VALUE) + "");
                    fileManager.writeLine(fileIndexOptimLog,globalParams.get(OPTIMUM_OBJECT) + " : " + bestVal + " -> " + state + " : " + globalParams.get(MAXIMUN_VALUE));

                }
                else if(valuation < (double)globalParams.get(MINIMUN_VALUE))
                {
                    globalParams.replace(MINIMUN_VALUE, valuation);
                    minimumValue = valuation;
                }
            }
            else
            {
                if(valuation > (double)globalParams.get(MAXIMUN_VALUE))
                {
                    globalParams.replace(MAXIMUN_VALUE, valuation);
                    maximumValue = valuation;

                }
                else if(valuation < (double)globalParams.get(MINIMUN_VALUE))
                {
                    globalParams.replace(MINIMUN_VALUE, valuation);
                    bestValue = valuation;
                    isMoreOptimun = true;
                    minimumValue = valuation;
                    fileManager.writeLine(fileIndexOptimLog,globalParams.get(OPTIMUM_OBJECT) + " -> " + state + " : " + globalParams.get(MINIMUN_VALUE));
                    fileManager.writeLine(fileIndexOptimTab,globalParams.get(MAXIMUN_VALUE) + "");
                }
            }
        }
        
        return isMoreOptimun;
    }

    @Override
    public long startMultiThreadOptimization(boolean appendFile, boolean logTrack)
    {
        
        long fileImproveReg = fileManager.openFile(outputPath + "_RC_ImproveState.txt",appendFile);
        fileManager.closeFile(fileImproveReg);

        fileImproveReg = fileManager.openFile(outputPath + "_RC_ImproveStateTab.txt",appendFile);
        fileManager.closeFile(fileImproveReg);


        long fileIndex = fileManager.openFile(outputPath + "_log.txt",appendFile);
        fileManager.writeFile(fileIndex,(appendFile ? "\n" : "") + "Tracking for " + evalFunction.getFunctionName() + "\n",appendFile);


        long initTime = new Date().getTime();
        
        for(int k = 1; k < totalThreads + 1; k++)
        {
            new Thread(createOptimizator(k,logTrack)).start();
        }

        boolean allThreads = false;
        long timeIni = new Date().getTime();
        while(!allThreads)
        {
            boolean currentAll = true;
            for(boolean threadFinished : (boolean[])globalParams.get(FINALIZED_THREADS))
            
                currentAll = currentAll && threadFinished;

            allThreads = currentAll;
            // long currentTime = new Date().getTime();
            // if(currentTime - timeIni >= 2000)
            // {
            //     timeIni = currentTime;
            //     // Double progress = Math.round(((long)globalParams.get(TOTAL_ITERATIONS))*10000.0/iterations)/100.0;
            //     // if(progress > 100.0)
            //     //     progress = 100.0;
            //     // System.out.println(progress + "% completado");
            //     // StringBuilder progressBar = new StringBuilder("|");
            //     // int barLength = 100;
            //     // for(int i = 0; i < barLength; i++)
            //     // {
            //     //     progressBar.append( i < barLength*progress/100.0 ? "=" : " ");
            //     // }
            //     // progressBar.append("|");
            //     // System.out.println(progressBar);
            //     // System.out.println(progressBar + "\n");
            //     for(boolean threadFinished : (boolean[])globalParams.get(FINALIZED_THREADS))
            
            //         System.out.println(threadFinished);
            // }
        }
        bestValue = (double)globalParams.get(optimizeDirection ? MAXIMUN_VALUE : MINIMUN_VALUE);
        globalBinaryRepresentationState = (BinaryRepresentation)globalParams.get(OPTIMUM_OBJECT);
        long finalTime = new Date().getTime();
        return finalTime - initTime;

    }

    public abstract AbstractOptimizator createOptimizator(int hilo, boolean logTrack);

    public void resetGlobalParams()
    {
        if(!globalParams.containsKey(MAXIMUN_VALUE))
            globalParams.put(MAXIMUN_VALUE,0.0);
        else
            globalParams.replace(MAXIMUN_VALUE, 0.0);

        
        if(!globalParams.containsKey(MINIMUN_VALUE))
            globalParams.put(MINIMUN_VALUE,0.0);
        else
            globalParams.replace(MINIMUN_VALUE, 0.0);


        if(!globalParams.containsKey(MEAN_VALUE))
            globalParams.put(MEAN_VALUE,0.0);
        else
            globalParams.replace(MEAN_VALUE, 0.0);


        if(!globalParams.containsKey(TOTAL_ITERATIONS))
            globalParams.put(TOTAL_ITERATIONS,0L);
        else
            globalParams.replace(TOTAL_ITERATIONS, 0L);


        if(!globalParams.containsKey(PROGRESS_INDICATOR))
            globalParams.put(PROGRESS_INDICATOR,0.0);
        else
            globalParams.replace(PROGRESS_INDICATOR, 0.0);


        if(!globalParams.containsKey(FINALIZED_THREADS))
            globalParams.put(FINALIZED_THREADS,new boolean[totalThreads]);
        else
            globalParams.replace(FINALIZED_THREADS, new boolean[totalThreads]);

        if(!globalParams.containsKey(OPTIMUM_OBJECT))
            globalParams.put(OPTIMUM_OBJECT,null);
        else
            globalParams.replace(OPTIMUM_OBJECT, null);

        if(!globalParams.containsKey(EXECUTION_TIME))
            globalParams.put(EXECUTION_TIME,0L);
        else
            globalParams.replace(EXECUTION_TIME, 0L);

    }

    public void optimizeToMax()
    {
        this.optimizeDirection = true;
    }

    public void optimizeToMin()
    {
        this.optimizeDirection = false;
    }

    public void setLogTrack(boolean logTrack) {
        this.logTrack = logTrack;
    }

    @Override
    public BinaryRepresentation getOptimumState()
    {
        return (BinaryRepresentation)globalParams.get(OPTIMUM_OBJECT);
    }

    @Override
    public double getOptimumValuation()
    {
        return (double)globalParams.get(MAXIMUN_VALUE);
    }
    

    @Override
    public Object getResult()
    {
        return globalBinaryRepresentationState;
    }

}
