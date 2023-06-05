package com.fciencias.evolutivo.implementations.tarea2;

import java.util.HashMap;
import java.util.Map;

import com.fciencias.evolutivo.basics.optimizator.AbstractOptimizator;
import com.fciencias.evolutivo.binaryRepresentation.*;
import com.fciencias.evolutivo.evalFunctions.*;
import com.fciencias.evolutivo.libraries.FileManager;
import com.fciencias.evolutivo.libraries.ParamsValidator;
/**
 * Hello world!
 *
 */
public class KnapsackSearch extends AbstractOptimizator
{
    protected double maxCost;
    protected EvalFunction gainCalculator;
    protected String fileInput;

    public void setFileInput(String fileInput)
    {
        this.fileInput = fileInput;
    }

    public KnapsackSearch(EvalFunction evalFunction,long iterations,int representationalBits, int dimension, int hilo)
    {
        super(evalFunction, iterations, representationalBits, dimension,hilo);
    }

    public KnapsackSearch(EvalFunction evalFunction,long iterations,int representationalBits, int dimension, BinaryRepresentation globalBinaryRepresentationState, int hilo)
    {
        super(evalFunction, iterations, representationalBits, dimension,globalBinaryRepresentationState,hilo);
    }

    public void setMaxCost(double maxCost)
    {
        this.maxCost = maxCost;
    }

    public void setGainCalculator(EvalFunction gainCalculator) {
        this.gainCalculator = gainCalculator;
    }

    @Override
    public void initOptimizator() {
        
        BinaryDiscreteState binaryDiscreteState = new BinaryDiscreteState(representationalBits);
        globalBinaryRepresentationState = binaryDiscreteState.getRandomState(representationalBits/2, binaryDiscreteState.getRealValue());
        bestValue = evalFunction.evalSoution(globalBinaryRepresentationState.getRealValue());
    }


    @Override
    public BinaryRepresentation[] getNewStates() {

        return globalBinaryRepresentationState.getNeighborhoods(1, representationalBits); 

    }

    
    @Override
    protected BinaryRepresentation cheapSolution() {
        return new BinaryDiscreteState(representationalBits);
    }

    @Override
    public boolean isMoreOptimunState(BinaryRepresentation state) {
        
        double valuation = evalFunction.evalSoution(state.getRealValue());
        boolean isMoreOptimun = false;
        if(valuation < maxCost)
        {
            double combinationGain = gainCalculator.evalSoution(state.getRealValue());
            if(combinationGain > (double)globalParams.get(MAXIMUN_VALUE))
            {
                globalParams.replace(MAXIMUN_VALUE, combinationGain);
                bestValue = combinationGain;
                isMoreOptimun = true;
                
                maximumValue = combinationGain;

            }
        }
        return isMoreOptimun;
    }

    @Override
    public AbstractOptimizator createOptimizator(int hilo, boolean logTrack) {
        
        KnapsackSearch knapsackSearch = new KnapsackSearch(evalFunction, iterations, representationalBits, dimension, hilo);
        knapsackSearch.setLogTrack(logTrack);
        knapsackSearch.setMaxCost(maxCost);
        knapsackSearch.setGainCalculator(gainCalculator);
        knapsackSearch.setGlobalBinaryRepresentationState(globalBinaryRepresentationState);
        knapsackSearch.setFileInput(fileInput);
        knapsackSearch.setTotalThreads(totalThreads);
        knapsackSearch.resetGlobalParams();
        knapsackSearch.globalParams.replace(MINIMUN_VALUE, bestValue);
        return knapsackSearch;
    }

    public static void main( String[] args )
    {
        FileManager fileManager = new FileManager();
        ParamsValidator.validate(args);
        String fileInput = ParamsValidator.getFileIntput();
        long fileIndex = fileManager.openFile("inputs/" + fileInput, true);
        int touples = Integer.parseInt(fileManager.readFileLine(fileIndex, 0));
        double[] w = new double[touples];
        double[] p = new double[touples];
        

        for(int i = 0; i < touples; i++)
        {
            String[] toupleValues = fileManager.readFileLine(fileIndex, i+1).split(" ");
            p[i] = Double.parseDouble(toupleValues[1]);
            w[i] = Double.parseDouble(toupleValues[2]);
            
        }
       
        double maxCost = Double.parseDouble(fileManager.readFileLine(fileIndex, touples + 1));

        long iterations = ParamsValidator.getIterations();
        int totalThreads = ParamsValidator.getThreads();
        System.out.println("Parametros de ejecucion: ");
        System.out.println("\tIteraciones:  " + iterations);
        Map<String,Object> globalParams = new HashMap<>();
        KnapsackSearch knapsackSearch = new KnapsackSearch(new DiscreteWeightFunction(w), iterations, touples, touples, 0);
        knapsackSearch.setMaxCost(maxCost);
        knapsackSearch.setGainCalculator(new DiscreteWeightFunction(p));
        knapsackSearch.setTotalThreads(totalThreads);
        String initialState = knapsackSearch.getGlobalBinaryRepresentationState().getBinaryString();
        long deltaTime = knapsackSearch.startMultiThreadOptimization(false, true);

        fileIndex = fileManager.openFile("outputs/Knapsack/" + fileInput + "_Results.txt", true);
        fileManager.writeLine(fileIndex,"Parametros de ejecucion: ");
        fileManager.writeLine(fileIndex, "\tArchivo de entrada:  " + fileInput);
        fileManager.writeLine(fileIndex, "\tIteraciones:  " + globalParams.get(TOTAL_ITERATIONS));
        fileManager.writeLine(fileIndex, "\tBits de representacion:  " + touples);
        fileManager.writeLine(fileIndex, "\tDimension:  " + touples);
        fileManager.writeLine(fileIndex, "\nEstado Inicial: " + initialState);
        fileManager.writeLine(fileIndex, "Resultado Optimo: " + globalParams.get(OPTIMUM_OBJECT));
        fileManager.writeLine(fileIndex, "Ganancia: " + globalParams.get(MAXIMUN_VALUE));
        if(globalParams.get(OPTIMUM_OBJECT) != null)
            fileManager.writeLine(fileIndex, "Costo: " + new DiscreteWeightFunction(w).evalSoution(((BinaryRepresentation)globalParams.get(OPTIMUM_OBJECT)).getRealValue()));
        else
            fileManager.writeLine(fileIndex, "Costo: NO EVALUADO");
        fileManager.writeLine(fileIndex, "\nTiempo de ejecucion: " + deltaTime/1000.0 + "s\n\n");
        fileManager.closeFile(fileIndex);
    }

    @Override
    public boolean isValidState(BinaryRepresentation state) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isValidState'");
    }

}
