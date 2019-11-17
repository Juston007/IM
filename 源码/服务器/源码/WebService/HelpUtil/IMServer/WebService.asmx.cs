using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Services;
using IM;
using HelpUtil;
using System.Collections;
using Newtonsoft.Json;

namespace IMServer
{
    /// <summary>
    /// WebService 的摘要说明
    /// </summary>
    [WebService(Namespace = "http://tempuri.org/")]
    [WebServiceBinding(ConformsTo = WsiProfiles.BasicProfile1_1)]
    [System.ComponentModel.ToolboxItem(false)]
    // 若要允许使用 ASP.NET AJAX 从脚本中调用此 Web 服务，请取消注释以下行。 
    [System.Web.Script.Services.ScriptService]
    public class WebService : System.Web.Services.WebService
    {
        public WebService()
        {
            //初始化连接字符串
            HelpUtil.Config.ConnectionStr = System.Configuration.ConfigurationManager.AppSettings["ConStr"].ToString();
        }

        //**************************************************************************账户相关逻辑**************************************************************************

        [WebMethod(Description = "登入")]
        public void Login(String uid, String pwd)
        {
            String result = String.Empty;
            try
            {
                result = BaseUtil.login(uid, pwd);
                bool issuccess = result != String.Empty;
                Util.writeWeb(Context, Status.Success, issuccess ? Status.Success : Status.Fail, issuccess ? "登入成功" : "账户名称或密码不正确", result);
            }
            catch (Exception ex)
            {
                Util.writeError(Context, ex.Message);
            } 
        }

        [WebMethod(Description = "注册")]
        public void RegUser(String pwd, String alias)
        {
            try
            {
                String uid = BaseUtil.regUser(pwd, alias);
                bool issuccess = uid != String.Empty;
                Util.writeWeb(Context, Status.Success, issuccess ? Status.Success : Status.Fail, issuccess ? "注册成功" : "注册失败,请稍后再试", uid);
            }
            catch (Exception ex)
            {
                Util.writeError(Context, ex.Message);
            }
        }

        [WebMethod(Description = "修改密码")]
        public void ResetPwd(String uid, String oldpwd, String newpwd)
        {
            try
            {
                bool result = BaseUtil.changePwd(uid, oldpwd, newpwd);
                Util.writeWeb(Context, Status.Success, result ? Status.Success : Status.Fail, result ? "修改成功" : "修改失败,请稍后再试", String.Empty);
                if (result)
                {
                    //添加未处理事件
                    BaseUtil.addEvent(uid, EventType.TokenChange);
                }
            }
            catch (Exception ex)
            {
                Util.writeError(Context, ex.Message);
            }
        }

        //**************************************************************************好友相关逻辑**************************************************************************

        [WebMethod(Description = "添加好友")]
        public void AddFriend(String token, String touid)
        {
            try
            {
                String uid = BaseUtil.getUidByToken(token);
                if (uid != String.Empty)
                {
                    String result = BaseUtil.sendFriendRequest(uid, touid);
                    bool issuccess = result == String.Empty;
                    Util.writeWeb(Context, Status.Success, issuccess ? Status.Success : Status.Fail, issuccess ? "发送好友请求成功" : result, String.Empty);
                    if (issuccess)
                    {
                        BaseUtil.addEvent(touid, EventType.FriendRequest);
                    }
                }
                else
                {
                    Util.writeWeb(Context, Status.Success, Status.Fail, "请求非法,请尝试重新登入", String.Empty);
                }
            }
            catch (Exception ex)
            {
                Util.writeError(Context, ex.Message);
            }
        }

        [WebMethod(Description = "删除好友")]
        public void DeleteFriend(String token, String uid1)
        {
            try
            {
                String uid = BaseUtil.getUidByToken(token);
                bool result = uid != String.Empty;
                if (result)
                {
                    result = BaseUtil.deleteFriend(uid, uid1);
                    Util.writeWeb(Context, Status.Success, result ? Status.Success : Status.Fail, result ? "删除成功" : "删除失败", String.Empty);
                    if (result)
                    {
                        BaseUtil.addEvent(uid, EventType.FriendChange);
                        BaseUtil.addEvent(uid1, EventType.FriendChange);
                    }
                }
                else
                {
                    Util.writeWeb(Context, Status.Success, Status.Fail, "请求非法", String.Empty);
                }
            }
            catch (Exception ex)
            {
                Util.writeError(Context, ex.Message);
            }
        }

        [WebMethod(Description = "处理好友请求")]
        public void HandlerFriendRequest(String token, String senduid, int handler)
        {
            try
            {
                String uid = BaseUtil.getUidByToken(token);
                bool result = uid != String.Empty;
                if (result)
                {
                    String handlerresult = BaseUtil.handlerFirendRequest(senduid, uid, (HandlerRequest)handler);
                    result = (handlerresult == String.Empty);
                    Util.writeWeb(Context, Status.Success, result ? Status.Success : Status.Fail, result ? "处理好友请求成功" : handlerresult, String.Empty);
                    if (result)
                    {
                        BaseUtil.addEvent(senduid, EventType.FriendChange);
                        BaseUtil.addEvent(uid, EventType.FriendChange);
                    }
                }
                else
                {
                    Util.writeWeb(Context, Status.Success, Status.Fail, "请求非法", String.Empty);
                }
            }
            catch (Exception ex)
            {
                Util.writeError(Context, ex.Message);
            }
        }

