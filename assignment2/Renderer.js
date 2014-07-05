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
    document.GraphicForm.RotateX1.value = 0
}