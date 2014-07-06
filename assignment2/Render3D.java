// Tui Popenoe
// Render3D.java

// Abstract class representing a 3D render
public abstract class Render3D
{
    public static boolean bAmbientLight = true;
    public static boolean bPointLight = true;
    public static boolean bDirectionalLight = true;
    protected Vertex3D vlist[];
    boolean m_reflect_mode;

    // Sets the Reflectance mode
    public void SetReflect(boolean reflect_mode){
        m_reflect_mode=reflect_mode;
    }

    // Illuminates the Surface
    public void Illuminate(Light l[], int lights, int v[], Surface surface,
        float r[],float g[],float b[]){

        r[0] =r[1] =r[2] = 0;
        g[0] =g[1] =g[2] = 0;
        b[0] =b[1] =b[2] = 0;
        for (int i = 0; i < lights; i++){
            for (int j = 0; j < 3; j++){
                if(l[i].lightType==Light.POINT &&!bPointLight){
                    continue;
                }
                if(l[i].lightType==Light.DIRECTIONAL&&!bDirectionalLight){
                    continue;
                }

                switch(l[i].lightType){
                    case Light.AMBIENT:
                        if(bAmbientLight){
                            r[j] += surface.ka * surface.r * l[i].ir;
                            g[j] += surface.ka * surface.g * l[i].ig;
                            b[j] += surface.ka * surface.b * l[i].ib;
                        }
                        break;
                    case Light.DIRECTIONAL:
                    case Light.POINT:
                        float lx=  vlist[v[j]].x -l[i].x 
                              ,ly=  vlist[v[j]].y -l[i].y 
                              ,lz= vlist[v[j]].z  -l[i].z 
                              ;
                        float tmp=1.0f;
                        if(Light.DIRECTIONAL==l[i].lightType)
                        {
                            lx=l[i].x; ly=l[i].y; lz=l[i].z;
                        } 
                        else
                        {         
                            lx=  vlist[v[j]].x -l[i].x; 
                            ly=  vlist[v[j]].y -l[i].y; 
                            lz= vlist[v[j]].z  -l[i].z; 

                            tmp=lx*lx+ly*ly+lz*lz;
                            if(tmp<0.00000001)
                            {
                                 lx=vlist[v[j]].nx;
                                 ly=vlist[v[j]].ny;
                                 lz=vlist[v[j]].nz;

                            }
                            else
                            tmp=(float)Math.sqrt(tmp);
                        }
                        float diff =(vlist[v[j]].nx*lx 
                                    + vlist[v[j]].ny*ly 
                                    + vlist[v[j]].nz*lz)/tmp;
                        if(m_reflect_mode)
                        {
                                
                             float vx=  vlist[v[j]].x
                                  ,vy=  vlist[v[j]].y
                                  ,vz= vlist[v[j]].z
                                         ;                            
                             tmp=vx*vx+vy*vy+vz*vz;
                             if(tmp<0.000001)
                             {
                                  vx=vlist[v[j]].nx;
                                   vy=vlist[v[j]].ny;
                                   vz=vlist[v[j]].nz;
                                   tmp=1;
                             }
                            else{
                                vx/=tmp; vy/=tmp; vz/=tmp;
                            }

                            float hx,hy,hz;
                            hx=lx+vx;
                            hy=ly+vy; hz=lz+vz;
                            tmp=hx*hx+hy*hy+hz*hz;

                            if(tmp<0.000001)
                            {
                                hx=vlist[v[j]].nx;
                                hy=vlist[v[j]].ny;
                                hz=vlist[v[j]].nz;
                            }
                            else{
                                hx/=tmp; 
                                hy/=tmp;
                                hz/=tmp;
                            }
                            
                            tmp = hx*vlist[v[j]].nx + hy*vlist[v[j]].ny
                                + hz*vlist[v[j]].nz;
                                diff+=surface.ks*Math.pow(tmp,surface.ns);
                            }
                            if (diff > 0){
                                     r[j] += diff * surface.kd * surface.r * l[i].ir;
                                     g[j] += diff * surface.kd * surface.g * l[i].ig;
                                     b[j] += diff * surface.kd * surface.b * l[i].ib;
                            }
                        break;
                 }
            }
        }

        for(int i=0; i<3; i++){
            r[i]*=255f; 
            g[i]*=255f; 
            b[i]*=255f; 
            if (r[i]<0) r[i]=0f;
            if (g[i]<0) g[i]=0f;
            if (b[i]<0) b[i]=0f;
            if (r[i]>255f) r[i]=255f;
            if (g[i]>255f) g[i]=255f;
            if (b[i]>255f) b[i]=255f;
        }
    }

    public void fill(Light lightList[],int lights,Vertex3D worldList[],
        int nVertices, Matrix4x4 model, Matrix4x4 view, TriFace triList[],
        int nTriangles, Raster raster){

        Vertex3D tranList[]=new Vertex3D[nVertices];
        
        for ( int i=0; i<nVertices; i++){
            tranList[i]= new Vertex3D();
        }

        model.transform(worldList,tranList, nVertices);
        
        vlist=tranList;
        for(int i=0;i<nTriangles;i++){
            Illuminate(lightList, lights, triList[i].getVertex(),
                triList[i].getSurface(),
                triList[i].getR(),
                triList[i].getG(),
                triList[i].getB());
        }
        view.transform(tranList,tranList, nVertices);
        for(int i=0;i<nTriangles;i++)
        {
            triList[i].calColor();
            Draw(raster,triList[i].getVertex(), triList[i].getR(),
                triList[i].getG(),triList[i].getB(),
                triList[i].getColor());
        }
    }

    // Return the area of the object
    public final static int Area2(int x1,int y1,int x2,int y2, int x3, int y3){
       	 return x1*y2+y1*x3+x2*y3-x3*y2-y3*x1-x2*y1;
    }

    // Abstract method to draw the object
    public abstract void Draw(Raster rast, int v[], float r[], float g[],
        float b[], int color);
}

