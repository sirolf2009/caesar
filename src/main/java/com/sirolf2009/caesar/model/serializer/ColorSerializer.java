package com.sirolf2009.caesar.model.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import javafx.scene.paint.Color;

public class ColorSerializer extends CaesarSerializer<Color> {
    @Override
    public void write(Kryo kryo, Output output, Color object) {
        output.writeDouble(object.getRed());
        output.writeDouble(object.getGreen());
        output.writeDouble(object.getBlue());
        output.writeDouble(object.getOpacity());
    }

    @Override
    public Color read(Kryo kryo, Input input, Class<Color> type) {
        double r = input.readDouble();
        double g = input.readDouble();
        double b = input.readDouble();
        double o = input.readDouble();
        return new Color(r, g, b, o);
    }
}
