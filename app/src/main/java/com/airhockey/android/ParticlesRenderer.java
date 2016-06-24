package com.airhockey.android;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLSurfaceView.Renderer;

import com.airhockey.android.objects.ParticleShooter;
import com.airhockey.android.objects.ParticleSystem;
import com.airhockey.android.objects.Skybox;
import com.airhockey.android.programs.ParticleShaderProgram;
import com.airhockey.android.programs.SkyboxShaderProgram;
import com.airhockey.android.util.Geometry;
import com.airhockey.android.util.Geometry.Point;
import com.airhockey.android.util.Geometry.Vector;
import com.airhockey.android.util.MatrixHelper;
import com.airhockey.android.util.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;
import static android.opengl.Matrix.*;

/**
 * Created by Jonathan on 6/14/2016.
 */
public class ParticlesRenderer implements Renderer {
    private final Context context;
    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];

    // Particle fields
    private ParticleShaderProgram particleProgram;
    private ParticleSystem particleSystem;
    private ParticleShooter redParticleShooter;
    private ParticleShooter greenParticleShooter;
    private ParticleShooter blueParticleShooter;
    long globalStartTime;
    final float angleVarianceInDegrees = 5f;
    final float speedVariance = 1f;
    private int particleTexture;

    private SkyboxShaderProgram skyboxProgram;
    private Skybox skybox;
    private int skyboxTexture;

    public ParticlesRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        // Particle initialization
        particleProgram = new ParticleShaderProgram(context);
        particleSystem = new ParticleSystem(10000);
        globalStartTime = System.nanoTime();
        final Geometry.Vector particleDirection = new Vector(0f, 0.5f, 0f);
        redParticleShooter = new ParticleShooter(
                new Point(-1f, 0f, 0f),
                particleDirection,
                Color.rgb(255, 50, 5),
                angleVarianceInDegrees,
                speedVariance );
        greenParticleShooter = new ParticleShooter(
                new Point(0f, 0f, 0f),
                particleDirection,
                Color.rgb(25, 255, 25),
                angleVarianceInDegrees,
                speedVariance );
        blueParticleShooter = new ParticleShooter(
                new Point(1f, 0f, 0f),
                particleDirection,
                Color.rgb(5, 50, 255),
                angleVarianceInDegrees,
                speedVariance );

        // load particleTexture for particles
        particleTexture = TextureHelper.loadTexture(context, R.drawable.particle_texture);

        // skybox initialization
        skyboxProgram = new SkyboxShaderProgram(context);
        skybox = new Skybox();
        skyboxTexture = TextureHelper.loadCubeMap(context,
                new int[] { R.drawable.left, R.drawable.right,
                        R.drawable.bottom, R.drawable.top,
                        R.drawable.front, R.drawable.back});
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);
        MatrixHelper.perspectiveM(projectionMatrix, 45, (float) width
                / (float) height, 1f, 10f);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT);
        drawSkybox();
        drawParticles();
    }

    private void drawParticles() {
        float currentTime = (System.nanoTime() - globalStartTime) / 1000000000f;

        redParticleShooter.addParticles(particleSystem, currentTime, 5);
        greenParticleShooter.addParticles(particleSystem, currentTime, 5);
        blueParticleShooter.addParticles(particleSystem, currentTime, 5);

        setIdentityM(viewMatrix, 0);
        translateM(viewMatrix, 0, 0f, -1.5f, -5f);
        rotateM(viewMatrix, 0, -yRotation, 1f, 0f, 0f);
        rotateM(viewMatrix, 0, -xRotation, 0f, 1f, 0f);
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE);

        particleProgram.useProgram();
        particleProgram.setUniforms(viewProjectionMatrix, currentTime, particleTexture);
        particleSystem.bindData(particleProgram);
        particleSystem.draw();

        glDisable(GL_BLEND);
    }

    public void drawSkybox(){
        setIdentityM(viewMatrix, 0);
        rotateM(viewMatrix, 0, -yRotation, 1f, 0f, 0f);
        rotateM(viewMatrix, 0, -xRotation, 0f, 1f, 0f);
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        skyboxProgram.useProgram();
        skyboxProgram.setUniforms(viewProjectionMatrix, skyboxTexture);
        skybox.bindData(skyboxProgram);
        skybox.draw();
    }

    private float xRotation, yRotation;
    public void handleTouchDrag(float deltaX, float deltaY) {
        xRotation += deltaX / 16f;
        yRotation += deltaY / 16f;
        if (yRotation < -90) {
            yRotation = -90;
        } else if (yRotation > 90) {
            yRotation = 90;
        }
    }

}
