package com.fciencias.evolutivo.evalFunctions;

public class QueensFunction extends EvalUtils{

    @Override
    public double evalSoution(double[] param) {
        
        double x1,y1,x2,y2;
        double attackQueens = 0;
        for(int i = 0; i < param.length; i ++)
        {
            x1 = i;
            y1 = param[i];
            for(int j = (i + 1); j < param.length; j ++)
            {
                x2 = j;
                y2 = param[j];
                if(Math.abs(x1 - x2) == Math.abs(y1 - y2) || (x1 == x2) || (y1 == y2))
                    attackQueens++;
            }
        }
        return -attackQueens;
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
        return "Queens Function";
    }
    
}
