var canvas;
var engine;
var currentScene;
var currentObject;
var currentFPS;
var divCurrentFPS;
var previousDate;
var now;
var lastFPSValues = new Array(60);

window.requestAnimationFrame = (function (){
    return window.requestAnimationFrame ||
        window.webkitRequestAnimationFrame ||
        window.mozRequestAnimationFrame ||
        function (callback) {
            window.setTimeout(callback, 1000 / 60);
        };
    })();

window.onload = function(){
    loadScene('banana.babylon');
}

function init(){
    canvas = document.getElementById('renderCanvas');
    engine = new BABYLON.Engine(canvas, true);
    divCurrentFPS = document.getElementById('currentFPS');
    previousDate = Date.now();
}

function loadScene(sceneName){
    if (BABYLON.Engine.isSupported()){
        
        init();

        BABYLON.SceneLoader.Load('', sceneName, engine, 
            function(newScene){
                newScene.executeWhenReady(function(){

                    currentScene = newScene;
                    currentScene.activeCamera.attachControl(canvas);
                    currentObject = newScene.meshes[0];

                    engine.runRenderLoop(function(){
                        currentObject.rotation.y += 0.002;
                        calcFPS();
                        newScene.render();
                    });
                });
            },
            function (progress){

            });
    }
}

function calcFPS(){
    now = Date.now();
    currentFPS = 1000 / (now - previousDate);
    if(currentFPS > 60){
        currentFPS = 60;
    }
    previousDate = now;

    divCurrentFPS.textContent = currentFPS.toFixed(2);
}

function scaleCurrentObject(x, y, z){
    currentObject.scaling.x = x;
    currentObject.scaling.y = y;
    currentObject.scaling.z = z;
}

function scale(){
    var x = document.getElementById('scaleX').value;
    var y = document.getElementById('scaleY').value;
    var z = document.getElementById('scaleZ').value;
    scaleCurrentObject(x, y, z);
}

function translateCurrentObject(x, y, z){
    currentObject.position.x = x;
    currentObject.position.y = y;
    currentObject.position.z = z;
}

function trans(){
    var x = document.getElementById('transX').value;
    var y = document.getElementById('transY').value;
    var z = document.getElementById('transZ').value;
    translateCurrentObject(x, y, z);
}

function rotateCurrentObject(x, y, z){
    currentObject.rotation.x = Math.PI/x;
    currentObject.rotation.y = Math.PI/y;
    currentObject.rotation.z = Math.PI/z;
}

function rotate(){
    var x = document.getElementById('rotX').value;
    var y = document.getElementById('rotY').value;
    var z = document.getElementById('rotZ').value;
    rotateCurrentObject(x, y, z);
}

function addPointLight(){
    var light0 = new BABYLON.PointLight('Omni0', new BABYLON.Vector3(1, 10, 1),
        currentScene);
    light0.diffuse = new BABYLON.Color3(1, 0, 0);
    light0.specular = new BABYLON.Color3(1, 1, 1);
}

function addDirectionalLight(){
    var light0 = new BABYLON.DirectionalLight("Dir0", 
        new BABYLON.Vector3(0, -1, 0), currentScene);
    light0.diffuse = new BABYLON.Color3(1, 0, 0);
    light0.specular = new BABYLON.Color3(1, 1, 1);
}

function addAmbientLight(){
    var light0 = new BABYLON.HemisphericLight("Hemi0", 
        new BABYLON.Vector3(0, 1, 0), currentScene);
    light0.diffuse = new BABYLON.Color3(1, 1, 1);
    light0.specular = new BABYLON.Color3(1, 1, 1);
    light0.groundColor = new BABYLON.Color3(0, 0, 0);
}