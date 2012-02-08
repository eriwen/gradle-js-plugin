package com.eriwen.gradle.js.source.internal;

import org.gradle.api.Project;
import org.gradle.api.internal.Instantiator;
import org.gradle.api.internal.file.FileResolver;
import org.gradle.api.internal.project.ProjectInternal;

/**
 * Centralises access to internal Gradle API
 */
public abstract class InternalGradle {

    public static Instantiator toInstantiator(Project project) {
        return toProjectInternal(project).getServices().get(Instantiator.class);
    }

    public static ProjectInternal toProjectInternal(Project project) {
        return ((ProjectInternal)project);
    }

    public static FileResolver toFileResolver(Project project) {
        return toProjectInternal(project).getFileResolver();
    }
}
