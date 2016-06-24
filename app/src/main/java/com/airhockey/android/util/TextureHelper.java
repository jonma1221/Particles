package com.airhockey.android.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import static android.opengl.GLES20.*;
import static android.opengl.GLUtils.*;

/**
 * Created by Jonathan on 6/16/2016.
 */
public class TextureHelper {
    private static final String TAG = "TextureHelper";

    public static int loadTexture(Context context, int resourceId) {
        final int[] textureObjectIds = new int[1];
        glGenTextures(1, textureObjectIds, 0);
        if (textureObjectIds[0] == 0) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "Could not generate a new OpenGL texture object.");
            }
            return 0;
        }

        // decompress image files into a form that OpenGL understands
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        final Bitmap bitmap = BitmapFactory.decodeResource(
                context.getResources(), resourceId, options); // decode image into bitmap

        if (bitmap == null) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "Resource ID " + resourceId + " could not be decoded.");
            }
            glDeleteTextures(1, textureObjectIds, 0);
            return 0;
        }

        // tell OpenGL that future texture calls should be applied to this texture object
        glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);
        // set filters
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        // tells OpenGL to read in the bitmap data defined by bitmap and copy
        // it over into the texture object that is currently bound
        texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);

        bitmap.recycle(); // release the bitmap since we no longer need it

        glGenerateMipmap(GL_TEXTURE_2D);

        glBindTexture(GL_TEXTURE_2D, 0); /*unbind from the texture so that we don’t accidentally make
                                           further changes to this texture with other texture calls*/

        return textureObjectIds[0];
    }

    public static int loadCubeMap(Context context, int[] cubeResources){
        final int[] textureObjectIds = new int[1];
        glGenTextures(1, textureObjectIds, 0);

        if (textureObjectIds[0] == 0) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "Could not generate a new OpenGL texture object.");
            }
            return 0;
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        final Bitmap[] cubeBitmaps = new Bitmap[6];

        // pass in 6 image resources
        for (int i = 0; i < 6; i++) {
            cubeBitmaps[i] =
                    BitmapFactory.decodeResource(context.getResources(),
                            cubeResources[i], options);
            if (cubeBitmaps[i] == null) {
                if (LoggerConfig.ON) {
                    Log.w(TAG, "Resource ID " + cubeResources[i]
                            + " could not be decoded.");
                }
                glDeleteTextures(1, textureObjectIds, 0);
                return 0;
            }
        }
        Log.d("Before Error", "");
        glBindTexture(GL_TEXTURE_CUBE_MAP, textureObjectIds[0]);

        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        // associate each image with the appropriate face
        texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, cubeBitmaps[0], 0);
        texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, cubeBitmaps[1], 0);
        texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, cubeBitmaps[2], 0);
        texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, cubeBitmaps[3], 0);
        texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, cubeBitmaps[4], 0);
        texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, cubeBitmaps[5], 0);

        glBindTexture(GL_TEXTURE_2D, 0);
        for (Bitmap bitmap : cubeBitmaps) {
            bitmap.recycle();
        }
        return textureObjectIds[0];

    }
}
