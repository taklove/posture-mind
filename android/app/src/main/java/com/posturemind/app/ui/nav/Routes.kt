package com.posturemind.app.ui.nav

object Routes {
    const val LOGIN = "login"
    const val HOME = "home"
    const val CAPTURE = "capture"
    const val RESULT = "result"
    const val TRAINING = "training"
    const val EXERCISE = "exercise/{exerciseId}"
    const val PROGRESS = "progress"
    const val ABOUT = "about"

    fun exercise(id: String) = "exercise/$id"
}
