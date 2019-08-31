package com.blanke.wechatbotxposed.hook

import com.blanke.wechatbotxposed.Mysql.JavaMySql
import com.blanke.wechatbotxposed.hook.SendMsgHooker.wxMsgSplitStr
import com.gh0u1l5.wechatmagician.spellbook.interfaces.IMessageStorageHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers

object WechatMessageHook : IMessageStorageHook {

    private val keyTerms = arrayOf("CPA", "CPC", "CPS", "UV", "寻优质乙方", "实时后台", "日结", "量结",
            "无前期", "UV结算", "API对接", "现金贷系统", "自研系统", "优质甲方", "优质乙方", "新盘",
            "跑UV", "跑S", "跑A", "买系统", "日量", "高转化", "回款高", "代理", "盘贷超", "低逾期",
            "脱敏", "可预付", "转化好的系统", "短信渠道")

    private val numberTerms = 30
    private val database = JavaMySql()

    override fun onMessageStorageCreated(storage: Any) {
    }

    override fun onMessageStorageInserted(msgId: Long, msgObject: Any) {
        XposedBridge.log("onMessageStorageInserted msgId=$msgId,msgObject=$msgObject")
//        printMsgObj(msgObject)
        // 这些都是消息的属性，内容，发送人，类型等
        val field_content = XposedHelpers.getObjectField(msgObject, "field_content") as String
        val field_talker = XposedHelpers.getObjectField(msgObject, "field_talker") as String?
        val field_type = (XposedHelpers.getObjectField(msgObject, "field_type") as Int).toInt()
        val field_isSend = (XposedHelpers.getObjectField(msgObject, "field_isSend") as Int).toInt()
        XposedBridge.log("field_content=$field_content,field_talker=$field_talker," +
                "field_type=$field_type,field_isSend=$field_isSend")
        if (field_isSend == 1) {// 代表自己发出的，不处理
            return
        }
        if (field_type == 1) { //文本消息
            // field_content 就是消息内容，可以接入图灵机器人回复
            if (!checkValid(field_content)) {
                return
            }
            val response = database.getData(field_content);
            if (response.size == 0) {
                return
            }

            val latest = database.counter
            database.counter = 0

            var replyContent = "最后消息为:\n"
            for (k in 0 until latest) {
                replyContent += "消息: "
                replyContent += response[k].content
                replyContent += "关键词: "
                replyContent += response[k].key
                replyContent += "时间: "
                replyContent += response[k].time
                replyContent += "\n"

                XposedBridge.log(replyContent);

                Objects.ChattingFooterEventImpl?.apply {
                    // 将 wx_id 和 回复的内容用分隔符分开
                    val content = "$field_talker$wxMsgSplitStr$replyContent"
                    val success = Methods.ChattingFooterEventImpl_SendMsg.invoke(this, content) as Boolean
                    XposedBridge.log("reply msg success = $success")
                }

                replyContent = ""

            }
            //val replyContent = "repo: \n$field_content"

        }
    }

    private fun checkValid(key: String): Boolean {
        for (k in 0 until numberTerms) {
            if (key.compareTo(keyTerms[k]) == 0) {
                return true
            }
        }
        return false
    }

    private fun printMsgObj(msg: Any) {
        val fieldNames = msg::class.java.fields
        fieldNames.forEach {
            val field = it.get(msg)
            if (field is Array<*>) {
                val s = StringBuffer()
                field.forEach {
                    s.append(it.toString() + " , ")
                }
                XposedBridge.log("$it = $s")
            } else {
                XposedBridge.log("$it = $field")
            }
        }
    }
}