package com.fciencias.evolutivo.basics;

public class HeatDistribution extends AbstractRandomDistribution{

    public HeatDistribution(double[] params) {

        super(params,10000,new double[]{0,100});
    }

    @Override
    public double densityFunction(double x) {
        
        return Math.exp(-x/params[0])/params[0];
    }

    @Override
    public double distributionFunction(double x) {

        return 1 - Math.exp(-x/params[0]);
    }
    
    public static void main(String[] args) {
        generateTestValues(new HeatDistribution(new double[]{2}));
    }
}
