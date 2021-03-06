/*
 * Copyright (C) 2016 GESIS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, see 
 * http://www.gnu.org/licenses/.
 */
package org.gesis.linkinspect;

import java.util.Optional;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

/**
 * @author Felix Bensmann
 * Start class for this application
 */
public class MainApp extends Application {

    /**
     * Start application. Loads the main windows.
     *
     * @param stage
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {
        LogManager.getLogger(MainApp.class).log(Level.INFO, "Entering application.");

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Scene.fxml"));

        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/Styles.css");

        stage.setMinWidth(800);
        stage.setMinHeight(500);
        stage.setTitle("LinkInspect");
        stage.setScene(scene);

        //handle clicks to red cross button
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Please confirm");
                alert.setHeaderText("Please make sure you generated a report.");
                alert.setContentText("You are about to close this session. Proceed?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    LogManager.getLogger(MainApp.class).log(Level.INFO, "Shutting down with value 0.");
                    System.exit(0);
                } else {
                    we.consume();
                    LogManager.getLogger(MainApp.class).log(Level.INFO, "Shutting down aborted.");
                }
            }
        });

        stage.show();
        LogManager.getLogger(MainApp.class).log(Level.DEBUG, "Showing main window.");
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        LogManager.getLogger(MainApp.class).log(Level.INFO, "Entering application via main.");
        launch(args);
    }

}
