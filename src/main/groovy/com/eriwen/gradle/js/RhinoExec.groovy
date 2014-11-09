package com.eriwen.gradle.js

import org.gradle.api.Project
import org.gradle.process.ExecResult
import org.gradle.api.file.FileCollection

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
        final String maxHeapSizeVal =  options.get('maxHeapSize', null)
        final FileCollection classpathIn =  options.get('classpath', null)

        def execOptions = {
            main = RHINO_MAIN_CLASS
            args = ["-opt", "9"] + execargs
            workingDir = workingDirIn
            ignoreExitValue = ignoreExitCode
            standardOutput = out
            if (maxHeapSizeVal) {
                maxHeapSize = maxHeapSizeVal
            }

            if (classpathIn) {
              classpath = classpathIn
            }
            else {
              classpath = project.configurations.rhino
            }
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
