package com.example.bai1_currency

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    // Danh sách tiền tệ (có VND và EUR)
    private val currencies = listOf(
        "USD - United States Dollar",
        "EUR - Euro",
        "JPY - Japanese Yen",
        "GBP - British Pound",
        "AUD - Australian Dollar",
        "CAD - Canadian Dollar",
        "CHF - Swiss Franc",
        "CNY - Chinese Yuan",
        "SEK - Swedish Krona",
        "VND - Vietnamese Dong"
    )

    // Tỷ giá quy đổi sang USD
    private val ratesToUSD = mapOf(
        "USD" to 1.0,
        "EUR" to 1.15,        // 1 EUR = 1.15 USD
        "JPY" to 0.0069,      // 1 JPY = 0.0069 USD
        "GBP" to 1.30,        // 1 GBP = 1.30 USD
        "AUD" to 0.70,        // 1 AUD = 0.70 USD
        "CAD" to 0.75,        // 1 CAD = 0.75 USD
        "CHF" to 1.05,        // 1 CHF = 1.05 USD
        "CNY" to 0.15,        // 1 CNY = 0.15 USD
        "SEK" to 0.09,        // 1 SEK = 0.09 USD
        "VND" to 0.0000427    // 1 VND = 0.0000427 USD
    )


    private lateinit var etAmount1: EditText
    private lateinit var etAmount2: EditText
    private lateinit var spinnerCurrency1: Spinner
    private lateinit var spinnerCurrency2: Spinner
    private lateinit var tvCurrency1: TextView
    private lateinit var tvCurrency2: TextView

    private var isEditing1 = false
    private var isEditing2 = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Ánh xạ view
        etAmount1 = findViewById(R.id.etAmount1)
        etAmount2 = findViewById(R.id.etAmount2)
        spinnerCurrency1 = findViewById(R.id.spinnerCurrency1)
        spinnerCurrency2 = findViewById(R.id.spinnerCurrency2)
        tvCurrency1 = findViewById(R.id.tvCurrency1)
        tvCurrency2 = findViewById(R.id.tvCurrency2)

        // Adapter cho spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCurrency1.adapter = adapter
        spinnerCurrency2.adapter = adapter

        // Đặt selection mặc định đồng bộ với TextView:
        val defaultCurrency1 = "VND"
        val defaultIndex1 = currencies.indexOfFirst { it.startsWith(defaultCurrency1) }
        spinnerCurrency1.setSelection(if (defaultIndex1 != -1) defaultIndex1 else 0)

        val defaultCurrency2 = "EUR"
        val defaultIndex2 = currencies.indexOfFirst { it.startsWith(defaultCurrency2) }
        spinnerCurrency2.setSelection(if (defaultIndex2 != -1) defaultIndex2 else 1)

        // Cập nhật TextView theo selection mặc định
        updateTextView(tvCurrency1, spinnerCurrency1.selectedItemPosition)
        updateTextView(tvCurrency2, spinnerCurrency2.selectedItemPosition)

        // Xử lý khi nhập số tiền hoặc đổi tiền tệ
        etAmount1.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!isEditing2) {
                    isEditing1 = true
                    convertCurrency(fromFirstToSecond = true)
                    isEditing1 = false
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        etAmount2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!isEditing1) {
                    isEditing2 = true
                    convertCurrency(fromFirstToSecond = false)
                    isEditing2 = false
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Listener cập nhật TextView khi Spinner thay đổi
        spinnerCurrency1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                updateTextView(tvCurrency1, position)
                if (!isEditing1 && !isEditing2) {
                    convertCurrency(fromFirstToSecond = true)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        spinnerCurrency2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                updateTextView(tvCurrency2, position)
                if (!isEditing1 && !isEditing2) {
                    convertCurrency(fromFirstToSecond = true)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Khởi tạo giá trị mặc định
        etAmount1.setText("0")
    }

    // Hàm cập nhật TextView theo vị trí spinner
    private fun updateTextView(tv: TextView, position: Int) {
        // Lấy phần tên tiền tệ (bỏ 6 ký tự đầu "XXX - ")
        val fullText = currencies[position]
        val displayName = if (fullText.length > 6) fullText.substring(6) else fullText
        tv.text = displayName
    }

    private fun convertCurrency(fromFirstToSecond: Boolean) {
        val amount1Text = etAmount1.text.toString()
        val amount2Text = etAmount2.text.toString()

        val currencyCode1 = spinnerCurrency1.selectedItem.toString().substring(0, 3)
        val currencyCode2 = spinnerCurrency2.selectedItem.toString().substring(0, 3)

        if (fromFirstToSecond) {
            val amount1 = amount1Text.toDoubleOrNull() ?: 0.0
            val rate1 = ratesToUSD[currencyCode1] ?: 1.0
            val rate2 = ratesToUSD[currencyCode2] ?: 1.0
            val converted = amount1 * rate1 / rate2
            etAmount2.setText("%.2f".format(converted))
        } else {
            val amount2 = amount2Text.toDoubleOrNull() ?: 0.0
            val rate1 = ratesToUSD[currencyCode1] ?: 1.0
            val rate2 = ratesToUSD[currencyCode2] ?: 1.0
            val converted = amount2 * rate2 / rate1
            etAmount1.setText("%.2f".format(converted))
        }
    }
}
