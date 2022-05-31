package view.local.zz.customviews.views.videosurface;

import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;

import view.local.zz.customviews.R;

public class MediaPlayerUtil {

    private MediaPlayer mediaPlayer;
    private Point mPreSize;
    public MediaPlayerUtil(Resources resources, final VideoRender videoRender) {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setLooping(true);
        try {
            AssetFileDescriptor test1 = resources.openRawResourceFd(R.raw.test1);
            mediaPlayer.setDataSource(test1.getFileDescriptor(), test1.getStartOffset(), test1.getLength());
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mPreSize = new Point(mp.getVideoWidth(), mp.getVideoHeight());
                videoRender.setPoint(mPreSize);
                mp.start();
            }
        });

    }

    public void setSurface(SurfaceTexture texture) {
        Surface surface = new Surface(texture);
        mediaPlayer.setSurface(surface);
        mediaPlayer.prepareAsync();
    }



}
