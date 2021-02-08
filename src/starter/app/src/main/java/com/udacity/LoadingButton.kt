package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var loadedPercentage = 0f

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Loading -> {
                loadAnimation()
                previousText = buttonText
                buttonText = resources.getString(R.string.download_button_process_text)
            }
            ButtonState.Clicked -> {
                isClickable = false
                buttonState = ButtonState.Loading
            }
            ButtonState.Completed -> {
                isClickable = true
                buttonText = previousText
            }
        }

        invalidate()
    }

    private var buttonBackgroundColor = 0
    private var buttonFontColor = 0
    private var buttonTextSize = 0
    private var buttonText = ""
    private var previousText = ""

    init {
        isClickable = true

        //Custom attributes. They defined on content_main.xml and attrs.xml files.
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            buttonBackgroundColor = getColor(R.styleable.LoadingButton_backgroundColor, 0)
            buttonFontColor = getColor(R.styleable.LoadingButton_buttonFontColor, 0)
            buttonTextSize = getInt(R.styleable.LoadingButton_buttonFontSize, 0)
            buttonText = getString(R.styleable.LoadingButton_buttonText).toString()
        }
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = buttonTextSize.toFloat()
        color = buttonBackgroundColor
    }

    override fun performClick(): Boolean {
        buttonState = ButtonState.Clicked
        if (super.performClick()) return true

        invalidate()
        return true
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawStaticButtonShape(canvas)

        if(buttonState == ButtonState.Loading) {
            drawAnimatedButtonShape(canvas)
        }

        writeButtonText(canvas)
    }

    private fun drawStaticButtonShape(canvas: Canvas?) {
        paint.color = buttonBackgroundColor
        canvas!!.drawRect(
            0.toFloat(),
            0.toFloat(),
            widthSize.toFloat(),
            heightSize.toFloat(),
            paint
        )
    }

    private fun drawAnimatedButtonShape(canvas: Canvas?) {
        paint.color = resources.getColor(R.color.colorPrimaryDark)
        canvas!!.drawRect(
                0.toFloat(),
                0.toFloat(),
                0 + loadedPercentage,
                heightSize.toFloat(),
                paint
        )
    }

    private fun writeButtonText(canvas: Canvas?) {
        paint.color = buttonFontColor
        canvas!!.drawText(
            buttonText,
            (widthSize / 2).toFloat(),
            ((heightSize - paint.ascent()) / 2).toFloat(),
            paint
        )
    }

    private fun loadAnimation() {
        val animator = ValueAnimator.ofInt(0, 100).apply {
            duration = 2000
            interpolator = LinearInterpolator()
            addUpdateListener { valueAnimator ->
                loadedPercentage = ((valueAnimator.animatedValue as Int).toFloat() / 100) * widthSize

                if(valueAnimator.animatedValue == 100) {
                    buttonState = ButtonState.Completed
                }

                invalidate()
            }
        }

        animator?.start()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }
}