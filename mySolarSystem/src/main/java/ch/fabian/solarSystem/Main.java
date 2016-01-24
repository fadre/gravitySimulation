package ch.fabian.solarSystem;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Sphere;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class Main extends Application {

    private Group simulation3d;
    private GravitySimulation currentSimulation;
    private AnimationTimer animationTimer;
    private ControlsController controlsController;
    private SimulationRunner simulationRunner;

    @Override
    public void start(Stage primaryStage) throws Exception {
        HBox hBox = new HBox();
        SubScene content = createContent();
        Pane contentPane = new Pane(content);
        Pane controls = buildControls();
        hBox.getChildren().addAll(controls, contentPane);

        Scene scene = new Scene(hBox);
        CameraCreator cameraCreator = new CameraCreator();
        Camera camera = cameraCreator.createCamera();
        content.setCamera(camera);
        cameraCreator.handleSceneMouseEvents(scene, camera);

        content.heightProperty().bind(scene.heightProperty());
        content.widthProperty().bind(scene.widthProperty());
        primaryStage.setScene(scene);
        primaryStage.setHeight(800);
        primaryStage.setWidth(1500);
        primaryStage.show();
    }

    private SubScene createContent() {
        simulationRunner = createSimulation();
        List<ViewObject> shapes = createShapes(simulationRunner);

        // Build the Scene Graph
        PointLight pointLight = new PointLight(Color.BEIGE);
        pointLight.setTranslateY(50);
        pointLight.setTranslateZ(50);
        pointLight.setTranslateX(50);

        simulation3d = new Group();
        simulation3d.getChildren().addAll(pointLight);
        simulation3d.getChildren().addAll(shapes.stream().map(ViewObject::getShape).collect(Collectors.toList()));
        simulation3d.getChildren().addAll(new AxisCreator().buildAxes());
        SubScene simulationScene = new SubScene(simulation3d, 200, 200);

        simulationScene.setFill(Color.GREY);

        startSimulation(simulationRunner, shapes);
        return simulationScene;
    }

    private void startSimulation(final SimulationRunner simulationRunner, List<ViewObject> shapes) {
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                System.out.println("Animation frame: " + now);
                shapes.forEach(s -> {
                            Shape3D shape = s.getShape();
                            SpaceObject spaceObject = s.getSpaceObject();
                            Point3D lastPosition = spaceObject.getLastPosition();
                            shape.setTranslateX(lastPosition.getX());
                            shape.setTranslateY(lastPosition.getY());
                            shape.setTranslateZ(lastPosition.getZ());
                        }
                );
            }
        };
        animationTimer.start();
        simulationRunner.startSimulation();
    }

    private List<ViewObject> createShapes(SimulationRunner simulationRunner) {
        List<ViewObject> viewObjects = new ArrayList<>();
        simulationRunner.getGravitySimulation().getObjects().forEach(o -> {
                    Shape3D shape = new Sphere(o.getRadius());
                    Point3D lastPosition = o.getLastPosition();
                    shape.setTranslateX(lastPosition.getX());
                    shape.setTranslateY(lastPosition.getY());
                    shape.setTranslateZ(lastPosition.getZ());
                    viewObjects.add(new ViewObject(o, shape));
                }
        );
        return viewObjects;
    }

    private Pane buildControls() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("controls.fxml"));
        Pane myPane = loader.load();
        controlsController = loader.getController();
        controlsController.setCurrentSimulation(currentSimulation);
        controlsController.addResetListener(this::resetScene);
        return myPane;
    }

    private void resetScene() {
        animationTimer.stop();
        simulationRunner.stop();
        simulation3d.getChildren().removeAll(simulation3d.getChildren().stream().filter(c -> c instanceof Shape3D).collect(toList()));
        simulationRunner = createSimulation();
        controlsController.setCurrentSimulation(currentSimulation);

        List<ViewObject> shapes = createShapes(simulationRunner);
        simulation3d.getChildren().addAll(shapes.stream().map(ViewObject::getShape).collect(Collectors.toList()));
        startSimulation(simulationRunner, shapes);
    }

    private SimulationRunner createSimulation() {
        List<SpaceObject> spaceObjects = createMany();
//        List<SpaceObject> spaceObjects = create4();

        currentSimulation = new GravitySimulation(spaceObjects);
        return new SimulationRunner(currentSimulation);
    }

    private List<SpaceObject> create4() {
        SpaceObject object1 = createObject(-5, -5, 0, 500000);
        object1.setLastSpeed(new Point3D(0.01, 0, 0.01));
        SpaceObject object2 = createObject(5, -5, 0, 50000);
        object2.setLastSpeed(new Point3D(0.01, 0.01, 0));
        SpaceObject object3 = createObject(0, 0, 0, 5000000);
        object3.setLastSpeed(new Point3D(0, 0, 0));
        SpaceObject object4 = createObject(5, 5, 0, 50000);
        object4.setLastSpeed(new Point3D(0.02, -0.015, 0.015));
        return Arrays.asList(object1, object2, object3, object4);
    }

    private SpaceObject createObject(int inX, int inY, int inZ, int inMass) {
        SpaceObject spaceObject = new SpaceObject();
        spaceObject.setLastPosition(new Point3D(inX, inY, inZ));
        spaceObject.setMass(inMass);
        return spaceObject;
    }


    private List<SpaceObject> createMany() {
        List<SpaceObject> spaceObjects = new ArrayList<>();
        for (int i = -100; i < 100; i += 20) {
            for (int j = -100; j < 100; j += 20) {
                for (int k = -100; k < 100; k += 20) {
                    SpaceObject spaceObject1 = new SpaceObject();
                    spaceObject1.setLastPosition(new Point3D(i, j, k));
                    spaceObject1.setMass(10E7);
                    spaceObject1.setLastSpeed(new Point3D(Math.random() * 0.1 - 0.05, Math.random() * 0.1 - 0.05, Math.random() * 0.1 - 0.05));
                    spaceObjects.add(spaceObject1);
                }
            }
        }
        return spaceObjects;
    }

    public static void main(String[] args) {
        launch(args);
    }

}
