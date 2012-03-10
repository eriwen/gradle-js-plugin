package com.eriwen.gradle.js

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.rules.TemporaryFolder

class TestProjectRule extends TemporaryFolder {
    Project project

    @Override
    protected void before() {
        super.before()
        project = ProjectBuilder.builder().withProjectDir(getRoot()).build()
    }
}
