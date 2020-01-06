package com.davenonymous.libnonymous.utils;

import com.davenonymous.libnonymous.gui.framework.util.FontAwesomeIcons;

import java.util.Random;

public class FontAwesomeHelper {
    private static Random rand = new Random();
    private static FontAwesomeIcons[] animalIcons = new FontAwesomeIcons[] {
            FontAwesomeIcons.SOLID_Cat,
            FontAwesomeIcons.SOLID_Crow,
            FontAwesomeIcons.SOLID_Dog,
            FontAwesomeIcons.SOLID_Dove,
            FontAwesomeIcons.SOLID_Dragon,
            FontAwesomeIcons.SOLID_Fish,
            FontAwesomeIcons.SOLID_Frog,
            FontAwesomeIcons.SOLID_Hippo,
            FontAwesomeIcons.SOLID_Horse,
            FontAwesomeIcons.SOLID_KiwiBird,
            FontAwesomeIcons.SOLID_Otter,
    };

    public static FontAwesomeIcons getRandomAnimalIcon() {
        return animalIcons[rand.nextInt(animalIcons.length)];
    }
}
