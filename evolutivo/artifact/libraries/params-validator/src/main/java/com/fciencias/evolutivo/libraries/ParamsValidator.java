package com.fciencias.evolutivo.libraries;

import java.security.InvalidParameterException;

/**
 *  Validacion y asignacion de parametros
 *
 */
public class ParamsValidator 
{
    private ParamsValidator(){}
    
    static int dimension = 3;
    static int representationalBits = 64;
    static long iterations = 100;
    static long heuristicIterations = 100;
    static int threads = 25;
    static String fileIntput= "";
    static String fileOutput= "";
    static int populationSize = 10;
    static int maxTime = 10;
    

    public static int getDimension() {
        return dimension;
    }

    public static int getRepresentationalBits() {
        return representationalBits;
    }

    public static long getIterations() {
        return iterations;
    }

    public static long getHeuristicIterations() {
        return heuristicIterations;
    }

    public static String getFileIntput() {
        return fileIntput;
    }

    public static String getFileOutput() {
        return fileOutput;
    }

    public static int getThreads() {
        return threads;
    }

    public static int getPopulationSize() {
        return populationSize;
    }

    public static int getMaxTime() {
        return maxTime;
    }
    

    public static void validate(String[] params)
    {
        for(int i = 0; i < params.length; i++)
        {
            if(params[i].equals("-d"))
            {
                dimension = Integer.parseInt(params[i+1]);
                i++;
            }
            else if(params[i].equals("-b"))
            {
                representationalBits = Integer.parseInt(params[i+1]);
                i++;
            }
            else if(params[i].equals("-i"))
            {
                iterations = Integer.parseInt(params[i+1]);
                i++;
            }
            else if(params[i].equals("-ih"))
            {
                heuristicIterations = Integer.parseInt(params[i+1]);
                i++;
            }
            else if(params[i].equals("-f"))
            {
                fileIntput = params[i+1];
                i++;
            }
            else if(params[i].equals("-fo"))
            {
                fileOutput = params[i+1];
                i++;
            }
            else if(params[i].equals("-t"))
            {
                threads = Integer.parseInt(params[i+1]);
                i++;
            }
            else if(params[i].equals("-p"))
            {
                populationSize = Integer.parseInt(params[i+1]);
                i++;
            }
            else if(params[i].equals("-tm"))
            {
                maxTime = Integer.parseInt(params[i+1]);
                i++;
            }
            else
            {
                StringBuilder exceptionMessage = new StringBuilder("\nNo se reconoce el comando '")
                .append(params[i])
                .append("' \n")
                .append("Los comandos validos son los siguientes: \n")
                .append("\t-d: Dimension del espacio de busqueda\n")
                .append("\t-b: Logitud de bits de representacion binaria\n")
                .append("\t-i: Numero de iteraciones de la busqueda\n")
                .append("\t-ih: Numero de iteraciones de la metaheuristica\n")
                .append("\t-t: Numero de hilos\n")
                .append("\t-f: Archivo de entrada\n")
                .append("\t-fo: Archivo de salida\n")
                .append("\t-p: Tamanio de poblacion (Geneticos)\n")
                .append("\t-tm: Tiempo maximo de ejecucion en segundos(Geneticos)\n");
                throw new InvalidParameterException(exceptionMessage.toString());
            }
                
        }
    }

    

}
