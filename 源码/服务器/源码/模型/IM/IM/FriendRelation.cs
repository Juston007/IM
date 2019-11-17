using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace IM
{
    public class FriendRelation
    {
        public FriendRelation(String id,String senduid,String acceptuid,DateTime accepttime)
        {
            this.id = id;
            this.sendUid = senduid;
            this.acceptUid = acceptuid;
            this.acceptTime = accepttime;
        }

        private String sendUid, acceptUid, id;
        private DateTime acceptTime;

        public String ID
        {
            get { return id; }
        }

        public DateTime AcceptTime
        {
            get { return acceptTime; }
        }

        public String AcceptUid
        {
            get { return acceptUid; }
        }

        public String SendUid
        {
            get { return sendUid; }
        }
    }
}
