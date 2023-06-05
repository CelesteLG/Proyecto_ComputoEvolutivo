package com.fciencias.evolutivo.evalFunctions;

public class AckleyFunction extends EvalUtils {

    @Override
    public double evalSoution(double[] param) {
        
        int n = param.length;
        double pSum = powersSum(param, 2) / n;
        double cSum = trigoSum(param, 1, 0, 1, 0) / n;
        return 20 + Math.E - 20 * Math.exp(-0.2 * Math.sqrt(pSum)) - Math.exp(cSum);
    }

    @Override
    public double partialDerivative(double[] param,int n) {
        
        return 0;
    }

    @Override
    public double[] gradientFuntion(double[] param) {

        double[] gradient = new double[param.length];
        int i = 0;
        for(double pi : param)
            gradient[i++] = pi; 
            
        return gradient;
    }

    @Override
    public String getFunctionName() {
        
        return "Ackley Function";
    }
    
}
