package com.eriwen.gradle.js.source.internal;

import org.gradle.api.Project;
import org.gradle.api.internal.file.FileResolver;
import org.gradle.internal.reflect.Instantiator;

import java.lang.reflect.Method;

/**
 * Centralises access to internal Gradle API
 */
public abstract class InternalGradle {

    public static Instantiator toInstantiator(Project project) {
        try {
            Method getServices = project.getClass().getMethod("getServices");
            Object serviceFactory = getServices.invoke(project);
            Method get = serviceFactory.getClass().getMethod("get", Class.class);
            return (Instantiator) get.invoke(serviceFactory, Instantiator.class);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static FileResolver toFileResolver(Project project) {
        try {
            Method getFileResolver =  project.getClass().getMethod("getFileResolver");
            return (FileResolver) getFileResolver.invoke(project);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
