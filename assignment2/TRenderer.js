// Tui Popenoe
// TRenderer.js

function changeSize(pic){
    document.images[pic].width = 45;
    document.images[pic].height = 48;
}

function resetSize(pic){
    document.images[pic].width = 50;
    document.images[pic].height = 53;
}

function update(){
    document.DrawGraphic.reload();
}

function resetVal(){
    document.getElementById('reflectance').checked = true;
    document.getElementById('directionalLight').checked = true;
    document.getElementById('pointLight').checked = true;
    document.getElementById('ambientLight').checked = true;
    document.getElementById('angle').value = 0;
    document.getElementById('rotateX1').value = 0;
    document.getElementById('rotateX2').value = 0;
    document.getElementById('rotateY1').value = 0;
    document.getElementById('rotateY2').value = 0;
    document.getElementById('rotateZ1').value = 1;
    document.getElementById('rotateZ2').value = -1;
    document.getElementById('translateX').value = 0;
    document.getElementById('translateY').value = 0;
    document.getElementById('translateZ').value = 0;
    document.getElementById('scaleX').value = 0;
    document.getElementById('scaleY').value = 0;
    document.getElementById('scaleZ').value = 0;

    document.DrawGraphic.UPDATE();
}

function get3DObject(model){
    document.DrawGraphic.displayObject(model);
    document.shape1.checked = true;
}

function passVal(){
    document.DrawGraphic.angleVal(document.getElementById('angle').value);
    document.DrawGraphic.rotateX1Val(document.getElementById('rotateX1')
        .value);
    document.DrawGraphic.rotateX2Val(document.getElementById('rotateX2')
        .value);
    document.DrawGraphic.rotateY1Val(document.getElementById('rotateY1')
        .value);
    document.DrawGraphic.rotateY2Val(document.getElementById('rotateY2')
        .value);
    document.DrawGraphic.rotateZ1Val(document.getElementById('rotateZ1')
        .value);
    document.DrawGraphic.rotateZ2Val(document.getElementById('rotateZ2')
        .value);
    document.DrawGraphic.translateXVal(document.getElementById('translateX')
        .value);
    document.DrawGraphic.translateYVal(document.getElementById('translateY')
        .value);
    document.DrawGraphic.translateZVal(document.getElementById('translateZ')
        .value);
    document.DrawGraphic.scaleXVal(document.getElementById('scaleX')
        .value);
    document.DrawGraphic.scaleYVal(document.getElementById('scaleY')
        .value);
    document.DrawGraphic.scaleZVal(document.getElementById('scaleZ')
        .value);

    document.DrawGraphic.UPDATE();
}

function reflectancePassVal(){
    document.DrawGraphic.ReflectanceVal(document.getElementById('reflectance').checked);
    document.DrawGraphic.UPDATE();
}

function directionalLightPassVal(){
    document.DrawGraphic.DirectionalLightVal(document.getElementById('directionalLight').checked);
    document.DrawGraphic.UPDATE();
}

function pointLightPassVal(){
    document.DrawGraphic.PointLightVal(document.getElementById('pointLight').checked);
    document.DrawGraphic.UPDATE();
}

function ambientLightPassVal(){
    document.DrawGraphic.AmbientLightVal(
        document.getElementById('ambientLight').checked);
    document.DrawGraphic.UPDATE();
}

function GouraudShapePassVal(){
    if(document.shapeRadio2.checked==true){
        document.DrawGraphic.GouraudShapeVal(
            document.getElementById('shapeRadio2').checked);
        document.DrawGraphic.UPDATE();
    }
}

function FlatShapePassVal(){
    if(document.shapeRadio2.checked==true){
        document.DrawGraphic.FlatShapeVal(
            document.getElementById('shapeRadio1').checked);
        document.DrawGraphic.UPDATE();
    }
}