package ru.spbau.mit.roguelike.screens;


import ru.spbau.mit.roguelike.entities.Creature;
import ru.spbau.mit.roguelike.world.Item;

public class DropScreen extends InventoryBasedScreen {

    public DropScreen(Creature player) {
        super(player);
    }

    @Override
    protected boolean isAcceptable(Item item) {
        return true;
    }

    @Override
    protected String getVerb() {
        return "drop";
    }

    @Override
    protected Screen use(Item item) {
        player.drop(item);
        return null;
    }
}
