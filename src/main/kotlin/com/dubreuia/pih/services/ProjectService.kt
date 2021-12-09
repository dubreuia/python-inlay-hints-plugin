package com.dubreuia.pih.services

import com.intellij.openapi.project.Project
import com.dubreuia.pih.MyBundle

class ProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }

}
