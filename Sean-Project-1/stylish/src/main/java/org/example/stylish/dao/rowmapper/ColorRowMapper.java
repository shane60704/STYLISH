package org.example.stylish.dao.rowmapper;

import org.example.stylish.model.Color;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ColorRowMapper implements RowMapper<Color> {
    @Override
    public Color mapRow(ResultSet rs, int rowNum) throws SQLException {
        Color color = new Color();
        color.setCode(rs.getString("code"));
        color.setName(rs.getString("name"));
        return color;
    }
}
