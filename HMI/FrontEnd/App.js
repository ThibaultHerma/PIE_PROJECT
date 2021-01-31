/**
 * MAIN APP FUNCTION
 * This function is executed as a script of the index.html.
 * -it creates the cesium viewer
 * -it loads the terrain
 * -it listens to the websocket and do the following depending of the request
 *  --> display a test popup when a connection test is triggered
 *  --> load the constellation  and the zone when there is a start request
 *  --> update the constellation position when there is a position request
 *  --> unload the constellation at the end
 *
 * @author: PIE CONSTELLATION
 */

(function () {
    "use strict";

    //////////////////////////////////////////////////////////////////////////
    //WEBSOCKET COMMUNICATION
    //////////////////////////////////////////////////////////////////////////
    var ws = new WebSocket('ws://' + location.host);
    ws.onmessage = function (message) {
        try {
            var obj = JSON.parse(message.data);

            switch (obj.request) {
                /**
                 * print hello world each time the app is connected to the server
                 */
                case 'firstConnection':
                    console.log('hello world')
                    break;

                /**
                 * used for a connection test from the java app. It displays a popup if the test
                 * is received and send back a success message.
                 */
                case 'testConnection':
                    console.log('test Connection')
                    toggle_visibility_test_popup();
                    ws.send(JSON.stringify({"request": "testResult", "result": "success"}))

            }
        } catch (err) {
            console.error(err);
        }
    }

    //////////////////////////////////////////////////////////////////////////
    // Creating the Viewer
    //////////////////////////////////////////////////////////////////////////

    // CESIUM TOKEN FROM THEO ACCOUNT
    Cesium.Ion.defaultAccessToken = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJqdGkiOiIwZDBjNTU0NS1kMTgwLTRmMDktYjJkOC00M2Q5MzRkNWEzNTciLCJpZCI6NDI5MDcsImlhdCI6MTYxMTkzOTMxNH0.JZPC-gowFnl1t848IqMr5D6h9Te3L_WHfOgFgkH1z6o';

    var viewer = new Cesium.Viewer('cesiumContainer', {
        scene3DOnly: true,
        selectionIndicator: false,
        baseLayerPicker: false,
        timeline: false,
        animation: false
    });

    viewer._cesiumWidget._creditContainer.style.display = "none";

    //////////////////////////////////////////////////////////////////////////
    // Loading Imagery
    //////////////////////////////////////////////////////////////////////////

    // Remove default base layer
    viewer.imageryLayers.remove(viewer.imageryLayers.get(0));

    // Add Sentinel-2 imagery
    viewer.imageryLayers.addImageryProvider(new Cesium.IonImageryProvider({assetId: 3954}));
    //viewer.imageryLayers.addImageryProvider(new Cesium.IonImageryProvider({ assetId: 2 }));

    //////////////////////////////////////////////////////////////////////////
    // Loading Terrain
    //////////////////////////////////////////////////////////////////////////

    // Load Cesium World Terrain
    viewer.terrainProvider = Cesium.createWorldTerrain({
        requestWaterMask: true, // required for water effects
        requestVertexNormals: true // required for terrain lighting
    });
    // Enable depth testing so things behind the terrain disappear.
    viewer.scene.globe.depthTestAgainstTerrain = true;

    //////////////////////////////////////////////////////////////////////////
    // Configuring the Scene
    //////////////////////////////////////////////////////////////////////////

    // Enable lighting based on sun/moon positions
    viewer.scene.globe.enableLighting = true;

    // Create an initial camera view
    var initialPosition = new Cesium.Cartesian3.fromDegrees(-73.998114468289017509, 40.674512895646692812, 12000000);
    var initialOrientation = new Cesium.HeadingPitchRoll.fromDegrees(180, 270, 180);
    var homeCameraView = {
        destination: initialPosition,
        orientation: {
            heading: initialOrientation.heading,
            pitch: initialOrientation.pitch,
            roll: initialOrientation.roll
        }
    };
    // Set the initial view
    viewer.scene.camera.setView(homeCameraView);

    // Add some camera flight animation options
    homeCameraView.duration = 2.0;
    homeCameraView.maximumHeight = 2000;
    homeCameraView.pitchAdjustHeight = 2000;
    homeCameraView.endTransform = Cesium.Matrix4.IDENTITY;
    // Override the default home button
    viewer.homeButton.viewModel.command.beforeExecute.addEventListener(function (e) {
        e.cancel = true;
        viewer.scene.camera.flyTo(homeCameraView);
    });

    // Set up clock and timeline.
    /*viewer.clock.shouldAnimate = true; // default
    viewer.clock.startTime = Cesium.JulianDate.fromIso8601("2017-07-11T16:00:00Z");
    viewer.clock.stopTime = Cesium.JulianDate.fromIso8601("2017-07-14T16:20:00Z");
    viewer.clock.currentTime = Cesium.JulianDate.fromIso8601("2017-07-11T16:00:00Z");
    viewer.clock.multiplier = 2; // sets a speedup
    viewer.clock.clockStep = Cesium.ClockStep.SYSTEM_CLOCK_MULTIPLIER; // tick computation mode
    viewer.clock.clockRange = Cesium.ClockRange.LOOP_STOP; // loop at the end
    viewer.timeline.zoomTo(viewer.clock.startTime, viewer.clock.stopTime); // set visible range*/


}());
