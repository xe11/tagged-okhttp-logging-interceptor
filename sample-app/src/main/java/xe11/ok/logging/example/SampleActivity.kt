package xe11.ok.logging.example

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.isVisible
import xe11.ok.logger.Config.LoggingStrategy
import xe11.ok.logger.Config.LoggingStrategy.Accumulate.LogLevelScheme.HighlightedErrors
import xe11.ok.logger.level.Level
import xe11.ok.logging.tagged.R

class SampleActivity : AppCompatActivity() {

    private val showcase = TaggedLoggerShowcase()

    private lateinit var loggerSpinner: Spinner
    private lateinit var taggedLoggerSettings: LinearLayout
    private lateinit var baseLoggingLevelSpinner: Spinner
    private lateinit var loggingStrategySpinner: Spinner
    private lateinit var accumulatingSettings: LinearLayout
    private lateinit var passThroughSettings: LinearLayout
    private lateinit var synchronizeLoggingSwitch: SwitchCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_smaple)

        initSetings()
        initButtons()
    }

    private fun initSetings() {
        taggedLoggerSettings = findViewById(R.id.taggedLoggerSettings)
        loggerSpinner = findViewById(R.id.loggerSpinner)
        val loggerSpinnerItems = arrayOf("TaggedLogger", "OkHttp")
        val loggerSpinnerAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, loggerSpinnerItems)
        loggerSpinner.apply {
            adapter = loggerSpinnerAdapter
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) = Unit
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    taggedLoggerSettings.isVisible = position == 0
                }
            }
        }

        baseLoggingLevelSpinner = findViewById(R.id.baseLoggingLevelSpinner)
        val baseLoggingLevelSpinnerItems = Level.values().map(Level::name)
        val baseLoggingLevelSpinnerAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, baseLoggingLevelSpinnerItems)
        baseLoggingLevelSpinner.adapter = baseLoggingLevelSpinnerAdapter

        loggingStrategySpinner = findViewById(R.id.loggingStrategySpinner)
        accumulatingSettings = findViewById(R.id.accumulatingSettings)
        passThroughSettings = findViewById(R.id.passThroughSettings)
        synchronizeLoggingSwitch = findViewById(R.id.synchronizeLoggingSwitch)

        val loggingStrategyItems = arrayOf("Accumulating", "PassThrough")
        val loggingStrategyAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, loggingStrategyItems)
        loggingStrategySpinner.apply {
            adapter = loggingStrategyAdapter
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) = Unit
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    accumulatingSettings.isVisible = position == 0
                    passThroughSettings.isVisible = position == 1
                }
            }
        }
    }

    private fun initButtons() {
        findViewById<Button>(R.id.btnParallelRequests).setOnClickListener {
            applyHttpClientSettings()
            showcase.launchParallelRequestsShowcase()
        }

        findViewById<Button>(R.id.btnTagsHighlighting).setOnClickListener {
            applyHttpClientSettings()
            showcase.launchTagsAndHighlightingShowcase()
        }
    }

    private fun applyHttpClientSettings() {
        val baseLevel = Level.values()[baseLoggingLevelSpinner.selectedItemPosition]
        val loggingStrategy = when (loggingStrategySpinner.selectedItemPosition) {
            0 -> LoggingStrategy.Accumulate(
                logLevelScheme = HighlightedErrors(baseLevel = baseLevel),
                synchronizeLogging = synchronizeLoggingSwitch.isChecked
            )

            1 -> LoggingStrategy.PassThrough(level = baseLevel)
            else -> error("")
        }

        val strategy = if (loggerSpinner.selectedItemPosition == 0) loggingStrategy else null
        showcase.initHttpClient(strategy)
    }
}
