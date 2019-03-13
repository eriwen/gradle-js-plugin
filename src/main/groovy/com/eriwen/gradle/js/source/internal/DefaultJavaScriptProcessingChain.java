package com.eriwen.gradle.js.source.internal;

import java.util.Collections;
import java.util.concurrent.Callable;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.DefaultNamedDomainObjectList;
import org.gradle.api.tasks.SourceTask;
import org.gradle.internal.reflect.Instantiator;

import com.eriwen.gradle.js.source.JavaScriptProcessingChain;
import com.eriwen.gradle.js.source.JavaScriptSourceSet;

import groovy.lang.Closure;

public class DefaultJavaScriptProcessingChain extends DefaultNamedDomainObjectList<SourceTask> implements JavaScriptProcessingChain {

    private final DefaultJavaScriptSourceSet source;
    private final Project project;

    public DefaultJavaScriptProcessingChain(final Project project, final DefaultJavaScriptSourceSet source, final Instantiator instantiator) {
        super(SourceTask.class, instantiator, new Task.Namer());
        this.source = source;
        this.project = project;
        wireChain();
    }

    @Override
    public JavaScriptSourceSet getSource() {
        return source;
    }

    protected void wireChain() {
        all(new Action<SourceTask>() {
            @Override
            public void execute(final SourceTask sourceTask) {
                sourceTask.source(new Callable<FileCollection>() {
                    @Override
                    public FileCollection call() throws Exception {
                        int i = indexOf(sourceTask);
                        if (i == -1) {
                            return null; // task has been removed, noop
                        } else if (i == 0) {
                            return getSource().getJs();
                        } else {
                            SourceTask previous = get(i - 1);
                            return previous.getOutputs().getFiles();
                        }
                    }
                });
            }
        });
    }

    @Override
    public <T extends SourceTask> T task(final Class<T> type) {
        return task(calculateName(type), type);
    }

    @Override
    public <T extends SourceTask> T task(final String name, final Class<T> type) {
        return task(name, type, null);
    }

    @Override
    public <T extends SourceTask> T task(final Class<T> type, final Closure<?> closure) {
        return task(calculateName(type), type, closure);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends SourceTask> T task(final String name, final Class<T> type, final Closure<?> closure) {
        T task = (T)project.task(Collections.singletonMap("type", type), name, closure);
        add(task);
        return task;
    }

    protected String calculateName(final Class<? extends SourceTask> type) {
        String name = type.getName();
        if (name.endsWith("Task")) {
            name = name.substring(0, name.length() - 4);
        }

        return source.getName() + name;
    }
}
