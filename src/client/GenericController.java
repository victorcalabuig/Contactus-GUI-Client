package client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Controlador genérico que extienden los controladores de las vistas userHomeView
 * y adminHomeView. Contiene toda la lógica común a ambnos controladores.
 */
public class GenericController {

    @FXML
    public Text username;

    @FXML
    public GridPane map;

    @FXML
    void paint(ActionEvent event) {
        Label position = new Label("x");
        //position.setStyle("-fx-background-color: black");
        map.add(position, 3, 3);
        System.out.println("Hello world!");
    }

    public String userId;

    /**
     * Symbol used to mark a location.
     */
    public String mark = "x";

    public void setUsername(String username){
        this.username.setText(username);
    }

    public void setUserId(String userId){
        this.userId = userId;
    }

}
