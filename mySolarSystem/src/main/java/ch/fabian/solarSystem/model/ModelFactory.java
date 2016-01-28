package ch.fabian.solarSystem.model;

import javafx.geometry.Point3D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModelFactory {

    public List<SpaceObject> create4Objects() {
        SpaceObject object1 = createObject(-5, -5, 0, 5000);
        object1.setLastSpeed(new Point3D(0.01, 0, 0.01));
        SpaceObject object2 = createObject(5, -5, 0, 5000);
        object2.setLastSpeed(new Point3D(0.01, 0.01, 0));
        SpaceObject object3 = createObject(0, 0, 0, 5000000);
        object3.setLastSpeed(new Point3D(0, 0, 0));
        SpaceObject object4 = createObject(5, 5, 0, 5000);
        object4.setLastSpeed(new Point3D(0.02, -0.015, 0.015));
        return Arrays.asList(object1, object2, object3, object4);
    }

    public List<SpaceObject> createManyObjects() {
        List<SpaceObject> spaceObjects = new ArrayList<>();
        for (int i = -100; i <= 100; i += 40) {
            for (int j = -100; j <= 100; j += 40) {
                for (int k = -100; k <= 100; k += 40) {
                    SpaceObject spaceObject1 = new SpaceObject();
                    spaceObject1.setLastPosition(new Point3D(i, j, k));
                    spaceObject1.setMass(10E7);
                    spaceObject1.setLastSpeed(new Point3D(Math.random() * 0.1 - 0.05, Math.random() * 0.1 - 0.05, Math.random() * 0.1 - 0.05));
                    spaceObjects.add(spaceObject1);
                }
            }
        }
        SpaceObject spaceObject1 = createObject(new Point3D(50, 0, -50), 10E9, new Point3D(0.1, 0.0,-0.1));
        SpaceObject spaceObject2 = createObject(new Point3D(-50, -0.1, 50), 10E9, new Point3D(0.1, 0.0, 0.1));
        spaceObjects.add(spaceObject1);
        spaceObjects.add(spaceObject2);
        return spaceObjects;
    }


    private SpaceObject createObject(int inX, int inY, int inZ, int inMass) {
        SpaceObject spaceObject = new SpaceObject();
        spaceObject.setLastPosition(new Point3D(inX, inY, inZ));
        spaceObject.setMass(inMass);
        return spaceObject;
    }

    private SpaceObject createObject(Point3D inLastPosition, double inMass, Point3D inLastSpeed) {
        SpaceObject spaceObject1 = new SpaceObject();
        spaceObject1.setLastPosition(inLastPosition);
        spaceObject1.setMass(inMass);
        spaceObject1.setLastSpeed(inLastSpeed);
        return spaceObject1;
    }
}
