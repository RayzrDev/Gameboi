package dev.rayzr.gameboi.game

import dev.rayzr.gameboi.Gameboi
import dev.rayzr.gameboi.manager.InviteManager
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageReaction
import net.dv8tion.jda.api.entities.TextChannel
import java.util.concurrent.TimeUnit

class Invite(
    val channel: TextChannel,
    val from: Player,
    val to: Player,
    val game: Game,
    private val life: Long = 120000
) {
    var message: Message? = null

    init {
        channel.sendMessage(
            EmbedBuilder()
                .setThumbnail(from.user.avatarUrl)
                .setTitle("${from.user.name} has invited you to play ${game.name}!")
                .setDescription("Press the check mark below to accept!")
                .setFooter("This invite will expire in ${life / 60000} minutes")
                .build()
        ).queue({
            message = it

            it.addReaction("\u2705").queue() // check mark
            it.addReaction("\u274c").queue() // x mark

            it.delete().queueAfter(life, TimeUnit.MILLISECONDS) { InviteManager.remove(to.user) }
        }, {
            InviteManager.remove(to.user)
            channel.sendMessage(":x: Failed to send invite embed! Please check the bot's permissions!")
        })
    }

    fun handleReaction(reaction: MessageReaction, message: Message, player: Player) {
        if (player != to && player != from) {
            reaction.removeReaction(player.user).queue()
            return
        }

        when (reaction.reactionEmote.name) {
            "\u2705" -> {
                if (player != to) {
                    reaction.removeReaction(player.user).queue()
                    return
                }

                // Check *again*
                if (from.currentMatch != null || to.currentMatch != null) {
                    message.channel.sendMessage(":x: One of you has already joined another match!").queue {
                        it.textChannel.deleteMessages(listOf(it, message))
                            .queueAfter(Gameboi.errorLife, TimeUnit.MILLISECONDS)
                        InviteManager.remove(to.user)
                    }
                    return
                }

                // TODO: More than 2-player game support?
                val match = Match(game, channel)

                match.addPlayer(from)
                match.addPlayer(to)
            }
            "\u274c" -> {
                // Do nothing special
            }
            else -> return
        }

        // Bye bye invite
        InviteManager.remove(to.user)
        message.delete().queue()
    }
}