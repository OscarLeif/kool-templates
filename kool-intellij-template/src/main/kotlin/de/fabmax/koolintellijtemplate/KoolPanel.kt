package de.fabmax.koolintellijtemplate

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.pipeline.backend.vk.RenderBackendVk
import de.fabmax.kool.util.BackendScope
import de.fabmax.kool.util.delayFrames
import de.fabmax.kool.util.logI
import kotlinx.coroutines.launch
import java.awt.BorderLayout
import java.awt.Canvas
import java.awt.Dimension
import javax.swing.JPanel
import javax.swing.SwingUtilities

class KoolPanel(koolCanvas: Canvas) : JPanel(BorderLayout()) {
    private val spacer = JPanel()
    private var spacerAdded = false

    init {
        spacer.preferredSize = Dimension(100, 1)
        add(koolCanvas, BorderLayout.CENTER)
    }

    fun forceUpdateLayout() {
        SwingUtilities.invokeLater {
            if (!spacerAdded) {
                revalidate()
                add(spacer, BorderLayout.SOUTH)
            } else {
                revalidate()
                remove(spacer)
            }
            spacerAdded = !spacerAdded
        }
    }

    fun resetSurface() {
        val ctx = KoolSystem.getContextOrNull() ?: return
        logI { "Recreating render surface" }
        (ctx.backend as? RenderBackendVk)?.recreateSurface()
        BackendScope.launch {
            delayFrames(1)
            (ctx.backend as? RenderBackendVk)?.recreateSurface()
            delayFrames(1)
            forceUpdateLayout()
        }
    }
}