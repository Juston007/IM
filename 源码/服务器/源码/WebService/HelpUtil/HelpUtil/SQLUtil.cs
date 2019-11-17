using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Data;
using System.Data.SqlClient;
using System.Collections;

namespace HelpUtil
{
    /// <summary>
    /// 此类作SQL基本的执行查询操作
    /// </summary>
    public class SQLUtil
    {
        /// <summary>
        /// 执行SQL语句
        /// </summary>
        /// <param name="str">要执行的sql语句</param>
        /// <param name="parameters">SQLParameters参数</param>
        /// <returns>受影响行数</returns>
        public static int excuteSQL(String str, Array parameters)
        {
            using (SqlConnection con = new SqlConnection(Config.ConnectionStr))
            {
                con.Open();
                using (SqlCommand cmd = new SqlCommand(str, con))
                {
                    if (parameters != null)
                        cmd.Parameters.AddRange(parameters);
                    return cmd.ExecuteNonQuery();
                }
            }
        }


        /// <summary>
        /// 查询数据
        /// </summary>
        /// <param name="str">查询语句</param>
        /// <param name="parameters">SQLParameters</param>
        /// <returns>数据集合 键为列名 值为数据</returns>
        public static ArrayList rawQuery(String str, Array parameters)
        {
            using (SqlConnection con = new SqlConnection(Config.ConnectionStr))
            {
                con.Open();
                using (SqlCommand cmd = new SqlCommand(str, con))
                {
                    if (parameters != null)
                        cmd.Parameters.AddRange(parameters);
                    using (SqlDataReader reader = cmd.ExecuteReader())
                    {
                        ArrayList array = new ArrayList();
                        while (reader.Read())
                        {
                            Hashtable hashtable = new Hashtable();
                            for (int i = 0; i < reader.FieldCount; i++)
                            {
                                hashtable.Add(reader.GetName(i), reader.GetValue(i));
                            }
                            array.Add(hashtable);
                        }
                        return array;
                    }
                }
            }
        }
    }
}
