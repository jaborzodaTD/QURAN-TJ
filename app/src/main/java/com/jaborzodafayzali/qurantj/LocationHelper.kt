package com.jaborzodafayzali.qurantj
import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
object LocationHelper{
 @SuppressLint("MissingPermission")
 suspend fun current(c:Context)=suspendCancellableCoroutine<android.location.Location?>{x->
  LocationServices.getFusedLocationProviderClient(c).getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY,null)
   .addOnSuccessListener{if(x.isActive)x.resume(it)}.addOnFailureListener{if(x.isActive)x.resume(null)}
 }
}