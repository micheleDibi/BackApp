import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.application.Application;

public class App extends Application {

    private ArrayList<Element> masterElements = new ArrayList<Element>();
    private ArrayList<Element> originalMasterElement = new ArrayList<Element>();
    private ArrayList<Element> viewElements = new ArrayList<Element>();
    private ArrayList<Element> detailElements = new ArrayList<Element>();

    private ArrayList<String> checkedPath = new ArrayList<String>();

    private @FXML VBox masterVBox, viewVBox, detailVBox;
    private @FXML MenuItem currentPathMenu;
    private @FXML Button btnIndietro, btnStartBackup;

    private int utente_id = 0;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("fxml/main.fxml"));

        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add("resources/style.css");
        stage.setScene(scene);

        stage.getIcons().add(new Image("resources/backup.png"));
        stage.setTitle("BackApp");

        stage.show();

        masterVBox = (VBox) scene.lookup("#MasterVBox");
        viewVBox = (VBox) scene.lookup("#ViewVBox");
        detailVBox = (VBox) scene.lookup("#DetailVBox");

        currentPathMenu = (Menu) fxmlLoader.getNamespace().get("currentPath");
        currentPathMenu.setStyle("-fx-text-fill: black;");
        currentPathMenu.setText("/");

        btnIndietro = (Button) scene.lookup("#btn_indietro");

        btnIndietro.setOnAction(
                e -> {
                    if (masterElements.size() != 0) {
                        Element masterElement = masterElements.get(0);
                        Element parentElement = masterElement.getParent();

                        if (parentElement != null) {
                            Element grandParentElement = parentElement.getParent();

                            detailElements = viewElements;
                            displayElements(detailElements, detailVBox);

                            viewElements = masterElements;
                            displayElements(viewElements, viewVBox);

                            if (grandParentElement != null) {
                                masterElements = grandParentElement.getChildren();
                            } else {
                                masterElements = originalMasterElement;
                            }

                            currentPathMenu.setText(parentElement.getPathFile());
                            displayElements(masterElements, masterVBox);
                        }
                    }
                });

        btnStartBackup = (Button) scene.lookup("#btn_start_backup");
        DirectoryChooser directoryChooser = new DirectoryChooser();

        btnStartBackup.setOnAction(
                e -> {

                    if (checkedPath.size() != 0) {
                        File selectedDirectory = directoryChooser.showDialog(stage);

                        if (selectedDirectory != null) {
                            System.out.println(selectedDirectory.getAbsolutePath());

                            FileManager fm = new FileManager();

                            try {
                                fm.compressioneFile(checkedPath, selectedDirectory.getAbsolutePath());

                                Alert a = new Alert(AlertType.CONFIRMATION);
                                a.setTitle("Operazione completata");
                                a.setHeaderText("Operazione completata con successo.");
                                a.setContentText("Puoi trovare il backup crittografato: " + selectedDirectory.getAbsolutePath());
                                
                                a.showAndWait();
                            }
                            catch (Exception exc) {
                                exc.printStackTrace();
                            }

                        } else {
                            Alert a = new Alert(AlertType.ERROR);
                            a.setTitle("ERRORE");
                            a.setHeaderText("Nessun cartella di destinazione selezionata");
                            a.setContentText(
                                    "Si prega di notare che non è stata selezionata alcuna cartella di destinazione per il backup. Per procedere, si prega di selezionare la cartella di destinazione appropriata.");
                            a.showAndWait();
                        }
                    } else {
                        Alert a = new Alert(AlertType.ERROR);
                        a.setTitle("ERRORE");
                        a.setHeaderText("Nessun file selezionato");
                        a.setContentText(
                                "Si prega di notare che non è stato selezionato alcun file o cartella per il backup. Per procedere, si prega di selezionare le caselle di controllo corrispondenti ai file o alle cartelle di cui si desidera effettuare il backup.");
                        a.showAndWait();
                    }
                });

        for (File root : File.listRoots()) {
            masterElements.add(createElement(root, root.getPath(), null));
        }

        originalMasterElement = masterElements;

        displayElements(masterElements, masterVBox);
    }

    private void displayElements(ArrayList<Element> elements, VBox box) {
        Iterator<Element> iterator = elements.iterator();

        box.getChildren().clear();

        while (iterator.hasNext()) {
            box.getChildren().add(((Element) iterator.next()).getHBox());
        }
    }

    private void autoDisplayElements(Element element, ArrayList<Element> elements) {

        if (masterElements.contains(element)) {
            viewElements = elements;
            displayElements(viewElements, viewVBox);
        } else if (viewElements.contains(element)) {
            detailElements = elements;
            displayElements(elements, detailVBox);
        } else if (detailElements.contains(element)) {

            masterElements = viewElements;
            displayElements(masterElements, masterVBox);

            viewElements = detailElements;
            displayElements(viewElements, viewVBox);

            detailElements = elements;
            displayElements(detailElements, detailVBox);
        }

    }

    private void checkAllElement(Element element, Boolean bool) {
        if (element.getChildren() != null && element.getChildren().size() != 0) {
            Iterator<Element> iterator = element.getChildren().iterator();

            while (iterator.hasNext()) {
                Element el = (Element) iterator.next();

                if (bool) {
                    checkedPath.add(el.getPathFile());
                } else {
                    if (checkedPath.contains(el.getPathFile())) {
                        checkedPath.remove(el.getPathFile());
                    }
                }

                CheckBox checkbox = el.getCheckBox();
                checkbox.setSelected(bool);

                if (((File) el.getFile()).isDirectory()) {
                    checkAllElement(el, bool);
                }

            }
        }
    }

    private Element createElement(File file, String filename, Element parent) {

        CheckBox cb = new CheckBox();

        if (parent != null) {
            CheckBox cbParent = (CheckBox) parent.getCheckBox();
            cb.setSelected(cbParent.isSelected());
        }

        Hyperlink hp = new Hyperlink(filename);
        hp.getStyleClass().add("custom_hyperlink");

        HBox hbox = new HBox(cb, hp);

        Element element = new Element(file, cb, hbox, parent);

        hp.setOnAction(
                e -> {

                    if (file.isDirectory()) {

                        if (element.getChildren().size() == 0) {
                            element.clearChildren();

                            if (file.listFiles() != null && file.listFiles().length != 0) {
                                for (File fl : file.listFiles()) {
                                    element.addChildren(createElement(fl, fl.getName(), element));
                                }
                            }
                        }

                        currentPathMenu.setText(file.getAbsolutePath());

                        autoDisplayElements(element, element.getChildren());
                    }
                });

        String imageUrl = "";

        if (file.isDirectory()) {
            imageUrl = "resources/folder.png";
        } else {
            imageUrl = "resources/document.png";
        }

        Image image = new Image(imageUrl);
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(25);
        imageView.setFitWidth(20);
        imageView.setPreserveRatio(true);

        cb.setGraphic(imageView);

        cb.selectedProperty().addListener(
                (observable, oldValue, newValue) -> {

                    if (newValue) {
                        // Inserisco il percorso assouluto del file / directory selezionato nell'elenco
                        // dei percorsi

                        if (file.isDirectory()) {
                            // devo cercare tutti i figli
                            manageCheckedPath(file, newValue);
                        } else {
                            checkedPath.add(file.getAbsolutePath());
                        }
                    } else {

                        if (file.isDirectory()) {
                            manageCheckedPath(file, newValue);
                        } else {
                            if (checkedPath.contains(file.getAbsolutePath())) {
                                checkedPath.remove(file.getAbsolutePath());
                            }
                        }

                    }

                    checkAllElement(element, newValue);
                });

        return element;
    }

    private void manageCheckedPath(File file, Boolean bool) {
        if (file != null) {
            if (file.listFiles() != null && file.listFiles().length != 0) {
                
                for (File child : file.listFiles()) {

                    if (child.isDirectory()) {
                        manageCheckedPath(child, bool);
                    } else {

                        if (bool) {
                            checkedPath.add(child.getAbsolutePath());
                        } else {
                            if (checkedPath.contains(child.getAbsolutePath())) {
                                checkedPath.remove(child.getAbsolutePath());
                            }
                        }

                    }

                }
            }
        }
    }

    public App(int utente_id) {
        this.utente_id = utente_id;

        launch();
    }
}