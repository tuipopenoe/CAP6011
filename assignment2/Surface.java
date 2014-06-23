
class Surface {
    public float r, g, b;
    public float ka, kd, ks, ns;

    public Surface(float sr, float sg, float sb, float ska, float skd, float sks, float sns)
    {
        r = sr; g = sg; b = sb;
        ka = ska; kd = skd; ks = sks; ns = sns;
    }
}