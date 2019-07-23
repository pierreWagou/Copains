package controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Vector;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import jfxtras.scene.control.ToggleGroupValue;
import javafx.util.Callback;
import model.Group;
import model.Model;
import model.Pays;
import model.Adresse;
import model.Contact;

public class Controller {

  private Model model;
  private Contact editingContact;
  private Contact originalContact;
  private Adresse editingAdresse;
  private Pays pays;
  private Image contactImage;
  private Image groupImage;

  @FXML
  private TextField prenomTextField;
  @FXML
  private TextField nomTextField;
  @FXML
  private TextField rueTextField;
  @FXML
  private TextField codeTextField;
  @FXML
  private TextField villeTextField;
  @FXML
  private ChoiceBox<String> paysChoiceBox;
  @FXML
  private DatePicker naissanceDatePicker;
  @FXML
  private RadioButton mRadioButton;
  @FXML
  private RadioButton fRadioButton;
  @FXML
  private Button validerButton;
  @FXML
  private Button addButton;
  @FXML
  private Button deleteButton;
  @FXML
  private TreeView<Object> treeView;
  @FXML
  private AnchorPane contactPane;

  public Controller() throws FileNotFoundException, IOException {
    this.model = new Model();
    this.pays = new Pays();
    this.editingAdresse = new Adresse ();
    this.editingContact = new Contact("", "", editingAdresse, LocalDate.of(1998, 5, 7), "");
  }

