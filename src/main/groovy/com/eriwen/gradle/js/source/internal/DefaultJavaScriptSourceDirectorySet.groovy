package com.eriwen.gradle.js.source.internal

import org.gradle.api.Project;
import com.eriwen.gradle.js.source.JavaScriptProcessingChain
import com.eriwen.gradle.js.source.JavaScriptSourceSet
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.DefaultSourceDirectorySet
import org.gradle.util.ConfigureUtil
import org.gradle.util.GUtil

class DefaultJavaScriptSourceDirectorySet extends DefaultSourceDirectorySet {
	public DefaultJavaScriptSourceDirectorySet(String name, Project project){
		super(name,
			String.format("%s JavaScript source", GUtil.toWords(name)),
			InternalGradle.toFileResolver(project))
	}
}
