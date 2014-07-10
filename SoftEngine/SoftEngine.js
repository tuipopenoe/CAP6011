var SoftEngine;

(function(SoftEngine){
    var Camera = (function(){
        function Camera(){
            this.Position = BABYLON.Vector3.Zero();
            this.Target = BABYLON.Vector3.Zero();
        }
        return Camera;
    })();
    
    SoftEngine.Camera = Camera;

    var Mesh = (function(){
        function Mesh(name, verticesCount, facesCount){
            this.name = name;
            this.Vertices = new Array(verticesCount);
            this.Faces = new Array(facesCount)
            this.Rotation = BABYLON.Vector3.Zero();
            this.Position = BABYLON.Vector3.Zero();
        }
        return Mesh;
    })();

    SoftEngine.Mesh = Mesh;

    var Device = (function(){
        function Device(canvas){
            /// The back buffer size is equal to the number of pixels to draw
            // on screen (width * height) * 4 (RGBA values)
            this.workingCanvas = canvas;
            this.workingWidth = canvas.width;
            this.workingHeight = canvas.height;
            this.workingContext = this.workingCanvas.getContext("2d");
        }

        //This function called to clear the back buffer with a specific color
        Device.prototype.clear = function(){
            // Clearing with black color by default
            this.workingContext.clearRect(0, 0, this.workingWidth, 
                this.workingHeight);

            // Once cleared with black pixels, get associated image data to
            // clear out the back buffer
            this.backbuffer = this.workingContext.getImageData(0, 0, 
                this.workingWidth, this.workingHeight);
        };

        // Once everything is ready, flush back buffer into the front buffer
        Device.prototype.present = function(){
            this.workingContext.putImageData(this.backbuffer, 0, 0);
        };

        // Called to put a pixel on screen at a specific X, Y coordinates
        Device.prototype.putPixel = function(x, y, color){
            this.backbufferdata = this.backbuffer.data;
            // As we have a 1-D Array for our back buffer
            // Need to know the equivalent cell index in 1-D based
            // on the 2D coordinates of the screen
            var index = ((x >> 0) + (y >> 0) *this.workingWidth) * 4;

            // RGBA color space is used by the HTML5 canvas
            this.backbufferdata[index] = color.r *255;
            this.backbufferdata[index+1] = color.g *255;
            this.backbufferdata[index+2] = color.b *255;
            this.backbufferdata[index+3] = color.a *255;
        };

        // Project takes 3D coordinates and transform them into 2D
        // coordinates using the transformation matrix
        Device.prototype.project = function(coord, transMat){
            var point = BABYLON.Vector3.TransformCoordinates(coord, transMat);
            // The transformed coordinates will be based on coordinate system
            // starting on the center of the screen. Screens are normally 
            // drawn from top left, so need to transform them.
            var x = point.x * this.workingWidth + this.workingWidth / 2.0 >>0;
            var y = -point.y *this.workingHeight + this.workingHeight /2.0>>0;
            return (new BABYLON.Vector2(x,y));
        };

        // drawPoint calls putPixel but does clipping operation first
        Device.prototype.drawPoint = function(point){
            // Clipping what's visible on screen
            if(point.x >= 0 && point.y >= 0 && point.x < this.workingWidth 
                && point.y < this.workingHeight){
                // Drawing a yellow point
                this.putPixel(point.x,point.y, new BABYLON.Color4(1, 1, 0, 1));
            }
        };

        Device.prototype.drawLine = function(point0, point1){
            var dist = point1.subtract(point0).length();

            // If the distance is less than 2 pixels
            if(dist < 2){
                return;
            }

            // Find the midpoint
            var midpoint = point0.add((point1.subtract(point0)).scale(0.5));
            // Draw the point on screen
            this.drawPoint(midpoint);
            // Recursive algorithm between first and midpoint and between 
            // midpoint and second point
            this.drawLine(point0, midpoint);
            this.drawLine(midpoint, point1);
        };

        Device.prototype.drawBline = function(point0, point1){
            var x0 = point0.x >> 0;
            var y0 = point0.y >> 0;
            var x1 = point1.x >> 0;
            var y1 = point1.y >> 0;
            var dx = Math.abs(x1-x0);
            var dy = Math.abs(y1-y0);
            var sx = (x0 < x1) ? 1 : -1;
            var sy = (y0 < y1) ? 1 : -1;
            var err = dx -dy;
            while(true){
                this.drawPoint(new BABYLON.Vector2(x0, y0));
                if((x0 == x1) && (y0 == y1)){
                    break;
                }
                var e2 = 2 * err;
                if(e2 > -dy) {
                    err -= dy;
                    x0 += sx;
                }
                if(e2 < dx){
                    err += dx;
                    y0 += sy;
                }
            }
        };

        // The main method of the engine that re-compute each vertex projection
        // during each frame
        Device.prototype.render = function(camera, meshes){
            var viewMatrix = BABYLON.Matrix.LookAtLH(camera.Position, 
                camera.Target, BABYLON.Vector3.Up());
            var projectionMatrix = BABYLON.Matrix.PerspectiveFovLH(0.78,
                this.workingWidth / this.workingHeight, 0.01, 1.0);

            for (var index = 0; index < meshes.length; index++){
                // current mesh to work on 
                var cMesh = meshes[index];
                // Apply rotation before translation
                var worldMatrix = BABYLON.Matrix.RotationYawPitchRoll(
                    cMesh.Rotation.y, cMesh.Rotation.x, cMesh.Rotation.z)
                    .multiply(BABYLON.Matrix.Translation(
                        cMesh.Position.x, cMesh.Position.y, cMesh.Position.z));

                var transformMatrix = worldMatrix.multiply(viewMatrix)
                    .multiply(projectionMatrix);

                for(var indexFaces = 0; indexFaces < cMesh.Faces.length; 
                    indexFaces++){
                    var currentFace = cMesh.Faces[indexFaces];
                    var vertexA = cMesh.Vertices[currentFace.A];
                    var vertexB = cMesh.Vertices[currentFace.B];
                    var vertexC = cMesh.Vertices[currentFace.C];

                    var pixelA = this.project(vertexA, transformMatrix);
                    var pixelB = this.project(vertexB, transformMatrix);
                    var pixelC = this.project(vertexC, transformMatrix);

                    this.drawBline(pixelA, pixelB);
                    this.drawBline(pixelB, pixelC);
                    this.drawBline(pixelC, pixelA);
                }
            }
        };

        // Loading the JSON file in an asynchronous manner and calling
        // back with the function passed providing the array of meshes loaded
        Device.prototype.LoadJSONFileAsync = function(fileName, callback){
            var jsonObject = {};
            var xmlhttp = new XMLHttpRequest();
            xmlhttp.open('GET', fileName, true);
            var that = this;
            xmlhttp.onreadystatechange = function(){
                if(xmlhttp.readyState == 4 && xmlhttp.status == 200){
                    jsonObject = JSON.parse(xmlhttp.responseText);
                    callback(that.CreateMeshesFromJSON(jsonObject));
                }
            };
            xmlhttp.send(null);
        };

        Device.prototype.CreateMeshesFromJSON = function(jsonObject){
            var meshes = [];

            for(var meshIndex = 0; meshIndex < jsonObject.meshes.length;
                meshIndex++){

                var verticesArray = jsonObject.meshes[meshIndex].vertices;

                // Faces
                var indicesArray = jsonObject.meshes[meshIndex].indices;

                var uvCount = jsonObject.meshes[meshIndex].uvCount;
                var verticesStep = 1;

                // Depending on the number of texture's coordinates per vertex
                // jump in the vertices array by 6, 8 and 10 windows frame
                switch(uvCount){
                    case 0:
                        verticesStep = 6;
                        break;
                    case 1:
                        verticesStep = 8;
                        break;
                    case 2:
                        verticesStep = 10;
                        break;
                }

                // the number of interesting vertices information for us
                var verticesCount = verticesArray.length / veticesStep;

                // the number of faces is logically the size of the array
                // divided by 3 (A, B, C)
                var facesCount = indices.length / 3;

                var mesh = new SoftEngine.Mesh(
                        jsonObject.meshes[meshIndex].name, verticesCount,
                        facesCount);

                // Fill the Vertices array of mesh first
                for(var index = 0; index < verticesCount; index++){

                    var x = verticesArray[index * verticesStep];
                    var y = verticesArray[index * verticesStep + 1];
                    var z = verticesArray[index * verticesStep + 2];
                    mesh.Vertices[index] = new BABYLON.Vector3(x, y, z);
                }

                for(var index = 0; index < facesCount; index++){
                    var a = indicesArray[index *3];
                    var b = indicesArray[index *3 +1];
                    var c = indicesArray[index *3 +2];
                    mesh.Faces[index] = { A: a, B: b, C: c};
                }

                // Get the position set in model editing program
                var position = jsonObject.meshes[meshIndex].position;
                mesh.position = new BABYLON.Vector3(position[0], position[1],
                    position[2]);
                meshes.push(mesh);
            }

            return meshes;
        };

        return Device;
    })();
    SoftEngine.Device = Device;
})(SoftEngine || (SoftEngine = {}));