package com.crestinfosystems_jinay.trello.Screens_Activity.setting.ProfileScreen.UserDataForm

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.crestinfosystems_jinay.trello.data.UserData
import com.crestinfosystems_jinay.trello.databinding.ActivityUserDataFormBinding
import com.crestinfosystems_jinay.trello.databinding.DialogCustomBackConfirmationBinding
import com.crestinfosystems_jinay.trello.network.FirestoreDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserDataForm : AppCompatActivity() {
    var binding: ActivityUserDataFormBinding? = null
    var db = FirestoreDatabase()
    var isCorrect: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityUserDataFormBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        binding?.popUpBackUserForm?.setOnClickListener { customDialogForBackButton() { onBackPressed() } }
        setContentView(binding?.root)
        updateDataButtonLisner()
        updateUIwithInitData()
        onTextEdit()
    }

    private fun String?.toEditable(): Editable? {
        return Editable.Factory.getInstance().newEditable(this)
    }

    private fun customDialogForBackButton(onYesPressed: () -> Unit) {

        val customDialog = Dialog(this)
        var dialogBinding: DialogCustomBackConfirmationBinding? =
            DialogCustomBackConfirmationBinding.inflate(layoutInflater)
        customDialog.setContentView(dialogBinding?.root!!)
        dialogBinding?.dialogBtnYes?.setOnClickListener {
            onYesPressed()
            customDialog.dismiss()
        }
        dialogBinding?.dialogBtnNo?.setOnClickListener {
            customDialog.dismiss()
        }
        customDialog.show()
    }

    private fun updateUIwithInitData() {
        if (intent.getStringExtra("mobile_num") != null) {
            binding?.textInputLayoutMobilenumEdit?.text =
                intent.getStringExtra("mobile_num").toEditable()
        }
        if (intent.getStringExtra("organization") != null) {
            binding?.textInputLayoutOrganizationEdit?.text =
                intent.getStringExtra("organization").toEditable()
        }
    }

    private fun updateDataButtonLisner() {
        binding?.updateBtn?.setOnClickListener {
            if (isCorrect) {
                Toast.makeText(this, "Update Successful", Toast.LENGTH_SHORT).show()
                CoroutineScope(Dispatchers.IO).launch {
                    db.updateUser(
                        user = UserData(
                            email = intent.getStringExtra("email"),
                            name = null,
                            mobileNumber = binding?.textInputLayoutMobilenumEdit?.text.toString(),
                            organization = binding?.textInputLayoutOrganizationEdit?.text.toString()
                        )
                    ) {
                        Toast.makeText(
                            this@UserDataForm,
                            "Error while Updating Try after some time",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    withContext(Dispatchers.Main) {
                        this@UserDataForm.finish()
                    }
                }
            } else {
                Toast.makeText(this, "Please Fill Correct Data to Update", Toast.LENGTH_SHORT)
                    .show()
            }
        }


    }

    private fun onTextEdit() {
        binding?.textInputLayoutMobilenumEdit?.setOnFocusChangeListener { v, hasFocus ->
            binding?.textInputLayoutMobilenum?.isHintEnabled = !hasFocus
        }
        binding?.textInputLayoutOrganizationEdit?.setOnFocusChangeListener { v, hasFocus ->
            binding?.textInputLayoutOrganization?.isHintEnabled = !hasFocus
        }
        binding?.textInputLayoutMobilenumEdit?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (!isValidMobileNumber(s.toString())) {
                    isCorrect = false
                    binding?.textInputLayoutMobilenum?.error = "Invalid Mobile Number"
                } else {
                    isCorrect = true
                    binding?.textInputLayoutMobilenum?.error = null
                }
            }
        })
        binding?.textInputLayoutOrganizationEdit?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length > 1) {
                    binding?.textInputLayoutOrganization?.error = null
                    isCorrect = true
                } else {
                    binding?.textInputLayoutOrganization?.error = "Enter Correct Organization Name"
                    isCorrect = false
                }
            }
        })
    }

    private fun isValidMobileNumber(mobile: String): Boolean {
        val pattern = Regex("^[6-9]\\d{9}\$")
        // Use Patterns class to validate the mobile number
        return pattern.containsMatchIn(mobile)
    }
}