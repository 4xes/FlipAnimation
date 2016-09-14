package example.easy.animations.view;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import java.util.ArrayList;
import java.util.List;

public class OnSwipeTouchListener implements OnTouchListener {

  private final GestureDetector gestureDetector;
  public List<OnSwipeListener> swipeListeners = new ArrayList<>();

  public OnSwipeTouchListener(Context context) {
    gestureDetector = new GestureDetector(context.getApplicationContext(), new GestureListener());
  }

  @Override public boolean onTouch(View v, MotionEvent event) {
    return gestureDetector.onTouchEvent(event);
  }

  public void addSwipeListener(OnSwipeListener swipeListener) {
    if (swipeListener != null && !swipeListeners.contains(swipeListener)) {
      swipeListeners.add(swipeListener);
    }
  }

  public void removeSwipeListener(OnSwipeListener swipeListener) {
    if (swipeListener != null && swipeListeners.contains(swipeListener)) {
      swipeListeners.remove(swipeListener);
    }
  }

  public void removeSwipeListeners() {
    swipeListeners.clear();
  }

  private final class GestureListener extends SimpleOnGestureListener {

    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
      boolean result = false;
      try {
        float diffY = e2.getY() - e1.getY();
        float diffX = e2.getX() - e1.getX();
        if (Math.abs(diffX) > Math.abs(diffY)) {
          if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
            if (diffX > 0) {
              for (OnSwipeListener swipeListener : swipeListeners) {
                swipeListener.onSwipeRight();
                Log.d("Gesture", "SwipeRight");
              }
            } else {
              for (OnSwipeListener swipeListener : swipeListeners) {
                swipeListener.onSwipeLeft();
                Log.d("Gesture", "SwipeLeft");
              }
            }
          }
          result = true;
        } else if (Math.abs(diffY) > SWIPE_THRESHOLD
            && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
          if (diffY > 0) {
            for (OnSwipeListener swipeListener : swipeListeners) {
              swipeListener.onSwipeDown();
              Log.d("Gesture", "SwipeDown");
            }
          } else {
            for (OnSwipeListener swipeListener : swipeListeners) {
              swipeListener.onSwipeUp();
              Log.d("Gesture", "SwipeUp");
            }
          }
        }
        result = true;
      } catch (Exception exception) {
        exception.printStackTrace();
      }

      return result;
    }
  }
}