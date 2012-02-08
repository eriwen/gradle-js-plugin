package com.eriwen.gradle.js.source.internal;

import com.eriwen.gradle.js.source.JavaScriptSourceSet;
import com.eriwen.gradle.js.source.JavaScriptSourceSetContainer;
import org.gradle.api.Project;
import org.gradle.api.internal.AbstractNamedDomainObjectContainer;

public class DefaultJavaScriptSourceSetContainer extends AbstractNamedDomainObjectContainer<JavaScriptSourceSet> implements JavaScriptSourceSetContainer {

    private final Project project;

    public DefaultJavaScriptSourceSetContainer(Project project) {
        super(JavaScriptSourceSet.class, InternalGradle.toInstantiator(project));
        this.project = project;
    }

    @Override
    protected JavaScriptSourceSet doCreate(String name) {
        return getInstantiator().newInstance(DefaultJavaScriptSourceSet.class, name, project);
    }
}
