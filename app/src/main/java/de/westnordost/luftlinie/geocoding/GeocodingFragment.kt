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
import de.westnordost.luftlinie.databinding.FragmentGeocodingBinding
import de.westnordost.luftlinie.databinding.RowGeocodeResultBinding
import de.westnordost.luftlinie.view.SimpleRecyclerViewAdapter
import de.westnordost.osmfeatures.FeatureDictionary
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

/** Fragment that manages the geocoding search and user selection of the correct result */
class GeocodingFragment : Fragment(R.layout.fragment_geocoding) {

    private val featureDictionary: FeatureDictionary by inject()

    private lateinit var binding: FragmentGeocodingBinding
    private val model: GeocodingViewModel by viewModel()
    private val mainModel: MainViewModel by sharedViewModel()

    /* --------------------------------------- Lifecycle --------------------------------------- */

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentGeocodingBinding.bind(view)

        setupFittingToSystemWindowInsets()

        setupResultsViewItemSpacing()


        binding.inputTextView.setOnEditorActionListener { v, actionId, event ->
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

        binding.copyrightTextView.setOnClickListener { openUrl("https://www.openstreetmap.org/copyright") }
    }

    private fun setupResultsViewItemSpacing() {
        val xSpace = requireContext().resources.getDimensionPixelSize(R.dimen.activity_margin)
        val ySpace = requireContext().resources.getDimensionPixelSize(R.dimen.item_spacing)/2
        binding.geocodeResultsView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                outRect.set(xSpace, ySpace, xSpace, ySpace)
            }
        })
    }

    private fun setupFittingToSystemWindowInsets() {
        view?.setOnApplyWindowInsetsListener { v: View, insets: WindowInsets ->
            binding.birdImageView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = insets.systemWindowInsetTop + v.context.resources.getDimensionPixelSize(R.dimen.activity_margin)*2
            }
            binding.copyrightTextView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
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
        binding.geocodeNoResultsTextView.visibility = if (results != null && results.isEmpty()) View.VISIBLE else View.GONE
        binding.geocodeResultsView.visibility = if (results != null && !results.isEmpty()) View.VISIBLE else View.GONE
        binding.copyrightTextView.visibility = if (results != null && !results.isEmpty()) View.VISIBLE else View.GONE
        binding.birdImageView.visibility = if (results == null) View.VISIBLE else View.GONE

        if (results != null && !results.isEmpty()) {
            binding.geocodeResultsView.adapter = object : SimpleRecyclerViewAdapter(R.layout.row_geocode_result) {

                override fun getItemCount(): Int = results.size

                override fun onBindView(view: View, position: Int) {
                    val result = results[position]
                    val binding = RowGeocodeResultBinding.bind(view)
                    binding.resultTextView.text = result.displayName
                    binding.resultTypeTextView.text = getFeatureName(result.key to result.value)
                    if (position == 0) {
                        binding.resultTextView.setTypeface(null, Typeface.BOLD)
                    }
                    binding.root.setOnClickListener { onClickedResult(result) }
                }
            }
            binding.geocodeResultsView.scheduleLayoutAnimation()
        }
    }

    private fun updateSearching(searching: Boolean) {
        if (searching) {
            binding.progressBar.show()
            binding.inputTextView.isEnabled = false
        } else {
            binding.progressBar.hide()
            binding.inputTextView.isEnabled = true
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