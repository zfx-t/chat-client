import cn.hutool.*
import cn.hutool.json.JSONUtil
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket

object client {
    lateinit var name: String
    lateinit var socket: Socket
    lateinit var input: InputStream
    lateinit var output: OutputStream
}

/**
 * 显示菜单界面
 */
suspend fun view(job:Job) {
    var order: String
    while (isAlive) {
        println("===============小贺在线聊天系统==============")
        println("1.修改当前昵称")
        println("2.查看当前在线人数")
        println("3.发送消息")
        println("4.发送图片")
        println("5.退出登陆")
        print("Enter your order:")
        order = readln()
        when (order) {
            "1" -> {
                println("准备修改昵称")
                //修改昵称
                client.output.write("1\n".toByteArray())
                client.output.flush()
                val oldName = client.name
                modifyName()
                val user = object {
                    val oldName = oldName
                    val newName = client.name
                }
                client.output.write((JSONUtil.toJsonStr(user) + "\n").toByteArray())
                client.output.flush()
            }

            "2" -> {
                println("准备返回用户列表")
                client.output.write("2\n".toByteArray())
                client.output.flush()
//                checkNumsOfPeople()
            }

            "3" -> {
                println("准备发送消息")
                client.output.write("3\n".toByteArray())
                client.output.flush()
                sendMessage()
                println("发送完成")
            }

            "4" -> {
                println("准备发送图片")
                client.output.write("4\n".toByteArray())
                client.output.flush()
                delay(200)
                sendImage()
                println("发送完成")
            }

            "5" -> {
                println("程序结束，下次再见")
                client.output.write("5\n".toByteArray())
                client.output.flush()
                delay(100)
                client.output.write("${client.name}\n".toByteArray())
                client.output.flush()
                isAlive = false
            }
        }
        delay(100)
    }
}

/**
 * 检查初始化昵称,并且修改昵称
 */
fun checkInit() {
    println("请输入你聊天的昵称:")
    val name = readln()
    println("你当前的昵称为:$name")
    client.name = name
    val writer = client.output.bufferedWriter()
    writer.write(name + "\n")
    writer.flush()
}

/**
 * 修改昵称
 */
fun modifyName() {
    print("请输入修改后的名字:")
    val name = readln()
    client.name = name
}
/**
 * 获取所有在线人数
 */
//fun checkNumsOfPeople() {
//    var userMap = client.input.bufferedReader().readLine()
//    println(userMap)
//}
/**
 * 发送消息
 */
fun sendMessage() {
    print("发送对象:")
    //获取发送消息的对象
    val name = readln()
    print("消息内容:")
    val message = readln()
    val msg = object {
        val sender = client.name
        val receiver = name
        val msg = message
    }
    client.output.write((JSONUtil.toJsonStr(msg) + "\n").toByteArray())
    client.output.flush()
}

/**
 * 发送图片
 */
suspend fun sendImage() {
    print("发送对象:")
    //获取发送消息的对象
    val name = readln()
    print("文件位置:")
    val filePath = readln()
    val file = File(filePath)
    val fileName = file.name

    //暂时不使用后缀
    val fileSuffix = fileName.substringAfterLast(".")
    var bytes = file.readBytes()
    println(bytes.size)
    val msg = object {
        val sender = client.name
        val receiver = name
        val fileName = fileName
        val fileSize = bytes.size
    }

    client.output.write((JSONUtil.toJsonStr(msg) + "\n").toByteArray())
    client.output.flush()
    delay(1000)
    println("开始发送图片:")
    client.output.buffered().write(bytes)
//    client.output.flush()
//    client.output.write((JSONUtil.toJsonStr(msg) + "\n").toByteArray())
//    client.output.flush()
}