package com.merpyzf.itemdecorationdemo

import android.content.Context
import android.graphics.*
import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.max


/**
 * 时间线
 *
 * Date: 6/16/21
 * @author wangke
 *
 */
class DateLineItemDecoration(
    context: Context,
    builder: Builder
) : RecyclerView.ItemDecoration() {
    // 允许自定义的属性
    private var timeLineBgColor = Color.WHITE
    private var textCircleBgColor = Color.parseColor("#6C8EFE")
    private var textColor = Color.WHITE
    private var textSize = sp2Px(12f, context).toFloat()
    private var textPadding = dp2Px(6f, context).toFloat()
    private var circleMargin = dp2Px(3f, context).toFloat()
    private var timeLineHorizontalMargin = dp2Px(12f, context).toFloat()
    private var callback: ((position: Int) -> Section)? = null

    // 通过计算动态得出具体值
    private var textWidth = 0f
    private var timeLineWidth = 0f
    private var leftOffset = 0f
    private var sectionVerticalOffset = 0
    private var cornerRadius = 0f
    private var circleRadius = 0f

    private val paintRect = Paint()
        .apply {
            isAntiAlias = true
            style = Paint.Style.FILL
        }
    private val paintBgCircle = Paint()
        .apply {
            isAntiAlias = true
            style = Paint.Style.FILL
        }
    private val paintRoundCorner = Paint()
        .apply {
            isAntiAlias = true
            style = Paint.Style.FILL
        }
    private val paintText = Paint()
        .apply {
            isAntiAlias = true
        }

    init {
        // 获取自定义参数
        builder._timeLineBgColor?.let {
            this.timeLineBgColor = it
        }
        builder._textCircleBgColor?.let {
            this.textCircleBgColor = it
        }
        builder._textColor?.let {
            this.textColor = it
        }
        builder._textSize?.let {
            this.textSize = it
        }
        builder._textPadding?.let {
            this.textPadding = it
        }
        builder._circleMargin?.let {
            this.circleMargin = it
        }
        builder._timeLineHorizontalMargin?.let {
            this.timeLineHorizontalMargin = it
        }
        builder._callback?.let {
            this.callback = it
        }
        paintRect.color = timeLineBgColor
        paintRoundCorner.color = timeLineBgColor
        paintBgCircle.color = textCircleBgColor
        paintText.color = textColor
        paintText.textSize = textSize

        textWidth = measureTextWidth(paintText)
        // 确定时间线宽度
        timeLineWidth = (textWidth + textPadding * 2 + circleMargin * 2)
        // 确定 ItemView 距离父View左侧的距离
        leftOffset = timeLineWidth + timeLineHorizontalMargin * 2
        // 圆角半径
        cornerRadius = timeLineWidth / 2
        // 文本背景圆的半径
        circleRadius = ((textWidth + textPadding * 2) / 2.0f)
        // 分组间垂直间距
        sectionVerticalOffset = (timeLineWidth * 1f).toInt()
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        for (i in 0 until parent.childCount) {
            val childAdapterPosition = parent.getChildAdapterPosition(parent[i])
            val itemView = parent.getChildAt(i)
            val section = callback?.invoke(childAdapterPosition)!!

            var top = itemView.top
            val left = itemView.left / 2f - timeLineWidth / 2
            val right = itemView.left / 2f + timeLineWidth / 2f
            val bottom = itemView.bottom

            if (i == 0) {
                if (section.isFirst && section.isEnd) {
                    c.drawRect(RectF(left, top.toFloat(), right, bottom.toFloat()), paintRect)
                } else {
                    if (section.isEnd) {
                        top = itemView.top + sectionVerticalOffset
                        c.drawRect(RectF(left, top.toFloat(), right, bottom.toFloat()), paintRect)
                    } else {
                        // 屏幕上第一个item，但不是每组最后一个
                        top = sectionVerticalOffset
                        // 避免时间轴绘制超出 sectionVerticalOffset 的情况发生
                        if (bottom > sectionVerticalOffset) {
                            c.drawRect(
                                RectF(left, top.toFloat(), right, bottom.toFloat()),
                                paintRect
                            )
                        }
                    }
                }
            } else {
                if (section.isFirst) {
                    c.drawRect(RectF(left, top.toFloat(), right, bottom.toFloat()), paintRect)
                } else {
                    // 避免时间轴绘制超出 sectionVerticalOffset 的情况发生
                    if (top <= sectionVerticalOffset) {
                        top = sectionVerticalOffset
                    }
                    c.drawRect(RectF(left, top.toFloat(), right, bottom.toFloat()), paintRect)
                }
            }
        }
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        for (i in 0 until parent.childCount) {
            val itemView = parent.getChildAt(i)
            val section = callback?.invoke(parent.getChildAdapterPosition(itemView))!!
            if (i == 0) {
                if (section.isFirst && section.isEnd) {
                    drawTopRoundCorner(c, itemView, itemView.top.toFloat())
                    drawDateTextBgCircle(c, itemView, itemView.top.toFloat())
                    drawBottomRoundCorner(c, itemView)
                    drawDateText(c, itemView.top.toFloat(), section.date)
                } else {
                    if (section.isFirst) {
                        drawTopRoundCorner(c, itemView, sectionVerticalOffset.toFloat())
                        drawDateTextBgCircle(c, itemView, sectionVerticalOffset.toFloat())
                        drawDateText(c, sectionVerticalOffset.toFloat(), section.date)
                    } else if (section.isEnd) {
                        drawTopRoundCorner(
                            c,
                            itemView,
                            (itemView.top + sectionVerticalOffset).toFloat()
                        )
                        drawDateTextBgCircle(
                            c,
                            itemView,
                            (itemView.top + sectionVerticalOffset).toFloat()
                        )
                        drawBottomRoundCorner(c, itemView)
                        drawDateText(
                            c,
                            itemView.top + sectionVerticalOffset.toFloat(),
                            section.date
                        )
                    } else {
                        drawTopRoundCorner(c, itemView, sectionVerticalOffset.toFloat())
                        // 屏幕上第一个 & 不是每组第一 & 不是每组最后一个
                        drawDateTextBgCircle(c, itemView, sectionVerticalOffset.toFloat())
                        drawDateText(c, sectionVerticalOffset.toFloat(), section.date)
                    }
                }
            } else {
                if (section.isFirst) {
                    drawTopRoundCorner(c, itemView, itemView.top.toFloat())
                    drawDateTextBgCircle(c, itemView, itemView.top.toFloat())
                    drawDateText(c, itemView.top.toFloat(), section.date)
                }
                if (section.isEnd) {
                    drawBottomRoundCorner(c, itemView)
                }
            }
        }
    }

    private fun measureTextWidth(paint: Paint): Float {
        val rect = Rect()
        paint.getTextBounds("00", 0, 2, rect)
        return max(rect.width().toFloat(), rect.height().toFloat())
    }

    // region draw helper
    private fun drawDateTextBgCircle(canvas: Canvas, itemView: View, y: Float) {
        canvas.drawCircle(
            itemView.left / 2.0f,
            y,
            circleRadius, paintBgCircle
        )
    }

    private fun drawTopRoundCorner(canvas: Canvas, itemView: View, y: Float) {
        canvas.drawArc(
            RectF(
                timeLineHorizontalMargin,
                y + cornerRadius,
                itemView.left - timeLineHorizontalMargin,
                y - cornerRadius
            ), 180f, 360f, false, paintRoundCorner
        )
    }

    private fun drawBottomRoundCorner(canvas: Canvas, itemView: View) {
        canvas.drawArc(
            RectF(
                timeLineHorizontalMargin,
                itemView.bottom.toFloat() - cornerRadius,
                itemView.left - timeLineHorizontalMargin,
                itemView.bottom + cornerRadius
            ),
            0f,
            180f,
            false,
            paintRoundCorner
        )
    }


    private fun drawDateText(canvas: Canvas, y: Float, day: String) {
        paintText.measureText(day)
        val fontMetrics = paintText.fontMetrics
        val dy =
            (fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.descent;
        canvas.drawText(
            day,
            timeLineHorizontalMargin + circleMargin / 2.0f + textPadding,
            y + dy,
            paintText
        )
    }
    // endregion

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val childAdapterPosition = parent.getChildAdapterPosition(view)
        outRect.left = leftOffset.toInt()
        val section = callback?.invoke(childAdapterPosition)!!
        if (section.isFirst) {
            outRect.top = sectionVerticalOffset
        }
        if (section.isEnd) {
            outRect.bottom = cornerRadius.toInt()
        }
        parent.adapter?.let {
            if (childAdapterPosition == it.itemCount - 1) {
                outRect.bottom = sectionVerticalOffset
            }
        }
    }

    class Builder {
        var _timeLineBgColor: Int? = null
        var _textCircleBgColor: Int? = null
        var _textColor: Int? = null
        var _textSize: Float? = null
        var _textPadding: Float? = null
        var _circleMargin: Float? = null
        var _timeLineHorizontalMargin: Float? = null
        var _callback: ((position: Int) -> Section)? = null

        fun timeLineBgColor(@ColorInt color: Int): Builder {
            this._timeLineBgColor = color
            return this
        }

        fun textCircleBgColor(@ColorInt color: Int): Builder {
            this._textCircleBgColor = color
            return this
        }

        fun textColor(@ColorInt color: Int): Builder {
            this._textColor = color
            return this
        }

        fun textSize(size: Float): Builder {
            this._textSize = size
            return this
        }

        fun textPadding(padding: Float): Builder {
            this._textPadding = padding
            return this
        }

        fun circleMargin(margin: Float): Builder {
            this._circleMargin = margin
            return this
        }

        fun timeLineHorizontalMargin(margin: Float): Builder {
            this._timeLineHorizontalMargin = margin
            return this
        }

        fun getSectionInfo(callback: ((position: Int) -> Section)): Builder {
            this._callback = callback
            return this
        }

        fun build(context: Context): DateLineItemDecoration {
            return DateLineItemDecoration(context, this)
        }
    }

    /**
     * Item 的分组信息
     */
    class Section {
        var isFirst = false
        var isEnd = false
        var date = "00"
    }

    companion object {
        private fun dp2Px(dp: Float, context: Context): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.resources.displayMetrics
            )
                .toInt()
        }

        private fun sp2Px(sp: Float, context: Context): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                sp,
                context.resources.displayMetrics
            )
                .toInt()
        }
    }
}