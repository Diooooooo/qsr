package com.qsr.sdk.service.helper;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class NumberPool {

    final static Logger logger = LoggerFactory.getLogger(NumberPool.class);

    final static int default_increment=100;
    final static int default_init_value=1;
    //static long next_value = -1;
    static Map<String, long[]> types = new HashMap<>();

    private static long[] getValues(String key) {
        long[] values = types.get(key);
        if (values == null) {
            values = new long[]{0, 0};
            types.put(key, values);
        }
        return values;
    }

    public static synchronized long nextLong(final String key) {

        final long[] values = getValues(key);

        if (values[0] >= values[1]) {
            Db.tx(() -> {
                    String sql = "select value,(value+increment_value) as next_value from tools_numberpool where name=? for update";
                    String sql1 = "update tools_numberpool set value=if((value+increment_value) < max_value, value+increment_value, 0) where name=?";
                    String sql2 = "insert tools_numberpool(name,value,increment_value) values(?,?,?) ";
                    Record record = Db.findFirst(sql, key);
                    if (record != null) {
                        values[0] = record.getLong("value");
                        values[1] = record.getLong("next_value");
                        Db.update(sql1, key);
                    } else {
                        values[0]=default_init_value;
                        values[1]=default_init_value+default_increment;
                        Db.update(sql2,key,values[1],default_increment);
                    }
                    return true;
            });
        }
        values[0]++;
        return values[0];
    }
}
