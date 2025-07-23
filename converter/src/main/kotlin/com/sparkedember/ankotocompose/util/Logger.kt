package com.sparkedember.ankotocompose.util

/**
 * Simple logging utility for the converter
 */
object Logger {
    
    enum class Level {
        DEBUG, INFO, WARN, ERROR
    }
    
    var currentLevel: Level = Level.INFO
    
    fun debug(message: String) {
        if (currentLevel <= Level.DEBUG) {
            println("[DEBUG] $message")
        }
    }
    
    fun info(message: String) {
        if (currentLevel <= Level.INFO) {
            println("[INFO] $message")
        }
    }
    
    fun warn(message: String) {
        if (currentLevel <= Level.WARN) {
            println("[WARN] $message")
        }
    }
    
    fun error(message: String, throwable: Throwable? = null) {
        if (currentLevel <= Level.ERROR) {
            println("[ERROR] $message")
            throwable?.printStackTrace()
        }
    }
}