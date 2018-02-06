package com.im.yutalker.factory.data.helper;

import com.im.yutalker.factory.model.dp.AppDataBase;
import com.im.yutalker.factory.model.dp.Group;
import com.im.yutalker.factory.model.dp.GroupMember;
import com.im.yutalker.factory.model.dp.Group_Table;
import com.im.yutalker.factory.model.dp.Message;
import com.im.yutalker.factory.model.dp.Session;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.ModelAdapter;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 数据库的辅助工具类
 * 辅助完成：增删改
 * Created by Phillip on 2018/1/31.
 */

public class DbHelper {
    private static final DbHelper instance;

    static {
        instance = new DbHelper();
    }

    private DbHelper() {

    }

    /**
     * 观察者的集合
     * Class<?>：观察的表
     * Set<ChangedListener>：每一个表对应的观察者有很多，观察者接口集合
     */
    private final Map<Class<?>, Set<ChangedListener>> changedListeners = new HashMap<>();

    /**
     * 从所有监听者中获取某一个表的所有监听
     *
     * @param tClass  某一个表
     * @param <Model> 表的泛型
     * @return 当前表的监听集合
     */
    private <Model extends BaseModel> Set<ChangedListener> getListeners(Class<Model> tClass) {
        if (changedListeners.containsKey(tClass)) {
            return changedListeners.get(tClass);
        }
        return null;
    }

    /**
     * 添加一个监听
     *
     * @param tClass   对某个表的关注
     * @param listener 监听者
     * @param <Model>  表的泛型
     */
    public static <Model extends BaseModel> void addChangedListener(final Class<Model> tClass,
                                                                    ChangedListener<Model> listener) {
        Set<ChangedListener> changedListeners = instance.getListeners(tClass);
        if (changedListeners == null) {
            // 初始化容器
            changedListeners = new HashSet<>();
            // 添加到总的Map
            instance.changedListeners.put(tClass, changedListeners);
        }
        changedListeners.add(listener);

    }

    /**
     * 删除某一个表的其中一个监听器
     *
     * @param tClass   对某个表的关注
     * @param listener 监听者
     * @param <Model>  表的泛型
     */
    public static <Model extends BaseModel> void removeChangedListener(final Class<Model> tClass,
                                                                       ChangedListener<Model> listener) {
        Set<ChangedListener> changedListeners = instance.getListeners(tClass);
        if (changedListeners == null) {
            // 本身就没添加，不需要删除
            return;
        }
        // 从容器删除
        changedListeners.remove(listener);
    }

