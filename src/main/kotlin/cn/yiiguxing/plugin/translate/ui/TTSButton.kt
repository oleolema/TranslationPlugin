package cn.yiiguxing.plugin.translate.ui

import cn.yiiguxing.plugin.translate.message
import cn.yiiguxing.plugin.translate.trans.Lang
import cn.yiiguxing.plugin.translate.util.TextToSpeech
import com.intellij.openapi.Disposable
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.ui.components.labels.LinkLabel
import com.intellij.ui.components.labels.LinkListener
import com.intellij.util.ui.JBDimension
import icons.Icons
import javax.swing.SwingConstants

/**
 * TTSButton
 */
class TTSButton : LinkLabel<Any>(), LinkListener<Any?>, Disposable {

    var project: Project? = null

    private var ttsDisposable: Disposable? = null

    private lateinit var dataSource: () -> Pair<String, Lang>?

    init {
        icon = Icons.Audio
        disabledIcon = Icons.AudioDisabled
        setHoveringIcon(Icons.AudioPressed)
        myPaintUnderline = false
        toolTipText = message("tooltip.listen")
        horizontalAlignment = SwingConstants.CENTER
        setListener(this, null)
        preferredSize = JBDimension(16, 14)
    }

    fun dataSource(ds: () -> Pair<String, Lang>?) {
        dataSource = ds
    }

    override fun linkSelected(aSource: LinkLabel<Any?>?, aLinkData: Any?) {
        play()
    }

    fun play() {
        if (!isEnabled) {
            return
        }

        dataSource()?.let { (text, lang) ->
            ttsDisposable?.let {
                Disposer.dispose(it)
                return
            }

            icon = Icons.TTSSuspend
            setHoveringIcon(Icons.TTSSuspendHovering)
            TextToSpeech.speak(project, text, lang).let { disposable ->
                ttsDisposable = disposable
                Disposer.register(disposable, Disposable {
                    ttsDisposable = null
                    icon = Icons.Audio
                    setHoveringIcon(Icons.AudioPressed)
                })
            }
        }
    }

    override fun dispose() {
        ttsDisposable?.dispose()
    }
}