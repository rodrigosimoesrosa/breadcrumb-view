package br.com.mirabilis.breadcrumb

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import br.com.mirabilis.breadcrumb.behavior.Breadcrumb
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), Breadcrumb.Listener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        breadcrumb.setListener(this)
        btnNext.setOnClickListener { breadcrumb.next() }
        btnPrevious.setOnClickListener { breadcrumb.previous() }
    }

    /**
     * You could add a fragment using order
     */
    override fun onBreadcrumbOrderChanged(order: Int) {
        Toast.makeText(this, getString(R.string.your_breadcrumb_order_is, order), Toast.LENGTH_LONG).show()
    }
}
