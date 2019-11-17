using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace IM
{
    public class UserInfoModel
    {
        public UserInfoModel(String uid, String pwd, String regtime, String alias,String faceid = "0", String token = null, String tokengeneratetime = "1970/01/01", String birthday = "1970/01/01", bool sex = false)
        {
            this.uid = uid;
            this.pwd = pwd;
            this.token = token;
            this.alias = alias;
            this.tokenGenerateTime = DateTime.Parse(tokengeneratetime == String.Empty ? "1970/01/01" : tokengeneratetime);
            this.regTime = DateTime.Parse(regtime == String.Empty ? "1970/01/01" : regtime);
            this.birthDay = DateTime.Parse(birthday == String.Empty ? "1970/01/01" : birthday);
            this.faceID = faceid == String.Empty ? "0" : faceid;
            this.sex = sex;
        }

        //字段
        private String uid, pwd, token, alias, faceID;
        private DateTime tokenGenerateTime, regTime, birthDay;
        private bool sex;

        public String FaceID
        {
            get { return faceID; }
        }

        public bool Sex
        {
            get { return sex; }
        }

        //属性
        public DateTime BirthDay
        {
            get { return birthDay; }
        }

        public DateTime RegTime
        {
            get { return regTime; }
        }

        public String Uid
        {
            get { return uid; }
        }

        private String Pwd
        {
            get { return pwd; }
        }

        private String Token
        {
            get { return token; }
        }

        public String Alias
        {
            get { return alias; }
        }

        /// <summary>
        /// 验证密码
        /// </summary>
        /// <param name="pwd">密码</param>
        /// <returns>是否正确</returns>
        public bool authPwd(String pwd)
        {
            return pwd == this.Pwd;
        }

        /// <summary>
        /// 验证Token
        /// </summary>
        /// <param name="token">令牌</param>
        /// <returns>是否一致</returns>
        public bool authToekn(String token)
        {
            return token == Token;
        }

        /// <summary>
        /// 获取令牌生成时间
        /// </summary>
        /// <returns>生成时间</returns>
        public DateTime getTokenGenerateTime()
        {
            return tokenGenerateTime;
        }

        /// <summary>
        /// Token是否为空
        /// </summary>
        /// <returns></returns>
        public bool tokenIsNull()
        {
            return token == String.Empty;
        }

        /// <summary>
        /// 获取Token
        /// </summary>
        /// <param name="pwd">密码</param>
        /// <returns>Token 密码验证失败返回空字符</returns>
        public String getToken(String Pwd)
        {
            if (authPwd(Pwd))
                return Token;
            else
                return String.Empty;
        }
    }
}
