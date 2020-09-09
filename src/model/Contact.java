package model;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.time.LocalDate;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Contact implements Externalizable {

  private StringProperty prenom;
  private StringProperty nom;
  private ObjectProperty<Adresse> adresse;
  private ObjectProperty<LocalDate> naissance;
  private StringProperty sexe;
  private ObservableList<String> fields;

  public Contact() {
    this.prenom = new SimpleStringProperty("", "prenom", "");
    this.nom = new SimpleStringProperty("", "nom", "");
    this.adresse = new SimpleObjectProperty<Adresse>(new Adresse());
    this.naissance = new SimpleObjectProperty<LocalDate>("", "naissance", LocalDate.of(1997, 3, 21));
    this.sexe = new SimpleStringProperty("", "sexe", "masculin");
    this.fields = FXCollections.observableArrayList();
    incompleteFields();
  }

  public Contact(String nom, String prenom, Adresse adresse, LocalDate date, String sexe) {
    this.prenom = new SimpleStringProperty("", "prenom", prenom);
    this.nom = new SimpleStringProperty("", "nom", nom);
    this.adresse = new SimpleObjectProperty<Adresse>(adresse);
    this.naissance = new SimpleObjectProperty<LocalDate>("", "naissance", date);
    this.sexe = new SimpleStringProperty("", "sexe", sexe);
    this.fields = FXCollections.observableArrayList();
    incompleteFields();
  }

  private void incompleteFields(){
    checkField(this.prenom);
    checkField(this.nom);
    checkField(this.getAdresse().rueProperty());
    checkField(this.getAdresse().codeProperty());
    checkField(this.getAdresse().villeProperty());
    checkField(this.getAdresse().paysProperty());
    //checkField(this.naissance);
    checkField(this.sexe);
  }

  private void checkField(StringProperty field) {
    field.addListener((observable, oldValue, newValue)-> {
      if(newValue.equals("")) {
        fields.add(field.getName());
      }
      else {
        fields.remove(field.getName());
      }
    });
  }

  public String getPrenom() {
    return this.prenom.get();
  }

  public StringProperty prenomProperty() {
    return this.prenom;
  }

  public void setPrenom(String prenom) {
    this.prenom.set(prenom);
  }

  public String getNom() {
    return this.nom.get();
  }

  public StringProperty nomProperty() {
    return this.nom;
  }

  public void setNom(String nom) {
    this.nom.set(nom);
  }

  public Adresse getAdresse() {
    return this.adresse.get();
  }

  public ObjectProperty<Adresse> adresseProperty() {
    return this.adresse;
  }

  public void setAdresse(Adresse adresse) {
    this.adresse.set(adresse);
  }

  public LocalDate getNaissance() {
    return this.naissance.get();
  }

  public ObjectProperty<LocalDate> naissanceProperty() {
    return this.naissance;
  }

  public void setNaissance(LocalDate naissance) {
    this.naissance.set(naissance);
  }

  public String getSexe() {
    return this.sexe.get();
  }

  public StringProperty sexeProperty() {
    return this.sexe;
  }

  public void setSexe(String sexe) {
    this.sexe.set(sexe);
  }

  public ObservableList<String> getFields() {
    return this.fields;
  }

  @Override
  public String toString() {
      return this.nom.getValue().toString() + " " + this.prenom.getValue().toString();
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeUTF(getNom());
    out.writeUTF(getPrenom());
    out.writeObject(getAdresse());
    out.writeUTF(getSexe());
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
	  this.setNom(in.readUTF());
	  this.setPrenom(in.readUTF());
	  this.setAdresse((Adresse) in.readObject());
	  this.setSexe(in.readUTF());
  }
}
