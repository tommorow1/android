package com.example.bloold.buildp.ui.fragments

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.example.bloold.buildp.R
import com.example.bloold.buildp.common.IntentHelper
import com.example.bloold.buildp.databinding.FragmentWebviewBinding
import kotlinx.android.synthetic.main.app_bar_main.*

class WebViewFragment : Fragment()
{
    private lateinit var mBinding:FragmentWebviewBinding
    private lateinit var title:String
    private var lastOpenUrl: String? = null
    private var lastFailUrl: String? = null

    private var mCurrentUrl: String? = null//Для того чтобы правильно отслеживать редиректы

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_webview, container, false)
        return mBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title=arguments?.getString(IntentHelper.EXTRA_TITLE)?:""
    }

    override fun onResume() {
        super.onResume()
        activity?.toolbar?.title = title
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, progress: Int) {
                mBinding.progressBar.progress = progress
                if (progress == 100) {
                    mBinding.progressBar.visibility = View.GONE
                    lastOpenUrl = view.url
                    if (lastOpenUrl != null && lastOpenUrl == lastFailUrl)
                        mBinding.vBlank.visibility = View.VISIBLE
                    else
                        mBinding.vBlank.visibility = View.GONE

                } else {
                    mBinding.progressBar.visibility = View.VISIBLE
                }

            }
        }
        mBinding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
                Log.d(TAG, "shouldOverrideUrlLoading() called with: view = [$view], url = [$url]")
                if (mCurrentUrl != null && url != null && url == mCurrentUrl) {
                    mBinding.webView.goBack()
                    return true
                }

                view.loadUrl(url)
                mCurrentUrl = url
                return true
            }

            override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
                Log.d(TAG, "onReceivedError() called with: view = [$view], errorCode = [$errorCode], description = [$description], failingUrl = [$failingUrl]")
                super.onReceivedError(view, errorCode, description, failingUrl)
                if (errorCode == WebViewClient.ERROR_HOST_LOOKUP) {
                    lastFailUrl = failingUrl
                    activity?.let { Toast.makeText(it, R.string.error_check_internet, Toast.LENGTH_LONG).show() }
                }
            }
        }
        mBinding.webView.settings.javaScriptEnabled = true

        mBinding.webView.loadUrl(arguments?.getString(IntentHelper.EXTRA_URL))
    }

    companion object {
        val TAG = "FinesFragment"

        fun newInstance(title: String, url: String): WebViewFragment
                = WebViewFragment().apply { arguments=Bundle().apply {
                putString(IntentHelper.EXTRA_TITLE, title)
                putString(IntentHelper.EXTRA_URL, url)
            }}

    }
}
