package com.example.bookseeker.view

import android.content.Intent
import android.os.Bundle
import android.content.Context
import android.view.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.bookseeker.R
import java.util.*
import kotlin.collections.ArrayList
import kotlinx.android.synthetic.main.activity_recommend.*
import android.view.MotionEvent
import android.view.View.OnTouchListener
import androidx.core.content.ContextCompat
import android.graphics.Typeface
import android.view.Gravity
import android.view.LayoutInflater
import android.util.Log
import android.widget.*
import android.widget.LinearLayout.*
import com.example.bookseeker.contract.RecommendContract
import com.example.bookseeker.model.data.RecommendData
import com.example.bookseeker.presenter.RecommendPresenter

class RecommendActivity : BaseActivity(), RecommendContract.View {
    // RatingActivity와 함께 생성될 RatingPresenter를 지연 초기화
    private lateinit var recommendPresenter: RecommendPresenter

    var windowwidth: Int = 0
    var screenCenter: Int = 0
    var x_cord: Int = 0
    var y_cord: Int = 0
    var x: Int = 0
    var y: Int = 0
    var Likes = 0
    lateinit var parentView: ConstraintLayout
    var alphaValue = 0f
    lateinit var context: Context

    lateinit var recommendArrayList: ArrayList<RecommendData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_recommend)

        context = this
        parentView = findViewById(R.id.recommend_constraintlayout_content) as ConstraintLayout
        windowwidth = windowManager.defaultDisplay.width
        screenCenter = windowwidth / 2
        recommendArrayList = ArrayList()
        getArrayData()

        for (i in 0 until recommendArrayList.size) {
            val inflate = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val containerView = inflate.inflate(R.layout.item_cardv_recommend, null)

            val userIMG = containerView.findViewById(R.id.userIMG) as ImageView
            val relativeLayoutContainer = containerView.findViewById(R.id.relative_container) as RelativeLayout


            val layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT
            )

            containerView.setLayoutParams(layoutParams)

            containerView.setTag(i)
            userIMG.setBackgroundResource(recommendArrayList.get(i).photo)

            // m_view.setRotation(i);
            // containerView.setPadding(0, i, 0, 0);

            val layoutTvParams = LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT
            )


            // ADD dynamically like TextView on image.
            val tvLike = TextView(context)
            tvLike.layoutParams = layoutTvParams
            tvLike.setPadding(10, 10, 10, 10)
            tvLike.setBackgroundDrawable(resources.getDrawable(R.drawable.abc_btn_check_material))
            tvLike.text = "LIKE"
            tvLike.gravity = Gravity.CENTER
            tvLike.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD))
            tvLike.textSize = 25f
            tvLike.setTextColor(ContextCompat.getColor(context, R.color.mediumRed))
            tvLike.x = 20f
            tvLike.y = 100f
            tvLike.rotation = -50f
            tvLike.alpha = alphaValue
            relativeLayoutContainer.addView(tvLike)


            // ADD dynamically dislike TextView on image.
            val tvUnLike = TextView(context)
            tvUnLike.layoutParams = layoutTvParams
            tvUnLike.setPadding(10, 10, 10, 10)
            tvUnLike.setBackgroundDrawable(resources.getDrawable(R.drawable.abc_btn_check_material))
            tvUnLike.text = "UNLIKE"
            tvUnLike.gravity = Gravity.CENTER
            tvUnLike.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD))
            tvUnLike.textSize = 25f
            tvUnLike.setTextColor(ContextCompat.getColor(context, R.color.mediumRed))
            tvUnLike.x = 500f
            tvUnLike.y = 150f
            tvUnLike.rotation = 50f
            tvUnLike.alpha = alphaValue
            relativeLayoutContainer.addView(tvUnLike)


            val tvName = containerView.findViewById(R.id.tvName) as TextView
            val tvTotLikes = containerView.findViewById(R.id.tvTotalLikes) as TextView


            tvName.setText(recommendArrayList.get(i).name)
            tvTotLikes.setText(recommendArrayList.get(i).totalLikes)

            // Touch listener on the image layout to swipe image right or left.
            relativeLayoutContainer.setOnTouchListener(object : OnTouchListener {
                override fun onTouch(v: View, event: MotionEvent): Boolean {
                    x_cord = event.rawX.toInt()
                    y_cord = event.rawY.toInt()
                    containerView.setX(0f)
                    containerView.setY(0f)
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            x = event.x.toInt()
                            y = event.y.toInt()
                            Log.v("On touch", "$x $y")
                        }
                        MotionEvent.ACTION_MOVE -> {
                            x_cord = event.rawX.toInt()
                            // smoother animation.
                            y_cord = event.rawY.toInt()
                            containerView.setX((x_cord - x).toFloat())
                            containerView.setY((y_cord - y).toFloat())
                            if (x_cord >= screenCenter) {
                                containerView.setRotation(((x_cord - screenCenter) * (Math.PI / 32)).toFloat())
                                if (x_cord > screenCenter + screenCenter / 2) {
                                    tvLike.alpha = 1f
                                    if (x_cord > windowwidth - screenCenter / 4) {
                                        Likes = 2
                                    } else {
                                        Likes = 0
                                    }
                                } else {
                                    Likes = 0
                                    tvLike.alpha = 0f
                                }
                                tvUnLike.alpha = 0f
                            } else {
                                // rotate image while moving
                                containerView.setRotation(((x_cord - screenCenter) * (Math.PI / 32)).toFloat())
                                if (x_cord < screenCenter / 2) {
                                    tvUnLike.alpha = 1f
                                    if (x_cord < screenCenter / 4) {
                                        Likes = 1
                                    } else {
                                        Likes = 0
                                    }
                                } else {
                                    Likes = 0
                                    tvUnLike.alpha = 0f
                                }
                                tvLike.alpha = 0f
                            }
                        }
                        MotionEvent.ACTION_UP -> {

                            x_cord = event.rawX.toInt()
                            y_cord = event.rawY.toInt()

                            Log.e("X Point", "$x_cord , Y $y_cord")
                            tvUnLike.alpha = 0f
                            tvLike.alpha = 0f

                            if (Likes === 0) {
                                Toast.makeText(context, "NOTHING", Toast.LENGTH_SHORT).show()
                                Log.e("Event_Status :-> ", "Nothing")
                                containerView.setX(0f)
                                containerView.setY(0f)
                                containerView.setRotation(0f)
                            } else if (Likes === 1) {
                                Toast.makeText(context, "UNLIKE", Toast.LENGTH_SHORT).show()
                                Log.e("Event_Status :-> ", "UNLIKE")
                                parentView.removeView(containerView)
                            } else if (Likes === 2) {
                                Toast.makeText(context, "LIKED", Toast.LENGTH_SHORT).show()
                                Log.e("Event_Status :-> ", "Liked")
                                parentView.removeView(containerView)
                            }
                        }
                        else -> {
                        }
                    }
                    return true
                }
            })
            parentView.addView(containerView)
        }

        // View가 Create(Bind) 되었다는 걸 Presenter에 전달
        recommendPresenter.takeView(this)

        // BottomNavigationView 이벤트 처리
        switchBottomNavigationView()
    }

    private fun getArrayData() {
        var model = RecommendData("A", "3M", R.drawable.img_novel1)
        recommendArrayList.add(model)

        var model2 = RecommendData("B", "3M", R.drawable.img_novel2)
        recommendArrayList.add(model2)

        var model3 = RecommendData("C", "3M", R.drawable.img_novel1)
        recommendArrayList.add(model3)

        var model4 = RecommendData("D", "3M", R.drawable.img_novel2)
        recommendArrayList.add(model4)

        var model5 = RecommendData("E", "3M", R.drawable.img_novel1)
        recommendArrayList.add(model5)

        Collections.reverse(recommendArrayList)
    }

    // initPresenter : View와 상호작용할 Presenter를 주입하기 위한 함수
    override fun initPresenter() {
        recommendPresenter = RecommendPresenter()
    }

    // switchBottomNavigationView : RecommendActivity에서 BottomNavigationView 전환 이벤트를 처리하는 함수
    override fun switchBottomNavigationView() {
        recommend_btmnavview_menu.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.btmnavmenu_itm_search -> {
                    val nextIntent = Intent(baseContext, SearchActivity::class.java)
                    nextIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(nextIntent)
                    overridePendingTransition(0, 0)
                }
                R.id.btmnavmenu_itm_recommend -> {
                    val nextIntent = Intent(baseContext, RecommendActivity::class.java)
                    nextIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(nextIntent)
                    overridePendingTransition(0, 0)
                }
                R.id.btmnavmenu_itm_rating -> {
                    val nextIntent = Intent(baseContext, RatingActivity::class.java)
                    nextIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(nextIntent)
                    overridePendingTransition(0, 0)
                }
                R.id.btmnavmenu_itm_mypage -> {
                    val nextIntent = Intent(baseContext, MypageActivity::class.java)
                    nextIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(nextIntent)
                    overridePendingTransition(0, 0)
                }
            }
            false
        }
        recommend_btmnavview_menu.menu.findItem(R.id.btmnavmenu_itm_recommend)?.setChecked(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        // View가 Delete(Unbind) 되었다는 걸 Presenter에 전달
        recommendPresenter.dropView()
    }

    // setProgressON :  공통으로 사용하는 Progress Bar의 시작을 정의하는 함수
    override fun setProgressON(msg: String){
        progressON(msg)
    }

    // setProgressOFF() : 공통으로 사용하는 Progress Bar의 종료를 정의하는 함수
    override fun setProgressOFF() {
        progressOFF()
    }

    // showMessage : 공통으로 사용하는 messsage 출력 부분을 생성하는 함수
    override fun showMessage(msg: String) {
        Toast.makeText(this@RecommendActivity, msg, Toast.LENGTH_SHORT).show()
    }

    // executionLog : 공통으로 사용하는 Log 출력 부분을 생성하는 함수
    override fun executionLog(tag: String, msg: String){
        Log.e(tag, msg)
    }
}