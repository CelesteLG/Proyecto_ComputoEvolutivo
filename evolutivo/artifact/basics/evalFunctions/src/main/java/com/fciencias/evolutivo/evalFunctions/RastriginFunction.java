package com.fciencias.evolutivo.evalFunctions;

public class RastriginFunction extends EvalUtils{

    @Override
    public double evalSoution(double[] param) {
        
        return 10*param.length + powersSum(param, 2) - trigoSum(param, 10, 0, 1, 0);
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
            gradient[i++] = 2*pi + 20*Math.PI*Math.sin(2*Math.PI*pi); 
            
        return gradient;
    }

    
    @Override
    public String getFunctionName() {
        
        return "Rastrigin Function";
    }
    
}
