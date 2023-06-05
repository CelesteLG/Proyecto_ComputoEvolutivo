using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System.IO;

public class ComunicationsManager
{
    private string FILES_PATH = "";

    public ComunicationsManager(string path)
    {
        FILES_PATH = path;
    }

    public ComunicationsManager()
    {}

    public void setPath(string path)
    {
        FILES_PATH = path;
    }

    public void writeLine(string filePath, string content, bool append)
    {
        write(filePath,content + "\n",append);
    }

    public void write(string filePath, string content, bool append)
    {
        string fullPath = FILES_PATH + filePath;
        using (StreamWriter writer = new StreamWriter(fullPath,append))
        {
            writer.Write(content);
        }
    }

    public string readFile(string filePath)
    {
        string content = "";
        string fullPath = FILES_PATH + filePath;
        if (File.Exists(fullPath))
        {
            using (StreamReader reader = new StreamReader(fullPath))
            {
                content = reader.ReadToEnd();
            }
        }

        return content;
    }

    public string readFileLine(string filePath,int lineIndex)
    {
        string line = null;
        string fullPath = FILES_PATH + filePath;
        if (File.Exists(fullPath))
        {
            using (StreamReader reader = new StreamReader(fullPath))
            {
                for (int i = 0; i <= lineIndex; i++)
                {
                    line = reader.ReadLine();
                    if (line == null)
                        break;
                }
            }
        }
        return line;
    }
    

    
}
