package com.fciencias.evolutivo.basics;

import com.fciencias.evolutivo.libraries.FileManager;

public abstract class AbstractRandomDistribution implements RandomDistribution {
    
    protected double[] interval;
    protected int steps;
    protected int tableRows;
    protected double[] valuesTable;
    protected double[] params;

    public void setParams(double[] params)
    {
        this.params = params;
    }

    protected AbstractRandomDistribution()
    {}

    protected AbstractRandomDistribution(double[] params)
    {
        tableRows = 10000;
        steps = 5*tableRows;
        valuesTable = new double[tableRows];
        interval = new double[]{params[0] - 3*params[1],params[0] + 3*params[1]};
        this.params = params;
        fillTable();
    }

    protected AbstractRandomDistribution(double[] params, int tableRows)
    {
        this.tableRows = tableRows;
        this.steps = 5*tableRows;
        this.valuesTable = new double[tableRows];
        this.interval = new double[]{params[0] - 3*params[1],params[0] + 3*params[1]};
        this.params = params;
        fillTable();
    }


    protected AbstractRandomDistribution(double[] params, int tableRows, double[] interval)
    {
        this.tableRows = tableRows;
        this.steps = 5*tableRows;
        this.valuesTable = new double[tableRows];
        this.interval = interval;
        this.params = params;
        fillTable();
    }

    @Override
    public double getRandomValue() {
        
        double ux = Math.random();
        return inverseDistribution(ux);
    }

    protected double inverseDistribution(double p) {
        
        int row = (int)(p*tableRows);
        return valuesTable[row];

    }

    public int getTablesRows()
    {
        return this.tableRows;
    }

    public double[] getValuesTable()
    {
        return this.valuesTable;
    }
    
    protected void fillTable() {
        
        double acumulator = 0;
        double delta = (interval[1] - interval[0])/steps;
        int i = 0;
        
        double x = 0;
        valuesTable[0] = interval[0];
        for(int j = 1; j < tableRows; j++)
        {
            while(acumulator < ((double)j)/((double)tableRows) && i < steps)
            {
                x = interval[0] + i*delta;
                acumulator += densityFunction(x)*delta;
                i++;
            }
            valuesTable[j] = x;
        } 
    }

    public static void generateTestValues(RandomDistribution randomDistribution)
    {
        FileManager fileManager = new FileManager();
        long fileIndex = fileManager.openFile("Inversa.txt", false);
        long muestra = fileManager.openFile("generacionAleatoria.txt", false);

        for(double row : randomDistribution.getValuesTable())
        {
            fileManager.writeLine(fileIndex,row + "");
        }

        for(int i = 0; i < 10000; i++)
        {
            fileManager.writeLine(muestra,randomDistribution.getRandomValue() + "");
        }
    }
}
