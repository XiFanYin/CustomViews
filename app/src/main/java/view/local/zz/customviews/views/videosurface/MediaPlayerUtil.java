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

    public MediaPlayerUtil(Resources resources) {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setLooping(true);
        try {
            AssetFileDescriptor test1 = resources.openRawResourceFd(R.raw.test2);
            mediaPlayer.setDataSource(test1.getFileDescriptor(), test1.getStartOffset(), test1.getLength());
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        //创建图片和预览的宽高
        mPreSize = new Point(mediaPlayer.getVideoWidth(), mediaPlayer.getVideoHeight());
        Log.e("rrrrrrrrrrrrrrrr", mPreSize.toString());

    }

    public void setSurface(SurfaceTexture texture) {
        Surface surface = new Surface(texture);
        mediaPlayer.setSurface(surface);
        mediaPlayer.prepareAsync();
    }

    public Point getmPreSize() {
        return mPreSize;
    }

}
