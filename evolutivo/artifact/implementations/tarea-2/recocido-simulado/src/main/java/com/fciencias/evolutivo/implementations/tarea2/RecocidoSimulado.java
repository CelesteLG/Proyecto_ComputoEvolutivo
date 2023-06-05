package com.fciencias.evolutivo.implementations.tarea2;


import java.util.HashMap;
import java.util.Map;

import com.fciencias.evolutivo.basics.HeatDistribution;
import com.fciencias.evolutivo.basics.RandomDistribution;
import com.fciencias.evolutivo.basics.optimizator.AbstractOptimizator;
import com.fciencias.evolutivo.binaryRepresentation.BinaryDiscreteState;
import com.fciencias.evolutivo.binaryRepresentation.BinaryRepresentation;
import com.fciencias.evolutivo.evalFunctions.DiscreteWeightFunction;
import com.fciencias.evolutivo.evalFunctions.EvalFunction;
import com.fciencias.evolutivo.libraries.FileManager;
import com.fciencias.evolutivo.libraries.ParamsValidator;


public class RecocidoSimulado extends KnapsackSearch
{

    private static final double INIT_TEMP = 50.0;

    private static final double FINISH_TEMP = 0.00005;

    private static final String TEMPERATURA = "TEMP";

    protected double temp;

    private double alpha;

    protected int totalIterations;

    public RecocidoSimulado(EvalFunction evalFunction, long iterations, int representationalBits, int dimension, Map<String, Object> globalParams, int hilo) {
        
        super(evalFunction, iterations, representationalBits, dimension, hilo);
        temp = INIT_TEMP;
        alpha = Math.pow(FINISH_TEMP/INIT_TEMP, 2.0/(representationalBits*iterations));
        totalIterations = 0;
    }

    public RecocidoSimulado(EvalFunction evalFunction, long iterations, int representationalBits, int dimension, Map<String, Object> globalParams, BinaryRepresentation globalBinaryRepresentationState, int hilo) {
        
        super(evalFunction, iterations, representationalBits, dimension, globalBinaryRepresentationState, hilo);
        temp = INIT_TEMP;
        alpha = Math.pow(FINISH_TEMP/INIT_TEMP, 2.0/(representationalBits*iterations));
        totalIterations = 0;
    }

    @Override
    public void initOptimizator() {
        
        BinaryDiscreteState binaryDiscreteState = new BinaryDiscreteState(representationalBits);
        globalBinaryRepresentationState = binaryDiscreteState;
        bestValue = evalFunction.evalSoution(globalBinaryRepresentationState.getRealValue());
    }
    
    @Override
    protected boolean brakCondition()
    {
        return ((double)globalParams.get(TEMPERATURA)) < FINISH_TEMP ;
    }

    @Override
    public boolean isMoreOptimunState(BinaryRepresentation state) {
        
        double valuation = evalFunction.evalSoution(state.getRealValue());
        boolean isMoreOptimun = false;
        RandomDistribution heatProbability = new HeatDistribution(new double[]{temp});
        double p = heatProbability.getRandomValue();
        long fileIndex = fileManager.openFile("outputs/Recocido/" + fileInput + "_JumpUpState.txt", true);
        long fileIndexOptimLog = fileManager.openFile("outputs/Recocido/" + fileInput + "_ImproveState.txt", true);
        if(valuation < maxCost)
        {
            double combinationGain = gainCalculator.evalSoution(state.getRealValue());
                
            if(combinationGain > (double)globalParams.get(MAXIMUN_VALUE))
            {
                globalParams.replace(MAXIMUN_VALUE, combinationGain);
                bestValue = combinationGain;
                isMoreOptimun = true;
                
                maximumValue = combinationGain;
                fileManager.writeLine(fileIndexOptimLog,globalParams.get(TEMPERATURA) + ", " + globalBinaryRepresentationState + " -> " + state);

            }
            else
            {
                if(p > ((double)globalParams.get(MAXIMUN_VALUE) - combinationGain))
                {
                    globalParams.replace(MAXIMUN_VALUE, combinationGain);
                    bestValue = combinationGain;
                    isMoreOptimun = true;
                    
                    maximumValue = combinationGain;
                    fileManager.writeLine(fileIndex,globalParams.get(TEMPERATURA) + ", " + globalBinaryRepresentationState + " -> " + state);
                }
            }
        }
        globalParams.replace(TEMPERATURA, (double)globalParams.get(TEMPERATURA)*alpha);
        temp = temp*alpha;
        return isMoreOptimun;
    }

    @Override
    public AbstractOptimizator createOptimizator(int hilo, boolean logTrack) {
        
        RecocidoSimulado recocidoSimulado = new RecocidoSimulado(evalFunction, iterations, representationalBits, dimension, globalParams, globalBinaryRepresentationState, hilo);
        recocidoSimulado.globalParams.replace(MINIMUN_VALUE, bestValue);
        recocidoSimulado.globalParams.replace(TEMPERATURA, temp);
        recocidoSimulado.setLogTrack(logTrack);
        recocidoSimulado.setMaxCost(maxCost);
        recocidoSimulado.setGainCalculator(gainCalculator);
        recocidoSimulado.setFileInput(fileInput);
        recocidoSimulado.setTotalThreads(totalThreads);
        recocidoSimulado.resetGlobalParams();
        return recocidoSimulado;
    }

