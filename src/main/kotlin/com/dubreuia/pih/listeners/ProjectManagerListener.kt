package com.dubreuia.pih.listeners

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.dubreuia.pih.services.ProjectService

internal class ProjectManagerListener : com.intellij.openapi.project.ProjectManagerListener {

    override fun projectOpened(project: Project) {
        project.service<ProjectService>()
    }

}
