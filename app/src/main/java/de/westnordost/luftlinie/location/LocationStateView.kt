package de.westnordost.luftlinie.location

import android.content.Context
import android.graphics.drawable.Animatable
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import de.westnordost.luftlinie.R
import de.westnordost.luftlinie.databinding.WidgetLocationStateBinding
import de.westnordost.luftlinie.location.LocationState.*

class LocationStateView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding = WidgetLocationStateBinding.inflate(LayoutInflater.from(context), this)

    init {
        inflate(context, R.layout.widget_location_state, this)
        orientation = VERTICAL
        gravity = Gravity.CENTER_HORIZONTAL

        val typedValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, typedValue, true)
        setBackgroundResource(typedValue.resourceId)
    }

    var state: LocationState = DENIED
    set(value) {
        field = value
        val drawable = context.getDrawable(value.imageResId)
        binding.locationStateImageView.setImageDrawable(drawable)
        if (drawable is Animatable) {
            if(!drawable.isRunning) drawable.start()
        }
        val textResId = value.descriptionResId
        if (textResId != 0) {
            binding.locationStateDescriptionView.setText(textResId)
        } else {
            binding.locationStateDescriptionView.text = null
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val ss = SavedState(superState)
        ss.state = state
        return ss
    }

    override fun onRestoreInstanceState(s: Parcelable) {
        val ss = s as SavedState
        super.onRestoreInstanceState(ss.superState)
        state = ss.state
    }

    internal class SavedState : BaseSavedState {
        var state: LocationState = DENIED

        constructor(source: Parcel) : super(source) {
            state = valueOf(source.readString()!!)
        }

        constructor(superState: Parcelable?) : super(superState)

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeString(state.name)
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(source: Parcel): SavedState = SavedState(source)
                override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
            }
        }
    }
}

private val LocationState.descriptionResId get() = when(this) {
    DENIED -> R.string.location_permission_denied
    ALLOWED -> R.string.location_off
    SEARCHING -> R.string.location_waiting_for_fix
    else -> 0
}

private val LocationState.imageResId get() = when(this) {
    DENIED -> R.drawable.ic_location_disabled_black_240dp
    ALLOWED -> R.drawable.ic_location_nolocation_black_240dp
    ENABLED -> R.drawable.ic_location_nolocation_black_240dp
    SEARCHING -> R.drawable.ic_location_searching_black_240dp
    UPDATING -> R.drawable.ic_location_black_240dp
}