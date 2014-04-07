package com.eriwen.gradle.js.source.internal;

import java.lang.reflect.Method;
import org.gradle.api.Project;
import org.gradle.api.internal.file.FileResolver;
import org.gradle.internal.reflect.Instantiator;
import org.gradle.api.internal.project.ProjectInternal;

/**
 * Centralises access to internal Gradle API
 */
public abstract class InternalGradle {

    public static Instantiator toInstantiator(Project project) {
        try {
            // Make compatible with different gradle versions.
            ProjectInternal projectInternal = toProjectInternal(project);
            Method getServices = projectInternal.getClass().getMethod("getServices");
            Object services = getServices.invoke(projectInternal);
            Method get = services.getClass().getMethod("get", Class.class);
            return (Instantiator)get.invoke(services, Instantiator.class);
        } catch (Exception e) {
            return null;
        }
    }

    public static ProjectInternal toProjectInternal(Project project) {
        return ((ProjectInternal)project);
    }

    public static FileResolver toFileResolver(Project project) {
        return toProjectInternal(project).getFileResolver();
    }
}
