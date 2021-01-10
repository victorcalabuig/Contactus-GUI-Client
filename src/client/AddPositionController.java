package client;

import client.ClientUtils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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


public class AddPositionController implements Initializable {

    //Componentes para conectarse con el servidor (del proyecto Contactus)
    Socket clientSocket;
    PrintWriter out;
    BufferedReader in;

    public String userId;

    @FXML
    private TextField latitude;

    @FXML
    private TextField longitude;

    @FXML
    private Text errorMessage;

    /**
     * Envía el comando addPosition al servidor junto con las coordenadas suministradas en
     * los campos del formulario.
     */
    @FXML
    void addPosition(ActionEvent event) throws IOException {
        errorMessage.setVisible(false);

        String coordinates = latitude.getText().trim() + " " + longitude.getText().trim();
        String msgToServer = ClientUtils.buildMessage(userId, "addPosition", coordinates);

        out.println(msgToServer);
        out.flush();

        String serverResponse = in.readLine();
        System.out.println("[MREC] server response: " + serverResponse); //log for console

        //Implementar lógica tras contestación
        //Formato de mensaje recibido: "comando_ejecutado resultado info1 info2 ...."
        String[] fields = serverResponse.split(" ");
        if(fields.length > 0) {
            switch (fields[0]) {
                case "addPosition" -> processAddPositionResult(fields);
            }
        }
    }

    /**
     * Se ejecuta cuando se presiona el botón "close".
     */
    @FXML
    void close(ActionEvent event) {
        closeWindow();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
     * Cierra la ventana actual.
     */
    public void closeWindow(){
        Stage currStage = (Stage) latitude.getScene().getWindow();
        currStage.close();
    }

    /**
     * Procesa la contestación del servidor tras envíar el comando addPosition. Si el comando
     * se ha ejecutado correctamente cierra la ventana addPosition, sino muestra el mensaje
     * de error.
     * @param fields
     */
    public void processAddPositionResult(String[] fields){
        if(ClientUtils.commandSuccess(fields, 2)){
            String coordinates = latitude.getText().trim() + " " + longitude.getText().trim();
            System.out.println("[INFO] added position " + coordinates);

            closeWindow();
        }
        else{
            errorMessage.setVisible(true);
        }
    }

    public void setUserId(String userId){
        this.userId = userId;
    }

}
