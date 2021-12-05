package com.github.dubreuia.pythoninlayhintsplugin.services

import com.intellij.openapi.project.Project
import com.github.dubreuia.pythoninlayhintsplugin.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
