using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Collections;
using System.Data.SqlClient;
using IM;

namespace HelpUtil
{
    /// <summary>
    /// 此类作逻辑操作
    /// </summary>
    public class BaseUtil
    {
        public static Object[] getParameterList(SqlParameter[] parameter)
        {
            return parameter;
        }

        /// <summary>
        /// 根据Token获取Uid
        /// </summary>
        /// <param name="token">令牌</param>
        /// <returns>Uid</returns>
        public static String getUidByToken(String token)
        {
            Object[] parameter = getParameterList(new SqlParameter[] { new SqlParameter("@token", token) });
            ArrayList arr = SQLUtil.rawQuery("Select Uid From UserInfo Where Token = @token", parameter);
            if (arr.Count > 0)
            {
                Hashtable table = (Hashtable)arr[0];
                return table["Uid"].ToString().Trim();
            }
            else
            {
                return String.Empty;
            }
        }

        /// <summary>
        /// 检查账户是否存在
        /// </summary>
        /// <param name="uid">ID</param>
        /// <returns>账户是否存在</returns>
        public static bool exist(String uid)
        {
            Object[] parameter = getParameterList(new SqlParameter[] { new SqlParameter("@uid", uid) });
            ArrayList arr = SQLUtil.rawQuery("Select Uid From UserInfo Where Uid = @uid", parameter);
            return arr.Count > 0;
        }

        /// <summary>
        /// 修改密码
        /// </summary>
        /// <param name="uid">ID</param>
        /// <param name="pwd">密码</param>
        /// <returns>执行结果</returns>
        public static bool changePwd(String uid, String oldpwd, String newpwd)
        {
            if (!exist(uid) || oldpwd == newpwd)
                return false;
            Object[] parameter = getParameterList(new SqlParameter[] { new SqlParameter("@newpwd", newpwd), new SqlParameter("@oldpwd", oldpwd), new SqlParameter("@uid", uid) });
            int result = SQLUtil.excuteSQL("Update UserInfo Set Pwd = @newpwd ,Token = NULL , TokenGenerateTime = NULL Where Uid = @uid And Pwd = @oldpwd", parameter);
            return result > 0;
        }

        /// <summary>
        /// 获取用户信息
        /// </summary>
        /// <param name="uid">ID</param>
        /// <returns>用户模型实例 获取不到返回null</returns>
        public static UserInfoModel getUserInfo(String uid)
        {
            if (!exist(uid))
                return null;
            Object[] parameter = getParameterList(new SqlParameter[] { new SqlParameter("@uid", uid) });
            ArrayList arr = SQLUtil.rawQuery("Select * From UserInfo Where Uid = @uid", parameter);
            if (arr.Count > 0)
            {
                Hashtable table = (Hashtable)arr[0];
                //密码
                String pwd = table["Pwd"].ToString().Trim();
                //注册时间
                String regtime = table["RegTime"].ToString().Trim();
                //别名
                String alias = table["Alias"].ToString().Trim();
                //Token
                String token = table["Token"].ToString().Trim();
                //Token注册时间
                String generatestr = table["TokenGenerateTime"].ToString().Trim();
                //生日
                String birthday = table["BirthDay"].ToString().Trim();
                //头像ID
                String faceid = table["FaceID"].ToString().Trim();
                //性别
                String sexstr = table["Sex"].ToString().Trim();
                bool sex = Convert.ToBoolean(sexstr == "" ? "True" : sexstr);
                //UserInfo对象
                UserInfoModel user = new UserInfoModel(uid.Trim(), pwd.Trim(), regtime.Trim(), alias.Trim(), faceid.Trim(), token.Trim(), generatestr.Trim(), birthday.Trim(), sex);
                return user;
            }
            else
                return null;
        }

        /// <summary>
        /// 登入
        /// </summary>
        /// <param name="uid">ID</param>
        /// <param name="pwd">密码</param>
        /// <returns>访问令牌</returns>
        public static String login(String uid, String pwd)
        {
            UserInfoModel user = getUserInfo(uid);
            if (user != null)
            {
                if (!user.authPwd(pwd))
                {
                    return String.Empty;
                }
                String token = String.Empty;
                if (user.tokenIsNull())
                {
                    String str = generateToken(uid);
                    return str;
                }
                else
                {
                    if ((DateTime.Now - user.getTokenGenerateTime()).TotalDays > 7)
                    {
                        return generateToken(uid);
                    }
                    return user.getToken(pwd);
                }
            }
            else
                return String.Empty;
        }

