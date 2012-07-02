/*
 * Copyright 2012 Joshua Newman
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
package com.eriwen.gradle.js.commonJs

import groovy.json.JsonSlurper

/**
 * Decorates a path to a package.json file w/ property accessors and
 */
class Package {
    // The portion added to a github url before a tag to get a .zip
    private final static ZIP_NAME = "zipball"

    private String path
    private String delegate
    private Object data;
    private List repositories

    /**
     * @param path The path to a package.json file. @see http://wiki.commonjs.org/wiki/Packages/1.0
     */
    Package(String path) {
        this.path = path.trim()
        this.delegate = path.toURL().getText()
        this.data = new JsonSlurper().parseText(this.delegate)
    }

    def getProperty(String name) {
        if (name == 'repositories') {
            return this.repositories != null ? this.repositories : this.getRepositories();
        }
        else if (name == 'folder') {
            return this.getFolder()
        }
        else if (name in this.data) {
            return this.data[name]
        }
        else {
            return this.delegate.getProperty(name)
        }
    }

    /**
     * A lot of projects don't properly implement the CommonJS spec, so we'll assume the
     * package.json is at the root of any repository
     *
     * @return collection of at least one valid repositories.
     */
    List getRepositories() {
        if ("repositories" in this.data) {
            return this.repositories = this.data.repositories
        } else {
            return this.repositories = [
                    [
                        type: "git", // TODO how should I know if it's a git repo?
                        // Replacing the package.json, which I'll assume is in the root.
                        url: this.packagePathToRepo(this.path)
                    ]
            ]
        }
    }

    /**
     *
     * @param packagePath Full path to a package.json
     * @return
     */
    protected String packagePathToRepo(String packagePath) {
        // Trim off the package.json, then remove master for git repos.
        // TODO find a better way to get from a repo to its root.
        // TODO Might need a lot of these sort of replacements.
        return packagePath.replaceAll('package.json$', '').replaceAll('/?master/?$', '.git')
    }
}