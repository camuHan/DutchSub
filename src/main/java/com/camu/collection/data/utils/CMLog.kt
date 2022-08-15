package com.camu.collection.data.utils

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Debug
import android.os.SystemClock
import android.text.TextUtils
import android.util.Log

object CMLog {
    const val TAG = "[UI]"
    const val LOGCAT_IN = "[In]"
    const val LOGCAT_OUT = "[Out]"
    const val LOGCAT_RESULT = "Result:"
    private const val bLogDisplay = true
    @JvmStatic
    fun trace(ste: Array<StackTraceElement>?) {
        if (!bLogDisplay) {
            return
        }
        if (ste == null) return
        Log.e(
            TAG,
            "[Line:" + ste[0].lineNumber + "] [" + ste[0].className + "] " + ste[0].methodName
        )
    }

    @JvmStatic
    fun w(tag: String, msg: String) {
        var msg = msg
        if (!bLogDisplay) {
            return
        }
        msg = if (TextUtils.isEmpty(msg)) {
            return
        } else {
            "[$tag]$msg"
        }
        Log.w(TAG, msg)
    }

    @JvmStatic
    fun d(tag: String, msg: String) {
        var msg = msg
        if (!bLogDisplay) {
            return
        }
        msg = if (TextUtils.isEmpty(msg)) {
            return
        } else {
            "[$tag]$msg"
        }
        Log.d(TAG, msg)
    }

    @JvmStatic
    fun e(tag: String, msg: String) {
        var msg = msg
        if (!bLogDisplay) {
            return
        }
        msg = if (TextUtils.isEmpty(msg)) {
            return
        } else {
            "[$tag]$msg"
        }
        Log.e(TAG, msg)
    }

    @JvmStatic
    fun e(tag: String, msg: String, thr: Throwable?) {
        var msg = msg
        if (!bLogDisplay) {
            return
        }
        msg = if (TextUtils.isEmpty(msg)) {
            return
        } else {
            "[$tag]$msg"
        }
        Log.e(TAG, msg)
    }

    @JvmStatic
    fun v(tag: String, msg: String) {
        var msg = msg
        if (!bLogDisplay) {
            return
        }
        msg = if (TextUtils.isEmpty(msg)) {
            return
        } else {
            "[$tag]$msg"
        }
        Log.v(TAG, msg)
    }

    @JvmStatic
    fun i(tag: String, msg: String) {
        var msg = msg
        if (!bLogDisplay) {
            return
        }
        msg = if (TextUtils.isEmpty(msg)) {
            return
        } else {
            "[$tag]$msg"
        }
        Log.i(TAG, msg)
    }

    @JvmStatic
    fun startTimeTrace(tag: String): Long {
        if (!bLogDisplay) {
            return 0
        }
        if (TextUtils.isEmpty(tag)) {
            return 0
        }
        Log.d(TAG, "[$tag] timeTrace start.")
        return SystemClock.uptimeMillis()
    }

    @JvmStatic
    fun endTimeTrace(tag: String, startTime: Long) {
        if (!bLogDisplay) {
            return
        }
        if (TextUtils.isEmpty(tag)) {
            return
        }
        Log.d(
            TAG,
            "[" + tag + "] timeTrace end = [" + (SystemClock.uptimeMillis() - startTime) + " ms]"
        )
    }

    @JvmStatic
    fun showDevice() {
        try {
            e(TAG, "==============================================")
            e(TAG, "BOARD : " + Build.BOARD)
            e(TAG, "BOOTLOADER : " + Build.BOOTLOADER)
            e(TAG, "BRAND : " + Build.BRAND)
            e(TAG, "CPU_ABI : " + Build.CPU_ABI)
            e(TAG, "CPU_ABI2 : " + Build.CPU_ABI2)
            e(TAG, "DEVICE : " + Build.DEVICE)
            e(TAG, "DISPLAY : " + Build.DISPLAY)
            e(TAG, "FINGERPRINT : " + Build.FINGERPRINT)
            e(TAG, "HARDWARE : " + Build.HARDWARE)
            e(TAG, "MANUFACTURER : " + Build.MANUFACTURER)
            e(TAG, "MODEL : " + Build.MODEL)
            e(TAG, "PRODUCT : " + Build.PRODUCT)
            e(TAG, "TAGS : " + Build.TAGS)
            e(TAG, "TYPE : " + Build.TYPE)
            e(TAG, "USER : " + Build.USER)
            e(TAG, "SDK_INT : " + Build.VERSION.SDK_INT)
            e(TAG, "==============================================")
        } catch (e: Exception) {
            trace(e.stackTrace)
        }
    }

    @JvmStatic
    fun showMemory(context: Context?, tag: String, caller: String) {
        if (context == null) {
            return
        }
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val mi = ActivityManager.MemoryInfo()
        am.getMemoryInfo(mi)
        e(
            tag,
            caller + " availMem : " + mi.availMem / 1024 / 1024 + " MB, " + mi.availMem + " Byte"
        )
    }

    //For Guide Document - Required Heap Memory
    //Use at DocumentFragment onOpenComplete() with Blank Document
    //Check [Native] heapSize
    fun debugMemory(context: Context) {
        val runtime = Runtime.getRuntime()
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val mi = ActivityManager.MemoryInfo()
        am.getMemoryInfo(mi)
        val maxHeapSize = am.memoryClass // Max Heap Size Per Application (Dalvik) MB
        val largeHeapSize =
            am.largeMemoryClass // Max Heap Size Per Application set largeHeap (Dalvik) MB
        val kernelHeapSize = mi.availMem // Max Heap Size in Kernel
        val builder = StringBuilder()
        builder.append(
            """
    
    [Max (MB)] heapSize : $maxHeapSize, largeHeapSize : $largeHeapSize, kernelHeapSize : ${kernelHeapSize / 1024 / 1024}
    """.trimIndent()
        )
        builder.append(
            """
    
    [Native] heapSize : ${getSize(Debug.getNativeHeapSize())}, allocated : ${getSize(Debug.getNativeHeapAllocatedSize())}, free : ${
                getSize(
                    Debug.getNativeHeapFreeSize()
                )
            }
    """.trimIndent()
        )
        builder.append(
            """
    
    [Runtime] max : ${getSize(runtime.maxMemory())}, total : ${getSize(runtime.totalMemory())}, alloc : ${
                getSize(
                    runtime.totalMemory() - runtime.freeMemory()
                )
            }, free : ${getSize(runtime.freeMemory())}
    """.trimIndent()
        )
        builder.append(
            """
    
    [Kernel] device total : ${getSize(mi.totalMem)}, available : ${getSize(mi.availMem)}, lowMemory : ${mi.lowMemory}, threshold : ${mi.threshold}
    """.trimIndent()
        )
        d("TEST", "" + builder)
    }

    private fun getSize(size: Long): String {
        var size = size
        var Size: String? = null
        val origin = size
        var cnt = 0
        var quota: Long = 0
        while (size / 1024.also { quota = it.toLong() } > 0) {
            cnt++
            size = if (quota > 1024) quota else break
        }
        Size = when (cnt) {
            0 -> {
                "B"
            }
            1 -> {
                "KB"
            }
            2 -> {
                "MB"
            }
            3 -> {
                "GB"
            }
            else -> {
                "None"
            }
        }
        return "$origin($quota $Size)"
    }
}