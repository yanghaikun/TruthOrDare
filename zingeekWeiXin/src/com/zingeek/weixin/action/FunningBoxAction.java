package com.zingeek.weixin.action;

import java.io.Serializable;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import com.ocpsoft.pretty.faces.util.StringUtils;
import com.zingeek.core.support.Utils;
import com.zingeek.support.Req;
import com.zingeek.weixin.entity.RoomFB;
import com.zingeek.weixin.entity.UserFB;
import com.zingeek.weixin.manager.FunningBoxManager;

/**
 * 用户所有行为入口
 * @author Vincent Hiven Yang
 *
 */
@Path("/funningBox")
public class FunningBoxAction implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject Req req;
	@Inject FunningBoxManager funningBoxManager;
	
	
	/**
	 *测试接口
	 * @return
	 */
	@GET @POST @Path("/test")
	public String test() {
		String ToUserName = req.getString("ToUserName");
		String FromUserName = req.getString("FromUserName");
		String CreateTime = req.getString("CreateTime");
		String MsgType = req.getString("MsgType");
		String Content = req.getString("Content");
		String MsgId = req.getString("MsgId");
		
		
		//解析成功
        UserFB user = funningBoxManager.getUser(FromUserName);
        //文字信息
        String response  = "";
        if("text".equals(MsgType)) {
        	response = parseTextTest(Content, user);
        	//return responseText(FromUserName, ToUserName, System.currentTimeMillis(), response);
        } else {
        	response = "欢迎体验奇趣盒，这里将会有各种经典的小游戏，现在的游戏是：谁在哪儿干什么！（大家一起输入自己在哪里干什么，然后随机组合，生成各种爆笑结果，如：小明在白宫吃糖葫芦。。。尽情发挥你的想象恶搞）。【开】：创建房间；【进】：加入房间；【玩】：开始游戏；【笑】：游戏结果！";
        }
        
        return response;
	}
	/**
	 * 解析用户发来的文字并回复
	 * @param Content
	 * @return
	 */
	public String parseTextTest(String Content, UserFB user) {
		String response = "";
		if(StringUtils.isBlank(Content)) {
			return response;
		}
		
		if ("T".equals(Content)) {
			if (user.status <= 10) {
				response = "你没有在房间里";
			} else {
				funningBoxManager.out(user);
				response = "你已经退出了房间。";
			}
			return response;
		} 
		
		int status = user.status;
		switch (status) {
		//什么都没做呢
		case 0:
			if("J".equals(Content)) {
				user.status = 10;
				response = "请输入房间号：如   8888";
			} else if ("K".equals(Content)) {
				funningBoxManager.start(user);
				response = "你的房间号是：" + user.roomId + "，邀请你的朋友们加入吧，大家全部加入后发送【玩】开始游戏。";
			} else {
				response = "【开】：创建房间；【进】：加入房间；【玩】：开始游戏；【笑】：游戏结果！";
			}
			break;
		//输入过进了	
		case 10:
			//是数字
			if(Content.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$")) {
				int roomId = Utils.intValue(Content);
				RoomFB room = funningBoxManager.getRooms().get(roomId);
				if (roomId < 0 || room == null) {
					response = "房间号不正确，重新输入房间号或者【开】一个新房间。";
				} else {
					room.users.add(user);
					user.status = 11;
					user.roomId = roomId;
					funningBoxManager.getUsers().put(user.id, user);

					response = "你已经加入了房间：【" + user.roomId + "】，如果大家都已经进入房间，发送【玩】开始游戏。";
				}
			} else {
				response = "房间号不正确，重新输入房间号或者【开】一个新房间。房间号是一个四位数字";
			}
			break;
		//在房间中
		case 11:
			if("W".equals(Content)) {
				user.status = 30;
				response = "游戏开始了！首先输入你的名字（可以是代号昵称，但是必须要大家都知道是你）：";
			} else {
				response = "你在房间：" + user.roomId + "中，如果大家都已经进入房间准备好了，发送【玩】开始游戏。";
			}
			break;
		//开始玩游戏，等待输入名字
		case 30:
			user.who = Content;
			user.status = 31;
			response = "你刚刚输入了自己的名字，现在请输入一个地点，可以是任何的地方，比如普吉岛，或者厕所。。。";
			break;
		case 31:
			user.where = Content;
			user.status = 32;
			response = "你刚刚输入了一个地点，现在请输入一件事儿，可以是任何事情，比如吃饭，看书，便便。。。";
			break;
		case 32:
			user.what = Content;
			user.status = 33;
			response = "很好，你已经完成输入了，确保大家都输入完成后，输入【笑】来查看游戏结果，笑翻天";
			funningBoxManager.getRooms().get(user.roomId).check();
			break;
		case 33:
			if("X".equals(Content)) {
				RoomFB room = funningBoxManager.getRooms().get(user.roomId);
				if(!room.ready) {
					String us = "";
					for(UserFB u : room.users) {
						if(u.status < 33) {
							us += u.who + ",";
						}
					}
					response = "这些玩家还没完成输入：" + us;
				} else {
					user.status = 11;
					response = "你领到的爆笑结果是：" + room.result.get(user.id) + "；大家安顺序大声念出来！- _-@";
				}
			} else {
				response = "如果大家都输入完成，输入【笑】来查看游戏结果，笑翻天";
			}
			break;
			
		default:
			response = "【开】：创建房间；【进】：加入房间；【玩】：开始游戏；【笑】：游戏结果！";
			break;
		}
		
		return response;
	}
	
	@GET @POST @Path("/play")
	public String play() {
		//验证接口
		String echostr = req.getString("echostr");
		if(!StringUtils.isBlank(echostr)) {
			return echostr;
		}
		
		HttpServletRequest request = req.getHttpServletRequest();
		//解析对方发来的xml数据，获得EventID节点的值    
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();    
        DocumentBuilder db;
        Document d = null;
		try {
			db = dbf.newDocumentBuilder();
			d = db.parse(request.getInputStream()); 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    
         // 请求时传递的哪些值，需要的就取出来。。  
        String ToUserName = d.getElementsByTagName("ToUserName").item(0).getFirstChild().getNodeValue();   
        String FromUserName = d.getElementsByTagName("FromUserName").item(0).getFirstChild().getNodeValue();  
        //String CreateTime = d.getElementsByTagName("CreateTime").item(0).getFirstChild().getNodeValue();  
        String MsgType = d.getElementsByTagName("MsgType").item(0).getFirstChild().getNodeValue();  
        //String MsgId = d.getElementsByTagName("MsgId").item(0).getFirstChild().getNodeValue();  
        
        //解析成功
        UserFB user = funningBoxManager.getUser(FromUserName);
        //文字信息
        String response = "";
        if("text".equals(MsgType)) {
        	String Content = d.getElementsByTagName("Content").item(0).getFirstChild().getNodeValue();  
        	response = parseText(Content, user);
        } else {
        	//事件，关注或者取消关注
        	response = "欢迎体验奇趣盒，这里将会有各种经典的小游戏，现在的游戏是：谁在哪儿干什么！（大家一起输入自己在哪儿干什么，然后随机组合，生成各种爆笑结果，如：小明在白宫打灰机。。。尽情发挥你的想象恶搞）。【开】：创建房间；【进】：加入房间；【玩】：开始游戏；【笑】：游戏结果！      ps：很不好意思，开始是想做真心话大冒险来着，发现有人给做了，现在颇有些像挂羊头卖狗肉的勾当。。。不过还是希望你玩的开心";
        }
        
        return funningBoxManager.responseText(FromUserName, ToUserName, System.currentTimeMillis(), response);
	}
	
	/**
	 * 解析用户发来的文字并回复
	 * @param Content
	 * @return
	 */
	public String parseText(String Content, UserFB user) {
		String response = "";
		if(StringUtils.isBlank(Content)) {
			return response;
		}
		
		if ("退".equals(Content)) {
			if (user.status <= 10) {
				response = "你没有在房间里。【开】：创建房间；【进】：加入房间；【玩】：开始游戏；【笑】：游戏结果！；【退】：退出房间。";
			} else {
				funningBoxManager.out(user);
				response = "你已经退出了房间。【开】：创建房间；【进】：加入房间；【玩】：开始游戏；【笑】：游戏结果！；【退】：退出房间。";
			}
			return response;
		} 
		
		int status = user.status;
		switch (status) {
		//什么都没做呢
		case 0:
			if("进".equals(Content)) {
				user.status = 10;
				response = "请输入房间号：如   8888";
			} else if ("开".equals(Content)) {
				funningBoxManager.start(user);
				response = "你的房间号是：" + user.roomId + "，邀请你的朋友们加入吧，大家全部加入后发送【玩】开始游戏。";
			} else {
				response = "【开】：创建房间；【进】：加入房间；【玩】：开始游戏；【笑】：游戏结果！；【退】：退出房间。";
			}
			break;
		//输入过进了	
		case 10:
			//是数字
			if(Content.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$")) {
				int roomId = Utils.intValue(Content);
				RoomFB room = funningBoxManager.getRooms().get(roomId);
				if (roomId < 0 || room == null) {
					response = "房间号不正确，重新输入房间号或者【开】一个新房间。";
				} else {
					room.users.add(user);
					user.status = 11;
					user.roomId = roomId;
					funningBoxManager.getUsers().put(user.id, user);

					response = "你已经加入了房间：【" + user.roomId + "】，如果大家都已经进入房间，发送【玩】开始游戏。";
				}
			} else {
				response = "房间号不正确，重新输入房间号或者【开】一个新房间。房间号是一个四位数字";
			}
			break;
		//在房间中
		case 11:
			if("玩".equals(Content)) {
				user.status = 30;
				response = "游戏开始了！首先输入你的名字（可以是代号昵称，但是必须要大家都知道是你）：";
			} else {
				response = "你在房间：" + user.roomId + "中，如果大家都已经进入房间准备好了，发送【玩】开始游戏。";
			}
			break;
		case 12:
			if("玩".equals(Content)) {
				user.status = 31;
				response = "你的名字是，" + user.who + "，我记性还行吧。现在请输入一个地点，可以是任何的地方，比如普吉岛，或者厕所。。。";
			} else {
				response = "你在房间：" + user.roomId + "中，如果大家都已经进入房间准备好了，发送【玩】开始游戏。";
			}
			break;
		//开始玩游戏，等待输入名字
		case 30:
			user.who = Content;
			user.status = 31;
			response = "你刚刚输入了自己的名字，现在请输入一个地点，可以是任何的地方，比如普吉岛，或者厕所（发挥想象）。。。";
			break;
		case 31:
			user.where = Content;
			user.status = 32;
			response = "你刚刚输入了一个地点，现在请输入一件事儿，可以是任何事情，比如吃饭，看书，便便（想象力哦，亲）。。。";
			break;
		case 32:
			user.what = Content;
			user.status = 33;
			response = "很好，你已经完成输入了，确保大家都输入完成后，输入【笑】来查看游戏结果，笑翻天";
			funningBoxManager.getRooms().get(user.roomId).check();
			break;
		case 33:
			if("笑".equals(Content)) {
				RoomFB room = funningBoxManager.getRooms().get(user.roomId);
				if(!room.ready) {
					String us = "";
					for(UserFB u : room.users) {
						if(u.status < 33) {
							us += u.who + ",";
						}
					}
					response = "这些玩家还没完成输入：" + us;
				} else {
					user.status = 12;
					response = "你领到的爆笑结果是：" + room.result.get(user.id) + "；大家按顺序大声念出来！-_-@....输入【玩】开始下一局游戏";
				}
			} else {
				response = "如果大家都输入完成，输入【笑】来查看游戏结果，笑翻天";
			}
			break;
			
		default:
			response = "【开】：创建房间；【进】：加入房间；【玩】：开始游戏；【笑】：游戏结果！；【退】：退出房间。";
			break;
		}
		
		return response;
	}
	
}
