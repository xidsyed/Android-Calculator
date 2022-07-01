package com.example.calculator

import android.content.Context
import android.os.*
import android.text.method.ScrollingMovementMethod
import android.transition.Fade
import android.transition.TransitionManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.transition.Transition

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), View.OnClickListener {

	// Declare Views
	private lateinit var result: EditText
	private lateinit var newNumber: EditText
	private lateinit var resultTransitionsContainer: ViewGroup
	private var calc = Calc()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		// Before setContent replace splashscreen with base theme
		setTheme(R.style.Theme_Calculator)
		setContentView(R.layout.activity_main)
		// late init view references
		resultTransitionsContainer = findViewById(R.id.resultLayout)
		result = findViewById(R.id.resultText)
		newNumber = findViewById(R.id.newNumberText)
		// Init Views
		result.setTextColor(resources.getColor(R.color.offwhite))
		Log.d(TAG, "${newNumber.text}")
		initButtonsOnclickListener()
	}

	// NEW NUMBER FUNCTIONS
	fun newNumberAppend(str: String) {
		newNumber.text.append(str)
	}

	fun newNumberReplace(str:String) {
		var update = newNumber.text.dropLast(1)
		newNumber.setText("$update$str")
	}

	fun newNumberClear() {
		newNumber.setText("")
	}

	fun clearUI () {
		result.setText("")
		newNumber.setText("")
	}

	/*TODO:
	*  XXX Splash Screen
	*  XXX Make newNumber View Responsive
	*  XXX Make EditTexts Unclickable
	*  1. Clear UI Fancy Animation
	*  3. runRes Lower, Finally displays upper. Animation*/

	fun displayResult (db: Double, setFinal: Boolean) {
		// limit to 2 decimal places
		var str = String.format("%.2f", db)
		// remove decimals if not needed
		if(str.contains(".00"))
			str = str.substring(0, str.length-3)

		result.setText(str)
		if (setFinal == true) {
			result.setTextColor(resources.getColor(R.color.white))
		} else {
			result.setTextColor(resources.getColor(R.color.offwhite))
		}
	}

	// Initialise Button Listener
	fun initButtonsOnclickListener() {
		findViewById<Button>(R.id.button0).setOnClickListener(this)
		findViewById<Button>(R.id.button1).setOnClickListener(this)
		findViewById<Button>(R.id.button2).setOnClickListener(this)
		findViewById<Button>(R.id.button3).setOnClickListener(this)
		findViewById<Button>(R.id.button4).setOnClickListener(this)
		findViewById<Button>(R.id.button5).setOnClickListener(this)
		findViewById<Button>(R.id.button6).setOnClickListener(this)
		findViewById<Button>(R.id.button8).setOnClickListener(this)
		findViewById<Button>(R.id.button9).setOnClickListener(this)
		findViewById<Button>(R.id.button10).setOnClickListener(this)
		findViewById<Button>(R.id.button11).setOnClickListener(this)
		findViewById<Button>(R.id.button12).setOnClickListener(this)
		findViewById<Button>(R.id.button13).setOnClickListener(this)
		findViewById<Button>(R.id.button21).setOnClickListener(this)
		findViewById<Button>(R.id.button22).setOnClickListener(this)
		findViewById<Button>(R.id.btnMultiply).setOnClickListener(this)
		findViewById<Button>(R.id.btnSubtract).setOnClickListener(this)
		findViewById<Button>(R.id.button23).setOnClickListener(this)
		findViewById<Button>(R.id.buttonEquals).setOnClickListener(this)
	}

	// OnClick Override
	override fun onClick(v: View?) {
		when (v?.id) {
			R.id.buttonEquals -> {
				shortVibration()
				calc.input("=")
			} // =
			R.id.button0 -> {
				shortVibration()
				calc.clearAll()
			} // C

			R.id.btnMultiply -> {
				Log.d(TAG, "- called")
				calc.input("*")
			} // *
			R.id.btnSubtract -> { calc.input("-")} // -
			R.id.button1 -> {
				shortVibration()
				displayMsg("PAY $50 for Premium Features 😊")
			} // ( )
			R.id.button11 -> {calc.input("+")} // +
			R.id.button3 -> { calc.input("/") } // /
			R.id.button2 -> {
				shortVibration()
				displayMsg("PAY $50 for Premium Features 😊")} // %
			R.id.button4 -> { calc.input("7")} // 7
			R.id.button5 -> { calc.input("8")} // 8
			R.id.button6 -> {calc.input("9")} // 9
			R.id.button21 -> { calc.input("4")} // 4
			R.id.button22 -> { calc.input("5")} // 5
			R.id.button23 -> { calc.input("6")} // 6
			R.id.button8 -> { calc.input("1")} // 1
			R.id.button9 -> { calc.input("2")} // 2
			R.id.button10 -> { calc.input("3")} // 3
			R.id.button12 -> { calc.input(".")} // .
			R.id.button13 -> { calc.input("0")} // 0

		}
	}

	// Display Toast
	fun displayMsg(msg: String) {
		Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
	}

	// Stores State in ENUM
	enum class OP {MULTIPLY, DIVIDE, ADD, SUBTRACT, PERCENTAGE, DEFAULT}

	// Calculator Object
	inner class Calc {

		private var currentNum: String? = null
		private var res: Double = 0.0
		private var state = OP.DEFAULT
		private var runRes: Double = 0.0

		// CALCULATE runRes & UPDATE UI
		fun newRunRes() {
			// get double from string
			var number = currentNum!!.toDouble()
			// perform operation
			runRes = when (state) {
				OP.ADD -> (res + number)
				OP.SUBTRACT -> (res - number)
				OP.DIVIDE -> (res / number)
				OP.MULTIPLY -> (res * number)
				OP.DEFAULT -> number
				OP.PERCENTAGE -> (res* number/100.00)
			}
			// update UI
			displayResult(runRes, false)
		}

		// debug
		val printvars = {Log.d(TAG, "\n\nrunRes = $runRes \nnum: $currentNum? \nres: $res? \nstate: ${state}")}

		// Handle Input Logic
		// Integer and Symbols
		// TODO: Handle multiple '.'s
		fun input(s: String) {
			// --- IF INTEGER or DOT--- //
			if (s.matches("-?\\d+(\\.\\d+)?".toRegex())||s == "."){
				if(currentNum == null) currentNum = ""
				// APPEND to numString
				currentNum = "$currentNum$s"
				// update result and UI
				newNumberAppend(s)
				newRunRes()
				// return
				printvars()
				return
			}
			// --- ELSE SYMBOL --- //
			else {
				// 1. FIND STATE
				var newState = when (s) {
					"+" -> OP.ADD
					"-" -> OP.SUBTRACT
					"*" -> OP.MULTIPLY
					"/" -> OP.DIVIDE
					"%" -> OP.PERCENTAGE
					else -> OP.DEFAULT
				}

				// 2. PROPER STATE CHANGE
				// No first number!
				if (currentNum == null) return

				// STATE SAME AS BEFORE, on SAME EMPTY NUMBER
				// no current number and state same (meaning state has already been printed
				if(currentNum =="" && state == newState) return

				// CHANGE PREXISTING STATE
				// no current number but state different
				// make sure previous state wasnt '='
				if(currentNum == "" && state != newState && state != OP.DEFAULT) {
					// change active state
					state = newState
					// print change
					newNumberReplace(s)
					return
				}
				Log.d(TAG, "State has changed")

				// UPDATE STATE and in UI
				state = newState
				newNumberAppend(s)

				// 2. UPDATE RES if Number not empty
				if(currentNum!="") {
					// update result to runningResult
					res = runRes
					// reset currentNum to null or " "
					currentNum = ""
					if(state == OP.DEFAULT) {
						newNumberClear()
					}
				}
				// 3. DISPLAY RESULT
				displayResult(res, state == OP.DEFAULT)
				printvars()
			}
		}
		// C Button
		fun clearAll() {
			currentNum = null
			res = 0.0
			state = OP.DEFAULT
			runRes = 0.0
			Log.d(TAG, "Cleared Everything")
			clearUI()
		}
	}


	// Vibrate Phone
	inline fun shortVibration () {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
			val vibe = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
			val vib = vibe.defaultVibrator
			vib.vibrate(VibrationEffect.createOneShot(30,100))
		} else {
			val vib = getSystemService(VIBRATOR_SERVICE) as Vibrator
			vib.vibrate(50)
		}
	}





}