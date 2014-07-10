window.requestAnimationFrame = (function (){
    return window.requestAnimationFrame ||
        window.webkitRequestAnimationFrame ||
        window.mozRequestAnimationFrame ||
        function (callback) {
            window.setTimeout(callback, 1000 / 60);
        };
    })();

var canvas;
var device;
var mesh;
var meshes = [];
var mera;

document.addEventListener('DOMContentLoaded', init, false);

function init(){
    canvas = document.getElementById('frontBuffer');
    mera = new SoftEngine.Camera();
    device = new SoftEngine.Device(canvas);
    
    device.LoadJSONFileAsync('monkey.babylon', loadJSONCompleted);
}

function loadJSONCompleted(meshesLoaded){
    meshes = meshesLoaded;

    // Call HTML5 rendering loop
    requestAnimationFrame(drawingLoop);
}

function drawingLoop(){
    device.clear();

    for(var i = 0; i < meshes.length; i++){
        meshes[i].Rotation.x += 0.01;
        meshes[i].Rotation.y += 0.01;
    }

    // Perform matrix operations
    device.render(mera, meshes);
    // Flush the backbuffer into the front buffer
    device.present();

    // Call the HTML5 loop recursively
    requestAnimationFrame(drawingLoop);
}