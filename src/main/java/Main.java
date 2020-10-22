import javafx.application.Application;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;

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
        BoundingBoxOptimizer boundingBoxOptimizer = new BoundingBoxOptimizer();
        LandParcelOptimizer landParcelOptimizer = new LandParcelOptimizer();
        BuildingPlacer placer = new BuildingPlacer();
        SceneRenderer sceneRenderer = new SceneRenderer();
        int i = 0;
        long startTime = System.currentTimeMillis();
        System.out.println(LandParcels.size());
        for (LandParcel parcel : LandParcels) {
            parcel.surroundingParcels(reader);
            boundingBoxOptimizer.BoundingBoxOptimization(parcel, 5, 5, 1.5);

            i++;
            if(i == 100){
                break;
            }
            //if(i % 100 == 0)
                System.out.println("Parcels computed: " + i + " in " + (System.currentTimeMillis() - startTime)/1000 + "s");
        }
        i = 0;
        startTime = System.currentTimeMillis();
        for (LandParcel parcel : LandParcels) {
            reader.getBuildingFootprints(currentDir.toAbsolutePath() + "/input/buildingFootprints.json");
            placer.setRoadCentre(parcel);
            placer.surroundingFootprints(parcel);
            placer.placeBuildings(parcel, reader);

            switch (parcel.landType){
                case industry:
                    SceneRenderer.render(parcel.getFootprintGeometries(), SceneRenderer.ColorSpectrum.Yellow);
                    break;
                case commercial:
                    SceneRenderer.render(parcel.getFootprintGeometries(), SceneRenderer.ColorSpectrum.Blue);
                    break;
                case residential:
                    SceneRenderer.render(parcel.getFootprintGeometries(), SceneRenderer.ColorSpectrum.Green);
                    break;
                case undefined:
                    SceneRenderer.render(parcel.getFootprintGeometries(), SceneRenderer.ColorSpectrum.Red);
                    break;
            }

            for (Footprint footprint : parcel.footprints) {
                //if (footprint.id == 157 || footprint.id == 165) {
                //    placer.createDriveway(footprint);
                //}

                if (footprint.building != null) {
                    SceneRenderer.render(footprint.building.polygon, Color.GREY);
                }

            }

            i++;
            //if(i % 100 == 0)
                System.out.println("Buildings placed: " + i + " in " + (System.currentTimeMillis() - startTime)/1000 + "s");
        }
        System.out.println("Finished Computing");
        sceneRenderer.start(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
