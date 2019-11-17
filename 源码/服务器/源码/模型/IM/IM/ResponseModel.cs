using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace IM
{
    public enum Status
    {
        Success = 0,
        Fail = 1,
        Exception = 2,
        Unknow = 3
    }

    public class ResponseModel
    {
        public ResponseModel(Status status, Status statuscode, String msg, String data, String errormsg = null)
        {
            this.status = status;
            this.statusCode = statuscode;
            this.data = data;
            this.errorMsg = errormsg;
            this.messgae = msg;
        }

        private Status status = Status.Fail;
        private Status statusCode = Status.Fail;
        private String messgae, errorMsg, data;

        public Status Status
        {
            get { return status; }
        }


        public Status StatusCode
        {
            get { return statusCode; }
        }


        public String Data
        {
            get { return data; }
        }

        public String ErrorMsg
        {
            get { return errorMsg; }
        }

        public String Messgae
        {
            get { return messgae; }
        }
    }
}
