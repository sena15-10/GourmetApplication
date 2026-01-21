package com.example.gourmetsearchapplication

import android.os.Bundle
import android.util.Log
import android.widget.EditText
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

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: RestaurantAdapter
    private val shopList = mutableListOf<Shop>()

    companion object {
        private const val DEBUG_TAG = "GourmetSearch"
        private const val API_KEY = "e162d3653b8b0b5a"
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
        setupFab()

        // 初期表示用のダミーデータ
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
                searchRestaurants(keyword)
            }
        }
    }

    private fun setupFab() {
        val fab = findViewById<FloatingActionButton>(R.id.fabLocation)
        fab.setOnClickListener {
            Log.d(DEBUG_TAG, "現在地取得ボタンが押されました")
            // ここにGPS連携処理を追加予定
        }
    }

    private fun searchRestaurants(keyword: String) {
        Log.d(DEBUG_TAG, "検索キーワード: $keyword")
        
        // コルーチンを使用して非同期処理を行う準備
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // 通信処理のシミュレーション（あとで実API通信に差し替え）
                val results = withContext(Dispatchers.IO) {
                    // ここでAPI通信を行う予定
                    fetchFromApi(keyword)
                }
                adapter.updateData(results)
            } catch (e: Exception) {
                Log.e(DEBUG_TAG, "検索エラー", e)
            }
        }
    }

    private fun fetchFromApi(keyword: String): List<Shop> {
        // API通信の代わりにダミーの結果を返す
        return listOf(
            Shop("$keyword 店", "キーワード「$keyword」の検索結果です", "https://picsum.photos/800/400?random=3", "和食", "1.2km", "東京都...", "〇〇駅 徒歩5分")
        )
    }

    private fun loadInitialData() {
        val dummyList = listOf(
            Shop("サンプルイタリアン", "絶品パスタとワインが楽しめるお店です。", "https://picsum.photos/800/400?random=1", "イタリアン", "300m", "東京都中央区銀座1-2-3", "銀座駅 徒歩3分"),
            Shop("居酒屋 ほっと", "鮮度抜群の刺身と地酒。個室完備。", "https://picsum.photos/800/400?random=2", "居酒屋", "500m", "東京都中央区築地4-5-6", "築地駅 徒歩5分")
        )
        adapter.updateData(dummyList)
    }
}