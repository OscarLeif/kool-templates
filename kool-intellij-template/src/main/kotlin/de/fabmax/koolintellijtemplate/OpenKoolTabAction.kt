package de.fabmax.koolintellijtemplate

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.fileEditor.impl.JComponentEditorProviderUtils
import com.intellij.openapi.project.DumbAwareAction
import de.fabmax.kool.KoolSystem
import javax.swing.SwingUtilities
import kotlin.concurrent.thread

class OpenKoolTabAction : DumbAwareAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        if (KoolSystem.getContextOrNull() == null) {
            val koolPanel = KoolService.koolPanel
            val editor = JComponentEditorProviderUtils.openEditor(project, "Kool", koolPanel).first()

            FileEditorManager.getInstance(project).addFileEditorManagerListener(object : FileEditorManagerListener {
                override fun selectionChanged(event: FileEditorManagerEvent) {
                    if (event.newEditor == editor) {
                        koolPanel.resetSurface()
                    }
                }
            })

            SwingUtilities.invokeLater {
                thread {
                    KoolService.initKool()
                }
            }
        }
    }
}