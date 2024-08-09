package com.example;

import com.example.bean.Bar;
import com.example.bean.Foo;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) {
        System.out.println("Hello World!");

        // Context.register("foo", Foo.class);
        // Context.register("bar", Bar.class);

        Context.autoRegister();

        Bar bar = (Bar) Context.getBean("bar");
        System.out.println(bar.getClass().getName());

        System.out.println(bar.getFoo().getName());
    }

}