        /// <summary>
        /// 生成令牌
        /// </summary>
        /// <param name="uid">ID</param>
        /// <returns>令牌</returns>
        private static String generateToken(String uid)
        {
            //生成Token
            String str = DateTime.Now.Millisecond + new Random().NextDouble() + uid;
            str = Convert.ToBase64String(System.Text.Encoding.Unicode.GetBytes(str));
            //上传至数据库
            object[] parameter = getParameterList(new SqlParameter[] { new SqlParameter("@token", str), new SqlParameter("@time", DateTime.Now.ToShortDateString()), new SqlParameter("@uid", uid) });
            SQLUtil.excuteSQL("Update UserInfo Set Token = @token ,TokenGenerateTime = @time Where Uid = @uid", parameter);
            return str;
        }

        /// <summary>
        /// 发送好友请求
        /// </summary>
        /// <param name="senduid">发送ID</param>
        /// <param name="acceptuid">接受ID</param>
        /// <returns>执行结果 为Empty时发送请求成功</returns>
        public static String sendFriendRequest(String senduid, String acceptuid)
        {
            String error = String.Empty;
            //查询是否已发送过请求
            if (!BaseUtil.exist(acceptuid))
            {
                return "不存在此用户";
            }
            if (isFirend(senduid, acceptuid))
            {
                return "你们已经是好友";
            }
            Object[] parameter = getParameterList(new SqlParameter[] { new SqlParameter("@senduid", senduid), new SqlParameter("@acceptuid", acceptuid) });
            ArrayList arr = SQLUtil.rawQuery("Select id From FriendRequest Where SendUid = @senduid And AcceptUid = @acceptuid", parameter);
            if (arr != null)
            {
                if (arr.Count > 0)
                {
                    return "好友请求已存在,请勿重复发送好友请求";
                }
                else
                {
                    parameter = getParameterList(new SqlParameter[] { new SqlParameter("@senduid", senduid), new SqlParameter("@acceptuid", acceptuid) });
                    int result = SQLUtil.excuteSQL("Insert into  FriendRequest Values(@senduid,@acceptuid)", parameter);
                    return result > 0 ? String.Empty : "发送好友请求失败";
                }
            }
            return "未知错误";
        }

        /// <summary>
        /// 处理好友请求
        /// </summary>
        /// <param name="senduid">发送Uid</param>
        /// <param name="acceptuid">接受Uid</param>
        /// <param name="status">处理状态</param>
        /// <returns>执行结果 Empty是处理成功 其他为错误原因</returns>
        public static String handlerFirendRequest(String senduid, String acceptuid, HandlerRequest status)
        {
            Object[] parameter = getParameterList(new SqlParameter[] { new SqlParameter("@senduid", senduid), new SqlParameter("@acceptuid", acceptuid) });
            //查询是否有此请求
            ArrayList arr = SQLUtil.rawQuery("Select id From FriendRequest Where SendUid = @senduid And AcceptUid = @acceptuid", parameter);
            if (arr.Count > 0)
            {
                Hashtable table = (Hashtable)arr[0];
                bool result = true;
                parameter = getParameterList(new SqlParameter[] { new SqlParameter("@senduid", senduid), new SqlParameter("@acceptuid", acceptuid) });
                SQLUtil.excuteSQL("Delete FriendRequest Where SendUid = @senduid And AcceptUid = @acceptuid", parameter);
                if (status == HandlerRequest.Accept)
                {
                    //注册到好友关系表当中
                    parameter = getParameterList(new SqlParameter[] { new SqlParameter("@senduid", senduid), new SqlParameter("@acceptuid", acceptuid), new SqlParameter("@time", DateTime.Now) });
                    int excuteresult = SQLUtil.excuteSQL("Insert into FriendRelation Values(@senduid,@acceptuid,@time)", parameter);
                    result = excuteresult > 0;
                }
                return result ? String.Empty : "处理请求失败";
            }
            else
            {
                return "没有此请求";
            }
        }

