package com.eriwen.gradle.js.source.internal;

import com.eriwen.gradle.js.source.JavaScriptProcessingChain;
import com.eriwen.gradle.js.source.JavaScriptSourceSet;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.DefaultNamedDomainObjectList;
import org.gradle.api.tasks.SourceTask;

import java.util.concurrent.Callable;

public class DefaultJavaScriptProcessingChain extends DefaultNamedDomainObjectList<SourceTask> implements JavaScriptProcessingChain{

    private final DefaultJavaScriptSourceSet source;

    public DefaultJavaScriptProcessingChain(Project project, DefaultJavaScriptSourceSet source) {
        super(SourceTask.class, InternalGradle.toInstantiator(project), new Task.Namer());
        this.source = source;
        wireChain();
    }

    public JavaScriptSourceSet getSource() {
        return source;
    }

    protected void wireChain() {
        all(new Action<SourceTask>() {
            public void execute(final SourceTask sourceTask) {
                sourceTask.source(new Callable<FileCollection>() {
                    public FileCollection call() throws Exception {
                        int index = indexOf(sourceTask);
                        if (index == -1) {
                            return null; // task has been removed, noop
                        } else if (index == 0) {
                            return getSource().getJs();
                        } else {
                            SourceTask previous = get(index - 1);
                            return previous.getOutputs().getFiles();
                        }
                    }
                });
            }
        });
    }
}
