package com.fciencias.evolutivo.evalFunctions;

public class SphereFunction extends EvalUtils{

    @Override
    public double evalSoution(double[] param) {
        return powersSum(param, 2);
    }

    @Override
    public double partialDerivative(double[] param, int n) {
        if(n <= param.length)
            return 2*param[n];

        else 
            return 0;
    }

    @Override
    public double[] gradientFuntion(double[] param) {
        
        double[] gradient = new double[param.length];
        int i = 0;
        for(double pi : param)
            gradient[i++] = 2*pi; 
            
        return gradient;
    }

    
    @Override
    public String getFunctionName() {
        
        return "Sphere Function";
    }
    
}
