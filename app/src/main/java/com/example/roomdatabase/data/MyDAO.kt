package com.example.roomdatabase.data

import androidx.lifecycle.LiveData
import androidx.room.*

// Dao 는 쿼리를 날릴 때 사용함
@Dao
interface MyDAO {
    // INSERT, key 충돌이 나면 새 데이터로 교체 (학생 추가)
    @Insert(onConflict =  OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student)

    // LiveData<> 사용 (변동이 생길 때마다 데이터 리턴)
    @Query("SELECT * FROM student_table")
    fun getAllStudents() : LiveData<List<Student>>

    // 메소드 인자를 SQL문에서 :을 붙여 사용 (이름으로 검색하는 것)
    @Query("SELECT * FROM student_table WHERE name = :sname")
    suspend fun getStudentByName(sname: String): List<Student>
    // List로 받는 이유는 검색한 데이터가 여러 개일 수 있기 때문

    @Delete // (지우는 것)
    suspend fun deleteStudent(student: Student);
    // primary key is used to find the student
}