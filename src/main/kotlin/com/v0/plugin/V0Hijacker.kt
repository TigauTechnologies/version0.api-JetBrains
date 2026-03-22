package com.v0.plugin

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ide.util.PropertiesComponent

object V0Hijacker {
    fun suppressNativeAI(project: Project) {
        // Suppress Gemini Property
        PropertiesComponent.getInstance().setValue("google.gemini.enabled", false)
        
        // Hide native AI tool windows from the UI
        val toolWindowManager = ToolWindowManager.getInstance(project)
        val aiWindows = listOf("Gemini", "AI Assistant", "Studio Bot")
        
        aiWindows.forEach { id ->
            toolWindowManager.getToolWindow(id)?.let { window ->
                window.setAvailable(false, null)
            }
        }
    }
}
