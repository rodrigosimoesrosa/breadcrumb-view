package br.com.mirabilis.breadcrumb.implementation

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import br.com.mirabilis.breadcrumb.R
import br.com.mirabilis.breadcrumb.behavior.Breadcrumb
import br.com.mirabilis.breadcrumb.behavior.Check
import kotlinx.android.synthetic.main.breadcrumb_check.view.*

/**
 * Created by rodrigosimoesrosa on 02/04/19.
 * Copyright ©Mirabilis 2019. All rights reserved.
 */
class BreadcrumbCheckView : ConstraintLayout, Check.View {

    private var state: Boolean
    private var order: Int

    private var settings: Breadcrumb.Settings? = null

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null,
                defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {
        val attributes = context.theme.obtainStyledAttributes(
                attrs, R.styleable.BreadcrumbCheckView, 0, 0)

        val image = attributes.getDrawable(R.styleable.BreadcrumbCheckView_breadcrumbCheckImage)
        setImage(image)

        state = attributes.getBoolean(R.styleable.BreadcrumbCheckView_breadcrumbCheckState,
                Check.BREADCRUMB_CHECK_DEFAULT)

        order = attributes.getInt(R.styleable.BreadcrumbCheckView_breadcrumbCheckOrder, 0)

        applyState()
    }

    init {
        LayoutInflater.from(context)
                .inflate(R.layout.breadcrumb_check, this, true)
    }

    override fun getOrder(): Int {
        return order
    }

    override fun getState(): Boolean {
        return state
    }

    override fun changeState(state: Boolean) {
        this.state = state
        applyState()
    }

    private fun setImage(image: Drawable?) {
        imageCheck.setImageDrawable(image)
    }

    private fun applyState() {
        val isAnimated = settings?.isChecksAnimated() ?: false
        if (state) {
            if (isAnimated) {
                enableWithAnimation()
                return
            }
            enable()
            return
        }

        if (isAnimated) {
            disableWithAnimation()
            return
        }
        disable()
    }

    private fun disableWithAnimation() {
        val interpolator = settings?.getChecksInterpolator()
        val duration = settings?.getChecksAnimationDuration() ?: 0

        val animation = ScaleAnimation(
            Check.SCALE_DEFAULT, Check.SCALE_DISABLE_DEFAULT,
                Check.SCALE_DEFAULT, Check.SCALE_DISABLE_DEFAULT,
                Animation.RELATIVE_TO_SELF, Check.PIVOT_DEFAULT,
                Animation.RELATIVE_TO_SELF, Check.PIVOT_DEFAULT)

        animation.fillAfter = true
        animation.duration = duration

        if (interpolator != null) {
            animation.interpolator = interpolator
        }

        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                imageCheck.visibility = View.GONE
            }

            override fun onAnimationStart(animation: Animation?) {}
        })

        imageCheck.startAnimation(animation)
    }

    private fun enableWithAnimation() {
        val interpolator = settings?.getChecksInterpolator()
        val duration = settings?.getChecksAnimationDuration() ?: 0

        imageCheck.visibility = View.VISIBLE

        val animation = ScaleAnimation(
            Check.SCALE_DISABLE_DEFAULT, Check.SCALE_DEFAULT,
                Check.SCALE_DISABLE_DEFAULT, Check.SCALE_DEFAULT,
                Animation.RELATIVE_TO_SELF, Check.PIVOT_DEFAULT,
                Animation.RELATIVE_TO_SELF, Check.PIVOT_DEFAULT)

        animation.fillAfter = true
        animation.duration = duration

        if (interpolator != null) {
            animation.interpolator = interpolator
        }

        imageCheck.startAnimation(animation)
    }

    private fun disable() {
        imageCheck.visibility = View.GONE
    }

    private fun enable() {
        imageCheck.visibility = View.VISIBLE
    }

    override fun setBreadcrumbSettings(settings: Breadcrumb.Settings) {
        this.settings = settings
    }
}
