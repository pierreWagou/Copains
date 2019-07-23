package model;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Adresse implements Externalizable {
  private StringProperty rue;
  private StringProperty code;
  private StringProperty ville;
  private StringProperty pays;

  public Adresse() {
    this.rue = new SimpleStringProperty("", "rue", "");
    this.code = new SimpleStringProperty("", "code", "");
    this.ville = new SimpleStringProperty("", "ville", "");
    this.pays = new SimpleStringProperty("", "pays", "");
  }

  public Adresse(String rue, String code, String ville, String pays) {
	    this.rue = new SimpleStringProperty("", "rue", rue);
	    this.code = new SimpleStringProperty("", "code", code);
	    this.ville = new SimpleStringProperty("", "ville", ville);
	    this.pays = new SimpleStringProperty("", "pays", pays);
	  }

  public String getRue() {
    return this.rue.get();
  }

  public StringProperty rueProperty() {
    return this.rue;
  }

  public void setRue(String rue) {
    this.rue.set(rue);
  }

  public String getCode() {
    return this.code.get();
  }

  public StringProperty codeProperty() {
    return this.code;
  }

  public void setCode(String code) {
    this.code.set(code);
  }

  public String getVille() {
    return this.ville.get();
  }

  public StringProperty villeProperty() {
    return this.ville;
  }

  public void setVille(String ville) {
    this.ville.set(ville);
  }

  public String getPays() {
    return this.pays.get();
  }

  public StringProperty paysProperty() {
    return this.pays;
  }

  public void setPays(String pays) {
    this.pays.set(pays);
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeUTF(getRue());
    out.writeUTF(getCode());
    out.writeUTF(getVille());
    out.writeUTF(getPays());
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    this.setRue(in.readUTF());
    this.setCode(in.readUTF());
    this.setVille(in.readUTF());
    this.setPays(in.readUTF());
  }
}
