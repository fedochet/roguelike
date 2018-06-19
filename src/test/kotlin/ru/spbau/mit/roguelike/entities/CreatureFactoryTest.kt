package ru.spbau.mit.roguelike.entities

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import ru.spbau.mit.roguelike.world.FieldOfView
import ru.spbau.mit.roguelike.world.MessagesHub
import ru.spbau.mit.roguelike.world.World

class CreatureFactoryTest {
    val mockWorld = mockk<World> {}
    val creatureFactory = CreatureFactory(mockWorld)

    @Test
    fun `newFungus creates new fungus and puts it to the world on the passed level`() {
        every { mockWorld.addCeatureAtEmptyLocation(any(), 1) } returns Unit

        val fungus = creatureFactory.newFungus(1)

        assertThat(fungus.name).isEqualTo("Fungus")
        assertThat(fungus.ai).isInstanceOf(FungusAI::class.java)
        verify { mockWorld.addCeatureAtEmptyLocation(fungus, 1) }
    }

    @Test
    fun `newBat creates new bat and puts it to thew world on passed level`() {
        every { mockWorld.addCeatureAtEmptyLocation(any(), 1) } returns Unit

        val bat = creatureFactory.newBat(1)

        assertThat(bat.name).isEqualTo("Bat")
        assertThat(bat.ai).isInstanceOf(BatAI::class.java)
        verify { mockWorld.addCeatureAtEmptyLocation(bat, 1) }
    }

    @Test
    fun `newPlayer creates a player creature with passed fieldOfView and messageHub at 0 level`() {
        val fieldOfView = mockk<FieldOfView>()
        val messagesHub = mockk<MessagesHub>()

        every { mockWorld.addCeatureAtEmptyLocation(any(), 0) } returns Unit

        val player = creatureFactory.newPlayer(fieldOfView, messagesHub)

        assertThat(player.name).isEqualTo("Player")
        assertThat(player.glyph).isEqualTo('@')
        assertThat(player.ai as PlayerAI)
                .extracting("fieldOfView", "messages")
                .containsExactly(fieldOfView, messagesHub)

        verify { mockWorld.addCeatureAtEmptyLocation(player, 0) }

    }
}