package com.fciencias.evolutivo.implementations.tarea4;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fciencias.evolutivo.basics.optimizator.NQuinsOptimizator;
import com.fciencias.evolutivo.binaryRepresentation.BinaryRepresentation;
import com.fciencias.evolutivo.evalFunctions.QueensFunction;
import com.fciencias.evolutivo.libraries.FileManager;
import com.fciencias.evolutivo.libraries.ParamsValidator;

/**
 * Hello world!
 *
 */
public class GeneticQueens 
{
    public static void main( String[] args )
    {
        ParamsValidator.validate(args);
        int totalThreads = ParamsValidator.getThreads();
        String fileInput = ParamsValidator.getFileIntput();
        int dimension = ParamsValidator.getDimension();
        int representationalBits = (int)Math.ceil(Math.log(dimension)/Math.log(2.0)); 
        int populationSize = ParamsValidator.getPopulationSize();
        int maxTime = ParamsValidator.getMaxTime();

        String fileOutput = ParamsValidator.getFileOutput();

        if(fileOutput.equals(""))
            fileOutput = fileInput;

        NQuinsOptimizator nQuinsOptimizator = new NQuinsOptimizator(new QueensFunction(), 100, representationalBits, dimension, populationSize, 0);
        nQuinsOptimizator.setMaxExecutionTime(maxTime*1000.0);
        nQuinsOptimizator.setTotalThreads(totalThreads);
        nQuinsOptimizator.resetGlobalParams();
        long time = nQuinsOptimizator.startMultiThreadOptimization(false, false);

        List<BinaryRepresentation> resultList = (List<BinaryRepresentation>)nQuinsOptimizator.getResult();
        long iterations = nQuinsOptimizator.getIterations();

        FileManager fileManager = new FileManager();
        long fileIndex = fileManager.openFile("outputs/tarea4/GeneticQueens/resultVectors_" + dimension + ".txt", false);
        long reportIndex = fileManager.openFile("outputs/tarea4/GeneticQueens/globalReport_" + dimension + ".txt", true);
        for(BinaryRepresentation result : resultList)
            fileManager.writeLine(fileIndex,result.printRealValue() + " , " + result.getBinaryString());

        fileManager.writeLine(reportIndex,dimension + "," + iterations + "," + resultList.size() + "," + populationSize + "," + time);

        fileManager.closeFile(fileIndex);
        fileManager.closeFile(reportIndex);
        System.out.println("Soluciones encontradas: " + resultList.size() + " en " + (Math.round(time*100.0)/100000.0) + " segundos");

    }
}
