package com.eriwen.gradle.js.source.internal;

import com.eriwen.gradle.js.source.JavaScriptSourceSet;
import com.eriwen.gradle.js.source.JavaScriptSourceSetContainer;
import org.gradle.api.NamedDomainObjectFactory;
import org.gradle.api.internal.AbstractNamedDomainObjectContainer;
import org.gradle.api.internal.FactoryNamedDomainObjectContainer;
import org.gradle.api.internal.Instantiator;

public class DefaultJavaScriptSourceSetContainer extends AbstractNamedDomainObjectContainer<JavaScriptSourceSet> implements JavaScriptSourceSetContainer {
    public DefaultJavaScriptSourceSetContainer(Instantiator instantiator) {
        super(JavaScriptSourceSet.class, instantiator);
    }

    @Override
    protected JavaScriptSourceSet doCreate(String name) {
        return getInstantiator().newInstance(DefaultJavaScriptSourceSet.class, name);
    }
}
