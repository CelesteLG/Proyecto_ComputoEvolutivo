package com.fciencias.evolutivo.evalFunctions;

import com.fciencias.evolutivo.binaryRepresentation.BinaryRepresentation;

public class IdentityEvaluation extends EvalUtils{

    @Override
    public double evalSoution(double[] param) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'evalSoution'");
    }

    @Override
    public double evalSoution(BinaryRepresentation binaryRepresentation)
    {
        return binaryRepresentation.getEvaluation();
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getFunctionName'");
    }
    
}
