package app.ardeco

import java.io.File

fun main() {
    println("------- ARDeco Service Watcher -------")
    println("Check services file existence")
    val fileName = "services"

    val directoryFiles = File(".").listFiles()?.map { it.name }
    if (directoryFiles != null) {
        val pos = directoryFiles.indexOf(fileName)
        if (pos != -1) {
            println("File \"$fileName\" exists")
        } else {
            error("File \"$fileName\" does not exist")
        }
    } else {
        error("Failed to list files in the directory")
    }

    val file = File(fileName)
    file.canRead().let {
        if (it) {
            println("File \"$fileName\" can be read")
        } else {
            error("File \"$fileName\" cannot be read")
        }
    }

    println("\nCheck timestamp log directory existence")
    val timestampDirectory = File("/var/opt/ardeco")
    if (timestampDirectory.exists()) {
        println("Directory \"/var/opt/ardeco\" exists")
    } else {
        println("Directory \"/var/opt/ardeco\" does not exist")
        println("Creating directory \"/var/opt/ardeco\"")
        val created = timestampDirectory.mkdir()
        if (!created) {
            error("Failed to create directory \"/var/opt/ardeco\"")
        }
        println("Directory \"/var/opt/ardeco\" has been created")
    }

    val lines = file.readLines()
    val services = lines.map { it.split("=") }.filter { it.size == 2 }

    services.forEach {
        println("\nProcessing ${it[0]}")
        val service = it[0]
        val directory = it[1]

        val directoryFile = File(directory)
        if (directoryFile.exists()) {
            println("Directory \"$directory\" exists")
        } else {
            println("Directory \"$directory\" does not exist")
            return@forEach
        }

        val time = directoryFile.lastModified()

        println("Check for corresponding timestamp file")
        val timestampFile = File("/var/opt/ardeco/$service")
        if (timestampFile.exists()) {
            println("Timestamp file exists (\"/var/opt/ardeco/$service\")")
        } else {
            println("Timestamp file does not exist, creating one")
            val created = timestampFile.createNewFile()
            if (!created) {
                println("Failed to create timestamp file")
                return@forEach
            }
            timestampFile.writeText("0")
            println("Timestamp file has been created to \"/var/opt/ardeco/$service\"")
        }

        val timestamp = timestampFile.readText().toLongOrNull() ?: 0
        if (time != timestamp) {
            println("App \"$service\" has been modified")
            println("Restart service \"$service\"")
            val result = ProcessBuilder("systemctl", "restart", service).start().waitFor()
            if (result != 0) {
                println("Failed to restart service \"$service\"")
                return@forEach
            }
            println("Service has been restarted, updating timestamp file")
            timestampFile.writeText(time.toString())
        } else {
            println("App \"$service\" has not been modified")
            return@forEach
        }
    }
}
