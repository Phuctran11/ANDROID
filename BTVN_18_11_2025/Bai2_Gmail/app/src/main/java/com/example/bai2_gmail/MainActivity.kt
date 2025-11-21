package com.example.bai2_gmail

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

// Mô hình dữ liệu cho 1 email
data class Email(
    val senderInitial: String,
    val senderName: String,
    val subject: String,
    val snippet: String,
    val time: String,
    var isFavorite: Boolean = false
)

// Activity chính hiển thị danh sách email
class MainActivity : AppCompatActivity() {

    // RecyclerView chứa danh sách email
    private lateinit var rvEmails: RecyclerView

    // Nút soạn thư nổi
    private lateinit var fabCompose: FloatingActionButton

    // Icon tìm kiếm trên toolbar (ImageView theo activity_main.xml)
    private lateinit var ivSearch: ImageView

    // Ô nhập tìm kiếm (ẩn theo mặc định)
    private lateinit var etSearch: EditText

    // Dữ liệu email và adapter
    private val emailList = mutableListOf<Email>()
    private lateinit var adapter: EmailAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Khởi tạo view references
        rvEmails = findViewById(R.id.rvEmails)
        fabCompose = findViewById(R.id.fabCompose)
        ivSearch = findViewById(R.id.ivSearch)
        etSearch = findViewById(R.id.etSearch)

        // Thêm dữ liệu mẫu
        populateEmailList()

        // Thiết lập RecyclerView + Adapter
        adapter = EmailAdapter(emailList)
        rvEmails.layoutManager = LinearLayoutManager(this)
        rvEmails.adapter = adapter

        // Sự kiện nút soạn và tìm
        fabCompose.setOnClickListener { handleComposeEmail() }
        ivSearch.setOnClickListener { handleSearchEmail() }

        // Đặt IME action để sự kiện Enter/Done là "search"
        etSearch.imeOptions = EditorInfo.IME_ACTION_SEARCH
    }

    // Thêm dữ liệu mẫu vào danh sách
    private fun populateEmailList() {
        emailList.addAll(
            listOf(
                Email("E", "Edurila.com", "$19 Only (First 10 spots) - Bestselling...",
                    "Are you looking to Learn Web Designin...", "12:34 PM"),
                Email("C", "Chris Abad", "Help make Campaign Monitor better",
                    "Let us know your thoughts! No Images...", "11:22 AM"),
                Email("E", "Edurila.com", "$19 Only (First 10 spots) - Bestselling...",
                    "Are you looking to Learn Web Designin...", "12:34 PM"),
                Email("C", "Chris Abad", "Help make Campaign Monitor better",
                    "Let us know your thoughts! No Images...", "11:22 AM"),
                Email("E", "Edurila.com", "$19 Only (First 10 spots) - Bestselling...",
                    "Are you looking to Learn Web Designin...", "12:34 PM"),
                Email("C", "Chris Abad", "Help make Campaign Monitor better",
                    "Let us know your thoughts! No Images...", "11:22 AM"),
                Email("E", "Edurila.com", "$19 Only (First 10 spots) - Bestselling...",
                    "Are you looking to Learn Web Designin...", "12:34 PM"),
                Email("C", "Chris Abad", "Help make Campaign Monitor better",
                    "Let us know your thoughts! No Images...", "11:22 AM"),
                Email("E", "Edurila.com", "$19 Only (First 10 spots) - Bestselling...",
                    "Are you looking to Learn Web Designin...", "12:34 PM"),
                Email("C", "Chris Abad", "Help make Campaign Monitor better",
                    "Let us know your thoughts! No Images...", "11:22 AM")
            )
        )
    }

    // Xử lý khi nhấn nút soạn (hiện để triển khai)
    private fun handleComposeEmail() {
        // TODO: mở activity soạn thư
    }

    // Hiển thị ô tìm kiếm, mở bàn phím và xử lý action search
    private fun handleSearchEmail() {
        etSearch.visibility = View.VISIBLE
        etSearch.requestFocus()

        // Hiện bàn phím
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(etSearch, InputMethodManager.SHOW_IMPLICIT)

        // Lắng nghe action trên bàn phím (Search / Done)
        etSearch.setOnEditorActionListener { _, actionId, event ->
            val isEnterKey = event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || isEnterKey) {
                val query = etSearch.text.toString().trim()
                searchEmailBySenderName(query)

                // Ẩn bàn phím và reset input
                imm.hideSoftInputFromWindow(etSearch.windowToken, 0)
                etSearch.visibility = View.GONE
                etSearch.setText("")
                etSearch.clearFocus()
                true
            } else {
                false
            }
        }
    }

    // Gọi adapter để lọc theo tên người gửi
    private fun searchEmailBySenderName(searchText: String) {
        adapter.filter(searchText)
    }


    // ADAPTER
    inner class EmailAdapter(private val fullList: List<Email>) :
        RecyclerView.Adapter<EmailAdapter.EmailViewHolder>() {

        // Danh sách hiển thị (sẽ thay đổi khi lọc)
        private val displayList = mutableListOf<Email>().apply {
            addAll(fullList)
        }

        // ViewHolder chứa references tới view trong item_email.xml
        inner class EmailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvInitial: TextView = itemView.findViewById(R.id.tvInitial)
            val tvSenderName: TextView = itemView.findViewById(R.id.tvSenderName)
            val tvSubject: TextView = itemView.findViewById(R.id.tvSubject)
            val tvSnippet: TextView = itemView.findViewById(R.id.tvSnippet)
            val tvTime: TextView = itemView.findViewById(R.id.tvTime)
            val btnStar: ImageButton = itemView.findViewById(R.id.btnStar)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmailViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_email, parent, false)
            return EmailViewHolder(view)
        }

        override fun onBindViewHolder(holder: EmailViewHolder, position: Int) {
            val email = displayList[position]
            bindEmailData(holder, email)

            // Toggle favorite khi nhấn nút sao
            holder.btnStar.setOnClickListener {
                toggleEmailFavorite(email, holder)
            }
        }

        override fun getItemCount(): Int = displayList.size

        // Lọc danh sách theo văn bản (tên người gửi), không phân biệt hoa thường
        fun filter(text: String) {
            val query = text.trim()
            displayList.clear()

            if (query.isEmpty()) {
                displayList.addAll(fullList)
            } else {
                displayList.addAll(
                    fullList.filter { it.senderName.contains(query, ignoreCase = true) }
                )
            }

            notifyDataSetChanged()
        }

        // Gán dữ liệu email cho views trong item
        private fun bindEmailData(holder: EmailViewHolder, email: Email) {
            holder.tvInitial.text = email.senderInitial
            holder.tvSenderName.text = email.senderName
            holder.tvSubject.text = email.subject
            holder.tvSnippet.text = email.snippet
            holder.tvTime.text = email.time

            holder.btnStar.setImageResource(
                if (email.isFavorite) R.drawable.ic_star_filled
                else R.drawable.ic_star_outline
            )
        }

        // Đổi trạng thái favorite và cập nhật UI + toast
        private fun toggleEmailFavorite(email: Email, holder: EmailViewHolder) {
            email.isFavorite = !email.isFavorite
            holder.btnStar.setImageResource(
                if (email.isFavorite) R.drawable.ic_star_filled
                else R.drawable.ic_star_outline
            )

            Toast.makeText(
                this@MainActivity,
                if (email.isFavorite) "Email đã được gắn dấu sao" else "Email đã bỏ gắn dấu sao",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
