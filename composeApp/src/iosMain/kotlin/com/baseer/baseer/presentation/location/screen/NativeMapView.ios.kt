package com.baseer.baseer.presentation.location.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import com.baseer.baseer.domain.model.LocationData
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCAction
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGRectMake
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.MapKit.MKCoordinateRegionMakeWithDistance
import platform.MapKit.MKMapView
import platform.MapKit.MKPointAnnotation
import platform.MapKit.MKUserTrackingButton
import platform.UIKit.*
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
@Composable
actual fun NativeMapView(
    modifier: Modifier,
    selectedLocation: LocationData?,
    onMapClick: (latitude: Double, longitude: Double) -> Unit
) {
    val mapState = remember { MapState() }

    UIKitView(
        modifier = modifier.fillMaxSize(),
        factory = {
            val mapView = MKMapView()
            mapState.mapView = mapView
            mapState.annotation = MKPointAnnotation()

            mapView.showsUserLocation = true

            val handler = GestureHandler(mapView, onMapClick)
            mapState.gestureHandler = handler

            val tapRecognizer = UITapGestureRecognizer(
                target = handler,
                action = platform.objc.sel_registerName("handleTap:")
            )
            mapView.addGestureRecognizer(tapRecognizer)

            val longPressRecognizer = UILongPressGestureRecognizer(
                target = handler,
                action = platform.objc.sel_registerName("handleLongPress:")
            )
            longPressRecognizer.minimumPressDuration = 0.5
            mapView.addGestureRecognizer(longPressRecognizer)

            selectedLocation?.let { location ->
                mapState.updateLocation(location, animate = false)
            }

            mapView
        },
        update = { _ ->
            selectedLocation?.let { location ->
                mapState.updateLocation(location, animate = true)
            }
        }
    )
}

@OptIn(ExperimentalForeignApi::class)
private class MapState {
    var mapView: MKMapView? = null
    var annotation: MKPointAnnotation? = null

    var gestureHandler: GestureHandler? = null
    private var lastLatitude: Double? = null
    private var lastLongitude: Double? = null

    fun updateLocation(location: LocationData, animate: Boolean) {
        val map = mapView ?: return
        val ann = annotation ?: return

        if (lastLatitude == location.latitude && lastLongitude == location.longitude) {
            return
        }

        lastLatitude = location.latitude
        lastLongitude = location.longitude

        val coordinate = CLLocationCoordinate2DMake(location.latitude, location.longitude)

        val region = MKCoordinateRegionMakeWithDistance(coordinate, 1000.0, 1000.0)
        map.setRegion(region, animated = animate)

        map.removeAnnotation(ann)
        ann.setCoordinate(coordinate)
        map.addAnnotation(ann)
    }
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
private class GestureHandler(
    private val mapView: MKMapView,
    private val onLocationSelected: (latitude: Double, longitude: Double) -> Unit
) : NSObject() {

    @ObjCAction
    fun handleTap(recognizer: UITapGestureRecognizer) {
        if (recognizer.state == UIGestureRecognizerStateEnded) {
            selectLocation(recognizer)
        }
    }

    @ObjCAction
    fun handleLongPress(recognizer: UILongPressGestureRecognizer) {
        if (recognizer.state == UIGestureRecognizerStateBegan) {
            selectLocation(recognizer)
        }
    }

    private fun selectLocation(recognizer: platform.UIKit.UIGestureRecognizer) {
        val point = recognizer.locationInView(mapView)
        val coordinate = mapView.convertPoint(point, toCoordinateFromView = mapView)
        coordinate.useContents {
            onLocationSelected(latitude, longitude)
        }
    }
}