package com.im.yutalker.factory;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.im.yutalker.common.app.Application;
import com.im.yutalker.factory.data.DataSource;
import com.im.yutalker.factory.data.group.GroupCenter;
import com.im.yutalker.factory.data.group.GroupDispatcher;
import com.im.yutalker.factory.data.message.MessageCenter;
import com.im.yutalker.factory.data.message.MessageDispatcher;
import com.im.yutalker.factory.data.user.UserCenter;
import com.im.yutalker.factory.data.user.UserDispatcher;
import com.im.yutalker.factory.model.api.PushModel;
import com.im.yutalker.factory.model.api.RspModel;
import com.im.yutalker.factory.model.card.GroupCard;
import com.im.yutalker.factory.model.card.GroupMemberCard;
import com.im.yutalker.factory.model.card.MessageCard;
import com.im.yutalker.factory.model.card.UserCard;
import com.im.yutalker.factory.persistence.Account;
import com.im.yutalker.factory.utils.DBFlowExclusionStrategy;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Phillip on 2018/1/11.
 */

public class Factory {
    private static final String TAG = Factory.class.getSimpleName();
    // 单例模式
    private static Factory instance;
    // 线程池
    private final Executor executor;
    // 全局Gson
    private final Gson gson;

    // 恶汉模式的单例，只要调用这个类的任何一个方法都会实例化该类
    static {
        instance = new Factory();
    }

    public Factory() {
        // 新建4个线程的线程池
        executor = Executors.newFixedThreadPool(4);
        gson = new GsonBuilder()
                // 设置时间格式
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
                // 设置一个过滤器，数据库级别的Model不进行Json转换
                .setExclusionStrategies(new DBFlowExclusionStrategy())
                .create();
    }

    /**
     * Factory 中的初始化
     */
    public static void setup() {
        // 初始化数据库
        FlowManager.init(new FlowConfig.Builder(app())
                .openDatabasesOnInit(true)// 数据库初始化的时候就打开数据库
                .build());
        // 持久化的数据进行初始化
        Account.load(app());
    }

    public static Application app() {
        return Application.getInstance();
    }

    /**
     * 异步运行的方法
     *
     * @param runnable Runnable
     */
    public static void runOnAsync(Runnable runnable) {
        // 拿到单例，拿到线程池，然后异步执行
        instance.executor.execute(runnable);
    }

    /**
     * 返回一个全局的Gson，在这可以进行Gson的一些的全局的初始化
     *
     * @return Gson
     */
    public static Gson getGson() {
        return instance.gson;
    }

    /**
     * 进行错误code的解析
     * 把网络请求的code值进行统一的规划，并返回一个String资源
     *
     * @param model    RspModel
     * @param callback DataSource.FailedCallBack 用于返回一个错误的资源Id
     */
    public static void decodeRspCode(RspModel model, DataSource.FailedCallBack callback) {
        if (model == null)
            return;

        // 进行Code区分
        switch (model.getCode()) {
            case RspModel.SUCCEED:
                return;
            case RspModel.ERROR_SERVICE:
                decodeRspCode(R.string.data_rsp_error_service, callback);
                break;
            case RspModel.ERROR_NOT_FOUND_USER:
                decodeRspCode(R.string.data_rsp_error_not_found_user, callback);
                break;
            case RspModel.ERROR_NOT_FOUND_GROUP:
                decodeRspCode(R.string.data_rsp_error_not_found_group, callback);
                break;
            case RspModel.ERROR_NOT_FOUND_GROUP_MEMBER:
                decodeRspCode(R.string.data_rsp_error_not_found_group_member, callback);
                break;
            case RspModel.ERROR_CREATE_USER:
                decodeRspCode(R.string.data_rsp_error_create_user, callback);
                break;
            case RspModel.ERROR_CREATE_GROUP:
                decodeRspCode(R.string.data_rsp_error_create_group, callback);
                break;
            case RspModel.ERROR_CREATE_MESSAGE:
                decodeRspCode(R.string.data_rsp_error_create_message, callback);
                break;
            case RspModel.ERROR_PARAMETERS:
                decodeRspCode(R.string.data_rsp_error_parameters, callback);
                break;
            case RspModel.ERROR_PARAMETERS_EXIST_ACCOUNT:
                decodeRspCode(R.string.data_rsp_error_parameters_exist_account, callback);
                break;
            case RspModel.ERROR_PARAMETERS_EXIST_NAME:
                decodeRspCode(R.string.data_rsp_error_parameters_exist_name, callback);
                break;
            case RspModel.ERROR_ACCOUNT_TOKEN:
                instance.logout();
                break;
            case RspModel.ERROR_ACCOUNT_LOGIN:
                decodeRspCode(R.string.data_rsp_error_account_login, callback);
                break;
            case RspModel.ERROR_ACCOUNT_REGISTER:
                decodeRspCode(R.string.data_rsp_error_account_register, callback);
                break;
            case RspModel.ERROR_ACCOUNT_NO_PERMISSION:
                decodeRspCode(R.string.data_rsp_error_account_no_permission, callback);
                break;
            case RspModel.ERROR_UNKNOWN:
            default:
                decodeRspCode(R.string.data_rsp_error_unknown, callback);
                break;


        }

    }

