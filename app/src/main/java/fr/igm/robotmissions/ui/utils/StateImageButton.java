package fr.igm.robotmissions.ui.utils;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

public class StateImageButton extends androidx.appcompat.widget.AppCompatImageButton {
    public StateImageButton(Context context) {
        super(context);
    }

    public StateImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StateImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled) {
            setColorFilter(null);
        } else {
            setColorFilter(Color.LTGRAY);
        }
    }
}
