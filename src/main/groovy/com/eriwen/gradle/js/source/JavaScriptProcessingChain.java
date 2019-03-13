package com.eriwen.gradle.js.source;

import org.gradle.api.NamedDomainObjectList;
import org.gradle.api.tasks.SourceTask;

import groovy.lang.Closure;

public interface JavaScriptProcessingChain extends NamedDomainObjectList<SourceTask> {

    JavaScriptSourceSet getSource();

    <T extends SourceTask> T task(Class<T> type);
    <T extends SourceTask> T task(String name, Class<T> type);
    <T extends SourceTask> T task(Class<T> type, Closure<?> closure);
    <T extends SourceTask> T task(String name, Class<T> type, Closure<?> closure);

}
