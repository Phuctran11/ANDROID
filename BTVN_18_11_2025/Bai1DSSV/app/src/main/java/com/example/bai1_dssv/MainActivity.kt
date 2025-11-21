package com.example.bai1_dssv

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// Data class lưu thông tin sinh viên: mã số sinh viên và tên
data class Student(
    var mssv: String,
    var name: String
)

class MainActivity : AppCompatActivity() {

    // Các view trong layout
    private lateinit var edtMSSV: EditText      // ô nhập MSSV
    private lateinit var edtName: EditText      // ô nhập tên sinh viên
    private lateinit var btnAdd: Button          // nút thêm sinh viên
    private lateinit var btnUpdate: Button       // nút cập nhật sinh viên
    private lateinit var rvStudents: RecyclerView  // danh sách sinh viên dạng RecyclerView

    private val studentList = mutableListOf<Student>()  // danh sách lưu trữ sinh viên
    private lateinit var adapter: StudentAdapter        // adapter cho RecyclerView

    private var selectedPosition: Int = -1  // vị trí sinh viên được chọn để cập nhật (-1 là chưa chọn)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Ánh xạ các view từ layout
        edtMSSV = findViewById(R.id.edtMSSV)
        edtName = findViewById(R.id.edtName)
        btnAdd = findViewById(R.id.btnAdd)
        btnUpdate = findViewById(R.id.btnUpdate)
        rvStudents = findViewById(R.id.rvStudents)

        // Khởi tạo adapter cho RecyclerView, truyền 2 lambda xử lý sự kiện: xóa và chọn item
        adapter = StudentAdapter(studentList,
            onDeleteClick = { position ->
                studentList.removeAt(position)          // xóa sinh viên tại vị trí
                adapter.notifyItemRemoved(position)    // thông báo adapter cập nhật
                clearInputs()                          // xóa dữ liệu nhập
                selectedPosition = -1                  // reset vị trí chọn
            },
            onItemClick = { position ->
                val student = studentList[position]
                edtMSSV.setText(student.mssv)          // hiển thị MSSV lên ô nhập
                edtName.setText(student.name)          // hiển thị tên lên ô nhập
                selectedPosition = position             // lưu vị trí sinh viên được chọn
            }
        )
        rvStudents.layoutManager = LinearLayoutManager(this)  // thiết lập layout cho RecyclerView
        rvStudents.adapter = adapter                           // gán adapter cho RecyclerView

        // Xử lý sự kiện nút Add
        btnAdd.setOnClickListener {
            val mssv = edtMSSV.text.toString().trim()
            val name = edtName.text.toString().trim()

            if (mssv.isEmpty()) {
                edtMSSV.error = "Vui lòng nhập MSSV"   // kiểm tra nhập MSSV
                return@setOnClickListener
            }
            if (name.isEmpty()) {
                edtName.error = "Vui lòng nhập Họ tên"  // kiểm tra nhập tên
                return@setOnClickListener
            }

            // Kiểm tra MSSV đã tồn tại trong danh sách chưa
            val exists = studentList.any { it.mssv == mssv }
            if (exists) {
                Toast.makeText(this, "MSSV đã tồn tại", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Thêm sinh viên mới vào danh sách
            studentList.add(Student(mssv, name))
            adapter.notifyItemInserted(studentList.size - 1)  // cập nhật adapter
            clearInputs()                                      // xóa dữ liệu nhập
            selectedPosition = -1                              // reset vị trí chọn
        }

        // Xử lý sự kiện nút Update
        btnUpdate.setOnClickListener {
            if (selectedPosition == -1) {
                Toast.makeText(this, "Vui lòng chọn sinh viên để cập nhật", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val mssv = edtMSSV.text.toString().trim()
            val name = edtName.text.toString().trim()

            if (mssv.isEmpty()) {
                edtMSSV.error = "Vui lòng nhập MSSV"
                return@setOnClickListener
            }
            if (name.isEmpty()) {
                edtName.error = "Vui lòng nhập Họ tên"
                return@setOnClickListener
            }

            // Kiểm tra MSSV có trùng với sinh viên khác (ngoại trừ vị trí đang chọn) không
            val exists = studentList.anyIndexed { index, student ->
                student.mssv == mssv && index != selectedPosition
            }
            if (exists) {
                Toast.makeText(this, "MSSV đã tồn tại ở sinh viên khác", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Cập nhật thông tin sinh viên tại vị trí đã chọn
            val student = studentList[selectedPosition]
            student.mssv = mssv
            student.name = name
            adapter.notifyItemChanged(selectedPosition)  // cập nhật adapter
            clearInputs()
            selectedPosition = -1
        }
    }

    // Xóa dữ liệu nhập trên 2 ô EditText
    private fun clearInputs() {
        edtMSSV.setText("")
        edtName.setText("")
    }

    // Extension function: kiểm tra điều kiện với index (tương tự any nhưng có index)
    private inline fun <T> Iterable<T>.anyIndexed(predicate: (index: Int, T) -> Boolean): Boolean {
        var index = 0
        for (element in this) {
            if (predicate(index, element)) return true
            index++
        }
        return false
    }

    // Adapter cho RecyclerView hiển thị danh sách sinh viên
    inner class StudentAdapter(
        private val students: MutableList<Student>,      // danh sách sinh viên
        private val onDeleteClick: (Int) -> Unit,        // callback khi xóa item
        private val onItemClick: (Int) -> Unit            // callback khi chọn item
    ) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

        inner class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvName: TextView = itemView.findViewById(R.id.tvName)      // TextView hiển thị tên
            val tvMSSV: TextView = itemView.findViewById(R.id.tvMSSV)      // TextView hiển thị MSSV
            val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)  // nút xóa

            init {
                itemView.setOnClickListener {
                    onItemClick(adapterPosition)    // gọi callback khi chọn item
                }
                btnDelete.setOnClickListener {
                    onDeleteClick(adapterPosition)  // gọi callback khi nhấn xóa
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_student, parent, false)  // inflate layout item
            return StudentViewHolder(view)
        }

        override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
            val student = students[position]
            holder.tvName.text = student.name    // gán tên sinh viên
            holder.tvMSSV.text = student.mssv    // gán MSSV sinh viên
        }

        override fun getItemCount(): Int = students.size   // trả về số lượng item
    }
}
