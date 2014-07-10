var canvas;
var device;
var mesh;
var meshes = [];
var mera;

document.addEventListener('DOMContentLoaded', init, false);

function init(){
    canvas = document.getElementById('frontBuffer');
    mesh = new SoftEngine.Mesh('Cube', 8);
    meshes.push(mesh);
    mera = new SoftEngine.Camera();
    device = new SoftEngine.Device(canvas);

    mesh.Vertices[0] = new BABYLON.Vector3(-1, 1, 1);
    mesh.Vertices[1] = new BABYLON.Vector3(1, 1, 1);
    mesh.Vertices[2] = new BABYLON.Vector3(-1, -1, 1);
    mesh.Vertices[3] = new BABYLON.Vector3(-1, -1, -1);
    mesh.Vertices[4] = new BABYLON.Vector3(-1, 1, -1);
    mesh.Vertices[5] = new BABYLON.Vector3(1, 1, -1);
    mesh.Vertices[6] = new BABYLON.Vector3(1, -1, 1);
    mesh.Vertices[7] = new BABYLON.Vector3(1, -1, -1);

    mera.Position = new BABYLON.Vector3(0, 0, 10);
    mera.Target = new BABYLON.Vector3(0, 0, 0);

    // Call HTML5 rendering loop
    requestAnimationFrame(drawingLoop);
}

function drawingLoop(){
    device.clear();

    // rotating the cube slightly each frame
    mesh.Rotation.x += 0.01;
    mesh.Rotation.y += 0.01;

    // Perform matrix operations
    device.render(mera, meshes);
    // Flush the backbuffer into the front buffer
    device.present();

    // Call the HTML5 loop recursively
    requestAnimationFrame(drawingLoop);
}