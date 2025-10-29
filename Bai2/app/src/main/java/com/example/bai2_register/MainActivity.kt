package com.example.bai2_register

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var etFirstName: EditText
    private lateinit var etLastName: EditText
    private lateinit var rgGender: RadioGroup
    private lateinit var etBirthday: EditText
    private lateinit var btnSelect: Button
    private lateinit var calendarView: CalendarView
    private lateinit var etAddress: EditText
    private lateinit var etEmail: EditText
    private lateinit var cbAgree: CheckBox
    private lateinit var btnRegister: Button

    private var isCalendarVisible = false
    private val originalBackgrounds = mutableMapOf<EditText, Drawable>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Map views
        etFirstName = findViewById(R.id.etFirstName)
        etLastName = findViewById(R.id.etLastName)
        rgGender = findViewById(R.id.rgGender)
        etBirthday = findViewById(R.id.etBirthday)
        btnSelect = findViewById(R.id.btnSelect)
        calendarView = findViewById(R.id.calendarView)
        etAddress = findViewById(R.id.etAddress)
        etEmail = findViewById(R.id.etEmail)
        cbAgree = findViewById(R.id.cbAgree)
        btnRegister = findViewById(R.id.btnRegister)

        // Save default backgrounds to restore after validation
        listOf(etFirstName, etLastName, etBirthday, etAddress, etEmail).forEach { et ->
            originalBackgrounds[et] = et.background
        }

        // Toggle CalendarView on Select
//        btnSelect.setOnClickListener {
//            isCalendarVisible = !isCalendarVisible
//            calendarView.visibility = if (isCalendarVisible) View.VISIBLE else View.GONE
//        }
        // Calendarview không thể render được, sử dụng datePickerDialog thay thế
        btnSelect.setOnClickListener {
            val cal = Calendar.getInstance()
            val year = cal.get(Calendar.YEAR)
            val month = cal.get(Calendar.MONTH)
            val day = cal.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = android.app.DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedCal = Calendar.getInstance().apply {
                        set(selectedYear, selectedMonth, selectedDay)
                    }
                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    etBirthday.setText(sdf.format(selectedCal.time))
                },
                year, month, day
            )
            datePickerDialog.show()
        }

        // Pick a date -> fill EditText and hide calendar
//        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
//            val cal = Calendar.getInstance().apply { set(year, month, dayOfMonth) }
//            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
//            etBirthday.setText(sdf.format(cal.time))
//            calendarView.visibility = View.GONE
//            isCalendarVisible = false
//        }

        // Validate on Register
        btnRegister.setOnClickListener {
            var valid = true
            resetBackgrounds()

            if (etFirstName.text.toString().trim().isEmpty()) {
                setErrorBackground(etFirstName); valid = false
            }
            if (etLastName.text.toString().trim().isEmpty()) {
                setErrorBackground(etLastName); valid = false
            }
            if (rgGender.checkedRadioButtonId == -1) {
                Toast.makeText(this, "Vui lòng chọn giới tính", Toast.LENGTH_SHORT).show()
                valid = false
            }
            if (etBirthday.text.toString().trim().isEmpty()) {
                setErrorBackground(etBirthday); valid = false
            }
            if (etAddress.text.toString().trim().isEmpty()) {
                setErrorBackground(etAddress); valid = false
            }
            if (etEmail.text.toString().trim().isEmpty()) {
                setErrorBackground(etEmail); valid = false
            }
            if (!cbAgree.isChecked) {
                Toast.makeText(this, "Bạn phải đồng ý với điều khoản sử dụng", Toast.LENGTH_SHORT).show()
                valid = false
            }

            if (valid) {
                Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setErrorBackground(editText: EditText) {
        editText.setBackgroundColor(Color.RED)
    }

    private fun resetBackgrounds() {
        originalBackgrounds.forEach { (et, bg) -> et.background = bg }
    }
}