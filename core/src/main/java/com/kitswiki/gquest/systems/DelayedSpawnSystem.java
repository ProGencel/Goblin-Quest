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

    private static class Pending {
        final String[] flags;
        final Supplier<Entity> creator;
        final String nearDialogId;
        final float radius;
        boolean wasNear;

        public Pending(Supplier<Entity> creator, String[] flags, String nearDialogId, float radius) {
            this.creator = creator;
            this.flags = flags;
            this.nearDialogId = nearDialogId;
            this.radius = radius;
        }
    }

    private Array<Pending> pendings = new Array<>();

    private final ComponentMapper<DialogComponent> dialogM = ComponentMapper.getFor(DialogComponent.class);
    private final ComponentMapper<BodyComponent> bodyM = ComponentMapper.getFor(BodyComponent.class);

    private ImmutableArray<Entity> players;
    private ImmutableArray<Entity> dialogNpcs;

    private final GameState gameState;

    public DelayedSpawnSystem(GameState gameState) {
        super(0.2f);
        this.gameState = gameState;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        dialogNpcs = engine.getEntitiesFor(Family.all(DialogComponent.class).get());
        players = engine.getEntitiesFor(Family.all(PlayerComponent.class).get());
    }

    @Override
    protected void updateInterval() {

        if(pendings.size == 0)
        {
            return;
        }

        Vector2 playerPos = bodyM.get(players.first()).body.getPosition();

        for(int i = pendings.size - 1;i>=0;i--)
        {
            Pending p = pendings.get(i);
            if(isReady(p,playerPos))
            {
                p.creator.get();
                pendings.removeIndex(i);
            }
        }
    }

    private boolean isReady(Pending p, Vector2 playerPos)
    {
        for(String s: p.flags)
        {
            if(!gameState.has(s))
            {
                return false;
            }
        }

        if (p.nearDialogId == null)
        {
            return true;
        }

        if(playerPos == null)
        {
            return false;
        }

        Vector2 npcPos = findNpcPos(p.nearDialogId);
        if(npcPos == null)
        {
            return false;
        }

        boolean near = npcPos.dst2(playerPos) <= p.radius * p.radius;
        if(near)
        {
            p.wasNear = true;
        }

        return !near && p.wasNear;
    }

    private Vector2 findNpcPos(String dialogId)
    {
        for(Entity npc : dialogNpcs)
        {
            if(dialogId.equals(dialogM.get(npc).dialogId))
            {
                return bodyM.get(npc).body.getPosition();
            }
        }
        return null;
    }

    public void addPending(String[] flags, Supplier<Entity> creator, String nearDialogId, float radius)
    {
        pendings.add(new Pending(creator, flags,nearDialogId,radius));
    }

    public void addPending(String[] flags, Supplier<Entity> creator)
    {
        addPending(flags,creator,null,0);
    }
}
