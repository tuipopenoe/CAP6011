window.onload = function(){
    if (BABYLON.Engine.isSupported()){
        var canvas = document.getElementById('renderCanvas');
        var engine = new BABYLON.Engine(canvas, true);

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