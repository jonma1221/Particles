package com.airhockey.android.programs;

import android.content.Context;
import android.graphics.Shader;
import static android.opengl.GLES20.*;
import com.airhockey.android.R;

/**
 * Created by pixuredlinux3 on 6/20/16.
 */
public class ParticleShaderProgram extends ShaderProgram {
    // Uniform locations
    private final int uMatrixLocation;
    private final int uTimeLocation;
    private final int uTextureUnitLocation;

    // Attribute locations
    private final int aPositionLocation;
    private final int aColorLocation;
    private final int aDirectionVectorLocation;
    private final int aParticleStartTimeLocation;

    public ParticleShaderProgram(Context context) {
        super(context, R.raw.particle_vertex_shader,
                R.raw.particle_fragment_shader);
        // Retrieve uniform locations for the shader program.
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        uTimeLocation = glGetUniformLocation(program, U_TIME);
        uTextureUnitLocation = glGetUniformLocation(program, U_TEXTURE_UNIT);

        // Retrieve attribute locations for the shader program.
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aColorLocation = glGetAttribLocation(program, A_COLOR);
        aDirectionVectorLocation = glGetAttribLocation(program, A_DIRECTION_VECTOR);
        aParticleStartTimeLocation =
                glGetAttribLocation(program, A_PARTICLE_START_TIME);
    }

    public void setUniforms(float[] matrix, float elapsedTime, int textureId) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
        glUniform1f(uTimeLocation, elapsedTime);

        // bind the textures
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureId);
        glUniform1i(uTextureUnitLocation, 0);
    }

    public int getPositionAttributeLocation(){
        return aPositionLocation;
    }
    public int getColorAttributeLocation(){
        return aColorLocation;
    }
    public int getDirectionVectorAttributeLocation(){
        return aDirectionVectorLocation;
    }
    public int getParticleStartTimeAttributeLocation(){
        return aParticleStartTimeLocation;
    }

}
