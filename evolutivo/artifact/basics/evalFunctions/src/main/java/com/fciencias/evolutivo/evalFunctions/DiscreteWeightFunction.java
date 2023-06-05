package com.fciencias.evolutivo.evalFunctions;

public class DiscreteWeightFunction extends EvalUtils{

    private double[] weights;

    public DiscreteWeightFunction(double[] weights)
    {
        this.weights = weights;
    }

    @Override
    public double evalSoution(double[] param) {
        
        double sum = 0;
        for(int i = 0; i < param.length; i++)
        {
            if(i < weights.length)
                sum+= param[i]*weights[i];
            else
                break;
        }
        return sum;
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
        return("Discrete Weight Sum");
    }

    public double[] getWeights() {
        return weights;
    }

    public void setWeights(double[] weights) {
        this.weights = weights;
    }
    
}
