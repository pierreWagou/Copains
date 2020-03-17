package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Group;

public class Model {

  private ObservableList<Group> listGroup;
  private File file;

  public Model() {
	  this.listGroup = FXCollections.observableArrayList();
    this.file = new File("./src/save");

	  Group amis = new Group();
    amis.setName("Amis");
	  Group famille = new Group();
    famille.setName("Famille");
	  Group travail = new Group();
    travail.setName("Travail");

	  Adresse addressePierre = new Adresse ("15 rue saint nicolas", "60200", "Compiegne", "France");
	  Contact PierreRomon = new Contact("Romon", "Pierre", addressePierre, LocalDate.of(1997, 3, 21), "masculin");
	  amis.getListContact().add(PierreRomon);

	  Adresse addresseNico = new Adresse ("16 rue Marc Antoine Charpentier", "95520", "Osny", "France");
	  Contact NicolasSchlegel = new Contact("Nicolas", "Schlegel", addresseNico, LocalDate.of(1999, 1, 28), "masculin");
	  famille.getListContact().add(NicolasSchlegel);

	  Adresse addresseEtud1 = new Adresse ("15 rue du manoir", "60200", "Compiegne", "France");
	  Contact NF28_Etud1 = new Contact("Etud1", "NF28", addresseEtud1, LocalDate.of(1998, 5, 7), "masculin");
	  travail.getListContact().add(NF28_Etud1);

	  this.listGroup.addAll(amis, famille, travail);
  }

  public ObservableList<Group> getListGroup() {
    return this.listGroup;
  }

  public void setFile(File f) {
    this.file = f;
  }

  public void save() throws FileNotFoundException, IOException {
    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(this.file));
    oos.writeObject(new LinkedHashSet<Group>(listGroup));
    oos.close();
  }

  @SuppressWarnings("unchecked")
public void load() throws FileNotFoundException, IOException, ClassNotFoundException {
    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(this.file));
    listGroup.clear();
    ((Set<Group>) ois.readObject()).forEach(g -> {
      listGroup.add(g);
    });
    ois.close();
  }
}
