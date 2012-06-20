package com.eriwen.gradle.js

import org.gradle.api.Project

/**
 * Utility for executing JS with Rhino.
 *
 * @author Eric Wendelin
 * @date 2/20/12
 */
class RhinoExec {
    private static final String RHINO_MAIN_CLASS = 'org.mozilla.javascript.tools.shell.Main'
    Project project

    void execute(final Iterable<String> execargs, final String workingDirIn = '.', final Boolean ignoreExitCode = false) {
        def options = {
            main = RHINO_MAIN_CLASS
            classpath = project.configurations.rhino
            args = execargs
            workingDir = workingDirIn
            ignoreExitValue = ignoreExitCode
        }

        project.javaexec(options)
    }

    public RhinoExec(final Project projectIn) {
        project = projectIn
    }
}
