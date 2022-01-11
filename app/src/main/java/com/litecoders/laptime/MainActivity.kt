package com.litecoders.laptime

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

/************************
 * @author Kadiri Ehijie
 * A simple program to count how many minutes has passed
 * Implement the menu design, save function and countdown timer
 ************************/

class MainActivity : AppCompatActivity() {

    //todo Implement the menu button

    // Create placeholder variables to better performance
    private lateinit var startButton: Button
    private lateinit var resetButton: Button
    private lateinit var pauseButton: Button

    private lateinit var timeLeftView: TextView
    private lateinit var lapsView: TextView

    private var timeLeft = 60
    private var laps = 0

    private lateinit var countDownTimer: CountDownTimer

    private var watchStarted = false
    private var watchPaused = false

    private var timeOnPause: Long = 0
    private var intervalTime = 1000L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState != null){
            timeLeft = savedInstanceState.getInt(TIMELEFT)
            laps = savedInstanceState.getInt(LAPS)

            startWatch()
        }

        //Attach the views to the variables
        startButton = findViewById(R.id.start_button)
        pauseButton = findViewById(R.id.pause_button)
        resetButton = findViewById(R.id.reset_button)

        timeLeftView = findViewById(R.id.time_view)
        lapsView = findViewById(R.id.laps)

        //set the default time and laps done
        timeLeftView.text = getString(R.string.time_left, timeLeft)
        lapsView.text = getString(R.string.laps_done, laps)

        //check the buttons for clicks
        startButton.setOnClickListener { startWatch() }
        //only call functions if the watchStarted is true
        pauseButton.setOnClickListener { if(watchStarted) pauseWatch() }
        resetButton.setOnClickListener { if(watchStarted) resetWatch() }
    }

    // Save the TimeLeft and Laps done
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(TIMELEFT, timeLeft)
        outState.putInt(LAPS, laps)
    }

    // create and populate the menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu, menu)
        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.about){
            showInfo()
        }
        return true
    }

    private fun showInfo(){
        val dialogTitle = getString(R.string.author)
        val dialogVersion = getString(R.string.version, BuildConfig.VERSION_CODE)

        val builder = AlertDialog.Builder(this)
        builder.setTitle(dialogVersion)
        builder.setMessage(dialogTitle)
        builder.create().show()
    }

    //Called when start button is clicked
    private fun startWatch() {
        //checks to see if watch is currently active and modify accordingly
        if(watchPaused){
            watchPaused = false
            countDown(timeOnPause)
        }// if watch is not paused and watch is not started, start watch
        else if(!watchPaused && !watchStarted){
            watchStarted = true
            countDown((timeLeft * 1000).toLong())
        }
    }

    // pauses the watch and stops the countdown
    private fun pauseWatch(){
        watchPaused = true
        countDownTimer.cancel()
    }

    // increments the lap time
    private fun lapWatch(){
        watchPaused = false
        watchStarted = false
        timeLeft = 60
        //increments the lap and assigns to the view
        laps++
        lapsView.text = getString(R.string.laps_done, laps)

        // resets the timer
        startWatch()
    }

    //resets the watch to original state
    private fun resetWatch(){
        watchPaused = false
        watchStarted = false
        countDownTimer.cancel()

        //reset laps and assigns it to the text view
        laps = 0
        timeLeft = 60
        lapsView.text = getString(R.string.laps_done, laps)

        //resets the timer
        startWatch()
    }

    //the countdown function
    private fun countDown(initialTime: Long){
        //initializes the countDownTimer
        countDownTimer = object : CountDownTimer(initialTime, intervalTime) {
            override fun onTick(millisLeft: Long) {
                //find the time left and converts it to int and assigns it to the textView
                timeLeft = (millisLeft / intervalTime).toInt()
                timeLeftView.text = getString(R.string.time_left, timeLeft)

                //sacves the current time to use in for the pause functionality
                timeOnPause = millisLeft
            }

            override fun onFinish() {
                //stops the timer and calls the lapWatch function
                countDownTimer.cancel()
                lapWatch()
            }
        }.start()
    }

    companion object{
        private val TIMELEFT = "TIMELEFT"
        private val LAPS = "LAPS"
    }
}