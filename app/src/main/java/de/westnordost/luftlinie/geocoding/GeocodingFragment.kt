package de.westnordost.luftlinie.geocoding

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Rect
import android.graphics.Typeface
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.inputmethod.EditorInfo
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import de.westnordost.luftlinie.MainViewModel
import de.westnordost.luftlinie.R
import de.westnordost.luftlinie.view.SimpleRecyclerViewAdapter
import de.westnordost.osmfeatures.FeatureDictionary
import kotlinx.android.synthetic.main.row_geocode_result.view.*
import kotlinx.android.synthetic.main.fragment_geocoding.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

/** Fragment that manages the geocoding search and user selection of the correct result */
class GeocodingFragment : Fragment(R.layout.fragment_geocoding) {

    private val featureDictionary: FeatureDictionary by inject()

    private val model: GeocodingViewModel by viewModel()
    private val mainModel: MainViewModel by sharedViewModel()

    /* --------------------------------------- Lifecycle --------------------------------------- */

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupFittingToSystemWindowInsets()

        setupResultsViewItemSpacing()

        inputTextView.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_NULL) {
                model.search(v.text.toString().trim())
                true
            } else {
                false
            }
        }

        model.results.observe(viewLifecycleOwner, this::updateResults)
        updateResults(model.results.value)

        model.isSearching.observe(viewLifecycleOwner, this::updateSearching)
        updateSearching(model.isSearching.value ?: false)

        copyrightTextView.setOnClickListener { openUrl("https://www.openstreetmap.org/copyright") }
    }

    private fun setupResultsViewItemSpacing() {
        val xSpace = requireContext().resources.getDimensionPixelSize(R.dimen.activity_margin)
        val ySpace = requireContext().resources.getDimensionPixelSize(R.dimen.item_spacing)/2
        geocodeResultsView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                outRect.set(xSpace, ySpace, xSpace, ySpace)
            }
        })
    }

    private fun setupFittingToSystemWindowInsets() {
        view?.setOnApplyWindowInsetsListener { v: View, insets: WindowInsets ->
            birdImageView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = insets.systemWindowInsetTop + v.context.resources.getDimensionPixelSize(R.dimen.activity_margin)*2
            }
            copyrightTextView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = insets.systemWindowInsetBottom
            }
            insets
        }
    }

    private fun openUrl(url: String): Boolean {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        return tryStartActivity(intent)
    }

    private fun tryStartActivity(intent: Intent): Boolean {
        return try {
            startActivity(intent)
            true
        } catch (e: ActivityNotFoundException) {
            false
        }
    }

    /* -------------------------------------- View updates -------------------------------------- */

    private fun updateResults(results: List<GeocodingResult>?) {
        geocodeNoResultsTextView.visibility = if (results != null && results.isEmpty()) View.VISIBLE else View.GONE
        geocodeResultsView.visibility = if (results != null && !results.isEmpty()) View.VISIBLE else View.GONE
        copyrightTextView.visibility = if (results != null && !results.isEmpty()) View.VISIBLE else View.GONE
        birdImageView.visibility = if (results == null) View.VISIBLE else View.GONE

        if (results != null && !results.isEmpty()) {
            geocodeResultsView.adapter = object : SimpleRecyclerViewAdapter(R.layout.row_geocode_result) {

                override fun getItemCount(): Int = results.size

                override fun onBindView(view: View, position: Int) {
                    val result = results[position]
                    view.resultTextView.text = result.displayName
                    view.resultTypeTextView.text = getFeatureName(result.key to result.value)
                    if (position == 0) {
                        view.resultTextView.setTypeface(null, Typeface.BOLD)
                    }
                    view.setOnClickListener { onClickedResult(result) }
                }
            }
            geocodeResultsView.scheduleLayoutAnimation()
        }
    }

    private fun updateSearching(searching: Boolean) {
        if (searching) {
            progressBar.show()
            inputTextView.isEnabled = false
        } else {
            progressBar.hide()
            inputTextView.isEnabled = true
        }
    }

    private fun getFeatureName(tag: Pair<String, String>): String? =
        featureDictionary.byTags(mapOf(tag)).find().firstOrNull()?.name

    private fun onClickedResult(result: GeocodingResult) {
        mainModel.destinationLocation.value = Location(null as String?).apply {
            longitude = result.position.longitude
            latitude = result.position.latitude
        }
    }
}