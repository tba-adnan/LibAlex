package com.example.libalex.frags

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.libalex.R

class SummaryFragment : Fragment() {
    private lateinit var summaryTextView: TextView

    companion object {
        private const val ARG_SUMMARY = "arg_summary"

        fun newInstance(summary: String): SummaryFragment {
            val fragment = SummaryFragment()
            val args = Bundle()
            args.putString(ARG_SUMMARY, summary)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_summary, container, false)
        summaryTextView = view.findViewById(R.id.summaryTextView)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val summary = arguments?.getString(ARG_SUMMARY)
        summary?.let {
            summaryTextView.text = it
        }
    }
}
