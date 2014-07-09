// Tui Popenoe
// FlatTri.java

import java.awt.Color;

class EdgeEqn{
    public final static int FRACBITS = 11;
    public int A, B, C;
    public int flag;

    // Constructor
    public EdgeEqn(Vertex3D v0, Vertex3D v1)
    {
        int v0x = (int) (v0.x * 32);
        int v0y = (int) (v0.y * 32);
        int v1x = (int) (v1.x * 32);
        int v1y = (int) (v1.y * 32);
        A = v0y - v1y;
        B = v1x - v0x;
        C = -(A*(v0x + v1x) + B*(v0y + v1y));
        A *= 64;
        B *= 64;
        flag = 0;
        if (A >= 0) flag += 8;
        if (B >= 0) flag += 1;
    }

    // Flip the Edge values
    public void flip(){
        A = -A;
        B = -B;
        C = -C;
    }

    // Evaluate the equation
    public int evaluate(int x, int y){
        return (A*x + B*y + C);
    }
}

// FlatTri Class
public class FlatTri implements Drawable {
    protected static Vertex3D vlist[];
    protected int v[];
    protected int color;
    protected Surface surface;
    public FlatTri(){ }
    public FlatTri(int v0, int v1, int v2){
        v = new int[3];
        v[0] = v0;
        v[1] = v1;
        v[2] = v2;
        color = 0;
        scale = -1;
    }

    // Get / Set Methods
    public Color getColor(){
        return new Color(color);
    }

    public void setColor(Color c){
        color = c.getRGB();
    }

    public void setSurface(Surface s){
        surface = s;
    }

    public Surface getSurface(){
        return surface;
    }

    // Illuminate the Tri
    public void Illuminate(Light l[], int lights){
        float r[] = new float[3];
        float g[] = new float[3];
        float b[] = new float[3];
        for (int i = 0; i < lights; i++) {
            for (int j = 0; j < 3; j++) {
                if (i == 0) {
                    r[j] = 0;
                    g[j] = 0;
                    b[j] = 0;
                }
                if (l[i].lightType == Light.AMBIENT) {
                    r[j] += surface.ka * surface.r * l[i].ir;
                    g[j] += surface.ka * surface.g * l[i].ig;
                    b[j] += surface.ka * surface.b * l[i].ib;
                } 
                else{
                    // None
                }
                if (l[i].lightType == Light.DIRECTIONAL){
                    float diff = vlist[v[j]].nx*l[i].x + 
                        vlist[v[j]].ny*l[i].y + vlist[v[j]].nz*l[i].z;

                    if (diff > 0) {
                        r[j] += diff * surface.kd * surface.r * l[i].ir;
                        g[j] += diff * surface.kd * surface.g * l[i].ig;
                        b[j] += diff * surface.kd * surface.b * l[i].ib;
                    }
                }
            }
        }

        float aver = (r[0] + r[1] + r[2])/3;
        float aveg = (g[0] + g[1] + g[2])/3;
        float aveb = (b[0] + b[1] + b[2])/3;
        if (aver > 1f) aver = 1f;
        if (aveg > 1f) aveg = 1f;
        if (aveb > 1f) aveb = 1f;

        color = 0xff000000
              + (((int)(aver*255)) << 16)
              + (((int)(aveg*255)) << 8)
              + ((int)(aveb*255));
    }

    // Set the list of vertices
    public void setVertexList(Vertex3D list[]){
        vlist = list;
    }

    protected EdgeEqn edge[];

    protected int area;

    protected int xMin, xMax, yMin, yMax;

    protected double scale;

    private static byte sort[][] = {
        {0, 1}, {1, 2}, {0, 2}, {2, 0}, {2, 1}, {1, 0}
    };

