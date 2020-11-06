package net.ravage.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import lombok.Setter;
import net.ravage.Main;
import net.ravage.login.AuthPoints;
import net.ravage.login.AuthenticationException;
import net.ravage.login.Authenticator;
import net.ravage.login.model.AuthAgent;
import net.ravage.login.model.response.AuthResponse;
import net.ravage.nologin.NoLogin;
import net.ravage.nologin.account.Account;
import net.ravage.nologin.util.Utilities;

import java.util.ArrayList;
import java.util.List;

public class LoginController {

    @FXML
    private Label back;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    private CheckBox rememberCheck;

    @FXML
    private Label loginStatusLabel;

    @Setter
    private MainController mainController;

    private String clientToken = "";



    @FXML
    public void initialize() {
        NoLogin noLogin = new NoLogin();
        String token = Utilities.getClientToken();
        if (token != null) {
            clientToken = token;
        }

        Main.accountList = new ArrayList<Account>();
        List<Account> accounts = noLogin.getAccountManager().getAccounts();
        String defaultAccount = Utilities.getDefaultAccount();
        System.out.println("Comptes trouvés : " + accounts.size());
        for(Account acc : accounts) {
            if(noLogin.getValidator().validateAccount(acc)) {
                Main.accountList.add(acc);
                System.out.println(acc.getDisplayName() + " valide");
                if (acc.getUUID().equals(defaultAccount)) {
                    Main.account = acc;
                    System.out.println(acc.getDisplayName() + " est le compte par défaut");
                    back.setVisible(true);
                }
            } else {
                System.out.println(acc.getDisplayName() + " invalide");
            }
        }
    }

    public void onConnectClicked() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        Boolean remember = rememberCheck.isSelected();

        if (!username.isEmpty() && !password.isEmpty()) {
            auth(username, password, remember);
        } else {
            loginStatusLabel.setText("Merci de remplir le nom d'utilisateur et le mot de passe.");
            loginStatusLabel.setTextFill(Color.RED);
        }
    }

    private void auth(String username, String password, Boolean remember) {
        Authenticator authenticator = new Authenticator(Authenticator.MOJANG_AUTH_URL, AuthPoints.NORMAL_AUTH_POINTS);
        AuthResponse response;

        try {
            response = authenticator.authenticate(AuthAgent.MINECRAFT, username, password, clientToken);
            Account account = new Account(response.getSelectedProfile().getId(),
                    response.getSelectedProfile().getName(), response.getAccessToken(),
                    response.getSelectedProfile().getId(), username);

            Main.account = account;

            if (remember) {
                Main.accountList.add(account);
                System.out.println("Nombre de comptes : " + Main.accountList.size());
                Utilities.addAccount(account, response);
                Utilities.updateDefaultAccount(account);
            }

            mainController.onAuthCompleted();
        } catch (AuthenticationException e) {
            e.printStackTrace();
            loginStatusLabel.setText(e.getMessage());
            loginStatusLabel.setTextFill(Color.RED);
        }
    }

    @FXML
    private void onBackClicked() {
        mainController.reopenPlay();
    }

    public MainController getMainController() {
        return mainController;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
