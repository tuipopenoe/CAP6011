function changeSize(pic){
    document.images[pic].width = 45;
    document.images[pic].height = 53;
}

function resetSize(pic){
    document.images[pic].width = 50;
    document.images[pic].height = 53;
}

function update(){
    document.DrawGraphic.reload();
}

function resetVal(){
    document.GraphicForm.reflectance.checked = true;
    document.GraphicForm.directionalLight.checked = true;
    document.GraphicForm.pointLight.checked = true;
    document.GraphicForm.ambientLight.checked = true;
    document.GraphicForm.Angle.value = 0;
    document.GraphicForm.RotateX1.value = 0;
    document.GraphicForm.RotateX2.value = 0;
    document.GraphicForm.RotateY1.value = 0;
    document.GraphicForm.RotateY2.value = 0;
    document.GraphicForm.RotateZ1.value = 0;
    document.GraphicForm.RotateZ2.value = 0;
    document.GraphicForm.TranslateX.value = 0;
    document.GraphicForm.TranslateY.value = 0;
    document.GraphicForm.TranslateZ.value = 0;
    document.GraphicForm.ScaleX.value = 0;
    document.GraphicForm.ScaleY.value = 0;
    document.GraphicForm.ScaleZ.value = 0;

    document.DrawGraphic.UPDATE();
}

function get3DObject(model){
    document.GraphicForm.displayObject(model);
    document.GraphicForm.shape[1].checked = true;
}

function passVal(){
    document.DrawGraphic.angleVal(document.GraphicForm.Angle.value);
    document.DrawGraphic.rotateX1Val(document.GraphicForm.RotateX1.value);
    document.DrawGraphic.rotateX2Val(document.GraphicForm.RotateX2.value);
    document.DrawGraphic.rotateY1Val(document.GraphicForm.RotateY1.value);
    document.DrawGraphic.rotateY2Val(document.GraphicForm.RotateY2.value);
    document.DrawGraphic.rotateZ1Val(document.GraphicForm.RotateZ1.value);
    document.DrawGraphic.rotateZ2Val(document.GraphicForm.RotateZ2.value);
    document.DrawGraphic.translateXVal(document.GraphicForm.TranslateX.value);
    document.DrawGraphic.translateYVal(document.GraphicForm.TranslateY.value);
    document.DrawGraphic.translateZVal(document.GraphicForm.TranslateZ.value);
    document.DrawGraphic.scaleXVal(document.GraphicForm.ScaleX.value);
    document.DrawGraphic.scaleYVal(document.GraphicForm.ScaleY.value);
    document.DrawGraphic.scaleZVal(document.GraphicForm.ScaleZ.value);

    document.DrawGraphic.UPDATE();
}

function reflectancePassVal(){
    document.DrawGraphic.ReflectanceVal(document.GraphicForm.reflectance.checked);
    document.DrawGraphic.UPDATE();
}

function directionalLightPassVal(){
    document.DrawGraphic.DirectionalLightVal(document.GraphicForm.directionalLight.checked);
    document.DrawGraphic.UPDATE();
}

function pointLightPassVal(){
    document.DrawGraphic.PointLightVal(document.GraphicForm.pointLight.checked);
    document.DrawGraphic.UPDATE();
}

function ambientLightPassVal(){
    document.DrawGraphic.AmbientLightVal(document.GraphicForm.ambientLight.checked);
    document.DrawGraphic.UPDATE();
}

function GouraudShapePassVal(){
    if(document.GraphicForm.shape[1].checked==true){
        document.DrawGraphic.GouraudShapeVal(document.GraphicForm.shape[1].checked);
        document.DrawGraphic.UPDATE();
    }
}

function FlatShapePassVal(){
    if(document.GraphicForm.shape[0].checked==true){
        document.DrawGraphic.FlatShapeVal(document.GraphicForm.shape[0].checked);
        document.DrawGraphic.UPDATE();
    }
}