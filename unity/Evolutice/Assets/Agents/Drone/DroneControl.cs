using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class DroneControl : MonoBehaviour
{
    Rigidbody rb;
    public float w = 0;
    public float rw = 0;
    public Vector3 normalTarget;
    public  Vector3 normal;

    public bool survival = true;

    public Vector3 targetPosition = new Vector3(0,5,0);
    public Vector3 targetVelocity = new Vector3(0,0,0);

    public float d1;
    public float d2;
    public Vector3 dn;
    public float dz;

    public float onFloorTime = 0;

    public float Kp = 2;
    public float Kd = 1;
    public float Ki = 0;

    public float KpY= 10;
    public float KdY = 5;
    public float KiY = 1;

    public float KpZ= 10;
    public float KdZ = 5;
    public float KiZ = 0;

    private float de1 = 0;
    public float ie1 = 0;

    private float de2 = 0;
    public float ie2 = 0;

    private float deY = 0;
    public float ieY = 0;

    private float deZ = 0;
    public float ieZ = 0;

    private readonly float MAX_ERROR = 1000.0f;


    public DroneControl()
    {
        System.Random random = new System.Random();
        Kp = random.Next(0, 100);
        Kd = random.Next(0, 100);
        Ki = random.Next(0, 100);

        KpY= random.Next(0, 100);
        KdY = random.Next(0, 100);
        KiY = random.Next(0, 100);

        KpZ= random.Next(0, 100);
        KdZ = random.Next(0, 100);
        KiZ = random.Next(0, 100);
    }

    public DroneControl(float Kp, float Kd, float Ki, float KpY, float KdY, float KiY, float KpZ, float KdZ, float KiZ)
    {
        this.Kp = Kp;
        this.Kd = Kd;
        this.Ki = Ki;

        this.KpY= KpY;
        this.KdY = KdY;
        this.KiY = KiY;
        
        this.KpZ = KpZ;
        this.KdZ = KdZ;
        this.KiZ = KiZ;
    }

    public void resetParams()
    {
        this.Kp = 0;
        this.Kd = 0;
        this.Ki = 0;

        this.KpY= 0;
        this.KdY = 0;
        this.KiY = 0;
        
        this.KpZ= 0;
        this.KdZ = 0;
        this.KiZ = 0;
    }

    
    public void setParams(float[] paramsArray)
    {
        
        this.Kp = paramsArray[0];
        this.Kd = 0.0f;
        this.Ki = paramsArray[2];

        this.KpY = paramsArray[3];
        this.KdY = 0.0f;
        this.KiY = paramsArray[5];
        
        this.KpZ = paramsArray[6];
        this.KdZ = 0.0f;
        this.KiZ = paramsArray[8];

    }

    public float[] getParams()
    {
        return new float[]{Kp,Kd,Ki,KpY,KdY,KiY,KpZ,KdZ,KiZ};
    }

    public bool getSurvivalStatus()
    {
        return survival;
    }

    void Start()
    {
        rb = gameObject.GetComponent<Rigidbody>();
    }

    public float getTotalError()
    {
        // return Mathf.Min(Mathf.Sqrt(ie1*ie1 + ie2*ie2 + ieZ*ieZ + ieY*ieZ),MAX_ERROR);
        return Mathf.Sqrt(ie1*ie1 + ie2*ie2 + ieZ*ieZ + ieY*ieY);
    }

    // Update is called once per frame
    void FixedUpdate()
    {
        
        Vector3 forceDirection = new Vector3(0f, 3f, 0f);

        Vector3 forcePosition1 = new Vector3(0.25f, 0f, 0.25f);

        Vector3 forcePosition2 = new Vector3(-0.25f, 0f, -0.25f);

        Vector3 forcePosition3 = new Vector3(-0.25f, 0f, 0.25f);

        Vector3 forcePosition4 = new Vector3(0.25f, 0f, -0.25f);

        normal = transform.rotation*Vector3.up;
        
        // Vector3 Te1 = transform.rotation*new Vector3(1,0,0);


        
        // Quaternion antirotation = new Quaternion(0,transform.rotation.y,0,transform.rotation.w);
        // Quaternion rotation = new Quaternion(antirotation.x, -antirotation.y,antirotation.z,antirotation.w);

        
        float yRotation = transform.rotation.eulerAngles.y;
        Matrix4x4 rotationMatrix = Matrix4x4.Rotate(Quaternion.Euler(0f, yRotation, 0f));




        Vector3 currentPosition = transform.position;
        Vector3 currentVelocity = GetComponent<Rigidbody>().velocity;
        // float forward = Input.GetAxis("Vertical");
        // float lateral = Input.GetAxis("Horizontal");
        // normalTarget = new Vector3(lateral,1,forward).normalized;


        rw+=Time.deltaTime;
        normalTarget = new Vector3(Mathf.Cos(2.0f*Mathf.PI*rw/3.0f),1.0f,Mathf.Sin(2.0f*Mathf.PI*rw/3.0f)).normalized;

        normalTarget = rotationMatrix.MultiplyVector(normalTarget);
        dn = (normalTarget - normal);
        Vector3 r1 = transform.rotation*(new Vector3(1,0,1)).normalized;
        Vector3 r2 = transform.rotation*(new Vector3(-1,0,1)).normalized;
        d1 = Vector3.Dot(dn,r1);
        d2 = Vector3.Dot(dn,r2);

        float dy = targetVelocity.y - currentVelocity.y;

        float PID1 = Kp*d1 + Kd*(d1 - de1)/Time.deltaTime + Ki*ie1;
        float PID2 = Kp*d2 + Kd*(d2 - de2)/Time.deltaTime + Ki*ie2;
        float PIDy = KpY*dy + (dy < 0 ? KdY/5.0f : KdY)*(dy - deY)/Time.deltaTime + KiY*(ieY);

        

        Vector3 forcePositionGlobal1 = gameObject.transform.TransformPoint(forcePosition1);
        Vector3 forcePositionGlobal2 = gameObject.transform.TransformPoint(forcePosition2);
        Vector3 forcePositionGlobal3 = gameObject.transform.TransformPoint(forcePosition3);
        Vector3 forcePositionGlobal4 = gameObject.transform.TransformPoint(forcePosition4);

        rb.AddForceAtPosition(rb.transform.TransformDirection(forceDirection) * Mathf.Min(PIDy - PID1,50), forcePositionGlobal1);
        rb.AddForceAtPosition(rb.transform.TransformDirection(forceDirection) * Mathf.Min(PIDy + PID1,50), forcePositionGlobal2);
        rb.AddForceAtPosition(rb.transform.TransformDirection(forceDirection) * Mathf.Min(PIDy - PID2,50), forcePositionGlobal3);
        rb.AddForceAtPosition(rb.transform.TransformDirection(forceDirection) * Mathf.Min(PIDy + PID2,50), forcePositionGlobal4);

        Vector3 YawPosition = rotationMatrix.MultiplyVector(new Vector3(1,0,0));
        Vector3 YawTarget = new Vector3(Mathf.Cos(w),0,Mathf.Sin(w));
        float YawTorque = (Vector3.Cross(YawPosition,YawTarget)).y;
        // dz = (w == 0 ? (YawTorque) : (w - rb.angularVelocity.y));
        // dz = w - rb.angularVelocity.y;
        dz = YawTorque;
        
        
        float PIDz = KpZ*dz + KdZ*(dz - deZ)/Time.deltaTime + KiZ*(ieZ);
        rb.AddTorque(new Vector3(0,Mathf.Min(PIDz,20),0));

        de1 = d1;
        ie1+= d1*Time.deltaTime;

        de2 = d2;
        ie2+= d2*Time.deltaTime;

        deY = dy;
        ieY+= dy*Time.deltaTime;

        deZ = dz;
        ieZ+= dz*Time.deltaTime;
        

        
        if(transform.position.y < 0.5)
            onFloorTime+= Time.deltaTime;

        if(onFloorTime > 1)
            survival = false;

        if(rb.velocity.magnitude > 5.0f)
        {
            rb.velocity = rb.velocity.normalized*5.0f;
        } 
    }

}