        /// <summary>
        /// 好友状态
        /// </summary>
        /// <param name="uid1">ID1</param>
        /// <param name="uid2">ID2</param>
        /// <returns>是否为好友</returns>
        public static bool isFirend(String uid1, String uid2)
        {
            Object[] parameter = getParameterList(new SqlParameter[] { new SqlParameter("@senduid", uid1), new SqlParameter("@acceptuid", uid2) });
            ArrayList arr = SQLUtil.rawQuery("Select id From FriendRelation Where (SendUid = @senduid And AcceptUid = @acceptuid) Or (SendUid = @acceptuid And AcceptUid = @senduid)", parameter);
            return arr.Count > 0;
        }

        /// <summary>
        /// 删除好友
        /// </summary>
        /// <param name="uid1">ID1</param>
        /// <param name="uid2">ID2</param>
        /// <returns>执行结果</returns>
        public static bool deleteFriend(String uid1, String uid2)
        {
            if (isFirend(uid1, uid2))
            {
                Object[] parameter = getParameterList(new SqlParameter[] { new SqlParameter("@uid1", uid1), new SqlParameter("@uid2", uid2) });
                int result = SQLUtil.excuteSQL("Delete FriendRelation Where (SendUid = @uid1 And AcceptUid = @uid2) Or (SendUid = @uid2 And AcceptUid = @uid1)", parameter);
                return result > 0;
            }
            else
            {
                return false;
            }
        }

        /// <summary>
        /// 注册用户
        /// </summary>
        /// <param name="uid">ID</param>
        /// <param name="pwd">密码</param>
        /// <param name="alias">别名</param>
        /// <returns>注册结果</returns>
        private static String addUser(String uid, String pwd, String alias)
        {
            if (exist(uid))
            {
                return String.Empty;
            }
            Object[] parameter = getParameterList(new SqlParameter[] { new SqlParameter("@uid", uid), new SqlParameter("@pwd", pwd), new SqlParameter("@time", DateTime.Now.ToString()), new SqlParameter("@alias", alias) });
            int result = SQLUtil.excuteSQL("Insert into UserInfo (Uid,Pwd,RegTime,Alias,FaceID) Values(@uid,@pwd,@time,@alias,0)", parameter);
            return result > 0 ? uid : String.Empty;
        }

        /// <summary>
        /// 注册用户
        /// </summary>
        /// <param name="pwd">密码</param>
        /// <param name="alias">别名</param>
        /// <returns>注册的uid</returns>
        public static String regUser(String pwd, String alias)
        {
            //生成uid
            ArrayList arr = SQLUtil.rawQuery("select TOP 1 Uid from UserInfo order by id desc", null);
            int uid = 10000;
            if (arr.Count > 0)
            {
                Hashtable table = (Hashtable)arr[0];
                uid = Convert.ToInt32(table["Uid"]);
            }
            return addUser((++uid).ToString(), pwd, alias);
        }

        /// <summary>
        /// 获取好友列表
        /// </summary>
        /// <param name="uid">ID</param>
        /// <returns>好友集合</returns>
        public static ArrayList getFriendList(String uid)
        {
            //查询所有好友
            Object[] parameter = getParameterList(new SqlParameter[] { new SqlParameter("uid", uid) });
            ArrayList arr = SQLUtil.rawQuery("Select * From FriendRelation Where SendUid = @uid Or AcceptUid = @uid", parameter);
            if (arr.Count > 0)
            {
                String[] arruid = new String[arr.Count];
                for (int i = 0; i < arr.Count; i++)
                {
                    Hashtable table = (Hashtable)arr[i];
                    if (table["SendUid"].ToString().Trim() == uid)
                    {
                        arruid[i] = table["AcceptUid"].ToString();
                    }
                    else
                    {
                        arruid[i] = table["SendUid"].ToString();
                    }
                }
                //查询所有好友基本信息
                ArrayList firendlist = new ArrayList();
                for (int i = 0; i < arruid.Length; i++)
                {
                    UserInfoModel user = BaseUtil.getUserInfo(arruid[i]);
                    firendlist.Add(user);
                }
                return firendlist;
            }
            else
            {
                return null;
            }
        }

        /// <summary>
        /// 获取好友请求列表
        /// <param name="uid">ID</param>
        /// <returns>请求列表</returns>
        public static ArrayList getFriendRequestList(String uid)
        {
            Object[] parameter = getParameterList(new SqlParameter[] { new SqlParameter("uid", uid) });
            ArrayList arr = SQLUtil.rawQuery("Select * From FriendRequest Where AcceptUid = @uid", parameter);
            if (arr.Count > 0)
            {
                ArrayList array = new ArrayList();
                foreach (Hashtable table in arr)
                {
                    array.Add(new FriendRequest(table["id"].ToString().Trim(), table["SendUid"].ToString().Trim(), table["AcceptUid"].ToString().Trim()));
                }
                return array;
            }
            else
            {
                return null;
            }
        }