        [WebMethod(Description = "获取好友请求列表")]
        public void GetFriendRequestList(String token)
        {
            try
            {
                String uid = BaseUtil.getUidByToken(token);
                bool result = uid != String.Empty;
                if (result)
                {
                    ArrayList array = BaseUtil.getFriendRequestList(uid);
                    result = array != null;
                    if (result)
                    {
                        String datastr = JsonConvert.SerializeObject(array);
                        Util.writeWeb(Context, Status.Success, Status.Success, "请求待处理好友列表成功", datastr);
                    }
                    else
                    {
                        Util.writeWeb(Context, Status.Success, Status.Fail, "没有待处理的好友请求列表", String.Empty);
                    }
                }
                else
                {
                    Util.writeWeb(Context, Status.Success, Status.Fail, "请求非法", String.Empty);
                }
            }
            catch (Exception ex)
            {
                Util.writeError(Context, ex.Message);
            }
        }

        [WebMethod(Description = "获取好友列表")]
        public void GetFriendList(String token)
        {
            try
            {
                String uid = BaseUtil.getUidByToken(token);
                bool result = uid != String.Empty;
                if (result)
                {
                    ArrayList arr = BaseUtil.getFriendList(uid);
                    if (arr != null)
                    {
                        String datastr = JsonConvert.SerializeObject(arr);
                        Util.writeWeb(Context, Status.Success, Status.Success, "请求好友列表成功", datastr);
                    }
                    else
                    {
                        Util.writeWeb(Context, Status.Success, Status.Fail, "您还没有任何好友", String.Empty);
                    }
                }
                else
                {
                    Util.writeWeb(Context, Status.Success, Status.Fail, "请求非法", String.Empty);
                }
            }
            catch (Exception ex)
            {
                Util.writeError(Context, ex.Message);
            }
        }

        //**************************************************************************聊天相关逻辑************************************************************************

        [WebMethod(Description = "发送信息")]
        public void SendMessage(String token, String touid, int msgtype, String msg)
        {
            try
            {
                String uid = BaseUtil.getUidByToken(token);
                bool result = uid != String.Empty;
                if (result)
                {
                    result = BaseUtil.sendMsg(uid, touid, msg, (MessageType)msgtype);
                    Util.writeWeb(Context, Status.Success, result ? Status.Success : Status.Fail, result ? "发送成功" : "发送失败", String.Empty);
                    if (result)
                        BaseUtil.addEvent(touid, EventType.Message);
                }
                else
                {
                    Util.writeWeb(Context, Status.Success, Status.Fail, "请求非法", String.Empty);
                }
            }
            catch (Exception ex)
            {
                Util.writeError(Context, ex.Message);
            }
        }

        [WebMethod(Description = "获取信息")]
        public void GetMessage(String token, String touid, int isread)
        {
            try
            {
                String uid = BaseUtil.getUidByToken(token);
                bool result = uid != String.Empty;
                if (result)
                {
                    ArrayList arr = BaseUtil.receiverMsg(uid, touid, isread == 1);
                    result = (arr != null);
                    String datastr = JsonConvert.SerializeObject(arr);
                    Util.writeWeb(Context, Status.Success, result ? Status.Success : Status.Fail, result ? "接收成功" : "没有可以接收的信息", datastr);
                }
                else
                {
                    Util.writeWeb(Context, Status.Success, Status.Fail, "请求非法", String.Empty);
                }
            }
            catch (Exception ex)
            {
                Util.writeError(Context, ex.Message);
            }
        }

        [WebMethod(Description = "获取信息内容")]
        public void GetMessageContent(String token,String msgid,int msgtype)
        {
            try
            {
                String uid = BaseUtil.getUidByToken(token);
                bool result = uid != String.Empty;
                if (result)
                {
                    String datastr = BaseUtil.getMessageContent(msgid, (MessageType)msgtype);
                    result = datastr != String.Empty;
                    Util.writeWeb(Context, Status.Success, result ? Status.Success : Status.Fail, result ? "获取信息内容成功" : "没有找到此信息", datastr);
                }
                else
                {
                    Util.writeWeb(Context, Status.Success, Status.Fail, "请求非法", String.Empty);
                }
            }
            catch (Exception ex)
            {
                Util.writeError(Context, ex.Message);
            }
        }

        //**************************************************************************资料相关逻辑************************************************************************

        [WebMethod(Description = "查询账户资料")]
        public void GetUserInfo(String token,String queryuid)
        {
            try
            {
                String uid = BaseUtil.getUidByToken(token);
                bool result = uid != String.Empty;
                if (result)
                {
                    UserInfoModel model = BaseUtil.getUserInfo(queryuid);
                    result = model != null;
                    String datastr = String.Empty;
                    if (result)
                        datastr = JsonConvert.SerializeObject(model);
                    Util.writeWeb(Context, Status.Success, result ? Status.Success : Status.Fail, result ? "获取账户资料成功" : "没有找到此账户", datastr);
                }
                else
                {
                    Util.writeWeb(Context, Status.Success, Status.Fail, "请求非法", String.Empty);
                }
            }
            catch (Exception ex)
            {
                Util.writeError(Context, ex.Message);
            }
        }

