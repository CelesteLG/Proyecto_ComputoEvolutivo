package com.fciencias.evolutivo.evalFunctions;

public class ThenthPowerFunction extends EvalUtils{

    @Override
    public double evalSoution(double[] param) {
        
        return powersSum(param, 10);
    }

    @Override
    public double partialDerivative(double[] param, int n) {
        
        if(n < param.length)
            return gradientFuntion(param)[n];
        else
            return 0;
    }

    @Override
    public double[] gradientFuntion(double[] param) {

        double[] gradient = new double[param.length];
        int i = 0;
        for(double pi : param)
            gradient[i++] = 10*Math.pow(pi,9); 
            
        return gradient;
    }

    
    @Override
    public String getFunctionName() {
        
        return "Thenth Power Function";
    }
    
}
