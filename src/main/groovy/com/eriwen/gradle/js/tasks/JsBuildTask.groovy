/**
 * Copyright 2012 Eric Wendelin
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.eriwen.gradle.js.tasks


import com.eriwen.gradle.js.JsMinifier
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.Exec


class BuilJsTask extends Exec {
    private final List<Object> source = new ArrayList<Object>();
    private final PatternFilterable patternSet = new PatternSet();
    private static final JsClosureBuilder BUILDER = new JsClosureBuilder()

    @OutputFile def dest

    File getDest() {
        project.file(dest)
    }

    @TaskAction
    def run() {
        Set<File> externsFiles = project.closure.externs ? project.closure.externs.files : [] as Set<File>
        BUILDER.buildJsFile(source.files, externsFiles, dest as File,
                project.closure.compilerOptions, project.closure.warningLevel, project.closure.compilationLevel)
    }
    /**
     * Returns the source for this task, after the include and exclude patterns have been applied. Ignores source files which do not exist.
     *
     * @return The source.
     */
    @InputFiles
    @SkipWhenEmpty
    public FileTree getSource() {
        FileTree src;
        if (this.source.isEmpty()) {
            src = DeprecationLogger.whileDisabled(new Factory<FileTree>() {
                public FileTree create() {
                    return getDefaultSource();
                }
            });
        } else {
            src = getProject().files(new ArrayList<Object>(this.source)).getAsFileTree();
        }
        return src == null ? getProject().files().getAsFileTree() : src.matching(patternSet);
    }

    /**
     * Returns the default source for this task, if any.
     *
     * @return The source. May return null.
     * @deprecated Use getSource() instead.
     */
    @Deprecated
    protected FileTree getDefaultSource() {
        DeprecationLogger.nagUserOfReplacedMethod("SourceTask.getDefaultSource()", "getSource()");
        return null;
    }

    /**
     * Sets the source for this task. The given source object is evaluated as for {@link org.gradle.api.Project#files(Object...)}.
     *
     * @param source The source.
     */
    public void setSource(Object source) {
        this.source.clear();
        this.source.add(source);
    }

    /**
     * Adds some source to this task. The given source objects will be evaluated as for {@link org.gradle.api.Project#files(Object...)}.
     *
     * @param sources The source to add
     * @return this
     */
    public SourceTask source(Object... sources) {
        for (Object source : sources) {
            this.source.add(source);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public SourceTask include(String... includes) {
        patternSet.include(includes);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public SourceTask include(Iterable<String> includes) {
        patternSet.include(includes);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public SourceTask include(Spec<FileTreeElement> includeSpec) {
        patternSet.include(includeSpec);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public SourceTask include(Closure includeSpec) {
        patternSet.include(includeSpec);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public SourceTask exclude(String... excludes) {
        patternSet.exclude(excludes);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public SourceTask exclude(Iterable<String> excludes) {
        patternSet.exclude(excludes);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public SourceTask exclude(Spec<FileTreeElement> excludeSpec) {
        patternSet.exclude(excludeSpec);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public SourceTask exclude(Closure excludeSpec) {
        patternSet.exclude(excludeSpec);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> getIncludes() {
        return patternSet.getIncludes();
    }

    /**
     * {@inheritDoc}
     */
    public SourceTask setIncludes(Iterable<String> includes) {
        patternSet.setIncludes(includes);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> getExcludes() {
        return patternSet.getExcludes();
    }

    /**
     * {@inheritDoc}
     */
    public SourceTask setExcludes(Iterable<String> excludes) {
        patternSet.setExcludes(excludes);
        return this;
    }
}
