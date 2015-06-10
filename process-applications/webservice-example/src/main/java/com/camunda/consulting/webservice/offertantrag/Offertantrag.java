package com.camunda.consulting.webservice.offertantrag;

import java.util.Date;

public class Offertantrag {
  
  private VersicherungsdeckungEnum anzahlReiseTeilnehmer;
  private Date versicherungsBeginn;
  private VersicherungsdauerEnum versicherungsDauer;
  private Paket enthaltenesPaket;
  
  public Offertantrag(VersicherungsdeckungEnum anzahlReiseTeilnehmer, Date versicherungsBeginn, VersicherungsdauerEnum versicherungsDauer,
      Paket enthaltenesPaket) {
    super();
    this.anzahlReiseTeilnehmer = anzahlReiseTeilnehmer;
    this.versicherungsBeginn = versicherungsBeginn;
    this.versicherungsDauer = versicherungsDauer;
    this.enthaltenesPaket = enthaltenesPaket;
  }
  
  public Offertantrag() {
    super();
  }

  public VersicherungsdeckungEnum getAnzahlReiseTeilnehmer() {
    return anzahlReiseTeilnehmer;
  }

  public void setAnzahlReiseTeilnehmer(VersicherungsdeckungEnum anzahlReiseTeilnehmer) {
    this.anzahlReiseTeilnehmer = anzahlReiseTeilnehmer;
  }

  public Date getVersicherungsBeginn() {
    return versicherungsBeginn;
  }

  public void setVersicherungsBeginn(Date versicherungsBeginn) {
    this.versicherungsBeginn = versicherungsBeginn;
  }

  public VersicherungsdauerEnum getVersicherungsDauer() {
    return versicherungsDauer;
  }

  public void setVersicherungsDauer(VersicherungsdauerEnum versicherungsDauer) {
    this.versicherungsDauer = versicherungsDauer;
  }

  public Paket getEnthaltenesPaket() {
    return enthaltenesPaket;
  }

  public void setEnthaltenesPaket(Paket enthaltenesPaket) {
    this.enthaltenesPaket = enthaltenesPaket;
  }

  
}
