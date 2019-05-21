package me.eater.emo.aardvark.controllers

import com.github.kittinunf.fuel.core.FuelError
import javafx.beans.property.SimpleListProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import me.eater.emo.ModpackCache
import me.eater.emo.aardvark.AutoObserver
import me.eater.emo.aardvark.fxprop
import me.eater.emo.emo.dto.repository.ModpackVersion
import tornadofx.Controller
import tornadofx.listProperty

class InstallerController : Controller() {
    private val emoController: EmoController by inject()
    private val tasksList: ObservableList<Task> = FXCollections.observableList(mutableListOf())

    val tasks: ObservableList<Task>
        get() = tasksList

    var state: State by fxprop(State.Idle)
        private set

    fun startInstall(job: Job) {
        synchronized(state) {
            if (!state.canStart()) {
                throw RuntimeException("Installer running already")
            }

            state = State.Running(job)
        }

        val ch = Channel<Task>()

        GlobalScope.launch {
            var lastTask: Task? = null

            val result = try {
                emoController.startInstall(emoController.getEmoContext(job)) {
                    lastTask?.let { task -> task.state = Task.TaskState.Done }
                    lastTask = Task(it.description, Task.TaskState.Running)
                    ch.send(lastTask!!)
                }

                ch.close()
                lastTask?.let { task -> task.state = Task.TaskState.Done }
                emoController.updateProfiles()

                State.Done(job)
            } catch (t: Throwable) {
                lastTask?.let { task -> task.state = Task.TaskState.Error(t) }
                if (t is FuelError) {
                    println("Error for ${t.response.url}")
                }
                t.printStackTrace()
                State.Error(t, job)
            }

            synchronized(state) {
                state = result
            }
        }

        GlobalScope.launch(Dispatchers.JavaFx) {
            tasksList.clear()

            for (item in ch) {
                tasksList.add(item)
            }
        }
    }

    class Task(val description: String, state: TaskState = TaskState.Idle) : AutoObserver() {
        var state: TaskState by observe(state)

        sealed class TaskState {
            object Idle : TaskState()
            object Running : TaskState()
            object Done : TaskState()
            data class Error(val t: Throwable) : TaskState()
        }
    }

    data class Job(
        val name: String,
        val location: String,
        val modpackCache: ModpackCache,
        val modpackVersion: ModpackVersion
    )

    sealed class State {
        object Idle : State()
        open class WithJob protected constructor(val job: Job) : State()
        class Running(job: Job) : WithJob(job)
        class Done(job: Job) : WithJob(job)
        class Error(val t: Throwable, job: Job) : WithJob(job)

        fun canStart() = this !is Running
    }
}