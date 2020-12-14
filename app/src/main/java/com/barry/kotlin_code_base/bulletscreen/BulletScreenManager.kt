package com.barry.kotlin_code_base.bulletscreen

import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.*

object BulletScreenManager {
    private var nowBulletScreenView : BulletScreenView? = null
    private var nowMessageList : MutableList<String> = mutableListOf()
    private var mutableMapRecord: MutableMap<String, Int> = mutableMapOf()
    private var playJob : Job? = null
    private var nowId : String = ""
    private var playIndex = 0;
    private var index = 0
    private var indexDelay = longArrayOf(500, 500, 500, 500)
    private var isPlaying = false

    fun playMessage(newBulletScreen : BulletScreenView, newMessageList : MutableList<String>, newId : String) {
        isPlaying = true
        // save state
        nowBulletScreenView?.apply {
            clear()
        }
        nowBulletScreenView = newBulletScreen
        if (nowId.isNotEmpty()) {
            mutableMapRecord[nowId] = playIndex
        }
        // init
        playIndex = 0
        nowId = newId
        nowMessageList.addAll(newMessageList)
        if (mutableMapRecord.containsKey(nowId)) {
            mutableMapRecord[nowId]?.let {
                playIndex = it
            }
            if (playIndex >= nowMessageList.size) {
                playIndex = 0
            }
        }
        // play
        playJob = GlobalScope.launch() {
            for (s in playIndex.. nowMessageList.size - 1) {
                if (!isPlaying) break
                var handler = Handler(Looper.getMainLooper()).post {
                    nowBulletScreenView?.add(nowMessageList[s])
                }
                playIndex = s
                indexDelay[index] = (250 + 30 * (nowMessageList[s].length / 2)).toLong()
                delay(indexDelay[index])
                index++
                if (index >= 4) {
                    index = 0
                }
            }
            playIndex = 0
        }
    }

    fun clear() {
        if (nowId.isNotEmpty()) {
            mutableMapRecord[nowId] = playIndex
        }
        playJob?.cancel()
        val handler = Handler(Looper.getMainLooper()).post {
            nowBulletScreenView?.let {
                it.stop()
                it.clear()
                it.restart()
            }
        }
        nowMessageList.clear()
        indexDelay = longArrayOf(500, 500, 500, 500)
        isPlaying = false
    }
}