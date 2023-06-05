package com.fciencias.evolutivo.libraries;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class FilesManagerTest 
{
 
    final String FILE_CONTENT = "Este es un archivo de prueba\nContiene varias lineas de texto\nEl archivo debera poder leer linea por linea\nPero no se probaran caracteres especiales del espaniol\n";
    final String FILE_PATH = "testObjects/testFile.txt";

    FileManager fileManager = new FileManager();
    
    //@Test
    public void readFileTest()
    {
        long fileIndex = fileManager.openFile(FILE_PATH, true);
        Assert.assertEquals(0,fileIndex);
        String[] lines = FILE_CONTENT.split("\n");

        Assert.assertEquals(FILE_CONTENT,fileManager.readFile(fileIndex));

        for(int i = 0; i < lines.length; i++)
            Assert.assertEquals(lines[i], fileManager.readFileLine(fileIndex,i));
    }

    //@Test
    public void NoFileTest() 
    {
        long fileIndex = fileManager.openFile("a" + FILE_PATH, true);
        Assert.assertEquals(-1, fileIndex);
    }

    //@Test
    public void wirteOnFileTest()
    {
        long fileIndex = fileManager.openFile(FILE_PATH, true);
        String newTextOnFile = "Nueva linea de texto";

        final String INIT_CONTENT = fileManager.readFile(fileIndex);
        fileManager.writeLine(fileIndex, newTextOnFile);
        
        Assert.assertEquals(INIT_CONTENT + "\n" + newTextOnFile,fileManager.readFile(fileIndex));

        String complementaryNewTextOnFile = ". Complemento de la nueva linea de texto";
        fileManager.writeFile(fileIndex, complementaryNewTextOnFile,true);
        Assert.assertEquals(INIT_CONTENT + "\n" + newTextOnFile + complementaryNewTextOnFile,fileManager.readFile(fileIndex));

        fileManager.writeFile(fileIndex, FILE_CONTENT, false);

        Assert.assertEquals(INIT_CONTENT,fileManager.readFile(fileIndex));
        
    }

    //@Test
    public void closeFile()
    {
        long fileIndex = fileManager.openFile(FILE_PATH, true);
        fileManager.closeFile(fileIndex);
        Assert.assertTrue(true);
    }


}
