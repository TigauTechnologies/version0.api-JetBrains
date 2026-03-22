package com.v0.plugin

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileEditorManager
import org.java_websocket.server.WebSocketServer
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import javax.jmdns.JmDNS
import javax.jmdns.ServiceInfo
import java.net.InetSocketAddress
import java.net.InetAddress
import org.json.JSONObject
import kotlin.concurrent.thread

class V0BridgeService : ProjectActivity {
    private var jmdns: JmDNS? = null

    override suspend fun execute(project: Project) {
        // 1. Silent mDNS Discovery Broadcast
        thread {
            try {
                val address = InetAddress.getLocalHost()
                jmdns = JmDNS.create(address)
                val serviceInfo = ServiceInfo.create("_v0bridge._tcp.local.", "v0-as", 5813, "V0 Bridge Listener")
                jmdns?.registerService(serviceInfo)
            } catch (e: Exception) { 
                e.printStackTrace() 
            }
        }

        // 2. WebSocket Server Pipeline
        val server = object : WebSocketServer(InetSocketAddress(5813)) {
            override fun onOpen(conn: WebSocket?, handshake: ClientHandshake?) {
                V0Hijacker.suppressNativeAI(project)
            }
            
            override fun onMessage(conn: WebSocket?, message: String?) {
                try {
                    val json = JSONObject(message)
                    if (json.optString("action") == "COMMIT_CODE") {
                        applyCode(project, json.getString("code"))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            
            override fun onStart() {}
            override fun onError(conn: WebSocket?, ex: Exception?) {}
            override fun onClose(conn: WebSocket?, code: Int, reason: String?, remote: Boolean) {}
        }
        server.start()
    }

    private fun applyCode(project: Project, code: String) {
        WriteCommandAction.runWriteCommandAction(project) {
            val editor = FileEditorManager.getInstance(project).selectedTextEditor
            editor?.document?.let { doc ->
                val offset = editor.caretModel.offset
                doc.insertString(offset, code)
            }
        }
    }
}
