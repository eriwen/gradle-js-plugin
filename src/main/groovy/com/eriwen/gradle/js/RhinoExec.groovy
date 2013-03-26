package com.eriwen.gradle.js

import org.gradle.api.Project
import org.gradle.process.ExecResult

/**
 * Utility for executing JS with Rhino.
 *
 * @author Eric Wendelin
 * @date 2/20/12
 */
class RhinoExec {
    private static final String RHINO_MAIN_CLASS = 'org.mozilla.javascript.tools.shell.Main'
    Project project

    void execute(final Iterable<String> execargs, final Map<String, Object> options = [:]) {
        final String workingDirIn = options.get('workingDir', '.')
        final Boolean ignoreExitCode = options.get('ignoreExitCode', false).asBoolean()
        final OutputStream out = options.get('out', System.out) as OutputStream
        def execOptions = {
            main = RHINO_MAIN_CLASS
            classpath = project.configurations.rhino
            args = ["-opt", "9"] + execargs
            workingDir = workingDirIn
            ignoreExitValue = ignoreExitCode
            standardOutput = out
        }

        ExecResult result = project.javaexec(execOptions)
        if (!ignoreExitCode) {
            result.assertNormalExitValue()
        }
    }

    public RhinoExec(final Project projectIn) {
        project = projectIn
    }
}
