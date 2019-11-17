using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using System.Net;
using System.IO;
using Newtonsoft.Json;
using IM;

namespace IMAccount
{
    public partial class Manage : System.Web.UI.Page
    {
        private WebRequest request;

        protected void Page_Load(object sender, EventArgs e)
        {
            String url = "http://" + System.Configuration.ConfigurationManager.AppSettings["ServerHostAddress"] + "/WebService.asmx/RegUser";
            request = WebRequest.Create(new Uri(url));
            request.Method = "POST";
            request.ContentType = "application/x-www-form-urlencoded";
        }

        protected void btnReg_Click(object sender, EventArgs e)
        {
            String alias = txtAlias.Text.Trim();
            String pwd = txtPwd.Text.Trim();
            String confirmpwd = txtConfirmPwd.Text.Trim();
            if (alias == String.Empty && pwd == String.Empty && confirmpwd == String.Empty)
            {
                Response.Write("<script>alert('请填写完全！');</script>");
            }
            else
            {
                if (pwd == confirmpwd)
                {
                    String postData = String.Format("pwd={0}&alias={1}", pwd, alias);
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
                    if (model.Status == Status.Success && model.StatusCode == Status.Success)
                    {
                        Response.Write("<script>alert('注册成功！您的Uid：" + model.Data + "');</script>");
                    }
                    else
                    {
                        Response.Write("<script>alert('" + model.ErrorMsg + "');</script>");
                    }
                }
                else
                {
                    Response.Write("<script>alert('两次密码不一致！');</script>");
                }
            }
        }
    }
}