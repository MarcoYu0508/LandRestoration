package com.mhy.landrestoration.ui.mapbox

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.maps.*
import com.mapbox.maps.extension.style.expressions.dsl.generated.rgb
import com.mapbox.maps.extension.style.image.image
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.rasterLayer
import com.mapbox.maps.extension.style.layers.generated.symbolLayer
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.sources.generated.rasterSource
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.extension.style.types.transitionOptions
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import com.mhy.landrestoration.R
import com.mhy.landrestoration.util.BitmapUtil
import com.mhy.landrestoration.util.CoordinateUtil
import com.mhy.landrestoration.util.resize
import com.mhy.landrestoration.viewmodels.CoordinateSelectViewModel
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class SelectPointMapFragment : MapBoxFragment() {

    companion object {
        private const val PLACE_ICON_ID = "place_icon_id"
        private const val GEOJSON_SOURCE_ID = "geojson-source-id"
        private const val UNCLUSTERED_POINT_LAYER_ID = "unclustered_point_layer_id"
        private const val EMAP98 = "EMAP98"
        private const val SELECTED_ADD_COEF_DP: Float = 8f

//        private const val SOURCE_ID = "source_id"
//        private const val LAYER_ID = "layer_id"
//        private const val LATITUDE = 25.0502
//        private const val LONGITUDE = 121.5380
    }

    private val coordinateSelectViewModel: CoordinateSelectViewModel by activityViewModels()

    private lateinit var pointCollection: FeatureCollection

    private var markerHeight = 0

    override fun onMapReady() {
        mapView.getMapboxMap().setCamera(
            CameraOptions.Builder()
                .zoom(14.0)
                .build()
        )

        val eMapSource = rasterSource(EMAP98) {
            tileSet(
                "tileset",
                listOf("https://wmts.nlsc.gov.tw/wmts/EMAP98/default/GoogleMapsCompatible/{z}/{y}/{x}.png")
            ) {
                tileSize(256)
            }
        }
        val eMapLayer = rasterLayer(EMAP98, EMAP98) {}


        mapView.getMapboxMap().addOnMapClickListener(this)
        mapView.getMapboxMap().loadStyle(style(styleUri = Style.MAPBOX_STREETS) {
            initLocationComponent()
            setupGesturesListener()
            transitionOptions {
                duration(0)
                delay(0)
            }
            +eMapSource
            +eMapLayer
            +image(PLACE_ICON_ID) {
                BitmapUtil.bitmapFromDrawableRes(
                    requireContext(),
                    R.drawable.ic_baseline_place_128_black
                )?.let {
                    val markerIcon = it.resize(128)
                    bitmap(markerIcon)
                    markerHeight = markerIcon.height
                }
                sdf(true)
            }
//            +geoJsonSource(SOURCE_ID) {
//                geometry(Point.fromLngLat(LONGITUDE, LATITUDE))
//            }
//            +symbolLayer(LAYER_ID, SOURCE_ID) {
//                iconImage(PLACE_ICON_ID)
//                iconAnchor(IconAnchor.BOTTOM)
//            }
        })
        setupLayer()
    }

    override fun onMapClick(point: Point): Boolean {
        val mapboxMap = mapView.getMapboxMap()
        mapboxMap.queryRenderedFeatures(
            RenderedQueryGeometry(mapboxMap.pixelForCoordinate(point)), RenderedQueryOptions(
                listOf(UNCLUSTERED_POINT_LAYER_ID), null
            )
        ) {
            onFeatureClicked(it) { feature ->
                addViewAnnotation(point, feature)
            }
            viewAnnotationManager.removeAllViewAnnotations()
        }
        return true
    }

    override fun setupLayer() {
        lifecycleScope.launch {
            val projects = coordinateSelectViewModel.selectedProjects.value
            if (projects != null) {
                val coordinates =
                    coordinateListViewModel.getCoordinatesByProjectIdsSync(projects.map { project -> project.id })
                val featureCollection = JSONObject()
                try {
                    featureCollection.put("type", "FeatureCollection")
                    val features = JSONArray()
                    for (coordinate in coordinates) {
                        val feature = JSONObject()
                        feature.put("type", "Feature")
                        feature.put("id", coordinate.id)
                        val properties = JSONObject()
                        properties.put("name", coordinate.name).put("N", coordinate.N)
                            .put("E", coordinate.E)
                        val geometry = JSONObject()
                        geometry.put("type", "Point")
                        val point = CoordinateUtil.twd97ToLatLng(coordinate.E, coordinate.N)
                        geometry.put(
                            "coordinates",
                            JSONArray().put(point.longitude()).put(point.latitude())
                        )
                        feature.put("properties", properties).put("geometry", geometry)
                        features.put(feature)
                    }
                    featureCollection.put("features", features)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                pointCollection = FeatureCollection.fromJson(featureCollection.toString())
                mapView.getMapboxMap().getStyle() { style ->
                    val pointSource = geoJsonSource(GEOJSON_SOURCE_ID) {
                        featureCollection(pointCollection)
                        cluster(true)
                        clusterMaxZoom(14)
                        clusterRadius(50)
                    }
                    style.addSource(pointSource)

                    val unClusteredLayer =
                        symbolLayer(UNCLUSTERED_POINT_LAYER_ID, GEOJSON_SOURCE_ID) {
                            iconAllowOverlap(true)
                            iconImage(PLACE_ICON_ID)
                            iconSize(1.0)
                            iconAnchor(IconAnchor.BOTTOM)
                            iconColor(rgb(232.0, 81.0, 72.0))
                        }
                    style.addLayer(unClusteredLayer)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun addViewAnnotation(point: Point, feature: Feature) {
        viewAnnotationManager.addViewAnnotation(
            resId = R.layout.item_callout_view,
            options = viewAnnotationOptions {
                geometry(point)
                associatedFeatureId(feature.id())
                anchor(ViewAnnotationAnchor.BOTTOM)
                allowOverlap(false)
            }, asyncInflater = asyncInflater
        ) { viewAnnotation ->
            viewAnnotation.visibility = View.VISIBLE
            viewAnnotationManager.updateViewAnnotation(
                viewAnnotation,
                viewAnnotationOptions {
                    offsetY(markerHeight / 2)
                }
            )
            viewAnnotation.findViewById<TextView>(R.id.textNativeView).text =
                "lat=%.2f\nlon=%.2f".format(point.latitude(), point.longitude())
            viewAnnotation.findViewById<ImageView>(R.id.closeNativeView).setOnClickListener { _ ->
                viewAnnotationManager.removeViewAnnotation(viewAnnotation)
            }

            viewAnnotation.findViewById<Button>(R.id.selectButton).setOnClickListener { b ->
                val button = b as Button
                val isSelected = button.text.toString().equals("SELECT", true)
                val pxDelta =
                    (if (isSelected) SELECTED_ADD_COEF_DP.dpToPx() else -SELECTED_ADD_COEF_DP.dpToPx()).toInt()
                button.text = if (isSelected) "DESELECT" else "SELECT"
                viewAnnotationManager.updateViewAnnotation(
                    viewAnnotation,
                    viewAnnotationOptions {
                        selected(isSelected)
                    }
                )
                (button.layoutParams as ViewGroup.MarginLayoutParams).apply {
                    bottomMargin += pxDelta
                    rightMargin += pxDelta
                    leftMargin += pxDelta
                }
                button.requestLayout()
            }
        }
    }
}