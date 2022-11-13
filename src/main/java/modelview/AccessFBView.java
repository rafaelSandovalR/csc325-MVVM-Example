package modelview;

import com.mycompany.mvvmexample.App;
import viewmodel.AccessDataViewModel;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import com.mycompany.mvvmexample.FirestoreContext;
import com.mycompany.mvvmexample.FirestoreContext;
import com.mycompany.mvvmexample.FirestoreContext;
import com.mycompany.mvvmexample.FirestoreContext;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import javafx.util.converter.IntegerStringConverter;
import models.Person;

public class AccessFBView {

    @FXML
    private TextField nameField;
    @FXML
    private TextField majorField;
    @FXML
    private TextField ageField;
    @FXML
    private Button writeButton;
    @FXML
    private Button readButton;
    @FXML
    private Button deleteButton;
    @FXML
    private TableView outputField = new TableView<Person>();

    @FXML
    private TableColumn nameCol = new TableColumn<Person, String>("Name");
    @FXML
    private TableColumn majorCol = new TableColumn<Person, String>("Major");
    @FXML
    private TableColumn ageCol = new TableColumn<Person, String>("Age");

    private boolean key;
    private ObservableList<Person> listOfUsers = FXCollections.observableArrayList();
    private Person person;
    @FXML
    private Button switchScene;

    public ObservableList<Person> getListOfUsers() {
        return listOfUsers;
    }

    void initialize() {
        AccessDataViewModel accessDataViewModel = new AccessDataViewModel();
        nameField.textProperty().bindBidirectional(accessDataViewModel.userNameProperty());
        majorField.textProperty().bindBidirectional(accessDataViewModel.userMajorProperty());
        writeButton.disableProperty().bind(accessDataViewModel.isWritePossibleProperty().not());
        deleteButton.disableProperty().bind(Bindings.isEmpty(outputField.getSelectionModel().getSelectedItems()));
    }

    @FXML
    private void addRecord(ActionEvent event) {
        addData();
        clearForm();
    }

    @FXML
    private void writeRecord(ActionEvent event) {
        readFirebase();
    }

    public void addData() {

        DocumentReference docRef = App.fstore.collection("References").document(UUID.randomUUID().toString());
        // Add document data  with id "alovelace" using a hashmap
        Map<String, Object> data = new HashMap<>();

        data.put("Name", nameField.getText());
        data.put("Major", majorField.getText());
        data.put("Age", Integer.parseInt(ageField.getText()));
        //asynchronously write data
        ApiFuture<WriteResult> result = docRef.set(data);
    }

    public boolean readFirebase() {
        clearOutputData();
        //allows table to be edited
        outputField.setEditable(true);
        key = false;

        //asynchronously retrieve all documents
        ApiFuture<QuerySnapshot> future = App.fstore.collection("References").get();
        // future.get() blocks on response
        List<QueryDocumentSnapshot> documents;
        try {
            documents = future.get().getDocuments();
            if (documents.size() > 0) {
                System.out.println("Outing....");
                for (QueryDocumentSnapshot document : documents) {

                    person = new Person(document.getId(), String.valueOf(document.getData().get("Name")),
                            document.getData().get("Major").toString(),
                            Integer.parseInt(document.getData().get("Age").toString()));
                    listOfUsers.add(person);

                    //used to extract values from the objects
                    nameCol.setCellValueFactory(new PropertyValueFactory<Person, String>("name"));
                    majorCol.setCellValueFactory(new PropertyValueFactory<Person, String>("major"));
                    ageCol.setCellValueFactory(new PropertyValueFactory<Person, String>("age"));

                    //to make cells editable
                    nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
                    majorCol.setCellFactory(TextFieldTableCell.forTableColumn());
                    ageCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

                    //handles edits made to each cell
                    nameCol.setOnEditCommit(new EventHandler<CellEditEvent<Person, String>>() {

                        @Override
                        public void handle(CellEditEvent<Person, String> event) {
                            Person person = event.getRowValue();
                            person.setName(event.getNewValue());

                            updateDocument(person, "Name");
                        }
                    });

                    majorCol.setOnEditCommit(new EventHandler<CellEditEvent<Person, String>>() {

                        @Override
                        public void handle(CellEditEvent<Person, String> event) {
                            Person person = event.getRowValue();
                            person.setMajor(event.getNewValue());

                            updateDocument(person, "Major");
                        }
                    });

                    ageCol.setOnEditCommit(new EventHandler<CellEditEvent<Person, Integer>>() {

                        @Override
                        public void handle(CellEditEvent<Person, Integer> event) {
                            Person person = event.getRowValue();
                            person.setAge(event.getNewValue());

                            updateDocument(person, "Age");
                        }
                    });

                    outputField.getItems().add(person);

                }
            } else {
                System.out.println("No data");
            }
            key = true;

        } catch (InterruptedException | ExecutionException ex) {
            ex.printStackTrace();
        }
        return key;
    }

    private void updateDocument(Person person, String field) {
        if (field == "Name") {
            // (async) Update one field
            ApiFuture<WriteResult> future = App.fstore.collection("References").document(person.getId()).update(field, person.getName());
        } else if (field == "Major") {
            // (async) Update one field
            ApiFuture<WriteResult> future = App.fstore.collection("References").document(person.getId()).update(field, person.getMajor());
        } else if (field == "Age") {
            // (async) Update one field
            ApiFuture<WriteResult> future = App.fstore.collection("References").document(person.getId()).update(field, person.getAge());
        }
    }

    @FXML
    private void regRecord(ActionEvent event) {
        try {
            registerUserInFB();
        } catch (FirebaseAuthException ex) {
            Logger.getLogger(AccessFBView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void registerUserInFB() throws FirebaseAuthException {
        CreateRequest request = new CreateRequest()
                .setEmail("sandr2@farmingdale.edu")
                .setEmailVerified(false)
                .setPassword("CjkTF3OnpaRRGP09BWyhQOiOCXh1")
                .setPhoneNumber("+11234567890")
                .setDisplayName("John Doe")
                .setPhotoUrl("http://www.example.com/12345678/photo.png")
                .setDisabled(false);

        UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
        System.out.println("Successfully created new user: " + userRecord.getUid());
    }

    @FXML
    private void switchScene(ActionEvent event) {
    }

    private void clearForm() {
        nameField.clear();
        majorField.clear();
        ageField.clear();
        nameField.requestFocus();
    }

    private void clearOutputData() {
        outputField.getItems().removeAll(listOfUsers);
    }

    @FXML
    private void deleteRecord(ActionEvent event) {
        //determine which row is currently selected
        int row = outputField.getSelectionModel().getSelectedIndex();

        if (row >= 0) {
            Person p = (Person) outputField.getSelectionModel().getSelectedItem();
            outputField.getItems().remove(row);
            //clears selection of other rows
            outputField.getSelectionModel().clearSelection();

            deleteFirebase(p);
        }
    }

    //deletes document for specific person on firestore
    private void deleteFirebase(Person person) {
        // asynchronously delete a document
        ApiFuture<WriteResult> writeResult = App.fstore.collection("References").document(person.getId()).delete();

    }

}