        /// <summary>
        /// 上传Log
        /// </summary>
        /// <param name="ip">ip地址</param>
        /// <param name="uid">uid</param>
        /// <param name="msg">信息</param>
        public static void updateLog(String ip, String uid, String msg)
        {
            Object[] parameter = getParameterList(new SqlParameter[] { new SqlParameter("@uid", uid), new SqlParameter("@ip", ip), new SqlParameter("@msg", msg), new SqlParameter("@time", DateTime.Now) });
            int result = SQLUtil.excuteSQL("Insert into dbo.Log Values(@ip,@uid,@msg,@time);", parameter);
        }

        /// <summary>
        /// 发送信息
        /// </summary>
        /// <param name="senduid">发送ID</param>
        /// <param name="touid">接受ID</param>
        /// <param name="message">信息</param>
        /// <param name="msgtype"></param>
        /// <returns></returns>
        public static bool sendMsg(String senduid, String touid, String message, MessageType msgtype = MessageType.Text)
        {
            //判断发送者、接受者是否存在
            if (exist(touid) && exist(senduid) && isFirend(senduid,touid))
            {
                int type = Convert.ToInt32(msgtype);
                //生成随机id 此id指示着信息的id 靠此id获取该条信息 不生成符号 因为符号与SQLite语句会冲突
                String id = senduid + "," + touid + "," + type + "," + DateTime.Now.ToString("O") + getStr(false, 6);
                //上传消息基本信息
                Object[] parameter = getParameterList(new SqlParameter[] { new SqlParameter("@senduid", senduid), new SqlParameter("@touid", touid), new SqlParameter("@type", type), new SqlParameter("@id", id), new SqlParameter("@time", DateTime.Now.ToString()) });
                bool result = SQLUtil.excuteSQL("Insert into MessageInfo Values(@id,@senduid,@touid,@time,@type,0)", parameter) > 0;
                if (result)
                {
                    String sqlstr = String.Empty;
                    byte[] data = null;
                    //上传消息详情
                    if (msgtype == MessageType.Text)
                    {
                        parameter = getParameterList(new SqlParameter[] { new SqlParameter("@id", id), new SqlParameter("@msg", message) });
                    }
                    else
                    {
                        //转换为byte
                        data = Convert.FromBase64String(message);
                        parameter = getParameterList(new SqlParameter[] { new SqlParameter("@id", id), new SqlParameter("@msg", data) });
                    }
                    switch (msgtype)
                    {
                        case MessageType.Text:
                            sqlstr = "Insert TxtMsg  Values(@id,@msg);";
                            break;
                        case MessageType.Image:
                            sqlstr = "Insert ImgMsg  Values(@id,@msg);";
                            break;
                        case MessageType.Sound:
                            sqlstr = "Insert SoundMsg  Values(@id,@msg);";
                            break;
                        case MessageType.Other:
                            sqlstr = "Insert OtherMsg  Values(@id,@msg);";
                            break;
                    }
                    result = SQLUtil.excuteSQL(sqlstr, parameter) > 0;
                    return result;
                }
                else
                    return false;
            }
            else
                return false;
        }

