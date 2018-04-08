package com.sirolf2009.caesar.model.serializer;

import com.esotericsoftware.kryo.Kryo;
import javafx.scene.paint.Color;

public class CaesarKryo extends Kryo {

    public CaesarKryo() {
        super();
        register(Color.class, new ColorSerializer());
    }

}
