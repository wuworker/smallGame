/**
 * 五子棋的js文件
 * 包含了界面的绘制和服务器的通讯
 * */
var URL_ADDRESS="ws://127.0.0.1:30000";
//             常量
//cmd
var CMD_PLAYER_WHO = "player_who";
var CMD_PLAYER_MSG = "player_msg";
var CMD_STEP = "step";
var CMD_GAME = "game";
var CMD_MSG = "chat_msg";
//params
var PLAYER1 = "player1";
var PLAYER2 = "player2";
var GAME_READY="ready";
var GAME_END="end";

//棋盘的大小
var SIZE=550;
//一个棋子的大小
var CELL_SIZE=30;
var SYSMSG="系统消息:";

var webSocket;
//dom
//接收区
var txt;
//局数
var gnum2;
var win2;
var gnum1;
var win1;


//canvas
var gameCtx;
var tipCtx;

//格子数组
var cells=[];
//已选择图片索引
var imgIndex;
//局数
var gameNum=0;
var myWinNum=0;
//轮到谁
var otherName;
var isTurn;
var who;
var myChessImg=new Image();
var otherChessImg=new Image();
var myTurnImg;
var otherTurnImg;

var isStart=false;
var isWatting = true;
var isEnd=false;

var isReady=false;
//对方是否准备
var isReady2=false;
var isReady2New=false;

window.gobang={};
//等待游戏开始
gobang.waitGame=function(){
	gobang.initView();
	gobang.initCon();
}

//初始化视图
gobang.initView=function(){
	gnum2=document.getElementById("gnum2");
	win2=document.getElementById("win2");
	gnum1=document.getElementById("gnum1");
	win1=document.getElementById("win1");
	myTurnImg=document.getElementById("turnImg1");
	otherTurnImg=document.getElementById("turnImg2");
	txt=document.getElementById("receive");
	
	var gameCan=document.getElementById("gameCanvas");
	gameCtx=gameCan.getContext("2d");
	var backCan=document.getElementById("backCanvas");
	var backCtx=backCan.getContext("2d");
	var tipCan=document.getElementById("tipCanvas");
	tipCtx=tipCan.getContext("2d");
	tipCtx.fillStyle="#00f";
	tipCtx.textAlign="center";
	tipCtx.textBaseAlign="bottom";
	tipCtx.font="bold 30px Arial"; 
	
	//画背景
	var backImg=new Image();
	backImg.src="imgs/back.jpg";
	backImg.onload=function(){
		backCtx.drawImage(backImg,0,0,SIZE,SIZE);
	}
	//初始化格子
	for(var i=0;i<15;i++){
		cells[i]=new Array();
		for(var j=0;j<15;j++){
			cells[i][j]=new cellObject(i,j);
		}
	}
	//初始化用户信息
	var name=getQueryString("name");
	imgIndex=getQueryString("img");
	setUserMsg(name,imgIndex,"user1","rank1","meImg");
	//画布点击
	tipCan.onclick=function(e){
		if(isEnd){
			clearText();
			gameReset();
			return;
		}
		if(!isStart){
			alert("游戏未开始,等待双方准备完毕");
			return;
		}
		if(isTurn){
			Label1:
			for(var i=0;i<15;i++){
				for(var j=0;j<15;j++){
					if(cells[i][j].isInclude(e.offsetX,e.offsetY)){
						if(cells[i][j].isClick){
							alert("不能下这里");
						}else{
							cells[i][j].draw(gameCtx,myChessImg);
							cells[i][j].who = 1;
							webSocket.send(CMD_STEP+":"+i+","+j);
							if(isWin(i,j)){
								txt.value += SYSMSG+"恭喜您获得胜利\n";
								webSocket.send(CMD_GAME+":"+GAME_END);
								isEnd=true;
								isReady=false;
								isReady2=false;
								drawText("恭喜您获得胜利,点击重新开始",false);

								upGameNum(++myWinNum);
							} else {
								switchState();
							}
						}
						
						break Label1;
					}
				}
			}
		} else {
			alert("等待对方下棋");
		}

	}
	//点击发送聊天
	document.getElementById("sendBtn").onclick=function(){
		var contant = document.getElementById("send").value;
		var s =   CMD_MSG + ":" + contant;
		txt.value += "我:" + contant + "\n";
		if(!isWatting){
			webSocket.send(s);
		}
	}
	//准备按钮
	document.getElementById("readyBtn").onclick=function(){
		if(isReady)
			return;
		readyGame(true);
		//对方已接入
		if(!isWatting){
			webSocket.send(CMD_GAME + ":" +GAME_READY);
			if(isReady2){
				startGame();
			}
		}
	}
	//求和按钮
	document.getElementById("sumBtn").onclick=function(){
		
	}
	//悔棋按钮
	document.getElementById("undoBtn").onclick=function(){
		
	}
	//认输按钮
	document.getElementById("giveupBtn").onclick=function(){
		
	}
}
//初始化连接
gobang.initCon = function(){
	webSocket=new WebSocket(URL_ADDRESS);
	txt.value += SYSMSG+"正在连接...\n";
	webSocket.onopen=function(e){
		txt.value += SYSMSG+"连接成功\n";
		webSocket.send(CMD_PLAYER_MSG+":"+getMyMsg());
	}
	webSocket.onerror=function(e){
		txt.value += SYSMSG+"连接出现错误\n";
	}
	webSocket.onclose=function(e){
		txt.value += SYSMSG+"已关闭连接\n";
	}
	webSocket.onmessage=function(e){
		var attr = e.data.split(":");
		var cmd = attr[0];
		var dat = attr[1];
		
		switch(cmd){
		case CMD_PLAYER_WHO:
			setWho(dat);
			break;
		case CMD_PLAYER_MSG:
			setOtherMsg(dat);
			break;
		case CMD_STEP:
			drawChess(dat);
			break;
		case CMD_GAME:
			setGame(dat);
			break;
		case CMD_MSG:
			txt.value += otherName +":"+ dat+"\n";
			break;
		}
	}
}

