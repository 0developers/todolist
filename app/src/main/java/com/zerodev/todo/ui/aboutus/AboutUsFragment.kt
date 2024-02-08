package com.zerodev.todo.ui.aboutus

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.zerodev.todo.databinding.FragmentAboutusBinding

class AboutUsFragment : Fragment() {

    private var _binding: FragmentAboutusBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val aboutUsViewModel =
            ViewModelProvider(this).get(AboutUsViewModel::class.java)

        _binding = FragmentAboutusBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.appver.text = "App Version : ${context?.let { getAPPVersion(it) }}"
        //on clicks for social media channels 🤑🤑
        //telegram
        binding.telegram.setOnClickListener{
            val telUrl = "https://t.me/zero_developers"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(telUrl))
            intent.setPackage("org.telegram.messenger")
            if (context?.let { isIntentAvailable(it, intent) } == true) {
                startActivity(intent)
            } else {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(telUrl)))
            }
        }
        // instagram
        // for now i remove the instagram
        binding.instagram.visibility = View.GONE
        binding.instagram.setOnClickListener{
            val insUrl = ""
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(insUrl))
            intent.setPackage("com.discord")
            if (context?.let { isIntentAvailable(it, intent) } == true) {
                startActivity(intent)
            } else {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(insUrl)))
            }
        }
        binding.discord.setOnClickListener{
            val disUrl = "https://discord.gg/FQxG3pmEB7"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(disUrl))
            intent.setPackage("com.discord")
            if (context?.let { isIntentAvailable(it, intent) } == true) {
                startActivity(intent)
            } else {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(disUrl)))
            }
        }
        binding.tiktok.setOnClickListener {
            val Url = "https://www.tiktok.com/@grandffyt?_t=8jiaWme2IUo&_r=1"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Url))
            intent.setPackage("com.zhiliaoapp.musically")
            if (context?.let { isIntentAvailable(it, intent) } == true) {
                startActivity(intent)
            } else {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(Url)))
            }
        }
        binding.github.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.tiktok.com/@grandffyt?_t=8jiaWme2IUo&_r=1")))
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun getAPPVersion(context: Context) : String {
        val pkgManager = context.packageManager
        val pkgInfo = pkgManager.getPackageInfo(context.packageName, 0)
        return pkgInfo.versionName
    }
    private fun isIntentAvailable(context: Context, intent: Intent): Boolean {
        val packageManager = context.packageManager
        val activities = packageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL)
        return activities.isNotEmpty()
    }

}