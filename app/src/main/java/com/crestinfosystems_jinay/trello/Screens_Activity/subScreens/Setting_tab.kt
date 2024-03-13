package com.crestinfosystems_jinay.trello.Screens_Activity.subScreens

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.crestinfosystems_jinay.trello.MainActivity
import com.crestinfosystems_jinay.trello.R
import com.crestinfosystems_jinay.trello.Screens_Activity.setting.ProfileScreen.Profile_Screen
import com.crestinfosystems_jinay.trello.Screens_Activity.setting.User_Manual
import com.crestinfosystems_jinay.trello.Screens_Activity.setting.projects.ProjectList
import com.crestinfosystems_jinay.trello.adapter.SettingTabAdapter
import com.crestinfosystems_jinay.trello.data.SettinsTilesFilelds
import com.crestinfosystems_jinay.trello.databinding.FragmentSettingTabBinding
import com.crestinfosystems_jinay.trello.network.FirestoreDatabase
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth


class Setting_tab : Fragment() {
    var db = FirestoreDatabase()
    private var binding: FragmentSettingTabBinding? = null
    private var settingTileField: List<SettinsTilesFilelds> =
        listOf(
            SettinsTilesFilelds(
                id = 0,
                title = "Profile",
                icon = R.drawable.ic_setting_profile
            ) {
                var intent = Intent(activity, Profile_Screen::class.java)
                startActivity(intent)
            },
            SettinsTilesFilelds(
                id = 1,
                title = "User Manual",
                icon = R.drawable.ic_setting_conversation
            ) {
                var intent = Intent(activity, User_Manual::class.java)
                startActivity(intent)
            },
            SettinsTilesFilelds(
                id = 2,
                title = "Projects (assigned to you)",
                icon = R.drawable.ic_setting_project
            ) {
                var intent = Intent(activity, ProjectList::class.java)
                startActivity(intent)
                Toast.makeText(
                    activity,
                    "This List will show Project Assigned to You!!",
                    Toast.LENGTH_SHORT
                ).show()
            },
//            SettinsTilesFilelds(
//                id = 3,
//                title = "Terms and Conditions",
//                icon = R.drawable.ic_setting_terms_and_condition,
//                {}),
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingTabBinding.inflate(inflater, container, false)
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding?.settingTabTiles?.layoutManager = layoutManager
        binding?.settingTabTiles?.adapter = SettingTabAdapter(settingTileField)
        binding?.logoutBtn?.setOnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            var mGoogleSignInClient = activity?.let { it1 -> GoogleSignIn.getClient(it1, gso) }
            FirebaseAuth.getInstance().signOut()
            mGoogleSignInClient?.signOut()
            var intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}