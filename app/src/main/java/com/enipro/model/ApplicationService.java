package com.enipro.model;

import android.view.View;

import com.enipro.presentation.base.MvpPresenter;

import java.util.HashMap;

/**
 * The application service of run on a separate thread from the application thread.
 * The work of the application service is numerous. Two services in the application
 * cannot occur simultaneously except otherwise stated by a variant of @ApplicationService.
 */
public abstract class ApplicationService {

    /* Private Instance Variable */

    /* Running services in the application. */
    private static HashMap<String, ApplicationService> runningServices = new HashMap<>();


    /**
     * The constructor of the application service cannot be called by
     * outside classes. Some of the services running under @ApplicationService is a singleton
     * and has only an instance.
     */
    protected ApplicationService() {
    }

    public static ApplicationService getInstance(ServiceType type) {
        ApplicationService applicationService = null;
        switch (type) {
            case ValidationService:
                String serviceName = type.toString();
                if (runningServices.containsKey(serviceName))
                    applicationService = runningServices.get(serviceName);
                else {
                    applicationService = new ValidationService();

                    // add the validation service to the list of running services.
                    runningServices.put(serviceName,applicationService);
                }

                break;
            default:
                throw new NullPointerException("Invalid service type.");
        }
        return applicationService;
    }

    /**
     * Registers a view in an activity in the application where the service also registers a listener
     * for the view and waits for the event to occur before taking necessary action.
     *
     * @param presenter the presenter in the application that registers the listener.
     * @param view     the view in the activity that triggers the application code to run.
     * @param listener the type of listener the view is bounded to.
     */
    public abstract void register(MvpPresenter presenter, View view, String listener, EditTextDataExtractor _extractor) throws Exception;


}
