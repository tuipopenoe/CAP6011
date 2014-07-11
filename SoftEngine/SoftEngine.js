var SoftEngine;

(function(SoftEngine){
    // Initialize Camera
    var Camera = (function(){
        function Camera(){
            this.Position = BABYLON.Vector3.Zero();
            this.Target = BABYLON.Vector3.Zero();
        }
        return Camera;
    })();
    
    SoftEngine.Camera = Camera;

    // Initialize Mesh
    var Mesh = (function(){
        function Mesh(name, verticesCount, facesCount){
            this.name = name;
            this.Vertices = new Array(verticesCount);
            this.Faces = new Array(facesCount);
            this.Rotation = new BABYLON.Vector3(0, 0, 0);
            this.Position = new BABYLON.Vector3(0, 0, 0);
        }
        return Mesh;
    })();

    SoftEngine.Mesh = Mesh;

    // Initialize Texture
    var Texture = (function(){
        // Working with a fixed size texture (512x512, 1024x1024)
        function Texture(filename, width, height){
            this.width = width;
            this.height = height;
            this.load(filename);
        }

        Texture.prototype.load = function(filename){
            var _this = this;
            var imageTexture = new Image();
            imageTexture.height = this.height;
            imageTexture.width = this.width;
            imageTexture.onload = function(){
                var internalCanvas = document.createElement('canvas');
                internalCanvas.width = _this.width;
                internalCanvas.height = _this.height;
                var internalContext = internalCanvas.getContext('2d');
                internalContext.drawImage(imageText, 0, 0);
                _this.internalBuffer = internalContext.getImageData(0, 0, 
                    _this.width, _this.height);
            };
            imageTexture.src = filename;
        };

        // Takes the U & V coordinates exported by modelling program
        // and return the corresponding pixel color in the texture
        Texture.prototype.map = function(tu, tv){
            if(this.internalBuffer){
                // using a % operator to cycle/repeat the texture if needed
                var u = Math.abs(((tu * this.width) % this.width));
                var v = Math.abs(((tv * this.height) % this.height));

                var pos = (u + v * this.width) * 4;

                var r = this.internalBuffer.data[pos];
                var g = this.internalBuffer.data[pos + 1];
                var b = this.internalBuffer.data[pos + 2];
                var a = this.internalBuffer.data[pos + 3];

                return new BABYLON.Color4(r / 255.0, g / 255.0, b / 255.0,
                    a / 255.0);
            }
            // Image is not loaded yet
            else{
                return new BABYLON.Color4(1, 1, 1, 1);
            }
        };

        return Texture;
    })();

    SoftEngine.Texture = Texture;

    var Device = (function(){
        // Device Constructor
        function Device(canvas){
            /// The back buffer size is equal to the number of pixels to draw
            // on screen (width * height) * 4 (RGBA values)
            this.workingCanvas = canvas;
            this.workingWidth = canvas.width;
            this.workingHeight = canvas.height;
            this.workingContext = this.workingCanvas.getContext("2d");
            this.depthbuffer = new Array(this.workingWidth * 
                this.workingHeight);
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

            // Clear depth buffer
            for(var i = 0; i < this.depthbuffer.length; i++){
                // Maximize possible value
                this.depthbuffer[i] = 10000000;
            }
        };

        // Called to put a pixel on screen at a specific X, Y coordinates
        Device.prototype.putPixel = function(x, y, z, color){
            this.backbufferdata = this.backbuffer.data;
            // As we have a 1-D Array for our back buffer
            // Need to know the equivalent cell index in 1-D based
            // on the 2D coordinates of the screen
            var index = ((x >> 0) + (y >> 0) * this.workingWidth);
            var index4  = index * 4;

            if(this.depthbuffer[index] < z){
                return;
            }
            
            this.depthbuffer[index] = z;

            // RGBA color space is used by the HTML5 canvas
            this.backbufferdata[index4] = color.r *255;
            this.backbufferdata[index4 + 1] = color.g *255;
            this.backbufferdata[index4 + 2] = color.b *255;
            this.backbufferdata[index4 + 3] = color.a *255;
        };

        // drawPoint calls putPixel but does clipping operation first
        Device.prototype.drawPoint = function(point, color){
            // Clipping what's visible on screen
            if(point.x >= 0 && point.y >= 0 && point.x < this.workingWidth 
                && point.y < this.workingHeight){
                // Draw a point of color
                this.putPixel(point.x, point.y, point.z, color);
            }
        };

        // Once everything is ready, flush back buffer into the front buffer
        Device.prototype.present = function(){
            this.workingContext.putImageData(this.backbuffer, 0, 0);
        };

        // Clamping values to keep them between 0 and 1
        Device.prototype.clamp = function(value, min, max){
            if(typeof min === 'undefined'){
                min = 0;
            }
            if(typeof max === 'undefined'){
                max = 1;
            }
            return Math.max(min, Math.min(value, max));
        };

        // Project takes 3D coordinates and transform them into 2D
        // coordinates using the transformation matrix
        Device.prototype.project = function(vertex, transMat, world){
            // transforming the coordinates
            var point2d = 
                BABYLON.Vector3.TransformCoordinates(vertex.Coordinates,
                    transMat);

            var point3DWorld = BABYLON.Vector3.TransformCoordinates(
                vertex.Coordinates, world);

            var normal3DWorld = BABYLON.Vector3.TransformCoordinates(
                vertex.Normal, world);

            // The transformed coordinates will be based on coordinate system
            // starting on the center of the screen. Screens are normally 
            // drawn from top left, so need to transform them.
            var x = point2d.x * this.workingWidth + this.workingWidth / 2.0;
            var y = -point2d.y *this.workingHeight + this.workingHeight / 2.0;
            return ({
                Coordinates: new BABYLON.Vector3(x, y, point2d.z),
                Normal: normal3DWorld,
                WorldCoordinates: point3DWorld
            });
        };

        // Compute the cosine of the angle between the light vector and the
        // normal vector. REturns a value between 0 and 1
        Device.prototype.computeNDotL = function(vertex, normal, 
            lightPosition){
            var lightDirection = lightPosition.subtract(vertex);

            normal.normalize();
            lightDirection.normalize();

            return Math.max(0, BABYLON.Vector3.Dot(normal, lightDirection));
        }

        // Interpolating the value between 2 vertices
        // min is the starting point and max the ending point
        // gradient is the % between the 2 points
        Device.prototype.interpolate = function(min, max, gradient){
            return min + (max - min) * this.clamp(gradient);
        };

        // Drawing line between 2 points from left to right
        // papb -> pcpd
        // pa, pb, pc, and pd must then be sorted before
        Device.prototype.processScanLine = function(data, va, vb, vc, vd, 
            color, texture){
            var pa = va.Coordinates;
            var pb = vb.Coordinates;
            var pc = vc.Coordinates;
            var pd = vd.Coordinates;

            // Using current Y, and gradient to compute other values
            // if pa.Y == pb.Y or pc.Y == pd.Y, gradient is forced to 1
            var gradient1 = pa.y != pb.y ? (data.currentY - pa.y) / 
                (pb.y - pa.y) : 1;
            var gradient2 = pc.y != pd.y ? (data.currentY - pc.y) / 
                (pd.y - pc.y) : 1;

            var sx = this.interpolate(pa.x, pb.x, gradient1) >> 0;
            var ex = this.interpolate(pc.x, pd.x, gradient2) >> 0;

            var z1 = this.interpolate(pa.z, pb.z, gradient1);
            var z2 = this.interpolate(pc.z, pd.z, gradient2);

            var snl = this.interpolate(data.ndotla, data.ndotlb, gradient1);
            var enl = this.interpolate(data.ndotlc, data.ndotld, gradient2);

            var su = this.interpolate(data.ua, data.ub, gradient1);
            var eu = this.interpolate(data.uc, data.ud, gradient2);
            var sv = this.interpolate(data.va, data.vb, gradient1);
            var ev = this.interpolate(data.vc, data.vd, gradient2);

            for(var x = sx; x < ex; x++){
                var gradient = (x - sx) / (ex - sx);
                var z = this.interpolate(z1, z2, gradient);
        
                var ndotl = this.interpolate(snl, enl, gradient);

                var u = this.interpolate(snl, enl, gradient);
                var v = this.interpolate(sv, ev, gradient);

                var textureColor;

                if(texture){
                    textureColor = texture.map(u, v);
                }
                else{
                    textureColor = new BABYLON.Color4(1, 1, 1, 1);
                }
        
                // Changing the color value using the cosine of the angle
                // between the light vector and the normal vector
                this.drawPoint(new BABYLON.Vector3(x, data.currentY, z),
                    new BABYLON.Color4( color.r * ndotl * textureColor.r,
                                        color.g *ndotl * textureColor.g,
                                        color.b * ndotl * textureColor.b, 1));
            }
        };

        Device.prototype.drawTriangle = function(v1, v2, v3, color){
            // Sorting the points in order to always have this order on screen
            // p1, p2, p3, with p1 always up, then p2 between p1 and p3
            if(v1.Coordinates.y > v2.Coordinates.y){
                var temp = v2;
                v2 = v1;
                v1 = temp;
            }
            if(v2.Coordinates.y > v3.Coordinates.y){
                var temp = v2;
                v2 = v3;
                v3 = temp;
            }
            if(v1.Coordinates.y > v2.Coordinates.y){
                var temp = v2;
                v2 = v1;
                v1 = temp;
            }

            var p1 = v1.Coordinates;
            var p2 = v2.Coordinates;
            var p3 = v3.Coordinates;

            // normal face's vector is the average normal between each vertex's
            // normal computing also the center point of the face
            var vnFace = (v1.Normal.add(v2.Normal.add(v3.Normal))).scale(1/3);
            var centerPoint = (v1.WorldCoordinates.add(v2.WorldCoordinates.add(
                v3.WorldCoordinates))).scale(1/3);
            // light position
            var lightPos = new BABYLON.Vector3(0, 10, 10);
            // computing the cos of the angle between the light vector and the
            // normal vector returns a value between 0 and 1 that will be used
            // as the intensity of the color
        
            var nl1 = this.computeNDotL(v1.WorldCoordinates, v1.Normal, 
                lightPos);
            var nl2 = this.computeNDotL(v2.WorldCoordinates, v2.Normal, 
                lightPos);
            var nl3 = this.computeNDotL(v3.WorldCoordinates, v3.Normal, 
                lightPos);

            var data = {};

            var dP1P2;
            var dP1P3;

            // Compute slopes
            if(p2.y - p1.y > 0){
                dP1P2 = (p2.x - p1.x) / (p2.y - p1.y);
            }
            else{
                dP1P2 = 0;
            }

            if(p3.y - p1.y > 0){
                dP1P3 = (p3.x - p1.x) / (p3.y - p1.y);
            }
            else{
                dP1P3 = 0;
            }

            // P1
            //    p2
            // P3
            if(dP1P2 > dP1P3){
                for(var y = p1.y >> 0; y <= p3.y >> 0; y++){
                    data.currentY = y;

                    if(y < p2.y){
            data.ndotla = nl1;
            data.ndotlb = nl3;
            data.ndotlc = nl1;
            data.ndotld = nl2;
                        this.processScanLine(data, v1, v3, v1, v2, color);
                    }
                    else{
                        data.ndotla = nl1;
            data.ndotlb = nl3;
            data.ndotlc = nl2;
            data.ndotld = nl3;
            this.processScanLine(data, v1, v3, v2, v3, color);
                    }
                }
            }

            //    P1
            // P2
            //    P3
            else{
                for(var y = p1.y >> 0; y <= p3.y >> 0; y++){
                    data.currentY = y;

                    if(y < p2.y){
                        data.ndotla = nl1;
            data.ndotlb = nl2;
            data.ndotlc = nl1;
            data.ndotld = nl3;
            this.processScanLine(data, v1, v2, v1, v3, color);
                    }
                    else{
                        data.ndotla = nl2;
            data.ndotlb = nl3;
            data.ndotlc = nl1;
            data.ndotld = nl3;
            this.processScanLine(data, v2, v3, v1, v3, color);
                    }
                }
            }
        };

        /*Device.prototype.drawLine = function(point0, point1){
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
            var x0 = point0.x;
            var y0 = point0.y;
            var x1 = point1.x;
            var y1 = point1.y;
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
        };*/



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

                    var pixelA = this.project(vertexA, transformMatrix, worldMatrix);
                    var pixelB = this.project(vertexB, transformMatrix, worldMatrix);
                    var pixelC = this.project(vertexC, transformMatrix, worldMatrix);

                    var color = 1.0;
            
                    this.drawTriangle(pixelA, pixelB, pixelC, 
                        new BABYLON.Color4(color, color, color, 1));
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
            var materials = [];

            for(var materialIndex = 0; materialIndex < 
                    jsonObject.materials.length; materialIndex++){
                var material = {};

                material.Name = jsonObject.materials[materialIndex].name;
                material.ID = jsonObject.materials[materialIndex].id;
                if(jsonObject.materials[materialIndex].diffuseTexture){
                    material.DiffuseTextureName = 
                    jsonObject.materials[materialIndex].diffuseTexture.name;
                }
                material[material.ID] = material;
            }

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
                var verticesCount = verticesArray.length / verticesStep;

                // the number of faces is logically the size of the array
                // divided by 3 (A, B, C)
                var facesCount = indicesArray.length / 3;

                var mesh = new SoftEngine.Mesh(
                        jsonObject.meshes[meshIndex].name, verticesCount,
                        facesCount);

                // Fill the Vertices array of mesh first
                for(var index = 0; index < verticesCount; index++){

                    var x = verticesArray[index * verticesStep];
                    var y = verticesArray[index * verticesStep + 1];
                    var z = verticesArray[index * verticesStep + 2];

                    var nx = verticesArray[index * verticesStep +3];
                    var ny = verticesArray[index * verticesStep +4];
                    var nz = verticesArray[index * verticesStep +5];

                    mesh.Vertices[index] = {
                        Coordinates: new BABYLON.Vector3(x, y, z),
                        Normal: new BABYLON.Vector3(nx, ny, nz),
                        WorldCoordinates: null
                    };

                    if(uvCount > 0){
                        // Loading the texture coordinates
                        var u = verticesArray[index * verticesStep + 6];
                        var v = verticesArray[index * verticesStep + 7];
                        mesh.Vertices[index].TextureCoordinates = 
                             new BABYLON.Vector2(u, v);
                    }
                    else{
                        mesh.Vertices[index].TextureCoordinates = 
                             new BABYLON.Vector2(0,0);
                    }
                }

                for(var index = 0; index < facesCount; index++){
                    var a = indicesArray[index *3];
                    var b = indicesArray[index *3 +1];
                    var c = indicesArray[index *3 +2];
                    mesh.Faces[index] = { A: a, B: b, C: c};
                }

                // Get the position set in model editing program
                var position = jsonObject.meshes[meshIndex].position;
                mesh.Position = new BABYLON.Vector3(position[0], position[1],
                    position[2]);

                if(uvCount > 0){
                    var meshTextureID = 
                        jsonObject.meshes[meshIndex].materialID;
                    var meshTextureName = 
                        materials[meshTextureID].DiffuseTextureName;
                    mesh.Texture = new Texture(meshTextureName, 512, 512);
                }
                meshes.push(mesh);
            }

            return meshes;
        };

        return Device;
    })();
    SoftEngine.Device = Device;
})(SoftEngine || (SoftEngine = {}));