    public void PlaneEqn(int eqn[], int p0, int p1, int p2)
    {
        int Ap, Bp, Cp;
        double sp0 = scale * p0;
        double sp1 = scale * p1;
        double sp2 = scale * p2;
        Ap = (int)(edge[0].A*sp2 + edge[1].A*sp0 + edge[2].A*sp1);
        Bp = (int)(edge[0].B*sp2 + edge[1].B*sp0 + edge[2].B*sp1);
        Cp = (int)(edge[0].C*sp2 + edge[1].C*sp0 + edge[2].C*sp1);
        eqn[0] = Ap;
        eqn[1] = Bp;
        eqn[2] = Ap*xMin + Bp*yMin + Cp + (1 << (EdgeEqn.FRACBITS - 1));
    }

    protected boolean triangleSetup(Raster r){
        if (edge == null) edge = new EdgeEqn[3];

        // Compute edge equations
        edge[0] = new EdgeEqn(vlist[v[0]], vlist[v[1]]);
        edge[1] = new EdgeEqn(vlist[v[1]], vlist[v[2]]);
        edge[2] = new EdgeEqn(vlist[v[2]], vlist[v[0]]);

        // Orient edges so tri's are internal
        area = edge[0].C + edge[1].C + edge[2].C;
        if (area >= 0) {
            return false;                // degenerate triangle
        }

        if (area < 0) {
            edge[0].flip();
            edge[1].flip();
            edge[2].flip();
            area = -area;
        }

        // Compute the bounding box
        int xflag = edge[0].flag + 2*edge[1].flag + 4*edge[2].flag;
        int yflag = (xflag >> 3) - 1;
        xflag = (xflag & 7) - 1;
        xMin = (int) (vlist[v[sort[xflag][0]]].x);
        xMax = (int) (vlist[v[sort[xflag][1]]].x + 1);
        yMin = (int) (vlist[v[sort[yflag][1]]].y);
        yMax = (int) (vlist[v[sort[yflag][0]]].y + 1);

        // Attach bounding box to Raster
        xMin = (xMin < 0) ? 0 : xMin;
        xMax = (xMax >= r.width) ? r.width - 1 : xMax;
        yMin = (yMin < 0) ? 0 : yMin;
        yMax = (yMax >= r.height) ? r.height - 1 : yMax;
        return true;
    }

    // Draw the Flat tri given the raster
    public void Draw(Raster r){
        int zPlane[] = new int[3];

        if (!triangleSetup(r)) return;
        scale = (1 << EdgeEqn.FRACBITS) / ((double) area);
        PlaneEqn(zPlane, (int) vlist[v[0]].z, (int) vlist[v[1]].z, 
            (int) vlist[v[2]].z);

        int x, y;
        int A0 = edge[0].A;
        int A1 = edge[1].A;
        int A2 = edge[2].A;
        int B0 = edge[0].B;
        int B1 = edge[1].B;
        int B2 = edge[2].B;
        int t0 = A0*xMin + B0*yMin + edge[0].C;
        int t1 = A1*xMin + B1*yMin + edge[1].C;
        int t2 = A2*xMin + B2*yMin + edge[2].C;
        int Az = zPlane[0];
        int Bz = zPlane[1];
        int tz = zPlane[2];

        yMin *= r.width;
        yMax *= r.width;

        // Scan converted triangle
        for (y = yMin; y <= yMax; y += r.width) {
            int e0 = t0;
            int e1 = t1;
            int e2 = t2;
            int z = tz;
            boolean beenInside = false;
            for (x = xMin; x <= xMax; x++) {
                if ((e0|e1|e2) >= 0) {       // all 3 edges must be >= 0
                    int iz = z >> EdgeEqn.FRACBITS;
                    if (iz <= r.zbuff[y+x]) {
                        r.pixel[y+x] = color;
                        r.zbuff[y+x] = iz;
                    }
                    beenInside = true;
                } 
                else if (beenInside) {
                    break;
                }
                e0 += A0;
                e1 += A1;
                e2 += A2;
                z += Az;
            }
            t0 += B0;
            t1 += B1;
            t2 += B2;
            tz += Bz;
        }
    }
}