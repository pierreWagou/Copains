package model;

import java.util.Locale;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Pays {

  private ObservableList<String> listPays;

  public Pays() {
    this.listPays = FXCollections.observableArrayList();

    this.getCountry();
  }

  private void getCountry() {
    String[] listCode = Locale.getISOCountries();
    for(String code : listCode) {
      Locale locale = new Locale("", code);
      String country = locale.getDisplayCountry();
      this.listPays.add(country);
    }
  }

  public ObservableList<String> getListPays() {
    return this.listPays;
  }
}
