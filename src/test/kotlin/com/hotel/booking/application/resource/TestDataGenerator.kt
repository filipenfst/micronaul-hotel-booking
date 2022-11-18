package com.hotel.booking.application.resource

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.BufferedWriter
import java.io.FileOutputStream
import java.io.OutputStream
import java.time.LocalDate
import kotlin.system.measureTimeMillis

internal object TestDataGenerator {
    fun generate() = measureTimeMillis {
        runBlocking(Dispatchers.IO) {
            val now = LocalDate.now().plusDays(1)
            FileOutputStream("filename.csv").apply {
                val requests = flow {
                    repeat(100000) {
                        now.plusDays(it.toLong())
                            .run {
                                ReservationRequestDTO(
                                    start = this,
                                    end = this
                                )
                            }.run {
                                emit(this)
                            }

                    }

                }
                requests.toList()
                writeCsv(requests)
            }
        }
    }.also { println("Executer in: $it") }

    suspend fun OutputStream.writeCsv(requests: Flow<ReservationRequestDTO>) {
        val writer = bufferedWriter()

        writer.writeLine("start,end")
        requests.collect {
            writer.writeLine("${it.start},${it.end}")
        }
    }

    suspend fun BufferedWriter.writeLine(value: String) = withContext(Dispatchers.IO) {
        write(value)
        newLine()
        flush()
    }
}

internal fun main() {
    TestDataGenerator.generate()
}

