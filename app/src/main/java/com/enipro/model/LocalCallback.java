package com.enipro.model;

@FunctionalInterface
public interface LocalCallback<T> {

    void respond(T object);
}