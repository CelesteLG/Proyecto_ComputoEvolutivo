package com.fciencias.evolutivo.evalFunctions;

public class GriewankFunction extends EvalUtils{

    @Override
    public double evalSoution(double[] param) {
        
        return (1 + powersSum(param,2)/4000 - cosProduct(param));
    }

    @Override
    public double partialDerivative(double[] param, int n) {
        if(n <= param.length)
            return gradientFuntion(param)[n];
        
        return 0;
    }

    @Override
    public double[] gradientFuntion(double[] param) {
        double[] gradient = new double[param.length];
        int i = 0;
        for(double pi : param)
            gradient[i++] = pi/2000 - cosDerivativeProduct(param,i); 
            
        return gradient;
    }

    private double cosProduct(double[] x)
    {
        double product = 1;
        for(int i = 0; i < x.length; i++)
            product *= Math.cos(x[i]/Math.sqrt(i+1));


        return product;
    }

    private double cosDerivativeProduct(double[] x, int i)
    {
        double product = 1;
        for(int j = 0; j < x.length; j++)
        {
            if(i!= j)
                product *= Math.cos(x[j]/Math.sqrt(j));
            else
                product *= -Math.sin(x[j]/Math.sqrt(j))/Math.sqrt(j);
        }
            

        return product;
    }
    
    
    @Override
    public String getFunctionName() {
        
        return "Griewank Function";
    }
}
