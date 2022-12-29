package de.westnordost.luftlinie.location

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.location.Location
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import de.westnordost.luftlinie.R
import de.westnordost.luftlinie.databinding.WidgetDestinationPointerBinding
import java.util.*
import kotlin.math.roundToInt

class DestinationPointerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding = WidgetDestinationPointerBinding.inflate(LayoutInflater.from(context), this)
    private val rnd = Random()

    var currentLocation: Location? = null
        set(value) {
            field = value
            // TODO only for debugging
            val h = rnd.nextFloat() * 360f
            val color: Int = Color.HSVToColor(floatArrayOf(h, 0.75f, 1f))
            binding.targetNeedleView.imageTintList = ColorStateList.valueOf(color)
            update()
        }
    var destinationLocation: Location? = null
        set(value) {
            field = value
            update()
        }
    var deviceRotation: Float = 0f
        set(value) {
            field = value
            update()
        }


    private fun update() {
        val start = currentLocation ?: return
        val destination = destinationLocation ?: return
        val bearing = start.bearingTo(destination)
        binding.targetNeedleView.rotation = bearing - deviceRotation

        val dist = start.distanceTo(destination)
        binding.distanceTextView.text = dist.toDisplayDistance()

        val inaccuracy = start.accuracy + destination.accuracy
        val inaccuracyIsRelevant = inaccuracy / dist >= 0.333
        binding.inaccuracyTextView.visibility = if (inaccuracyIsRelevant) View.VISIBLE else View.INVISIBLE
        binding.inaccuracyTextView.text = "+/- " + inaccuracy.toDisplayDistance()

        val drawable = context.getDrawable(
            if (dist <= inaccuracy) R.drawable.ic_circle_black_240dp
            else R.drawable.ic_compass_needle_black_280dp
        )
        binding.targetNeedleView.setImageDrawable(drawable)
    }
}

private fun Float.toDisplayDistance(): String =
    if (this < 1000) "${this.roundToInt()} m"
    else "${"%.1f".format((this/100f).roundToInt()/10f)} km"