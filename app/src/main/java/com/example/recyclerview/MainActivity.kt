package com.example.recyclerview

import android.icu.lang.UCharacter
import android.media.Image
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.isGone
import androidx.core.widget.doOnTextChanged
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.qtalk.recyclerviewfastscroller.RecyclerViewFastScroller
import kotlinx.coroutines.delay
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil
import java.util.*


class MainActivity : AppCompatActivity() {

    val songobjects = mutableListOf<dataclass>()
    val temp = mutableListOf<dataclass>()
    val rcv1 = myadapter(songobjects)

    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recycler = findViewById<RecyclerView>(R.id.recyclerview)

        val swipe = findViewById<SwipeRefreshLayout>(R.id.swipetorefresh)

        val textview2 = findViewById<TextView>(R.id.textView2)

        textview2.setVisibility(View.GONE)

        val float = findViewById<FloatingActionButton>(R.id.floatingActionButton)

        val x = db.collection("name").document("NWC4cSGes4ycf0ZxPTQI").get()
        x.addOnSuccessListener()
        { document ->

            if (document.exists()) {
                for (i in 1..100) {
                    if (document.getString("name$i") != null) {
                        var name = document.getString("name$i").toString()
                        songobjects.add(dataclass(name))
                        temp.add(dataclass(name))
                        rcv1.notifyDataSetChanged()
                    } else {
                        break
                    }
                }
            }
        }

        val search = findViewById<ImageView>(R.id.searchbar)

        val textview3 = findViewById<TextView>(R.id.textview3)

        val edittext = findViewById<EditText>(R.id.edittext)

        val close = findViewById<ImageView>(R.id.close)

        val back = findViewById<ImageView>(R.id.back)

        val menu = findViewById<ImageView>(R.id.menu)

        back.setVisibility(View.GONE)

        close.setVisibility(View.GONE)

        edittext.setVisibility(View.GONE)


        search.setOnClickListener()
        {
            textview3.setVisibility(View.GONE)

            edittext.setVisibility(View.VISIBLE)

            close.setVisibility(View.VISIBLE)

            search.setVisibility(View.GONE)

            back.setVisibility(View.VISIBLE)

            menu.setVisibility(View.GONE)

            edittext.requestFocus()

            UIUtil.showKeyboard(this, edittext)

            var x = 0

            edittext.doOnTextChanged { text, start, before, count ->
                songobjects.clear()
                rcv1.notifyDataSetChanged()
                x = 0
                for (item in temp) {
                    val text = "^${edittext.text.toString().toLowerCase()}".toRegex()
                    if (item.string1.toLowerCase().contains(text)) {
                        songobjects.add(item)
                        rcv1.notifyDataSetChanged()
                    } else {
                        x++;
                    }

                }

                if (x == 32) {
                    textview2.setVisibility(View.VISIBLE)
                    textview2.text = "NO SEARCH RESULTS"
                    songobjects.clear()
                    rcv1.notifyDataSetChanged()
                }
                else
                {
                    textview2.setVisibility(View.GONE)
                }


            }

            rcv1.notifyDataSetChanged()

        }

        back.setOnClickListener()
        {
            edittext.setVisibility(View.GONE)
            close.setVisibility(View.GONE)
            search.setVisibility(View.VISIBLE)
            textview3.setVisibility(View.VISIBLE)
            back.setVisibility(View.GONE)
            menu.setVisibility(View.VISIBLE)
            edittext.text = null
            songobjects.clear()
            songobjects.addAll(temp)

            UIUtil.hideKeyboard(this)

            rcv1.notifyDataSetChanged()
        }

        close.setOnClickListener()
        {
            edittext.text = null
        }





            val fragment = findViewById<FragmentContainerView>(R.id.fragmentcontainerview)

            val rcv = findViewById<RecyclerView>(R.id.recyclerview)

           //  temp.addAll(songobjects)

            rcv.adapter = rcv1
            rcv.layoutManager = LinearLayoutManager(this)


            val swipeflag = ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
            val dragflag = ItemTouchHelper.UP or ItemTouchHelper.DOWN


            swipe.setOnRefreshListener {
                temp.clear()
                songobjects.clear()
                val xx = db.collection("name").document("NWC4cSGes4ycf0ZxPTQI").get()

                xx.addOnSuccessListener()
                {
                    document ->
                    if(document.exists())
                    {
                        for(i in 1..100)
                        {
                            if(document.getString("name$i")!=null)
                            {
                                var name = document.getString("name$i").toString()
                                songobjects.add(dataclass(name))
                                temp.add(dataclass(name))
                                rcv1.notifyDataSetChanged()
                            }
                            else
                            {
                                break
                            }
                        }
                    }
                }
                    rcv1.notifyDataSetChanged()
                    swipe.isRefreshing = false
                }

                ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(dragflag, swipeflag) {
                    override fun onMove(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder
                    ): Boolean {
                        val startposition = viewHolder.adapterPosition
                        val endposition = target.adapterPosition
                        Collections.swap(songobjects, startposition, endposition)
                        rcv1.notifyItemMoved(startposition, endposition)
                        return true
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                        val position = viewHolder.adapterPosition
                        songobjects.removeAt(position)
                        rcv1.notifyItemRemoved(position)
                        rcv1.notifyDataSetChanged()
                    }
                }).attachToRecyclerView(rcv)

            }

        }






