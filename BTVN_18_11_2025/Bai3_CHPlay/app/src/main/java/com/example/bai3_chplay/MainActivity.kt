package com.example.bai3_chplay

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {

    // Thanh tab trên cùng
    private lateinit var tabLayout: TabLayout

    // RecyclerView danh sách đề xuất (dọc)
    private lateinit var rvSuggested: RecyclerView

    // RecyclerView danh sách đề nghị (ngang)
    private lateinit var rvRecommended: RecyclerView

    // Thanh điều hướng dưới cùng
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Ánh xạ view từ layout
        tabLayout = findViewById(R.id.tabLayout)
        rvSuggested = findViewById(R.id.rvSuggested)
        rvRecommended = findViewById(R.id.rvRecommended)
        bottomNav = findViewById(R.id.bottomNav)

        // Thiết lập các thành phần giao diện
        setupTabs()
        setupSuggestedList()
        setupRecommendedList()
        setupBottomNav()
    }

    // Khởi tạo các tab trong TabLayout
    private fun setupTabs() {
        val tabs = listOf("For you", "Top charts", "Other devices", "Kids")
        for (t in tabs) tabLayout.addTab(tabLayout.newTab().setText(t))
    }

    // Thiết lập RecyclerView danh sách đề xuất (dọc)
    private fun setupSuggestedList() {
        // Dữ liệu mẫu: cặp tiêu đề và mô tả dung lượng
        val data = (1..12).map { i ->
            Pair("Mech Assemble: Zombie $i", "Action · Role Playing · ${100 + i} MB")
        }

        // LayoutManager dọc cho RecyclerView
        rvSuggested.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        // Tính chiều cao tổng và chiều cao mỗi item
        val totalHeight = dpToPx(350)
        val itemsPerScreen = 3
        val itemSpacing = dpToPx(12)
        val totalSpacing = itemSpacing * (itemsPerScreen - 1)
        val itemHeight = (totalHeight - totalSpacing) / itemsPerScreen

        val itemWidth = ViewGroup.LayoutParams.MATCH_PARENT

        // Gán adapter với thông số kích thước và khoảng cách item
        rvSuggested.adapter = SuggestedAdapter(data, itemWidth, itemHeight, itemSpacing, isVertical = true)
        rvSuggested.setHasFixedSize(true)
        rvSuggested.updateLayoutParams { height = totalHeight }
    }

    // Thiết lập RecyclerView danh sách đề nghị (ngang)
    private fun setupRecommendedList() {
        val icons = (1..8).map { "App $it" }
        rvRecommended.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvRecommended.adapter = RecommendedAdapter(icons)
        rvRecommended.setHasFixedSize(true)
        rvRecommended.updateLayoutParams { height = dpToPx(120) }
    }

    // Thiết lập BottomNavigationView với menu và sự kiện chọn
    private fun setupBottomNav() {
        val menu = bottomNav.menu
        menu.clear()
        menu.add(0, R.id.nav_games, 0, "Games").setIcon(android.R.drawable.ic_menu_compass)
        menu.add(0, R.id.nav_apps, 1, "Apps").setIcon(android.R.drawable.ic_menu_gallery)
        menu.add(0, R.id.nav_search, 2, "Search").setIcon(android.R.drawable.ic_menu_search)
        menu.add(0, R.id.nav_books, 3, "Books").setIcon(android.R.drawable.ic_menu_agenda)

        bottomNav.selectedItemId = R.id.nav_apps

        // Xử lý sự kiện chọn menu
        bottomNav.setOnNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.nav_games -> true
                R.id.nav_apps -> true
                R.id.nav_search -> true
                R.id.nav_books -> true
                else -> false
            }
        }
    }

    // Adapter cho danh sách đề xuất
    inner class SuggestedAdapter(
        private val items: List<Pair<String, String>>, // Dữ liệu tiêu đề và mô tả
        private val itemWidthPx: Int,                  // Chiều rộng item
        private val itemHeightPx: Int,                 // Chiều cao item
        private val itemSpacingPx: Int,                 // Khoảng cách giữa các item
        private val isVertical: Boolean = false         // Kiểu dọc hay ngang
    ) : RecyclerView.Adapter<SuggestedAdapter.SuggestVH>() {

        // ViewHolder cho item đề xuất
        inner class SuggestVH(val root: LinearLayout) : RecyclerView.ViewHolder(root) {
            val icon: ImageView = ImageView(root.context)    // Icon ứng dụng
            val title: TextView = TextView(root.context)     // Tiêu đề
            val subtitle: TextView = TextView(root.context)  // Mô tả phụ

            init {
                // Cấu hình layout root ngang, căn giữa theo chiều dọc
                root.orientation = LinearLayout.HORIZONTAL
                root.setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8))
                root.gravity = Gravity.CENTER_VERTICAL

                // Cấu hình icon kích thước 56dp x 56dp, nền xám nhạt
                val iconLp = LinearLayout.LayoutParams(dpToPx(56), dpToPx(56))
                icon.layoutParams = iconLp
                icon.setBackgroundColor(Color.LTGRAY)
                icon.scaleType = ImageView.ScaleType.CENTER_CROP
                root.addView(icon)

                // Layout chứa 2 TextView dọc
                val texts = LinearLayout(root.context).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(dpToPx(8), 0, 0, 0)
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                }
                // Cấu hình tiêu đề và mô tả
                title.setTextColor(Color.BLACK)
                title.textSize = 13f
                subtitle.setTextColor(Color.DKGRAY)
                subtitle.textSize = 12f
                texts.addView(title)
                texts.addView(subtitle)
                root.addView(texts)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestVH {
            // Tạo container LinearLayout cho từng item
            val container = LinearLayout(parent.context)
            val lp = if (isVertical) {
                RecyclerView.LayoutParams(itemWidthPx, itemHeightPx)
            } else {
                RecyclerView.LayoutParams(itemWidthPx, ViewGroup.LayoutParams.MATCH_PARENT)
            }
            val half = itemSpacingPx / 2
            if (isVertical) {
                lp.setMargins(0, half, 0, half) // Margin trên dưới cho dọc
            } else {
                lp.setMargins(half, dpToPx(6), half, dpToPx(6)) // Margin trái phải cho ngang
            }
            container.layoutParams = lp
            container.setBackgroundColor(Color.WHITE)
            container.elevation = dpToPx(2).toFloat() // Đổ bóng nhẹ
            return SuggestVH(container)
        }

        override fun onBindViewHolder(holder: SuggestVH, position: Int) {
            val (t, s) = items[position]
            holder.title.text = t          // Gán tiêu đề
            holder.subtitle.text = s       // Gán mô tả
        }

        override fun getItemCount(): Int = items.size   // Số lượng item
    }

    // Adapter cho danh sách đề nghị
    inner class RecommendedAdapter(private val items: List<String>) :
        RecyclerView.Adapter<RecommendedAdapter.RecVH>() {

        // ViewHolder cho item đề nghị
        inner class RecVH(root: LinearLayout) : RecyclerView.ViewHolder(root) {
            val icon: ImageView = ImageView(root.context)  // Icon ứng dụng
            val label: TextView = TextView(root.context)   // Nhãn tên ứng dụng

            init {
                // Layout dọc, căn giữa
                root.orientation = LinearLayout.VERTICAL
                root.gravity = Gravity.CENTER
                root.setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8))

                // Cấu hình icon 72dp x 72dp, nền xám nhạt
                val iconLp = LinearLayout.LayoutParams(dpToPx(72), dpToPx(72))
                icon.layoutParams = iconLp
                icon.setBackgroundColor(Color.LTGRAY)
                icon.scaleType = ImageView.ScaleType.CENTER_CROP
                root.addView(icon)

                // Cấu hình label bên dưới icon
                label.textSize = 12f
                label.setTextColor(Color.BLACK)
                val lblLp = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                lblLp.topMargin = dpToPx(6)
                root.addView(label, lblLp)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecVH {
            // Tạo container LinearLayout cho từng item
            val root = LinearLayout(parent.context)
            val lp = RecyclerView.LayoutParams(dpToPx(100), ViewGroup.LayoutParams.WRAP_CONTENT)
            lp.setMargins(dpToPx(8), dpToPx(6), dpToPx(8), dpToPx(6))
            root.layoutParams = lp
            return RecVH(root)
        }

        override fun onBindViewHolder(holder: RecVH, position: Int) {
            holder.label.text = items[position]   // Gán tên ứng dụng
        }

        override fun getItemCount(): Int = items.size   // Số lượng item
    }

    // Chuyển đổi dp sang pixel theo mật độ màn hình
    private fun dpToPx(dp: Int): Int =
        (dp * resources.displayMetrics.density + 0.5f).toInt()
}
