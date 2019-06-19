@file:JvmName("App")

package vnpay.test

import org.crsh.shell.Shell
import org.crsh.shell.ShellFactory

import jline.TerminalFactory
import jline.console.ConsoleReader
import org.crsh.command.InvocationContext
import org.crsh.console.jline.JLineProcessor
import java.io.FileDescriptor
import java.io.FileInputStream
import java.io.InputStream
import java.io.PrintWriter
import java.lang.reflect.*
import java.nio.file.Path

fun main(args: Array<String>) {
   // var shell: Shell = ShellLifecycle(configuration.commandsDirectory).start(config, configuration.user, configuration.password)
    println("Test create terminal")
    val terminal = TerminalFactory.create()

    println("binhnt.InteractiveShell.runLocalShell :  create consoleReader ")
    val consoleReader = ConsoleReader("Corda", FileInputStream(FileDescriptor.`in`), System.out, terminal)
    println("binhnt.End  ")
//    println("binhnt.InteractiveShell.runLocalShell :  create jlineProcessor ")
//    val jlineProcessor = JLineProcessor(terminal.isAnsiSupported, shell, consoleReader, System.out)
//    InterruptHandler { jlineProcessor.interrupt() }.install()

}