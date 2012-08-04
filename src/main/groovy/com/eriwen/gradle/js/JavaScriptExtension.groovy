package com.eriwen.gradle.js

import org.gradle.api.artifacts.repositories.ArtifactRepository
import org.gradle.api.artifacts.repositories.IvyArtifactRepository
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.internal.artifacts.ResolverFactory
import org.gradle.api.internal.artifacts.repositories.layout.PatternRepositoryLayout
import org.gradle.api.Project
import org.gradle.util.ConfigureUtil
import com.eriwen.gradle.js.source.JavaScriptSourceSetContainer
import com.eriwen.gradle.js.source.internal.DefaultJavaScriptSourceSetContainer
import com.eriwen.gradle.js.source.internal.InternalGradle

class JavaScriptExtension {
    public static final NAME = "javascript"

    public static final String GRADLE_PUBLIC_JAVASCRIPT_REPO_URL = "http://repo.gradle.org/gradle/javascript-public"
    public static final String GOOGLE_APIS_REPO_URL = "http://ajax.googleapis.com/ajax/libs"

    private final ResolverFactory resolverFactory
    final JavaScriptSourceSetContainer source

    JavaScriptExtension(Project project) {
        source = InternalGradle.toInstantiator(project).newInstance(DefaultJavaScriptSourceSetContainer, project)
    }

    void source(Closure closure) {
        ConfigureUtil.configure(closure, source)
    }

    public ArtifactRepository getGradlePublicJavaScriptRepository() {
        MavenArtifactRepository repo = resolverFactory.createMavenRepository()
        repo.setUrl(GRADLE_PUBLIC_JAVASCRIPT_REPO_URL)
        repo.setName("Gradle Public JavaScript Repository")
        return repo
    }

    public ArtifactRepository getGoogleApisRepository() {
        IvyArtifactRepository repo = resolverFactory.createIvyRepository()
        repo.setName("Google Libraries Repository")
        repo.setUrl(GOOGLE_APIS_REPO_URL)
        repo.layout("pattern", new Closure(this) {
            public void doCall() {
                PatternRepositoryLayout layout = (PatternRepositoryLayout) getDelegate()
                layout.artifact("[organization]/[revision]/[module].[ext]")
                layout.ivy("[organization]/[revision]/[module].xml")
            }
        })
        return repo
    }
}
