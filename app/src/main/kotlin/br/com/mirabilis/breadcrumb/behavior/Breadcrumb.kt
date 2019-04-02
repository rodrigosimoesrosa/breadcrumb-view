package br.com.mirabilis.breadcrumb.behavior

import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator

/**
 * Created by rodrigosimoesrosa on 02/04/19.
 * Copyright @Mirabilis. All rights reserved.
 */
object Breadcrumb {

    const val INITIAL_BREADCRUMB = 0
    const val CLICKABLE_ITEMS = false
    const val SKIP_NEXT = false

    const val ANIMATION_ITEM_DEFAULT = true
    const val ANIMATION_ITEM_DURATION = 150L

    const val ANIMATION_CHECK_DEFAULT = true
    const val ANIMATION_CHECK_DURATION = 200L

    val INTERPOLATOR_ITEM_DEFAULT = LinearInterpolator()
    val INTERPOLATOR_CHECK_DEFAULT = OvershootInterpolator()

    interface Listener {
        fun onBreadcrumbOrderChanged(order: Int)
    }

    interface View {
        fun setItemsInterpolator(interpolator: Interpolator?)
        fun setChecksInterpolator(interpolator: Interpolator?)
        fun setListener(listener: Listener)
        fun previous()
        fun next()
    }

    interface Settings {
        fun getChecksAnimationDuration(): Long
        fun isChecksAnimated(): Boolean
        fun getChecksInterpolator(): Interpolator?

        fun getItemsAnimationDuration(): Long
        fun isItemsAnimated(): Boolean
        fun getItemsInterpolator(): Interpolator?
    }
}
