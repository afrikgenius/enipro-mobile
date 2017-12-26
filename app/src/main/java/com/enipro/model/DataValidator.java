package com.enipro.model;


/**
 * This interface provides a common ground for validation to be done
 * in the application
 */
public interface DataValidator {

    /**
     * Called to validate every data.
     * @param data the data to be validated
     */
    void validate(String[] data);


}
