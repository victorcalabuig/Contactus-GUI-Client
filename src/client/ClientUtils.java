package client;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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

/**
 * Clase con método y variables comunes a todas las clases del cliente.
 */
public class ClientUtils {


    /**
     * Constrye mensaje a partir de un userId, command y una serie de argumentos.
     */
    public String buildMessage(int userId, String command, String args){
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

}