        /// <summary>
        /// 接受信息
        /// </summary>
        /// <param name="senduid">ID1</param>
        /// <param name="touid">ID2</param>
        /// <param name="isread">是否为已读信息</param>
        /// <param name="count">读取数量 -1时读取全部</param>
        /// <returns>此集合当中包含了MessageInfo对象 每个对象即代表一条信息</returns>
        public static ArrayList receiverMsg(String id1, String id2, bool isread)
        {
            //根据条件查询信息
            if (!(exist(id1) && exist(id2)))
                return null;
            Object[] parameter = getParameterList(new SqlParameter[] { new SqlParameter("@id1", id1), new SqlParameter("@id2", id2), new SqlParameter("@isread", isread ? "1" : "0") });
            String querystr = String.Empty;
            if (!isread)
                querystr = "Select * From MessageInfo Where SendUid = @id2 And RecviceUid = @id1 And  ReceiverStatus = @isread";
            else
                querystr = "Select * From MessageInfo Where ((SendUid = @id2 And RecviceUid = @id1) Or (SendUid = @id1 And RecviceUid = @id2)) And  ReceiverStatus = @isread";
            ArrayList arr = SQLUtil.rawQuery(querystr, parameter);
            if (arr.Count <= 0)
                return null;
            ArrayList data = new ArrayList();
            String touid = String.Empty;
            for (int i = 0; i < arr.Count; i++)
            {
                Hashtable table = (Hashtable)arr[i];
                String msgid = table["MsgId"].ToString().Trim();
                DateTime time = (DateTime)table["time"];
                MessageType msgtype = (MessageType)table["MsgType"];
                String senduid = table["SendUid"].ToString().Trim();
                touid = table["RecviceUid"].ToString().Trim();
                MessageInfo msginfo = new MessageInfo(senduid, touid, isread, msgid, time, msgtype);
                data.Add(msginfo);
            }
            //将接收的信息置位为已读
            if (id1 == touid)
                if (!isread)
                {
                    parameter = getParameterList(new SqlParameter[] { new SqlParameter("@id1", id1), new SqlParameter("@id2", id2), new SqlParameter("@isread", isread ? "1" : "0") });
                    String sqlstr = "Update MessageInfo Set ReceiverStatus = 1 Where MsgId in (Select MsgId From MessageInfo Where SendUid = @id2 And RecviceUid = @id1 And  ReceiverStatus = @isread)";
                    int result = SQLUtil.excuteSQL(sqlstr, parameter);
                    Console.WriteLine(result);
                }
            return data;
        }

        /// <summary>
        /// 获取具体信息
        /// </summary>
        /// <param name="id">ID</param>
        /// <param name="msgtype">信息类型</param>
        /// <returns>信息</returns>
        public static String getMessageContent(String id, MessageType msgtype)
        {
            String querystr = "Select * From ";
            switch (msgtype)
            {
                case MessageType.Image:
                    querystr += "ImgMsg";
                    break;
                case MessageType.Other:
                    querystr += "OtherMsg";
                    break;
                case MessageType.Sound:
                    querystr += "SoundMsg";
                    break;
                case MessageType.Text:
                    querystr += "TxtMsg";
                    break;
            }
            querystr += " Where MsgId = @msgid";
            ArrayList arr = SQLUtil.rawQuery(querystr, getParameterList(new SqlParameter[] { new SqlParameter("@msgid", id) }));
            if (arr.Count > 0)
            {
                Hashtable table = (Hashtable)arr[0];
                if (msgtype != MessageType.Text)
                    return Convert.ToBase64String((byte[])table["Msg"]);
                else
                    return table["Msg"].ToString().Trim();
            }
            else
                return String.Empty;
        }

        /// <summary>
        /// 生成随机字符
        /// </summary>
        /// <param name="b">是否为复杂字符</param>
        /// <param name="n">长度</param>
        /// <returns>生成的字符</returns>
        public static string getStr(bool b, int n)
        {

            string str = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
            if (b)
            {
                str += "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";//复杂字符
            }
            StringBuilder sb = new StringBuilder();
            Random rd = new Random();
            for (int i = 0; i < n; i++)
            {
                sb.Append(str.Substring(rd.Next(0, str.Length), 1));
            }
            return sb.ToString();

        }

        /// <summary>
        /// 设置头像
        /// </summary>
        /// <param name="uid">ID</param>
        /// <param name="base64str">Base64字符串</param>
        /// <returns>设置结果</returns>
        public static bool setFace(String uid, String base64str)
        {
            if (!exist(uid) || base64str == String.Empty)
            {
                return false;
            }
            //获取最后的FaceID
            byte[] data = Convert.FromBase64String(base64str);
            //生成id
            ArrayList arr = SQLUtil.rawQuery("Select TOP 1 FaceID From Face order by FaceID desc", null);
            int faceid = 0;
            if (arr.Count == 0)
            {
                faceid = 1;
            }
            else
            {
                Hashtable table = (Hashtable)arr[0];
                faceid = (int)table["FaceID"];
                faceid++;
            }
            //插入到头像表
            SQLUtil.excuteSQL("Insert into Face values(@face)", getParameterList(new SqlParameter[] { new SqlParameter("@face", data) }));
            //更新FaceID
            int result = SQLUtil.excuteSQL("Update UserInfo set FaceID = @faceid Where Uid = @uid", getParameterList(new SqlParameter[] { new SqlParameter("@faceid", faceid), new SqlParameter("@uid", uid) }));
            return result > 0;
        }

