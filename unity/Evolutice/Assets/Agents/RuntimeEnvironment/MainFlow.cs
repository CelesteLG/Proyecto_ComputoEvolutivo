using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class MainFlow : MonoBehaviour
{
    private readonly string STATUS_FILE = "status.txt";
    private readonly string DATA_FILE = "data.txt";
    private readonly string PARAMS_FILE = "params.txt";
    private readonly string PROGRESS_FILE = "survivals.txt";
    private readonly string FILES_PATH = "";

    private PopulationManager populationManager;
    private ComunicationsManager comunicationsManager;
    private float[][] currentParams;
    private int populationSize = 40;
    public GameObject drone;
    
    private float generationLifeTime = 0;
    private float generationDuration = 5;
    

    private bool runningState;

    
    // Start is called before the first frame update
    void Start()
    {
        gameObject.AddComponent<PopulationManager>();
        comunicationsManager = new ComunicationsManager(FILES_PATH);
        populationManager = gameObject.GetComponent<PopulationManager>();
        populationManager.setDrone(drone);
        // populationManager.initPopulation(populationSize);
        readNewParams();
        populationManager.replacePopulation(currentParams,populationSize);
        // populationManager.setInitialGenoma(currentParams);
        // populationManager.initPopulation(populationSize);
        generationLifeTime = 0;
        comunicationsManager.writeLine(STATUS_FILE,"optimizator status:READY",false);
        comunicationsManager.writeLine(STATUS_FILE,"excecution status:RUNNING",true);
        runningState = true;
        runningState = true;

    }

    void Update()
    {
        generationLifeTime += Time.deltaTime;
        if(runningState && generationLifeTime >= generationDuration)
        {
            sendEvaluation(populationManager);
            InvokeRepeating("newGenerationInitializator", 0.0f, 3.0f);
            runningState = false;
        }
    }

 
    public bool checkForStatusUpdate()
    {
        string status = comunicationsManager.readFileLine(STATUS_FILE,0).Split(':')[1];
        if(status == "READY")
            return true;

        return false;
    }

    public void readNewParams()
    {
        populationSize = int.Parse(comunicationsManager.readFileLine(PARAMS_FILE,0).Split(':')[1]);
        currentParams = new float[populationSize][];
        for(int i = 0; i < populationSize; i ++)
        {
            string paramsLine = comunicationsManager.readFileLine(PARAMS_FILE,i+1);
            currentParams[i] = new float[9];
            for(int j = 0; j < 9; j++)
                currentParams[i][j] = float.Parse(paramsLine.Split(',')[j]);
        }

    }

    public void sendEvaluation(PopulationManager populationManager)
    {
        float[][] evaluationParams = populationManager.getPopulationEvaluation();
        comunicationsManager.writeLine(DATA_FILE,"Evaluation size:" + populationSize,false);
        for(int i = 0; i < populationSize; i++)
        {
            string paramsLine = "";
            for(int j = 0; j < 10; j++)
            {
                paramsLine += evaluationParams[i][j];
                if(j != 9)
                    paramsLine+=",";
            }
            comunicationsManager.writeLine(DATA_FILE,paramsLine,true);
        }
        comunicationsManager.writeLine(PROGRESS_FILE,"Survival individuals:" + populationManager.getSurvivalIndividuals() + " " +(populationManager.getSurvivalIndividuals()*100.0f)/(populationSize*1.0f) + "%" ,true);
        comunicationsManager.writeLine(STATUS_FILE,"optimizator status:PENDING",false);
        comunicationsManager.writeLine(STATUS_FILE,"excecution status:READY",true);
    }

    public void newGenerationInitializator()
    {
        if(checkForStatusUpdate())
        {
            readNewParams();
            populationManager.replacePopulation(currentParams,populationSize);
            generationLifeTime = 0;
            comunicationsManager.writeLine(STATUS_FILE,"optimizator status:READY",false);
            comunicationsManager.writeLine(STATUS_FILE,"excecution status:RUNNING",true);
            runningState = true;
            CancelInvoke("newGenerationInitializator");
        }
    }
}
