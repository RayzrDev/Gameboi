package dev.rayzr.gameboi.game

import dev.rayzr.gameboi.render.RenderContext
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageReaction

abstract class Game(private val width: Int, private val height: Int, val name: String, val maxPlayers: Int) {
    fun createRenderContext(match: Match): RenderContext = RenderContext(match, width, height)

    fun render(
        match: Match,
        reactions: List<String>,
        embedDescription: String? = null,
        messageContents: String? = null,
        function: RenderContext.() -> Unit
    ) {
        match.renderContext.clear()
        function.invoke(match.renderContext)
        match.renderContext.draw(embedDescription, messageContents) {
            addReactions(it, reactions)
        }
    }

    private fun addReactions(message: Message, reactions: List<String>) =
        reactions.forEach { message.addReaction(it).queue() }

    abstract fun begin(match: Match)
    abstract fun handleMessage(player: Player, match: Match, message: Message)
    abstract fun handleReaction(player: Player, match: Match, reaction: MessageReaction)
    abstract fun createData(match: Match): MatchData
}
