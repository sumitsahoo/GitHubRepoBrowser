
package com.sumit.githubrepobrowser.model.repolistresult;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class License
{

    @Id
    public long boxId;

    @SerializedName("key")
    @Expose
    public String key;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("spdx_id")
    @Expose
    public String spdxId;
    @SerializedName("url")
    @Expose
    public String url;


}
