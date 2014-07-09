// Tui Popenoe
// TridDObject.java

import java.awt.*;
public class TriDObject{
    final protected static int CHUNKSIZE=100;
    final public static int Flat = 1;
    final public static int Gouraud=2;
    final public static int GouraudReflect=3;

    protected Vertex3D tranList[]=null;
    protected Vertex3D worldList[];
    protected int nVertices;
    protected TriFace triList[];
    protected int nTriangles;
    protected Matrix4x4 model;

    Scene parent;
    
    public void Operation(Matrix4x4 m){
        m.compose(model);   
        model=new Matrix4x4(m);
    }

    // Constructor
    public TriDObject(Scene ascene){
        Reset(); parent=ascene;
    }

    // Reset the properties of the object
    public void Reset()
    {
        worldList=new Vertex3D[CHUNKSIZE];
        nVertices=0;
        triList=new TriFace[CHUNKSIZE];
        nTriangles=0;
        model= new Matrix4x4();
    }

    // Add a vertex to the object
    public void AddVertex(float xval, float yval, float zval){
        if(worldList.length==nVertices){
            Vertex3D newList[] = new Vertex3D[worldList.length+CHUNKSIZE];
            System.arraycopy(worldList, 0, newList, 0, worldList.length);
            worldList = newList;
        }

        worldList[nVertices]=new Vertex3D(xval, yval, zval);
        nVertices++;
    }

    // Add a face of vertices v1, v2, v3 and surface nsurface to the object
    public void AddTriFace(int v1, int v2, int v3, Surface nsurface){
        if(triList.length-1<=nTriangles){
            TriFace newList[] = new TriFace[triList.length+CHUNKSIZE];
            System.arraycopy(triList, 0, newList, 0, nTriangles);
            triList= newList;
        }

        triList[nTriangles]=new TriFace(v1,v2,v3,nsurface);
        nTriangles++;
    }

    // Add a normal to the object
    public void addNormal(int v, float nx,float ny,float nz){
          worldList[v].addNormal(nx,ny,nz);
    }

    // Average the normal values 
    void AverageNormal()
    {
        for(int i=0; i<nVertices; i++)
        {
            worldList[i].averageNormals();
        }
    }

    // Get the Vertor of the normals
    void GetNormalVertor(int v0, int v1, int v2
                            ,float tx[], float ty[], float tz[])
    {
        System.out.print('a');
        tx[0] = (worldList[v1].z - worldList[v0].z) * (worldList[v2].y - 
            worldList[v1].y) - (worldList[v1].y - worldList[v0].y) * 
            (worldList[v2].z - worldList[v1].z);
        ty[0] = (worldList[v1].x - worldList[v0].x) * (worldList[v2].z - 
            worldList[v1].z) - (worldList[v1].z - worldList[v0].z) * 
            (worldList[v2].x - worldList[v1].x);
        tz[0] = (worldList[v1].y - worldList[v0].y) * (worldList[v2].x - 
            worldList[v1].x) - (worldList[v1].x - worldList[v0].x) * 
            (worldList[v2].y - worldList[v1].y);
        System.out.print('b');
    }

    // Implement the abstract method draw on the object
    void Draw(Light lightList[],int lights,Raster raster,Matrix4x4 view, 
        int rending_mode,boolean reflect_mode, Raster rast){
        Render3D rendering;
        rast.fill(Color.lightGray);
        switch(rending_mode){
              case Flat :
                    rendering=new FlatRender3D();
                    break;
              case  GouraudReflect:
              case Gouraud:
                    rendering=new GouraudRender3D();
                    break;
              default: return;
        }
        rendering.SetReflect(reflect_mode);
        rendering.fill(lightList,lights,worldList,nVertices,model,view,triList,
            nTriangles, raster);
   }
}