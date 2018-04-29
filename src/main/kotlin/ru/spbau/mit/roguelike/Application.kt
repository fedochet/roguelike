package ru.spbau.mit.roguelike

import com.almasb.fxgl.settings.GameSettings
import com.almasb.fxgl.app.GameApplication
import javafx.application.Application

class BasicGameApp : GameApplication() {

    override fun initSettings(settings: GameSettings) {
        with(settings) {
            width = 800
            height = 600
            title = "Basic Game App"
            version = "0.1"
        }
    }
}

fun main(args: Array<String>) {
    Application.launch(BasicGameApp::class.java, *args)
}