using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace IM
{
    public enum EventType
    {
        FriendRequest = 0,
        FriendChange = 1,
        Message = 2,
        TokenChange = 3,
        DataChange,
    }

    public class UnHandlerEvent
    {
        public UnHandlerEvent(String uid,EventType eventtype)
        {
            this.uid = uid;
            this.msgType = eventtype;
        }

        private String uid;
        private EventType msgType = EventType.Message;

        public EventType MsgType
        {
            get { return msgType; }
        }

        public String Uid
        {
            get { return uid; }
        }
    }
}
