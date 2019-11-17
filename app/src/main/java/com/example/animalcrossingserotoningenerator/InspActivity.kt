package com.example.animalcrossingserotoningenerator

import android.R.id
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.quotes_main.*

class InspActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.quotes_frame)

        supportFragmentManager.beginTransaction().replace(
            id.content,
            InspFrag()
        ).commit()
    }

    class InspFrag : Fragment() {
        companion object {
            fun newInstance() = InspFrag()
        }
        private lateinit var viewModel: QuoteViewModel

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            // Each fragment has its own ViewModel
            viewModel =
                ViewModelProviders.of(this)[QuoteViewModel::class.java]
            return inflater.inflate(R.layout.quotes_main, container, false)
        }

        override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)

            viewModel.observeQuote().observe(this, Observer {
                quoteTV.text = it
            })

            newQuoteBut.setOnClickListener {
                viewModel.netFetchQuote()
            }

            viewModel.netFetchQuote()
        }
    }
}