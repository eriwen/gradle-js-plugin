package com.eriwen.gradle.js.source.internal;

import org.gradle.api.Project;
import org.gradle.api.internal.AbstractNamedDomainObjectContainer;
import org.gradle.api.internal.CollectionCallbackActionDecorator;
import org.gradle.api.internal.file.FileResolver;
import org.gradle.internal.reflect.Instantiator;

import com.eriwen.gradle.js.source.JavaScriptSourceSet;
import com.eriwen.gradle.js.source.JavaScriptSourceSetContainer;

public class DefaultJavaScriptSourceSetContainer extends AbstractNamedDomainObjectContainer<JavaScriptSourceSet> implements JavaScriptSourceSetContainer {

    private final Project project;
    private final Instantiator instantiator;
    private final FileResolver fileResolver;

    public DefaultJavaScriptSourceSetContainer(final Project project, final Instantiator instantiator, final FileResolver fileResolver) {
        super(JavaScriptSourceSet.class, instantiator, CollectionCallbackActionDecorator.NOOP);
        this.project = project;
        this.instantiator = instantiator;
        this.fileResolver = fileResolver;
    }

    @Override
    protected JavaScriptSourceSet doCreate(final String name) {
        return instantiator.newInstance(DefaultJavaScriptSourceSet.class, name, project, instantiator, fileResolver);
    }


}
