package net.ravage.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import lombok.Setter;
import net.ravage.AppProperties;
import net.ravage.BaseLauncher;
import net.ravage.Main;
import net.ravage.launch.LaunchManager;
import net.ravage.nologin.util.Utilities;
import net.ravage.update.UpdateManager;
import net.ravage.update.minecraft.download.DownloadManager;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;

public class PlayController {

    @FXML
    private Label versionLabel;
    @FXML
    private Label updateLabel;
    @FXML
    private Label playerNameLabel;
    @FXML
    private ImageView playerImage;
    @FXML
    private Circle mojangStatusCircle;
    @FXML
    private Label playersCountLabel;

    @Setter
    private MainController mainController;


    @FXML
    public void initialize() {

    /*    new Thread(() -> {
            Server server = new Server(Main.SERVER_IP, Main.SERVER_PORT);
            if (server.isOnline()) {
                Platform.runLater(() -> {
                    playersCountLabel.setText(server.getPlayersCount() + "/" + server.getMaxPlayers());
                });
            } else {
                Platform.runLater(() -> {
                    playersCountLabel.setText("Hors ligne");
                    playersCountLabel.setTextFill(Color.RED);
                });
            }
        }).start();

        new Thread(() -> {
            Mojang mojang = new Mojang();
            Platform.runLater(() -> {
                mojangStatusCircle.setFill(mojang.getStatusColor());
            });
        }).start();*/

        playerImage.setImage(new Image("https://www.esperia-rp.net/skins/avatar/minecraft/" + Utilities.formatUuid(Main.account.getUUID() + "/64")));
        playerNameLabel.setText(Main.account.getDisplayName());

        versionLabel.setText(Main.LAUNCHER_VERSION);
        checkLauncherUpdate();
    }

    @FXML
    private void onPlayerImageClicked() {
        this.mainController.loadSwitchUsers();
    }

    @FXML
    private void onExternalLinkClicked(MouseEvent event) {
        String id = ((ImageView) event.getSource()).getId();

        try {
            URI uri = null;
            switch (id) {
                case "w":
                    uri = new URI(Main.WEBSITE_URL);
                    break;
                case "ds":
                    uri = new URI("https://discord.gg/GGxR2gK");
                    break;
                case "tw":
                    uri = new URI("https://twitter.com/EsperiaRP");
                    break;
                case "fb":
                    uri = new URI("http://www.facebook.com/EsperiaRP");
                    break;
                case "yt":
                    uri = new URI("http://www.youtube.com/user/EsperiaRP");
                    break;
            }

            if (uri != null) {
                Desktop.getDesktop().browse(uri);
            }
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onOptionsClicked(MouseEvent event) {
        mainController.openOptions();
    }

    @FXML
    private void onPlayClicked() {
        new Thread(this::update).start();
    }

    private void update() {

        Platform.runLater(() -> {
            updateLabel.setText("Vérification de l'intégrité des fichiers");
            updateLabel.setVisible(true);
        });

      /*  if (!AppProperties.properties.getProperty("beta", "false").equals("true")) {

        } else {

        }*/

        if (Main.account != null) {
            launch();
        }


       Thread t = new Thread(new Runnable() {

            private Label l;

            public Runnable init(Label label) {
                l = label;
                return this;
            }

            @Override
            public void run() {

                boolean flag = true;
                while (flag) {
                    if (Main.getLauncher().getUpdateManager().isDownloading()) {
                        System.out.println("test");
                        String percent = DownloadManager.crossMult(Main.getLauncher().getUpdateManager().getDownloadedFiles(), Main.getLauncher().getUpdateManager().getTotalFiles(), 100) + "%";
                        Platform.runLater(() -> {
                            System.out.println("test2");
                            l.setText("Téléchargement en cours : " + Main.getLauncher().getUpdateManager().getUpdateMessage() + " (" + percent + " %).");
                        });

                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            flag = false;
                        }
                    }
                }
            }
        }.init(updateLabel));

        t.start();
    }

    private void launch() {
        System.out.println("test");
        Main.getLauncher().getUpdateManager().updateAll();
        Main.getLauncher().getLaunchManager().launch();
        /*try {
            ExternalLaunchProfile profile;
            ExternalLauncher mcLauncher;
            AuthInfos authInfos = new AuthInfos(Main.account.getDisplayName(), Main.account.getAccessToken(),
                    Main.account.getUUID());

            if (!AppProperties.properties.getProperty("beta", "false").equals("true"))
                profile = MinecraftLauncher.createExternalProfile(Main.INFOS, GameFolder.BASIC, authInfos);
            else
                profile = MinecraftLauncher.createExternalProfile(Main.BETA_INFOS, GameFolder.BASIC, authInfos);

            String ram = AppProperties.properties.getProperty("ram", "2");
            profile.getVmArgs().addAll(Arrays.asList("-Xms" + ram + "G", "-Xmx" + ram + "G"));
            mcLauncher = new ExternalLauncher(profile);
            mcLauncher.launch();

            System.exit(0);
        } catch (LaunchException exc) {
            exc.printStackTrace();
        }*/
    }

    private void checkLauncherUpdate() {
        new Thread(() -> {
            try {
                String out = null;
                out = new Scanner(new URL(Main.LAUNCHER_CHECK_URL).openStream(), "UTF-8").useDelimiter("\\A").next();
                if (!out.equals(Main.LAUNCHER_VERSION)) {
                    Platform.runLater(() -> {
                        versionLabel.setText(versionLabel.getText() + " (une mise à jour est disponible !)");
                        versionLabel.setStyle("-fx-underline: true;-fx-cursor: hand;");
                        versionLabel.setOnMousePressed(event -> {
                            try {
                                URI uri;
                                String os = System.getProperty("os.name").toLowerCase();
                                if (os.contains("win")) {
                                    uri = new URI(Main.LAUNCHER_DOWNLOAD_EXE_URL);
                                } else {
                                    uri = new URI(Main.LAUNCHER_DOWNLOAD_JAR_URL);
                                }
                                Desktop.getDesktop().browse(uri);
                            } catch (IOException | URISyntaxException e) {
                                e.printStackTrace();
                            }
                        });
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public MainController getMainController() {
        return mainController;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
