package com.geostat.census_2024.data.local.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "cl_tags", indices = { @Index(value = { "type", "title" }, unique = true) })
public class TagEntity {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private Integer id;

    @NonNull
    private Integer type;

    @NonNull
    private String title;

    public TagEntity(@NonNull Integer type, @NonNull String title) {
        this.type = type;
        this.title = title;
    }

    public void setId(@NonNull Integer id) {
        this.id = id;
    }

    @NonNull
    public Integer getId() {
        return id;
    }

    @NonNull
    public Integer getType() {
        return type;
    }

    public void setType(@NonNull Integer type) {
        this.type = type;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }
}
