package com.eriwen.gradle.js.source;

import org.gradle.api.NamedDomainObjectList;
import org.gradle.api.tasks.SourceTask;

public interface JavaScriptProcessingChain extends NamedDomainObjectList<SourceTask> {

    JavaScriptSourceSet getSource();

}
