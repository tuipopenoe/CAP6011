// Tui Popenoe
// TriFace.java

// A triangular face made up of 3 vertices
public class TriFace
{
    protected Surface aSurface;
    protected int nVertex[];
    protected float r[] = new float[3], g[] = new float[3], b[] = new float[3];
    protected int color;

    public TriFace(){
        nVertex = new int[3];
    }

    public TriFace(int v1, int v2, int v3,Surface surf){
        nVertex = new int[3];
        SetFace(v1, v2, v3, surf);
    }

    // Set Methods
    public void setSurface(Surface s){
        aSurface=s;
    }

    public Surface getSurface(){
        return aSurface;
    }

    public void SetFace(int v1, int v2, int v3,Surface surf){
        nVertex[0]=v1;
        nVertex[1]=v2;
        nVertex[2]=v3;
        aSurface=surf;
    }

    public int[] getVertex(){
        return nVertex;
    }

    public float[] getR (){
        return r;
    }

    public float[] getG (){
        return g;
    }
    public float[] getB (){
        return b;
    }

    public int getColor(){
        return color;
    }

    // Calculate the color based on the RGB values
    public void calColor()
    {
        float aver = (r[0] + r[1] + r[2])/3;
        float aveg = (g[0] + g[1] + g[2])/3;
        float aveb = (b[0] + b[1] + b[2])/3;
        color = 0xff000000
              + (((int)(aver)) << 16)
              + (((int)(aveg)) << 8)
              + ((int)(aveb));
    }
}