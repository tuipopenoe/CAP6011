

public class TriFace
{
    protected Surface aSurface;
    protected int nVertix[];
    protected float r[]=new float[3]
                    ,g[]=new float[3]
                    ,b[]=new float[3]
                    ;
    protected int color;                

    public TriFace()
    {
        nVertix=new int[3];
    }
    public TriFace(int v1, int v2, int v3,Surface surf)
    {
        nVertix=new int[3];
        SetFace(v1, v2, v3, surf);
    }
    public void setSurface(Surface s){aSurface=s;}
    public Surface getSurface(){return aSurface;}
    public void SetFace(int v1, int v2, int v3,Surface surf)
    {   nVertix[0]=v1; nVertix[1]=v2; nVertix[2]=v3; aSurface=surf; }
    public int[] getVertix(){ return nVertix;}
    public float[] getR (){return r;}
    public float[] getG (){return g;}
    public float[] getB (){return b;}
    public int getColor(){return color;}
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