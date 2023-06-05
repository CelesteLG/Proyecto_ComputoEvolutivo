package com.fciencias.evolutivo.basics;

import com.fciencias.evolutivo.libraries.FileManager;

public class DiscreteDistribution extends AbstractRandomDistribution{

    private double[] distribution;

    public DiscreteDistribution(double[] p)
    {
        super();
        setParams(new double[]{0,1});
        this.interval = new double[]{p[0],p[p.length -1]};
        distribution = p;
        this.tableRows = p.length*100;
        this.steps = tableRows;
        fillTable();
    }

    @Override
    protected void fillTable()
    {
        valuesTable = new double[this.tableRows];
        double delta = (interval[1] - interval[0])/steps;
        int cumulator = 0;
        for(int i = 0; i < this.tableRows; i ++)
        {
            double x = interval[0] + i*delta;
            if(x > distribution[cumulator])
                cumulator++;

            valuesTable[i] = cumulator;
        }
    }

    @Override
    public double densityFunction(double x) {
        
        throw new UnsupportedOperationException("Unimplemented method 'distributionFunction'");
    }

    @Override
    public double distributionFunction(double x) {
        
        if(x < interval[0] || x > interval[1])
            return -1;

        return distribution[(int)Math.floor(x)];
    }

    public static void main(String[] args) {
        
        double[] p = new double[]{
            0.0714,
            0.0,
            0.0325,
            0.0714, 
            0.0130 ,
            0.0130 ,
            0.0455 ,
            0.0390 ,
            0.0909 ,
            0.0519 ,
            0.0714 ,
            0.0974 ,
            0.0844 ,
            0.0195 ,
            0.0325 ,
            0.0390 ,
            0.0325 ,
            0.0909 ,
            0.0325 ,
            0.0714 ,
           
        };

        double[] cp = new double[p.length];

        cp[0] = p[0];
        for(int i = 1; i < p.length; i++)
        {
            cp[i] = cp[i - 1] + p[i];
        }
        RandomDistribution randomDistribution = new DiscreteDistribution(cp);

        final int n = 1000000;

        FileManager fileManager = new FileManager();

        long fileIndex = fileManager.openFile("Inversa.txt", false);
        long muestra = fileManager.openFile("generacionAleatoria.txt", false);

        for(double row : randomDistribution.getValuesTable())
        {
            fileManager.writeLine(fileIndex,row + "");
        }

        for(int i = 0; i < n; i++)
        {
            fileManager.writeLine(muestra,randomDistribution.getRandomValue() + "");
        }
    }
    
}
