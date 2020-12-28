package com.github.noonmaru.autoreloader.plugin

import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask
import java.io.File

/**
 * @author Noonmaru
 */
class AutoReloaderPlugin : JavaPlugin() {

    private lateinit var reloadTask: BukkitTask

    override fun onEnable() {
        // after plugins setup
        server.scheduler.runTask(
            this, Runnable {
                val list = server.pluginManager.plugins.map { it to File(file.parentFile, "update/${it.file.name}") }
                if (list.isEmpty()) {
                    logger.info("No files to monitor.")
                    return@Runnable
                }
                list.forEach {
                    logger.info("Monitor plugin update ${it.first.name}")
                }

                val watcher = UpdateWatcher(list)
                // sync
                reloadTask = server.scheduler.runTaskTimerAsynchronously(this, Runnable {
                    watcher.scanNext()?.let { pair ->
                        Bukkit.broadcastMessage("Plugin update found: ${pair.first.name}")
                        Bukkit.broadcastMessage("Attempt to reload...")
                        reload()
                        reloadTask.cancel()
                    }
                }, 0L, 1L)
            })
    }

    private fun reload() {
        server.scheduler.runTask(this, Runnable {
            // Bukkit.reload() -> java.lang.IllegalStateException: zip file closed
            Bukkit.dispatchCommand(server.consoleSender, "rl confirm")
        })
    }
}

// Protected
private val Plugin.file: File
    get() {
        return JavaPlugin::class.java.getDeclaredMethod("getFile").apply {
            isAccessible = true
        }.invoke(this) as File
    }

//plugin per tick
internal class UpdateWatcher(list: List<Pair<Plugin, File>>) {
    private val array: Array<Pair<Plugin, File>> = list.toTypedArray()
    private var index = 0

    fun scanNext(): Pair<Plugin, File>? {
        return array.let { it[index++ % it.count()] }.takeIf { it.second.exists() }
    }
}