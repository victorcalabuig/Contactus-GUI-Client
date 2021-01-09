package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller extends ClientUtils implements Initializable {

    //Connection components
    Socket clientSocket;
    PrintWriter out;
    BufferedReader in;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private Text errorMessage;

    @FXML
    void login(ActionEvent event) throws IOException {
        String credentials = username.getText() + " " + password.getText();
        String msgToServer = buildMessage(0, "login", credentials);

        out.println(msgToServer); //
        out.flush();

        String res = in.readLine();
        System.out.println(res);
    }

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

}
