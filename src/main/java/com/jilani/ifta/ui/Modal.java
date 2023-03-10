package com.jilani.ifta.ui;

import com.jilani.ifta.fatwa.Fatwa;

import java.util.ArrayList;
import java.util.List;

public class Modal {
    private String title = "Actions";
    private String id;
    private Fatwa fatwa;
    private List<Button> buttons = new ArrayList<>();

    public Modal() {
    }

    public void addApproveButton(long fatwaId) {
        Button button = new Button();
        button.setName("Approve");
        button.setUrl("/asked/"+fatwaId+"/approve");
        button.setClasss("btn mb-1 btn-success");
        addButton(button);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void addAnswerButton(long fatwaId) {
        Button button = new Button();
        button.setName("Answer");
        button.setUrl("/asked/"+fatwaId+"/answer");
        button.setClasss("btn mb-1 btn-secondary");
        addButton(button);
    }

    public void addDeleteButton(long fatwaId) {
        Button button = new Button();
        button.setName("Delete");
        button.setUrl("/asked/"+fatwaId+"/delete");
        button.setClasss("btn mb-1 btn-danger");
        addButton(button);
    }

    public void addDisapproveButton(long fatwaId) {
        Button button = new Button();
        button.setName("Disapprove");
        button.setUrl("/asked/"+fatwaId+"/disapprove");
        button.setClasss("btn mb-1 btn-warning");
        addButton(button);
    }

    public void addEditButton(long fatwaId) {
        Button button = new Button();
        button.setName("Edit");
        button.setUrl("/asked/"+fatwaId+"/edit");
        button.setClasss("btn mb-1 btn-info");
        addButton(button);
    }

    public void addDeselectButton(long fatwaId) {
        Button button = new Button();
        button.setName("Deselect");
        button.setUrl("/asked/"+fatwaId+"/deselect");
        button.setClasss("btn mb-1 btn-dark");
        addButton(button);
    }

    public void addCloseButton() {
        Button button = new Button();
        button.setName("Close");
        button.setUrl(null);
        button.setCloseBtn(true);
        button.setClasss("btn btn-outline-dark");
        addButton(button);
    }

    public Modal(String title) {
        this.title = title;
    }

    public void addButton(Button button) {
        this.buttons.add(button);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Fatwa getFatwa() {
        return fatwa;
    }

    public void setFatwa(Fatwa fatwa) {
        this.fatwa = fatwa;
    }

    public List<Button> getButtons() {
        return buttons;
    }

    public void setButtons(List<Button> buttons) {
        this.buttons = buttons;
    }
}
