package com.magenta.rx.kotlin.utils

import android.util.Log
import com.magenta.rx.kotlin.record.RowResult
import java.util.*

fun sum(x: Double, eps: Double): Double {
    var sum: Double = x
    var n = 1
    var fn = Math.pow(x, 3.0) / 6.0
    while (eps < Math.abs(fn) && !(fn == Double.NEGATIVE_INFINITY || fn == Double.POSITIVE_INFINITY)) {
        sum += fn
        n += 1
        fn *= (2 * n - 1) * (2 * n - 1) * x * x
        fn /= 2 * n * (2 * n + 1)
    }
    return sum
}

fun singleCalc(x: Double, eps: Double): RowResult {
    val negative = x < 0
    val fx = sum(x, eps)
    Log.d("[SINGLE CALC]", "Calc with x = $x; fx = $fx!")
    return RowResult((if (negative) -1 else 1) * x, (if (negative) -1 else 1) * fx, (if (negative) -1 else 1) * Math.asin(fx), Math.abs(Math.sin(x) - fx))
}

fun calc(xn: Double, xk: Double, dx: Double, eps: Double): List<RowResult> {
    var x = xn
    val result = LinkedList<RowResult>()
    Log.d("[CALC]", "Start with: xn = $xn; xk = $xk; dx = $dx; eps = $eps!")
    while (x <= xk) {
        result.add(singleCalc(x, eps))
        x += dx
        Log.d("[CALC]", "x = $x!")
    }
    Log.d("[CALC]", "Stop!")
    return result
}