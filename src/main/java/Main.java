import javafx.application.Application;
import javafx.stage.Stage;
import org.locationtech.jts.geom.Geometry;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

class DragContext {

    double mouseAnchorX;
    double mouseAnchorY;

    double translateAnchorX;
    double translateAnchorY;

}


public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Path currentDir = Paths.get(".");
        JsonReader reader = new JsonReader(currentDir.toAbsolutePath() + "/input/simpleroadnetwork.json");
        ArrayList<LandParcel> LandParcels = reader.getParcels();
        LandParcelOptimizer landParcelOptimizer = new LandParcelOptimizer();
        BuildingPlacer buildingPlacer = new BuildingPlacer();
        SceneRenderer sceneRenderer = new SceneRenderer();
        for (LandParcel parcels: LandParcels) {
            parcels.surroundingParcels(reader);
            Geometry[] footprints = landParcelOptimizer.BoundingBoxOptimization(parcels, 10, 0.5, 0.25);
            SceneRenderer.render(footprints);
            buildingPlacer.placeBuildings(parcels);
        }
        System.out.println("test");
        sceneRenderer.start(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
