package com.airhockey.android.objects;

import com.airhockey.android.data.VertexArray;
import com.airhockey.android.programs.SkyboxShaderProgram;

import java.nio.ByteBuffer;
import static android.opengl.GLES20.*;

/**
 * Created by pixuredlinux3 on 6/20/16.
 */
public class Skybox {
    private static final int POSITION_COMPONENT_COUNT = 3;
    VertexArray vertexArray;
    private final ByteBuffer indexArray;
    public Skybox() {
        // creates a cube
        vertexArray = new VertexArray(new float[]{
                -1, 1, 1,  // Top left near      (0)
                1 , 1, 1, // Top right near      (1)
                -1, -1, 1, // bottom left near   (2)
                1, -1, 1, // bottom right near   (3)
                -1, 1, -1, // top left far       (4)
                1, 1, -1, // top right far       (5)
                -1, -1, -1, // bottom left far   (6)
                1, -1, -1, // bottom right far   (7)
        });

        // index array
        indexArray = ByteBuffer.allocateDirect(6 * 6)
                .put(new byte[]{
                   //front
                   1, 3, 0,
                   0, 3, 2,

                   // Back
                   4, 6, 5,
                   5, 6, 7,

                   // left
                   0, 2, 4,
                   4, 2, 6,

                   // right
                   5, 7, 1,
                   1, 7, 3,

                   // top
                   5, 1, 4,
                   4, 1, 0,

                   // bottom
                   6, 2, 7,
                   7, 2, 3
                });
        indexArray.position(0);
    }

    public void bindData(SkyboxShaderProgram skyboxProgram) {
        vertexArray.setVertexAttribPointer(0,
                skyboxProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT, 0);
    }

    public void draw() {
        glDrawElements(GL_TRIANGLES, 36, GL_UNSIGNED_BYTE, indexArray);
    }
}
