package com.niuma.remembereverythingapp

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


inline fun <reified T> T.logger(): Logger = LoggerFactory.getLogger(T::class.java)

@SpringBootApplication
class RememberEverythingAppApplication

fun main(args: Array<String>) {
  runApplication<RememberEverythingAppApplication>(*args)
}
