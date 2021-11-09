package com.devtty;

import java.io.*;
import java.net.*;
import java.net.http.*;
import java.util.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.time.Duration;
import java.util.stream.Stream;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;


public class SampleController{

    @FXML private Text actiontarget;

    @FXML private MenuItem fileOpenMenuItem;

    @FXML private TableView tableView;

    @FXML private TreeTableView treeTableView;
	
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

	createTableView();
	createTreeTableView();
			   
	System.out.println("loaded " + urlChecks.size());
	
    }

    private void createTableView(){
	tableView.setItems(FXCollections.observableArrayList(urlChecks));
	TableColumn<UrlCheck, String> urlCol = new TableColumn<>("Url");
	urlCol.setCellValueFactory(new PropertyValueFactory<>(urlChecks.get(0).urltocheckProperty().getName()));

	TableColumn<UrlCheck, Integer> statusCol = new TableColumn("Status");
	statusCol.setCellValueFactory(new PropertyValueFactory<>(urlChecks.get(0).statusProperty().getName()));

	tableView.getColumns().setAll(urlCol, statusCol);
    }

    private void createTreeTableView(){

	UrlCheck rootCheck = new UrlCheck("test");
	
	TreeItem<UrlCheck> root = new TreeItem<>(rootCheck);
	
	for(UrlCheck uc : urlChecks){

	    try{
		String host = URI.create(uc.getUrltocheck()).getHost();

		String tld = host.substring(host.lastIndexOf("."));

		String[] h = host.split("\\.");
		
		
		TreeItem<UrlCheck> t = null;
		for(TreeItem<UrlCheck> t2 : root.getChildren()){
		    if(t2.getValue().getUrltocheck().equals(h[h.length-1])){
			t = t2;
		    }
		}
		
		if(t == null){
		    t = new TreeItem<>(new UrlCheck(h[h.length-1]));
		    root.getChildren().add(t);
		}

		TreeItem<UrlCheck> d = null;
		for(TreeItem<UrlCheck> t2 : t.getChildren()){
		    if(t2.getValue().getUrltocheck().equals(h[h.length-2])){
			d = t2;
		    }
		}
		
		if(d == null){
		    d = new TreeItem<>(new UrlCheck(h[h.length-2]));
		    t.getChildren().add(d);
		}

	       
		d.getChildren().add(new TreeItem<>(uc));

	    }catch(Exception u){
		System.out.println("Import not possible: " + uc.getUrltocheck());
	    }
	}
	treeTableView.setRoot(root);
	
	TreeTableColumn<UrlCheck, String> urlCol = new TreeTableColumn<>("Url");
	TreeTableColumn<UrlCheck, Integer> statusCol = new TreeTableColumn<>("Status");

	treeTableView.getColumns().setAll(urlCol, statusCol);

	urlCol.setCellValueFactory(new TreeItemPropertyValueFactory(rootCheck.urltocheckProperty().getName()));
	statusCol.setCellValueFactory(new TreeItemPropertyValueFactory(rootCheck.statusProperty().getName()));
    }


    @FXML
    public void checkHttp(ActionEvent event){

	//Optional mit direktem (linebyline) check oder hierarchisch optimiert (erst Domain)
	HttpClient client = HttpClient.newBuilder()
	    .version(HttpClient.Version.HTTP_2)
	    .followRedirects(HttpClient.Redirect.NEVER)
	    .connectTimeout(Duration.ofSeconds(20))
	    .build();
	
	for(int i=0;i<10;i++){
	    String url = urlChecks.get(i).getUrltocheck();

	    HttpRequest req = HttpRequest.newBuilder()
		.uri(URI.create(url))
		.GET()
		.build();

	    try{
		HttpResponse<String> response =
		    client.send(req, HttpResponse.BodyHandlers.ofString());

		urlChecks.get(i).setStatus(response.statusCode());
		
		if(response.statusCode()!=200){
		    System.out.println(url);
		    System.out.println(response.statusCode());
		}
	    }catch(Exception e){
		e.printStackTrace();
	    }
 	    
	}

	System.out.println("Http Check finished");
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
