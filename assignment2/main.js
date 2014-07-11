window.requestAnimationFrame = (function (){
    return window.requestAnimationFrame ||
        window.webkitRequestAnimationFrame ||
        window.mozRequestAnimationFrame ||
        function (callback) {
            window.setTimeout(callback, 1000 / 60);
        };
    })();


var canvas;
var engine;

function init(){
    canvas = document.getElementById('renderCanvas');
    engine = new BABYLON.Engine(canvas, true);
}

window.onload = function(){
    if (BABYLON.Engine.isSupported()){
        
        init();

        BABYLON.SceneLoader.Load('', 'banana.babylon', engine, 
            function(newScene){
                newScene.executeWhenReady(function(){
                    newScene.activeCamera.attachControl(canvas);

                    engine.runRenderLoop(function(){
                        newScene.render();
                    });
                });
            },
            function (progress){

            });
    }
}

function loadObject(filename){
    if (BABYLON.Engine.isSupported()){
        canvas = document.getElementById('renderCanvas');
        if(engine == 'undefined'){
            engine = new BABYLON.Engine(canvas, true);
        }

        BABYLON.SceneLoader.Load('', filename, engine, 
            function(newScene){
                newScene.executeWhenReady(function(){
                    newScene.activeCamera.attachControl(canvas);

                    engine.runRenderLoop(function(){
                        newScene.render();
                    });
                });
            },
            function (progress){

            });
    }
}
/*
function loadJSONCompleted(meshesLoaded){
    meshes = meshesLoaded;

    // Call HTML5 rendering loop
    requestAnimationFrame(drawingLoop);
}

function drawingLoop(){
    var now = Date.now();
    var currentFPS = 1000 / (now - previousDate);
    previousDate = now;

    divCurrentFPS.textContent = currentFPS.toFixed(2);

    if(lastFPSValues.length < 60){
        lastFPSValues.push(currentFPS);
    }
    else{
        lastFPSValues.shift();
        lastFPSValues.push(currentFPS);
        var totalValues = 0;
        for(var i = 0; i < lastFPSValues.length; i++){
            totalValues += lastFPSValues[i];
        }

        var averageFPS = totalValues / lastFPSValues.length;
        divAverageFPS.textContent = averageFPS.toFixed(2);
    }

    device.clear();

    for(var i = 0; i < meshes.length; i++){
        meshes[i].Rotation.y += 0.01;
    }

    // Perform matrix operations
    device.render(mera, meshes);
    // Flush the backbuffer into the front buffer
    device.present();
    // Call the HTML5 loop recursively
    requestAnimationFrame(drawingLoop);
}*/