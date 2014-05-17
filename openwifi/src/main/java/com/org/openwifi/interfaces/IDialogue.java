package com.org.openwifi.interfaces;

/**
 * Created by jack gurulian
 */
public interface IDialogue {
    /**
     * Builds the alert dialogue, without presenting it
     */
    public void build();

    /**
     * Presents the alert dialogue
     */
    public void showAlert();

    /**
     * Hides the alert dialogue, without dismissing it
     */
    public void hideAlert();

    /**
     * Dismisses the alert dialogue
     */
    public void dismissAlert();
}
