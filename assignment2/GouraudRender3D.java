
class PlaneEqn3
{
    float dxdy, dzdy; 
    float y1, x1, z1;// ,x, y,  z;
    float drdy, dgdy,dbdy;
    float r1,g1,b1;
    public PlaneEqn3(int x1val, int y1val, int z1val
                        , float r1val,float g1val, float b1val
                        ,int x2val, int y2val, int z2val 
                        , float r2val,float g2val, float b2val )
    {
        x1=x1val; y1=y1val;z1=z1val; 
        r1=r1val;
        g1=g1val;
        b1=b1val;
        if(y2val!=y1)
        {
           dxdy=(x2val-x1)/(y2val-y1);
           dzdy=(z2val-z1)/(y2val-y1);
           drdy=(r2val-r1)/(y2val-y1);
           dgdy=(g2val-g1)/(y2val-y1);
           dbdy=(b2val-b1)/(y2val-y1);
        }
        else
        {  dxdy=dzdy=drdy=dgdy=dbdy=0; }
    }
    void next()
    {
        y1++; x1+=dxdy; z1+=dzdy;
        r1+=drdy;g1+=dgdy; b1+=dbdy;
    }
    float getX(){return x1;}
    float getY(){return y1;}
    float getZ(){return z1;}
    float getR(){return r1;}
    float getG(){return g1;}
    float getB(){return b1;}
}

