using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace IM
{
    public enum HandlerRequest
    {
        Accept = 0,
        Refuse = 1,
        Ignore = 2
    }
    public class FriendRequest
    {
        public FriendRequest(String id,String senduid,String acceptuid)
        {
            this.id = id;
            this.sendUid = senduid;
            this.acceptUid = acceptuid;
        }
        private String id, sendUid, acceptUid;

        public String AcceptUid
        {
            get { return acceptUid; }
        }

        public String SendUid
        {
            get { return sendUid; }
        }

        public String ID
        {
            get { return id; }
        }
    }
}
