package com.android.sdk_integration_guide

import com.sign3.public_classes.common.AllSimInfo
import com.sign3.public_classes.common.AppAnalytics
import com.sign3.public_classes.common.AppliedRules
import com.sign3.public_classes.common.DeviceMeta
import com.sign3.public_classes.common.GPSLocationData
import com.sign3.public_classes.common.IPDetails
import com.sign3.public_classes.common.IntelligenceError
import com.sign3.public_classes.common.IntelligenceResponse
import com.sign3.public_classes.common.RuleAction
import com.sign3.public_classes.common.SimDetails
import org.json.JSONArray
import org.json.JSONObject

/** Renders IntelligenceResponse / IntelligenceError as pretty-printed JSON covering every public field. */
object IntelligenceFormatter {

    private const val INDENT = 2

    fun format(r: IntelligenceResponse): String = toJson(r).toString(INDENT)

    fun format(e: IntelligenceError): String = JSONObject().apply {
        put("error", true)
        put("requestId", e.requestId.orNull())
        put("errorMessage", e.errorMessage.orNull())
    }.toString(INDENT)

    /** Pretty-prints an arbitrary key/value map as JSON (used by the Update Options screen). */
    fun format(map: Map<String, Any?>): String = JSONObject().apply {
        map.forEach { (k, v) -> put(k, v.toJsonValue()) }
    }.toString(INDENT)

    fun toJson(r: IntelligenceResponse): JSONObject = JSONObject().apply {
        put("requestId", r.requestId.orNull())
        put("sessionId", r.sessionId.orNull())
        put("deviceId", r.deviceId.orNull())
        put("newDevice", r.newDevice.orNull())
        put("clientUserIds", r.clientUserIds.toJsonValue())

        put("sessionRiskScore", r.sessionRiskScore.orNull())
        put("deviceRiskScore", r.deviceRiskScore.orNull())
        put("riskScores", r.riskScores.toJsonValue())
        put("riskReasonCodes", r.riskReasonCodes.toJsonValue())
        put("ruleAction", r.ruleAction.toJson())
        put("appliedRules", r.appliedRules.toJson())
        put("behaviourInsights", r.behaviourInsights.toJsonValue())

        put("rooted", r.rooted.orNull())
        put("emulator", r.emulator.orNull())
        put("cloned", r.cloned.orNull())
        put("hooking", r.hooking.orNull())
        put("appTampering", r.appTampering.orNull())
        put("genuineInstall", r.genuineInstall.orNull())
        put("factoryReset", r.factoryReset.orNull())
        put("factoryResetTime", r.factoryResetTime.orNull())
        put("developerOptionsEnabled", r.developerOptionsEnabled.orNull())
        put("usbDebugging", r.usbDebugging.orNull())
        put("wirelessDebugging", r.wirelessDebugging.orNull())
        put("harmfulAppDetected", r.harmfulAppDetected.orNull())
        put("keyloggerDetected", r.keyloggerDetected.orNull())
        put("blacklistedDevice", r.blacklistedDevice.orNull())
        put("remoteAppProviders", r.remoteAppProviders.orNull())
        put("remoteAppProvidersCount", r.remoteAppProvidersCount.orNull())
        put("mirroredScreen", r.mirroredScreen.orNull())

        put("vpn", r.vpn.orNull())
        put("proxy", r.proxy.orNull())
        put("unsecuredWifi", r.unsecuredWifi.orNull())
        put("geoSpoofed", r.geoSpoofed.orNull())
        put("ip", r.ip.orNull())
        put("ipDetails", r.ipDetails.toJson())
        put("gpsLocation", r.gpsLocation.toJson())

        put("simInfo", r.simInfo.toJson())
        put("deviceMeta", r.deviceMeta.toJson())
        put("appAnalytics", r.appAnalytics.toJson())
        put("additionalData", r.additionalData.toJsonValue())
    }

    private fun RuleAction?.toJson(): Any = this?.let {
        JSONObject().apply {
            put("action", it.action.orNull())
            put("name", it.name.orNull())
            put("description", it.description.orNull())
            put("message", it.message.orNull())
        }
    } ?: JSONObject.NULL

    private fun AppliedRules?.toJson(): Any = this?.let {
        JSONObject().apply {
            put("totalScore", it.totalScore)
            put("rules", it.rules.toJsonValue())
        }
    } ?: JSONObject.NULL

    private fun IPDetails?.toJson(): Any = this?.let {
        JSONObject().apply {
            put("asn", it.asn.orNull())
            put("isp", it.isp.orNull())
            put("city", it.city.orNull())
            put("region", it.region.orNull())
            put("country", it.country.orNull())
            put("latitude", it.latitude.orNull())
            put("longitude", it.longitude.orNull())
            put("fraudScore", it.fraudScore.orNull())
        }
    } ?: JSONObject.NULL

    private fun GPSLocationData?.toJson(): Any = this?.let {
        JSONObject().apply {
            put("latitude", it.latitude.orNull())
            put("longitude", it.longitude.orNull())
            put("countryName", it.countryName.orNull())
            put("countryCode", it.countryCode.orNull())
            put("adminArea", it.adminArea.orNull())
            put("subAdminArea", it.subAdminArea.orNull())
            put("locality", it.locality.orNull())
            put("subLocality", it.subLocality.orNull())
            put("featureName", it.featureName.orNull())
            put("postalCode", it.postalCode.orNull())
            put("address", it.address.orNull())
        }
    } ?: JSONObject.NULL

    private fun AllSimInfo?.toJson(): Any = this?.let {
        JSONObject().apply {
            put("totalSimUsed", it.totalSimUsed)
            put("simIds", JSONArray().apply { it.simIds?.forEach { sim -> put(sim.toJson()) } })
        }
    } ?: JSONObject.NULL

    private fun SimDetails?.toJson(): Any = this?.let {
        JSONObject().apply {
            put("id", it.id.orNull())
            put("simSlotIndex", it.simSlotIndex.orNull())
            put("carrierName", it.carrierName.orNull())
            put("eSim", it.eSim.orNull())
        }
    } ?: JSONObject.NULL

    private fun DeviceMeta?.toJson(): Any = this?.let {
        JSONObject().apply {
            put("brand", it.brand.orNull())
            put("model", it.model.orNull())
            put("product", it.product.orNull())
            put("androidVersion", it.androidVersion.orNull())
            put("cpuType", it.cpuType.orNull())
            put("totalRAM", it.totalRAM.orNull())
            put("storageTotal", it.storageTotal.orNull())
            put("storageAvailable", it.storageAvailable.orNull())
            put("screenResolution", it.screenResolution.orNull())
        }
    } ?: JSONObject.NULL

    private fun AppAnalytics?.toJson(): Any = this?.let {
        JSONObject().apply { put("affinity", it.affinity.toJsonValue()) }
    } ?: JSONObject.NULL

    /** Converts Kotlin values (maps, lists, primitives, null) into org.json-compatible values. */
    private fun Any?.toJsonValue(): Any = when (this) {
        null -> JSONObject.NULL
        is Map<*, *> -> JSONObject().also { obj ->
            forEach { (k, v) -> obj.put(k.toString(), v.toJsonValue()) }
        }
        is Collection<*> -> JSONArray().also { arr -> forEach { arr.put(it.toJsonValue()) } }
        is Array<*> -> JSONArray().also { arr -> forEach { arr.put(it.toJsonValue()) } }
        is Boolean, is Number, is String, is JSONObject, is JSONArray -> this
        else -> toString()
    }

    private fun Any?.orNull(): Any = this ?: JSONObject.NULL
}
