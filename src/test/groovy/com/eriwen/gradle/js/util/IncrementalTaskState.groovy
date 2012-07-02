package com.eriwen.gradle.js.util

import org.gradle.api.tasks.TaskState

public class IncrementalTaskState  {

    private final TaskState delegate;

    public IncrementalTaskState(TaskState delegate) {
        this.delegate = delegate;
    }

    public boolean isUpToDate() {
        return delegate.getSkipMessage().equals("UP-TO-DATE");
    }

    public boolean getExecuted() {
        return delegate.getExecuted();
    }

    public Throwable getFailure() {
        return delegate.getFailure();
    }

    public void rethrowFailure() {
        delegate.rethrowFailure();
    }

    public boolean getDidWork() {
        return delegate.getDidWork();
    }

    public boolean getSkipped() {
        return delegate.getSkipped();
    }

    public String getSkipMessage() {
        return delegate.getSkipMessage();
    }
}
