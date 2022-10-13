package com.alten.hotel.booking.application.commons.conteiners

import com.alten.hotel.booking.commons.logger.logInfo
import org.flywaydb.core.Flyway
import org.testcontainers.containers.Network
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName


object PostgresContainer : PostgreSQLContainer<PostgresContainer>(
    DockerImageName.parse("debezium/postgres:13-alpine").asCompatibleSubstituteFor("postgres")
) {
    init {
        withUsername("root")
        withPassword("root")
        withDatabaseName("test")
        withNetwork(Network.SHARED)

        start()
    }
}

internal object PostgresqlInitializerConfig : ApplicationContainerInitializerConfig<PostgresContainer> {
    override val container: PostgresContainer = PostgresContainer

    override fun getProperties() = mapOf(
        "r2dbc.datasources.default.url" to getR2dbcUrl(),
        "datasources.default.url" to container.jdbcUrl,

        "r2dbc.datasources.default.username" to container.username,
        "r2dbc.datasources.default.password" to container.password,
    ).also {
        it.values.forEach { v -> logInfo("Postgresql----------$v") }
        runMigrations()
    }

    private fun runMigrations() {
        Flyway
            .configure()
            .dataSource(container.jdbcUrl, container.username, container.password)
            .schemas("public")
            .locations("classpath:db/migration", "classpath:db/scripts")
            .load()
            .migrate()
    }

    fun getR2dbcUrl(): String =
        container.jdbcUrl.replace("jdbc", "r2dbc")
}

internal object PostgresqlInitializer : ApplicationInitializer(PostgresqlInitializerConfig)
