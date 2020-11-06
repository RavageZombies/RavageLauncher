package net.ravage;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.ravage.launch.LaunchManager;
import net.ravage.nologin.account.Account;
import net.ravage.update.UpdateManager;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    public static final String APPLICATION_TITLE = "DrawLife Launcher";
    public static final String WEBSITE_URL = "https://www.esperia-rp.net/";

    public static final String LAUNCHER_VERSION = "1.2.2";
    public static final String LAUNCHER_CHECK_URL = "https://launcher.esperia-rp.net/resources/launcher.version";
    public static final String LAUNCHER_DOWNLOAD_EXE_URL = "https://launcher.esperia-rp.net/resources/Esperia.exe";
    public static final String LAUNCHER_DOWNLOAD_JAR_URL = "https://launcher.esperia-rp.net/resources/Esperia.jar";

    public static Account account;
    public static List<Account> accountList = new ArrayList<Account>();

    private double xOffset = 0.0;
    private double yOffset = 0.0;

    public static BaseLauncher launcher;
    private UpdateManager updateManager;
    private LaunchManager launchManager;

    @Override
    public void start(Stage primaryStage) throws Exception{

        this.launcher = new BaseLauncher("Ravage", "1.12.2", "14.23.5.2847", "https://drawlife.eu/app/webroot/326598/update/", false);
        this.launcher.getUpdateManager().enableCustomJre("https://drawlife.eu/app/webroot/326598/JRE.zip", "0ac70223f00b0be4f33846bc6d3d6114");
        this.updateManager = launcher.getUpdateManager();
        this.launchManager = launcher.getLaunchManager();
        if (System.getProperty("os.name").contains("Windows")) {
            if(System.getenv("ProgramFiles(x86)") != null) {
                if (System.getProperty("sun.arch.data.model").equals("32")) {
                    JOptionPane.showMessageDialog(null, "Java 32bit est installé sur un windows" +
                            "64bit. Le jeu ne pourra pas se lancer ! Le navigateur va s'ouvrir sur le site de " +
                            "téléchargement de Java. Il faut télécharger la version \"Windows Hors ligne (64 bits)\"");
                    Desktop.getDesktop().browse(new URI("https://www.java.com/fr/download/manual.jsp"));
                    System.exit(1);
                }
            }
        }

        Parent root = FXMLLoader.load(getClass().getResource("/views/main.fxml"));
        root.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        root.setOnMouseDragged(event -> {

            if (yOffset < 36) {
                primaryStage.setX(event.getScreenX() - xOffset);
                primaryStage.setY(event.getScreenY() - yOffset);
            }

        });

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle(Main.APPLICATION_TITLE);
        primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("/images/icon.png")));
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root, 1200, 737));
        primaryStage.show();
        Platform.runLater(root::requestFocus);
    }


    public static void main(String[] args) {
        launch(args);
    }

    public static BaseLauncher getLauncher() {
        return launcher;
    }
}
