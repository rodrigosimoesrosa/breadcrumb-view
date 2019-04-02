package br.com.mirabilis.breadcrumb.behavior

/**
 * Created by rodrigosimoesrosa on 02/04/19.
 * Copyright ©Mirabilis 2019. All rights reserved.
 */
object Item {

    const val SCALE_DEFAULT = 1.0F
    const val SCALE_CENTER_IMAGE_ENABLE = 1.0F
    const val SCALE_CENTER_IMAGE_DISABLE = .8F
    const val STATE_DEFAULT = false
    const val ALPHA_ENABLE = 1.0f
    const val ALPHA_DISABLE = .5f

    interface Listener {
        fun onClickListener(order: Int, value: String?)
    }

    interface View {
        fun setBreadcrumbSettings(settings: Breadcrumb.Settings)
        fun setOnClickListener(listener: Listener)
        fun getOrder(): Int
        fun getState(): Boolean
        fun changeState(state: Boolean)
    }
}
