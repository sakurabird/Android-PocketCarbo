package com.sakurafish.pockettoushituryou.model;

import com.github.gfx.android.orma.annotation.Column;
import com.github.gfx.android.orma.annotation.PrimaryKey;
import com.github.gfx.android.orma.annotation.Table;
import com.google.gson.annotations.SerializedName;

@Table
public class Kinds {

    @PrimaryKey(auto = false)
    @Column(indexed = true)
    @SerializedName("id")
    public int id;

    @Column(indexed = true)
    @SerializedName("name")
    public String name;

    @Column(indexed = true)
    @SerializedName("type_id")
    public int type_id;

    public Foods_Relation getFoods(OrmaDatabase orma) {
        return orma.relationOfFoods().kind_idEq(id);
    }

    @Override
    public String toString() {
        return "Kinds id:" + id + " name:" + name;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Kinds && ((Kinds) o).id == id || super.equals(o);
    }

    @Override
    public int hashCode() {
        return id;
    }
}
