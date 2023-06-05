package com.fciencias.evolutivo.evalFunctions;

import com.fciencias.evolutivo.libraries.FileManager;

public class ExternalValuationFunction extends EvalUtils {

    private String externalSource;
    private FileManager fileManager;
    private double defaultVal;

    
    
    public ExternalValuationFunction(String externalSource, double defaultVal) {
        this.externalSource = externalSource;
        this.defaultVal = defaultVal;
        fileManager = new FileManager();
    }

    @Override
    public double evalSoution(double[] param) {
        
        double evaluationValue = defaultVal;
        long fileIndex = fileManager.openFile(externalSource, true);
        int recordsSize = Integer.parseInt(fileManager.readFileLine(fileIndex, 0).split(":")[1]);
        for(int i=0; i < recordsSize;i++)
        {
            String[] recordValues = fileManager.readFileLine(fileIndex, i+1).split(",");
            double[] values = new double[recordValues.length];
            boolean targetEvaluation = true;
            for(int j = 0; j < recordValues.length; j++)
            {
                values[j] = Double.parseDouble(recordValues[j]);
                double factor = Math.pow(10, 4);
                if(j < (recordValues.length -1) &&  Math.abs(values[j] - (Math.round(param[j] * factor) / factor))>0.0001)
                {
                    targetEvaluation = false;
                    break;
                }
            }
            if(targetEvaluation)
            {
                evaluationValue = values[recordValues.length -1];
                
                fileManager.closeFile(fileIndex);
                return evaluationValue;
            }

        }
        
        fileManager.closeFile(fileIndex);
        return evaluationValue;
    }

    @Override
    public double partialDerivative(double[] param, int n) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'partialDerivative'");
    }

    @Override
    public double[] gradientFuntion(double[] param) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'gradientFuntion'");
    }

    @Override
    public String getFunctionName() {
        return "External evaluation function";
    }
    
}
