
class Light {
    public static final int AMBIENT = 0;
    public static final int DIRECTIONAL = 1;
    public static final int POINT = 2;

    public int lightType;
    public float x, y, z;           // the position of a point light or
    protected float tranx,trany,tranz; // the direction to a directional light
    public float ir, ig, ib;        // intensity of the light source

    public Light(int type, float xval, float yval , float zval, float r, float g, float b)
    {  Set(type, xval, yval , zval, r, g, b); }
    public void Set(int type, float xval, float yval , float zval, float r, float g, float b)
    {
        lightType = type;
        tranx=x=xval; trany=y=yval; tranz=z = zval;
        ir = r;   ig = g;   ib = b;
        if (type == DIRECTIONAL) 
        {
            float t=(float)Math.sqrt(x*x + y*y + z*z);
            if (t<0.0000001) return;
            t = (float) (1 /t);
            x *= t;
            y *= t;
            z *= t;
        } 
    }
}