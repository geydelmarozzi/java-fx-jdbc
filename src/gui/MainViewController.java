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
	
	//o synchronized --> garante que o processamento do método não será interrompido pelo multi-thread
	//o Consumer<T> initializinAction .. permite receber uma função Lambda de inicialização do método! Passa a ser uma função Genérica do tipo <T>
	private synchronized <T> void loadView(String absoluteName, Consumer<T> initializinAction) {
		try {
			
			//Gera objeto newVox com o conetúdo da tela que foi chamada.
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			VBox newVBox = loader.load();
			
			//Pega a tela principal.
			Scene mainScene = Main.getmainScene();
			VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();
			
			//Pega só o menu da tela principal
			Node mainMenu = mainVBox.getChildren().get(0);
			//Limpa tudo que possa ter na tela principal
			mainVBox.getChildren().clear();
			//Adiciona o Menu - Sempre vai existir.
			mainVBox.getChildren().add(mainMenu);
			//Adiciona os itens da tela que deseja-se abrir
			mainVBox.getChildren().addAll(newVBox.getChildren());
			
			//Ativa a função passar no parâmetro de Consumer<T>
			T controller = loader.getController();   //Cria um objeto do tipo T Genérico - passado por parâmetro, que irá receber o controller!
			initializinAction.accept(controller);  //Chama a função Lâmbda passando por parâmetro o controller (da View passada por parâmetro)
			
		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}	

}
