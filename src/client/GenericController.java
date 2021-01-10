package client;

import javafx.fxml.FXML;
import javafx.scene.text.Text;

/**
 * Controlador genérico que extienden los controladores de las vistas userHomeView
 * y adminHomeView. Contiene toda la lógica común a ambnos controladores.
 */
public class GenericController {

    @FXML
    public Text username;

    public void setUsername(String username){
        this.username.setText(username);
    }
}
