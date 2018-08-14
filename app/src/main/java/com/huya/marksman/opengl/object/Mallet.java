package com.huya.marksman.opengl.object;


import com.huya.marksman.opengl.object.bufferobject.VertexArray;
import com.huya.marksman.opengl.programs.ColorShaderProgram;
import com.huya.marksman.opengl.util.Geometry;
import com.huya.marksman.opengl.util.ObjectBuilder;
import com.huya.marksman.opengl.util.ObjectBuilder.*;

import java.util.List;

/**
 * Created by charles on 2018/6/30.
 */

public class Mallet {
    private static final int POSITION_COMPONENT_COUNT = 3;

    public final float radius;
    public final float height;

    private final VertexArray vertexArray;
    private final List<DrawCommand> drawList;

    public Mallet(float radius, float height, int numPointsAroundMallet) {
        GeneratedData generatedData = ObjectBuilder.createMallet(new Geometry.Point(0f,
                0f, 0f), radius, height, numPointsAroundMallet);

        this.radius = radius;
        this.height = height;

        vertexArray = new VertexArray(generatedData.vertexData);
        drawList = generatedData.drawList;
    }

    public void bindData(ColorShaderProgram colorProgram) {
        vertexArray.setVertexAttributePointer(0,
                colorProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT, 0);
    }

    public void draw() {
        for (DrawCommand drawCommand : drawList) {
            drawCommand.draw();
        }
    }
}
