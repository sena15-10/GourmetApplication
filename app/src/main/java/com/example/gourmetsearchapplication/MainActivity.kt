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
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: RestaurantAdapter
    private val shopList = mutableListOf<Shop>()

    // 1. API接続の設定（Retrofit：レトロフィット）
    // baseUrl: 接続先の住所です（wevservice を webservice に修正しました）
    // addConverterFactory: 届いたデータ(JSON)を、Kotlinのクラスに自動で変換する設定です
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://webservice.recruit.co.jp")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // 2. 作成した設定をもとに、通信を行うための「窓口（apiService）」を作成します
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
        setupFab()

        // アプリ起動時に、まずサンプルデータを表示しておきます
        loadInitialData()
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.rvShop)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = RestaurantAdapter(shopList)
        recyclerView.adapter = adapter
    }

    // 検索ボタンを押した時の処理を決めるメソッド
    private fun setupSearch() {
        val etSearch = findViewById<EditText>(R.id.etSearch)
        val btnSearch = findViewById<MaterialButton>(R.id.btnSearch)

        btnSearch.setOnClickListener {
            val keyword = etSearch.text.toString()
            if (keyword.isNotEmpty()) {
                // 入力された言葉を使って検索を開始します
                searchRestaurants(keyword)
            }
        }
    }

    private fun setupFab() {
        val fab = findViewById<FloatingActionButton>(R.id.fabLocation)
        fab.setOnClickListener {
            Log.d(DEBUG_TAG, "現在地取得ボタンが押されました")
        }
    }

    /**
     * 検索の「全体的な流れ」を制御するメソッドです。
     * 通信は時間がかかるので、CoroutineScopeを使って裏側で実行します。
     */
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

                // 通信が終わって結果が届いたら、アダプターに渡してリストを更新します
                adapter.updateData(results)
            } catch (e: Exception) {
                // 通信エラーなどが起きた場合に、ログを表示します
                Log.e(DEBUG_TAG, "検索エラーが発生しました", e)
            }
        }
    }

    /**
     * 実際にインターネットを通じて、ホットペッパーのサーバーからデータを取ってくるメソッドです。
     * 「suspend」をつけることで、通信待ちができるようになります。
     */
    private suspend fun fetchFromApi(keyword: String): List<Shop> {
        // apiServiceを使って、キーワードに合うお店を検索します
        val response = apiService.searchShops(API_KEY, keyword)

        // APIから返ってきた複雑なデータ（response.results.shops）を、
        // アプリの画面で使いやすい形式（Shopクラス）に１つずつ変換（map）します
        val shops = response.results.shops.map { item ->
            Shop(
                name = item.name,                  // 店名
                catchCopy = item.catchCopy,        // キャッチコピー
                imageUrl = item.photo.pc.large,    // お店の写真URL
                category = item.genre.name,        // ジャンル（イタリアンなど）
                distance = "不明",                  // ※APIから取得できないため固定
                address = item.address,            // 住所
                access = item.access               // アクセス方法
            )
        }
        return shops
    }

    // 最初に見せるダミーデータです
    private fun loadInitialData() {
        val dummyList = listOf(
            Shop("サンプルイタリアン", "絶品パスタとワインが楽しめるお店です。", "https://picsum.photos/800/400?random=1", "イタリアン", "300m", "東京都中央区銀座1-2-3", "銀座駅 徒歩3分"),
            Shop("居酒屋 ほっと", "鮮度抜群の刺身と地酒。個室完備。", "https://picsum.photos/800/400?random=2", "居酒屋", "500m", "東京都中央区築地4-5-6", "築地駅 徒歩5分")
        )
        adapter.updateData(dummyList)
    }
}
