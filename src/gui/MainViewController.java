package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.services.DepartmentService;
import model.services.SellerService;

public class MainViewController implements Initializable {

	@FXML
	private MenuItem menuItemSeller;

	@FXML
	private MenuItem menuItemDepartment;

	@FXML
	private MenuItem menuItemAbout;

	@FXML
	public void onMenuItemSellerAction() {
		loadView("/gui/SellerList.fxml", (SellerListController controller) -> {
			controller.setSellerService(new SellerService());
			controller.updateTableView();			
		});
	}

	@FXML
	public void onMenuItemDepartmentAction() {
		loadView("/gui/DepartmentList.fxml", (DepartmentListController controller) -> {
			controller.setDepartmentService(new DepartmentService());
			controller.updateTableView();			
		});
	}

	@FXML
	public void onMenuItemAboutAction() {
		loadView("/gui/About.fxml", x -> {});
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

	}
	
	//o synchronized --> garante que o processamento do m�todo n�o ser� interrompido pelo multi-thread
	//o Consumer<T> initializinAction .. permite receber uma fun��o Lambda de inicializa��o do m�todo! Passa a ser uma fun��o Gen�rica do tipo <T>
	private synchronized <T> void loadView(String absoluteName, Consumer<T> initializinAction) {
		try {
			
			//Gera objeto newVox com o conet�do da tela que foi chamada.
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			VBox newVBox = loader.load();
			
			//Pega a tela principal.
			Scene mainScene = Main.getmainScene();
			VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();
			
			//Pega s� o menu da tela principal
			Node mainMenu = mainVBox.getChildren().get(0);
			//Limpa tudo que possa ter na tela principal
			mainVBox.getChildren().clear();
			//Adiciona o Menu - Sempre vai existir.
			mainVBox.getChildren().add(mainMenu);
			//Adiciona os itens da tela que deseja-se abrir
			mainVBox.getChildren().addAll(newVBox.getChildren());
			
			//Ativa a fun��o passar no par�metro de Consumer<T>
			T controller = loader.getController();   //Cria um objeto do tipo T Gen�rico - passado por par�metro, que ir� receber o controller!
			initializinAction.accept(controller);  //Chama a fun��o L�mbda passando por par�metro o controller (da View passada por par�metro)
			
		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}	

}
