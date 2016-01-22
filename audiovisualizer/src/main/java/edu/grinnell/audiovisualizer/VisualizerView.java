package edu.grinnell.audiovisualizer;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class VisualizerView extends View {

    public static final String TAG = VisualizerView.class.getSimpleName();

    private static final byte EMPTY_BAR = 100;

    private Visualizer mVisualizer;
    private int mCaptureSize;
    private byte[] mWaveform;
    private int barWidth;
    private int spaceWidth;
    private Rect[] rects;
    private int innerHeight;

    // for painting
    private Paint barPaint = new Paint();

    public VisualizerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
    }

    public VisualizerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VisualizerView(Context context) {
        this(context, null, 0);
    }

    /**
     * init only after link
     */
    public void init() {
        mCaptureSize = 8; // must be 2 or more
        barWidth = 50;
        int visibleWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        spaceWidth = (visibleWidth - mCaptureSize * barWidth) / (mCaptureSize - 1);

        resetWaveform();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        innerHeight = getHeight() - getPaddingBottom() - getPaddingTop();
        init();
    }

    public void setBarColor(int color) {
        barPaint.setColor(color);
    }

    private void resetWaveform() {
        mWaveform = new byte[Visualizer.getCaptureSizeRange()[0]];
        for (int i = 0; i < mCaptureSize; i++)
            mWaveform[i] = EMPTY_BAR;

        initRects();
    }

    @Override
    public boolean isInEditMode() {
        return true;
    }

    private void initRects() {
        rects = new Rect[mCaptureSize];
        int left = getPaddingLeft();
        int height = getHeight() - getPaddingBottom() - getPaddingTop();
        int bottom =  getHeight() - getPaddingBottom();
        for (int i = 0; i < mCaptureSize; i++) {
            int fromBottom = getPaddingBottom() + (int) ((mWaveform[i] / (double) Visualizer.getCaptureSizeRange()[1]) * height);
            rects[i] = new Rect(left, getHeight() - fromBottom, left + barWidth, bottom);
            left += barWidth + spaceWidth;
        }

    }

    public void release() {
        mVisualizer.release();
    }

    /**
     * link a mediaplayer to the visualizer
     *
     * @param player the media player
     */
    public void link(MediaPlayer player) {
        if (player == null)
            throw new IllegalArgumentException("Trying to link a null MediaPlayer.");
        else {
            mVisualizer = new Visualizer(player.getAudioSessionId());

            mVisualizer.setCaptureSize(mCaptureSize);
            Visualizer.OnDataCaptureListener listener = new Visualizer.OnDataCaptureListener() {
                @Override
                public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
                    mWaveform = waveform;
                    invalidate();
                }

                @Override
                public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
                    // do nothing with frequency data
                }
            };
            mVisualizer.setDataCaptureListener(listener, Visualizer.getMaxCaptureRate() / 2, true, false);
            mVisualizer.setEnabled(true);
        }
    }

    public void link(int audioSessionId) {
        mVisualizer = new Visualizer(audioSessionId);

        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[0]);
        Visualizer.OnDataCaptureListener listener = new Visualizer.OnDataCaptureListener() {
            @Override
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
                mWaveform = waveform;
                invalidate();
            }

            @Override
            public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
                // do nothing with frequency data
            }
        };
        mVisualizer.setDataCaptureListener(listener, Visualizer.getMaxCaptureRate(), true, false);
        mVisualizer.setEnabled(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < mCaptureSize; i++) {
            Rect r = rects[i];
            int barHeight = mWaveform[i * (Visualizer.getCaptureSizeRange()[0] / mCaptureSize)] + 128;
            float ratio = barHeight / 256f;
            int fromBottom = (int) (ratio * innerHeight);
            r.top = getHeight() - getPaddingBottom() - fromBottom;
            canvas.drawRect(r, barPaint);
        }

    }
}