    /**
     * 新增或修改的统一方法
     *
     * @param tClass  传递一个Class信息
     * @param models  这个Class对应的实例
     * @param <Model> 这个实例的泛型，限定条件是BaseModel
     */
    public static <Model extends BaseModel> void save(final Class<Model> tClass, final Model... models) {
        if (models == null || models.length == 0)
            return;
        // 获得数据库管理者
        DatabaseDefinition definition = FlowManager.getDatabase(AppDataBase.class);
        // 提交事务
        definition.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                // 执行
                ModelAdapter<Model> adapter = FlowManager.getModelAdapter(tClass);
                // 保存
                adapter.saveAll(Arrays.asList(models));
                // 通知
                instance.notifySave(tClass, models);
            }
        }).build().execute();
    }

    /**
     * 删除的统一方法
     *
     * @param tClass  传递一个Class信息
     * @param models  这个Class对应的实例
     * @param <Model> 这个实例的泛型，限定条件是BaseModel
     */
    public static <Model extends BaseModel> void delete(final Class<Model> tClass, final Model... models) {
        if (models == null || models.length == 0)
            return;
        // 获得数据库管理者
        DatabaseDefinition definition = FlowManager.getDatabase(AppDataBase.class);
        // 提交事务
        definition.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                // 执行
                ModelAdapter<Model> adapter = FlowManager.getModelAdapter(tClass);
                // 删除
                adapter.deleteAll(Arrays.asList(models));
                // 通知
                instance.notifyDelete(tClass, models);
            }
        }).build().execute();
    }

    /**
     * 进行通知保存更改
     *
     * @param tClass  传递一个Class信息
     * @param models  这个Class对应的实例
     * @param <Model> 这个实例的泛型，限定条件是BaseModel
     */
    @SuppressWarnings("unchecked")
    private final <Model extends BaseModel> void notifySave(final Class<Model> tClass, final Model... models) {
        // 找到监听器
        final Set<ChangedListener> listeners = getListeners(tClass);
        if (listeners != null && listeners.size() > 0) {
            // 通用的通知
            for (ChangedListener<Model> listener : listeners) {
                listener.onDataSave(models);

            }
        }

        // 意外情况
        if (GroupMember.class.equals(tClass)) {
            // 群成员变更，需要通知对应群信息更新
            updateGroup((GroupMember[]) models);
        } else if (Message.class.equals(tClass)) {
            // 消息变化，应该通知会话列表更新
            updateSession((Message[]) models);
        }
    }

    /**
     * 进行通知删除
     *
     * @param tClass  传递一个Class信息
     * @param models  这个Class对应的实例
     * @param <Model> 这个实例的泛型，限定条件是BaseModel
     */
    @SuppressWarnings("unchecked")
    private final <Model extends BaseModel> void notifyDelete(final Class<Model> tClass, final Model... models) {
        // 找到监听器
        final Set<ChangedListener> listeners = getListeners(tClass);
        if (listeners != null && listeners.size() > 0) {
            // 通用的通知
            for (ChangedListener<Model> listener : listeners) {
                listener.onDataDelete(models);

            }
        }

        // 意外情况
        if (GroupMember.class.equals(tClass)) {
            // 群成员变更，需要通知对应群信息更新
            updateGroup((GroupMember[]) models);
        } else if (Message.class.equals(tClass)) {
            // 消息变化，应该通知会话列表更新
            updateSession((Message[]) models);
        }
    }

    /**
     * 从成员中找出对应的群，并对群进行更新
     *
     * @param members 群成员列表
     */
    private void updateGroup(GroupMember... members) {
        // Set<String> 不允许重复，只有一个
        final Set<String> groupIds = new HashSet<>();
        for (GroupMember member : members) {
            // 添加群Id
            groupIds.add(member.getGroup().getId());
        }
        // 异步的数据库查询，并异步的发起二次通知
        // 获得数据库管理者
        DatabaseDefinition definition = FlowManager.getDatabase(AppDataBase.class);
        // 提交事务
        definition.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                // 找到需要通知的群
                List<Group> groups = SQLite.select()
                        .from(Group.class)
                        .where(Group_Table.id.in(groupIds))
                        .queryList();

                // 调用自己进行一次通知分发
                notifySave(Group.class, groups.toArray(new Group[0]));
            }
        }).build().execute();
    }

    /**
     * 从消息列表中，筛选出对应的会话，并对会话进行更新
     *
     * @param messages
     */
    private void updateSession(Message... messages) {
        // 标识一个Session的唯一性
        final Set<Session.Identify> identifies = new HashSet<>();
        for (Message message : messages) {
            Session.Identify identify = Session.createSessionIdentify(message);
            identifies.add(identify);
        }

        // 异步的数据库查询，并异步的发起二次通知
        // 获得数据库管理者
        DatabaseDefinition definition = FlowManager.getDatabase(AppDataBase.class);
        // 提交事务
        definition.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                ModelAdapter<Session> adapter = FlowManager.getModelAdapter(Session.class);
                Session[] sessions = new Session[identifies.size()];
                int index = 0;
                for (Session.Identify identify : identifies) {
                    Session session = SessionHelper.findFromLocal(identify.id);
                    if (session == null) {
                        // 第一次聊天，创建一个你和对方的会话
                        session = new Session(identify);
                    }
                    // 把会话刷新到当前Message的最新状态
                    session.refreshToNow();
                    // 数据库存储
                    adapter.save(session);
                    // 添加到集合,index++表示先引用后+1
                    sessions[index++] = session;
                }
                // 调用通知分发
                instance.notifySave(Session.class, sessions);
            }
        }).build().execute();

    }


    /**
     * 通知监听器接口
     */
    @SuppressWarnings({"unchecked", "unused"})
    public interface ChangedListener<Data extends BaseModel> {
        // 通知任意类型数据保存更改
        void onDataSave(Data... list);

        // 通知任意类型数据删除
        void onDataDelete(Data... list);

    }
}
