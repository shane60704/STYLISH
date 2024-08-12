package org.example.stylish.dao;

public interface SizeDao {
    Integer createSize(String sizeName);

    Integer getSizeByName(String sizeName);

    String getSizeById(Integer sizeId);

}
