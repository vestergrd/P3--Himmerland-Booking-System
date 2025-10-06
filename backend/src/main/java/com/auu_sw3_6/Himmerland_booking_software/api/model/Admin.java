package com.auu_sw3_6.Himmerland_booking_software.api.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;

@Entity
public class Admin extends User {

    @ElementCollection
    private List<String> caretakerNames;

    public Admin() {
        this.caretakerNames = new ArrayList<>();
    }

    public List<String> getCaretakerNames() {
        return caretakerNames;
    }

    public void setCaretakerNames(List<String> caretakerNames) {
        this.caretakerNames = caretakerNames;
    }

    public void addCaretakerName(String caretakerName) {
        this.caretakerNames.add(caretakerName);
    }

    public void removeCaretakerName(String caretakerName) {
        this.caretakerNames.removeIf(name -> name.equalsIgnoreCase(caretakerName.trim()));
    }
}
