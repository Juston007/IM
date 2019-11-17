using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace HelpUtil
{
    public class Config
    {
        private static String connectionStr = "";

        public static String ConnectionStr
        {
            get { return Config.connectionStr; }
            set { Config.connectionStr = value; }
        }
    }
}
