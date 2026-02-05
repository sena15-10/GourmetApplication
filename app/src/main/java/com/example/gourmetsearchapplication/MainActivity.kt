package com.example.gourmetsearchapplication

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: RestaurantAdapter
    private val shopList = mutableListOf<Shop>()

    // ページネーション管理用の変数
    private var currentStart = 1
    private val PAGE_COUNT = 20
    private var totalCount = 0
    private var currentKeyword = ""
//ここでAPIから届いた「文字列」を解析してHotPepper.ktの構造をじっくり見て、パズルのように当てはめていくものである。
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://webservice.recruit.co.jp")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(HotPepperApiService::class.java)

    companion object {
        private const val DEBUG_TAG = "GourmetSearch"
        private const val API_KEY = "e162d3653b8b0b5a" // ホットペッパーのAPIキー
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerView()
        setupSearch()
        setupPagination()
        setupFab()

        loadInitialData()
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.rvShop)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = RestaurantAdapter(shopList)
        recyclerView.adapter = adapter
    }

    private fun setupSearch() {
        val etSearch = findViewById<EditText>(R.id.etSearch)
        val btnSearch = findViewById<MaterialButton>(R.id.btnSearch)

        btnSearch.setOnClickListener {
            val keyword = etSearch.text.toString()
            if (keyword.isNotEmpty()) {
                currentKeyword = keyword
                currentStart = 1 // 新規検索時は1ページ目から
                searchRestaurants(currentKeyword, currentStart)
            }
        }
    }

    private fun setupPagination() {
        val btnPrev = findViewById<MaterialButton>(R.id.btnPrev)
        val btnNext = findViewById<MaterialButton>(R.id.btnNext)

        btnPrev.setOnClickListener {
            if (currentStart > 1) {
                currentStart -= PAGE_COUNT
                if (currentStart < 1) currentStart = 1
                searchRestaurants(currentKeyword, currentStart)
            }
        }

        btnNext.setOnClickListener {
            if (currentStart + PAGE_COUNT <= totalCount) {
                // 現在の開始位置(1)に 20 を足して 21 に更新する
                currentStart += PAGE_COUNT
                // 21番目から取得してね！と命令を出す
                searchRestaurants(currentKeyword, currentStart)
            }
        }
    }

    private fun updatePaginationUi() {
        val btnPrev = findViewById<MaterialButton>(R.id.btnPrev)
        val btnNext = findViewById<MaterialButton>(R.id.btnNext)
        val tvPageInfo = findViewById<TextView>(R.id.tvPageInfo)

        // 「前へ」ボタン：1ページ目なら無効
        btnPrev.isEnabled = currentStart > 1

        // 「次へ」ボタン：次のデータがなければ無効
        btnNext.isEnabled = (currentStart + PAGE_COUNT) <= totalCount

        // ページ情報の表示 (例: 1-20 / 150)
        val endRange = if (currentStart + PAGE_COUNT - 1 > totalCount) totalCount else currentStart + PAGE_COUNT - 1
        if (totalCount > 0) {
            tvPageInfo.text = "$currentStart - $endRange / $totalCount"
        } else {
            tvPageInfo.text = "0 / 0"
        }
    }

    private fun setupFab() {
        val fab = findViewById<FloatingActionButton>(R.id.fabLocation)
        fab.setOnClickListener {
            Log.d(DEBUG_TAG, "現在地取得ボタンが押されました")
        }
    }

    private fun searchRestaurants(keyword: String, start: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiService.searchShops(API_KEY, keyword, start = start, count = PAGE_COUNT)
                }

                totalCount = response.results.totalAvailable
                
                val shops = response.results.shops.map { item ->
                    Shop(
                        name = item.name,
                        catchCopy = item.catchCopy,
                        imageUrl = item.photo.pc.large,
                        category = item.genre.name,
                        distance = "",
                        address = item.address,
                        access = item.access
                    )
                }

                adapter.updateData(shops)
                updatePaginationUi()
                
                // リストの先頭へスクロール
                findViewById<RecyclerView>(R.id.rvShop).scrollToPosition(0)

            } catch (e: Exception) {
                Log.e(DEBUG_TAG, "検索エラーが発生しました", e)
            }
        }
    }

    private fun loadInitialData() {
        // 初期状態は空かサンプルを表示
        updatePaginationUi()
    }
}
