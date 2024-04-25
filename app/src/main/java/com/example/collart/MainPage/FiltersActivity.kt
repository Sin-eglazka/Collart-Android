package com.example.collart.MainPage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.example.collart.Auth.CurrentUser
import com.example.collart.MainPage.Home.Projects.Experience
import com.example.collart.NetworkSystem.SkillModule
import com.example.collart.NetworkSystem.ToolsModule
import com.example.collart.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FiltersActivity : AppCompatActivity() {

    private lateinit var programsView: TextView
    private lateinit var experienceView: TextView
    private lateinit var skillsView: TextView
    private lateinit var applyBtn: Button

    private var selectedPrograms = BooleanArray(0)
    private var programs = arrayOf<String>()
    private val programsList = ArrayList<Int>()

    private var selectedSkills = BooleanArray(0)
    private var skills = arrayOf<String>()
    private val skillsList = ArrayList<Int>()

    private var experiences = Experience.entries.map { it.stringValue }.toTypedArray()
    private var selectedExperience = BooleanArray(experiences.size)
    private val experiencesList = ArrayList<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filters)

        val toolbar: Toolbar = findViewById(R.id.toolbarFilterPage)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setTitle("Фильтры")

        toolbar.setNavigationOnClickListener {
            onBackPressed() // Handle back button click
        }

        getAvailablePrograms()
        getAvailableSkills()


        programsView = findViewById(R.id.filterProgramsView)
        experienceView = findViewById(R.id.filterExperienceView)
        skillsView = findViewById(R.id.filterSkillView)

        programsView.setOnClickListener {
            createDropDownList(programsView, "Выберите программы", programs, selectedPrograms, programsList)
        }
        experienceView.setOnClickListener {
            createDropDownList(experienceView, "Выберите опыт", experiences, selectedExperience, experiencesList)
        }
        skillsView.setOnClickListener {
            createDropDownList(skillsView, "Выберите специальности", skills, selectedSkills, skillsList)
        }

        applyBtn = findViewById(R.id.aaplyFilters)
        applyBtn.setOnClickListener {
            closeActivity();
        }

    }

    private fun closeActivity(){
        val tools: MutableList<String> = emptyList<String>().toMutableList()
        for (j in programsList.indices) {
            tools.add(programs[programsList[j]])
        }

        val sendSkills: MutableList<String> = emptyList<String>().toMutableList()
        for (j in skillsList.indices) {
            sendSkills.add(skills[skillsList[j]])
        }

        val sendExperience: MutableList<String> = emptyList<String>().toMutableList()
        for (j in experiencesList.indices) {
            sendExperience.add(Experience.fromNameToString(experiences[experiencesList[j]]))
        }

        val intent = Intent()
        intent.putExtra("tools", tools.toTypedArray())
        intent.putExtra("skills", sendSkills.toTypedArray())
        intent.putExtra("experiences", sendExperience.toTypedArray())
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun getAvailablePrograms(){
        GlobalScope.launch(Dispatchers.Main) {
            var tools = ToolsModule.getAllTools(CurrentUser.token)
            programs = tools.map { tool -> tool.name }.toTypedArray()
            selectedPrograms = BooleanArray(programs.size)
        }
    }

    private fun getAvailableSkills(){
        GlobalScope.launch(Dispatchers.Main) {
            skills = SkillModule.getAllSkillsRu().toTypedArray()
            selectedSkills = BooleanArray(skills.size)
        }
    }

    private fun createDropDownList(view: TextView, title: String, items: Array<String>, selectedItems: BooleanArray, list: ArrayList<Int>){
        val builder = AlertDialog.Builder(this)

        // Set title
        builder.setTitle(title)

        // Set dialog non-cancelable
        builder.setCancelable(false)

        builder.setMultiChoiceItems(items, selectedItems) { _, i, b ->
            // Check condition
            if (b) {
                // When checkbox selected
                // Add position in lang list
                list.add(i)
                // Sort array list
                list.sort()
            } else {
                // When checkbox unselected
                // Remove position from langList
                list.remove(i)
            }
        }

        builder.setPositiveButton("OK") { dialogInterface, _ ->
            // Initialize string builder
            val stringBuilder = StringBuilder()

            // Use for loop
            for (j in list.indices) {
                // Concat array value
                stringBuilder.append(items[list[j]])
                // Check condition
                if (j != list.size - 1) {
                    // When j value not equal
                    // to lang list size - 1
                    // Add comma
                    stringBuilder.append(", ")
                }
            }

            // Set text on textView
            view.text = stringBuilder.toString()
            dialogInterface.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialogInterface, _ ->
            // Dismiss dialog
            dialogInterface.dismiss()
        }

        builder.setNeutralButton("Clear All") { dialogInterface, _ ->
            // Use for loop
            for (j in selectedItems.indices) {
                // Remove all selection
                selectedItems[j] = false
            }
            // Clear language list
            list.clear()
            // Clear text view value
            view.text = "Программы"
            dialogInterface.dismiss()
        }
        // Show dialog
        builder.show()
    }
}