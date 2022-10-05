package cn.letswap.citor.extra;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import cn.letswap.citor.R;

public class CitorButton extends AppCompatButton {

    private static final int[] STATE_ON = {R.attr.state_on};

    public boolean isStateOn() {
        return stateOn;
    }

    public void setStateOn(boolean stateOn) {
        this.stateOn = stateOn;
    }

    private boolean stateOn;

    // 这三个构造函数都要写
    public CitorButton(@NonNull Context context) {
        super(context);
    }

    public CitorButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CitorButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {

        if (stateOn) {
            final int[] drableState = super.onCreateDrawableState(extraSpace + 1);

            mergeDrawableStates(drableState, STATE_ON);
            return drableState;
        }

        return super.onCreateDrawableState(extraSpace);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                stateOn = !stateOn;
                refreshDrawableState();
        }
        return super.onTouchEvent(event);
    }
}
