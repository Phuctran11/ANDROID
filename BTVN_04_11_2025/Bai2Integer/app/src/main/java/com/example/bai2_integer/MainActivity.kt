package com.example.bai2_integer

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    // Khai báo các View trong layout
    private lateinit var etNumber: EditText           // Input nhập số tối đa
    private lateinit var radioGroup1: RadioGroup       // Nhóm RadioButton 1 (3 lựa chọn)
    private lateinit var radioGroup2: RadioGroup       // Nhóm RadioButton 2 (3 lựa chọn)
    private lateinit var listView: ListView             // Hiển thị danh sách số
    private lateinit var tvNoResult: TextView           // Hiển thị thông báo khi không có kết quả

    private lateinit var adapter: ArrayAdapter<Int>     // Adapter để hiển thị dữ liệu cho ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Ánh xạ các View từ layout
        etNumber = findViewById(R.id.etNumber)
        radioGroup1 = findViewById(R.id.radioGroup)
        radioGroup2 = findViewById(R.id.radioGroup2)
        listView = findViewById(R.id.listView)
        tvNoResult = findViewById(R.id.tvNoResult)

        // Khởi tạo adapter với danh sách rỗng, sử dụng layout mặc định cho mỗi item
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf())
        listView.adapter = adapter

        // Thêm listener để cập nhật danh sách mỗi khi người dùng nhập số mới
        etNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateList()   // Gọi hàm cập nhật danh sách khi nội dung thay đổi
            }
        })

        // Thiết lập listener cho 2 nhóm RadioGroup để đảm bảo chỉ chọn được 1 RadioButton trong tổng 6
        radioGroup1.setOnCheckedChangeListener(radioGroup1Listener)
        radioGroup2.setOnCheckedChangeListener(radioGroup2Listener)
    }

    // Listener cho nhóm RadioGroup 1
    // Khi chọn 1 RadioButton trong nhóm 1, nhóm 2 sẽ bị bỏ chọn
    private val radioGroup1Listener = RadioGroup.OnCheckedChangeListener { _, checkedId ->
        if (checkedId != -1) {
            // Tạm ngưng listener nhóm 2, bỏ chọn nhóm 2, rồi bật lại listener
            radioGroup2.setOnCheckedChangeListener(null)
            radioGroup2.clearCheck()
            radioGroup2.setOnCheckedChangeListener(radioGroup2Listener)
        }
        updateList()  // Cập nhật danh sách dựa trên lựa chọn mới
    }

    // Listener cho nhóm RadioGroup 2
    // Tương tự như nhóm 1, khi chọn 1 RadioButton trong nhóm 2 thì nhóm 1 bị bỏ chọn
    private val radioGroup2Listener: RadioGroup.OnCheckedChangeListener = RadioGroup.OnCheckedChangeListener { _, checkedId ->
        if (checkedId != -1) {
            radioGroup1.setOnCheckedChangeListener(null)
            radioGroup1.clearCheck()
            radioGroup1.setOnCheckedChangeListener(radioGroup1Listener)
        }
        updateList()  // Cập nhật danh sách dựa trên lựa chọn mới
    }

    // Hàm cập nhật danh sách số dựa trên số nhập và RadioButton được chọn
    private fun updateList() {
        val inputText = etNumber.text.toString()
        val maxNumber = inputText.toIntOrNull()  // Chuyển chuỗi nhập vào thành số nguyên, hoặc null nếu không hợp lệ

        // Nếu giá trị nhập không hợp lệ (null hoặc nhỏ hơn 1) thì xóa danh sách và hiện thông báo
        if (maxNumber == null || maxNumber < 1) {
            adapter.clear()
            tvNoResult.text = getString(R.string.no_valid_number)  // Thông báo "Không có số hợp lệ"
            tvNoResult.visibility = TextView.VISIBLE
            listView.visibility = ListView.GONE
            return
        }

        // Lấy id RadioButton được chọn trong 2 nhóm, ưu tiên nhóm 1 nếu có
        val selectedId = radioGroup1.checkedRadioButtonId.takeIf { it != -1 }
            ?: radioGroup2.checkedRadioButtonId

        // Nếu không có RadioButton nào được chọn thì cũng hiện thông báo lỗi
        if (selectedId == -1) {
            adapter.clear()
            tvNoResult.text = getString(R.string.no_valid_number)
            tvNoResult.visibility = TextView.VISIBLE
            listView.visibility = ListView.GONE
            return
        }

        // Tạo danh sách số tương ứng với lựa chọn RadioButton
        val numbers = when (selectedId) {
            R.id.rbOdd -> getOddNumbers(maxNumber)           // Số lẻ
            R.id.rbPrime -> getPrimeNumbers(maxNumber)       // Số nguyên tố
            R.id.rbPerfect -> getPerfectNumbers(maxNumber)   // Số hoàn hảo
            R.id.rbEven -> getEvenNumbers(maxNumber)         // Số chẵn
            R.id.rbSquare -> getSquareNumbers(maxNumber)     // Số chính phương
            R.id.rbFibonacci -> getFibonacciNumbers(maxNumber) // Số Fibonacci
            else -> emptyList()                               // Trường hợp khác (không xảy ra)
        }

        // Nếu danh sách rỗng thì hiện thông báo không có số hợp lệ
        if (numbers.isEmpty()) {
            tvNoResult.text = getString(R.string.no_valid_number)
            tvNoResult.visibility = TextView.VISIBLE
            listView.visibility = ListView.GONE
        } else {
            // Ngược lại, cập nhật adapter với danh sách mới và hiển thị ListView
            adapter.clear()
            adapter.addAll(numbers)
            adapter.notifyDataSetChanged()
            tvNoResult.visibility = TextView.GONE
            listView.visibility = ListView.VISIBLE
        }
    }

    // Hàm lấy danh sách số lẻ từ 1 đến max
    private fun getOddNumbers(max: Int): List<Int> = (1..max).filter { it % 2 != 0 }

    // Hàm lấy danh sách số chẵn từ 1 đến max
    private fun getEvenNumbers(max: Int): List<Int> = (1..max).filter { it % 2 == 0 }

    // Hàm lấy danh sách số nguyên tố từ 2 đến max
    private fun getPrimeNumbers(max: Int): List<Int> {
        val primes = mutableListOf<Int>()
        for (num in 2..max) {
            if (isPrime(num)) primes.add(num)
        }
        return primes
    }

    // Kiểm tra số nguyên tố: số > 1 và không chia hết cho số nào từ 2 đến căn bậc hai của nó
    private fun isPrime(n: Int): Boolean {
        if (n < 2) return false
        for (i in 2..Math.sqrt(n.toDouble()).toInt()) {
            if (n % i == 0) return false
        }
        return true
    }

    // Hàm lấy danh sách số hoàn hảo từ 2 đến max ( số hoàn hảo có tổng các ước trừ nó bằng chính nó)
    private fun getPerfectNumbers(max: Int): List<Int> {
        val perfects = mutableListOf<Int>()
        for (num in 2..max) {
            if (isPerfect(num)) perfects.add(num)
        }
        return perfects
    }

    // Kiểm tra số hoàn hảo: tổng các ước số (ngoại trừ chính nó) bằng chính số đó
    private fun isPerfect(n: Int): Boolean {
        var sum = 1  // 1 luôn là ước số của mọi số > 1
        for (i in 2..n / 2) {
            if (n % i == 0) sum += i
        }
        return n > 1 && sum == n
    }

    // Hàm lấy danh sách số chính phương (bình phương của số nguyên) đến max
    private fun getSquareNumbers(max: Int): List<Int> {
        val squares = mutableListOf<Int>()
        var i = 1
        while (i * i <= max) {
            squares.add(i * i)
            i++
        }
        return squares
    }

    // Hàm lấy danh sách số Fibonacci đến max
    private fun getFibonacciNumbers(max: Int): List<Int> {
        if (max < 1) return emptyList()
        val fibs = mutableListOf(1, 1)  // Bắt đầu dãy Fibonacci từ 1, 1
        while (true) {
            val next = fibs[fibs.size - 1] + fibs[fibs.size - 2]
            if (next > max) break
            fibs.add(next)
        }
        return fibs.filter { it <= max }
    }
}
