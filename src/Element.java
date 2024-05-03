import java.io.File;
import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;

class Element {
    
    @FXML 
    private CheckBox checkBox;
    private HBox hbox;
    private File file;
    private String pathFile;
    private ArrayList<Element> children = new ArrayList<Element>();
    private Element parent;

    Element(File f, CheckBox cb, HBox hbox, Element parent) {
        this.hbox = hbox;
        this.file = f;
        this.checkBox = cb;
        this.parent = parent;
        this.pathFile = file.getAbsolutePath();
    }

    File getFile() {
        return file;
    }

    CheckBox getCheckBox() {
        return checkBox;
    }

    ArrayList<Element> getChildren() {
        return children;
    }

    void addChildren(Element e) {
        children.add(e);
    }

    void clearChildren() {
        children.clear();
    }

    String getPathFile() {
        return pathFile;
    }

    HBox getHBox() {
        return hbox;
    }

    Element getParent() {
        return parent;
    }
}
