using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace IM
{
    public enum MessageType
    {
        Text = 0,
        Image = 1,
        Sound = 2,
        Other = 3
    }

    public class MessageInfo
    {
        public MessageInfo(String senduid, String receiveruid, bool status, String msgid,DateTime time,MessageType msgtype)
        {
            this.sendUid = senduid;
            this.receiverUid = receiveruid;
            this.receiverStatus = status;
            this.msgId = msgid;
            this.time = time;
            this.msgType = msgtype;
        }

        private String sendUid, receiverUid, msgId;
        private bool receiverStatus;
        private DateTime time;
        private MessageType msgType;

        public DateTime Time
        {
            get { return time; }
        }

        public String MsgId
        {
            get { return msgId; }
        }

        public bool ReceiverStatus
        {
            get { return receiverStatus; }
        }

        public MessageType MsgType
        {
            get { return msgType; }
        }

        public String ReceiverUid
        {
            get { return receiverUid; }
        }

        public String SendUid
        {
            get { return sendUid; }
        }
    }
}