        /// <summary>
        /// 获取头像
        /// </summary>
        /// <param name="uid">ID</param>
        /// <returns>Base64形式</returns>
        public static String getFace(String uid)
        {
            if (!exist(uid))
            {
                return String.Empty;
            }
            //查询到用户的FaceID
            ArrayList arr = SQLUtil.rawQuery("Select FaceID From UserInfo Where Uid = @uid", getParameterList(new SqlParameter[] { new SqlParameter("@uid", uid) }));
            Hashtable table = (Hashtable)arr[0];
            Object face = table["FaceID"];
            int faceid = 0;
            if (face != null)
            {
                faceid = (int)face;
            }
            //获取Face
            arr = SQLUtil.rawQuery("Select * From Face Where FaceID = @faceid", getParameterList(new SqlParameter[] { new SqlParameter("@faceid", faceid) }));
            table = (Hashtable)arr[0];
            byte[] data = (byte[])table["Face"];
            return Convert.ToBase64String(data);
        }

        /// <summary>
        /// 更新个人资料
        /// </summary>
        /// <param name="uid">ID</param>
        /// <param name="alias">别名</param>
        /// <param name="birthday">生日</param>
        /// <param name="sex">性别</param>
        /// <returns>更新结果</returns>
        public static bool updatePersonalData(String uid, String alias, DateTime birthday, bool sex)
        {
            int result = SQLUtil.excuteSQL("Update UserInfo set Alias = @alias,Sex = @sex,BirthDay = @birthday Where Uid = @uid", getParameterList(new SqlParameter[] { new SqlParameter("@alias", alias), new SqlParameter("@birthday", birthday), new SqlParameter("@sex", sex), new SqlParameter("@uid", uid) }));
            return result > 0;
        }

        /// <summary>
        /// 添加未处理事件
        /// </summary>
        /// <param name="uid">ID</param>
        /// <param name="eventtype">事件类型</param>
        public static bool addEvent(String uid, EventType eventtype)
        {
            //查询是否有相同类型的未处理事件
            ArrayList arr = SQLUtil.rawQuery("Select * From UnHandlerEvent Where EventType = @type And Uid = @uid", getParameterList(new SqlParameter[] { new SqlParameter("@uid", uid), new SqlParameter("@type", eventtype) }));
            if (arr.Count == 0)
            {
                int result = SQLUtil.excuteSQL("Insert into UnHandlerEvent Values(@uid,@type)", getParameterList(new SqlParameter[] { new SqlParameter("@uid", uid), new SqlParameter("@type", (int)eventtype) }));
                return result > 0;
            }
            else
                return false;
        }

        /// <summary>
        /// 获取未处理事件列表
        /// </summary>
        /// <param name="uid">ID</param>
        /// <returns>未处理时间List</returns>
        public static ArrayList getUnHandlerEvent(String uid)
        {
            ArrayList arr = SQLUtil.rawQuery("Select * From UnHandlerEvent Where Uid = @uid", getParameterList(new SqlParameter[] { new SqlParameter("@uid", uid) }));
            if (arr.Count <= 0)
            {
                return null;
            }
            ArrayList eventlist = new ArrayList();
            for (int i = 0; i < arr.Count; i++)
            {
                Hashtable table = (Hashtable)arr[i];
                UnHandlerEvent unhandlerevent = new UnHandlerEvent(uid, (EventType)table["EventType"]);
                eventlist.Add(unhandlerevent);
            }
            //删除未处理事件！！！！！！！！！！！！
            SQLUtil.excuteSQL("Delete UnHandlerEvent Where Uid = @uid", getParameterList(new SqlParameter[] { new SqlParameter("@uid", uid) }));
            return eventlist;
        }

        public static String[] getSenderUid(String uid)
        {
            ArrayList arr = SQLUtil.rawQuery("Select SendUid From MessageInfo Where RecviceUid = @uid And ReceiverStatus = 0 Group by SendUid", getParameterList(new SqlParameter[] { new SqlParameter("@uid", uid) }));
            if (arr.Count > 0)
            {
                String[] data = new String[arr.Count];
                int index = 0;
                foreach (Hashtable table in arr)
                {
                    data[index++] = table["SendUid"].ToString().Trim();
                }
                return data;
            }
            return null;
        }
    }
}
