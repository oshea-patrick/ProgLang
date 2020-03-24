package com.example.proglang.SQL

import android.os.AsyncTask
import android.util.Log
import com.example.proglang.Songs.Queue
import com.example.proglang.Songs.Song
import java.sql.DriverManager
import java.sql.ResultSetMetaData

public class getFromSQL :
    AsyncTask<String?, Void?, String>() {
    var res = ""
    val url = "jdbc:mysql://44.229.52.223:3306/data_collection"
    val user = "test_user"
    val pass = "proglang"
    var queue : Queue? = null

    var targetUser = ""

    fun update(a:String) {
        targetUser = a
    }

    override fun onPreExecute() {
        super.onPreExecute()
    }
    override fun onPostExecute(result: String) {
        Log.d("Result Printing", result)
    }

    private fun parseResult(a : String) : String {
        var out = ""
        for (char in a) {
            if (char - '0' >= 0 && char - '0' <= 9)
                out += char
        }
        return out
    }

    override fun doInBackground(vararg params: String?): String {
        try {
            queue = Queue()
            Class.forName("com.mysql.jdbc.Driver")
            val con = DriverManager.getConnection(url, user, pass)
            println("Databaseection success")
            var result = "Database Connection Successful\n"
            val st = con.createStatement()
            val rs =
                st.executeQuery("select * from Table1 where user='" + targetUser + "'")
            val rsmd: ResultSetMetaData = rs.metaData
            while (rs.next()) {
                var tempURI = """
                        ${rs.getString(1)}
                        
                        """.trimIndent()
                var tempUser = """
                        ${rs.getString(2)}
                        
                        """.trimIndent()
                var tempNumVotes = """
                        ${rs.getString(3)}
                        
                        """.trimIndent()
                var tempSong = Song(tempURI, tempUser, (parseResult(tempNumVotes).toInt()))
                queue?.add(tempSong)
                Log.d("Q", tempSong.toString())
            }
            queue?.sortList()
        } catch (e: Exception) {
            e.printStackTrace()
            res = e.toString()
        }
        return ""
    }
}