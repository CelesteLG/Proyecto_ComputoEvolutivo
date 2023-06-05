package com.fciencias.evolutivo.evalFunctions;

public class RosenbrockFunction extends EvalUtils{

    @Override
    public double evalSoution(double[] param) {
        
        double sum = 0;
        for(int i = 0; i < param.length - 1; i++)

            sum += 100*Math.pow(param[i+1] - param[i]*param[i],2) + Math.pow(1 - param[i], 2);

        return sum;
    }

    @Override
    public double partialDerivative(double[] param, int n) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public double[] gradientFuntion(double[] param) {
        // TODO Auto-generated method stub
        return new double[]{0.0};
    }

    
    @Override
    public String getFunctionName() {
        
        return "Rosenbrock Function";
    }
    
}
