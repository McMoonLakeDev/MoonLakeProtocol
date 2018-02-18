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

package com.mcmoonlake.protocol.chat

/**
 * ## ChatComponentKeybind (聊天组件按键)
 *
 * @see [ChatComponent]
 * @see [ChatComponentAbstract]
 * @author lgou2w
 * @since 2.0
 * @constructor ChatComponentKeybind
 * @param keybind Keybind
 * @param keybind 按键
 */
open class ChatComponentKeybind(
        /**
         * * Gets or sets the keybind object for this chat component keybind.
         * * 获取或设置此聊天组件按键的按键对象.
         */
        var keybind: String

) : ChatComponentAbstract() {

    /**
     * @see [ChatComponentKeybind.keybind]
     */
    fun setKeybind(keybind: String): ChatComponentKeybind
            { this.keybind = keybind; return this; }

    override fun equals(other: Any?): Boolean {
        if(other === this)
            return true
        if(other is ChatComponentKeybind)
            return super.equals(other) && keybind == other.keybind
        return false
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + keybind.hashCode()
        return result
    }

    override fun toString(): String {
        return "ChatComponentKeybind(keybind='$keybind', style=$style, extras=$extras)"
    }
}
