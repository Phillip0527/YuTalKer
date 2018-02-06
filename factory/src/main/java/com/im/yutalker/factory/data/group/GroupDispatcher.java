package com.im.yutalker.factory.data.group;

import com.im.yutalker.factory.data.helper.DbHelper;
import com.im.yutalker.factory.data.helper.GroupHelper;
import com.im.yutalker.factory.data.helper.UserHelper;
import com.im.yutalker.factory.model.card.GroupCard;
import com.im.yutalker.factory.model.card.GroupMemberCard;
import com.im.yutalker.factory.model.dp.Group;
import com.im.yutalker.factory.model.dp.GroupMember;
import com.im.yutalker.factory.model.dp.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 群中心的实现类
 * Created by Phillip on 2018/2/1.
 */

public class GroupDispatcher implements GroupCenter {
    private static GroupCenter instance;

    private final Executor executor = Executors.newSingleThreadExecutor();

    public static GroupCenter getInstance() {
        if (instance == null) {
            synchronized (GroupDispatcher.class) {
                if (instance == null)
                    instance = new GroupDispatcher();
            }
        }
        return instance;
    }

    @Override
    public void dispatch(GroupCard... cards) {
        if (cards == null || cards.length == 0)
            return;
        executor.execute(new GroupCardHandler(cards));
    }

    @Override
    public void dispatch(GroupMemberCard... cards) {
        if (cards == null || cards.length == 0)
            return;
        executor.execute(new GroupMemberCardHandler(cards));
    }

    /**
     * 把群Card处理为群DB类
     */
    private class GroupCardHandler implements Runnable {
        private final GroupCard[] cards;

        GroupCardHandler(GroupCard[] cards) {
            this.cards = cards;
        }

        @Override
        public void run() {
            List<Group> groups = new ArrayList<>();
            for (GroupCard card : cards) {
                // 得到owner
                User owner = UserHelper.search(card.getOwnerId());
                if (owner != null) {
                    Group group = card.build(owner);
                    groups.add(group);
                }
            }
            if (groups.size() > 0)
                DbHelper.save(Group.class, groups.toArray(new Group[0]));

        }

    }

    /**
     * 把群成员Card处理为群成员DB类
     */
    private class GroupMemberCardHandler implements Runnable {
        private final GroupMemberCard[] cards;

        GroupMemberCardHandler(GroupMemberCard[] cards) {
            this.cards = cards;
        }

        @Override
        public void run() {
            List<GroupMember> members = new ArrayList<>();
            for (GroupMemberCard card : cards) {
                // 成员对应的人的信息
                User user = UserHelper.search(card.getUserId());
                // 成员对应的群的信息
                Group group = GroupHelper.find(card.getGroupId());
                if (user != null && group != null) {
                    GroupMember member = card.build(group, user);
                    members.add(member);
                }
            }
            if (members.size() > 0)
                DbHelper.save(GroupMember.class, members.toArray(new GroupMember[0]));
        }
    }
}
