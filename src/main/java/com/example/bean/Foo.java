package com.example.bean;

import jakarta.inject.Named;

@Named
public class Foo {

    private String name;

    public Foo() {
        this.name = "fooのデフォルトの名前";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}