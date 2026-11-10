package com.kitswiki.gquest.components;

import com.badlogic.ashley.core.Component;

public class HealthComponent implements Component {

    public float maxHealth = 100f;
    public float currentHealth = 10f;
    public boolean isInvincible = false;
    public float invincibleTime = 2f;

}
