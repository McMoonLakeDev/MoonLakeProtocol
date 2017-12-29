/*
 * Copyright (C) 2016-Present The MoonLake (mcmoonlake@hotmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import com.mcmoonlake.protocol.client.MClient
import com.mcmoonlake.protocol.client.network.*
import com.mcmoonlake.protocol.network.DisconnectedEvent
import com.mcmoonlake.protocol.network.MProtocolType
import com.mcmoonlake.protocol.network.MProtocolVersion
import com.mcmoonlake.protocol.packet.play.CPacketChatMessage
import org.junit.Test
import java.net.Proxy
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class MProtocolTest {

    val host = "pi.axibug.com"
    val port = 25565
    val username = "Protocol"
    val proxy: Proxy? = null

    @Test
    fun testServerStatus() {
        val protocolType = MProtocolType.STATUS
        val protocolVersion = MProtocolVersion.V1_12_2
        val protocol = MClientProtocol(protocolType, protocolVersion, username)
        val factory = MClientConnectionFactory(proxy)
        val client = MClient(host, port, protocol, factory)
        val latch = CountDownLatch(1)
        client.connection.addListener(MClientConnectionListenerDefault())
        client.connection.addListener(object: MClientConnectionListenerAdapter() {
            override fun onDisconnected(event: DisconnectedEvent) {
                println("连接已断开: ${event.reason}")
                event.cause?.printStackTrace()
            }
            override fun onServerPingEvent(event: ServerPingEvent) {
                latch.countDown()
                println("version -> ${event.info.version}")
                println("description -> ${event.info.description.toRaw(false)}")
                println("players -> ${event.info.players}")
                println("icon -> ${event.info.favicon != null}")
            }
        })
        client.connection.connect()
        latch.await(10, TimeUnit.SECONDS)
    }

    @Test
    fun testServerJoin() {
        val protocolType = MProtocolType.LOGIN
        val protocolVersion = MProtocolVersion.V1_12_2
        val protocol = MClientProtocol(protocolType, protocolVersion, username)
        val factory = MClientConnectionFactory(proxy)
        val client = MClient(host, port, protocol, factory)
        val latch = CountDownLatch(1)
        client.connection.addListener(MClientConnectionListenerDefault())
        client.connection.addListener(object: MClientConnectionListenerAdapter() {
            override fun onDisconnected(event: DisconnectedEvent) {
                println("连接已断开: ${event.reason}")
                event.cause?.printStackTrace()
            }
            override fun onConnectedServer(event: ConnectedServerEvent) {
                event.connection.sendPacket(CPacketChatMessage("我是由 MoonLakeProtocol 生成的虚假客户端."))
                event.connection.sendPacket(CPacketChatMessage("by lgou2w."))
                event.connection.disconnect("quit", true)
                latch.countDown()
            }
        })
        client.connection.connect()
        latch.await(10, TimeUnit.SECONDS)
    }
}