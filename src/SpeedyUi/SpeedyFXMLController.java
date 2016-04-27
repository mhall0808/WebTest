/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SpeedyUi;


import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.openqa.selenium.WebDriver;

/**
 * FXML Controller class
 *
 * @author hallm8
 */
public class SpeedyFXMLController implements Initializable {

    
    
    @FXML
    TextField locationField;
    @FXML 
    Button goButton;
    @FXML
    WebView webView;
    
    WebEngine eng = webView.getEngine();
    
    
    
     @FXML
    public void goButtonClicked(ActionEvent event) {
        
        eng.load(locationField.getText().startsWith("http://") ? locationField.getText() :
                            "http://" + locationField.getText());
    }
    
    
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        
        
        eng.locationProperty().addListener(new ChangeListener<String>() {
                @Override public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    locationField.setText(newValue);
                }
            });
    }    
    
}
