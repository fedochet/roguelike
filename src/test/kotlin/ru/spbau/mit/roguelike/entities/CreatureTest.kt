package ru.spbau.mit.roguelike.entities

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import ru.spbau.mit.roguelike.world.World
import java.awt.Color

class CreatureTest {
    val mockWorld = mockk<World> {}

    @Test
    fun `creature removes itself from the world when it dies and notifies creatures around about it`() {
        val creatureNear = mockk<Creature> {
            every { canSee(any(), any(), any()) } returns true
            every { notify(any()) } returns Unit
        }

        every { mockWorld.removeCreature(any()) } returns true
        every { mockWorld.getCreatureAt(any(), any(), any()) } returnsMany listOf(creatureNear) andThen null

        val creature = Creature(mockWorld, "name", 'n', Color.BLACK, 10, 0, 0, 0)

        creature.hp = 0

        verify {
            creatureNear.notify("The 'name' die.")
            mockWorld.removeCreature(creature)
        }
    }

    @Test
    fun `creatrue delegates canSee to its ai`() {
        val mockAI = mockk<CreatureAI> {
            every { canSee(any(), any(), any()) } returns true
        }

        val creature = Creature(mockWorld, "name", 'n', Color.BLACK, 10, 0, 0, 0)
        creature.ai = mockAI

        assertThat(creature.canSee(1, 2, 3)).isTrue()

        verify { mockAI.canSee(1, 2, 3) }
    }
}