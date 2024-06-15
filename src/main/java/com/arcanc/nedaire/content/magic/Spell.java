/**
 * @author ArcAnc
 * Created at: 11.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.magic;

import net.minecraft.world.entity.monster.breeze.Shoot;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class Spell
{
    private final School school;
    private final List<SubType> subType = new ArrayList<>();
    private final int level;
    private final Consumer<Player> actions;

    public Spell(School school, int level, Consumer<Player> actions, SubType... subTypes)
    {
        this.school = school;
        this.level = level;
        this.actions = actions;
        subType.addAll(Arrays.asList(subTypes));
    }

    public School getSchool() {
        return school;
    }

    public List<SubType> getSubType() {
        return subType;
    }

    public int getLevel() {
        return level;
    }

    public Consumer<Player> getActions() {
        return actions;
    }
}
