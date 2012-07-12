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
package com.eriwen.gradle.js

/**
 * Operations to handle zip files and other resources on the classpath.
 *
 * @author Eric Wendelin
 */
class ResourceUtil {
    /**
     * Given a target dir and resource, extract resource to target if it's not there already.
     *
     * @param targetDirectory directory to extract to
     * @param resourcePath path to resource to extract
     * @return reference to extracted or existing file
     */
    File extractFileToDirectory(final File targetDirectory, final String resourcePath) {
        if (targetDirectory.exists() && !targetDirectory.isDirectory()) {
            throw new IllegalArgumentException("Target directory is a file!")
        } else if (!targetDirectory.exists()) {
            targetDirectory.mkdirs()
        }

        final File file = new File(targetDirectory, resourcePath)
        if (!file.exists()) {
            final InputStream inputStream = Thread.currentThread().contextClassLoader.getResourceAsStream(resourcePath)
            file << inputStream
            inputStream.close()
        }
        return file
    }

    /**
     * Given an existing ZipFile, unzip it in the same directory and return a reference to the unzipped directory.
     *
     * @param zipFile to extract
     * @return reference to the unzipped directory.
     */
    File extractZipFile(final File zipFile) {
        final String extractTargetPath = zipFile.canonicalPath.substring(0, zipFile.canonicalPath.length() - 4);
        final File zipTargetDir = new File(extractTargetPath)
        zipTargetDir.mkdir()

        // TODO: quiet this noise
        new AntBuilder().unzip(src: zipFile.canonicalPath, dest: extractTargetPath, overwrite: 'true')

        return zipTargetDir
    }
}
