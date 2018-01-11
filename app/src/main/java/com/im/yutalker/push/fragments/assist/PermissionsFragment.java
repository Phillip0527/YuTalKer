package com.im.yutalker.push.fragments.assist;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.im.yutalker.common.app.Application;
import com.im.yutalker.push.R;
import com.im.yutalker.push.fragments.media.GalleryFragment;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

/**
 * A simple {@link Fragment} subclass.
 */
public class PermissionsFragment extends BottomSheetDialogFragment implements EasyPermissions.PermissionCallbacks {

    private static final int RC_LOCATION = 0x0100;//权限回调的标识

    public PermissionsFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // 返回自定义的BottomSheetDialog
        return new GalleryFragment.TransStatusBottomSheetDialog(getContext());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 获取布局控件
        View root = inflater.inflate(R.layout.fragment_permissions, container, false);

        // 获取按钮
        root.findViewById(R.id.btn_submit)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 点击时申请权限
                        requestPermissions();
                    }
                });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 界面显示的时候进行刷新
        refreshState(getView());
    }

    /**
     * 刷新布局中图片状态
     *
     * @param root 根布局
     */
    private void refreshState(View root) {
        if (root == null)
            return;

        Context context = getContext();
        root.findViewById(R.id.im_state_permission_network)
                .setVisibility(haveNetworkPerm(context) ? View.VISIBLE : View.GONE);
        root.findViewById(R.id.im_state_permission_read)
                .setVisibility(haveReadPerm(context) ? View.VISIBLE : View.GONE);
        root.findViewById(R.id.im_state_permission_write)
                .setVisibility(haveWritePerm(context) ? View.VISIBLE : View.GONE);
        root.findViewById(R.id.im_state_permission_record_audio)
                .setVisibility(haveRecordAudioPerm(context) ? View.VISIBLE : View.GONE);
    }

    /**
     * 是否有网络权限
     *
     * @param context 上下文
     * @return True 则有
     */
    private static boolean haveNetworkPerm(Context context) {
        // 准备需要检查的网络权限
        String[] perms = new String[]{
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE
        };
        return EasyPermissions.hasPermissions(context, perms);
    }

    /**
     * 是否有外部存储读取权限
     *
     * @param context 上下文
     * @return True 则有
     */
    private static boolean haveReadPerm(Context context) {
        // 准备需要检查的读取权限
        String[] perms = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        return EasyPermissions.hasPermissions(context, perms);
    }

    /**
     * 是否有外部存储写入权限
     *
     * @param context 上下文
     * @return True 则有
     */
    private static boolean haveWritePerm(Context context) {
        // 准备需要检查的写入权限
        String[] perms = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        return EasyPermissions.hasPermissions(context, perms);
    }

    /**
     * 是否有录音权限
     *
     * @param context 上下文
     * @return True 则有
     */
    private static boolean haveRecordAudioPerm(Context context) {
        // 准备需要检查的录音权限
        String[] perms = new String[]{
                Manifest.permission.RECORD_AUDIO
        };
        return EasyPermissions.hasPermissions(context, perms);
    }


    /**
     * 私有的show方法
     *
     * @param manager FragmentManager v4包下的
     */
    private static void show(FragmentManager manager) {
        // 调用BottomSheetDialogFragment自带的show方法
        new PermissionsFragment().show(manager, PermissionsFragment.class.getName());
    }

    /**
     * 检查是否具有所有的权限
     *
     * @param context Context
     * @param manager FragmentManager
     * @return 是否有权限
     */
    public static boolean haveAllPerms(Context context, FragmentManager manager) {
        // 检查是否具有所有的权限
        boolean haveAll = haveNetworkPerm(context)
                && haveReadPerm(context)
                && haveWritePerm(context)
                && haveRecordAudioPerm(context);

        // 如果没有则显示当前申请权限的界面
        if (!haveAll) {
            show(manager);
        }
        return haveAll;
    }


    /**
     * 申请权限的方法
     */
    @AfterPermissionGranted(RC_LOCATION)
    private void requestPermissions() {
        // 准备需要检查的所有权限
        String[] perms = new String[]{
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        };

        if (EasyPermissions.hasPermissions(getContext(), perms)) {
            Application.showToast(R.string.label_permission_ok);
            // Fragment 调用getView()得到根布局，前提是onCreatedView方法之后
            refreshState(getView());
        } else {
//            EasyPermissions.requestPermissions(this, getString(R.string.title_assist_permissions), RC_LOCATION, perms);
            EasyPermissions.requestPermissions(
                    new PermissionRequest.Builder(this, RC_LOCATION, perms)
                            .setRationale(R.string.app_rationale_assist_permissions)
                            .setPositiveButtonText(R.string.dialog_ok)
                            .setNegativeButtonText(R.string.dialog_cancel)
                            .setTheme(R.style.Theme_AppCompat_Light_Dialog_Alert)
                            .build());
        }

    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        // 如果权限有没有申请成功的权限，则弹出弹出框，用户点击后去设置界面手动打开权限
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog
                    .Builder(this)
                    .setTitle(R.string.title_assist_permissions)
                    .setRationale(R.string.system_rationale_assist_permissions)
                    .setPositiveButton(R.string.dialog_ok)
                    .setNegativeButton(R.string.dialog_cancel)
                    .setThemeResId(R.style.Theme_AppCompat_Light_Dialog_Alert)
                    .build()
                    .show();
        }
    }

    /**
     * 权限申请时回调的方法，在这个方法中把对应的权限申请状态交给EasyPermissions框架
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 传递对应参数并且告知接受权限的处理者是我自己
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}
