// MainActivity.kt
package com.example.bai2_register

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    // Khai báo các view
    private lateinit var firstName: EditText
    private lateinit var lastName: EditText
    private lateinit var genderGroup: RadioGroup
    private lateinit var birthday: EditText
    private lateinit var selectDateButton: Button
    private lateinit var calendarView: CalendarView
    private lateinit var address: EditText
    private lateinit var email: EditText
    private lateinit var termsCheck: CheckBox
    private lateinit var registerButton: Button
    private lateinit var scrollView: ScrollView

    // Biến để lưu background mặc định
    private var defaultEditTextBg: Drawable? = null
    private var defaultRadioGroupBg: Drawable? = null
    private var defaultCheckboxColor: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Sử dụng file XML của bạn

        // Ánh xạ các view
        firstName = findViewById(R.id.etFirstName)
        lastName = findViewById(R.id.etLastName)
        genderGroup = findViewById(R.id.rgGender)
        birthday = findViewById(R.id.etBirthday)
        selectDateButton = findViewById(R.id.btnSelect)
        calendarView = findViewById(R.id.calendarView)
        address = findViewById(R.id.etAddress)
        email = findViewById(R.id.etEmail)
        termsCheck = findViewById(R.id.cbAgree)
        registerButton = findViewById(R.id.btnRegister)
        scrollView = findViewById(R.id.scrollView)

        // Lưu lại background/màu sắc mặc định
        defaultEditTextBg = firstName.background
        defaultRadioGroupBg = genderGroup.background
        defaultCheckboxColor = termsCheck.currentTextColor

        // --- Xử lý CalendarView ---
        setupCalendar()

        // --- Xử lý nút Register ---
        registerButton.setOnClickListener {
            if (validateInputs()) {
                Toast.makeText(this, "Registration Successful!", Toast.LENGTH_LONG).show()
                // (Bạn có thể thêm logic gửi dữ liệu đi ở đây)
            } else {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupCalendar() {
        // Nhấn nút "Select" để ẩn/hiện CalendarView
        selectDateButton.setOnClickListener {
            if (calendarView.visibility == View.VISIBLE) {
                calendarView.visibility = View.GONE
            } else {
                calendarView.visibility = View.VISIBLE

                // Cuộn xuống CalendarView khi nó hiện
                scrollView.post {
                    scrollView.smoothScrollTo(0, calendarView.top)
                }
            }
        }

        // Khi người dùng chọn ngày trên CalendarView
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // `month` bắt đầu từ 0, nên cần +1
            val selectedDate = "$dayOfMonth/${month + 1}/$year"

            // Cập nhật ngày vào EditText
            birthday.setText(selectedDate)

            // Tự động ẩn CalendarView sau khi chọn
            calendarView.visibility = View.GONE
        }
    }

    private fun validateInputs(): Boolean {
        // Reset tất cả các lỗi về mặc định trước khi kiểm tra
        resetErrorBackgrounds()

        var isValid = true

        // Kiểm tra First Name
        if (firstName.text.toString().trim().isEmpty()) {
            firstName.setBackgroundColor(Color.RED)
            isValid = false
        }

        // Kiểm tra Last Name
        if (lastName.text.toString().trim().isEmpty()) {
            lastName.setBackgroundColor(Color.RED)
            isValid = false
        }

        // Kiểm tra Gender
        if (genderGroup.checkedRadioButtonId == -1) { // -1 nghĩa là chưa chọn cái nào
            genderGroup.setBackgroundColor(Color.RED)
            isValid = false
        }

        // Kiểm tra Birthday
        if (birthday.text.toString().trim().isEmpty()) {
            birthday.setBackgroundColor(Color.RED)
            isValid = false
        }

        // Kiểm tra Address
        if (address.text.toString().trim().isEmpty()) {
            address.setBackgroundColor(Color.RED)
            isValid = false
        }

        // Kiểm tra Email
        if (email.text.toString().trim().isEmpty()) {
            email.setBackgroundColor(Color.RED)
            isValid = false
        }
        // Bạn có thể thêm kiểm tra định dạng email ở đây nếu muốn

        // Kiểm tra CheckBox Terms
        if (!termsCheck.isChecked) {
            termsCheck.setTextColor(Color.RED) // Đổi màu chữ của CheckBox
            isValid = false
        }

        return isValid
    }

    private fun resetErrorBackgrounds() {
        // Trả các EditText về background mặc định
        firstName.background = defaultEditTextBg
        lastName.background = defaultEditTextBg
        birthday.background = defaultEditTextBg
        address.background = defaultEditTextBg
        email.background = defaultEditTextBg

        // Trả RadioGroup về background mặc định
        genderGroup.background = defaultRadioGroupBg

        // Trả CheckBox về màu chữ mặc định
        termsCheck.setTextColor(defaultCheckboxColor)
    }
}