        [WebMethod(Description = "修改头像")]
        public void UpdateFace(String token,String base64str)
        {
            try
            {
                String uid = BaseUtil.getUidByToken(token);
                bool result = uid != String.Empty;
                if (result)
                {
                    result = BaseUtil.setFace(uid, base64str);
                    Util.writeWeb(Context, Status.Success, result ? Status.Success : Status.Fail, result ? "更新头像成功" : "更新头像失败", String.Empty);
                    if (result)
                        BaseUtil.addEvent(uid, EventType.DataChange);
                }
                else
                {
                    Util.writeWeb(Context, Status.Success, Status.Fail, "请求非法", String.Empty);
                }
            }
            catch (Exception ex)
            {
                Util.writeError(Context, ex.Message);
            }
        }

        [WebMethod(Description = "获取头像")]
        public void GetFace(String token,String queryuid)
        {
            try
            {
                String uid = BaseUtil.getUidByToken(token);
                bool result = uid != String.Empty;
                if (result)
                {
                    String base64str = BaseUtil.getFace(queryuid);
                    result = base64str != String.Empty;
                    Util.writeWeb(Context, Status.Success, result ? Status.Success : Status.Fail, result ? "获取头像成功" : "获取头像失败", base64str);
                }
                else
                {
                    Util.writeWeb(Context, Status.Success, Status.Fail, "请求非法", String.Empty);
                }
            }
            catch (Exception ex)
            {
                Util.writeError(Context, ex.Message);
            }
        }

        [WebMethod(Description = "更新资料")]
        public void UpdateUserInfo(String token,String alias,DateTime date,int sex)
        {
            try
            {
                String uid = BaseUtil.getUidByToken(token);
                bool result = uid != String.Empty;
                if (result)
                {
                    result = BaseUtil.updatePersonalData(uid, alias, date, sex == 1);
                    if (result)
                        BaseUtil.addEvent(uid, EventType.DataChange);
                    Util.writeWeb(Context, Status.Success, result ? Status.Success : Status.Fail, result ? "更新资料成功" : "更新资料失败", String.Empty);
                }
                else
                {
                    Util.writeWeb(Context, Status.Success, Status.Fail, "请求非法", String.Empty);
                }
            }
            catch (Exception ex)
            {
                Util.writeError(Context, ex.Message);
            }
        }

        //**************************************************************************其他逻辑*****************************************************************************

         [WebMethod(Description = "获取未处理事件列表")]
        public void GetUnHandlerEventList(String token)
        {
            try
            {
                String uid = BaseUtil.getUidByToken(token);
                bool result = uid != String.Empty;
                if (result)
                {
                    ArrayList arr = BaseUtil.getUnHandlerEvent(uid);
                    result = arr != null;
                    String datastr = String.Empty;
                    if (result)
                        datastr = JsonConvert.SerializeObject(arr);
                    Util.writeWeb(Context, Status.Success, result ? Status.Success : Status.Fail, result ? "获取未处理事件列表成功" : "没有未处理事件列表", datastr);
                }
                else
                {
                    Util.writeWeb(Context, Status.Success, Status.Fail, "请求非法", String.Empty);
                }
            }
            catch (Exception ex)
            {
                Util.writeError(Context, ex.Message);
            }
        }

         [WebMethod(Description = "获取未处理信息发送者Uid")]
         public void GetSenderUid(String token)
         {
             try
             {
                 String uid = BaseUtil.getUidByToken(token);
                 bool result = uid != String.Empty;
                 if (result)
                 {
                     String[] data = BaseUtil.getSenderUid(uid);
                     result = data != null;
                     String datastr = JsonConvert.SerializeObject(data);
                     Util.writeWeb(Context, Status.Success, result ? Status.Success : Status.Fail, result ? "获取未处理信息发送者Uid成功" : "没有未处理记录", datastr);
                 }
                 else
                 {
                     Util.writeWeb(Context, Status.Success, Status.Fail, "请求非法", String.Empty);
                 }
             }
             catch (Exception ex)
             {
                 Util.writeError(Context, ex.Message);
             }
         }
    }

    public class Util
    {
        public static void writeWeb(HttpContext context, Status status, Status statuscode, String msg, String data, String errormsg = null)
        {
            //设置ContentType
            context.Response.ContentType = "application/json";
            //获取序列化字符串
            String result = WebUtil.serializeResponse(status, statuscode, msg, data, errormsg);
            //写入
            context.Response.Write(result);
            //将所有缓冲写入至客户端
            context.Response.Flush();
        }

        public static void writeError(HttpContext context,String errormsg)
        {
            Util.writeWeb(context, Status.Exception, Status.Fail, String.Empty, String.Empty, "参考原因：" + errormsg);
        }
    }
}
