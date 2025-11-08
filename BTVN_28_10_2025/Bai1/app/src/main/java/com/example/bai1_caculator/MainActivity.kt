package com.example.bai1_caculator

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var tvDisplay: TextView         // Hiển thị số hiện tại
    private lateinit var tvExpression: TextView      // Hiển thị biểu thức đang tính (ví dụ: "55 +")

    // Biến lưu trạng thái tính toán
    private var currentInput: String = "0"  // Toán hạng hiện tại đang nhập (chuỗi)
    private var operand1: Int? = null       // Toán hạng thứ nhất
    private var operator: Char? = null      // Phép toán đang chọn: '+', '-', '*', '/'

    private var errorMessage: String? = null // Thong bao loi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Ánh xạ view
        tvDisplay = findViewById(R.id.tvDisplay)
        tvExpression = findViewById(R.id.tvExpression)

        updateDisplay()
        updateExpression()

        // Gán sự kiện cho các nút số
        val numberButtons = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3,
            R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7,
            R.id.btn8, R.id.btn9
        )
        for (id in numberButtons) {
            findViewById<Button>(id).setOnClickListener {
                onNumberPressed((it as Button).text.toString())
            }
        }

        // Nút các phép toán
        findViewById<Button>(R.id.btnAdd).setOnClickListener { onOperatorPressed('+') }
        findViewById<Button>(R.id.btnSubtract).setOnClickListener { onOperatorPressed('-') }
        findViewById<Button>(R.id.btnMultiply).setOnClickListener { onOperatorPressed('*') }
        findViewById<Button>(R.id.btnDivide).setOnClickListener { onOperatorPressed('/') }

        // Nút BS (Backspace) - xóa chữ số hàng đơn vị
        findViewById<Button>(R.id.btnBS).setOnClickListener { onBackspacePressed() }

        // Nút CE - xóa toán hạng hiện tại về 0
        findViewById<Button>(R.id.btnCE).setOnClickListener { onClearEntryPressed() }

        // Nút C - xóa toàn bộ phép toán
        findViewById<Button>(R.id.btnC).setOnClickListener { onClearPressed() }

        // Nút =
        findViewById<Button>(R.id.btnEquals).setOnClickListener { onEqualsPressed() }

        // Nút +/- đổi dấu
        findViewById<Button>(R.id.btnPlusMinus).setOnClickListener { onPlusMinusPressed() }

    }

    // Xử lý chuỗi input
    private fun onNumberPressed(number: String) {
        if (currentInput == "0") {
            currentInput = number // không hiển thị số 0 ở đầu input
        } else {
            currentInput += number //
        }
        updateDisplay()
    }

    // Xử lý nhập phép toán
    private fun onOperatorPressed(op: Char) {
        if (operator != null && operand1 != null) {
            val operand2 = currentInput.toIntOrNull() ?: 0
            val result = calculate(operand1!!, operand2, operator!!)
            if (errorMessage != null) {
                // Có lỗi, hiển thị lỗi, không thay đổi toán hạng và phép toán
                tvDisplay.text = errorMessage
                errorMessage = null
                return
            }
            operand1 = result
            currentInput = "0"
            operator = op
            updateDisplayWithResult(result)
        } else {
            operand1 = currentInput.toIntOrNull() ?: 0
            operator = op
            currentInput = "0"
            updateDisplay()
        }
        updateExpression()
    }


    // Xử lý nút Backspace (BS): xóa chữ số cuối của toán hạng hiện tại
    private fun onBackspacePressed() {
        if (currentInput.length > 1) {
            currentInput = currentInput.dropLast(1)
        } else {
            currentInput = "0"
        }
        updateDisplay()
    }

    // Xử lý nút CE - xóa toán hạng hiện tại về 0
    private fun onClearEntryPressed() {
        currentInput = "0"
        updateDisplay()
    }

    // Xử lý nút C - xóa toàn bộ phép toán, nhập lại từ đầu
    private fun onClearPressed() {
        currentInput = "0"
        operand1 = null
        operator = null
        updateDisplay()
        updateExpression()
    }

    // Xử lý nút =
    private fun onEqualsPressed() {
        if (operator != null && operand1 != null) {
            val operand2 = currentInput.toIntOrNull() ?: 0
            val result = calculate(operand1!!, operand2, operator!!)
            if (errorMessage != null) {
                // Hiển thị lỗi chia cho 0
                tvDisplay.text = errorMessage
                errorMessage = null
                return
            }
            currentInput = result.toString()
            operand1 = null
            operator = null
            updateDisplayWithResult(result)
            updateExpression()
        }
    }

    // Xử lý nút +/- đổi dấu toán hạng hiện tại
    private fun onPlusMinusPressed() {
        if (currentInput != "0") {
            currentInput = if (currentInput.startsWith("-")) {
                currentInput.removePrefix("-") // nếu đang là số âm
            } else {
                "-$currentInput" // nếu đang là số dương
            }
            updateDisplay()
        }
    }

    // Hàm tính toán 2 toán hạng với phép toán
    private fun calculate(op1: Int, op2: Int, oper: Char): Int {
        return when (oper) {
            '+' -> op1 + op2
            '-' -> op1 - op2
            '*' -> op1 * op2
            '/' -> if (op2 != 0) {
                op1 / op2
            } else {
                errorMessage = getString(R.string.error_divide_by_zero)
                0  // Trả về 0 tạm, nhưng sẽ không dùng vì có lỗi
            }
            else -> 0
        }
    }

    // Cập nhật TextView hiển thị số hiện tại
    private fun updateDisplay() {
        tvDisplay.text = currentInput
    }

    // Cập nhật TextView hiển thị kết quả tính toán
    private fun updateDisplayWithResult(result: Int) {
        tvDisplay.text = result.toString()
    }

    // Cập nhật TextView hiển thị biểu thức đang tính
    private fun updateExpression() {
        if (operand1 != null && operator != null) {
            tvExpression.text = "${operand1.toString()} $operator"
        } else {
            tvExpression.text = ""  // Không hiển thị khi chưa có phép toán
        }
    }
}
