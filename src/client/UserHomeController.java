package client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.text.Text;

import client.ClientUtils;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

import java.util.ResourceBundle;

/**
 * Controlador de la vista userHomeView.
 */
public class UserHomeController extends GenericController implements Initializable {

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

    @Override
    public void initialize(URL location, ResourceBundle resources){
    }



}
