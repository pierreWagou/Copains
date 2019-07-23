package model;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.LinkedHashSet;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Contact;

public class Group implements Externalizable {

	private String name;
	private ObservableList<Contact> listContact;

	public Group() {
		this.name = new String("New group");
		this.listContact = FXCollections.observableArrayList();
  }

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name ;
	}

	public ObservableList<Contact> getListContact() {
		return this.listContact ;
	}

	@Override
  public String toString() {
      return this.name;
  }

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
      out.writeUTF(getName());
      out.writeObject(new LinkedHashSet<Contact>(listContact));
  }

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		this.setName(in.readUTF());
		listContact.clear();
		((Set<Contact>) in.readObject()).forEach(c -> {
			listContact.add(c);
		});
	}
}
