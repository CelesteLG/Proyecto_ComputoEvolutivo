package com.fciencias.evolutivo.basics;

/**
 * Interface que se utiliza para obtener variables aleatorias
 */
public interface RandomDistribution 
{
    public double getRandomValue();
    public double[] getValuesTable();
    public double densityFunction(double x);
    public double distributionFunction(double x);
}
