package br.com.mirabilis.breadcrumb.behavior

import br.com.mirabilis.breadcrumb.behavior.Breadcrumb

/**
 * Created by rodrigosimoesrosa on 02/04/19.
 * Copyright ©Mirabilis 2019. All rights reserved.
 */
object Check {

    const val BREADCRUMB_CHECK_DEFAULT = false
    const val SCALE_DEFAULT = 1.0F
    const val SCALE_DISABLE_DEFAULT = 0F
    const val PIVOT_DEFAULT = .5F

    interface View {
        fun getOrder(): Int
        fun getState(): Boolean
        fun changeState(state: Boolean)
        fun setBreadcrumbSettings(settings: Breadcrumb.Settings)
    }
}
