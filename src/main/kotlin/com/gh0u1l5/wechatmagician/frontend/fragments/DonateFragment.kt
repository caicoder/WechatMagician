package com.gh0u1l5.wechatmagician.frontend.fragments

import android.content.ComponentName
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.Toast
import com.gh0u1l5.wechatmagician.Global.PREFERENCE_NAME_SETTINGS
import com.gh0u1l5.wechatmagician.Global.SETTINGS_CUSTOM_PACKAGE_NAME
import com.gh0u1l5.wechatmagician.Global.STATUS_FLAG_URI_ROUTER
import com.gh0u1l5.wechatmagician.Global.WECHAT_PACKAGE_NAME
import com.gh0u1l5.wechatmagician.R
import com.gh0u1l5.wechatmagician.frontend.fragments.StatusFragment.Companion.readHookStatus
import com.gh0u1l5.wechatmagician.util.AlipayUtil
import kotlinx.android.synthetic.main.fragment_donate.*


class DonateFragment : Fragment() {

    private val alipayCode = "FKX04114Q6YBQLKYU0KS09"
    private val tenpayCode = "f2f00-2YC_1Sfo3jM1G--Zj8kC2Z7koDXC8r"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_donate, container, false)

    override fun onStart() {
        super.onStart()

        // Hide Tenpay if the URI router is not hijacked.
        val status = readHookStatus(activity)
        if (status == null || status[STATUS_FLAG_URI_ROUTER] != true) {
            donate_tenpay.visibility = GONE
        }

        // Set onClick listeners for donation buttons.
        donate_alipay.setOnClickListener { view ->
            if (!AlipayUtil.hasInstalledAlipayClient(view.context)) {
                Toast.makeText(view.context, R.string.prompt_alipay_not_found, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Toast.makeText(view.context, R.string.prompt_wait, Toast.LENGTH_SHORT).show()
            AlipayUtil.startAlipayClient(view.context, alipayCode)
        }
        donate_tenpay.setOnClickListener { view ->
            // TODO: Add support for regular expressions
            val settings = view.context.getSharedPreferences(PREFERENCE_NAME_SETTINGS, MODE_PRIVATE)
            val packageName = settings.getString(SETTINGS_CUSTOM_PACKAGE_NAME, WECHAT_PACKAGE_NAME)
            val className = "$packageName.plugin.base.stub.WXCustomSchemeEntryActivity"
            val componentName = ComponentName(packageName, className)
            try {
                view.context.startActivity(Intent(Intent.ACTION_VIEW).apply {
                    component = componentName
                    data = Uri.parse("weixin://magician/donate/$tenpayCode")
                    flags = Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP
                })
                Toast.makeText(view.context, R.string.prompt_wait, Toast.LENGTH_SHORT).show()
            } catch (e: Throwable) {
                Toast.makeText(view.context, e.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        fun newInstance(): DonateFragment = DonateFragment()
    }
}
