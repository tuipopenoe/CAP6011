var canvas;
var engine;
var camera;
var light;
var scene;
var object;
var material;

var previousDate = Date.now();
var now;
var currentFPS;
var divCurrentFPS;
var renderContent;

window.requestAnimationFrame = (function(){
    return window.requestAnimationFrame ||
        window.webkitRequestAnimationFrame ||
        window.mozRequestAnimationFrame ||
        function(callback){
            window.setTimeout(callback, 1000 / 60);
        };
    })();

window.onload = function(){
    createScene();
}

function init(){
    canvas = document.getElementById('renderCanvas');
    renderContent = document.getElementById('renderContent');
    engine = new BABYLON.Engine(canvas, true);
    divCurrentFPS = document.getElementById('currentFPS');
    if(typeof object != 'undefined'){
            object.rotation.y = 0;
        object.rotation.x = 0;
    }
}

var createScene = function(){
    if(BABYLON.Engine.isSupported()){
        init();
    }
    scene = new BABYLON.Scene(engine);

    camera = new BABYLON.ArcRotateCamera('Camera', 0, Math.PI / 2, 12, 
        BABYLON.Vector3.Zero(), scene);

    light = new BABYLON.HemisphericLight('hemi', 
        new BABYLON.Vector3(0, 1, 0), scene);

    object = randomizeObject()
        /*BABYLON.Mesh.CreateTorusKnot('mesh', 2, 0.5, 128, 64, 2, 3, 
           scene);*/

    material = new BABYLON.StandardMaterial('std', scene);
    material.diffuseColor = randomColor(); //new BABYLON.Color3(0.5, 0, 0.5);
    material.backFaceCulling = false;

    object.material = material;

    engine.runRenderLoop(function(){
        object.rotation.y += 0.002;
        object.rotation.x += 0.002;
        scene.render();
        previousDate = Date.now();
    }, calcFPS());
    //return scene;
}

var rSize = function(min, max, mod){
    return Math.floor(Math.random() * (max - min)) + 1 + mod;
}

// Return Random Color
var randomColor = function(){
    return new BABYLON.Color3(Math.random(), Math.random(),
        Math.random(), Math.random());
}
var randomizeObject = function(){
    num = Math.floor(Math.random() *(8-1)) + 1;

    switch(num){
        case 1:
            renderContent.textContent = 'Box';
            return BABYLON.Mesh.CreateBox('mesh', rSize(2, 6, 3), scene);
            break;
        case 2:
            renderContent.textContent = 'Sphere';
            return BABYLON.Mesh.CreateSphere('mesh', rSize(20, 30, 3), 
                rSize(1, 3, 2), scene);
            break;
        case 3:
            renderContent.textContent = 'Plane';
            return BABYLON.Mesh.CreatePlane('mesh', rSize(5, 13, 1), scene);
            break;
        case 4:
            renderContent.textContent = 'Cylinder';
            var i = rSize(6, 8, 0);
            return BABYLON.Mesh.CreateCylinder('mesh', 
                rSize(5, 10, 1),
                i,
                i,
                rSize(12, 40, 0), scene, 
                false);
            break;
        case 5:
            renderContent.textContent = 'Small Torus';
            return BABYLON.Mesh.CreateTorus('mesh', 
                rSize(5, 10, 0),
                rSize(1, 3, 0),
                rSize(10, 20, 0),
                 scene, false);
            break;
        case 6: 
            renderContent.textContent = 'Large Torus';
            return BABYLON.Mesh.CreateTorus('mesh', 
                rSize(8, 12, 0), 
                rSize(1.5, 3, 0),
                rSize(20, 40, 0), scene,
                false);
            break;
        case 7:
            renderContent.textContent = 'Torus Knot';
            return BABYLON.Mesh.CreateTorusKnot('mesh', 2, 0.5, 128, 64, 2, 3,
                scene);
            break;
        default:
            renderContent.textContent = 'Default Torus'
            return BABYLON.Mesh.CreateTorus('mesh', 
                rSize(5, 6, 0),
                rSize(1, 2, 0),
                rSize(10, 16, 0),
                 scene, false);
            break;
    }
}

var calcFPS = function(){
    var now = Date.now();
    var currentFPS = 1000 / (now - previousDate);
    previousDate = now;

    divCurrentFPS.textContent = currentFPS.toFixed(2);
}