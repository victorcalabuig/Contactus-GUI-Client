package client;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Position;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

/**
 * Clase con método y variables comunes a todas las clases del cliente.
 */
public class ClientUtils {

    public static int gridSize = 16; //0 to 15


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


    /**
     * Devuelve el estado actual de un usuario
     */
    public static String getUserState(String userId, PrintWriter out, BufferedReader in) throws IOException {
        String msgToServer = ClientUtils.buildMessage(userId, "state", "");
        System.out.println("[MSEN] msg to server: " + msgToServer);

        out.println(msgToServer);
        out.flush();

        String serverResponse = in.readLine();
        System.out.println("[MREC] server response: " + serverResponse); //log for console

        //Implementar lógica tras contestación
        //Formato de mensaje recibido: "comando_ejecutado resultado info1 info2 ...."
        String[] fields = serverResponse.split(" ");
        if(fields.length > 0) {
            if(ClientUtils.commandSuccess(fields, 3)){
                return fields[2];
            }
        }
        return "error";
    }

    /**
     * Envía el comando listAlarms al servidor para el usuario indicado, y devuelve la contestación
     * del servidor en un array (mesnsaje separado por espacios)
     *
     * @return Array con el mensaje del servidor separado por espacios.
     */
    public static String[] listAlarms(String userId, PrintWriter out, BufferedReader in) throws IOException {
        String msgToServer = ClientUtils.buildMessage(userId, "listAlarms", "");
        System.out.println("[MSEN] msg to server: " + msgToServer);

        out.println(msgToServer);
        out.flush();

        String serverResponse = in.readLine();
        System.out.println("[MREC] server response: " + serverResponse); //log for console

        //Implementar lógica tras contestación
        //Formato de mensaje recibido: "comando_ejecutado resultado info1 info2 ...."
        String[] fields = serverResponse.split(" ");
        if(fields.length > 0) {
            if(ClientUtils.commandSuccess(fields, 2)){
                if(fields.length > 2){
                    return Arrays.copyOfRange(fields, 2, fields.length);
                }
            }
        }
        return null;
    }

    /**
     * Envía un comando al servidor, y procesa la contestación con el comando commandSuccess.
     *
     * @param userId Usuario que envía el comando
     * @param command comando a envíar.
     * @param args argumentos del comando (si no tiene pasar cadena vacía)
     * @param answerMinArgs Número de argumentos (palabras) que debería tener la respuesta del servidor.
     * @return Si el comando se ejecuta correctamente, devuelve el mensaje del servidor en un
     * array separado por espacios, sino, null.
     */
    public static String[] sendCommandToServer(String userId, String command, String args, int answerMinArgs,
                                             PrintWriter out, BufferedReader in)
            throws IOException
    {
        String msgToServer = ClientUtils.buildMessage(userId, command, args);
        System.out.println("[MSEN] msg to server: " + msgToServer);

        out.println(msgToServer);
        out.flush();

        String serverResponse = in.readLine();
        System.out.println("[MREC] server response: " + serverResponse); //log for console

        //Implementar lógica tras contestación
        //Formato de mensaje recibido: "comando_ejecutado resultado info1 info2 ...."
        String[] fields = serverResponse.split(" ");
        if(fields.length > 0 && commandSuccess(fields, answerMinArgs))
            return fields;

        return null; //command returns error (check log)
    }


    /**
     * Consulta la última posición de un usuario y la devuelve encapsulada en un objeto Position.
     *
     * @return Position que contiene el id del usuario y su última posición.
     */
    public static Position getUserLastPosition(String userId, PrintWriter out, BufferedReader in) throws IOException {
        String[] serverResponse = sendCommandToServer(userId, "lastPosition", "", 5, out, in);
        if(serverResponse == null) return null;

        int latitude = (int) Double.parseDouble(serverResponse[3]);
        int longitude = (int) Double.parseDouble(serverResponse[4]);

        return new Position(userId, latitude, longitude);
    }

    /**
     * Pinta una posición en el mapa indicado.
     *
     * @param position Posicción a pintar.
     * @param mark Caracter para representar la posición (por ejempplo "x").
     * @param color Color del caracter (rojo para infected, verde para healthy...)
     * @param map Mapa en el que se pinta la posición.
     */
    public static void paintPosition(Position position, String mark, String color, GridPane map){
        if(position == null || position.isOutOfGrid(gridSize)) return; //posición fuera de nuestro mapa

        Label pos = createLabel(mark, color);

        map.add(pos, position.latitude, position.longitude);
        System.out.println("[INFO] painted position " + position.latitude + " " + position.longitude);
    }

    /**
     * Crea un objeto Label con el estilo indicado.
     *
     * @param mark Texto del label.
     * @param color Color del texto del label.
     * @return Label con estilo
     */
    public static Label createLabel(String mark, String color){
        Label label = new Label(mark);
        label.setStyle(String.format("-fx-text-fill: %s", color));
        return label;
    }



}
