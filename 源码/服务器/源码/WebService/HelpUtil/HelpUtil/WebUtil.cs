using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Newtonsoft.Json;
using IM;

namespace HelpUtil
{
    public class WebUtil
    {
        public static String serializeResponse(Status status, Status statuscode, String msg, String data, String errormsg = null)
        {
            ResponseModel model = new ResponseModel(status, statuscode, msg, data, errormsg);
            return JsonConvert.SerializeObject(model);
        }

    }
}
