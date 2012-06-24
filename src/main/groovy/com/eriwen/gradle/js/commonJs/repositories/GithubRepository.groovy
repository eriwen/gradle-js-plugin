/*
 * Copyright 2012 Vodori Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.eriwen.gradle.js.commonJs.repositories

import groovy.json.JsonSlurper
import com.eriwen.gradle.js.commonJs.Repository
import com.eriwen.gradle.js.commonJs.Package

class GithubRepository implements Repository {
    private Package pack
    private String repoName

    GithubRepository(Package pack) {
        this.pack = pack
        // Need this for the api. getting from the end, so I don't have to care if the scheme is
        // present.
        this.repoName = this.pack.repositories[0].url.split('/')[-3]
    }

    File getTar(int index=0) {
        Object tag = getTagByName(pack.version)
        return new File((tag.getAt("zipball_url") as String).toURL().getText())
    }

    File getZip(int index=0) {
        Object tag = getTagByName(pack.version)
        return new File((tag.getAt("tarball_url") as String).toURL().getText())
    }

    Object getTagByName(String name) {
        // SEE: http://developer.github.com/v3/
        // TODO: This could be a whole lot nicer.
        String tagJson = "https://api.github.com/repos/${repoName}/$name/tags".toURL().getText()
        def tags = new JsonSlurper().parseText(tagJson)

        for (Object tag in tags) {
            if (tag.name == name) {
                return tag
            }
        }

        return null
    }
}
