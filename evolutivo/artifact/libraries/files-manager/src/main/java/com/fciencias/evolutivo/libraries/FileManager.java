package com.fciencias.evolutivo.libraries;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class FileManager {

    private Map<String,BufferedOutputStream> filesOutputBuffer;
    private Map<Long,String> filesIndex;
    private long currentKey = 0;

    public FileManager()
    {
        filesIndex = new HashMap<>();
        filesOutputBuffer = new HashMap<>();
    }

    public Long openFile(String file, boolean append)
    {
        if(filesIndex.containsValue(file))
        {
            for(Map.Entry<Long,String> fileTuple : filesIndex.entrySet())
            {
                if(fileTuple.getValue().equals(file))
                    return fileTuple.getKey();
            }
        }
        else
        {
            try 
            {
                BufferedOutputStream newBufferedOutStream = new BufferedOutputStream(new FileOutputStream(file,append));
                filesOutputBuffer.put(file, newBufferedOutStream);
                filesIndex.put(currentKey, file);
                currentKey++;
                return currentKey - 1;

            }
            catch (FileNotFoundException e) 
            {
                e.printStackTrace();
            } 
           
        }
        return -1L;
    }

    public void closeFile(long index)
    {
        try 
        {
            filesOutputBuffer.get(filesIndex.get(index)).close();
            filesIndex.remove(index);
        } 
        catch (IOException e) 
        {    
            e.printStackTrace();
        }
    }

    public void writeFile(long index, String newText,boolean appendFile)
    {

        BufferedOutputStream bufferedWriter = null;
        if(appendFile)
            bufferedWriter = filesOutputBuffer.get(filesIndex.get(index));
        else
            try {
                bufferedWriter = new BufferedOutputStream(new FileOutputStream(filesIndex.get(index),appendFile));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        
        try 
        {
            if(bufferedWriter!= null)
            {
                bufferedWriter.write(newText.getBytes());
                bufferedWriter.flush();
            }
                
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        finally
        {
            if(!appendFile && bufferedWriter!= null)
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        
       
    }

    public void writeLine(long index, String newText)
    {
        writeFile(index,newText + "\n",true);
    }

    public String readFile(long index)
    {
        int bufferSize = 1024<<8;
        byte[] newLine = new byte[bufferSize];
        int lineLength = 0;

        try(BufferedInputStream bufferedInputStream =  new BufferedInputStream(new FileInputStream((filesIndex.get(index))),bufferSize))
        {    
            
            lineLength = bufferedInputStream.read(newLine);

        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }

        byte[] readedLine = Arrays.copyOf(newLine, lineLength > 0 ? lineLength : 0);
        return new String(readedLine).replace("\r\n","\n");

    }

    public String readFileLine(long index, int line)
    {
        String[] lines =  readFile(index).split("\n");
        return lines[line];
    }

    public static void main(String[] args) {
        
        FileManager fileManager = new FileManager();

        long filesIndex = fileManager.openFile("prueba.txt", true);

        int populationSize  = 0;
        int survivalsSize  = 0;
        double ejecTime  = 0.0;
        populationSize = Integer.parseInt(fileManager.readFileLine(filesIndex, 0).split(":")[1]);
        survivalsSize = Integer.parseInt(fileManager.readFileLine(filesIndex, 2).split(":")[1]);
        ejecTime = Double.parseDouble(fileManager.readFileLine(filesIndex, 1).split(":")[1]);
        

        double[][] results = new double[populationSize][];
        for(int i =0; i < populationSize; i++)
        {
            String[] resultParams = fileManager.readFileLine(filesIndex, i+5).split(",");
            double kp = Double.parseDouble(resultParams[0]);
            double kd = Double.parseDouble(resultParams[1]);
            double ki = Double.parseDouble(resultParams[2]);

            results[i] = new double[]{kp,kd,ki};
        }

        System.out.println("Tamanio de la poblacion: " + populationSize);

        System.out.println("Supervivientes: " + survivalsSize);

        System.out.println("Tiempo de ejecucion: " + ejecTime);

        for(double[] result : results)
            System.out.println("kp: " + result[0] + "  kd: " + result[1] + "  ki: " + result[2]);
        
    }
    
}
