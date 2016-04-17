package ch.fabian.solarSystem.view;

import ch.fabian.solarSystem.IResetListener;
import ch.fabian.solarSystem.SimulationController;
import ch.fabian.solarSystem.model.GravitySimulation;
import ch.fabian.solarSystem.model.ModelSimulation;
import ch.fabian.solarSystem.model.SimulationParameters;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ControlsController {

    private List<IResetListener> resetListeners;

    @FXML
    private Label currentTimeStep;
    @FXML
    private Slider timeStepSlider;
    @FXML
    private Label animationFPSLabel;
    @FXML
    private Label simulationStepsLabel;
    @FXML
    private Label simulatedTimeLabel;
    @FXML
    private TextField maximumTimeStepField;
    @FXML
    private CheckBox weakCollisionsCheckBox;
    @FXML
    private CheckBox mergeObjectsCheckBox;
    @FXML
    private Label objectCountLabel;

    private ModelSimulation currentSimulation;

    public ControlsController() {
        resetListeners = new ArrayList<>();
    }

    public void setCurrentSimulation(SimulationController controller) {
        if (this.currentSimulation != null) {
            timeStepSlider.valueProperty().unbind();
        }
        this.currentSimulation = controller.getModelSimulation();
        GravitySimulation gravitySimulation = currentSimulation.getGravitySimulation();
        double timeStep = gravitySimulation.getParameters().getTimeStep();
        timeStepSlider.setValue(timeStep);
        mergeObjectsCheckBox.selectedProperty().setValue(currentSimulation.getCurrentParameters().isMergeObjects());
        weakCollisionsCheckBox.selectedProperty().setValue(currentSimulation.getCurrentParameters().isWeakenCollisions());

        StringConverter<Number> numberStringConverter = new NumberStringConverter();
        Bindings.bindBidirectional(maximumTimeStepField.textProperty(), timeStepSlider.maxProperty(), numberStringConverter);
        timeStepSlider.setMin(0);
        timeStepSlider.setMajorTickUnit(0.01);
        currentTimeStep.setText(formatTimeStepLabel(timeStep));
        timeStepSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            currentTimeStep.setText(formatTimeStepLabel(newValue));
            currentSimulation.setTimeStep(newValue.doubleValue());
        });
        mergeObjectsCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            SimulationParameters currentParameters = currentSimulation.getCurrentParameters();
            currentParameters.setMergeObjects(newValue);
            currentSimulation.setSimulationParameters(currentParameters);
        });

        weakCollisionsCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            SimulationParameters currentParameters = currentSimulation.getCurrentParameters();
            currentParameters.setWeakenCollisions(newValue);
            currentSimulation.setSimulationParameters(currentParameters);
        });
        controller.getViewSimulation().fpsProperty().addListener((observable, oldValue, newValue) -> {
            animationFPSLabel.setText(formatTimeStepLabel(newValue) + " FPS");
            if (changeCount % 10 == 0) {
                simulationStepsLabel.setText(formatSimulationSteps(computeSimulationStepsPerSecond(currentSimulation.getSimulationStepCount())));
            }
            objectCountLabel.setText(String.valueOf(currentSimulation.getObjectCount()));
            simulatedTimeLabel.setText(String.format("%.2f", currentSimulation.getSimulatedTime().doubleValue()) + " s");
            changeCount++;
        });
    }

    private long changeCount = 0;

    private double computeSimulationStepsPerSecond(long stepCount) {
        long currentTime = System.nanoTime();
        long stepDifference = stepCount - lastStepCount;
        long timeDifferenceNs = currentTime - lastStepTimeStamp;
        double stepsPerSecond = ((double) stepDifference / timeDifferenceNs) * 1_000_000_000;
        double weightedStepsPerSecond = 0.9 * lastStepsPersSecond + 0.1 * stepsPerSecond;
        lastStepTimeStamp = currentTime;
        lastStepCount = stepCount;
        lastStepsPersSecond = weightedStepsPerSecond;
        return weightedStepsPerSecond;
    }

    private double lastStepsPersSecond;
    private long lastStepCount;
    private long lastStepTimeStamp;

    private String formatSimulationSteps(Number value) {
        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setGroupingUsed(true);
        decimalFormat.setMaximumFractionDigits(2);
        return decimalFormat.format(value);
    }

    private String formatTimeStepLabel(Number value) {
        return String.format("%.02f", value.doubleValue()) + " s";
    }

    public void resetButtonAction() {
        resetListeners.forEach(IResetListener::reset);
        lastStepCount = 0;
    }

    public void addResetListener(IResetListener listener) {
        resetListeners.add(listener);
    }

    public void updateParams(SimulationParameters newParameters) {
        timeStepSlider.setValue(newParameters.getTimeStep());
        weakCollisionsCheckBox.selectedProperty().setValue(newParameters.isWeakenCollisions());
        mergeObjectsCheckBox.selectedProperty().setValue(newParameters.isMergeObjects());
    }
}