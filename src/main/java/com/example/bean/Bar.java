package com.example.bean;

import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named
public class Bar {

    private String name;

    @Inject
    private Foo foo;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Foo getFoo() {
        return foo;
    }
}