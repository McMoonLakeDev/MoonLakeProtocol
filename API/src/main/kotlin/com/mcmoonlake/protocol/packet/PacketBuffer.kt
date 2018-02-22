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

package com.mcmoonlake.protocol.packet

import com.mcmoonlake.protocol.chat.ChatComponent
import com.mcmoonlake.protocol.chat.ChatSerializer
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import java.nio.charset.Charset
import java.util.*

data class PacketBuffer(
        private var byteBuf: ByteBuf) {

    constructor() : this(Unpooled.buffer())
    constructor(buffer: ByteArray) : this(Unpooled.wrappedBuffer(buffer))

    fun getByteBuf(): ByteBuf
            = byteBuf

    fun setByteBuf(buffer: ByteBuf): PacketBuffer
            { byteBuf = buffer; return this; }

    fun setByteBuf(buffer: ByteArray): PacketBuffer
            { byteBuf = Unpooled.wrappedBuffer(buffer); return this; }

    fun writeByte(value: Byte): PacketBuffer
            { byteBuf.writeByte(value.toInt()); return this; }

    fun writeByte(value: Int): PacketBuffer
            { byteBuf.writeByte(value); return this; }

    fun writeBytes(value: ByteArray): PacketBuffer
            { byteBuf.writeBytes(value); return this; }

    fun writeBytes(value: ByteBuf): PacketBuffer
            { byteBuf.writeBytes(value); return this; }

    fun writeBytes(value: ByteArray, length: Int): PacketBuffer
            { byteBuf.writeBytes(value, 0, length); return this; }

    fun writeBytes(value: ByteBuf, length: Int): PacketBuffer
            { byteBuf.writeBytes(value, length); return this; }

    fun writeBytes(value: ByteBuf, index: Int, length: Int): PacketBuffer
            { byteBuf.writeBytes(value, index, length); return this; }

    fun writeBytes(value: PacketBuffer): PacketBuffer
            { byteBuf.writeBytes(value.byteBuf); return this; }

    fun writeBytes(value: PacketBuffer, length: Int): PacketBuffer
            { byteBuf.writeBytes(value.byteBuf, length); return this; }

    fun writeBytes(value: PacketBuffer, index: Int, length: Int): PacketBuffer
            { byteBuf.writeBytes(value.byteBuf, index, length); return this; }

    fun writeShort(value: Int): PacketBuffer
            { byteBuf.writeShort(value); return this; }

    fun writeInt(value: Int): PacketBuffer
            { byteBuf.writeInt(value); return this; }

    fun writeLong(value: Long): PacketBuffer
            { byteBuf.writeLong(value); return this; }

    fun writeFloat(value: Float): PacketBuffer
            { byteBuf.writeFloat(value); return this; }

    fun writeDouble(value: Double): PacketBuffer
            { byteBuf.writeDouble(value); return this; }

    /**
     * @throws IllegalArgumentException If string [value] bytes length > 32767.
     */
    @Throws(IllegalArgumentException::class)
    fun writeString(value: String): PacketBuffer {
        val buffer = value.toByteArray()
        if(buffer.size > 32767)
            throw IllegalArgumentException("字符串值字节不能大于 32767 长度.")
        writeVarInt(buffer.size)
        writeBytes(buffer)
        return this
    }

    /**
     * @throws IllegalArgumentException If per string for [value] bytes length > 32767.
     */
    @Throws(IllegalArgumentException::class)
    fun writeStrings(value: Array<out String>): PacketBuffer
            { value.forEach { writeString(it) }; return this; }

    fun writeUUID(value: UUID): PacketBuffer
            { writeLong(value.mostSignificantBits); writeLong(value.leastSignificantBits); return this; }

    fun writeVarInt(value: Int): PacketBuffer {
        var value0 = value
        while((value0 and 0x7F.inv()) != 0) {
            writeByte((value0 and 0x7F).or(0x80))
            value0 = value0.ushr(7)
        }
        writeByte(value0)
        return this
    }

    fun writeVarLong(value: Long): PacketBuffer {
        var value0 = value
        while((value0 and 0x7F.inv()) != 0L) {
            writeByte(((value0 and 0x7F).or(0x80)).toInt())
            value0 = value0.ushr(7)
        }
        writeByte(value0.toInt())
        return this
    }

    fun writeChatComponent(value: ChatComponent): PacketBuffer
            { writeString(value.toJson()); return this; }

    fun writeBoolean(value: Boolean): PacketBuffer
            { byteBuf.writeBoolean(value); return this; }

    fun readByte(): Byte
            = byteBuf.readByte()

    fun readUnsignedByte(): Short
            = byteBuf.readUnsignedByte()

    /**
     * @throws IllegalArgumentException If [length] < 0.
     */
    @Throws(IllegalArgumentException::class)
    fun readBytes(length: Int): ByteArray {
        val buffer = if(length >= 0) ByteArray(length) else throw IllegalArgumentException("待读取的数组不能小于 0 长度.")
        byteBuf.readBytes(buffer)
        return buffer
    }

    fun readBytes(value: ByteArray): PacketBuffer
            { byteBuf.readBytes(value, 0, value.size); return this; }

    fun readBytes(value: ByteBuf): PacketBuffer
            { byteBuf.readBytes(value); return this; }

    fun readBytes(value: ByteBuf, length: Int): PacketBuffer
            { byteBuf.readBytes(value, length); return this; }

    fun readBytes(value: ByteBuf, index: Int, length: Int): PacketBuffer
            { byteBuf.readBytes(value, index, length); return this; }

    fun readBytes(value: PacketBuffer): PacketBuffer
            { byteBuf.readBytes(value.byteBuf); return this; }

    fun readBytes(value: PacketBuffer, length: Int): PacketBuffer
            { byteBuf.readBytes(value.byteBuf, length); return this; }

    fun readBytes(value: PacketBuffer, index: Int, length: Int): PacketBuffer
            { byteBuf.readBytes(value.byteBuf, index, length); return this; }

    fun readShort(): Short
            = byteBuf.readShort()

    fun readUnsignedShort(): Int
            = byteBuf.readUnsignedShort()

    fun readInt(): Int
            = byteBuf.readInt()

    fun readLong(): Long
            = byteBuf.readLong()

    fun readFloat(): Float
            = byteBuf.readFloat()

    fun readDouble(): Double
            = byteBuf.readDouble()

    fun readString(): String {
        val length = readVarInt()
        val buffer = readBytes(length)
        return String(buffer)
    }

    fun readUUID(): UUID
            = UUID(readLong(), readLong())

    /**
     * @throws IllegalArgumentException If VarInt length > 5.
     */
    @Throws(IllegalArgumentException::class)
    fun readVarInt(): Int {
        var value = 0
        var size = 0
        var b = 0
        while((readByte().toInt().apply { b = this }.and(0x80)) == 0x80) {
            value = value or ((b and 0x7F) shl (size++ * 7))
            if(size > 5)
                throw IllegalArgumentException("VarInt 值数据太大，必须小于或等于 5 长度.")
        }
        return value or ((b and 0x7F) shl (size * 7))
    }

    /**
     * @throws IllegalArgumentException If VarLong length > 10.
     */
    @Throws(IllegalArgumentException::class)
    fun readVarLong(): Long {
        var value = 0L
        var size = 0
        var b = 0
        while((readByte().toInt().apply { b = this }.and(0x80)) == 0x80) {
            value = value or ((b and 0x7F) shl (size++ * 7)).toLong()
            if(size > 10)
                throw IllegalArgumentException("VarLong 值数据太大，必须小于或等于 10 长度.")
        }
        return value or ((b and 0x7F) shl (size * 7)).toLong()
    }

    fun readChatComponent(): ChatComponent
            = ChatSerializer.fromJsonLenient(readString())

    fun readBoolean(): Boolean
            = byteBuf.readBoolean()

    fun writerIndex(): Int
            = byteBuf.writerIndex()

    fun writerIndex(value: Int): PacketBuffer
            { byteBuf.writerIndex(value); return this; }

    fun readerIndex(): Int
            = byteBuf.readerIndex()

    fun readerIndex(value: Int): PacketBuffer
            { byteBuf.readerIndex(value); return this; }

    fun readableBytes(): Int
            = byteBuf.readableBytes()

    fun writableBytes(): Int
            = byteBuf.writableBytes()

    fun maxWritableBytes(): Int
            = byteBuf.maxWritableBytes()

    fun hasArray(): Boolean
            = byteBuf.hasArray()

    fun array(): ByteArray
            = byteBuf.array()

    fun arrayOffset(): Int
            = byteBuf.arrayOffset()

    fun hasMemoryAddress(): Boolean
            = byteBuf.hasMemoryAddress()

    fun memoryAddress(): Long
            = byteBuf.memoryAddress()

    fun toString(charset: Charset = Charsets.UTF_8): String
            = byteBuf.toString(charset)

    fun toString(index: Int, length: Int, charset: Charset = Charsets.UTF_8): String
            = byteBuf.toString(index, length, charset)

    fun clear(): PacketBuffer
            { byteBuf.clear(); return this; }

    fun release(): Boolean
            = byteBuf.release()

    fun release(decrement: Int): Boolean
            = byteBuf.release(decrement)

    override fun equals(other: Any?): Boolean {
        if(other === this)
            return true
        if(other is PacketBuffer)
            return byteBuf == other.byteBuf
        else if(other is ByteBuf)
            return byteBuf == other
        return false
    }

    override fun hashCode(): Int {
        return byteBuf.hashCode()
    }

    override fun toString(): String {
        return "PacketBuffer(byteBuf=$byteBuf)"
    }

    companion object {

        /**
         * * Empty bytes of PacketBuffer.
         */
        @JvmField
        val EMPTY = PacketBuffer(Unpooled.EMPTY_BUFFER)
    }
}
