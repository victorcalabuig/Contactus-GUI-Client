package client;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;

import java.util.ResourceBundle;

/**
 * Controlador de la vista adminHomeView.
 */
public class AdminHomeController extends GenericController implements Initializable {

    //Componentes para conectarse con el servidor (del proyecto Contactus)
    Socket clientSocket;
    PrintWriter out;
    BufferedReader in;

    @Override
    public void initialize(URL location, ResourceBundle resources){
        //Set up connection with server and in out buffers
        try {
            clientSocket = new Socket("localhost", 8000);
            out = new PrintWriter(clientSocket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e){
            e.printStackTrace();
        }

        map.setStyle("-fx-border-color: black");
    }

}
