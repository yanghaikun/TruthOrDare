package com.zingeek.weixin.action;

import java.io.Serializable;
import java.util.List;

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
import com.zingeek.weixin.entity.RoomTOD;
import com.zingeek.weixin.entity.UserTOD;
import com.zingeek.weixin.manager.ManagerTOD;
import com.zingeek.weixin.support.enumkey.StatusKeyTOD;

/**
 * 用户所有行为入口
 * @author Vincent Hiven Yang
 *
 */
@Path("/truthOrDare")
public class ActionTOD implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject Req req;
	@Inject ManagerTOD managerTOD;
	
	private static final List<String> 帮 = Utils.ofList("帮", "幫", "帮助", "幫助", "B", "b");
	private static final List<String> 退 = Utils.ofList("退", "T", "t");
	private static final List<String> 进 = Utils.ofList("进", "進", "J", "j");
	private static final List<String> 开 = Utils.ofList("开", "開", "K", "k");
	private static final List<String> 玩 = Utils.ofList("玩", "W", "w");
	private static final List<String> 查 = Utils.ofList("查", "C", "c");
	public static final List<String> 真 = Utils.ofList("真", "真心话", "Z", "z");
	public static final List<String> 大 = Utils.ofList("大", "大冒险", "D", "d");
	
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
        UserTOD user = managerTOD.getUser(FromUserName);
        //文字信息
        String response  = "";
        if("text".equals(MsgType)) {
        	response = parseText(Content, user);
        	//return responseText(FromUserName, ToUserName, System.currentTimeMillis(), response);
        } else {
        	response = "欢迎关注真心话大冒险助手。创建房间请输入【开】或者【k】；加入朋友的房间请输入【进】或者【j】。在游戏中，创建房间的玩家为1号，负责控制游戏开始，其他玩家按提示参与即可。";
        }
        
        return response;
	}
	
	/**
	 * 解析用户发来的文字并回复
	 * @param Content
	 * @return
	 */
	public String parseTextTest(String Content, UserTOD user) {
		String response = "";
		if(StringUtils.isBlank(Content)) {
			return response;
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
        UserTOD user = managerTOD.getUser(FromUserName);
        //文字信息
        String response = "";
        if("text".equals(MsgType)) {
        	String Content = d.getElementsByTagName("Content").item(0).getFirstChild().getNodeValue();  
        	response = parseText(Content, user);
        } else {
        	//事件，关注或者取消关注
        	response = "欢迎关注真心话大冒险助手。输入以下命令进行游戏：【开】或者【k】：创建房间；【进】或者【j】：加入房间；【退】或者【t】：退出房间；【帮】或者【b】：帮助信息。在游戏中，创建房间的玩家为1号，负责控制游戏开始，其他玩家输入【进】或者【j】加入房间即可开始游戏，所有真心话问题和大冒险惩罚都需要现场执行。友情提示：真心话大冒险得和一帮死党玩才过瘾，问题和惩罚都很劲爆，如果你心理素质不行，还是不要和大家玩了，免得成了笑话。";
        }
        
        return managerTOD.responseText(FromUserName, ToUserName, System.currentTimeMillis(), response);
	}
	
	/**
	 * 解析用户发来的文字并回复
	 * @param Content
	 * @return
	 */
	public String parseText(String Content, UserTOD user) {
		String response = "";
		if(StringUtils.isBlank(Content)) {
			return response;
		}
		if(退.contains(Content)) {
			if(user.roomId == 0 && user.room == null) {
				response = "你没有在房间里。输入【开】或者【k】创建新房间，【进】或者【j】加入房间。";
			} else {
				managerTOD.removeUser(user);
				user.room.users.remove(user);
				if(user.room.users.size() == 0) {
					response = "你已经成功退出房间：" + user.roomId + "，输入【开】或者【k】创建新房间，【进】或者【j】加入房间。";
					user.room.clear();
					user.room.canPlay = false;
				} else {
					response = "你已经成功退出房间：" + user.roomId + "，现在这个房间的顺序都已经打乱了，请告知剩余童鞋重新创建房间进行游戏。输入【开】或者【k】创建新房间，【进】或者【j】加入房间。";
					user.room.canPlay = false;
				}
				user = null;
			}
			
			return response;
		}
		
		//寻求帮助
		if(帮.contains(Content) || Content.contains("怎么") || Content.contains("如何") || Content.contains("怎样")) {
			response = "帮助中心：当你创建好一个房间后，把房间号告诉你的朋友，让他们输入【进】或者【j】来加入你的房间，当大家都加入后，由房间的创建者输入【玩】或者【w】开始游戏。这只是一个游戏助手，所有的真心话问题和大冒险惩罚都需要大家现场执行，have fun.";
			return response;
		}
		
		if(user.room != null && !user.room.canPlay) {
			response = "房间：" + user.roomId + "，有人中途退出，请重新创建房间来进行游戏。输入【退】或者【t】退出房间。";
			return response;
		}
		
		//根据不同的状态，判断各种输入应给与的回复
		switch (user.status) {
		case 闲:
			if(开.contains(Content)) {
				user.status = StatusKeyTOD.开;
				response = "几个人参加本次游戏，请输入人数，（1-50的一个数字），如 8。";
			}
			else if(进.contains(Content)) {
				user.status = StatusKeyTOD.进;
				response = "请输入房间号：房间号是一个四位的数字，如 8888。";
			} else {
				response = "输入：【开】或者【k】：创建房间；【进】或者【j】：加入房间；【退】或者【t】：退出房间；【帮】或者【b】：帮助信息。";
			}
			break;
			//玩家输入过开
		case 开:
			//输入的是数字
			if(Content.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$")) {
				int num = Utils.intValue(Content);
				if(num < 1 || num > 50) {
					response = "就你这脑子还来玩这游戏，再问你一遍，几个人参加本次游戏，输入人数，（1-50的一个数字），如 8。";
				} else {
					managerTOD.createRoom(user, num);
					user.status = StatusKeyTOD.准;
					response = "你的房间号是：" + user.roomId + "，本房间最大玩家数为：" + num + "，你在游戏中的编号为1号，是本房间游戏发号施令者，邀请你的朋友们加入吧（让你的朋友输入【进】或者【j】，按提示输入房间号，即可加入本房间），大家全部加入后，由你输入【玩】或者【w】开始游戏。";
				}
			} else if(进.contains(Content)) {
				user.status = StatusKeyTOD.进;
				response = "请输入房间号：房间号是一个四位的数字，如 8888。";
			} else {
				response = "几个人参加本次游戏，请输入人数（1-50的一个数字），如 8。加入其它房间，输入【进】或者【j】";
			}
			break;
		case 进:
			//输入的是数字
			if(Content.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$")) {
				int roomId = Utils.intValue(Content);
				RoomTOD room = managerTOD.getRoom(roomId);
				if(room == null) {
					response = "你输入的房间不存在，请确认房间号，或者输入【开】或者【k】创建一个新房间。";
				} else {
					if(!room.canPlay) {
						return response = "房间：" + room.id + "由于中途有人退出，现在不能加入。输入【开】或者【k】创建一个新房间。";
					}
					if(!room.canJoin()) {
						response = "房间：" + room.id + "已满，请换一个房间，或者输入【开】或者【k】创建一个新房间。";
					} else {
						room.join(user);
						user.room = room;
						user.status = StatusKeyTOD.准;
						if(user.num < room.count) {
							response = "你成功加入到房间：" + room.id + "，在本房间中，你的编号是：" + user.num + "。请等待剩下的：" + (room.count - room.users.size()) + "名玩家加入游戏。当所有玩家都加入游戏后由1号玩家输入【玩】或者【w】来开始游戏，之后，你可以输入【查】或者【c】来查看谁最倒霉。";
						} else {
							response = "你成功加入到房间：" + room.id + "，在本房间中，你的编号是：" + user.num + "。所有玩家都已经加入。现在请告知1号玩家输入【玩】或者【w】来开始游戏，之后，你可以输入【查】或者【c】来查看谁中招了";
						}
						
					}
				}
			} 
			else if(开.contains(Content)) {
				user.status = StatusKeyTOD.开;
				response = "几个人参加本次游戏，请输入人数，（1-50的一个数字），如 8。";
			}
			else {
				response = "请输入房间号：房间号是一个四位的数字，如 8888。如果还没有房间，输入【开】或者【k】创建一个新房间。";
			}
			break;
		case 准:
			RoomTOD room = user.room;
			if(玩.contains(Content)) {
				if(!room.isReady()) {
					if(user.num != 1) {
						response = "请等待所有玩家都加入房间后再由1号童鞋输入【玩】或者【w】开始本轮游戏，现在还有：" + (room.count - room.users.size()) + "名玩家没有加入。";
					} else {
						response = "请等待所有玩家都加入房间后再输入【玩】或者【w】开始本轮游戏，现在还有：" + (room.count - room.users.size()) + "名玩家没有加入。";
					}
					
				} else {
					if(user.num != 1) {
						if(!StringUtils.isBlank(user.room.truthOrDare)) {
							response = "游戏已经开始了，输入【查】或者【c】来查看本次游戏的倒霉蛋是谁 -_-@";
						} else {
							response = "你无权启动游戏，请编号为：1 的童鞋输入【玩】或者【w】来产生倒霉蛋";
						}
						
					} else {
						if(!room.start) {
							room.random();
							response = "倒霉蛋已经产生了，现在请输入【查】或者【c】来查看本次游戏的倒霉蛋是谁 -_-@，同时请告知大家输入【查】或者【c】来查看结果";
						} else {
							response = "本局游戏正在进行中，现在请等待倒霉蛋选择真心话还是大冒险，输入【查】或者【c】来查看本次游戏的倒霉蛋是谁 -_-@，同时请告知大家输入【查】或者【c】来查看结果";
						}
						
					}
				}
			}
			else if(查.contains(Content)) {
				if(user.knew) {
					if(user.num != user.room.random) {
						if(user.num == 1) {
							response = "本局的结果你不是已经知道了吗？如果倒霉蛋的表现已经让大家满意了!那赶快输入【玩】或者【w】开始新一轮游戏吧";
						} else {
							response = "本局的结果你不是已经知道了吗？如果倒霉蛋的表现已经让大家满意了？!那让赶快让1号童鞋输入【玩】或者【w】开始新一轮游戏吧";
						}
						
					} else {
						if(user.num == 1) {
							response = "本局的结果你不是已经知道了吗？如果你的表现已经让大家满意了？!那赶快输入【玩】或者【w】开始新一轮游戏吧";
						} else {
							response = "本局的结果你不是已经知道了吗？如果你的表现已经让大家满意了？!那让赶快让1号童鞋输入【玩】或者【w】开始新一轮游戏吧";
						}
					}
				} else {
					if(user.room.random == 0) {
						if(!room.isReady()) {
							if(user.num != 1) {
								response = "请等待所有玩家都加入房间后再由1号童鞋输入【玩】或者【w】开始本轮游戏，现在还有：" + (room.count - room.users.size()) + "名玩家没有加入。";
							} else {
								response = "请等待所有玩家都加入房间后再由你输入【玩】或者【w】开始本轮游戏，现在还有：" + (room.count - room.users.size()) + "名玩家没有加入。";
							}
						} else {
							if(user.num != 1) {
								response = "游戏还未开始，赶快催促一号童鞋输入【玩】或者【w】开始游戏吧。";
							} else {
								response = "游戏还未开始，你是游戏的控制者，赶快输入【玩】或者【w】开始游戏吧";
							}
						}
					} else {
						if(user.room.random != user.num) {
							if(StringUtils.isBlank(user.room.truthOrDare)) {
									response = "倒霉蛋是：" + user.room.random + "号童鞋，丫还在纠结呢，真心话呢还是大冒险呢，给TA点压力，让TA快一点。继续输入【查】或者【c】来看结果。";
								} else {
									response = "倒霉蛋是：" + user.room.random + "号童鞋，丫选择了" + user.room.choice + "，问题是：" + user.room.truthOrDare + "。TA的表现让大家满意后，请1号童鞋输入【玩】或者【w】开始新一轮游戏";
									user.knew = true;
								}
							} else {
								if(StringUtils.isBlank(user.room.truthOrDare)) {
									user.status = StatusKeyTOD.霉;
									response = "不用怀疑了，倒霉蛋就是你了。现在你可以输入【真】或者【z】选择真心话，输入【大】或者【d】来选择大冒险。当然你也可以装作什么都没发生。对了，你的号码是：" + user.num + "号。";
								} else {
									if(user.num != 1) {
										response = "你的表现已经让大家满意了？!那让1号童鞋输入【玩】或者【w】开始新一轮游戏吧";
									} else {
										response = "你的表现已经让大家满意了？!赶快输入【玩】或者【w】开始新一轮游戏吧";
									}
									
								}
							}
					}
					}
			} 
			else {
				if(user.room.random == 0) {
					if(user.num == 1) {
						if(!room.isReady()) {
							response = "请等待所有玩家都加入房间后输入【玩】或者【w】开始本轮游戏，现在还有：" + (room.count - room.users.size()) + "名玩家没有加入。【退】或者【t】退出本房间";
						} else {
							response = "输入【玩】或者【w】开始本轮游戏。【退】或者【t】退出本房间";
						}
					} else {
						if(!room.isReady()) {
							response = "请等待所有玩家都加入房间后由1号童鞋输入【玩】或者【w】开始本轮游戏，现在还有：" + (room.count - room.users.size()) + "名玩家没有加入。【退】或者【t】退出本房间";
						} else {
							response = "所有玩家都已加入，告知1号童鞋输入【玩】或者【w】开始本轮游戏，【退】或者【t】退出本房间";
						}
					}
				} else {
					if(!user.knew) {
							response = "输入【查】或者【c】查看本局的结果。";
					} else {
						if(user.num != user.room.random) {
							if(user.num == 1) {
								response = "倒霉蛋的表现已经让大家满意了？!那赶快输入【玩】或者【w】开始新一轮游戏吧";
							} else {
								response = "倒霉蛋的表现已经让大家满意了？!那让赶快1号童鞋输入【玩】或者【w】开始新一轮游戏吧";
							}
							
						} else {
							if(user.num == 1) {
								response = "你的表现已经让大家满意了？!那赶快输入【玩】或者【w】开始新一轮游戏吧";
							} else {
								response = "你的表现已经让大家满意了？!那让赶快1号童鞋输入【玩】或者【w】开始新一轮游戏吧";
							}
						}
					}
				}
	
			}
			break;
		case 霉:
			//真心话
			if(真.contains(Content)) {
				user.status = StatusKeyTOD.准;
				response = managerTOD.truthOrDare(Content);
				user.room.choice = "真心话";
				user.room.truthOrDare = response;
				user.room.start = false;
				user.knew = true;
			}
			else if(大.contains(Content)) {
				user.status = StatusKeyTOD.准;
				response = managerTOD.truthOrDare(Content);
				user.room.choice = "大冒险";
				user.room.truthOrDare = response;
				user.room.start = false;
				user.knew = true;
			}
			else if ("ustc".equals(Content)) {
				user.status = StatusKeyTOD.准;
				response = "人品爆发，免除本轮！";
				user.room.choice = "大冒险";
				user.room.truthOrDare = response;
				user.room.start = false;
				user.knew = true;
			}
			else {
				response = "你丫傻还是怎么着，让你丫输入【真】或者【z】选择真心话，【大】或者【d】选择大冒险，看不懂国语？";
			}
			break;
		default:
			response = "输入：【开】或者【k】：创建房间；【进】或者【j】：加入房间；【退】或者【t】：退出房间。";
			break;
		}
		
		
		return response;
	}
	
	
}
