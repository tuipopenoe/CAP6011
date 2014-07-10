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

                for (var indexVertices = 0; indexVertices < 
                    cMesh.Vertices.length; indexVertices++){
                    // Project the 3D coordinates into 2D space
                    var projectedPoint = 
                        this.project(cMesh.Vertices[indexVertices], 
                            transformMatrix);
                    // Draw point on screen
                    this.drawPoint(projectedPoint);
                }

                for(var i = 0; i < cMesh.Vertices.length -1; i++){
                    var point0 = this.project(cMesh.Vertices[i], 
                        transformMatrix);
                    var point1 = this.project(cMesh.Vertices[i+1], 
                        transformMatrix);
                    this.drawLine(point0, point1);
                }

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
        return Device;
    })();
    SoftEngine.Device = Device;
})(SoftEngine || (SoftEngine = {}));