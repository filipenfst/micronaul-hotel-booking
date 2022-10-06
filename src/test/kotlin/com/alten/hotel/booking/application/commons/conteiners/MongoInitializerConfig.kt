package com.alten.hotel.booking.application.commons.conteiners

import com.alten.hotel.booking.commons.logger.logInfo
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.containers.Network
import org.testcontainers.utility.DockerImageName

internal object MongoInitializerConfig : ApplicationContainerInitializerConfig<MongoDBContainer> {
    override val container: MongoDBContainer = MongoDBContainer(DockerImageName.parse("mongo"))
        .withNetwork(Network.SHARED)
        .withNetworkAliases("mongo")

    override fun getProperties() = mapOf(
        "mongodb.uri" to container.replicaSetUrl,
    ).also {
        it.values.forEach { v -> logInfo("Mongodb----------$v") }
    }
}

internal object MongoInitializer : ApplicationInitializer(MongoInitializerConfig)
