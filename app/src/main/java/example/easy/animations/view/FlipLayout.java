package example.easy.animations.view;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;
import android.widget.FrameLayout;

public class FlipLayout extends FrameLayout
    implements Animation.AnimationListener, View.OnClickListener, OnSwipeListener {
  public static final int ANIM_DURATION_MILLIS = 500;
  private static final Interpolator fDefaultInterpolator = new DecelerateInterpolator();
  private OnFlipListener mListener;
  private FlipAnimator mAnimation;
  private boolean mIsFlipped;
  private Direction mDirection;
  private OnSwipeTouchListener mOnTouchListener;
  private View mFrontView, mBackView;

  public FlipLayout(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init(context);
  }

  public FlipLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }

  public FlipLayout(Context context) {
    super(context);
    init(context);
  }

  private void init(Context context) {
    mAnimation = new FlipAnimator();
    mAnimation.setAnimationListener(this);
    mAnimation.setInterpolator(fDefaultInterpolator);
    mAnimation.setDuration(ANIM_DURATION_MILLIS);
    mDirection = Direction.DOWN;
    setSoundEffectsEnabled(true);
    mOnTouchListener = new OnSwipeTouchListener(context);
  }

  @Override protected void onFinishInflate() {
    super.onFinishInflate();

    if (getChildCount() > 2) {
      throw new IllegalStateException("FlipLayout can host only two direct children");
    }

    mFrontView = getChildAt(0);
    mFrontView.setOnTouchListener(mOnTouchListener);
    mFrontView.setOnClickListener(this);
    mBackView = getChildAt(1);
    mBackView.setOnTouchListener(mOnTouchListener);
    mBackView.setOnClickListener(this);
    mOnTouchListener.addSwipeListener(this);
  }

  private void toggleView() {
    if (mFrontView == null || mBackView == null) {
      return;
    }

    if (mIsFlipped) {
      mFrontView.setVisibility(View.VISIBLE);
      mBackView.setVisibility(View.GONE);
    } else {
      mFrontView.setVisibility(View.GONE);
      mBackView.setVisibility(View.VISIBLE);
    }

    mIsFlipped = !mIsFlipped;
  }

  public void setOnFlipListener(OnFlipListener listener) {
    mListener = listener;
  }

  public void reset() {
    mIsFlipped = false;
    mDirection = Direction.DOWN;
    mFrontView.setVisibility(View.VISIBLE);
    mBackView.setVisibility(View.GONE);
  }

  public void toggleUp() {
    mDirection = Direction.UP;
    startAnimation();
  }

  public void toggleDown() {
    mDirection = Direction.DOWN;
    startAnimation();
  }

  public void startAnimation() {
    mAnimation.setVisibilitySwapped();
    startAnimation(mAnimation);
  }

  @Override public void onAnimationStart(Animation animation) {
    if (mListener != null) {
      mListener.onFlipStart(this);
    }
  }

  @Override public void onAnimationEnd(Animation animation) {
    if (mListener != null) {
      mListener.onFlipEnd(this);
    }
    mDirection = mDirection == Direction.UP ? Direction.DOWN : Direction.UP;
  }

  @Override public void onAnimationRepeat(Animation animation) {
  }

  public void setAnimationListener(Animation.AnimationListener listener) {
    mAnimation.setAnimationListener(listener);
  }

  @Override public void onClick(View view) {
    toggleDown();
  }

  @Override public void onSwipeLeft() {

  }

  @Override public void onSwipeRight() {

  }

  @Override public void onSwipeUp() {
    toggleUp();
  }

  @Override public void onSwipeDown() {
    toggleDown();
  }

  private enum Direction {
    UP, DOWN
  }

  public interface OnFlipListener {

    void onFlipStart(FlipLayout view);

    void onFlipEnd(FlipLayout view);
  }

  public class FlipAnimator extends Animation {

    public static final float EXPERIMENTAL_VALUE = 50.f;
    private Camera camera;
    private float centerX;
    private float centerY;
    private boolean visibilitySwapped;

    public FlipAnimator() {
      setFillAfter(true);
    }

    public void setVisibilitySwapped() {
      visibilitySwapped = false;
    }

    @Override public void initialize(int width, int height, int parentWidth, int parentHeight) {
      super.initialize(width, height, parentWidth, parentHeight);
      camera = new Camera();
      this.centerX = width / 2;
      this.centerY = height / 2;
    }

    @Override protected void applyTransformation(float interpolatedTime, Transformation t) {
      // Angle around the y-axis of the rotation at the given time. It is
      // calculated both in radians and in the equivalent degrees.
      final double radians = Math.PI * interpolatedTime;

      float degrees = (float) (180.0 * radians / Math.PI);

      if (mDirection == Direction.UP) {
        degrees = -degrees;
      }

      // Once we reach the midpoint in the animation, we need to hide the
      // source view and show the destination view. We also need to change
      // the angle by 180 degrees so that the destination does not come in
      // flipped around. This is the main problem with SDK sample, it does
      // not
      // do this.
      if (interpolatedTime >= 0.5f) {
        if (mDirection == Direction.UP) {
          degrees += 180.f;
        }

        if (mDirection == Direction.DOWN) {
          degrees -= 180.f;
        }

        if (!visibilitySwapped) {
          toggleView();
          visibilitySwapped = true;
        }
      }

      final Matrix matrix = t.getMatrix();

      camera.save();
      camera.translate(0.0f, 0.0f, (float) (EXPERIMENTAL_VALUE * Math.sin(radians)));
      camera.rotateX(degrees);
      camera.rotateY(0);
      camera.rotateZ(0);
      camera.getMatrix(matrix);
      camera.restore();

      matrix.preTranslate(-centerX, -centerY);
      matrix.postTranslate(centerX, centerY);
    }
  }
}