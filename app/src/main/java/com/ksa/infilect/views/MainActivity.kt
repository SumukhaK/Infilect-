package com.ksa.infilect.views

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import com.ksa.foody.util.NetworkListener
import com.ksa.foody.util.NetworkResult
import com.ksa.infilect.adapter.CardStackAdapter
import com.ksa.infilect.viewmodels.MainViewModel
import com.ksa.infilect.databinding.ActivityMainBinding
import com.ksa.infilect.models.Result
import com.ksa.infilect.util.observeOnce
import com.yuyakaido.android.cardstackview.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.Exception

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() , CardStackListener {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var  binding : ActivityMainBinding
    private val manager by lazy { CardStackLayoutManager(this, this) }
    private val adapter by lazy { CardStackAdapter() }
    private var randomUsersList = emptyList<Result>()
    private lateinit var networkListener: NetworkListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        setupCardStackView()
        setupButton()

        lifecycleScope.launchWhenStarted {
            networkListener = NetworkListener()
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                networkListener.checkNetworkAvailability(this@MainActivity).collect{ status ->
                    Log.v("NetworkListener",status.toString())
                    mainViewModel.networkStatus = status
                    mainViewModel.showNetworkStatus()
                    if(status){
                        getAllUsersFromApi()
                    }else{
                        readDb()
                    }
                }
            }
        }

        //getAllUsersFromApi()
    }

    private fun readDb() {
        mainViewModel.readUsers.observeOnce(this) { database ->
            if (database.isNotEmpty()) {
                Log.v("READDATA", "database.isNotEmpty")
                randomUsersList = database[0].users.results
                adapter.setData(database[0].users)
            } else {
                Log.v("READDATA", "database.isEmpty")
                getAllUsersFromApi()
            }
        }
    }


    private fun getAllUsersFromApi(){
        mainViewModel.getRandomUsers("10")
        mainViewModel.randomUsersResponse.observe(this,{response ->
            when(response){
                is NetworkResult.Success -> {

                    response.data?.let {
                        hideLoadingScreen()
                        adapter.setData(it)
                        randomUsersList = it.results
                        //Log.v("UsersData ",it.toString())
                    }
                }

                is NetworkResult.Error -> {
                    loadDataFromCache()
                    Toast.makeText(this, response.message.toString(), Toast.LENGTH_SHORT).show()
                }

                is NetworkResult.Loading -> {
                    showLoadingScreen()
                }
            }
        })
    }

    private fun setupCardStackView() {
        initialize()
    }

    private fun showLoadingScreen(){
        binding.contentLayout.visibility = View.GONE
        binding.noconnectionTextView.visibility = View.GONE
        binding.loadingProgressBar.visibility = View.VISIBLE
        binding.nodataTextView.visibility = View.GONE
    }

    private fun hideLoadingScreen(){
        binding.contentLayout.visibility = View.VISIBLE
        binding.noconnectionTextView.visibility = View.GONE
        binding.loadingProgressBar.visibility = View.GONE
        binding.nodataTextView.visibility = View.GONE
    }

    private fun showNoConnection(){
        binding.contentLayout.visibility = View.GONE
        binding.noconnectionTextView.visibility = View.VISIBLE
        binding.loadingProgressBar.visibility = View.GONE
        binding.nodataTextView.visibility = View.GONE
    }

    private fun showNoData(){
        binding.contentLayout.visibility = View.GONE
        binding.noconnectionTextView.visibility = View.GONE
        binding.loadingProgressBar.visibility = View.GONE
        binding.nodataTextView.visibility = View.VISIBLE
    }

    private fun initialize() {
        manager.setStackFrom(StackFrom.None)
        manager.setVisibleCount(3)
        manager.setTranslationInterval(8.0f)
        manager.setScaleInterval(0.95f)
        manager.setSwipeThreshold(0.3f)
        manager.setMaxDegree(20.0f)
        manager.setDirections(Direction.HORIZONTAL)
        manager.setCanScrollHorizontal(true)
        manager.setCanScrollVertical(true)
        manager.setSwipeableMethod(SwipeableMethod.Automatic)
        manager.setOverlayInterpolator(LinearInterpolator())
        binding.cardStackView.layoutManager = manager
        binding.cardStackView.adapter = adapter
        binding.cardStackView.itemAnimator.apply {
            if (this is DefaultItemAnimator) {
                supportsChangeAnimations = false
            }
        }
    }

    private fun setupButton() {
        val skip = binding.skipButton
        skip.setOnClickListener {
            val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Left)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(AccelerateInterpolator())
                .build()
            manager.setSwipeAnimationSetting(setting)
            binding.cardStackView.swipe()

            //Toast.makeText(this,"${selectedUser.name.first} been left swiped !!..",Toast.LENGTH_SHORT).show()
        }

     /*   val rewind = findViewById<View>(R.id.rewind_button)
        rewind.setOnClickListener {
            val setting = RewindAnimationSetting.Builder()
                .setDirection(Direction.Bottom)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(DecelerateInterpolator())
                .build()
            manager.setRewindAnimationSetting(setting)
            cardStackView.rewind()
        }*/

        val like = binding.likeButton
        like.setOnClickListener {
            val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Right)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(AccelerateInterpolator())
                .build()
            manager.setSwipeAnimationSetting(setting)
            binding.cardStackView.swipe()

        }
    }

    private fun loadDataFromCache(){
        lifecycleScope.launch {
            mainViewModel.readUsers.observe(this@MainActivity,{database ->
                if(database.isNotEmpty()){
                    adapter.setData(database[0].users)
                }else{
                    showNoData()
                }
            })
        }
    }



    override fun onCardDragging(direction: Direction, ratio: Float) {
       // Log.d("CardStackView", "onCardDragging: d = ${direction.name}, r = $ratio")
    }

    override fun onCardSwiped(direction: Direction) {

        if (manager.topPosition == adapter.itemCount) {
            showNoData() // once all the 10 users are swiped
        }else{
            //selectedUser = randomUsersList[manager.topPosition-1]
            Toast.makeText(this,"${randomUsersList[manager.topPosition-1].name.first} been $direction swiped !!..",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCardRewound() {
        //Log.d("CardStackView", "onCardRewound: ${manager.topPosition}")
    }

    override fun onCardCanceled() {
        //Log.d("CardStackView", "onCardCanceled: ${manager.topPosition}")
    }

    override fun onCardAppeared(view: View, position: Int) {
        //Log.v("CardStackViewPosition", "onCardAppeared: ($position) ${randomUsersList[position]}")

    }

    override fun onCardDisappeared(view: View, position: Int) {

        //Log.d("CardStackView", "onCardDisappeared: ($position) ")
    }
}