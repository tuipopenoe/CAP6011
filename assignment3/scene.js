var canvas;
var engine;
var currentScene;
var currentObject;
var currentFPS;
var divCurrentFPS;

window.requestAnimationFrame = (function(){
    return window.requestAnimationFrame ||
        window.webkitRequestAnimationFrame ||
        window.mozRequestAnimationFrame ||
        function(callback){
            window.setTimeout(callback, 1000 / 60);
        };
    })();

window.onload = function(){
    loadScene(createScene());
}

function init(){
    canvas = document.getElementById('renderCanvas');
    engine = new BABYLON.Engine(canvas, true);
    divCurrentFPS = document.getElementById('currentFPS');
}

var createScene = function(){
    var scene = new BABYLON.Scene(engine);

    var camera = new BABYLON.ArcRotateCamera('Camera', 0, Math.PI / 2, 12, 
        BABYLON.Vector3.Zero(), scene);

    var light = new BABYLON.HemisphericLight('hemi', 
        new BABYLON.Vector3(0, 1, 0), scene);

    var object = randomizeObject()
    //BABYLON.Mesh.CreateTorusKnot('mesh', 2, 0.5, 128, 64, 2, 3, 
    //    scene);

    var material = new BABYLON.StandardMaterial('std', scene);
    material.diffuseColor = new BABYLON.Color3(0.5, 0, 0.5);

    object.material = material;

    return scene;
}

var randomizeObject = function(){
    num = Math.floor(Math.random() *(7-1)) + 1;

    switch(num):
        case 1:
            return BABYLON.Mesh.CreateBox('mesh', 6.0, scene);
            break;
        case 2:
            return BABYLON.Mesh.CreateSphere('mesh', 10.0, 3.0, scene);
            break;
        case 3:
            return BABYLON.Mesh.CreatePlane('mesh', 15.0, scene);
            break;
        case 4:
            return BABYLON.Mesh.CreateCylinder('mesh', 3, 3, 3, 6, scene, 
                false);
            break;
        case 5:
            return BABYLON.Mesh.CreateTorus('mesh', 5, 1, 10, scene, false);
            break;
        case 6: 
            return BABYLON.Mesh.CreateGround('mesh', 10.0, 10.0, 10, scene,
                false);
            break;
        case 7:
            return BABYLON.Mesh.CreateTorusKnow('mesh', 2, 0.5, 128, 64, 2, 3,
                scene);
            break;
        default:
         return;
         break;

}