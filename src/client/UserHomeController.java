package client;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import client.ClientUtils;
import javafx.stage.Stage;
import model.Position;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controlador de la vista userHomeView.
 */
public class UserHomeController extends GenericController implements Initializable {

    //Componentes para conectarse con el servidor (del proyecto Contactus)
    Socket clientSocket;
    PrintWriter out;
    BufferedReader in;

    public String color;

    @FXML
    Text state;

    @FXML
    Button stateButton;

    @FXML
    private Text alarmMessage;

    @FXML
    private Text currentPosition;

    /**
     * Abre una nueva ventana con el formulario para introducir una posición.
     */
    @FXML
    void addPosition(ActionEvent event) throws IOException, InterruptedException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("addPositionView.fxml"));
        Parent root = loader.load();

        AddPositionController addPositionController = loader.getController();
        addPositionController.setUserId(userId);

        Stage registerStage = ClientUtils.createStage("Contactus | Add Position", root);
        registerStage.show();
    }

    /**
     * Actualiza el mapa con la ultima posición del usuario logueado.
     */
    /*
    @FXML
    void refreshMap(ActionEvent event) {
        try {
            paintLastPosition(userId, userMark, color);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

     */

    /**
     * Actualiza el mapa, con la ultima posición del usuario y las posiciones (si las hay)
     * de usuarios sospechosos cercanos (alarmas).
     * @param event Pulsación del botón Refresh Map.
     */
    @FXML
    void refreshMap(ActionEvent event) throws IOException {
        resetMap();
        paintUserPosition();
        boolean alarmDetected = paintStrangerPositions();

        if(alarmDetected) alarmMessage.setVisible(true);
        else alarmMessage.setVisible(false);
    }

    /**
     * Consulta y actualiza el estado actual de un usuario. Necesario clickarlo cuando el
     * servidor nos asigna el estado suspect (no se actualiza automáticamente).
     * @param event
     * @throws IOException
     */
    @FXML
    void refreshState(ActionEvent event) throws IOException {
        updateGUI(ClientUtils.getUserState(userId, out, in));
    }

    /**
     * Actualiza el estado actual del usuario, y con ello toda la GUI.
     * @param event
     * @throws IOException
     */
    @FXML
    void changeState(ActionEvent event) throws IOException {
        if(state.getText().equals("infected")){
            changeTo("healthy");
        }
        else{
            changeTo("infected");
        }
    }

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
        stateButton.setStyle("-fx-background-radius: 40");
        alarmMessage.setVisible(false);
    }

    /**
     * Pinta en el mapa la última posición del usuario logueado.
     * @throws IOException
     */
    public void paintUserPosition() throws IOException {
        Position lastPosition= ClientUtils.getUserLastPosition(userId, out, in);
        if(lastPosition != null){
            currentPosition.setText(String.format("(%d, %d)", lastPosition.latitude, lastPosition.longitude));
            ClientUtils.paintPosition(lastPosition, userMark, color, map);
        }
    }

    /**
     * Comprueba (utilizando el comando listAlarms) si hay algún usuario sospechoso (infected o
     * suspect) cerca, es decir, si hay alarmas. Si las hay, pinta en el mapa la posición del
     * usuario sospechoso.
     * @return true si detecta alarmas, false si no.
     */
    public boolean paintStrangerPositions() throws IOException {
        String[] alarms = ClientUtils.listAlarms(userId, out, in); //alarms[] = ["dangerUserId-distance", "dangerUserId-distance"...]
        if(alarms == null) return false;

        for (String alarm : alarms) {
            String fields[] = alarm.split("-");
            Position strangerPosition = ClientUtils.getUserLastPosition(fields[0], out, in);
            if(strangerPosition != null){
                ClientUtils.paintPosition(strangerPosition, strangerMark, "red", map);
            }
        }
        return true;
    }

    /**
     * Cambia el estado de un usuario al estado indicado, enviando para ello el comando correspondient
     * al servidor. También actualiza la gui correspondientemente.
     * @param newState Nuevo estado al que cambiar.
     */
    public void changeTo(String newState) throws IOException {
        String msgToServer = ClientUtils.buildMessage(userId, newState, "");
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
                updateGUI(fields[0]);
            }
        }
    }

    /**
     * Actualiza la GUI con los colores y botones del estado correspondiente (infected = rojo,
     * healhty = verde, suspect = naranja).
     * @param newState
     */
    public void updateGUI(String newState){
        setState(newState);
        setStateColor();
        setStateButtonText();
    }

    /**
     * Consulta la última posició del usuario en el servidor con el comando "lastPosition".
     */
    public void paintLastPosition(String userId, String mark, String color) throws IOException {
        String msgToServer = ClientUtils.buildMessage(userId, "lastPosition", "");
        System.out.println("[MSEN] msg to server: " + msgToServer);

        out.println(msgToServer);
        out.flush();

        String serverResponse = in.readLine();
        System.out.println("[MREC] server response: " + serverResponse); //log for console

        //Implementar lógica tras contestación
        //Formato de mensaje recibido: "comando_ejecutado resultado info1 info2 ...."
        String[] fields = serverResponse.split(" ");
        if(fields.length > 0) {
            switch (fields[0]) {
                case "lastPosition" -> processLastPositionResult(fields, mark, color);
            }
        }
    }

    /**
     * Procesa la contestación del servidor, que nos envía la ultima posicion del usuario
     * logeado. Si el comando se ejecuta con exito, borra el mapa actual y pinta la nueva
     * posicion.
     * @param fields Mensaje del servidor.
     */
    public void processLastPositionResult(String[] fields, String mark, String color){
        if(ClientUtils.commandSuccess(fields, 4)){
            int latitude = (int) Double.parseDouble(fields[2]);
            int longitude = (int) Double.parseDouble(fields[3]);
            resetMap();
            paintPosition(latitude, longitude, mark, color);
        }
    }


    /**
     * Pint una posición en el mapa si está se encuentra dentro de sus limites (15x15)
     */
    public void paintPosition(int latitude, int longitude, String mark, String color){
        if(latitude > 14 || longitude > 14) return; //No cabe en el mapa 15x15

        Label pos = new Label(mark);
        pos.setStyle(String.format("-fx-text-fill: %s", color));

        map.add(pos,latitude, longitude);
        System.out.println("[INFO] painted position " + latitude + " " + longitude);
    }

    /**
     * Elimina todas las posiciones del mapa
     */
    public void resetMap(){
        ObservableList<Node> childrens = map.getChildren();

        List<Node> toRemove = new ArrayList();
        for (Node node : childrens) {
            if(node != null) toRemove.add(node);
        }
        for(Node node : toRemove){
            childrens.remove(node);
        }
    }

    public void setState(String state){
        this.state.setText(state);
    }

    public void setStateColor(){
        String color = "";
        switch (state.getText()){
            case "healthy" -> color = "green";
            case "suspect" -> color = "orange";
            case "infected" -> color = "red";
        }
        String css = String.format("-fx-fill: %s", color);
        state.setStyle(css);
        this.color = color;
    }

    public void setStateButtonText(){
        if(state.getText().equals("infected")){
            stateButton.setText("Healthy");
            //stateButton.setStyle("-fx-border-color: green");
        }
        else{
            stateButton.setText("Infected");
            //stateButton.setStyle("-fx-border-color: #d98282");
        }
    }

}
