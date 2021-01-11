package client;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Implementa la lógica de la vista login
 */
public class LoginController extends ClientUtils implements Initializable {

    //Componentes para conectarse con el servidor (del proyecto Contactus)
    Socket clientSocket;
    PrintWriter out;
    BufferedReader in;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private Text errorMessage;

    /**
     * Logica tras pulsar el botón login. Envía el comando login al servidor con los
     * argumentos suministrados en los campos username y passowrd de la vista.
     * @param event Usuario pulsa el botón login
     */
    @FXML
    void login(ActionEvent event) throws IOException {
        errorMessage.setVisible(false);
        String credentials = username.getText().trim() + " " + password.getText().trim();
        String msgToServer = buildMessage(0, "login", credentials);

        out.println(msgToServer);
        out.flush();

        String serverResponse = in.readLine();
        System.out.println("[MREC] server response: " + serverResponse); //log for console

        //Implementar lógica tras contestación
        //Formato de mensaje recibido: "comando_ejecutado resultado info1 info2 ...."
        String[] fields = serverResponse.split(" ");
        if(fields.length > 0) {
            switch (fields[0]) {
                case "login" -> processLoginResult(fields);
            }
        }
    }

    @FXML
    void register(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("addUserView.fxml"));
        Parent root = loader.load();

        Stage registerStage = createStage("Contactus | Add User", root);
        registerStage.show();
    }

    /**
     * Implementa la lógica de inicio de la vista: se conecta con el servidor y pone
     * el mensaje de error en "no visible".
     */
    @Override
    public void initialize(URL location, ResourceBundle resources){
        errorMessage.setVisible(false);

        //Set up connection with server and in out buffers
        try {
            clientSocket = new Socket("localhost", 8000);
            out = new PrintWriter(clientSocket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e){
            e.printStackTrace();
        }


    }

    /**
     * Comprueba, tras envíar un comando login que se ejecuta correctamente en el
     * servidor, que el usuario que se acaba de autenticar es de tipo admin.
     */
    public boolean isAdmin(String[] fields){
        return Integer.parseInt(fields[4]) == 0;
    }

    /**
     * Abre una nueva escena con la vista home, que puede ser de tipo user o de
     * tipo admin según el tipo de usuario que se autentique.
     * @param view Vista (archivo .fxml) a cargar (userHomeView o adminHomeView).
     * @param admin true si el usuario autenticado es de tipo admin (se utiliza
     *              para determinar que controlador utilizar).
     */
    public void openHome(String view, boolean admin, String userId) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(view));
        Parent root = loader.load();

        //Stage
        Stage homeStage = createStage("Contactus | Home", root);

        //Set new scene's username and userId
        if(admin){
            AdminHomeController adminHomeController = loader.getController();
            adminHomeController.setUsername(username.getText().trim());
            adminHomeController.setUserId(userId);
        }
        else{
            UserHomeController userHomeController = loader.getController();
            userHomeController.setUsername(username.getText().trim());
            userHomeController.setUserId(userId);
            userHomeController.setState(getUserState(userId, out, in));
            userHomeController.setStateColor();
            userHomeController.setStateButtonText();
        }

        homeStage.show();
    }


    /**
     * Proceso la contestación del servidor tras enviar un comando login. Si el comando
     * se ejecuta correctamente, abre una nueva escena con el método openHome
     * @param fields Contestación del servidor separada por espacios.
     */
    public void processLoginResult(String[] fields) throws IOException {
        if(commandSuccess(fields, 5)){
            String userId = fields[2];

            if(isAdmin(fields)) openHome("adminHomeView.fxml", true, userId);
            else openHome("userHomeView.fxml", false, userId);
        }
        else{
            errorMessage.setVisible(true);
        }
    }

}
