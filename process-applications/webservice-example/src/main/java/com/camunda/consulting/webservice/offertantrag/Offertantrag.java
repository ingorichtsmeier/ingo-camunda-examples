package com.camunda.consulting.webservice.offertantrag;

import java.util.Date;

public class Offertantrag {
  
  private VersicherungsdeckungEnum anzahlreiseTeilnehmer;
  private Date versicherungsbeginn;
  private VersicherungsdauerEnum versicherungsDauer;
  private Paket enthaltenesPaket;
  
  public Offertantrag(VersicherungsdeckungEnum anzahlreiseTeilnehmer, Date versicherungsbeginn, VersicherungsdauerEnum versicherungsDauer,
      Paket enthaltenesPaket) {
    super();
    this.anzahlreiseTeilnehmer = anzahlreiseTeilnehmer;
    this.versicherungsbeginn = versicherungsbeginn;
    this.versicherungsDauer = versicherungsDauer;
    this.enthaltenesPaket = enthaltenesPaket;
  }

  public VersicherungsdeckungEnum getAnzahlreiseTeilnehmer() {
    return anzahlreiseTeilnehmer;
  }

  public void setAnzahlreiseTeilnehmer(VersicherungsdeckungEnum anzahlreiseTeilnehmer) {
    this.anzahlreiseTeilnehmer = anzahlreiseTeilnehmer;
  }

  public Date getVersicherungsbeginn() {
    return versicherungsbeginn;
  }

  public void setVersicherungsbeginn(Date versicherungsbeginn) {
    this.versicherungsbeginn = versicherungsbeginn;
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