/**
 * 设置是玩家1还是2
 * */
var setWho=function(data){
	who=data;
	if(who==PLAYER1){
		isTurn = true;
		myChessImg.src="imgs/player1.png";
		otherChessImg.src="imgs/player2.png";
		txt.value += SYSMSG + "您执黑棋\n";
		txt.value += SYSMSG + "请准备\n";
		if(!isWatting){
			alert("您执黑棋,请准备");
		} else {
			alert("您执黑棋,等待对方接入...");
		}
	} else {
		isTurn = false;
		myChessImg.src="imgs/player2.png";
		otherChessImg.src="imgs/player1.png";
		txt.value += SYSMSG + "您执白棋\n";
		txt.value += SYSMSG + "请准备\n";
		if(!isWatting){
			alert("您执白棋,请准备");
		} else {
			alert("已找到对手,您执白棋,请准备");
		}
	}
}
/**
 * 准备游戏
 * */
var readyGame=function(isme){
	
	if(isme){
		isReady=true;
		myTurnImg.src = 
			who==PLAYER1 ? "imgs/player_black.png" :"imgs/player_white.png";
		myTurnImg.style.visibility="visible";
		txt.value+="我:已准备\n";
	} else {
		isReady2=true;
		otherTurnImg.src = 
			who==PLAYER2 ? "imgs/player_black.png" :"imgs/player_white.png";
		otherTurnImg.style.visibility="visible";
		txt.value+=otherName+":已准备\n";
	}
}
/**
 * 重新开始
 * */
var gameReset=function(){
	for(var i=0;i<15;i++)
		for(var j=0;j<15;j++)
			cells[i][j].clear(gameCtx);
	isEnd=false;
	isStart=false;
	isReady=false;

	setWho(who==PLAYER1? PLAYER2 : PLAYER1);
	myTurnImg.style.visibility="hidden";
	otherTurnImg.style.visibility="hidden";
	//复位之前是否有新数据到来
	if(!isReady2New){
		isReady2=false;
	} else if(isReady2){
		otherTurnImg.src = 
			who==PLAYER2 ? "imgs/player_black.png" :"imgs/player_white.png";
		otherTurnImg.style.visibility="visible";
		txt.value+=otherName+":已准备\n";
		isReady2New=false;
	}

}
/**
 * 设置游戏状态
 * */
var setGame=function(data){
	switch(data){
	case GAME_READY:
		//游戏结束，未复位
		if(isEnd){
			isReady2=true;
			isReady2New=true;
		} else {
			isReady2New=false;
			readyGame(false);
			if(isReady){
				startGame();
			}
		}
		break;
	case GAME_END:
		txt.value += SYSMSG + "您输了\n";
		isEnd=true;
		drawText("您输了,点击重新开始",false);
		upGameNum(myWinNum);
		break;
	}
}

/**
 * 切换状态
 * */
var switchState=function(){
	isTurn=!isTurn;
	//显示自己
	if(isTurn){
		myTurnImg.style.visibility="visible";
		otherTurnImg.style.visibility="hidden";
	} else {
		myTurnImg.style.visibility="hidden";
		otherTurnImg.style.visibility="visible";
	}
	
}

/**
 * 画棋子
 * */
var drawChess=function(data){
	var attr=data.split(",");
	cells[attr[0]][attr[1]].draw(gameCtx,otherChessImg);
	cells[attr[0]][attr[1]].who = 2;
	switchState();

}

/**
 * 开始游戏
 * */
var startGame=function(){
	txt.value += SYSMSG + "游戏开始\n";
	isStart=true;
	clearText();
	//隐藏白的
	if(who==PLAYER1){
		alert("游戏开始,我方先行");
		otherTurnImg.style.visibility="hidden";
		
	} else {
		alert("游戏开始,等待对方先行");
		myTurnImg.style.visibility="hidden";
	}
}

/**
 * 判断是否胜利
 * */
