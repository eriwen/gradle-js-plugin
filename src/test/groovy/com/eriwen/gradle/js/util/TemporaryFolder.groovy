package com.eriwen.gradle.js.util

import org.apache.commons.lang.StringUtils
import org.junit.rules.MethodRule
import org.junit.runner.Description
import org.junit.runners.model.FrameworkMethod
import org.junit.runners.model.Statement

import java.util.concurrent.atomic.AtomicInteger

class TemporaryFolder implements MethodRule {

    private static File root = new File("build/tmp/testWorkspaces").absoluteFile
    private static AtomicInteger testCounter = new AtomicInteger(1)

    private File dir
    private String prefix

    File getDir() {
        if (dir == null) {
            if (prefix == null) {
                // This can happen if this is used in a constructor or a @Before method. It also happens when using
                // @RunWith(SomeRunner) when the runner does not support rules.
                prefix = determinePrefix()
            }

            boolean dirCreated = false
            int counter = 0

            while (!dirCreated) {
                dir = new File(root, ++counter == 1 ? prefix : String.format("%s%d", prefix, counter))
                dirCreated = dir.mkdirs()
            }
        }

        dir
    }

    private String determinePrefix() {
        StackTraceElement[] stackTrace = new RuntimeException().getStackTrace()
        for (StackTraceElement element : stackTrace) {
            if (element.getClassName().endsWith("Test") || element.getClassName().endsWith("Spec")) {
                StringUtils.substringAfterLast(element.getClassName(), ".") + "/unknown-test-" + testCounter.getAndIncrement()
            }
        }
        "unknown-test-class-" + testCounter.getAndIncrement()
    }

    @Override
    Statement apply(Statement base, FrameworkMethod method, Object target) {
        init(method, target)
        new Statement() {
            @Override
            void evaluate() throws Throwable {
                base.evaluate()
                getDir().deleteDir()
            }
        }

    }

    Statement apply(final Statement base, Description description) {
    }

    private void init(FrameworkMethod method, Object target) {
        if (prefix == null) {
            String safeMethodName = method.name.replaceAll("\\s", "_").replace(File.pathSeparator, "_").replace(":", "_")
            if (safeMethodName.length() > 64) {
                safeMethodName = safeMethodName.substring(0, 32) + "..." + safeMethodName.substring(safeMethodName.length() - 32)
            }
            prefix = String.format("%s/%s", target.class.simpleName, safeMethodName)
        }
    }

    File file(path) {
        new File(getDir(), path.toString())
    }

    File newFile(path) {
        File file = file(path)
        if (!file.exists()) {
            assert file.parentFile.mkdirs() || file.parentFile.exists()
        }
        file
    }

    File dir(path) {
        File file = file(path)
        if (file.exists()) {
            if (!file.directory) {
                throw new RuntimeException("cannot get/create directory $file as it already exists as a file")
            }
        } else {
            assert file.mkdirs() || file.exists()
        }
        file
    }

}
