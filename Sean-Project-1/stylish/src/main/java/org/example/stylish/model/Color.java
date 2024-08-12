package org.example.stylish.model;

import lombok.Data;

import java.util.Objects;

@Data
public class Color {
    private String code;
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Color color = (Color) o;
        return Objects.equals(name, color.name) &&
                Objects.equals(code, color.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, code);
    }
}