var isWin = function(x,y){
	var n=1;
	var i;
	//判断横着的
	for(i=1;i<5 && y-i >= 0 && cells[x][y-i].who==1;n++,i++);
	for(i=1;i<5 && y+i < 15 && cells[x][y+i].who==1;n++,i++);
	if(n>=5)
		return true;
	n=1;
	//判断竖着的
	for(i=1;i<5 && x-i >= 0 && cells[x-i][y].who==1;n++,i++);
	for(i=1;i<5 && x+i < 15 && cells[x+i][y].who==1;n++,i++);
	if(n>=5)
		return true;
	n=1;
	//判断左斜
	for(i=1;i<5 && x-i >= 0 && y-i>=0 && cells[x-i][y-i].who==1;n++,i++);
	for(i=1;i<5 && x+i < 15 && y+i<15 && cells[x+i][y+i].who==1;n++,i++);
	if(n>=5)
		return true;
	n=1;
	//判断右斜
	for(i=1;i<5 && x-i >= 0 && y+i<15 && cells[x-i][y+i].who==1;n++,i++);
	for(i=1;i<5 && x+i < 15 && y-i>=0 && cells[x+i][y-i].who==1;n++,i++);
	
	return n>=5;
}
/**
 * 更新局数
 * */
var upGameNum=function(winNum){
	gameNum++;
	gnum1.innerHTML = gnum2.innerHTML = gameNum;
	win1.innerHTML = winNum;
	win2.innerHTML = gameNum - winNum;
}

/**
 * 格子类
 * */
var cellObject=function(x,y){
	this.locX = 23 + y * 36;
	this.locY = 23 + x * 36;
	this.isClick=false;
	this.who = 0;
	//x,y坐标处是否包含这个格子
	cellObject.prototype.isInclude=function(x,y){
		if(x >= this.locX - CELL_SIZE/2 &&
				x <= this.locX + CELL_SIZE/2 &&
				y >= this.locY - CELL_SIZE/2 &&
				y <= this.locY + CELL_SIZE/2){
			return true;
		} else{
			return false;
		}
	}
	//画棋子
	cellObject.prototype.draw=function(ctx,img){
		ctx.drawImage(img,this.locX - CELL_SIZE /2 ,this.locY -CELL_SIZE/2,
				CELL_SIZE,CELL_SIZE);
		this.isClick=true;
	}
	//清除棋子
	cellObject.prototype.clear=function(ctx){
		ctx.clearRect(this.locX - CELL_SIZE /2 ,this.locY -CELL_SIZE/2,
				CELL_SIZE,CELL_SIZE);
		this.isClick=false;
		this.who=0;
	}
	
}

/**
 * 得到自己的信息
 * 为了发给对方
 * */
var getMyMsg=function(){
	var name = document.getElementById("user1").innerHTML;
	
	return name+","+imgIndex;
}

/**
 * 设置对方信息
 * 此时说明对方已连入
 * */
var setOtherMsg=function(str){
	
	var attr=str.split(",");
	otherName=attr[0];
	setUserMsg(attr[0],attr[1],"user2","rank2","nomeImg");
	isWatting=false;
	
	if(isReady){
		webSocket.send(CMD_GAME+":"+GAME_READY);
	}
}

/**
 * 设置玩家的信息
 * */
var setUserMsg=function(name,imgIndex,nameId,rankId,imgId){
	document.getElementById(nameId).innerHTML = name;
	switch(imgIndex){
	case "0":
		document.getElementById(rankId).innerHTML = "青铜";
		document.getElementById(imgId).src = "imgs/look0.jpg";
		break;
	case "1":
		document.getElementById(rankId).innerHTML = "白银";
		document.getElementById(imgId).src = "imgs/look1.jpg";
		break;
	case "2":
		document.getElementById(rankId).innerHTML = "黄金";
		document.getElementById(imgId).src = "imgs/look2.jpg";
		break;
	case "3":
		document.getElementById(rankId).innerHTML = "铂金";
		document.getElementById(imgId).src = "imgs/look3.jpg";
		break;
	case "4":
		document.getElementById(rankId).innerHTML = "钻石";
		document.getElementById(imgId).src = "imgs/look4.jpg";
		break;
	case "5":
		document.getElementById(rankId).innerHTML = "大师";
		document.getElementById(imgId).src = "imgs/look5.jpg";
		break;
	case "6":
		document.getElementById(rankId).innerHTML = "王者";
		document.getElementById(imgId).src = "imgs/look6.jpg";
		break;
	}
}

/**
 * 画字
 * */
var drawText=function(str,isClear){
	tipCtx.fillText(str,275,100);
	if(isClear){
		setTimeout("tipCtx.clearRect(0,0,550,120)",4000);
	}
}

/**
 * 清除字
 * */
var clearText=function(){
	tipCtx.clearRect(0,0,550,120);
}

/**
 * 得到地址栏的参数
 * */
function getQueryString(name)
{
     var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
     var r = decodeURI(window.location.search.substr(1)).match(reg);
     if(r!=null)return  unescape(r[2]); return null;
}




