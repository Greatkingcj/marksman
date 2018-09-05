package com.huya.marksman.entity;

import java.io.Serializable;

/**
 * Created by charles on 2018/9/5.
 */

public class ItemBanner implements Serializable{
    private static final long serialVersionUID = 3226525180635128661L;
    /**
     * “relatedImage”: “推荐的封面地址”,
     * “relatedName”: “关联的数据标题，可自定义”,
     * “relatedUrl”: “关联的链接，支持内链”,
     * “relatedDesc”: “推荐位短评”,
     * “itemDataType”: 2,
     * “relatedId”: 1111,
     * “itemId”: 22222
     */
    public long Id;
    public String adName;
    public String adImageUrl;
    public String adDesc;
}