    public static void main( String[] args )
    {

        ParamsValidator.validate(args);
        long iterations = ParamsValidator.getIterations();
        String fileInput = ParamsValidator.getFileIntput();
        int totalThreads = ParamsValidator.getThreads();
        FileManager fileManager = new FileManager();
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



        Map<String,Object> globalParams = new HashMap<>();
        globalParams.put(TEMPERATURA, INIT_TEMP);
        RecocidoSimulado recocidoSimulado = new RecocidoSimulado(new DiscreteWeightFunction(w), iterations, touples, touples, globalParams, 0);
        String initialState = recocidoSimulado.globalBinaryRepresentationState.getBinaryString();
        recocidoSimulado.setMaxCost(maxCost);
        recocidoSimulado.setGainCalculator(new DiscreteWeightFunction(p));
        recocidoSimulado.setFileInput(fileInput);
        recocidoSimulado.setTotalThreads(totalThreads);
        long jumpStateIndex = fileManager.openFile("outputs/Recocido/" + fileInput + "_JumpUpState.txt", false);
        fileManager.writeLine(jumpStateIndex,"Registro de salto entre estados");
        long deltaTime = recocidoSimulado.startMultiThreadOptimization(false, true);
        fileIndex = fileManager.openFile("outputs/Recocido/" + fileInput + "_Results.txt", true);
        fileManager.writeLine(fileIndex,"Parametros de ejecucion: ");
        fileManager.writeLine(fileIndex, "\tArchivo de entrada:  " + fileInput);
        fileManager.writeLine(fileIndex, "\tIteraciones:  " + globalParams.get(TOTAL_ITERATIONS));
        fileManager.writeLine(fileIndex, "\tBits de representacion:  " + touples);
        fileManager.writeLine(fileIndex, "\tDimension:  " + touples);
        fileManager.writeLine(fileIndex, "\nEstado Inicial: " + initialState);
        fileManager.writeLine(fileIndex, "Resultado Optimo: " + globalParams.get(OPTIMUM_OBJECT));
        fileManager.writeLine(fileIndex, "Ganancia: " + globalParams.get(MAXIMUN_VALUE));
        fileManager.writeLine(fileIndex, "Costo: " + new DiscreteWeightFunction(w).evalSoution(((BinaryRepresentation)globalParams.get(OPTIMUM_OBJECT)).getRealValue()));
        fileManager.writeLine(fileIndex, "\nTiempo de ejecucion: " + deltaTime/1000.0 + "s");
        
        
    }

    public static void ejecutar(String fileInput, int iterations, int totalThreads)
    {
        FileManager fileManager = new FileManager();
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
        System.out.println("Costo maximo: " + maxCost);


        Map<String,Object> globalParams = new HashMap<>();
        globalParams.put(TEMPERATURA, INIT_TEMP);
        RecocidoSimulado recocidoSimulado = new RecocidoSimulado(new DiscreteWeightFunction(w), iterations, touples, touples, globalParams, 0);
        String initialState = recocidoSimulado.globalBinaryRepresentationState.getBinaryString();
        recocidoSimulado.setMaxCost(maxCost);
        recocidoSimulado.setGainCalculator(new DiscreteWeightFunction(p));
        recocidoSimulado.setFileInput(fileInput);
        recocidoSimulado.setTotalThreads(totalThreads);
        long jumpStateIndex = fileManager.openFile("outputs/Recocido/" + fileInput + "_JumpUpState.txt", false);
        fileManager.writeLine(jumpStateIndex,"Registro de salto entre estados");
        long deltaTime = recocidoSimulado.startMultiThreadOptimization(false, true);
        fileIndex = fileManager.openFile("outputs/Recocido/" + fileInput + "_Results.txt", true);
        fileManager.writeLine(fileIndex,"Parametros de ejecucion: ");
        fileManager.writeLine(fileIndex, "\tArchivo de entrada:  " + fileInput);
        fileManager.writeLine(fileIndex, "\tIteraciones:  " + globalParams.get(TOTAL_ITERATIONS));
        fileManager.writeLine(fileIndex, "\tBits de representacion:  " + touples);
        fileManager.writeLine(fileIndex, "\tDimension:  " + touples);
        fileManager.writeLine(fileIndex, "\nEstado Inicial: " + initialState);
        fileManager.writeLine(fileIndex, "Resultado Optimo: " + globalParams.get(OPTIMUM_OBJECT));
        fileManager.writeLine(fileIndex, "Ganancia: " + globalParams.get(MAXIMUN_VALUE));
        fileManager.writeLine(fileIndex, "Costo: " + new DiscreteWeightFunction(w).evalSoution(((BinaryRepresentation)globalParams.get(OPTIMUM_OBJECT)).getRealValue()));
        fileManager.writeLine(fileIndex, "\nTiempo de ejecucion: " + deltaTime/1000.0 + "s");
    }

    public static void main2(String[] args) {
        
        String[] argumentos = {"eje1n1000.txt","eje2n1000.txt","ejeknapPI_1_50_1000000_14.txt","ejeknapPI_3_200_1000_14.txt","ejeknapPI_11_20_1000_100.txt","ejeknapPI_13_100_1000_18.txt","ejeL10n20.txt","ejeL14n45.txt"};
        int[] iteraciones = {1000,1000,100000,1000,100,1000,20,45};
        for(int j = 0; j < 10; j++)
        {
            for(int i = 0; i< iteraciones.length; i++)
            {
                System.out.println("Ejecutando " + argumentos[i] + " con " + iteraciones[i] + " iteraciones");
                ejecutar(argumentos[i],5000,20);
                
            }
        }
        

    }
    
}
