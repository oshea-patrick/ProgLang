import android.os.AsyncTask
import android.util.Log
import java.sql.DriverManager
import java.sql.PreparedStatement

public class postToSQL :
    AsyncTask<String?, Void?, String>() {
    var res = ""
    val url = "jdbc:mysql://44.229.52.223:3306/data_collection"
    val user = "test_user"
    val pass = "proglang"

    var URI : String? = null
    var usr :String? = null
    var numVotes : Int = 0
    var code = 0

    override fun onPreExecute() {
        super.onPreExecute()
    }

    fun update(a: String?, b: String?, c: Int, d: Int) {
        URI = a;
        usr = b;
        numVotes = c;
        code = d;
    }

    override fun onPostExecute(result: String) {
        cancel(true);
    }
    override fun doInBackground(vararg param1: String?): String {
        try {
            Class.forName("com.mysql.jdbc.Driver")
            val conn = DriverManager.getConnection(url, user, pass)

            // create a sql date object so we can use it in our INSERT statement
            if (code == 0) {
                // the mysql insert statement
                val query =
                    (" insert into Table1 (URI, user, numVotes)"
                            + " values (?, ?, ?)")

                // create the mysql insert preparedstatement
                val preparedStmt: PreparedStatement = conn.prepareStatement(query)
                preparedStmt.setString(1, URI)
                preparedStmt.setString(2, usr)
                preparedStmt.setInt(3, numVotes)
                // execute the preparedstatement
                preparedStmt.execute()
                conn.close()
            } else if (code == 1) {
                // the mysql insert statement
                val query =
                    (" update Table1 set column3 = " + numVotes + " where (column1 = '" + URI + "' and column2 = '" + usr + "')")

                // create the mysql insert preparedstatement
                val preparedStmt: PreparedStatement = conn.prepareStatement(query)
                // execute the preparedstatement
                preparedStmt.execute()
                conn.close()
            } else {
                Log.d("Deleteing", "Deleting " + URI + " " + usr)
                val query =
                    (" delete from Table1 where (URI='" + URI + "' and user='" + usr + "')")

                // create the mysql insert preparedstatement
                val preparedStmt: PreparedStatement = conn.prepareStatement(query)
                // execute the preparedstatement
                preparedStmt.execute()
                conn.close()
            }
        } catch (e: java.lang.Exception) {
            Log.d("Got an exception!", e.message)
        }
        Log.d("Leaving Post", "Leaving")
        return res
    }
}