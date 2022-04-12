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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import com.ksa.foody.util.NetworkResult
import com.ksa.infilect.adapter.CardStackAdapter
import com.ksa.infilect.viewmodels.MainViewModel
import com.ksa.infilect.databinding.ActivityMainBinding
import com.ksa.infilect.models.Result
import com.yuyakaido.android.cardstackview.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() , CardStackListener {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var  binding : ActivityMainBinding
    private val manager by lazy { CardStackLayoutManager(this, this) }
    private val adapter by lazy { CardStackAdapter() }
    private var randomUsersList = emptyList<Result>()
    private lateinit var selectedUser : Result

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        setupCardStackView()
        setupButton()

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
                    //loadDataFromCache()
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
            Toast.makeText(this,"${selectedUser.name.first} been left swiped !!..",Toast.LENGTH_SHORT).show()
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
            Toast.makeText(this,"${selectedUser.name.first} been right swiped !!..",Toast.LENGTH_SHORT).show()
        }
    }





    override fun onCardDragging(direction: Direction, ratio: Float) {
       // Log.d("CardStackView", "onCardDragging: d = ${direction.name}, r = $ratio")
    }

    override fun onCardSwiped(direction: Direction) {
        //Log.v("CardStackViewPosition", "onCardSwiped: p = ${manager.topPosition}, d = $direction, ${randomUsersList[manager.topPosition]} ")
        //Log.d("CardStackViewItem", "onCardSwiped: p = ${randomUsersList[manager.topPosition]}")
        if (manager.topPosition == adapter.itemCount) {
            showNoData() // once all the 10 users are swiped
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
        selectedUser = randomUsersList[position]
    }

    override fun onCardDisappeared(view: View, position: Int) {

        //Log.d("CardStackView", "onCardDisappeared: ($position) ")
    }
}