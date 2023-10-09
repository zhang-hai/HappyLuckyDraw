package com.example.happyluckydraw.util

import com.example.happyluckydraw.R
import com.example.happyluckydraw.entry.Reward

object LuckDrawImpl {
    //颜色列表
    val colorList = listOf(
        0x50000000,
        0x5000FFFF,
        0x20888888,
        0x50FF00FF,
        0x50FFFF00
    )

    val list = listOf<Reward>(
        Reward(R.mipmap.bulbasaur,"妙蛙种子",0.5f),
        Reward(R.mipmap.ivysaur,"妙蛙草",0.5f),
        Reward(R.mipmap.venusaur,"妙蛙花",0.5f),
        Reward(R.mipmap.charmander,"小火龙",0.5f),
        Reward(R.mipmap.charmeleon,"火恐龙",0.5f),
        Reward(R.mipmap.charizard,"喷火龙",0.5f),
        Reward(R.mipmap.squirtle,"杰尼龟",0.5f),
        Reward(R.mipmap.wartortle,"卡咪龟",0.5f),
        Reward(R.mipmap.blastoise,"水箭龟",0.5f),
        Reward(R.mipmap.caterpie,"绿毛虫",0.5f),
        Reward(R.mipmap.metapod,"铁甲蛹",0.5f),
        Reward(R.mipmap.butterfree,"巴大蝶",0.5f),
        Reward(R.mipmap.weedle,"独角虫",0.5f),
        Reward(R.mipmap.kakuna,"铁壳蛹",0.5f),
        Reward(R.mipmap.beedrill,"大针蜂",0.5f),
        Reward(R.mipmap.pidgey,"波波",0.5f),
        Reward(R.mipmap.pidgeotto,"比比鸟",0.5f),
        Reward(R.mipmap.pidgeot,"大比鸟",0.5f),
        Reward(R.mipmap.rattata,"小拉达",0.5f),
        Reward(R.mipmap.raticate,"拉达",0.5f),
        Reward(R.mipmap.spearow,"烈雀",0.5f),
        Reward(R.mipmap.fearow,"大嘴雀",0.5f),
        Reward(R.mipmap.ekans,"阿柏蛇",0.5f),
        Reward(R.mipmap.arbok,"阿柏怪",0.5f),
        Reward(R.mipmap.pikachu,"皮卡丘",0.01f),
        Reward(R.mipmap.raichu,"雷丘",0.5f),
        Reward(R.mipmap.sandshrew,"穿山鼠",0.5f),
        Reward(R.mipmap.sandslash,"穿山王",0.5f),
        Reward(R.mipmap.nidoran,"尼多兰",0.5f),
        Reward(R.mipmap.nidorina,"尼多娜",0.5f),
        Reward(R.mipmap.nidoqueen,"尼多后",0.5f),
        Reward(R.mipmap.nidoran2,"尼多朗",0.5f),
        Reward(R.mipmap.nidorino,"尼多力诺",0.5f),
        Reward(R.mipmap.nidoking,"尼多王",0.5f),
        Reward(R.mipmap.clefairy,"皮皮",0.5f),
        Reward(R.mipmap.clefable,"皮可西",0.5f),
        Reward(R.mipmap.vulpix,"六尾",0.5f),
        Reward(R.mipmap.ninetales,"九尾",0.5f),
        Reward(R.mipmap.jigglypuff,"胖丁",0.5f),
        Reward(R.mipmap.wigglytuff,"胖可丁",0.5f),
        Reward(R.mipmap.psyduck,"可达鸭",0.5f),
        Reward(R.mipmap.snorlax,"卡比兽",0.5f),
        Reward(R.mipmap.zapdos,"闪电鸟",0.5f),
        Reward(R.mipmap.moltres,"火焰鸟",0.5f),
        Reward(R.mipmap.scyther,"飞天螳螂",0.5f),

    )
    private lateinit var newRatioList:MutableList<Float>

    init {
        luckDraw()
    }

    private fun luckDraw(){
        val oldRatioList = mutableListOf<Float>()
        for (item in list){
            oldRatioList.add(if(item.ratio < 0f) 0.01f else item.ratio)
        }
        //计算总概率
        var total = 0f
        oldRatioList.forEach {
            total += it
        }
        //计算出每个物品所处的概率范围
        newRatioList = mutableListOf<Float>()
        var tempVal = 0f
        oldRatioList.forEach {
            tempVal += it
            newRatioList.add(tempVal/total)
        }
    }

    fun luckIndex():Int{
        if(newRatioList.isNullOrEmpty()){
            return -1
        }
        val random = Math.random().toFloat()
        val list = mutableListOf<Float>().apply {
            addAll(newRatioList)
            add(random)
        }
        list.sort()
        val index = list.indexOf(random)
        println("---->恭喜你，抽中${LuckDrawImpl.list[index].name}")
        return index
    }

    /**
     * 获取中奖索引
     * @param radian 是否需要将索引转换成角度 true要转换，false 不转换
     */
    fun lotteryDegree(index:Int):Float{
        if(index == -1){
            return 0f
        }
        val per = 360f / newRatioList.size
        //(index+1) * per - per/2
        return (index + 0.5f) * per
    }

    /**
     * 获取奖品名称
     */
    fun lotteryReward(index:Int = -2):Reward{
        var i = index
        if(i == -2){
            i = luckIndex()
        }
        if (i == -1) return Reward(R.mipmap.ic_launcher_round,"空",1f)
        return list[i]
    }
}