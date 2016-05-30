package com.medhelp.medhelp.helpers;

import com.medhelp.medhelp.model.EBodyType;

import java.util.HashMap;

public class BodyPartHelper {

    public static HashMap<String, EBodyType> mBodyPartsMap = new HashMap<String, EBodyType>() {
        {
            put("Cabeça", EBodyType.Head);
            put("Peito", EBodyType.Chest);
            put("Abdómem", EBodyType.Abs);
            put("Braço direito", EBodyType.RightArm);
            put("Braço esquerdo", EBodyType.LeftArm);
            put("Perna direita", EBodyType.RightLeg);
            put("Perna esquerda", EBodyType.LeftLeg);
        }
    };

}
