package org.example.stylish.dao;


import org.example.stylish.model.Color;

public interface ColorDao {
    Integer createColor(String colorName, String colorcode);

    Integer getColorIdByNameAndCode(String colorName, String colorcode);

    Color getColorByColorId(Integer colorId);
}
