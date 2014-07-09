// Tui Popenoe
// Light.java

class Light{
    public static final int AMBIENT = 0;
    public static final int DIRECTIONAL = 1;
    public static final int POINT = 2;

    public int lightType;
    // Position of a point light
    public float x, y, z;
    // Direction to directional light
    protected float tranx,trany,tranz;
    // Intensity of source
    public float ir, ig, ib;

    // Constructor
    public Light(int type, float xval, float yval , float zval, float r, 
        float g, float b){
        Set(type, xval, yval , zval, r, g, b);
    }

    // Set the properties of the Light
    public void Set(int type, float xval, float yval , float zval, float r, 
        float g, float b){
        lightType = type;
        tranx=x=xval; trany=y=yval; tranz=z = zval;
        ir = r;   ig = g;   ib = b;
        if (type == DIRECTIONAL){
            float t=(float)Math.sqrt(x*x + y*y + z*z);
            if (t<0.0000001) return;
            t = (1 /t);
            x *= t;
            y *= t;
            z *= t;
        } 
    }
}