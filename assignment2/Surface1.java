
class Surface1 {
    public float r, g, b;
    public float ka, kd, ks, ns;

    public Surface1(float sr, float sg, float sb, float ska, float skd, float sks, float sns)
    { Set(sr, sg, sb, ska, skd, sks, sns);}
    public void Set(float sr, float sg, float sb, float ska, float skd, float sks, float sns)
    {
        r = sr; g = sg; b = sb;
        ka = ska; kd = skd; ks = sks; ns = sns;
    }
}