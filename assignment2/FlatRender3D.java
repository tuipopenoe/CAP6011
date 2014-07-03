import java.awt.Color;
class PlaneEqn2
{
    float dxdy, dzdy; 
    float y1, x1, z1;// ,x, y,  z;
    public PlaneEqn2(int x1val, int y1val, int z1val
                        ,int x2val, int y2val, int z2val )
    {
        x1=x1val; y1=y1val;z1=z1val; 

        if(y2val!=y1)
        {
           dxdy=(x2val-x1)/(y2val-y1);dzdy=(z2val-z1)/(y2val-y1);
        }
        else
        {  dxdy=dzdy=0; }
    }
    void next()
    {
        y1++; x1+=dxdy; z1+=dzdy;
    }
    float getX(){return x1;}
    float getY(){return y1;}
    float getZ(){return z1;}
    float GetdZdX(){ if( Math.abs(dxdy)>0)
                            return dzdy/dxdy;
                       else return 0;
                       }
}

public class FlatRender3D extends Render3D
{
    protected void SortVertix(int v[])
    {
        // sort vertix with y-coordinate   
        int min,max;
        if(vlist[v[0]].y>vlist[v[1]].y)
        {
             max=v[0];
             min=v[1];
        }
        else 
        {
             max=v[1];
             min=v[0];   
        }
        
        if(vlist[v[2]].y<=vlist[min].y)
        {
            v[0]=v[2];
            v[1]=min;
            v[2]=max;
        }
        else
        {
            if(vlist[v[2]].y>=vlist[max].y)
            {
                v[0]=min;
                v[1]=max;
            }
            else
            {
                v[0]=min;
                v[1]=v[2];
                v[2]=max;
            }
        }
    }

    public void Draw(Raster r,int v[]
                ,float dummyr[],float dummyg[],float dummyb[], int color)
    {
        SortVertix(v);
        int x1=(int)vlist[v[0]].x
           ,x2=(int)vlist[v[1]].x
           ,x3=(int)vlist[v[2]].x
           ,y1=(int)vlist[v[0]].y
           ,y2=(int)vlist[v[1]].y
           ,y3=(int)vlist[v[2]].y
           ,z1=((int)vlist[v[0]].z)>>11
           ,z2=((int)vlist[v[1]].z)>>11
           ,z3=((int)vlist[v[2]].z)>>11
           ;

         int area2=Area2(x1,y1,x2,y2,x3,y3);
         if(Math.abs(area2)<1) return;
         PlaneEqn2 p1 =new PlaneEqn2(x1,y1,z1,x3,y3,z3)
                 ,p2 =new PlaneEqn2(x1,y1,z1,x2,y2,z2)
                 ;
         if(area2>0)                   
         {
            p1 =new PlaneEqn2(x1,y1,z1,x3,y3,z3);
            p2 =new PlaneEqn2(x1,y1,z1,x2,y2,z2);
         }
         else
         {
            p2 =new PlaneEqn2(x1,y1,z1,x3,y3,z3);
            p1 =new PlaneEqn2(x1,y1,z1,x2,y2,z2);
         }
        for(int cy=y1;cy<y2;cy++)
        {
            float tz=p1.getZ();
            int tx1=(int) p1.getX()
               ,tx2=(int) p2.getX()
               ;
            float dzdx=(tz-p2.getZ())/(tx1-tx2);
            if(cy>=0&&cy<r.height)    
            {
                int offset=cy*r.width;
                for( int cx=(int) tx1; cx<=tx2;cx++)
                {
                    if(cx>=0&& cx<r.width)
                    {
                        if (tz <= r.zbuff[offset+cx]) 
                        {
                            r.pixel[offset+cx] =color;
                            r.zbuff[offset+cx] =(int)(tz) ;
                        }
                    }
                    tz+=dzdx;
                }           
            }
            p1.next(); p2.next();
        }
         if(area2>0)                   
         {
              p2 =new PlaneEqn2(x2,y2,z2,x3,y3,z3);
         }
         else
         {
             p1 =new PlaneEqn2(x2,y2,z2,x3,y3,z3);
         }
 
        for(int cy=y2;cy<y3;cy++)
        {
            float tz=p1.getZ();
            int tx1=(int) p1.getX()
               ,tx2=(int) p2.getX()
               ;
            float dzdx=(tz-p2.getZ())/(tx1-tx2);
            if(cy>=0&&cy<r.height)    
            {
                int offset=cy*r.width;
                for( int cx=(int) tx1; cx<=tx2;cx++)
                {
                    if(cx>=0&& cx<r.width)
                    {
                        if (tz <= r.zbuff[cy*r.width+cx]) 
                        {
                            r.pixel[offset+cx] =color;
                            r.zbuff[offset+cx] =(int)(tz) ;
                        }
                    }
                    tz+=dzdx;
                }         
            }    
            p1.next(); p2.next();
        }
    }

}
