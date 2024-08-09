package com.example;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import jakarta.inject.Inject;
import jakarta.inject.Named;

public class Context {

    @SuppressWarnings("rawtypes")
    static Map<String, Class> types = new HashMap<>();
    static ConcurrentMap<String, Object> beans = new ConcurrentHashMap<>();

    @SuppressWarnings("rawtypes")
    static void register(String name, Class type) {
        types.put(name, type);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Object getBean(String name) {
        Class type = types.get(name);
        Objects.requireNonNull(type, name + " not found.");

        return beans.computeIfAbsent(name, key -> {
            return createObject(type);
        });
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void autoRegister() {
        try {

            URL res = Context.class.getResource("/" + Context.class.getName().replace('.', '/') + ".class");
            System.out.println(res.toURI().toString());

            // Path classPath = new File(res.toURI()).toPath().resolve("../../..");
            Path classPath = Paths.get("");
            System.out.println(classPath.toAbsolutePath());

            Files.walk(classPath).filter(p -> !Files.isDirectory(p)).filter(p -> p.toString().endsWith(".class"))
                    .map(p -> classPath.relativize(p)).map(p -> p.toString().replace(File.separatorChar, '.'))
                    .map(n -> n.substring(0, n.length() - 6)).forEach(n -> {
                        Class c;
                        try {
                            c = Class.forName(n);
                            if (c.isAnnotationPresent(Named.class)) {
                                String simpleName = c.getSimpleName();
                                register(simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1), c);
                            }
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (Exception e) {
            throw new RuntimeException("Error creating bean: " + e.getMessage(), e);
        }

    }

    @SuppressWarnings("unchecked")
    private static <T> T createObject(Class<T> type) {
        T object = (T) newInstance(type);
        for (Field field : type.getDeclaredFields()) {
            if (!field.isAnnotationPresent(Inject.class)) {
                continue;
            }
            field.setAccessible(true);
            try {
                field.set(object, getBean(field.getName()));
            } catch (Exception e) {
                throw new RuntimeException("Error creating bean: " + e.getMessage(), e);
            }
        }
        return object;
    }

    @SuppressWarnings("unchecked")
    private static Object newInstance(Class clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Error creating bean: " + e.getMessage(), e);
        }
    }

}