public class GouraudRender3D extends Render3D
{
    protected void SortVertix(int  vertix_order[], int v[])
    {
        // sort vertix with y-coordinate
        int min,max,vmin,vmax;
        if(vlist[v[0]].y>vlist[v[1]].y)
        {
             vmin=v[1];
             vmax=v[0];
             max=0;
             min=1;
        }
        else
        {
             vmin=v[0];
             vmax=v[1];
             max=1;
             min=0;
        }

        if(vlist[v[2]].y<=vlist[vmin].y)
        {
            v[0]=v[2];
            v[1]=vmin;
            v[2]=vmax;
            vertix_order[0]=2;
            vertix_order[1]=min;
            vertix_order[2]=max;
        }
        else
        {
            if(vlist[v[2]].y>=vlist[vmax].y)
            {
                v[0]=vmin;
                v[1]=vmax;
                vertix_order[0]=min;
                vertix_order[1]=max;
                vertix_order[2]=2;

            }
            else
            {
                v[0]=vmin;
                v[1]=v[2];
                v[2]=vmax;
                vertix_order[0]=min;
                vertix_order[1]=2;
                vertix_order[2]=max;                
            }
        }
    }
     public void Draw(Raster rast, int v[]
                         ,float r[],float g[],float b[], int color)
     {
        int VertixOrder[]=new int[3];
        SortVertix(VertixOrder,  v);
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
        if (y3<0 ||y1>rast.height )
           return ;
        if  ( ( x1<0&&x2<0&&x3<0) || ( x1>rast.width&&x2>rast.width&&x3>rast.width))
           return ;
       int area2=Area2(x1,y1,x2,y2,x3,y3);
       if(Math.abs(area2)<1) return;
       PlaneEqn3 p1,p2;
       if(area2>0)                   
       {
            p1 =new PlaneEqn3(x1,y1,z1
                        ,r[VertixOrder[0]],g[VertixOrder[0]],b[VertixOrder[0]]
                        ,x3,y3,z3
                        ,r[VertixOrder[2]],g[VertixOrder[2]],b[VertixOrder[2]]);
            p2 =new PlaneEqn3(x1,y1,z1
                        ,r[VertixOrder[0]],g[VertixOrder[0]],b[VertixOrder[0]]
                        ,x2,y2,z2
                        ,r[VertixOrder[1]],g[VertixOrder[1]],b[VertixOrder[1]]);
         }
         else
         {
            p2 =new  PlaneEqn3(x1,y1,z1
                        ,r[VertixOrder[0]],g[VertixOrder[0]],b[VertixOrder[0]]
                        ,x3,y3,z3
                        ,r[VertixOrder[2]],g[VertixOrder[2]],b[VertixOrder[2]]);
            p1 =new  PlaneEqn3(x1,y1,z1
                        ,r[VertixOrder[0]],g[VertixOrder[0]],b[VertixOrder[0]]
                        ,x2,y2,z2
                        ,r[VertixOrder[1]],g[VertixOrder[1]],b[VertixOrder[1]]);
         }
        for(int cy=y1;cy<y2;cy++)
        {
            float tz=p1.getZ();
            float tr=p1.getR();
            float tg=p1.getG();
            float tb=p1.getB();
            int tx1=(int) p1.getX()
               ,tx2=(int) p2.getX()
               ;
            float dzdx=(tz-p2.getZ())/(tx1-tx2);
            float drdx=(tr-p2.getR())/(tx1-tx2);
            float dgdx=(tg-p2.getG())/(tx1-tx2);
            float dbdx=(tb-p2.getB())/(tx1-tx2);
            if(cy>=0&&cy<rast.height)
            {

                for( int cx=(int) tx1; cx<=tx2;cx++)
                {
                    int offset=cy*rast.width;    
                    if(cx>=0&& cx<rast.width)
                    {
                        if (tz <= rast.zbuff[offset+cx])
                        {
                            if(tr>255f) tr=255;
                            if(tg>255f) tg=255;
                            if(tb>255f) tb=255;
                            int tcolor= 0xff000000
                                      + ((((int)(tr))&0xff) << 16)
                                      + ((((int)(tg))&0xff) << 8)
                                      + (((int)(tb))&0xff);
                            rast.pixel[offset+cx] =tcolor;
                            rast.zbuff[offset+cx] =(int)(tz) ;
                        }
                    }
                    tz+=dzdx;
                    tr+=drdx;
                    tg+=dgdx;
                    tb+=dbdx;

                }           
            }
            p1.next(); p2.next();
        }
         if(area2>0)                   
         {
              p2 =new PlaneEqn3(x2,y2,z2
                                ,r[VertixOrder[1]],g[VertixOrder[1]],b[VertixOrder[1]]
                                ,x3,y3,z3
                                ,r[VertixOrder[2]],g[VertixOrder[2]],b[VertixOrder[2]]
                                );
         }
         else
         {
              p1 =new PlaneEqn3(x2,y2,z2
                                ,r[VertixOrder[1]],g[VertixOrder[1]],b[VertixOrder[1]]
                                ,x3,y3,z3
                                ,r[VertixOrder[2]],g[VertixOrder[2]],b[VertixOrder[2]]
                                );

         }
 
        for(int cy=y2;cy<y3;cy++)
        {
            float tz=p1.getZ();
            float tr=p1.getR();
            float tg=p1.getG();
            float tb=p1.getB();

            int tx1=(int) p1.getX()
               ,tx2=(int) p2.getX()
               ;
            float dzdx=(tz-p2.getZ())/(tx1-tx2);
            float drdx=(tr-p2.getR())/(tx1-tx2);
            float dgdx=(tg-p2.getG())/(tx1-tx2);
            float dbdx=(tb-p2.getB())/(tx1-tx2);
            if(cy>=0&&cy<rast.height)
            {
                int offset=cy*rast.width;    
                for( int cx=(int) tx1; cx<=tx2;cx++)
                {
                    if(cx>=0&& cx<rast.width)
                    {
                        if (tz <= rast.zbuff[offset+cx]) 
                        {
                            if(tr>255f) tr=255;
                            if(tg>255f) tg=255;
                            if(tb>255f) tb=255;
                            int tcolor= 0xff000000
                                      + ((((int)(tr))&0xff) << 16)
                                      + ((((int)(tg))&0xff) << 8)
                                      + (((int)(tb))&0xff);
                            rast.pixel[offset+cx] =tcolor;
                            rast.zbuff[offset+cx] =(int)(tz) ;
                        }
                    }
                    tz+=dzdx;
                    tr+=drdx;
                    tg+=dgdx;
                    tb+=dbdx;
                }           
            }
            p1.next(); p2.next();
        }
    }

 }



