package com.fciencias.evolutivo.implementations.tarea3;

import java.util.HashMap;
import java.util.Map;

import com.fciencias.evolutivo.basics.optimizator.KnapSackSimulatedAnnealingOptimizator;
import com.fciencias.evolutivo.evalFunctions.DiscreteWeightFunction;
import com.fciencias.evolutivo.libraries.FileManager;
import com.fciencias.evolutivo.libraries.ParamsValidator;

/**
 * Hello world!
 *
 */
public class App
{

    public static void main( String[] args )
    {
        ParamsValidator.validate(args);
        long iterations = ParamsValidator.getIterations();
        int totalThreads = ParamsValidator.getThreads();
        String fileInput = ParamsValidator.getFileIntput();
        String fileOutput = ParamsValidator.getFileOutput();
        if(fileOutput.equals(""))
            fileOutput = fileInput;

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
        System.out.println("Parametros de ejecucion: ");
        System.out.println("\tIteraciones:  " + iterations);
        
        double maxCost = Double.parseDouble(fileManager.readFileLine(fileIndex, touples + 1));
        System.out.println("Costo maximo: " + maxCost);



        Map<String,Object> globalParams = new HashMap<>();
        globalParams.put("TEMP", 50.0);
        KnapSackSimulatedAnnealingOptimizator recocidoSimulado = new KnapSackSimulatedAnnealingOptimizator(new DiscreteWeightFunction(p), iterations, touples, touples, 0);
        recocidoSimulado.setWeightCalculator(new DiscreteWeightFunction(w));
        recocidoSimulado.setMaxCost(maxCost);
        recocidoSimulado.setTotalThreads(totalThreads);
        recocidoSimulado.setGlobalParams(globalParams);
        recocidoSimulado.setOutputPath("outputs/tarea3/" + fileOutput);
        recocidoSimulado.resetGlobalParams();
        String initialState = recocidoSimulado.getGlobalBinaryRepresentationState().getBinaryString();
        long deltaTime = recocidoSimulado.startMultiThreadOptimization(false, true);
        fileIndex = fileManager.openFile("outputs/tarea3/" + fileOutput + "_Results.txt", true);
        fileManager.writeLine(fileIndex,"Parametros de ejecucion: ");
        fileManager.writeLine(fileIndex, "\tArchivo de entrada: " + fileInput);
        fileManager.writeLine(fileIndex, "\tIteraciones:  " + globalParams.get("Total iterations"));
        fileManager.writeLine(fileIndex, "\tBits de representacion:  " + touples);
        fileManager.writeLine(fileIndex, "\tDimension:  " + touples);
        fileManager.writeLine(fileIndex, "\nEstado Inicial: " + initialState);
        fileManager.writeLine(fileIndex, "Resultado Optimo: " + recocidoSimulado.getOptimumState());
        fileManager.writeLine(fileIndex, "Ganancia: " + recocidoSimulado.getOptimumValuation());
        fileManager.writeLine(fileIndex, "Costo: " + new DiscreteWeightFunction(w).evalSoution(recocidoSimulado.getOptimumState().getRealValue()));
        fileManager.writeLine(fileIndex, "\nTiempo de ejecucion: " + deltaTime/1000.0 + "s");

        fileIndex = fileManager.openFile("outputs/tarea3/" + fileOutput + "_table_Results.txt", true);
        StringBuilder stringBuilder = new StringBuilder();
        String separatorChar = ",";
        stringBuilder.append(globalParams.get("Total iterations")).append(separatorChar)
        .append(totalThreads).append(separatorChar)
        .append(recocidoSimulado.getOptimumValuation()).append(separatorChar)
        .append(deltaTime/1000.0);

        fileManager.writeLine(fileIndex, stringBuilder.toString());
    }
}
