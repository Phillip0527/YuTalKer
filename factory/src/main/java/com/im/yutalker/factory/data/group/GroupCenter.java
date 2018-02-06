package com.im.yutalker.factory.data.group;

import com.im.yutalker.factory.model.card.GroupCard;
import com.im.yutalker.factory.model.card.GroupMemberCard;

/**
 * 群中心的基本定义接口
 * Created by Phillip on 2018/2/1.
 */

public interface GroupCenter {
    // 群卡片的分发处理
    void dispatch(GroupCard... cards);

    // 群成员的分发处理
    void dispatch(GroupMemberCard... cards);
}
