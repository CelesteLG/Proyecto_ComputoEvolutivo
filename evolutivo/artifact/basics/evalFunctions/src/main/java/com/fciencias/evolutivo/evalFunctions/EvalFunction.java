package com.fciencias.evolutivo.evalFunctions;

import com.fciencias.evolutivo.binaryRepresentation.BinaryRepresentation;

public interface EvalFunction {

    public double evalSoution(double[] param);

    public double evalSoution(BinaryRepresentation binaryRepresentation);

    public double partialDerivative(double[] param, int n);

    public double[] gradientFuntion(double[] param);

    public String getFunctionName();
    
}
