package com.mhy.landrestoration.ui.mapbox

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.bindgen.Expected
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.QueriedFeature
import com.mapbox.maps.ResourceOptionsManager
import com.mapbox.maps.extension.style.expressions.generated.Expression.Companion.interpolate
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.gestures.OnMapClickListener
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.viewannotation.ViewAnnotationManager
import com.mhy.landrestoration.BuildConfig
import com.mhy.landrestoration.databinding.FragmentMapBoxBinding
import com.mhy.landrestoration.util.ShowAlert
import com.mhy.landrestoration.viewmodels.CoordinateListViewModel

private const val TAG = "MapBoxFragment"

abstract class MapBoxFragment : Fragment(), OnMapClickListener {

    protected val coordinateListViewModel: CoordinateListViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(
            this,
            CoordinateListViewModel.Factory(activity.application)
        )[CoordinateListViewModel::class.java]
    }

    private var binding: FragmentMapBoxBinding? = null

    protected val showAlert = ShowAlert()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onMapReady()
        } else {
            checkLocationPermission()
        }
    }

    private fun checkLocationPermission() {
        when {
            requireContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                onMapReady()
            }

            requireActivity().shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                showAlert.show(
                    requireContext(),
                    "權限請求",
                    "需要定位請求!",
                    "確定",
                    { _, _ -> requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) })
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    protected lateinit var mapView: MapView

    protected lateinit var viewAnnotationManager: ViewAnnotationManager

    private var location: Point? = null

    protected val asyncInflater by lazy { AsyncLayoutInflater(requireContext()) }

    private var isUserLocationInit = false

    private lateinit var locationComponentPlugin: LocationComponentPlugin

    private val onIndicatorBearingChangedListener = OnIndicatorBearingChangedListener {
        if (!isUserLocationInit) {
            mapView.getMapboxMap().setCamera(CameraOptions.Builder().bearing(it).build())
        }
    }

    private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
        location = it
        if (!isUserLocationInit) {
            mapView.getMapboxMap().setCamera(CameraOptions.Builder().center(it).zoom(15.0).build())
            isUserLocationInit = true
        }
        mapView.gestures.focalPoint = mapView.getMapboxMap().pixelForCoordinate(it)
    }

    private val onMoveListener = object : OnMoveListener {
        override fun onMoveBegin(detector: MoveGestureDetector) {
//            onCameraTrackingDismissed()
        }

        override fun onMove(detector: MoveGestureDetector): Boolean {
            return false
        }

        override fun onMoveEnd(detector: MoveGestureDetector) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ResourceOptionsManager.getDefault(
            requireContext(),
            defaultToken = BuildConfig.MapboxAccessToken
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val fragmentBinding = FragmentMapBoxBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            lifecycleOwner = viewLifecycleOwner

            this@MapBoxFragment.mapView = mapView

            viewAnnotationManager = mapView.viewAnnotationManager

            checkLocationPermission()
            imgMyLoc.setOnClickListener {
                setUserLocation()
            }
        }
    }

    private fun enrollIndicator() {
        locationComponentPlugin.addOnIndicatorPositionChangedListener(
            onIndicatorPositionChangedListener
        )
        locationComponentPlugin.addOnIndicatorBearingChangedListener(
            onIndicatorBearingChangedListener
        )
    }

    private fun onCameraTrackingDismissed() {
        Toast.makeText(requireContext(), "onCameraTrackingDismissed", Toast.LENGTH_SHORT).show()
        mapView.location
            .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView.location
            .removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        mapView.gestures.removeOnMoveListener(onMoveListener)
    }

    abstract fun onMapReady()

    override fun onMapClick(point: Point): Boolean {
        return true
    }

    protected fun onFeatureClicked(
        expected: Expected<String, List<QueriedFeature>>,
        onFeatureClicked: (Feature) -> Unit
    ) {
        if (expected.isValue && expected.value?.size!! > 0) {
            expected.value?.get(0)?.feature?.let { feature ->
                onFeatureClicked.invoke(feature)
            }
        }
    }

    protected fun initLocationComponent() {
        locationComponentPlugin = mapView.location
        locationComponentPlugin.updateSettings {
            this.enabled = true
            this.locationPuck = LocationPuck2D(
                bearingImage = AppCompatResources.getDrawable(
                    requireContext(),
                    com.mapbox.maps.R.drawable.mapbox_user_puck_icon
                ),
                shadowImage = AppCompatResources.getDrawable(
                    requireContext(),
                    com.mapbox.maps.R.drawable.mapbox_user_icon_shadow,
                ),
                scaleExpression = interpolate {
                    linear()
                    zoom()
                    stop {
                        literal(0.0)
                        literal(0.6)
                    }
                    stop {
                        literal(20.0)
                        literal(1.0)
                    }
                }.toJson()
            )
        }
        enrollIndicator()
    }

    protected fun setupGesturesListener() {
        mapView.gestures.addOnMoveListener(onMoveListener)
    }

    private fun setUserLocation() {
        location?.let {
            mapView.getMapboxMap()
                .setCamera(CameraOptions.Builder().center(location).zoom(15.0).build())
        }
    }

    open fun setupLayer() {

    }


    protected fun Float.dpToPx() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        resources.displayMetrics
    )

    override fun onDestroy() {
        super.onDestroy()
        mapView.location
            .removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        mapView.location
            .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView.gestures.removeOnMoveListener(onMoveListener)
    }
}