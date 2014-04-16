package com.eriwen.gradle.js.source.internal;

import com.eriwen.gradle.js.source.JavaScriptSourceSet;
import com.eriwen.gradle.js.source.JavaScriptSourceSetContainer;
import org.gradle.api.Project;
import org.gradle.api.internal.AbstractNamedDomainObjectContainer;
import org.gradle.api.internal.file.FileResolver;
import org.gradle.internal.reflect.Instantiator;

public class DefaultJavaScriptSourceSetContainer extends AbstractNamedDomainObjectContainer<JavaScriptSourceSet> implements JavaScriptSourceSetContainer {

    private final Project project;
    private final Instantiator instantiator;
    private final FileResolver fileResolver;

    public DefaultJavaScriptSourceSetContainer(Project project, Instantiator instantiator, FileResolver fileResolver) {
        super(JavaScriptSourceSet.class, instantiator);
        this.project = project;
        this.instantiator = instantiator;
        this.fileResolver = fileResolver;
    }

    @Override
    protected JavaScriptSourceSet doCreate(String name) {
        return instantiator.newInstance(DefaultJavaScriptSourceSet.class, name, project, instantiator, fileResolver);
    }


}
