
class Light {
    public static final int AMBIENT = 0;
    public static final int DIRECTIONAL = 1;
    public static final int POINT = 2;

    public int lightType;
    public float x, y, z;           // the position of a point light or
                                    // the direction to a directional light
    public float ir, ig, ib;        // intensity of the light source

    public Light(int type, float xval, float yval , float zval, float r, float g, float b)
    {
        lightType = type;
        x = xval; y = yval; z = zval;
        ir = r;   ig = g;   ib = b;
        if (type == DIRECTIONAL) {
            float t = (float) (1 / Math.sqrt(x*x + y*y + z*z));
            x *= t;
            y *= t;
            z *= t;
        }
    }
}