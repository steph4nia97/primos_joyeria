package com.primosjoyeria.util

import java.text.NumberFormat
import java.util.Locale

 fun clp(n: Int): String {
    val formato = NumberFormat.getNumberInstance(Locale("es", "CL"))
    return "$${formato.format(n)}"
}
