package com.drivesync.db;

import com.drivesync.dto.Notat;
import com.google.common.collect.ImmutableList;
import org.apache.commons.dbutils.ResultSetHandler;
import org.joda.time.LocalDateTime;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NotatHandler implements ResultSetHandler<ImmutableList<Notat>> {
    @Override
    public ImmutableList<Notat> handle(ResultSet rs) throws SQLException {
        List<Notat> notater = new ArrayList<Notat>();
        while (rs.next()) {
            notater.add(new Notat(
                    rs.getInt("id"),
                    rs.getString("tittel"),
                    rs.getString("innhold"),
                    new LocalDateTime(rs.getTimestamp("endret_tid")),
                    rs.getInt("gruppe_id")));
        }
        return ImmutableList.copyOf(notater);
    }
}
