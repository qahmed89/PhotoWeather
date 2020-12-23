package com.example.photoweather.other

import android.content.Context
import android.graphics.*

object WriteImage {

    fun writeOnImage(
        context: Context,
        bitmap: Bitmap,
        place: String,
        temp: String,
        condition: String
    ): Bitmap {

        val displayMetrics = context.resources.displayMetrics
        val b = Bitmap.createScaledBitmap(
            bitmap,
            displayMetrics.widthPixels / 2,
            displayMetrics.widthPixels / 2,
            true
        )
        val mutableBitmap: Bitmap = b.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutableBitmap)

        val paint = Paint()
        paint.color = Color.WHITE
        paint.textSize = 18f
        paint.isFakeBoldText = true
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)

        val transPaint = Paint()
        transPaint.color = Color.BLACK
        transPaint.alpha = 0x55

        canvas.drawBitmap(mutableBitmap, 0f, 0f, paint)
        canvas.drawRect(
            0F,
            0F,
            mutableBitmap.width.toFloat(),
            mutableBitmap.width.toFloat(),
            transPaint
        )
        canvas.drawText(
            place,
            (mutableBitmap.width / 2 - 50).toFloat(),
            (mutableBitmap.height - 50).toFloat(),
            paint
        )
        canvas.drawText(
            temp,
            (mutableBitmap.width / 2 - 50).toFloat(),
            (mutableBitmap.height - 100).toFloat(),
            paint
        )
        canvas.drawText(
            condition,
            (mutableBitmap.width / 2 - 50).toFloat(),
            (mutableBitmap.height - 150).toFloat(),
            paint
        )
        return mutableBitmap
    }

}