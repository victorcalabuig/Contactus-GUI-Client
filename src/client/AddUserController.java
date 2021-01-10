package client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class AddUserController extends ClientUtils implements Initializable {

    //Componentes para conectarse con el servidor (del proyecto Contactus)
    Socket clientSocket;
    PrintWriter out;
    BufferedReader in;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private Text resultMessage;

    /**
     * Envía el comando addUser al servidor con los argumentos suministrados en los
     * campos username y password.
     */
    @FXML
    void addUser(ActionEvent event) throws IOException {
        resultMessage.setVisible(false);
        String credentials = username.getText().trim() + " " + password.getText().trim();
        String msgToServer = buildMessage(0, "addUser", credentials);

        out.println(msgToServer);
        out.flush();

        String serverResponse = in.readLine();
        System.out.println("[MREC] server response: " + serverResponse); //log for console

        //Implementar lógica tras contestación
        //Formato de mensaje recibido: "comando_ejecutado resultado info1 info2 ...."
        String[] fields = serverResponse.split(" ");
        if(fields.length > 0) {
            switch (fields[0]) {
                case "addUser" -> processAddUserResult(fields);
            }
        }
    }

    /**
     * Cierra la ventana de register para volver a la ventana de login
     */
    @FXML
    void backToLogin(ActionEvent event) {
        Stage currStage = (Stage) username.getScene().getWindow();
        currStage.close();
    }

    /**
     * Establece la conexión con el servidor.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources){
        resultMessage.setVisible(false);

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
     * Muestra un mensaje de exito si se añade el usuario correctamente
     * @param fields Mensaje recibido del servidor.
     */
    public void processAddUserResult(String[] fields){
        if(commandSuccess(fields, 2)){
            resultMessage.setText("New user added!");
            resultMessage.setVisible(true);
        }
    }

}
