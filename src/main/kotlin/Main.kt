import cn.hutool.json.JSONUtil
import kotlinx.coroutines.*
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.Socket

fun main(): Unit = runBlocking {
    login()
    checkInit()
    var job = GlobalScope.launch { withContext(Dispatchers.IO) { handleServer() } }
    launch { withContext(Dispatchers.IO) { view(job) } }
}

object Type {
    val TYPE_MESSAGE = "0"
    val TYPE_IMAGE = "1"
}


/**
 * 登陆
 */
fun login() {
    //初始化
    client.apply {
        socket = Socket(host, port)
        println("连接成功")
        input = socket.getInputStream()
        output = socket.getOutputStream()
    }

}
var isAlive = true
/**
 * 接受消息
 */
suspend fun handleServer() {
    while (isAlive) {
        val msg = client.input.bufferedReader().readLine()
        when (msg) {
            Type.TYPE_MESSAGE -> {
                println()
                println(msg)
            }

            Type.TYPE_IMAGE -> {

                delay(200)
                var msg = JSONUtil.parseObj(client.input.bufferedReader().readLine())
                val fileName = msg.get("fileName").toString()
                val fileSize = msg.get("fileSize").toString().toInt()
                println()
                println(msg)
                delay(3000)
                val file = File(fileName)
                val fileOutput = FileOutputStream(file)
                val bytes = ByteArray(1024)
                println(fileSize)
                var i = 0

                fileOutput.use {
                    do {
                        var len: Int = client.input.read(bytes)
                        if(len!=-1){
                            it.write(bytes, 0, len)
                            i += len
                            println("已完成${i * 1.0 / fileSize}")
                        }
                    } while ( len!=-1 &&    i < fileSize)

                }
                println("已经传输完成")
            }
            else -> {
                println()
                println(msg)
            }
        }
    }
}

/**
 * 全局变量
 */
val host = "127.0.0.1"
val port = 12340