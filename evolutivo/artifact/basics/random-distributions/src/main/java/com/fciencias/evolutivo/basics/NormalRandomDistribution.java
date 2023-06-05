package com.fciencias.evolutivo.basics;


public class NormalRandomDistribution extends AbstractRandomDistribution{


    public NormalRandomDistribution() {
        super(new double[]{0,1});
    }

    public NormalRandomDistribution(double[] params) {
        super(params);
    }

    @Override
    public double densityFunction(double x) {
        
        double c = Math.sqrt(2*Math.PI)*params[1];
        double ex = Math.exp(-Math.pow(x - params[0],2)/(2*Math.pow(params[1], 2)));
        return ex/c;
    }


        
    @Override
    public double distributionFunction(double x) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'distributionFunction'");
    }
    public static void main(String[] args) {
        generateTestValues(new NormalRandomDistribution(new double[]{3,0.5}));
    }

  
}
