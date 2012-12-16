package com.notat.server.db;

import com.notat.server.dto.Gruppe;
import com.google.common.collect.ImmutableList;
import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GruppeHandler implements ResultSetHandler<ImmutableList<Gruppe>> {
    @Override
    public ImmutableList<Gruppe> handle(ResultSet rs) throws SQLException {
        List<Gruppe> grupper = new ArrayList<Gruppe>();

        while(rs.next()){
            grupper.add(new Gruppe(rs.getInt("id"), rs.getString("gruppenavn")));
        }
        return ImmutableList.copyOf(grupper);
    }
}
