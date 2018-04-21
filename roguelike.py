#!/usr/bin/env python3

import tdl
from tdl import Console
from abc import ABC, abstractmethod
from typing import NewType, Set
from enum import Enum, unique, auto

Char = NewType('Char', str)

# actual size of the window
SCREEN_WIDTH = 80
SCREEN_HEIGHT = 50

LIMIT_FPS = 20  # 20 frames-per-second maximum


class Drawer(ABC):
    @abstractmethod
    def draw_char(self, character: Char, x: int, y: int):
        pass


class TDLDrawer(Drawer):
    def __init__(self, console: Console):
        super().__init__()
        self._console = console

    def draw_char(self, character, x: int, y: int):
        self._console.draw_char(x, y, character)


@unique
class Command(Enum):
    UNKNOWN = auto()
    EXIT = auto()
    MOVE_UP = auto()
    MOVE_DOWN = auto()
    MOVE_LEFT = auto()
    MOVE_RIGHT = auto()


Command.MOVEMENTS = {
    Command.MOVE_UP,
    Command.MOVE_DOWN,
    Command.MOVE_LEFT,
    Command.MOVE_RIGHT
}


class CharObject:
    """
    Represents a single char object on the field. It is supposed to be a
    player or enemy, not a wall or floor.
    """

    def __init__(self, char: Char, x: int, y: int):
        self._char = char
        self._x, self._y = x, y

    def move(self, dx: int, dy: int):
        self._x += dx
        self._y += dy

    def draw(self, d: Drawer):
        d.draw_char(self._char, self._x, self._y)

    def clear(self, d: Drawer):
        d.draw_char(Char(' '), self._x, self._y)


class Player(CharObject):
    """
    Represents a player character `@` which can move around.
    """

    def __init__(self, x: int, y: int):
        super().__init__(Char("@"), x, y)
        self._next_action: Command = None

    @property
    def next_action(self) -> Command:
        return self._next_action

    @next_action.setter
    def next_action(self, next_action: Command):
        if not (next_action in {None, *Command.MOVEMENTS}):
            raise ValueError(
                "Passed command {} is not a movement command"
                    .format(next_action.name)
            )

        self._next_action = next_action

    def act(self):
        if self._next_action is None:
            return

        if self._next_action is Command.MOVE_RIGHT:
            self.move(1, 0)
        elif self._next_action is Command.MOVE_LEFT:
            self.move(-1, 0)
        elif self._next_action is Command.MOVE_UP:
            self.move(0, -1)
        elif self._next_action is Command.MOVE_DOWN:
            self.move(0, 1)

        self.next_action = None


def handle_keys() -> Command:
    user_input = tdl.event.key_wait()

    if user_input.key == 'ESCAPE':
        return Command.EXIT  # exit game

    if user_input.key == 'UP':
        return Command.MOVE_UP

    elif user_input.key == 'DOWN':
        return Command.MOVE_DOWN

    elif user_input.key == 'LEFT':
        return Command.MOVE_LEFT

    elif user_input.key == 'RIGHT':
        return Command.MOVE_RIGHT

    return Command.UNKNOWN


console = tdl.init(SCREEN_WIDTH, SCREEN_HEIGHT, title="Roguelike",
                   fullscreen=False)
tdl.setFPS(LIMIT_FPS)

drawer = TDLDrawer(console)


def main():
    player = Player(SCREEN_WIDTH // 2, SCREEN_HEIGHT // 2)

    objects = [
        CharObject(Char("#"), 10, 10),
        CharObject(Char("#"), 20, 10),
        player,
    ]

    while not tdl.event.is_window_closed():

        player.act()

        for obj in objects:
            obj.draw(drawer)

        tdl.flush()

        for obj in objects:
            obj.clear(drawer)

        # handle keys and exit game if needed
        user_command = handle_keys()
        if user_command is Command.EXIT:
            break

        if user_command in Command.MOVEMENTS:
            player.next_action = user_command


if __name__ == '__main__':
    main()
