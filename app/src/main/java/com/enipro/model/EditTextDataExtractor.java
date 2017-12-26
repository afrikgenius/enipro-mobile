package com.enipro.model;

import android.app.Activity;
import android.widget.EditText;


/**
 * This class extracts data from a form and passes it as asn array to the calling object.
 * The class takes in a view instance an id's of edit text objects and extracts
 */
public class EditTextDataExtractor {

    /* Private Instance Variables */

    /* The id's of the edit text view */
    private int[] viewIds;

    private EditText[] editTexts;

    /* The parent view of the view's with id's. */
    private Activity parentView;

    public EditTextDataExtractor(Activity parentView, int[] viewIds) {
        this.parentView = parentView;
        this.viewIds = viewIds;
    }

    public EditTextDataExtractor(EditText[] editTexts){
        this.editTexts = editTexts;
    }


    /**
     * Extracts the data from the edit text objects and returns the extracted data.
     * All the extracted data from the views are stored as strings and left for the calling
     * object to convert to needed type.
     * @return the extracted data.
     */
    public String[] extract() {
        String[] data = new String[viewIds.length];
        for(int i = 0;i < viewIds.length;i++)
            data[i] = ((EditText) parentView.findViewById(viewIds[i])).getText().toString();
        return data;
    }
}