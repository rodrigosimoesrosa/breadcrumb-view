package br.com.mirabilis.breadcrumb.implementation

import android.animation.Animator
import android.content.Context
import android.graphics.drawable.Drawable
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import br.com.mirabilis.breadcrumb.R
import br.com.mirabilis.breadcrumb.behavior.Breadcrumb
import br.com.mirabilis.breadcrumb.behavior.Item
import kotlinx.android.synthetic.main.breadcrumb_item.view.*

/**
 * Created by rodrigosimoesrosa on 02/04/19.
 * Copyright ©Mirabilis 2019. All rights reserved.
 */
class BreadcrumbItemView : ConstraintLayout, Item.View {

    private var imageScaleDisable: Float
    private var imageScaleEnable: Float

    private var imageEnable: Drawable?
    private var imageDisable: Drawable?

    private var drawableBackgroundEnable: Drawable?
    private var drawableBackgroundDisable: Drawable?

    private var state: Boolean
    private var title: String?
    private var order: Int

    private var settings: Breadcrumb.Settings? = null

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null,
                defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {

        val attributes = context.theme.obtainStyledAttributes(
                attrs, R.styleable.BreadcrumbItemView, 0, 0)

        imageEnable = attributes.getDrawable(R.styleable.BreadcrumbItemView_breadcrumbItemImageEnable)
        setImageEnable()

        imageDisable = attributes.getDrawable(R.styleable.BreadcrumbItemView_breadcrumbItemImageDisable)
        setImageDisable()

        state = attributes.getBoolean(R.styleable.BreadcrumbItemView_breadcrumbItemState, Item.STATE_DEFAULT)
        order = attributes.getInt(R.styleable.BreadcrumbItemView_breadcrumbItemOrder, 0)

        imageScaleDisable = attributes.getFloat(
                R.styleable.BreadcrumbItemView_breadcrumbItemImageScaleDisable, Item.SCALE_CENTER_IMAGE_DISABLE)
        imageScaleEnable = attributes.getFloat(
                R.styleable.BreadcrumbItemView_breadcrumbItemImageScaleEnable, Item.SCALE_CENTER_IMAGE_ENABLE)

        title = attributes.getString(R.styleable.BreadcrumbItemView_breadcrumbItemLabel)
        setTitle()

        drawableBackgroundDisable =
                attributes.getDrawable(R.styleable.BreadcrumbItemView_breadcrumbItemImageBackgroundDisable)
        setBackgroundDisable()

        drawableBackgroundEnable =
                attributes.getDrawable(R.styleable.BreadcrumbItemView_breadcrumbItemImageBackgroundEnable)
        setBackgroundEnable()

        applyState()
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.breadcrumb_item, this, true)
    }

    private fun setImageEnable() {
        imageCenterEnable.setImageDrawable(imageEnable)
    }

    private fun setImageDisable() {
        imageCenterDisable.setImageDrawable(imageDisable)
    }

    override fun getOrder(): Int {
        return order
    }

    override fun getState(): Boolean {
        return state
    }

    private fun setBackgroundDisable() {
        imageBackgroundDisable.setImageDrawable(drawableBackgroundDisable)
    }

    private fun setBackgroundEnable() {
        imageBackgroundEnable.setImageDrawable(drawableBackgroundEnable)
    }

    private fun setTitle() {
        if (title == null) {
            label.visibility = View.GONE
            return
        }

        label.visibility = View.VISIBLE
        label.text = title
    }

    private fun applyState() {
        val isAnimated = settings?.isItemsAnimated() ?: false
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

    override fun changeState(state: Boolean) {
        this.state = state
        applyState()
    }

    override fun setOnClickListener(listener: Item.Listener) {
        setOnClickListener { listener.onClickListener(order, title) }
    }

    private fun disable() {
        showImageBackgroundEnable(false)
        showImageEnable(false)
        showLabelEnable(false)

        imageCenterDisable.scaleX = imageScaleDisable
        imageCenterDisable.scaleY = imageScaleDisable
    }

    private fun enable() {
        showImageBackgroundEnable(true)
        showImageEnable(true)
        showLabelEnable(true)

        imageCenterEnable.scaleX = imageScaleEnable
        imageCenterEnable.scaleY = imageScaleEnable
    }

    private fun disableWithAnimation() {
        disableLabelWithAnimation()
        disableBackgroundWithAnimation()
        disableImageCenterWithAnimation()
    }

    private fun disableLabelWithAnimation() {
        val duration = settings?.getItemsAnimationDuration() ?: 0
        label.animate().alpha(Item.ALPHA_DISABLE).setDuration(duration).start()
    }

    private fun disableImageCenterWithAnimation() {
        val duration = settings?.getItemsAnimationDuration() ?: 0
        val interpolator = settings?.getItemsInterpolator()

        val animatorProperty = imageCenterEnable.animate()
                .scaleY(imageScaleDisable)
                .scaleX(imageScaleDisable)
                .setDuration(duration)
                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {}

                    override fun onAnimationCancel(animation: Animator?) {}

                    override fun onAnimationStart(animation: Animator?) {}

                    override fun onAnimationEnd(animation: Animator?) {
                        imageCenterDisable.visibility = View.VISIBLE
                        imageCenterEnable.visibility = View.GONE

                        imageCenterEnable.scaleX = imageScaleEnable
                        imageCenterEnable.scaleY = imageScaleEnable
                    }
                })

        if (interpolator != null) {
            animatorProperty.interpolator = interpolator
        }

        animatorProperty.start()
    }

    private fun disableBackgroundWithAnimation() {
        val differenceScale = getDrawableBackgroundDifferenceScale()
        val duration = settings?.getItemsAnimationDuration() ?: 0
        val interpolator = settings?.getItemsInterpolator()

        val animatorProperty = imageBackgroundEnable.animate()
                .scaleY(differenceScale)
                .scaleX(differenceScale)
                .setDuration(duration)
                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {}

                    override fun onAnimationCancel(animation: Animator?) {}

                    override fun onAnimationStart(animation: Animator?) {
                        imageBackgroundDisable.visibility = View.VISIBLE
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        imageBackgroundEnable.visibility = View.INVISIBLE

                        imageBackgroundEnable.scaleX = Item.SCALE_DEFAULT
                        imageBackgroundEnable.scaleY = Item.SCALE_DEFAULT
                    }
                })

        if (interpolator != null) {
            animatorProperty.interpolator = interpolator
        }

        animatorProperty.start()
    }

    private fun getDrawableBackgroundDifferenceScale(): Float {
        val disableIntrinsicWidth = drawableBackgroundDisable?.intrinsicWidth?.toFloat() ?: 0f
        val enableIntrinsicWidth = drawableBackgroundEnable?.intrinsicWidth?.toFloat() ?: 0f
        return disableIntrinsicWidth / enableIntrinsicWidth
    }

    private fun enableWithAnimation() {
        enableLabelWithAnimation()
        enableBackgroundWithAnimation()
        enableImageCenterWithAnimation()
    }

    private fun enableLabelWithAnimation() {
        val duration = settings?.getItemsAnimationDuration() ?: 0
        label.animate().alpha(Item.ALPHA_ENABLE).setDuration(duration).start()
    }

    private fun enableBackgroundWithAnimation() {
        val difference = (Item.SCALE_DEFAULT - getDrawableBackgroundDifferenceScale())
        val differenceScale = difference + Item.SCALE_DEFAULT

        val duration = settings?.getItemsAnimationDuration() ?: 0
        val interpolator = settings?.getItemsInterpolator()

        val animatorProperty = imageBackgroundDisable.animate()
                .scaleY(differenceScale)
                .scaleX(differenceScale)
                .setDuration(duration)
                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {}

                    override fun onAnimationCancel(animation: Animator?) {}

                    override fun onAnimationStart(animation: Animator?) {}

                    override fun onAnimationEnd(animation: Animator?) {
                        imageBackgroundEnable.visibility = View.VISIBLE
                        imageBackgroundDisable.visibility = View.INVISIBLE

                        imageBackgroundDisable.scaleX = Item.SCALE_DEFAULT
                        imageBackgroundDisable.scaleY = Item.SCALE_DEFAULT
                    }
                })

        if (interpolator != null) {
            animatorProperty.interpolator = interpolator
        }

        animatorProperty.start()
    }

    private fun enableImageCenterWithAnimation() {
        val duration = settings?.getItemsAnimationDuration() ?: 0
        val interpolator = settings?.getItemsInterpolator()

        val animatorProperty = imageCenterDisable.animate()
                .scaleY(imageScaleEnable)
                .scaleX(imageScaleEnable)
                .setDuration(duration)
                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {}

                    override fun onAnimationCancel(animation: Animator?) {}

                    override fun onAnimationStart(animation: Animator?) {}

                    override fun onAnimationEnd(animation: Animator?) {
                        imageCenterEnable.visibility = View.VISIBLE
                        imageCenterDisable.visibility = View.GONE

                        imageCenterDisable.scaleX = imageScaleDisable
                        imageCenterDisable.scaleY = imageScaleDisable
                    }
                })

        if (interpolator != null) {
            animatorProperty.interpolator = interpolator
        }

        animatorProperty.start()
    }

    private fun showImageEnable(value: Boolean) {
        if (value) {
            imageCenterDisable.visibility = View.GONE
            imageCenterEnable.visibility = View.VISIBLE
            return
        }

        imageCenterEnable.visibility = View.GONE
        imageCenterDisable.visibility = View.VISIBLE
    }

    private fun showImageBackgroundEnable(value: Boolean) {
        if (value) {
            imageBackgroundDisable.visibility = View.INVISIBLE
            imageBackgroundEnable.visibility = View.VISIBLE
            return
        }

        imageBackgroundEnable.visibility = View.INVISIBLE
        imageBackgroundDisable.visibility = View.VISIBLE
    }

    private fun showLabelEnable(value: Boolean) {
        if (value) {
            label.alpha = Item.ALPHA_ENABLE
            return
        }

        label.alpha = Item.ALPHA_DISABLE
    }

    override fun setBreadcrumbSettings(settings: Breadcrumb.Settings) {
        this.settings = settings
    }
}