  @FXML
  public void initialize() {
    this.contactPane.visibleProperty().set(false);
    this.prenomTextField.textProperty().bindBidirectional(this.editingContact.prenomProperty());
    this.nomTextField.textProperty().bindBidirectional(this.editingContact.nomProperty());
    this.rueTextField.textProperty().bindBidirectional(this.editingContact.getAdresse().rueProperty());
    this.codeTextField.textProperty().bindBidirectional(this.editingContact.getAdresse().codeProperty());
    this.villeTextField.textProperty().bindBidirectional(this.editingContact.getAdresse().villeProperty());
    this.paysChoiceBox.setItems(this.pays.getListPays());
    this.paysChoiceBox.valueProperty().bindBidirectional(this.editingContact.getAdresse().paysProperty());
    this.naissanceDatePicker.valueProperty().bindBidirectional(this.editingContact.naissanceProperty());
    ToggleGroupValue<String> groupValue = new ToggleGroupValue<String>();
    groupValue.add(this.mRadioButton, "masculin");
    groupValue.add(this.fRadioButton, "feminin");
    groupValue.valueProperty().bindBidirectional(this.editingContact.sexeProperty());

    this.editingContact.getFields().addListener((ListChangeListener<String>) f -> {
      while(f.next()) {
        if(f.wasAdded()) {
          invalidFields(f.getAddedSubList());;
        }
        if(f.wasRemoved()) {
          validFields(f.getRemoved());
        }
      }
    });

    groupImage = new Image("file:src/images/group.png");
    contactImage = new Image("file:src/images/contact.png");

    TreeItem<Object> root = new TreeItem<Object>("Fiche de contacts");
    root.setExpanded(true);
    this.treeView.setRoot(root);
    this.treeView.setEditable(true);
    treeView.setCellFactory(new Callback<TreeView<Object>, TreeCell<Object>>() {
			@Override
			public TreeCell<Object> call(TreeView<Object> p) {
				return new TextFieldTreeCellImpl();
			}
		});

    //Ajouter les Noeuds pr�sent d�s le d�part
    for(Group group: this.model.getListGroup()) {
    	TreeItem<Object> groupTreeItem = new TreeItem<Object>(group, new ImageView(groupImage));
		  this.treeView.getRoot().getChildren().add(groupTreeItem);
  		for(Contact contact: group.getListContact()) {
  			TreeItem<Object> contactTreeItem = new TreeItem<Object>(contact, new ImageView(contactImage));
  			groupTreeItem.getChildren().add(contactTreeItem);
  		}
      changeContact(group);
    }

    //Ajouter les noeuds au fur et � mesure de la modification des listes du mod�le
    this.model.getListGroup().addListener((ListChangeListener<Group>) g -> {
      while (g.next()) {
        if (g.wasAdded()) {
    	  	for(Group group : g.getAddedSubList()) {
            changeContact(group);
            TreeItem<Object> newGroupTreeItem = new TreeItem<Object>(group, new ImageView(groupImage));
            this.treeView.getRoot().getChildren().add(newGroupTreeItem);
            this.treeView.getSelectionModel().select(newGroupTreeItem);
          }
      	}
        if (g.wasRemoved()) {
          for(Group group : g.getRemoved()) {
          TreeItem<Object> deletedGroupItem = findGroupItem(group);
          this.treeView.getRoot().getChildren().remove(deletedGroupItem);
          this.treeView.getSelectionModel().select(this.treeView.getRoot());
          }
        }
      }
    });

    this.treeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
        if(newValue!=this.treeView.getRoot() && newValue.getParent()!=this.treeView.getRoot()) {
        	this.contactPane.visibleProperty().set(true);
        	this.originalContact = (Contact) newValue.getValue();
        	this.copyContact(this.originalContact, this.editingContact);
        }
        else {
        	this.contactPane.visibleProperty().set(false);
        }
    });
	}

   private void invalidFields(List<? extends String> list) {
     for(String field : list) {
       switch(field) {
         case "prenom":
           this.prenomTextField.setStyle("-fx-border-color:red");
           this.prenomTextField.setTooltip(new Tooltip("Le prénom doit être renseigné"));
           break;
         case "nom":
           this.nomTextField.setStyle("-fx-border-color:red");
           this.nomTextField.setTooltip(new Tooltip("Le nom doit être renseigné"));
           break;
         case "rue":
           this.rueTextField.setStyle("-fx-border-color:red");
           this.rueTextField.setTooltip(new Tooltip("La rue doit être renseigné"));
           break;
         case "code":
           this.codeTextField.setStyle("-fx-border-color:red");
           this.codeTextField.setTooltip(new Tooltip("Le code doit être renseigné"));
           break;
         case "ville":
           this.villeTextField.setStyle("-fx-border-color:red");
           this.villeTextField.setTooltip(new Tooltip("La ville doit être renseigné"));
           break;
         case "pays":
           this.paysChoiceBox.setStyle("-fx-border-color:red");
           this.paysChoiceBox.setTooltip(new Tooltip("Le pays doit être renseigné"));
           break;
         case "naissance":
           this.naissanceDatePicker.setStyle("-fx-border-color:red");
           this.naissanceDatePicker.setTooltip(new Tooltip("La date doit être renseigné"));
           break;
       }
     }
   }

   private void validFields(List<? extends String> list) {
     for(String field : list) {
       switch(field) {
         case "prenom":
           this.prenomTextField.setStyle("");
           this.prenomTextField.setTooltip(new Tooltip(null));
           break;
         case "nom":
           this.nomTextField.setStyle("");
           this.nomTextField.setTooltip(new Tooltip(null));
           break;
         case "rue":
           this.rueTextField.setStyle("");
           this.rueTextField.setTooltip(new Tooltip(null));
           break;
         case "code":
           this.codeTextField.setStyle("");
           this.codeTextField.setTooltip(new Tooltip(null));
           break;
         case "ville":
           this.villeTextField.setStyle("");
           this.villeTextField.setTooltip(new Tooltip(null));
           break;
         case "pays":
           this.paysChoiceBox.setStyle("");
           this.paysChoiceBox.setTooltip(new Tooltip(null));
           break;
         case "naissance":
           this.naissanceDatePicker.setStyle("");
           this.naissanceDatePicker.setTooltip(new Tooltip(null));
           break;
       }
     }
   }

   private TreeItem<Object> findGroupItem(Group group) {
     for(TreeItem<Object> groupItem : this.treeView.getRoot().getChildren()) {
       if(groupItem.getValue()==group) {
          return groupItem;
       }
     }
     return null;
   }

  private void changeContact(Group group) {
    group.getListContact().addListener((ListChangeListener<Contact>) c -> {
      TreeItem<Object> groupParent = findGroupItem(group);
      while(c.next()) {
        if(c.wasAdded()) {
          for(Contact contact: c.getAddedSubList()) {
            TreeItem<Object> contactItem = new TreeItem<Object>(contact, new ImageView(contactImage));
            groupParent.getChildren().add(contactItem);
            groupParent.setExpanded(true);
            treeView.getSelectionModel().select(contactItem);
          }
        }
        if(c.wasRemoved()) {
          for(Contact contact : c.getRemoved()) {
            TreeItem<Object> deletedContact = new TreeItem<Object>();
            for(TreeItem<Object> contactItem: groupParent.getChildren()) {
              if(contactItem.getValue()==contact) {
                deletedContact = contactItem;
              }
            }
            groupParent.getChildren().remove(deletedContact);
            this.treeView.getSelectionModel().select(groupParent);
          }
        }
      }
    });
  }

  @FXML
  private void handleValiderAction() {
    if(this.editingContact.getFields().size()==0) {
      TreeItem<Object> selectedNode = this.treeView.getSelectionModel().getSelectedItem();
      this.copyContact(editingContact, originalContact);
      Contact tmp = new Contact();
      this.copyContact(originalContact, tmp);
      Group parentGroup = (Group) selectedNode.getParent().getValue();
      parentGroup.getListContact().remove(originalContact);
      parentGroup.getListContact().add(tmp);
    }
  }

  @FXML
  private void handleAddAction() {
    if(this.treeView.getSelectionModel().getSelectedItem()==null) {
	    this.treeView.getSelectionModel().select(this.treeView.getRoot());
    }
    TreeItem<Object> selectedNode = this.treeView.getSelectionModel().getSelectedItem();
	  if(selectedNode==this.treeView.getRoot()) {
		  Group group = new Group ();
		  this.model.getListGroup().add(group);
	  }
	  else if(selectedNode.getParent()==treeView.getRoot()) {
      Contact contact = new Contact();
      Group groupParent = (Group) selectedNode.getValue();
      groupParent.getListContact().add(contact);
	  }
  }

  @FXML
  private void handleDeleteAction() {
    if(this.treeView.getSelectionModel().getSelectedItem()!=null) {
  	  TreeItem<Object> selectedNode = this.treeView.getSelectionModel().getSelectedItem();
  	  if (selectedNode!=this.treeView.getRoot()) {
  		  if(selectedNode.getParent()==treeView.getRoot()) {
  			  Group g = (Group) selectedNode.getValue();
          g.getListContact().clear();
  			  this.model.getListGroup().remove(g);
  		  }
  		  else {
  			  Group parentGroup = (Group) selectedNode.getParent().getValue();
  			  Contact c = (Contact) selectedNode.getValue();
  			  parentGroup.getListContact().remove(c);
  		  }
  	  }
      else {
        this.model.getListGroup().clear();
      }
    }
  }

  @FXML
  private void handleSaveAction() throws FileNotFoundException, IOException {
    FileChooser fileChooser = new FileChooser();
    File file = fileChooser.showSaveDialog(new Stage());
    if(file!=null) {
      this.model.setFile(file);
    }
    this.model.save();
  }

  @FXML
  private void handleLoadAction() throws FileNotFoundException, IOException, ClassNotFoundException {
    this.treeView.getSelectionModel().select(this.treeView.getRoot());
    this.model.load();
    for(Group group: this.model.getListGroup()) {
      Vector<Contact> tmp = new Vector<Contact>();
      for(Contact contact: group.getListContact()) {
        tmp.add(contact);
      }
      group.getListContact().clear();
      for(Contact contact: tmp) {
        group.getListContact().add(contact);
      }
    }
    this.treeView.getSelectionModel().select(this.treeView.getRoot());
  }

  //mettre les champs de c1 dans c2
  public void copyContact (Contact c1, Contact c2) {
	  c2.setNom(c1.getNom());
	  c2.setPrenom(c1.getPrenom());
	  this.copyAdresse(c1.getAdresse(), c2.getAdresse());
	  c2.setNaissance(c1.getNaissance());
	  c2.setSexe(c1.getSexe());
  }

  public void copyAdresse(Adresse a1, Adresse a2) {
	  a2.setCode(a1.getCode());
	  a2.setVille(a1.getVille());
	  a2.setPays(a1.getPays());
	  a2.setRue(a1.getRue());
  }

  private final class TextFieldTreeCellImpl extends TreeCell<Object> {
		private TextField textField;
		public TextFieldTreeCellImpl() {
		}
		@Override
		public void startEdit() {
			if (treeView.getSelectionModel().getSelectedItem().getParent() == treeView.getRoot()) {
				super.startEdit();
				if (textField == null) {
					Group g = (Group) treeView.getSelectionModel().getSelectedItem().getValue();
					createTextField(g);
				}
				setText(null);
				setGraphic(textField);
				textField.selectAll();
			}
		}
		@Override
		public void updateItem(Object item, boolean empty) {
			super.updateItem(item, empty);
			if (empty) {
				setText(null);
				setGraphic(null);
			} else {
				if (isEditing()) {
					if (textField != null) {
						textField.setText(getString());
					}
					setText(null);
					setGraphic(textField);
				} else {
					setText(getString());
					setGraphic(getTreeItem().getGraphic());
				}
			}
		}
		private void createTextField(Group g) {
			textField = new TextField(getString());
			textField.setOnKeyReleased(new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent t) {
					if (t.getCode() == KeyCode.ENTER) {
						commitEdit(textField.getText());
						g.setName(textField.getText());
					}
				}
			});
		}
		private String getString() {
			return getItem() == null ? "" : getItem().toString();
		}
	}
}
