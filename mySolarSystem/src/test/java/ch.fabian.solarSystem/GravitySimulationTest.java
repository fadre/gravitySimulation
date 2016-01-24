package ch.fabian.solarSystem;

import javafx.geometry.Point3D;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class GravitySimulationTest {

    private GravitySimulation gravitySimulation;
    private SpaceObject spaceObject1;
    private SpaceObject spaceObject2;

    @Before
    public void setUp() throws Exception {
        spaceObject1 = createObject(0, 0, 0, 10000);
        spaceObject2 = createObject(5, 5, 5, 10E7);
        gravitySimulation = new GravitySimulation(Arrays.asList(spaceObject1, spaceObject2));
    }

    @Test
    public void testComputeGravitationalForce() throws Exception {

        double force = gravitySimulation.computeGravityBetweenBodies(spaceObject1, spaceObject2);

        assertThat(force, is(8.898666666666665));
    }

    @Test
    public void testGravitationalVector1() throws Exception {
        SpaceObject spaceObject1 = createObject(0, 0, 0, 1);
        SpaceObject spaceObject2 = createObject(1, 0, 0, 1);
        Point3D forceVector = new GravitySimulation(Arrays.asList(spaceObject1,spaceObject2)).computeGravityVector(spaceObject1);

        assertThat(forceVector.getX(), is(GravitySimulation.GRAVITY_CONSTANT));
        assertThat(forceVector.getY(), is(0.0));
        assertThat(forceVector.getZ(), is(0.0));
    }

    @Test
    public void testGravitationalVector2() throws Exception {
        SpaceObject spaceObject1 = createObject(0, 0, 0, 1);
        SpaceObject spaceObject2 = createObject(-1, 0, 0, 1);
        Point3D forceVector = new GravitySimulation(Arrays.asList(spaceObject1,spaceObject2)).computeGravityVector(spaceObject1);

        assertThat(forceVector.getX(), is(-GravitySimulation.GRAVITY_CONSTANT));
        assertThat(forceVector.getY(), is(0.0));
        assertThat(forceVector.getZ(), is(0.0));
    }

    @Test
    public void testGravitationalVector3() throws Exception {
        SpaceObject spaceObject1 = createObject(0, 0, 0, 1);
        SpaceObject spaceObject2 = createObject(0.3, 0.3, 0.3, 1);
        Point3D forceVector = new GravitySimulation(Arrays.asList(spaceObject1,spaceObject2)).computeGravityVector(spaceObject1);

        assertThat(forceVector.getX(), is(1.4271243320635414E-9));
        assertThat(forceVector.getY(), is(1.4271243320635414E-9));
        assertThat(forceVector.getZ(), is(1.4271243320635414E-9));
    }

    @Test
    public void testGravitationalVector4() throws Exception {
        SpaceObject spaceObject1 = createObject(0, 0, 0, 5);
        SpaceObject spaceObject2 = createObject(3, 0, 0, 6);
        Point3D forceVector = new GravitySimulation(Arrays.asList(spaceObject1,spaceObject2)).computeGravityVector(spaceObject1);

        assertThat(forceVector.getX(), is(2.2233333333333334e-10));
        assertThat(forceVector.getY(), is(0));
        assertThat(forceVector.getZ(), is(0));
    }

    private SpaceObject createObject(double inX, double inY, double inZ, double inMass) {
        SpaceObject spaceObject1 = new SpaceObject();
        spaceObject1.setLastPosition(new Point3D(inX, inY, inZ));
        spaceObject1.setMass(inMass);
        return spaceObject1;
    }
}