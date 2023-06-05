# Proyecto_ComputoEvolutivo
Repositorio del desarrollo del Proyecto Final del curso de  Computo Evolutivo 2023-2

## Descripción
La siguiente implementación utiliza un algoritmo genético para optimizar los parámetros de control PID de un dron. Un control PID es un sistema de control de lazo cerrado que se retroalimenta del estado actual del individuo. Se calcula el error en función del estado actual y el objetivo deseado, y luego se utiliza una combinación lineal del error actual, la derivada del error y la integral del error. El resultado de esta combinación lineal se utiliza como la salida del sistema de control para lograr un estado objetivo de manera suave.

## Instrucciones de ejecución:
- Colocarse en la raiz y ejecutar el siguiente comando: 'java -jar executables/drones/Drones.jar'
- Una vez que el optimizador este corriendo, ejecutar el entorno de simulación: 'unity/Evolutivo.exe'

