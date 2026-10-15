package com.kitswiki.gquest.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.kitswiki.gquest.components.BodyComponent;
import com.kitswiki.gquest.components.DialogComponent;
import com.kitswiki.gquest.components.PlayerComponent;
import com.kitswiki.gquest.dialog.GameState;

import java.util.function.Supplier;

public class DelayedSpawnSystem extends IntervalSystem {

    private record Pending(String[] flags, Supplier<Entity> creator) {}

    private Array<Pending> pendings = new Array<>();

    private final ComponentMapper<DialogComponent> dialogM = ComponentMapper.getFor(DialogComponent.class);
    private final ComponentMapper<BodyComponent> bodyM = ComponentMapper.getFor(BodyComponent.class);

    private ImmutableArray<Entity> players;
    private ImmutableArray<Entity> goblins;

    private final GameState gameState;

    private final float REQ_DIST = 20f;

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        goblins = engine.getEntitiesFor(Family.all(DialogComponent.class).get());
        players = engine.getEntitiesFor(Family.all(PlayerComponent.class).get());
    }

    public DelayedSpawnSystem(GameState gameState) {
        super(0.2f);
        this.gameState = gameState;
    }

    @Override
    protected void updateInterval() {

        if (players.size() > 0) {
            Vector2 playerPos = bodyM.get(players.first()).body.getPosition();

            for (Entity npc : goblins) {
                String id = dialogM.get(npc).dialogId;
                Vector2 npcPos = bodyM.get(npc).body.getPosition();

                if (!"duckGob".equals(id))
                {
                    continue;
                }
                if (npcPos.dst2(playerPos) <= REQ_DIST * REQ_DIST && gameState.has("duckGob.duck_collected")) {
                    gameState.set("player.rangewduckgob");
                }
                if(gameState.has("player.rangewduckgob") && npcPos.dst2(playerPos) >= REQ_DIST * REQ_DIST)
                {
                    gameState.set("duck_can_show");
                }
            }
        }
        for (int i = pendings.size - 1; i >= 0; i--) {
            Pending pending = pendings.get(i);
            boolean isReqsOk = true;
            for(String s : pending.flags)
            {
                if(!gameState.has(s))
                {
                    isReqsOk = false;
                }
            }
            if(isReqsOk)
            {
                pending.creator.get();
                pendings.removeIndex(i);
            }
        }
    }

    public void addPending(String[] flags, Supplier<Entity> creator)
    {
        pendings.add(new Pending(flags, creator));
    }

    public void clearPending()
    {
        pendings.clear();
    }
}
