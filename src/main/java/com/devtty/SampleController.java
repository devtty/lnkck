package com.devtty;

import java.io.*;
import java.net.*;
import java.net.http.*;
import java.util.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.stream.Stream;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;


public class SampleController{

    @FXML private Text actiontarget;

    @FXML private MenuItem fileOpenMenuItem;

    @FXML private TableView tableView;
	
    List<UrlCheck> urlChecks; 
    
    @FXML
    public void openFileDialog(ActionEvent event){

	urlChecks = new ArrayList<>();
	
	FileChooser fC = new FileChooser();
	fC.setTitle("asdf");
	File file = fC.showOpenDialog(fileOpenMenuItem.getParentPopup().getScene().getWindow());

	try(Stream<String> lines = Files.lines(file.toPath())) {
	    lines.forEach(s -> process(s));
	} catch(IOException ex){
	    ex.printStackTrace();
	}

	tableView.setItems(FXCollections.observableArrayList(urlChecks));
	TableColumn<UrlCheck, String> urlCol = new TableColumn<>("Url");
	urlCol.setCellValueFactory(new PropertyValueFactory<>(urlChecks.get(0).urltocheckProperty().getName()));

	tableView.getColumns().setAll(urlCol);
			   
	System.out.println("CHECK FINISHED");
    }


    @FXML
    public void checkHttp(ActionEvent event){

	//Optional mit direktem (linebyline) check oder hierarchisch optimiert (erst Domain)
	HttpClient client = HttpClient.newBuilder()
	    .version(HttpClient.Version.HTTP_2)
	    .followRedirects(HttpClient.Redirect.NORMAL)
	    .build();
	
	for(int i=0;i<3;i++){
	    String url = urlChecks.get(i).getUrltocheck();
	    System.out.println(url);
	    HttpRequest req = HttpRequest.newBuilder()
		.uri(URI.create(url))
		.GET()
		.build();

	    try{
		HttpResponse<String> response =
		    client.send(req, HttpResponse.BodyHandlers.ofString());
		System.out.println(response.statusCode());
	    }catch(Exception e){
		e.printStackTrace();
	    }
 	    
	}
    }
    
    private void process(String s){

	try{
	    URL url = new URL(s);
	    urlChecks.add(new UrlCheck(s));	       
	}catch(MalformedURLException mex){
	    System.out.println(s + " OH NO");
	}
    }
    
}
