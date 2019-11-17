using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using System.IO;
using System.Net;
using IM;
using Newtonsoft.Json;

namespace IMAccount
{
    public partial class ResetPwd : System.Web.UI.Page
    {
        private WebRequest request;

        protected void Page_Load(object sender, EventArgs e)
        {
            String url = "http://" + System.Configuration.ConfigurationManager.AppSettings["ServerHostAddress"] + "/WebService.asmx/ResetPwd";
            Console.WriteLine(url);
            request = WebRequest.Create(new Uri(url));
            request.Method = "POST";
            request.ContentType = "application/x-www-form-urlencoded";
        }

        protected void btnResetPwd_Click(object sender, EventArgs e)
        {
            String uid = txtUid.Text.Trim();
            String currentPwd = txtCurrentPwd.Text.Trim();
            String newPwd = txtNewPwd.Text.Trim();
            String confirmNewPwd = txtConfirmNewPwd.Text.Trim();
            if (uid != String.Empty && currentPwd != String.Empty && newPwd != String.Empty && confirmNewPwd != String.Empty)
            {
                if (newPwd == confirmNewPwd)
                {
                    if (newPwd == currentPwd)
                    {
                        Response.Write("<script>alert('新密码不能与旧密码相同！');</script>");
                    }
                    else
                    {
                        String postData = String.Format("uid={0}&oldpwd={1}&newpwd={2}", uid, currentPwd, newPwd);
                        byte[] data = System.Text.Encoding.UTF8.GetBytes(postData);
                        request.ContentLength = data.Length;
                        Stream requestStream = request.GetRequestStream();
                        requestStream.Write(data, 0, data.Length);
                        requestStream.Close();
                        //获取相应流
                        WebResponse response = request.GetResponse();
                        Stream responseStream = response.GetResponseStream();
                        StreamReader read = new StreamReader(responseStream);
                        //转换json
                        String jsonStr = read.ReadToEnd();
                        ResponseModel model = JsonConvert.DeserializeObject<ResponseModel>(jsonStr);
                        Response.Write("<script>alert('" + ((model.StatusCode == Status.Success) ? "修改成功！" : "修改失败！") + "');</script>");
                    }
                }
                else
                {
                    Response.Write("<script>alert('两次密码不一致！');</script>");
                }
            }
            else
            {
                Response.Write("<script>alert('请填写完全！');</script>");
            }
        }
    }
}