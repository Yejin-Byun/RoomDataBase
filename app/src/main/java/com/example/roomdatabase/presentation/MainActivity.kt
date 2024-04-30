package com.example.roomdatabase.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.roomdatabase.data.MyDAO
import com.example.roomdatabase.data.MyDatabase
import com.example.roomdatabase.data.Student
import com.example.roomdatabase.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.StringBuilder

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    lateinit var myDao : MyDAO // MyDAO 는 전역 변수로 생성

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // MyDatabase 의 getDatabase 를 통해 MainActivity 로 인스턴스를 받아옴
        myDao = MyDatabase.getDatabase(this).getMyDao()

        // myDao 의 getAllStudents() = 해당 테이블에 있는 모든 학생의 정보를 List 형태의 LiveData로 받아옴
        val allStudent = myDao.getAllStudents()

        // allStudent 에 observe 를 달아서 데이터에 변경이 있을 때마다 StringBuilder 를 통해서 (append 는 문자열을 쭉 이어 붙임)
        // textStudentList 에 자동으로 추가해 줌
        allStudent.observe(this) {
            val str = StringBuilder().apply {
                for ((id, name) in it) {
                    append(id)
                    append("-")
                    append(name)
                    append("\n")
                }
            }.toString()
            binding.textStudentList.text = str
        }

        // Add Student 버튼을 클릭하면
        // id 와 name 정보 두 개를 읽고 비어 있는지 확인 후 Insert 를 하게 됨.
        binding.addStudent.setOnClickListener {
            val id = binding.editStudentId.text.toString().toInt()
            val name = binding.editStudentName.text.toString()
            if (id > 0 && name.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    myDao.insertStudent(Student(id, name))
                    // Insert를 할 때는 Student 객체가 들어가게 됨 (id, 이름)
                }
            }

            // Insert 이후에 다시 추가할 수 있도록 비워줌
            binding.editStudentId.text = null
            binding.editStudentName.text = null
        }

        // Query 버튼
        // name을 먼저 받고
        binding.queryStudent.setOnClickListener {
            val name = binding.editStudentName.text.toString()
            CoroutineScope(Dispatchers.IO).launch {

                // myDao.getStudentByName 을 사용하여 name 으로 학생을 검색함
                // getStudentByName 의 Where 절에는 name 을 통해 검색함
                val results = myDao.getStudentByName(name)

                // withContext 사용 이유: Main UI Thread 에서 발생하는 것이 아닌
                // 별도의 코루틴 Thread 에서 동작하는 것이기 때문에 withContext(Dispatchers.Main)을 걸어놔야 UI를 업데이트 할 수 있음
                if (results.isNotEmpty()) {
                    val str = StringBuilder().apply {
                        results.forEach { student ->
                            append(student.id)
                            append("-")
                            append(student.name)
                        }
                    }
                    withContext(Dispatchers.Main) {
                        binding.textQueryStudent.text = str
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        binding.textQueryStudent.text = ""
                    }
                }

            }
        }

    }
}