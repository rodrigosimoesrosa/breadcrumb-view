package br.com.mirabilis.breadcrumb.implementation

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Interpolator
import br.com.mirabilis.breadcrumb.R
import br.com.mirabilis.breadcrumb.behavior.Breadcrumb
import br.com.mirabilis.breadcrumb.behavior.Check
import br.com.mirabilis.breadcrumb.behavior.Item

/**
 * Created by rodrigosimoesrosa on 02/04/19.
 * Copyright ©Mirabilis 2019. All rights reserved.
 */
class BreadcrumbView : ConstraintLayout, Breadcrumb.View, Item.Listener, Breadcrumb.Settings {

    private var maxBreadcrumbItems: Int = 0
    private var listener: Breadcrumb.Listener? = null
    private var clickableItems: Boolean = Breadcrumb.CLICKABLE_ITEMS
    private var skipNextItem: Boolean = Breadcrumb.SKIP_NEXT
    private var currentOrder = Breadcrumb.INITIAL_BREADCRUMB

    private var itemsAnimation: Boolean
    private var itemsAnimationDuration = Breadcrumb.ANIMATION_ITEM_DURATION
    private var itemsInterpolator: Interpolator? = Breadcrumb.INTERPOLATOR_ITEM_DEFAULT

    private var checksAnimation: Boolean
    private var checksAnimationDuration = Breadcrumb.ANIMATION_CHECK_DURATION
    private var checksInterpolator: Interpolator? = Breadcrumb.INTERPOLATOR_CHECK_DEFAULT

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null,
                defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {

        val attributes = context.theme.obtainStyledAttributes(
                attrs, R.styleable.BreadcrumbView, 0, 0)

        itemsAnimation = attributes.getBoolean(R.styleable.BreadcrumbView_breadcrumbItemAnimation,
                Breadcrumb.ANIMATION_ITEM_DEFAULT)

        checksAnimation = attributes.getBoolean(R.styleable.BreadcrumbView_breadcrumbCheckAnimation,
                Breadcrumb.ANIMATION_CHECK_DEFAULT)

        clickableItems = attributes.getBoolean(
                R.styleable.BreadcrumbView_breadcrumbClickableItems, Breadcrumb.CLICKABLE_ITEMS)

        skipNextItem = attributes.getBoolean(
                R.styleable.BreadcrumbView_breadcrumbSkipNext, Breadcrumb.CLICKABLE_ITEMS)

        clickableItems()
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.breadcrumb, this, true)
    }

    override fun onViewAdded(view: View?) {
        super.onViewAdded(view)
        maxBreadcrumbItems = getCountBreadcrumbItems()
        applyBreadcrumbSettingsOnItems()
        applyBreadcrumbSettingsOnChecks()
    }

    fun setOrder(order: Int) {
        currentOrder = order
        applyCurrentBreadcrumb()
    }

    fun getOrder(): Int {
        return currentOrder
    }

    private fun applyBreadcrumbSettingsOnChecks() {
        val constraint = getChildAt(0) as ConstraintLayout
        for (index in 0 until constraint.childCount) {
            val child = constraint.getChildAt(index)
            if (child is Check.View) {
                child.setBreadcrumbSettings(this)
            }
        }
    }

    private fun applyBreadcrumbSettingsOnItems() {
        val constraint = getChildAt(0) as ConstraintLayout
        for (index in 0 until constraint.childCount) {
            val child = constraint.getChildAt(index)
            if (child is Item.View) {
                child.setBreadcrumbSettings(this)
            }
        }
    }

    private fun getCountBreadcrumbItems(): Int {
        val constraint = getChildAt(0) as ConstraintLayout
        var count = 0
        for (index in 0 until constraint.childCount) {
            val child = constraint.getChildAt(index)
            if (child is Item.View) {
                count++
            }
        }
        return count
    }

    override fun previous() {
        if (currentOrder == 0) {
            return
        }

        currentOrder--
        applyCurrentBreadcrumb()
    }

    override fun next() {
        val maxOrder = maxBreadcrumbItems -1
        if (currentOrder == maxOrder) {
            return
        }

        currentOrder++
        applyCurrentBreadcrumb()
    }

    private fun clickableItems() {
        val constraint = getChildAt(0) as ConstraintLayout

        if (clickableItems) {
            for (index in 0 until constraint.childCount) {
                val child = constraint.getChildAt(index)
                if (child is Item.View) {
                    child.setOnClickListener(this)
                }
            }
        }
    }

    private fun getHigherItems(order: Int): ArrayList<Item.View> {
        val items = arrayListOf<Item.View>()
        val constraint = getChildAt(0) as ConstraintLayout

        for (index in 0 until constraint.childCount) {
            val item = constraint.getChildAt(index)
            if (item is Item.View) {
                if (item.getOrder() > order) {
                    items.add(item)
                }
            }
        }
        return items
    }

    private fun getLowerItems(order: Int): ArrayList<Item.View> {
        val items = arrayListOf<Item.View>()
        val constraint = getChildAt(0) as ConstraintLayout

        for (index in 0 until constraint.childCount) {
            val item = constraint.getChildAt(index)
            if (item is Item.View) {
                if (item.getOrder() <= order) {
                    items.add(item)
                }
            }
        }
        return items
    }

    override fun onClickListener(order: Int, value: String?) {
        if (order == currentOrder) {
            return
        }

        if ((order - currentOrder) >= 1 && !skipNextItem) {
            return
        }

        currentOrder = order
        applyCurrentBreadcrumb()
    }

    private fun applyCurrentBreadcrumb() {
        val higherOrderItems = getHigherItems(currentOrder)
        higherOrderItems.forEach {
            if (it.getState()) {
                it.changeState(false)
            }
        }

        val lowerOrderItems = getLowerItems(currentOrder)
        lowerOrderItems.forEach {
            if (!it.getState()) {
                it.changeState(true)
            }
        }

        val higherChecks = getHigherChecks(currentOrder)
        higherChecks.forEach {
            if (it.getState()) {
                it.changeState(false)
            }
        }

        val lowerChecks = getLowerChecks(currentOrder)
        lowerChecks.forEach {
            if (!it.getState()) {
                it.changeState(true)
            }
        }

        listener?.onBreadcrumbOrderChanged(currentOrder)
    }

    private fun getHigherChecks(order: Int): ArrayList<Check.View> {
        val items = arrayListOf<Check.View>()
        val constraint = getChildAt(0) as ConstraintLayout

        for (index in 0 until constraint.childCount) {
            val item = constraint.getChildAt(index)
            if (item is Check.View) {
                if (item.getOrder() > order) {
                    items.add(item)
                }
            }
        }
        return items
    }

    private fun getLowerChecks(order: Int): ArrayList<Check.View> {
        val items = arrayListOf<Check.View>()
        val constraint = getChildAt(0) as ConstraintLayout

        for (index in 0 until constraint.childCount) {
            val item = constraint.getChildAt(index)
            if (item is Check.View) {
                if (item.getOrder() <= order) {
                    items.add(item)
                }
            }
        }
        return items
    }

    override fun setListener(listener: Breadcrumb.Listener) {
        this.listener = listener
    }

    override fun isItemsAnimated(): Boolean {
        return itemsAnimation
    }

    override fun getItemsAnimationDuration(): Long {
        return itemsAnimationDuration
    }

    override fun getItemsInterpolator(): Interpolator? {
        return itemsInterpolator
    }

    override fun setItemsInterpolator(interpolator: Interpolator?) {
        this.itemsInterpolator = interpolator
    }

    override fun getChecksInterpolator(): Interpolator? {
        return checksInterpolator
    }

    override fun isChecksAnimated(): Boolean {
        return checksAnimation
    }

    override fun getChecksAnimationDuration(): Long {
        return checksAnimationDuration
    }

    override fun setChecksInterpolator(interpolator: Interpolator?) {
        this.checksInterpolator = interpolator
    }
}
