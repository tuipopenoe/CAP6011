// Tui Popenoe
// DrawGraphic.java

import java.awt.*;
import java.applet.*;

@SuppressWarnings("serial")

public class DrawGraphic extends Applet{
    Scene world;

    float  rotateX1, rotateY1 , rotateZ1, rotateX2, rotateY2 , rotateZ2, angle;
    
    float translateX, translateY, translateZ, scaleX, scaleY, scaleZ; 
    
    boolean GouraudShape, FlatShape, Reflectance;
    
    boolean DirectionalLight, PointLight, AmbientLight;
    
    String ObjectName;

    public void displayObject(String s){
        ObjectName = s;
    }

    public static float ConvertToFloat(String s){
        Float FloatVal=new Float(s);
        return FloatVal.floatValue();
    }

    public void DirectionalLightVal (boolean s){
        DirectionalLight = s;
        Render3D.bDirectionalLight=DirectionalLight;
    }

    public void PointLightVal (boolean s){
        PointLight = s;
        Render3D.bPointLight=PointLight;
    }

    public void AmbientLightVal (boolean s){
        AmbientLight = s;
        Render3D.bAmbientLight=AmbientLight;
    }

    public void ReflectanceVal (boolean s){
        Reflectance = s;
        world.setReflect(Reflectance);
    }

    public void GouraudShapeVal(boolean s){
        GouraudShape = s;
        world.setReflect(Reflectance);
        world.setRenderMode(TriDObject.Gouraud);
    }

    public void FlatShapeVal(boolean s){
        FlatShape = s;
        world.setReflect(Reflectance);
        world.setRenderMode(TriDObject.Flat);
    }

    public void angleVal(String s){
        angle = ConvertToFloat(s);
        angle=(float)(angle*Math.PI/180.);
    }

    public void rotateX1Val(String s){
        rotateX1=ConvertToFloat(s);
    }

    public void rotateY1Val(String s){
        rotateY1=ConvertToFloat(s);
    }

    public void rotateZ1Val(String s){
        rotateZ1=ConvertToFloat(s);
    }

    public void rotateX2Val(String s){
        rotateX2=ConvertToFloat(s);
    }

    public void rotateY2Val(String s){
        rotateY2=ConvertToFloat(s);
    }

    public void rotateZ2Val(String s){
        rotateZ2=ConvertToFloat(s);
    }

    public void translateXVal(String s){
        translateX=ConvertToFloat(s);
    }

    public void translateYVal(String s){
        translateY=ConvertToFloat(s);
    }

    public void translateZVal(String s){
        translateZ=ConvertToFloat(s);
    }

    public void scaleXVal(String s){
        scaleX=ConvertToFloat(s);
    }

    public void scaleYVal(String s){
        scaleY=ConvertToFloat(s);
    }

    public void scaleZVal(String s){
        scaleZ=ConvertToFloat(s);
    }

    public Matrix4x4 DetectObjMatrix(){
        Matrix4x4 ObjModel=new Matrix4x4();
        if(Math.abs(scaleX) < 0.000001 && Math.abs(scaleY) < 0.000001 && 
            Math.abs(scaleZ) < 0.000001){
            showStatus("Invaild Scaling Coefficient, please enter again!");
            return ObjModel;
        }
        ObjModel.translate(rotateX1, rotateY1, rotateZ1);
        ObjModel.rotate(rotateX2-rotateX1, rotateY2-rotateY1, 
            rotateZ2-rotateZ1,angle);
        ObjModel.translate(-rotateX1, -rotateY1, -rotateZ1);
        ObjModel.translate(translateX,translateY, translateZ);
        ObjModel.scale(scaleX, scaleY, scaleZ);

        return ObjModel;
    }

    public void UPDATE(){
        showStatus("success!");
        Matrix4x4 m=DetectObjMatrix();
        world.Operation(m);
        world.Draw();
    }

    public void RELOAD(){
        Reset();
        String filename = ObjectName;
        showStatus("Loading "+filename);
        world.Reset();
        if(!world.FileOpen(getDocumentBase(),filename)){
           showStatus("Error loading "+filename);
        }
        else{
            showStatus(filename+" is successfully loaded!");
        }

        Render3D.bAmbientLight=true;
        Render3D.bDirectionalLight=true;
        Render3D.bPointLight=true;
        world.setReflect(true);
    }

    void Reset()
    {}

    public void init()
    {
        setLayout(null);
        setSize(300,380);
        setBackground(new Color(12632256));
        panel1 = new java.awt.Panel();
        panel1.setLayout(new BorderLayout(0,0));
        panel1.setBounds(0,0,256,256);
        panel1.setBackground(new Color(16777215));
        add(panel1);
    
        world=new Scene();
        panel1.add(world);
        String filename = getParameter("objectfile");
        ObjectName = getParameter("objectfile");
        showStatus("Loading "+filename+ "please wait!");

        if(!world.FileOpen(getDocumentBase(),filename)){
            showStatus("Error loading "+filename);
        }
        else{
            showStatus(filename+" is successfully loaded");
        }

        SymMouse aSymMouse = new SymMouse();
        SymAction lSymAction = new SymAction();

        Reset();
    }

    java.awt.Panel panel1;

    class SymMouse extends java.awt.event.MouseAdapter{
        public void mouseClicked(java.awt.event.MouseEvent event){
            Object object = event.getSource();
        }
    }

    class SymAction implements java.awt.event.ActionListener{
        public void actionPerformed(java.awt.event.ActionEvent event){
            Object object = event.getSource();
        }
    }
}
