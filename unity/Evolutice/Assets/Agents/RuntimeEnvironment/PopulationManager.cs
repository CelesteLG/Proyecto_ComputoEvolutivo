using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class PopulationManager : MonoBehaviour
{

    public GameObject drone;
    private GameObject[] deployedDrones;
    private int populationSize;
    private int survivalIndividuals;
    private int lastInsertedRone = 0;
    private float[][] initialGenoma;

    public void setInitialGenoma(float[][] initialGenoma)
    {
        this.initialGenoma = initialGenoma;
    }

    public void setDrone(GameObject drone)
    {
        this.drone = drone;
    }
    
    public void initPopulation(int populationSize)
    {
        float radius = 0f;
        float angle = 0;
        int contador = 1;
        int ring = 1;
        int lastRingSize = 0;
        this.populationSize = populationSize;
        deployedDrones = new GameObject[populationSize];

        int dronesBatch = 20;
        int deployedBatch = 0;
        Vector3[] positions = new Vector3[dronesBatch];
        while(contador <= populationSize)
        {
            // GameObject instantiatedObject = Instantiate(drone, new Vector3(Mathf.Cos(angle)*radius, 50f, Mathf.Sin(angle)*radius), Quaternion.identity);
            Vector3 position = new Vector3(Mathf.Cos(angle)*radius, 50f, Mathf.Sin(angle)*radius);
            // instantiatedObject.GetComponent<DroneControl>().resetParams();
            // deployedDrones[contador-1] = instantiatedObject;
            contador++;
            if(contador > (Mathf.Pow(ring,2) + lastRingSize))
            {
                ring++;
                radius+=2f;
                lastRingSize = contador - 1;
            }
            angle = 2*Mathf.PI/(ring*ring)*(contador - lastRingSize);
            if(deployedBatch < dronesBatch)
            {
                positions[deployedBatch] = position;
                deployedBatch++;
            }
            else
            {
                deployedBatch = 0;
                initDrones(positions,dronesBatch);
                positions = new Vector3[dronesBatch];
                positions[deployedBatch] = position;
            }
        }
    }

    private void initDrones(Vector3[] positions, int totalDrones)
    {
        for (int i = 0; i < totalDrones; i++)
        {
            GameObject newDrone = Instantiate(drone, positions[i], Quaternion.identity);
            // newDrone.GetComponent<DroneControl>().setParams(initialGenoma[lastInsertedRone]);
            newDrone.GetComponent<DroneControl>().resetParams();
            deployedDrones[lastInsertedRone] = newDrone;
            lastInsertedRone++;
        }
    }

    public void destroyPopulation()
    {
        for(int i = 0; i < populationSize; i++)
        {
            Destroy(deployedDrones[i],0.0f);
        }
        populationSize = 0;
    }

    public void replacePopulation(float[][] newGenoma, int populationSize)
    {
        destroyPopulation();
        float radius = 0f;
        float angle = 0;
        int ring = 1;
        int lastRingSize = 0;
        this.populationSize = populationSize;
        deployedDrones = new GameObject[populationSize];
        for(int i = 1; i <= populationSize; i++)
        {
            GameObject instantiatedObject = Instantiate(drone, new Vector3(Mathf.Cos(angle)*radius, 50f, Mathf.Sin(angle)*radius), Quaternion.identity);
            instantiatedObject.GetComponent<DroneControl>().setParams(newGenoma[i-1]);
            deployedDrones[i-1] = instantiatedObject;
            if(i > (Mathf.Pow(ring,2) + lastRingSize))
            {
                ring++;
                radius+=4.0f;
                lastRingSize = i - 1;
            }
            angle = 2*Mathf.PI/(ring*ring)*(i - lastRingSize);
        }
    }

    public float[][] getPopulationEvaluation()
    {
        float[][] populationEvaluation = new float[populationSize][];
        survivalIndividuals = 0;
        for(int i = 0; i < populationSize; i++)
        {
            populationEvaluation[i] = new float[10];
            for(int j= 0; j < 9; j++)
                populationEvaluation[i][j] = deployedDrones[i].GetComponent<DroneControl>().getParams()[j];

            populationEvaluation[i][9] = deployedDrones[i].GetComponent<DroneControl>().getTotalError();
            survivalIndividuals += (deployedDrones[i].GetComponent<DroneControl>().getSurvivalStatus() ? 1 : 0);
        }
        return populationEvaluation;
    }

    public int getSurvivalIndividuals()
    {
        return survivalIndividuals;
    }
}
