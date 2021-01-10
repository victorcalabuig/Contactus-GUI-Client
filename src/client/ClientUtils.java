package client;

import javafx.fxml.FXML;
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
 * Clase con método y variables comunes a todas las clases del cliente.
 */
public class ClientUtils {


    /**
     * Constrye mensaje a partir de un userId, command y una serie de argumentos.
     */
    public static String buildMessage(int userId, String command, String args){
        StringBuilder msg = new StringBuilder();

        msg.append(userId);
        msg.append(" ").append(command);
        msg.append(" ").append(args);

        return msg.toString();
    }
    /**
     * Constrye mensaje a partir de un userId, command y una serie de argumentos.
     * Pasa el userId como un String.
     */
    public static String buildMessage(String userId, String command, String args){
        StringBuilder msg = new StringBuilder();

        msg.append(userId);
        msg.append(" ").append(command);
        msg.append(" ").append(args);

        return msg.toString();
    }

    /**
     * Verifica que el comando enviado al servidor se ha completado con exito.
     */
    public static boolean commandSuccess(String[] fields, int minArgs){
        return Integer.parseInt(fields[1]) == 0 && fields.length >= minArgs;
    }

    /**
     * Crea y devuelve una stage con el título y parent indicados.
     */
    public static Stage createStage(String title, Parent root){
        Stage stage = new Stage();

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(title);
        stage.setScene(new Scene(root));

        return stage;
    }

}
