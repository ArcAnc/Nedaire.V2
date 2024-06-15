/**
 * @author ArcAnc
 * Created at: 11.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.magic;

import com.arcanc.nedaire.util.NDatabase;

import java.util.ArrayList;
import java.util.List;

public class MagicHandler
{
    public static School DESTRUCTION;
    public static School PROTECTION;
    public static School TRANSMUTATION;
    public static School NECROMANCY;

    public static SubType AIR;
    public static SubType LIGHT;
    public static SubType DEATH;
    public static SubType PLANT;

    private static final List<School> SCHOOLS = new ArrayList<>();
    private static final List<SubType> SUB_TYPES = new ArrayList<>();

    public static void init()
    {
        initSchools();
        initSubTypes();
    }

    public static void initSchools()
    {
        SCHOOLS.add(DESTRUCTION = new School(NDatabase.modRL(NDatabase.MagicInfo.SchoolsInfo.DESTRUCTION)));
        SCHOOLS.add(PROTECTION = new School(NDatabase.modRL(NDatabase.MagicInfo.SchoolsInfo.PROTECTION)));
        SCHOOLS.add(TRANSMUTATION = new School(NDatabase.modRL(NDatabase.MagicInfo.SchoolsInfo.TRANSMUTATION)));
        SCHOOLS.add(NECROMANCY = new School(NDatabase.modRL(NDatabase.MagicInfo.SchoolsInfo.NECROMANCY)));
    }

    public static void initSubTypes()
    {
        SUB_TYPES.add(AIR = new SubType(NDatabase.modRL(NDatabase.MagicInfo.SubTypesInfo.AIR)));
        SUB_TYPES.add(LIGHT = new SubType(NDatabase.modRL(NDatabase.MagicInfo.SubTypesInfo.LIGHT)));
        SUB_TYPES.add(PLANT = new SubType(NDatabase.modRL(NDatabase.MagicInfo.SubTypesInfo.PLANT)));
        SUB_TYPES.add(DEATH = new SubType(NDatabase.modRL(NDatabase.MagicInfo.SubTypesInfo.DEATH)));
    }
}