    private static void decodeRspCode(final int resId, final DataSource.FailedCallBack callback) {
        if (callback != null) {
            callback.onDataNotAvailable(resId);
        }
    }

    /**
     * 收到退出的消息需要进行账户退出重新登录
     */
    public static void logout() {
        Application.showToast(R.string.data_rsp_error_account_token);
        Account.setLogout();
        app().finishAll();
    }

    /**
     * 处理推送来的消息
     *
     * @param message 消息
     */
    public static void dispatchPush(String message) {
        // 检查登录状态
        if (!Account.isLogin())
            return;

        PushModel model = PushModel.decode(message);
        if (model == null)
            return;


        // 对推送集合进行遍历
        for (PushModel.Entity entity : model.getEntities()) {
            Log.e(TAG, "dispatchPush-Entity:" + entity.toString());
            switch (entity.type) {
                case PushModel.ENTITY_TYPE_LOGOUT:
                    instance.logout();
                    return;
                case PushModel.ENTITY_TYPE_MESSAGE: {
                    // 普通消息
                    MessageCard card = getGson().fromJson(entity.content, MessageCard.class);
                    getMessageCenter().dispatch(card);
                    break;
                }
                case PushModel.ENTITY_TYPE_ADD_FRIEND: {
                    // 添加朋友
                    UserCard card = getGson().fromJson(entity.content, UserCard.class);
                    getUserCenter().dispatch(card);
                    break;
                }
                case PushModel.ENTITY_TYPE_ADD_GROUP: {
                    // 添加群
                    GroupCard card = getGson().fromJson(entity.content, GroupCard.class);
                    getGroupCenter().dispatch(card);
                    break;
                }
                case PushModel.ENTITY_TYPE_ADD_GROUP_MEMBERS:
                case PushModel.ENTITY_TYPE_MODIFY_GROUP_MEMBERS: {
                    // 群成员变更，回来的是一个列表
                    Type type = new TypeToken<List<GroupMemberCard>>() {
                    }.getType();
                    List<GroupMemberCard> cards = getGson().fromJson(entity.content, type);
                    // 把数据集合丢到数据中心处理
                    getGroupCenter().dispatch(cards.toArray(new GroupMemberCard[0]));
                    break;
                }
                case PushModel.ENTITY_TYPE_EXIT_GROUP_MEMBERS:
                    // TODO 成员退出的推送
            }
        }
    }

    /**
     * 获取一个用户中心的实现类
     *
     * @return 用户中心的接口
     */
    public static UserCenter getUserCenter() {
        return UserDispatcher.getInstance();
    }

    /**
     * 获取一个群中心的实现类
     *
     * @return 群中心的接口
     */
    public static GroupCenter getGroupCenter() {
        return GroupDispatcher.getInstance();
    }

    /**
     * 获取一个消息中心的实现类
     *
     * @return 消息中心的接口
     */
    public static MessageCenter getMessageCenter() {
        return MessageDispatcher.getInstance();
    }


}
